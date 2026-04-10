package org.simulation.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Task Scheduler which manages thread pools and delegates subdomains securely.
 */
public class TaskScheduler {
    private final int numThreads;
    private final ExecutorService executor;
    private final SynchronizationManager syncManager;

    public TaskScheduler(int numThreads) {
        this.numThreads = numThreads;
        this.executor = Executors.newFixedThreadPool(numThreads);
        // We synchronize only the active workers
        this.syncManager = new SynchronizationManager(numThreads);
    }
    
    /**
     * Executes domain decomposition iteratively across Y dimension.
     */
    public void executeParallelRHS(ComputeRHSStrategy strategy, int nx, int ny, double[] rhsOut) {
        List<Worker> workers = new ArrayList<>();
        
        // Inner region rows to compute without borders
        int availableRows = ny - 2;
        int rowsPerThread = availableRows / numThreads;
        if (rowsPerThread < 1) rowsPerThread = 1;
        
        int currentY = 1;
        for (int i = 0; i < numThreads; i++) {
            int endY = (i == numThreads - 1) ? ny - 1 : currentY + rowsPerThread;
            
            Subdomain sub = new Subdomain(1, nx - 1, currentY, endY);
            Worker worker = new Worker(sub, strategy, rhsOut, syncManager);
            workers.add(worker);
            
            currentY = endY;
            if (currentY >= ny - 1) break; 
        }
        
        try {
            List<Future<?>> futures = new ArrayList<>();
            // Dispatch async
            for (Worker worker : workers) {
                futures.add(executor.submit(worker));
            }
            
            // Wait for aggregation (Barrier ensures internals align, futures wait thread finish)
            for (Future<?> f : futures) {
                f.get();
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Exception during parallel domain execution", e);
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    public int getNumThreads() { return numThreads; }
}
