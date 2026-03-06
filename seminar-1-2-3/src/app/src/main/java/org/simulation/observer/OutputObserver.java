package org.simulation.observer;

import org.simulation.core.OutputHandler;
import org.simulation.controller.SimulationController;

/**
 * Drives existing OutputHandler implementations (JSON, CSV, Console)
 * via Observer events instead of direct calls.
 *
 * ON_START      → handler.initialize()
 * ON_AFTER_STEP → handler.write()  (every outputEvery steps)
 * ON_STOP / ON_ERROR → handler.close()
 */
public class OutputObserver implements ISimulationObserver {

    private final SimulationController controller;
    private final OutputHandler        handler;
    private final int                  outputEvery;

    public OutputObserver(SimulationController controller,
                           OutputHandler handler,
                           int outputEvery) {
        this.controller  = controller;
        this.handler     = handler;
        this.outputEvery = outputEvery;
    }

    @Override
    public void onEvent(SimulationEvent event) {
        switch (event.getType()) {
            case ON_START:
                handler.initialize(controller.getDomain(), controller.getModel());
                handler.write(event.getTime(), event.getStep(),
                    controller.getModel().getFieldValues());
                break;
            case ON_AFTER_STEP:
                if (event.getStep() % outputEvery == 0) {
                    handler.write(event.getTime(), event.getStep(),
                        controller.getModel().getFieldValues());
                }
                break;
            case ON_STOP:
            case ON_ERROR:
                handler.close();
                break;
            default:
                break;
        }
    }

    @Override
    public String getName() { return "OutputObserver[" + handler.getName() + "]"; }
}