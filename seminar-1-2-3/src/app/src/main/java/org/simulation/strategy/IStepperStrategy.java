package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Strategy Pattern (A1) -- numerical time-stepping algorithm.
 * Now generic to support different numeric types T and model types M.
 */
public interface IStepperStrategy {

    /**
     * Advance the model by one time step dt.
     * Use bridge methods if needed for backward compatibility.
     */
    double[] step(PhysicalModel<?> model, SimulationDomain<?> domain, double dt);

    /** Residual from the last step. */
    double getLastResidual();

    String getName();
}