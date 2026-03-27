package org.simulation;

import org.simulation.controller.SimulationController;
import org.simulation.core.PhysicalModel;
import org.simulation.domain.Grid2D;
import org.simulation.models.FluidFlowModel;
import org.simulation.models.HeatTransferModel;
import org.simulation.observer.ConsoleLoggerObserver;
import org.simulation.output.CSVOutputHandler;
import org.simulation.strategy.ExplicitEulerStepper;
import org.simulation.strategy.ImplicitIterativeStepper;

public class Main {
    private static final int    NX           = 20;
    private static final int    NY           = 20;
    private static final double LX           = 1.0;
    private static final double LY           = 1.0;
    private static final double TOTAL_TIME   = 0.5;
    private static final double DT           = 1e-3;
    private static final int    OUTPUT_EVERY = 50;

    public static void main(String[] args) {
        System.out.println("Seminar Generic Components Demo");

        PhysicalModel<Double> model = new HeatTransferModel(1e-4);
        SimulationController controller = new SimulationController()
                .setDomain(new Grid2D(NX, NY, LX, LY))
                .setStepper(new ExplicitEulerStepper())
                .setModel(model)
                .setTotalTime(TOTAL_TIME)
                .setDt(DT)
                .setOutputEvery(OUTPUT_EVERY);

        controller.addObserver(new ConsoleLoggerObserver(OUTPUT_EVERY));
        controller.initialize();
        controller.run();

        System.out.println("Switching to FluidFlowModel...");
        model = new FluidFlowModel(1e-3, 1000.0);
        controller.setModel(model)
                  .setStepper(new ImplicitIterativeStepper());
        controller.initialize();
        controller.run();

        System.out.println("Generic Simulation completed successfully");
    }
}