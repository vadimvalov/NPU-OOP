package org.simulation.controller;

import org.simulation.core.NumericalSolver;
import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    private PhysicalModel model; // D principle, we inject the whole model, not classes
    private NumericalSolver solver;
    private SimulationDomain domain;
    private final List<OutputHandler> outputHandlers = new ArrayList<>();

    private double totalTime    = 1.0;
    private double dt           = 1e-3;
    private int    outputEvery  = 10;

    private double currentTime  = 0.0;
    private int    currentStep  = 0;
    private boolean initialized = false;

    public SimulationController setModel(PhysicalModel model) {
        this.model = model;
        this.initialized = false;
        return this;
    }

    public SimulationController setSolver(NumericalSolver solver) {
        this.solver = solver;
        return this;
    }

    public SimulationController setDomain(SimulationDomain domain) {
        this.domain = domain;
        this.initialized = false;
        return this;
    }

    public SimulationController addOutputHandler(OutputHandler handler) {
        this.outputHandlers.add(handler);
        return this;
    }

    public SimulationController setTotalTime(double totalTime) {
        if (totalTime <= 0) throw new IllegalArgumentException("Total time must be positive");
        this.totalTime = totalTime;
        return this;
    }

    public SimulationController setDt(double dt) {
        if (dt <= 0) throw new IllegalArgumentException("dt must be positive");
        this.dt = dt;
        return this;
    }

    public SimulationController setOutputEvery(int steps) {
        if (steps < 1) throw new IllegalArgumentException("outputEvery must be >= 1");
        this.outputEvery = steps;
        return this;
    }

    public void initialize() {
        validateConfiguration();

        model.initialize(domain);

        for (OutputHandler handler : outputHandlers) {
            handler.initialize(domain, model);
        }

        currentTime = 0.0;
        currentStep = 0;
        initialized = true;

        System.out.println("=== Simulation initialized ===");
        System.out.println("  Model:   " + model.getName());
        System.out.println("  Solver:  " + solver.getName());
        System.out.println("  Domain:  " + domain);
        System.out.printf( "  Time:    0.0 → %.4f s  (dt=%.2e, steps=%d)%n",
                totalTime, dt, computeTotalSteps());

        warnIfUnstable();
    }

    public void run() {
        if (!initialized) {
            initialize();
        }

        System.out.println("\n=== Simulation started: " + model.getName() + " ===");

        writeOutput();

        int totalSteps = computeTotalSteps();

        while (currentStep < totalSteps) {

            double actualDt = Math.min(dt, totalTime - currentTime);
            if (actualDt <= 0) break;

            solver.step(model, domain, actualDt);

            currentTime += actualDt;
            currentStep++;

            if (currentStep % outputEvery == 0 || currentStep == totalSteps) {
                writeOutput();
                printProgress(totalSteps);
            }
        }

        finalizeOutputs();

        System.out.println("=== Simulation finished ===");
        System.out.printf("  Completed %d steps, simulated time = %.6f s%n",
                currentStep, currentTime);
    }

    private void writeOutput() {
        for (OutputHandler handler : outputHandlers) {
            handler.write(currentTime, currentStep, model.getFieldValues());
        }
    }

    private void finalizeOutputs() {
        for (OutputHandler handler : outputHandlers) {
            handler.finalize();
        }
    }

    private int computeTotalSteps() {
        return (int) Math.ceil(totalTime / dt);
    }

    private void printProgress(int totalSteps) {
        double percent = 100.0 * currentStep / totalSteps;
        System.out.printf("  Step %5d / %d  (%.1f%%)  t = %.6f s%n",
                currentStep, totalSteps, percent, currentTime);
    }

    private void warnIfUnstable() {
        if (!solver.isStable(dt, domain)) {
            System.err.println("WARNING: dt=" + dt +
                    " may violate stability condition for solver " + solver.getName());
            System.err.println("         Consider reducing dt or increasing grid spacing.");
        }
    }

    private void validateConfiguration() {
        if (model  == null) throw new IllegalStateException("PhysicalModel is not set");
        if (solver == null) throw new IllegalStateException("NumericalSolver is not set");
        if (domain == null) throw new IllegalStateException("SimulationDomain is not set");
    }

    public double getCurrentTime()  { return currentTime; }
    public int    getCurrentStep()  { return currentStep; }
    public PhysicalModel  getModel()  { return model; }
    public NumericalSolver getSolver() { return solver; }
    public SimulationDomain getDomain() { return domain; }
}