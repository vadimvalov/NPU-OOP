package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * Base state: every operation throws IllegalStateException by default.
 * Concrete states override only the transitions that are valid for them.
 *
 * This follows the "don't repeat yourself" approach — instead of copying
 * the same exception boilerplate into every concrete state, we inherit it.
 */
public abstract class AbstractSimulationState implements SimulationState {

    @Override
    public void initialize(SimulationController ctx) {
        throw new IllegalStateException(
            "Cannot initialize from state [" + getName() + "]. " +
            "initialize() is only valid from IDLE."
        );
    }

    @Override
    public void run(SimulationController ctx) {
        throw new IllegalStateException(
            "Cannot run from state [" + getName() + "]. " +
            "run() is only valid from INITIALIZED or PAUSED."
        );
    }

    @Override
    public void pause(SimulationController ctx) {
        throw new IllegalStateException(
            "Cannot pause from state [" + getName() + "]. " +
            "pause() is only valid from RUNNING."
        );
    }

    @Override
    public void resume(SimulationController ctx) {
        throw new IllegalStateException(
            "Cannot resume from state [" + getName() + "]. " +
            "resume() is only valid from PAUSED."
        );
    }
}