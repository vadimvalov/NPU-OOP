package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

public class CompletedState extends AbstractSimulationState {

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        ctx.closeOutputs();
        System.out.println("=== Simulation COMPLETED ===");
        System.out.printf("  Steps: %d  |  Time: %.6f s%n",
            ctx.getCurrentStep(), ctx.getCurrentTime());
    }

    @Override
    public void handle(SimulationController ctx) {
        throw new IllegalStateException("Simulation already COMPLETED.");
    }

    @Override
    public String getName() { return "COMPLETED"; }
}