package org.simulation.solvers;

import org.simulation.core.SimulationDomain;
import org.simulation.core.PhysicalModel;

public class FiniteDifferenceSolver extends AbstractSolver<Double, PhysicalModel<Double>> {
    private static final String PARAM_DIFFUSIVITY = "diffusivity";

    public FiniteDifferenceSolver() {
        parameters.put(PARAM_DIFFUSIVITY, 1e-4);
    }

    @Override
    protected double[] applyScheme(double[] currentValues, double[] rhs, double dt) {
        double[] newValues = new double[currentValues.length];
        for (int k = 0; k < currentValues.length; k++) {
            newValues[k] = currentValues[k] + dt * rhs[k];
        }
        return newValues;
    }

    @Override
    public boolean isStable(double dt, SimulationDomain domain) {
        double alpha = getParameter(PARAM_DIFFUSIVITY, 1e-4);
        double dx = domain.getDx();
        double dy = domain.getDy();
        double dx2 = dx * dx;
        double dy2 = dy * dy;
        double maxDt = (dx2 * dy2) / (2.0 * alpha * (dx2 + dy2));
        return dt <= maxDt;
    }

    @Override
    public String getName() {
        return "Explicit Finite Difference Solver (Forward Euler)";
    }
}