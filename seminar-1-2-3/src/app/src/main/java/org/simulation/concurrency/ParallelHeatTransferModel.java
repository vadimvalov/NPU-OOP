package org.simulation.concurrency;

import org.simulation.core.SimulationDomain;
import org.simulation.models.HeatTransferModel;

/**
 * Extension for Task 5: Implement working prototype using concurrency setup.
 */
public class ParallelHeatTransferModel extends HeatTransferModel implements ComputeRHSStrategy {
    private final TaskScheduler scheduler;

    public ParallelHeatTransferModel(double thermalDiffusivity, TaskScheduler scheduler) {
        super(thermalDiffusivity);
        this.scheduler = scheduler;
    }

    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        double[] rhs = new double[nx * ny];
        
        // Defer computation execution graph to the Thread pool controller
        scheduler.executeParallelRHS(this, nx, ny, rhs);
        
        return rhs;
    }

    @Override
    public void computeSubdomainRHS(Subdomain sub, double[] rhsOut) {
        // Safe lock-free segment execution: each worker writes to disparate memory allocations
        // ensuring no thread stomps over another thread's target coordinate payload.
        for (int i = sub.getStartX(); i < sub.getEndX(); i++) {
            for (int j = sub.getStartY(); j < sub.getEndY(); j++) {
                int k = i + j * nx;
                
                // Fetch grid readings via encapsulated primitive 1D storage for blazing speeds!
                double u_ij = field.getDoubleValue(i, j);
                double lap = (field.getDoubleValue(i + 1, j) - 2.0 * u_ij + field.getDoubleValue(i - 1, j)) / (dx * dx)
                           + (field.getDoubleValue(i, j + 1) - 2.0 * u_ij + field.getDoubleValue(i, j - 1)) / (dy * dy);
                           
                rhsOut[k] = this.thermalDiffusivity * lap;
            }
        }
    }
    
    @Override
    public String getName() {
        return "Parallel Heat Transfer (" + scheduler.getNumThreads() + " threads)";
    }
}
