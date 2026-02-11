package org.simulation;

import org.simulation.controller.SimulationController;
import org.simulation.domain.Grid2D;
import org.simulation.models.FluidFlowModel;
import org.simulation.models.HeatTransferModel;
import org.simulation.models.ReactiveTransportModel;
import org.simulation.output.CSVOutputHandler;
import org.simulation.output.ConsoleOutputHandler;
import org.simulation.solvers.FiniteDifferenceSolver;

public class Main {

    private static final int    NX         = 20;
    private static final int    NY         = 20;
    private static final double LX         = 1.0;
    private static final double LY         = 1.0;

    private static final double TOTAL_TIME  = 0.5;
    private static final double DT          = 1e-3;
    private static final int    OUTPUT_EVERY = 50;

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   Modular Scientific Simulation Framework        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        runScenario(
            "Heat Transfer",
            new HeatTransferModel(1e-4),
            "heat_transfer.csv"
        );

        runScenario(
            "Fluid Flow",
            new FluidFlowModel(1e-4, 1000.0),
            "fluid_flow.csv"
        );

        runScenario(
            "Reactive Transport",
            new ReactiveTransportModel(1e-4, 0.1),
            "reactive_transport.csv"
        );

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   All scenarios completed successfully           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void runScenario(String scenarioName,
                                    org.simulation.core.PhysicalModel model,
                                    String csvFile) {

        System.out.println("──────────────────────────────────────────────────");
        System.out.println("  Scenario: " + scenarioName);
        System.out.println("──────────────────────────────────────────────────");

        Grid2D domain = new Grid2D(NX, NY, LX, LY);

        FiniteDifferenceSolver solver = new FiniteDifferenceSolver();
        solver.setParameter("diffusivity", 1e-4);

        ConsoleOutputHandler console = new ConsoleOutputHandler();
        console.setParameter("verbose", "true");

        CSVOutputHandler csv = new CSVOutputHandler(csvFile);

        SimulationController controller = new SimulationController()
                .setDomain(domain)
                .setSolver(solver)
                .setModel(model)
                .addOutputHandler(console)
                .addOutputHandler(csv)
                .setTotalTime(TOTAL_TIME)
                .setDt(DT)
                .setOutputEvery(OUTPUT_EVERY);

        controller.run();

        System.out.println();
    }
}