package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Strategy Pattern (A1) -- Implicit Jacobi-like Iterative implementation.
 */
public class ImplicitIterativeStepper implements IStepperStrategy {

    private double lastResidual = 0.0;
    private static final int MAX_ITER = 5;

    @Override
    public double[] step(PhysicalModel<?> model, SimulationDomain<?> domain, double dt) {
        double[] u   = model.getFieldValues();
        double[] uNew = new double[u.length];
        System.arraycopy(u, 0, uNew, 0, u.length);

        for (int iter = 0; iter < MAX_ITER; iter++) {
            model.updateState(uNew);
            
            double[] rhs = model.computeRHS(domain, 0.0);
            for (int i = 0; i < u.length; i++) {
                uNew[i] = u[i] + dt * rhs[i];
            }
        }

        lastResidual = 0.01; // Mocked
        return uNew;
    }

    @Override public double getLastResidual() { return lastResidual; }
    @Override public String getName() { return "Implicit Iterative (Strategy)"; }
}