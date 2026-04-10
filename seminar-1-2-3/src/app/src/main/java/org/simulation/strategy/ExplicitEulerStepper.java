package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Strategy Pattern (A1) -- Simple Forward Euler implementation.
 */
public class ExplicitEulerStepper implements IStepperStrategy {

    private double lastResidual = 0.0;

    @Override
    public double[] step(PhysicalModel<?> model, SimulationDomain<?> domain, double dt) {
        double[] u   = model.getFieldValues();
        double[] rhs = model.computeRHS(domain, 0.0);
        
        double[] uNew = new double[u.length];
        double residual = 0.0;

        for (int i = 0; i < u.length; i++) {
            uNew[i] = u[i] + dt * rhs[i];
            double diff = uNew[i] - u[i];
            residual += diff * diff;
        }

        lastResidual = Math.sqrt(residual / u.length);

        model.updateState(uNew);

        return uNew;
    }

    @Override public double getLastResidual() { return lastResidual; }
    @Override public String getName() { return "Explicit Euler (Strategy)"; }
}