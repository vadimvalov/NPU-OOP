package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Explicit Euler (Forward Euler):
 *   u_new = u + dt * RHS(u)
 *
 * Fast but conditionally stable — requires small dt.
 * Wraps the same logic as FiniteDifferenceSolver.applyScheme().
 */
public class ExplicitEulerStepper implements IStepperStrategy {

    private double lastResidual = 0.0;
    private double currentTime  = 0.0;

    @Override
    public double[] step(PhysicalModel model, SimulationDomain domain, double dt) {
        double[] u    = model.getFieldValues();
        double[] rhs  = model.computeRHS(domain, currentTime);

        double[] uNew = new double[u.length];
        for (int k = 0; k < u.length; k++) {
            uNew[k] = u[k] + dt * rhs[k];
        }

        domain.applyBoundaryConditions(uNew);
        model.updateState(uNew);
        currentTime += dt;

        lastResidual = computeResidual(u, uNew);
        return uNew;
    }

    private double computeResidual(double[] uOld, double[] uNew) {
        double sum = 0.0;
        for (int k = 0; k < uOld.length; k++) {
            double diff = uNew[k] - uOld[k];
            sum += diff * diff;
        }
        return Math.sqrt(sum / uOld.length);
    }

    @Override
    public double getLastResidual() { return lastResidual; }

    @Override
    public String getName() { return "Explicit Euler"; }
}