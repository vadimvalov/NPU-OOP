package org.simulation.observer;

import org.simulation.controller.SimulationController;
import org.simulation.strategy.ImplicitIterativeStepper;

/**
 * Observer → Strategy interaction (Interaction #1).
 *
 * Monitors residual on each ON_AFTER_STEP.
 * If residual stays above threshold for stepsBeforeSwitch consecutive steps,
 * swaps the stepper from Explicit to Implicit and publishes ON_SOLVER_SWITCHED.
 */
public class ConvergenceObserver implements ISimulationObserver {

    private final SimulationController controller;
    private final double               residualThreshold;
    private final int                  stepsBeforeSwitch;

    private int    highResidualCount = 0;
    private boolean alreadySwitched  = false;

    public ConvergenceObserver(SimulationController controller,
                                double residualThreshold,
                                int stepsBeforeSwitch) {
        this.controller        = controller;
        this.residualThreshold = residualThreshold;
        this.stepsBeforeSwitch = stepsBeforeSwitch;
    }

    @Override
    public void onEvent(SimulationEvent event) {
        if (event.getType() != SimulationEvent.Type.ON_AFTER_STEP) return;
        if (alreadySwitched) return;

        double residual = (Double) event.getMeta("residual");

        if (residual > residualThreshold) {
            highResidualCount++;
            if (highResidualCount >= stepsBeforeSwitch) {
                String fromName = controller.getStepper().getName();
                controller.swapStepper(new ImplicitIterativeStepper());
                alreadySwitched = true;

                controller.getEventBus().publish(
                    SimulationEvent.solverSwitched(
                        event.getTime(), event.getStep(),
                        fromName, controller.getStepper().getName()
                    )
                );
            }
        } else {
            highResidualCount = 0;
        }
    }

    @Override
    public String getName() { return "ConvergenceObserver"; }
}