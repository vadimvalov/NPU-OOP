package org.simulation.controller.state;

import org.simulation.controller.SimulationController;

/**
 * RUNNING — the time-stepping loop is active.
 *
 * Valid transitions:
 *   RUNNING → PAUSED   via pause()
 *   RUNNING → FINISHED when all steps are completed
 *
 * The loop checks ctx.isPauseRequested() each step so that pause()
 * can interrupt cleanly at a step boundary (no mid-step corruption).
 */
public class RunningState extends AbstractSimulationState {

    @Override
    public void run(SimulationController ctx) {
        System.out.println("[State] RUNNING: starting time-step loop from step "
            + ctx.getCurrentStep());

        ctx.writeOutput();   // snapshot at current step before advancing

        int totalSteps = ctx.computeTotalSteps();

        while (ctx.getCurrentStep() < totalSteps) {

            // ── pause hook: checked every step ──────────────────────────────
            if (ctx.isPauseRequested()) {
                ctx.clearPauseRequest();
                System.out.println("[State] Transition: RUNNING → PAUSED at step "
                    + ctx.getCurrentStep());
                ctx.setState(new PausedState());
                return;   // exit loop; state is now PAUSED
            }

            // ── time-step ────────────────────────────────────────────────────
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

        // ── finished ─────────────────────────────────────────────────────────
        ctx.closeOutputs();
        System.out.println("[State] Transition: RUNNING → FINISHED");
        ctx.setState(new FinishedState());

        System.out.println("=== Simulation finished ===");
        System.out.printf("  Completed %d steps, simulated time = %.6f s%n",
            ctx.getCurrentStep(), ctx.getCurrentTime());
    }

    @Override
    public void pause(SimulationController ctx) {
        // Called from outside (e.g. another thread) — sets a flag,
        // the loop above will pick it up at the next step boundary.
        System.out.println("[State] Pause requested (will take effect at next step boundary)");
        ctx.requestPause();
    }

    @Override
    public String getName() { return "RUNNING"; }
}