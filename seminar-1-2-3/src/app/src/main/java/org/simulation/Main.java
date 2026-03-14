package org.simulation;

import org.simulation.adapter.LegacyHeatModelAdapter;
import org.simulation.controller.SimulationController;
import org.simulation.core.AbstractPhysicalModel;
import org.simulation.domain.Grid2D;
import org.simulation.models.FluidFlowModel;
import org.simulation.models.HeatTransferModel;
import org.simulation.models.WavePropagationModel;
import org.simulation.observer.ConsoleLoggerObserver;
import org.simulation.observer.ConvergenceObserver;
import org.simulation.observer.OutputObserver;
import org.simulation.output.CSVOutputHandler;
import org.simulation.output.JSONOutputHandler;
import org.simulation.strategy.ExplicitEulerStepper;
import org.simulation.strategy.ImplicitIterativeStepper;

import org.simulation.strategy.IStepperStrategy;
import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
/**
 * SEMINAR 7 — Main entry point.
 *
 * Demonstrates all four required tasks:
 *
 *  Task 1 — Multi-level inheritance hierarchy:
 *    SimulationComponent → AbstractPhysicalModel → HeatTransferModel
 *                                                → FluidFlowModel
 *                                                → WavePropagationModel
 *
 *  Task 2 — Polymorphic simulation execution:
 *    PhysicalModel model = new HeatTransferModel(...)
 *    runScenario(model, ...)          // same controller, same loop
 *    model = new FluidFlowModel(...)
 *    runScenario(model, ...)          // swapped without changing controller
 *    model = new WavePropagationModel(...)
 *    runScenario(model, ...)          // third substitution
 *
 *  Task 3 — Extend without modifying the controller:
 *    WavePropagationModel added; SimulationController is untouched.
 *
 *  Task 4 — Three overridden methods shown explicitly:
 *    initialize() / initializeField() — each model sets its own ICs
 *    applyBoundaryCondition()         — Dirichlet vs Neumann vs mixed
 *    computeRHS()                     — different physics per model
 */
public class Main {

    private static final int    NX           = 20;
    private static final int    NY           = 20;
    private static final double LX           = 1.0;
    private static final double LY           = 1.0;
    private static final double TOTAL_TIME   = 0.5;
    private static final double DT           = 1e-3;
    private static final int    OUTPUT_EVERY = 50;

    public static void main(String[] args) {

        banner("Seminar 7 — Inheritance, Polymorphism & Extensible Simulation Framework");

        // ── Task 1: print the hierarchy before running anything ───────────────
        printHierarchy();

        // ── Task 2: POLYMORPHIC substitution — same call, different model ─────
        //
        // The variable 'model' is of type PhysicalModel (the interface).
        // Each assignment replaces the concrete object at runtime.
        // runScenario() never knows which subclass it received.

        // ── Task 3: RUNTIME POLYMORPHIC EXECUTION ─────────────────────────────
        //
        // Three independent axes, each swapped via base-class reference:
        //   PhysicalModel   model   — what physics to simulate
        //   NumericalSolver stepper — how to integrate in time
        //   OutputHandler   output  — where/how to write results
        //
        // controller.run() is identical every time — the loop never changes.

        PhysicalModel    model;
        IStepperStrategy stepper;
        OutputHandler    output;

        model   = new HeatTransferModel(1e-4);
        stepper = new ExplicitEulerStepper();
        output  = new CSVOutputHandler("heat_transfer.csv");
        runScenario("Heat  | Explicit | CSV",  model, stepper, output);

        model   = new FluidFlowModel(1e-3, 1000.0);
        stepper = new ImplicitIterativeStepper();
        output  = new JSONOutputHandler("fluid_flow.json");
        runScenario("Fluid | Implicit | JSON", model, stepper, output);

        model   = new WavePropagationModel(340.0);
        stepper = new ExplicitEulerStepper();
        output  = new CSVOutputHandler("wave_propagation.csv");
        runScenario("Wave  | Explicit | CSV",  model, stepper, output);

        demonstrateOverriding();

        model   = new LegacyHeatModelAdapter(1e-4, DT);
        stepper = new ExplicitEulerStepper();
        output  = new CSVOutputHandler("heat_legacy.csv");
        runScenario("Heat (Legacy Adapter) | Explicit | CSV", model, stepper, output);

        banner("All scenarios completed successfully");
    }
    /**
     * Task 3 — Runtime polymorphic execution.
     *
     * The simulation loop (controller.run()) never changes.
     * What changes at runtime:
     *   - model      : PhysicalModel reference  → Heat / Fluid / Wave
     *   - stepper    : NumericalSolver reference → Explicit / Implicit
     *   - output     : OutputHandler reference   → CSV / JSON / Console
     *
     * The controller only knows the interfaces — never the concrete types.
     */
    private static void runScenario(String scenarioName,
                                    PhysicalModel model,
                                    IStepperStrategy stepper,
                                    OutputHandler output) {

        System.out.println("──────────────────────────────────────────────────");
        System.out.println("  Scenario : " + scenarioName);
        System.out.println("  Model    : " + model.getClass().getSimpleName());
        System.out.println("  Solver   : " + stepper.getClass().getSimpleName());
        System.out.println("  Output   : " + output.getClass().getSimpleName());
        System.out.println("──────────────────────────────────────────────────");

        Grid2D domain = new Grid2D(NX, NY, LX, LY);

        // SimulationController is identical every time — loop never modified
        SimulationController controller = new SimulationController()
                .setDomain(domain)
                .setStepper(stepper)   // runtime-selected solver
                .setModel(model)       // runtime-selected model
                .setTotalTime(TOTAL_TIME)
                .setDt(DT)
                .setOutputEvery(OUTPUT_EVERY);

        controller.addObserver(new ConsoleLoggerObserver(OUTPUT_EVERY));
        controller.addObserver(new ConvergenceObserver(controller, 1.0, 10));
        controller.addObserver(new OutputObserver(
            controller,
            output,                    // runtime-selected output handler
            OUTPUT_EVERY
        ));

        controller.initialize();
        controller.run();
        System.out.println();
    }

    // =========================================================================
    // Task 1 — Print the inheritance hierarchy
    // =========================================================================

    private static void printHierarchy() {
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  TASK 1 — Multi-Level Inheritance Hierarchy             │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│                                                         │");
        System.out.println("│  SimulationComponent          (abstract, level 1)       │");
        System.out.println("│      └── AbstractPhysicalModel (abstract, level 2)      │");
        System.out.println("│              ├── HeatTransferModel    (concrete, lv 3)  │");
        System.out.println("│              ├── FluidFlowModel       (concrete, lv 3)  │");
        System.out.println("│              ├── ReactiveTransportModel (concrete, lv3) │");
        System.out.println("│              └── WavePropagationModel (concrete, lv 3)  │");
        System.out.println("│                                                         │");
        System.out.println("│  BoundaryCondition (interface)                          │");
        System.out.println("│      ├── DirichletBoundaryCondition  (u = value)        │");
        System.out.println("│      └── NeumannBoundaryCondition    (du/dn = flux)     │");
        System.out.println("│                                                         │");
        System.out.println("│  IStepperStrategy (interface)                           │");
        System.out.println("│      ├── ExplicitEulerStepper                           │");
        System.out.println("│      └── ImplicitIterativeStepper                       │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    // =========================================================================
    // Task 4 — Demonstrate method overriding with instanceof check
    //          Each model's overridden methods behave differently.
    // =========================================================================

    private static void demonstrateOverriding() {
        banner("TASK 4 — Demonstrating Method Overriding");

        // Build all three concrete models through the abstract reference
        AbstractPhysicalModel[] models = {
            new HeatTransferModel(1e-4),
            new FluidFlowModel(1e-3, 1000.0),
            new WavePropagationModel(340.0)
        };

        Grid2D domain = new Grid2D(NX, NY, LX, LY);

        for (AbstractPhysicalModel m : models) {
            System.out.println("─── Model: " + m.getName() + " ───");

            // Overridden method #1: initialize() → initializeField()
            System.out.println("  [1] initialize() — calls overridden initializeField():");
            m.initialize(domain);   // dispatches to the correct subclass

            // Overridden method #2: applyBoundaryCondition()
            System.out.println("  [2] applyBoundaryCondition() — each model applies different BC:");
            // (already called inside initialize; calling again to show override)
            // We can safely call it again to demonstrate
            m.applyBoundaryCondition(domain);  // protected, shown here for demo
            System.out.printf("      After BC: min=%.3f  max=%.3f%n", m.getMin(), m.getMax());

            // Overridden method #3: computeRHS()
            System.out.println("  [3] computeRHS() — physics differs per model:");
            double[] rhs = m.computeRHS(domain, 0.0);
            double rhsMax = 0.0;
            for (double v : rhs) rhsMax = Math.max(rhsMax, Math.abs(v));
            System.out.printf("      max |RHS| = %.6e%n", rhsMax);

            // Overridden method #4: getName() / getFieldValues() (writeOutput concept)
            System.out.println("  [4] getFieldValues() / getName() — each returns model-specific data:");
            double[] fv = m.getFieldValues();
            System.out.printf("      Model name: '%s', field size: %d%n", m.getName(), fv.length);
            System.out.println();
        }
    }

    // =========================================================================
    // Utility
    // =========================================================================

    private static void banner(String text) {
        int w = Math.max(text.length() + 4, 54);
        String line = "═".repeat(w);
        System.out.println("╔" + line + "╗");
        System.out.printf( "║  %-" + (w - 2) + "s║%n", text);
        System.out.println("╚" + line + "╝");
        System.out.println();
    }
}