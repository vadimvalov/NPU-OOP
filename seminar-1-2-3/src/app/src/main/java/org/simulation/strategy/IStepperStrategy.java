package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Strategy Pattern (A1) — numerical time-stepping algorithm.
 *
 * Replaces NumericalSolver as the primary strategy interface.
 * Three implementations: ExplicitEuler, ImplicitIterative, OperatorSplitting.
 */
public interface IStepperStrategy {

    /**
     * Advance the model by one time step dt.
     * Reads model.getFieldValues(), computes new state, calls model.updateState().
     *
     * @return new field values after the step
     */
    double[] step(PhysicalModel model, SimulationDomain domain, double dt);

    /** Residual from the last step — used by ConvergenceObserver. */
    double getLastResidual();

    String getName();
}