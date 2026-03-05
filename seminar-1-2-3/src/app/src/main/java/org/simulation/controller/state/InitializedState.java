package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * INITIALIZED — model and domain are ready, time-stepping not started.
 *
 * enter() — initializes model + domain + resets time.
 * handle() → transitions to RUNNING.
 */
public class InitializedState extends AbstractSimulationState {

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        ctx.getModel().initialize(ctx.getDomain());
        ctx.resetTime();
        System.out.println("[State] Model initialized, ready to run.");
    }

    @Override
    public void handle(SimulationController ctx) {
        transitionTo(ctx, new RunningState());
        ctx.getState().handle(ctx);
    }

    @Override
    public String getName() { return "INITIALIZED"; }
}