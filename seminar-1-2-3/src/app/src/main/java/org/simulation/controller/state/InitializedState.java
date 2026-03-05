package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * INITIALIZED — model and domain are ready, time-stepping has not started yet.
 *
 * Valid transitions:
 *   INITIALIZED → RUNNING  via run()
 */
public class InitializedState extends AbstractSimulationState {

    @Override
    public void run(SimulationController ctx) {
        System.out.println("[State] Transition: INITIALIZED → RUNNING");
        ctx.setState(new RunningState());
        ctx.getState().run(ctx);   // delegate immediately so RunningState drives the loop
    }

    @Override
    public String getName() { return "INITIALIZED"; }
}