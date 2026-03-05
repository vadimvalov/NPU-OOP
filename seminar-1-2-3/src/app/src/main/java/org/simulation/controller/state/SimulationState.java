package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * State Pattern — interface for all simulation lifecycle states.
 *
 * Each state implements enter/handle/exit:
 *   enter()  — called once on transition INTO this state (setup, logging)
 *   handle() — main action while IN this state (run loop, validation)
 *   exit()   — called once on transition OUT of this state (cleanup)
 *
 * Lifecycle:
 *   IDLE → CONFIGURED → INITIALIZED → RUNNING ⇄ PAUSED
 *                                         ↓
 *                                     COMPLETED / FAILED
 *
 * Allowed transitions are enforced per concrete state.
 */
public interface SimulationState {

    /** Called once when transitioning INTO this state. */
    void enter(SimulationController ctx);

    /**
     * Main action for this state.
     * RUNNING: drives the time-step loop.
     * Others:  validates config, triggers next transition.
     */
    void handle(SimulationController ctx);

    /** Called once when transitioning OUT of this state. */
    void exit(SimulationController ctx);

    /** Human-readable name of this state (for logging). */
    String getName();
}