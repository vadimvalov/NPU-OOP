package org.simulation.controller.state;

import org.simulation.controller.SimulationController;
import org.simulation.core.OutputHandler;

/**
 * IDLE — initial state. handle() validates config, inits model + output handlers,
 * resets time, then moves to INITIALIZED.
 */
public class IdleState extends AbstractSimulationState {

    @Override
    public void handle(SimulationController ctx) {
        ctx.validateConfiguration();
        ctx.getModel().initialize(ctx.getDomain());

        for (OutputHandler handler : ctx.getOutputHandlers()) {
            handler.initialize(ctx.getDomain(), ctx.getModel());
        }

        ctx.resetTime();
        transitionTo(ctx, new InitializedState());
    }

    @Override
    public String getName() { return "IDLE"; }
}