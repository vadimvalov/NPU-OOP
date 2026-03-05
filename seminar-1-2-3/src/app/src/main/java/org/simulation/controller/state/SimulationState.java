package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * State pattern — interface for all simulation lifecycle states.
 *
 * Each concrete state decides which operations are allowed
 * and how to transition to the next state.
 *
 * Lifecycle:
 *   IDLE → INITIALIZED → RUNNING → PAUSED → RUNNING → FINISHED
 *                                         ↑__________↓
 */
public interface SimulationState {

    /**
     * Configure domain, model, solver and output handlers.
     * Valid only from IDLE.
     */
    void initialize(SimulationController ctx);

    /**
     * Start or resume time-stepping loop.
     * Valid from INITIALIZED or PAUSED.
     */
    void run(SimulationController ctx);

    /**
     * Suspend the simulation mid-run.
     * Valid only from RUNNING.
     */
    void pause(SimulationController ctx);

    /**
     * Resume after pause — delegates to run().
     * Valid only from PAUSED.
     */
    void resume(SimulationController ctx);

    /**
     * Human-readable name of this state (for logging).
     */
    String getName();
}