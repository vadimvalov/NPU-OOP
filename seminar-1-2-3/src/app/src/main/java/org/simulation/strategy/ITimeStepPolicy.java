package org.simulation.strategy;

/**
 * Strategy Pattern (A2) — time step selection policy.
 *
 * Two implementations: FixedDtPolicy, AdaptiveDtPolicy.
 * Used by SimulationController to compute dt each step.
 */
public interface ITimeStepPolicy {

    /**
     * Compute the next dt.
     *
     * @param residual  residual from the last step (from IStepperStrategy.getLastResidual())
     * @param t         current simulation time
     * @param dtPrev    dt used in the last step
     * @param tEnd      total simulation end time
     * @return          dt for the next step
     */
    double nextDt(double residual, double t, double dtPrev, double tEnd);

    String getName();
}