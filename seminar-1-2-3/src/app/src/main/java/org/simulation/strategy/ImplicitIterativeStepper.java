package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Implicit Iterative Stepper — Jacobi iteration for implicit solve.
 *
 * Solves: (u_new - u) / dt = RHS(u_new)
 * Via fixed-point iteration: u_new^(k+1) = u + dt * RHS(u_new^(k))
 *
 * More stable than Explicit Euler — allows larger dt.
 * ConvergenceObserver switches to this when Explicit is struggling.
 */
public class ImplicitIterativeStepper implements IStepperStrategy {

    private final int    maxIterations;
    private final double tolerance;
    private double lastResidual = 0.0;
    private double currentTime  = 0.0;

    public ImplicitIterativeStepper() {
        this(50, 1e-6);
    }

    public ImplicitIterativeStepper(int maxIterations, double tolerance) {
        this.maxIterations = maxIterations;
        this.tolerance     = tolerance;
    }

    @Override
    public double[] step(PhysicalModel model, SimulationDomain domain, double dt) {
        double[] u    = model.getFieldValues();
        double[] uNew = u.clone(); // initial guess = current state

        for (int iter = 0; iter < maxIterations; iter++) {
            model.updateState(uNew);
            double[] rhs = model.computeRHS(domain, currentTime + dt);

            double[] uNext = new double[u.length];
            for (int k = 0; k < u.length; k++) {
                uNext[k] = u[k] + dt * rhs[k];
            }

            domain.applyBoundaryConditions(uNext);

            lastResidual = computeResidual(uNew, uNext);
            uNew = uNext;

            if (lastResidual < tolerance) break;
        }

        model.updateState(uNew);
        currentTime += dt;
        return uNew;
    }

    private double computeResidual(double[] a, double[] b) {
        double sum = 0.0;
        for (int k = 0; k < a.length; k++) {
            double diff = b[k] - a[k];
            sum += diff * diff;
        }
        return Math.sqrt(sum / a.length);
    }

    @Override
    public double getLastResidual() { return lastResidual; }

    @Override
    public String getName() { return "Implicit Iterative (Jacobi)"; }
}