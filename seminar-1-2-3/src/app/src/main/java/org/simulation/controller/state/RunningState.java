package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * RUNNING — drives the time-step loop.
 *
 * Pause is cooperative: requestPause() sets a flag,
 * the loop picks it up at the next step boundary.
 */
public class RunningState extends AbstractSimulationState {

    private volatile boolean pauseRequested = false;

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        ctx.writeOutput(); // snapshot at t=0
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

                ctx.getStepper().step(ctx.getModel(), ctx.getDomain(), actualDt);
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