package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * FAILED — simulation terminated due to an exception. Terminal state.
 *
 * enter() — logs the error, attempts emergency output flush.
 * handle() — throws: no recovery from FAILED.
 *
 * Transitioned to from RunningState when any exception escapes the loop.
 */
public class FailedState extends AbstractSimulationState {

    private final Exception cause;

    public FailedState(Exception cause) {
        this.cause = cause;
    }

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        System.err.println("=== Simulation FAILED ===");
        System.err.println("  Cause: " + cause.getMessage());
        System.err.printf("  At step=%d  t=%.6f%n",
            ctx.getCurrentStep(), ctx.getCurrentTime());

        // best-effort flush — don't let output loss compound the failure
        try {
            ctx.closeOutputs();
        } catch (Exception ignored) {}
    }

    @Override
    public void handle(SimulationController ctx) {
        throw new IllegalStateException(
            "Simulation FAILED — cannot continue. Cause: " + cause.getMessage(), cause);
    }

    public Exception getCause() { return cause; }

    @Override
    public String getName() { return "FAILED"; }
}