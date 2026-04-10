package org.simulation.concurrency;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

/**
 * Task 4 element: SynchronizationManager
 * Ensures memory boundary safety by making sure all workers complete their step
 * before returning control back to the sequential driver.
 */
public class SynchronizationManager {
    private final CyclicBarrier barrier;

    public SynchronizationManager(int numThreads) {
        // The '+ 1' includes the main thread orchestrating the tasks.
        // If the main thread joins using Futures, we only need 'numThreads'
        this.barrier = new CyclicBarrier(numThreads, () -> {
            // Optional: Action to perform when all threads hit the barrier
            // e.g., boundary condition exchanges can be placed here if needed
        });
    }

    public void awaitSynchronization() {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Concurrent execution interrupted at memory barrier", e);
        }
    }
}
