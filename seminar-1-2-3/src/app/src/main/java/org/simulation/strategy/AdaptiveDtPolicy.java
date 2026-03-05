package org.simulation.strategy;

/**
 * Adaptive time step based on residual from the last step.
 *
 * Logic:
 *   residual < targetResidual * 0.1  → grow dt (converging fast, can take bigger steps)
 *   residual > targetResidual        → shrink dt (converging slow, need smaller steps)
 *   otherwise                        → keep dt
 *
 * Result is always clamped to [dtMin, dtMax] and to (tEnd - t).
 */
public class AdaptiveDtPolicy implements ITimeStepPolicy {

    private final double dtMin;
    private final double dtMax;
    private final double targetResidual;
    private final double growFactor;
    private final double shrinkFactor;

    public AdaptiveDtPolicy(double dtMin, double dtMax, double targetResidual) {
        this(dtMin, dtMax, targetResidual, 1.2, 0.5);
    }

    public AdaptiveDtPolicy(double dtMin, double dtMax, double targetResidual,
                             double growFactor, double shrinkFactor) {
        this.dtMin          = dtMin;
        this.dtMax          = dtMax;
        this.targetResidual = targetResidual;
        this.growFactor     = growFactor;
        this.shrinkFactor   = shrinkFactor;
    }

    @Override
    public double nextDt(double residual, double t, double dtPrev, double tEnd) {
        double dt;

        if (residual < targetResidual * 0.1) {
            dt = dtPrev * growFactor;
        } else if (residual > targetResidual) {
            dt = dtPrev * shrinkFactor;
        } else {
            dt = dtPrev;
        }

        dt = Math.max(dtMin, Math.min(dtMax, dt));
        dt = Math.min(dt, tEnd - t);
        return dt;
    }

    @Override
    public String getName() {
        return "Adaptive dt [" + dtMin + ", " + dtMax + "] target residual=" + targetResidual;
    }
}