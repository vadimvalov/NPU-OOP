package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * PAUSED — loop suspended at a clean step boundary.
 *
 * enter()  — logs pause.
 * handle() → resumes: transitions back to RUNNING.
 */
public class PausedState extends AbstractSimulationState {

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        System.out.printf("[State] Paused at step=%d  t=%.6f%n",
            ctx.getCurrentStep(), ctx.getCurrentTime());
    }

    @Override
    public void handle(SimulationController ctx) {
        System.out.println("[State] Resuming from step " + ctx.getCurrentStep());
        transitionTo(ctx, new RunningState());
        ctx.getState().handle(ctx);
    }

    @Override
    public String getName() { return "PAUSED"; }
}