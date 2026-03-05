package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * Base state: enter/exit log by default, handle() throws.
 * Concrete states override only what's valid for them.
 */
public abstract class AbstractSimulationState implements SimulationState {

    @Override
    public void enter(SimulationController ctx) {
        System.out.println("[State] → " + getName());
    }

    @Override
    public void exit(SimulationController ctx) {
        // no-op by default
    }

    @Override
    public void handle(SimulationController ctx) {
        throw new IllegalStateException(
            "handle() not valid in state [" + getName() + "]"
        );
    }

    /**
     * Transition helper — always go through exit/enter so logging is consistent.
     */
    protected void transitionTo(SimulationController ctx, SimulationState next) {
        this.exit(ctx);
        ctx.setState(next);
        next.enter(ctx);
    }
}