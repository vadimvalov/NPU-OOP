package org.simulation.concurrency;

/**
 * Task 3 element: Worker
 * Runs independently on a thread, computing the spatial region assigned by a Subdomain.
 */
public class Worker implements Runnable {
    private final Subdomain subdomain;
    private final ComputeRHSStrategy strategy;
    private final double[] rhsOut;
    private final SynchronizationManager sync;

    public Worker(Subdomain subdomain, ComputeRHSStrategy strategy, double[] rhsOut, SynchronizationManager sync) {
        this.subdomain = subdomain;
        this.strategy = strategy;
        this.rhsOut = rhsOut;
        this.sync = sync;
    }

    @Override
    public void run() {
        // Execute numerical heavy-lifting only in this designated box chunk
        strategy.computeSubdomainRHS(subdomain, rhsOut);
        
        // Ensure memory visibility and structural synchronization
        if (sync != null) {
            sync.awaitSynchronization();
        }
    }
}
