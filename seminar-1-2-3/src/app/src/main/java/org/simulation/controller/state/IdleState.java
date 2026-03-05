package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * IDLE — initial state of every SimulationController.
 *
 * The only valid transition is: IDLE → INITIALIZED via initialize().
 * Calling run(), pause(), or resume() here throws IllegalStateException.
 */
public class IdleState extends AbstractSimulationState {

    @Override
    public void initialize(SimulationController ctx) {
        System.out.println("[State] IDLE → initializing...");

        ctx.validateConfiguration();
        ctx.getModel().initialize(ctx.getDomain());

        ctx.getOutputHandlers().forEach(h ->
            h.initialize(ctx.getDomain(), ctx.getModel())
        );

        ctx.resetTime();

        System.out.println("[State] Transition: IDLE → INITIALIZED");
        ctx.setState(new InitializedState());
    }

    @Override
    public String getName() { return "IDLE"; }
}