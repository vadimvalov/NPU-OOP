# Analytical Report: Conceptual Analysis of Sequential vs. Parallel Execution (Seminar 10)

## 1. Architectural and Execution Flow Differences
The architectural evolution strictly preserves the core object-oriented models while injecting a thread-pooling mechanism (`TaskScheduler`).
- **Sequential Flow:** The simulation iterates linearly. The `ExplicitEulerStepper` loops over the grid one element at a time, computes the Laplacian, and writes to `rhs[]`. It single-threads through `nx * ny` nested loops.
- **Concurrent Flow:** We implemented Domain Decomposition. The `ParallelHeatTransferModel` dynamically slices the 2D grid into contiguous Y-axis blocks (`Subdomain`). A `TaskScheduler` spans an `ExecutorService` threaded pool. Computation tasks are partitioned across isolated `Worker` objects that concurrently calculate their sub-domains without overlapping boundaries. Execution stops at a `SynchronizationManager` (acting via `CyclicBarrier`) until all shards successfully synchronize before yielding control back to the central controller.

## 2. Synchronization Mechanisms and Data Safety
The greatest risk in parallel matrix processing involves **race conditions** traversing bounds. 
To counteract this, memory segments are completely decoupled:
- **No Direct Memory Conflicts:** Since each `Worker` only dictates numerical outputs to exclusive indices strictly enclosed within its calculated `Subdomain`, write-collisions are impossible by mathematical necessity.
- **Barrier Control:** Java's `CyclicBarrier` dictates thread cadence. The orchestrating pipeline pauses until every individual threaded unit reports `awaitSynchronization()`. No single thread advances until the full snapshot frame computes, ensuring identical output timings to the sequential counterpart.

## 3. Potential Speedup and Scaling Limitations
- **Speedup Elements:** Execution limits directly correlate with multi-core scalability (e.g., executing on a 4-core processor theoretically scales computation nearly 4x, bounded structurally only by Amdahl's Law). The primitive `double[]` arrays introduced in the previous seminar pay huge dividends here, ensuring cache-lines remain warm without false sharing.
- **Synchronization Overhead:** Every cyclic latch carries latency costs. For dramatically small grids (e.g., 10x10), threading setup and barrier aggregation eclipse raw loop throughput, causing sequential logic to run faster. Concurrency truly thrives at large densities (500x500+).

## 4. Complexity Increase in OOP Design
Bridging concurrency injects structural complexity:
- New logical components are forced onto the pipeline (`Worker`, `Subdomain`, `SynchronizationManager`, `TaskScheduler`).
- Shared states require extreme code vigilance; we effectively stripped explicit field mutation out of models and deferred writing back structurally up to the caller to sidestep race-conditions.
- The `Simulation` loop had to surrender raw `for-loop` iteration syntax and offload to a proxy dispatcher (`ComputeRHSStrategy`), increasing tracking abstractions significantly but guaranteeing decoupled scaling execution.
