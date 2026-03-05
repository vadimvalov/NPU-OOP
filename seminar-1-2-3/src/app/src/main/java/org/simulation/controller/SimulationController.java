package org.simulation.controller;

import org.simulation.controller.state.IdleState;
import org.simulation.controller.state.RunningState;
import org.simulation.controller.state.SimulationState;
import org.simulation.core.NumericalSolver;
import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationController {

    // ── State machine ─────────────────────────────────────────────────────────
    private SimulationState state = new IdleState();

    public void setState(SimulationState newState) {
        this.state = newState;
    }

    public SimulationState getState() { return state; }

    // ── Collaborators ─────────────────────────────────────────────────────────
    private PhysicalModel    model;
    private NumericalSolver  solver;
    private SimulationDomain domain;
    private final List<OutputHandler> outputHandlers = new ArrayList<>();

    // ── Simulation parameters ─────────────────────────────────────────────────
    private double totalTime   = 1.0;
    private double dt          = 1e-3;
    private int    outputEvery = 10;

    // ── Runtime counters ──────────────────────────────────────────────────────
    private double currentTime = 0.0;
    private int    currentStep = 0;

    // =========================================================================
    // Public lifecycle API — delegated to current state via handle()
    // =========================================================================

    public void initialize() { state.handle(this); }

    public void run()        { state.handle(this); }

    public void pause() {
        if (state instanceof RunningState) {
            ((RunningState) state).requestPause();
        } else {
            throw new IllegalStateException("Can only pause from RUNNING, current: " + state.getName());
        }
    }

    public void resume()     { state.handle(this); }

    // =========================================================================
    // Builder-style configuration
    // =========================================================================

    public SimulationController setModel(PhysicalModel model) {
        this.model = model;
        this.state = new IdleState();
        return this;
    }

    public SimulationController setSolver(NumericalSolver solver) {
        this.solver = solver;
        return this;
    }

    public SimulationController setDomain(SimulationDomain domain) {
        this.domain = domain;
        this.state  = new IdleState();
        return this;
    }

    public SimulationController addOutputHandler(OutputHandler handler) {
        outputHandlers.add(handler);
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

    // =========================================================================
    // Helpers for State implementations
    // =========================================================================

    public void validateConfiguration() {
        if (model  == null) throw new IllegalStateException("PhysicalModel is not set");
        if (solver == null) throw new IllegalStateException("NumericalSolver is not set");
        if (domain == null) throw new IllegalStateException("SimulationDomain is not set");
    }

    public void resetTime() {
        currentTime = 0.0;
        currentStep = 0;
    }

    public void writeOutput() {
        for (OutputHandler handler : outputHandlers) {
            handler.write(currentTime, currentStep, model.getFieldValues());
        }
    }

    public void closeOutputs() {
        for (OutputHandler handler : outputHandlers) {
            handler.close();
        }
    }

    public void advanceTime(double actualDt) {
        currentTime += actualDt;
        currentStep++;
    }

    public int computeTotalSteps() {
        return (int) Math.ceil(totalTime / dt);
    }

    public void printProgress(int totalSteps) {
        double percent = 100.0 * currentStep / totalSteps;
        System.out.printf("  Step %5d / %d  (%.1f%%)  t = %.6f s%n",
            currentStep, totalSteps, percent, currentTime);
    }

    // =========================================================================
    // Accessors
    // =========================================================================

    public double           getCurrentTime()    { return currentTime; }
    public int              getCurrentStep()    { return currentStep; }
    public double           getTotalTime()      { return totalTime; }
    public double           getDt()             { return dt; }
    public int              getOutputEvery()    { return outputEvery; }
    public PhysicalModel    getModel()          { return model; }
    public NumericalSolver  getSolver()         { return solver; }
    public SimulationDomain getDomain()         { return domain; }
    public List<OutputHandler> getOutputHandlers() {
        return Collections.unmodifiableList(outputHandlers);
    }
}