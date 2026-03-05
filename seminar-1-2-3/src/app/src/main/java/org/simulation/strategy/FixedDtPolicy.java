package org.simulation.strategy;

/**
 * Fixed time step — always returns the same dt.
 * Clamped to (tEnd - t) at the last step.
 */
public class FixedDtPolicy implements ITimeStepPolicy {

    private final double dt;

    public FixedDtPolicy(double dt) {
        if (dt <= 0) throw new IllegalArgumentException("dt must be positive");
        this.dt = dt;
    }

    @Override
    public double nextDt(double residual, double t, double dtPrev, double tEnd) {
        return Math.min(dt, tEnd - t);
    }

    @Override
    public String getName() { return "Fixed dt=" + dt; }
}