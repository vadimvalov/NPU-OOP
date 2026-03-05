package org.simulation;

import org.simulation.adapter.LegacyHeatModelAdapter;
import org.simulation.controller.SimulationController;
import org.simulation.domain.Grid2D;
import org.simulation.models.HeatTransferModel;
import org.simulation.output.ConsoleOutputHandler;
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

        runScenario(
            "Heat Transfer (Native)",
            new HeatTransferModel(1e-4),
            "heat_native.json"
        );

        runScenario(
            "Heat Transfer (Legacy Adapter)",
            new LegacyHeatModelAdapter(1e-4, DT),
            "heat_legacy.json"
        );

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   All scenarios completed successfully           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void runScenario(String scenarioName,
                                    org.simulation.core.PhysicalModel model,
                                    String jsonFile) {

        System.out.println("──────────────────────────────────────────────────");
        System.out.println("  Scenario: " + scenarioName);
        System.out.println("──────────────────────────────────────────────────");

        Grid2D domain = new Grid2D(NX, NY, LX, LY);

        ConsoleOutputHandler console = new ConsoleOutputHandler();
        console.setParameter("verbose", "true");

        JSONOutputHandler json = new JSONOutputHandler(jsonFile);

        SimulationController controller = new SimulationController()
                .setDomain(domain)
                .setStepper(new ExplicitEulerStepper())
                .setModel(model)
                .addOutputHandler(console)
                .addOutputHandler(json)
                .setTotalTime(TOTAL_TIME)
                .setDt(DT)
                .setOutputEvery(OUTPUT_EVERY);

        controller.initialize();
        controller.run();

        System.out.println();
    }
}