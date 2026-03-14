package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

public class IdleState extends AbstractSimulationState {

    @Override
    public void handle(SimulationController ctx) {
        ctx.validateConfiguration();
        ctx.getModel().initialize(ctx.getDomain());
        ctx.resetTime();
        transitionTo(ctx, new InitializedState());
    }

    @Override
    public String getName() { return "IDLE"; }
}