package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Strategy Pattern (A1) -- Simple Forward Euler implementation.
 */
public class ExplicitEulerStepper implements IStepperStrategy {

    private double lastResidual = 0.0;

    @Override
    @SuppressWarnings("unchecked")
    public double[] step(PhysicalModel<?> model, SimulationDomain<?> domain, double dt) {
        double[] u   = model.getFieldValues();
        // Since we are using generic PhysicalModel<T>, computeRHS returns T[].
        // For Backward compatibility with old steppers, we cast the Double[] bridge.
        Double[] rhs = (Double[]) model.computeRHS(domain, 0.0);
        
        double[] uNew = new double[u.length];
        double residual = 0.0;

        for (int i = 0; i < u.length; i++) {
            uNew[i] = u[i] + dt * rhs[i];
            double diff = uNew[i] - u[i];
            residual += diff * diff;
        }

        lastResidual = Math.sqrt(residual / u.length);

        // Bridge: update state from double[]
        Double[] wrapped = new Double[uNew.length];
        for (int i = 0; i < uNew.length; i++) wrapped[i] = uNew[i];
        ((PhysicalModel<Double>)model).updateState(wrapped);

        return uNew;
    }

    @Override public double getLastResidual() { return lastResidual; }
    @Override public String getName() { return "Explicit Euler (Strategy)"; }
}