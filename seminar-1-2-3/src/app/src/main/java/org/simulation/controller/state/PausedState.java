package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * PAUSED — time-stepping loop is suspended at a clean step boundary.
 *
 * Valid transitions:
 *   PAUSED → RUNNING  via resume()
 */
public class PausedState extends AbstractSimulationState {

    @Override
    public void resume(SimulationController ctx) {
        System.out.println("[State] Transition: PAUSED → RUNNING (resuming from step "
            + ctx.getCurrentStep() + ")");
        ctx.setState(new RunningState());
        ctx.getState().run(ctx);   // re-enter the loop
    }

    @Override
    public String getName() { return "PAUSED"; }
}