package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * CONFIGURED — model/solver/domain are set, but initialize() not yet called.
 *
 * This state exists so the controller has a clear moment where configuration
 * is validated but physics haven't started yet.
 *
 * enter()  — logs that configuration is accepted.
 * handle() → initializes model + domain → transitions to INITIALIZED.
 */
public class ConfiguredState extends AbstractSimulationState {

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        System.out.println("[State] Configuration validated. Call initialize() to proceed.");
    }

    @Override
    public void handle(SimulationController ctx) {
        transitionTo(ctx, new InitializedState());
        ctx.getState().handle(ctx); // immediately proceed to RUNNING
    }

    @Override
    public String getName() { return "CONFIGURED"; }
}