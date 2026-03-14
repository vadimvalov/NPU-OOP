package org.simulation.controller.state;

import org.simulation.controller.SimulationController;
import org.simulation.observer.SimulationEvent;

/**
 * RUNNING — drives the time-step loop and publishes events.
 *
 * Публикует события вместо прямых вызовов writeOutput/printProgress.
 * Все observer-ы (логирование, запись файлов, конвергенция) реагируют на события.
 */
public class RunningState extends AbstractSimulationState {

    private volatile boolean pauseRequested = false;

    @Override
    public void enter(SimulationController ctx) {
        super.enter(ctx);
        // ON_START — OutputObserver вызовет handler.initialize() и первый write
        ctx.getEventBus().publish(
            SimulationEvent.start(ctx.getCurrentTime(), ctx.getCurrentStep())
        );
    }

    @Override
    public void handle(SimulationController ctx) {
        int totalSteps = ctx.computeTotalSteps();

        try {
            while (ctx.getCurrentStep() < totalSteps) {

                if (pauseRequested) {
                    pauseRequested = false;
                    // ON_PAUSED — State→Observer interaction (Interaction #2)
                    ctx.getEventBus().publish(
                        SimulationEvent.paused(ctx.getCurrentTime(), ctx.getCurrentStep())
                    );
                    transitionTo(ctx, new PausedState());
                    return;
                }

                double actualDt = Math.min(ctx.getDt(),
                    ctx.getTotalTime() - ctx.getCurrentTime());
                if (actualDt <= 0) break;

                ctx.getEventBus().publish(
                    SimulationEvent.beforeStep(ctx.getCurrentTime(), ctx.getCurrentStep())
                );

                ctx.getStepper().step(ctx.getModel(), ctx.getDomain(), actualDt);
                ctx.advanceTime(actualDt);

                double residual = ctx.getStepper().getLastResidual();

                // ON_AFTER_STEP — ConvergenceObserver следит за residual,
                // OutputObserver пишет файл, ConsoleLoggerObserver логирует
                ctx.getEventBus().publish(
                    SimulationEvent.afterStep(ctx.getCurrentTime(), ctx.getCurrentStep(), residual)
                );
            }

        } catch (Exception e) {
            ctx.getEventBus().publish(
                SimulationEvent.error(ctx.getCurrentTime(), ctx.getCurrentStep(), e.getMessage())
            );
            transitionTo(ctx, new FailedState(e));
            return;
        }

        // ON_STOP — OutputObserver закроет файлы
        ctx.getEventBus().publish(
            SimulationEvent.stop(ctx.getCurrentTime(), ctx.getCurrentStep())
        );
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