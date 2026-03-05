package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * RUNNING — the time-stepping loop is active.
 *
 * enter()  — logs start.
 * handle() — drives the loop; transitions to PAUSED, COMPLETED, or FAILED.
 * exit()   — logs end.
 *
 * Pause is cooperative: pause() sets a flag,
 * handle() checks it each step boundary.
 */
public class RunningState extends AbstractSimulationState {

    private volatile boolean pauseRequested = false;

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        ctx.writeOutput(); // snapshot at t=0 before first step
    }

    @Override
    public void handle(SimulationController ctx) {
        int totalSteps = ctx.computeTotalSteps();

        try {
            while (ctx.getCurrentStep() < totalSteps) {

                if (pauseRequested) {
                    pauseRequested = false;
                    transitionTo(ctx, new PausedState());
                    return;
                }

                double actualDt = Math.min(ctx.getDt(),
                                           ctx.getTotalTime() - ctx.getCurrentTime());
                if (actualDt <= 0) break;

                ctx.getSolver().step(ctx.getModel(), ctx.getDomain(), actualDt);
                ctx.advanceTime(actualDt);

                if (ctx.getCurrentStep() % ctx.getOutputEvery() == 0
                        || ctx.getCurrentStep() == totalSteps) {
                    ctx.writeOutput();
                    ctx.printProgress(totalSteps);
                }
            }
        } catch (Exception e) {
            System.err.println("[State] Exception during run: " + e.getMessage());
            transitionTo(ctx, new FailedState(e));
            return;
        }

        transitionTo(ctx, new CompletedState());
    }

    /** Called from another thread — sets flag, loop picks it up at next step. */
    public void requestPause() {
        pauseRequested = true;
    }

    @Override
    public void exit(SimulationController ctx) {
        System.out.printf("[State] ← Exiting RUNNING at step=%d  t=%.6f%n",
            ctx.getCurrentStep(), ctx.getCurrentTime());
    }

    @Override
    public String getName() { return "RUNNING"; }
}