package org.simulation;

import org.simulation.controller.SimulationController;
import org.simulation.core.PhysicalModel;
import org.simulation.domain.Grid2D;
import org.simulation.models.FluidFlowModel;
import org.simulation.models.HeatTransferModel;
import org.simulation.observer.ConsoleLoggerObserver;
import org.simulation.core.HistoryRecorder;
import org.simulation.output.JSONOutputHandler;
import org.simulation.strategy.ExplicitEulerStepper;
import org.simulation.strategy.ImplicitIterativeStepper;
import org.simulation.concurrency.TaskScheduler;
import org.simulation.concurrency.ParallelHeatTransferModel;

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
        
        TaskScheduler scheduler = new TaskScheduler(4); // 4 Threads

        PhysicalModel<Double> model = new ParallelHeatTransferModel(1e-4, scheduler);
        SimulationController controller = new SimulationController()
                .setDomain(new Grid2D(NX, NY, LX, LY))
                .setStepper(new ExplicitEulerStepper())
                .setModel(model)
                .setTotalTime(TOTAL_TIME)
                .setDt(DT)
                .setOutputEvery(OUTPUT_EVERY);

        controller.addObserver(new ConsoleLoggerObserver(OUTPUT_EVERY));
        
        // Task 2 Integration: Add History Recorder
        HistoryRecorder recorder = new HistoryRecorder(controller, OUTPUT_EVERY, 100);
        controller.addObserver(recorder);
        
        controller.initialize();
        controller.run();
        
        System.out.println("Recorded history size: " + recorder.getHistory().size());

        System.out.println("Switching to FluidFlowModel...");
        model = new FluidFlowModel(1e-3, 1000.0);
        controller.setModel(model)
                  .setStepper(new ImplicitIterativeStepper());
        controller.initialize();
        controller.run();

        System.out.println("Generic Simulation completed successfully");
        
        // Shut down concurrency tools
        scheduler.shutdown();
    }
}