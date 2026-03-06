package org.simulation;

import org.simulation.adapter.LegacyHeatModelAdapter;
import org.simulation.controller.SimulationController;
import org.simulation.domain.Grid2D;
import org.simulation.models.HeatTransferModel;
import org.simulation.observer.*;
import org.simulation.output.CSVOutputHandler;
import org.simulation.output.JSONOutputHandler;
import org.simulation.strategy.ExplicitEulerStepper;

public class Main {

    private static final int    NX           = 20;
    private static final int    NY           = 20;
    private static final double LX           = 1.0;
    private static final double LY           = 1.0;

    private static final double TOTAL_TIME   = 0.5;
    private static final double DT           = 1e-3;
    private static final int    OUTPUT_EVERY = 50;

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   Modular Scientific Simulation Framework        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        runScenario("Heat Transfer (Native)",
            new HeatTransferModel(1e-4), "heat_native");

        runScenario("Heat Transfer (Legacy Adapter)",
            new LegacyHeatModelAdapter(1e-4, DT), "heat_legacy");

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   All scenarios completed successfully           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void runScenario(String name,
                                    org.simulation.core.PhysicalModel model,
                                    String filePrefix) {

        System.out.println("──────────────────────────────────────────────────");
        System.out.println("  Scenario: " + name);
        System.out.println("──────────────────────────────────────────────────");

        Grid2D domain = new Grid2D(NX, NY, LX, LY);

        SimulationController controller = new SimulationController()
                .setDomain(domain)
                .setStepper(new ExplicitEulerStepper())
                .setModel(model)
                .setTotalTime(TOTAL_TIME)
                .setDt(DT)
                .setOutputEvery(OUTPUT_EVERY);

        // Observer #1 — console logging every 50 steps
        controller.addObserver(new ConsoleLoggerObserver(OUTPUT_EVERY));

        // Observer #2 — convergence monitor, switches to Implicit if residual is high
        controller.addObserver(new ConvergenceObserver(controller, 1.0, 10));

        // Observer #3 — checkpoint every 200 steps
        controller.addObserver(new CheckpointObserver(controller, 200, "."));

        // Observer #4 — CSV output via existing CSVOutputHandler
        controller.addObserver(new OutputObserver(
            controller,
            new CSVOutputHandler(filePrefix + ".csv"),
            OUTPUT_EVERY
        ));

        // Observer #5 — JSON output via existing JSONOutputHandler
        controller.addObserver(new OutputObserver(
            controller,
            new JSONOutputHandler(filePrefix + ".json"),
            OUTPUT_EVERY
        ));

        controller.initialize();
        controller.run();

        System.out.println();
    }
}