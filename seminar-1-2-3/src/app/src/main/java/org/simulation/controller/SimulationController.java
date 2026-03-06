package org.simulation.controller;

import org.simulation.controller.state.IdleState;
import org.simulation.controller.state.RunningState;
import org.simulation.controller.state.SimulationState;
import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.observer.ISimulationObserver;
import org.simulation.observer.SimulationEventBus;
import org.simulation.strategy.IStepperStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationController {

    // ── State machine ─────────────────────────────────────────────────────────
    private SimulationState state = new IdleState();

    public void setState(SimulationState newState) { this.state = newState; }
    public SimulationState getState()              { return state; }

    // ── Observer event bus ────────────────────────────────────────────────────
    private final SimulationEventBus eventBus = new SimulationEventBus();

    public SimulationController addObserver(ISimulationObserver observer) {
        eventBus.subscribe(observer);
        return this;
    }

    public SimulationEventBus getEventBus() { return eventBus; }

    // ── Collaborators ─────────────────────────────────────────────────────────
    private PhysicalModel    model;
    private IStepperStrategy stepper;
    private SimulationDomain domain;

    // kept for backward compat with OutputObserver wrapping existing handlers
    private final List<OutputHandler> outputHandlers = new ArrayList<>();

    // ── Simulation parameters ─────────────────────────────────────────────────
    private double totalTime   = 1.0;
    private double dt          = 1e-3;
    private int    outputEvery = 10;

    // ── Runtime counters ──────────────────────────────────────────────────────
    private double currentTime = 0.0;
    private int    currentStep = 0;

    // =========================================================================
    // Public lifecycle API
    // =========================================================================

    public void initialize() { state.handle(this); }
    public void run()        { state.handle(this); }
    public void resume()     { state.handle(this); }

    public void pause() {
        if (state instanceof RunningState) {
            ((RunningState) state).requestPause();
        } else {
            throw new IllegalStateException("Can only pause from RUNNING, current: " + state.getName());
        }
    }

    // =========================================================================
    // Builder-style configuration
    // =========================================================================

    public SimulationController setModel(PhysicalModel model) {
        this.model = model;
        this.state = new IdleState();
        return this;
    }

    public SimulationController setStepper(IStepperStrategy stepper) {
        this.stepper = stepper;
        return this;
    }

    public void swapStepper(IStepperStrategy stepper) {
        System.out.println("[Controller] Swapping stepper: "
            + this.stepper.getName() + " → " + stepper.getName());
        this.stepper = stepper;
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
        if (totalTime <= 0) throw new IllegalArgumentException("totalTime must be positive");
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
        if (model   == null) throw new IllegalStateException("PhysicalModel is not set");
        if (stepper == null) throw new IllegalStateException("IStepperStrategy is not set");
        if (domain  == null) throw new IllegalStateException("SimulationDomain is not set");
    }

    public void resetTime() {
        currentTime = 0.0;
        currentStep = 0;
    }

    /** Direct write — used only by OutputObserver internally. */
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
        System.out.printf("  Step %5d / %d  (%.1f%%)  t = %.6f s%n",
            currentStep, totalSteps,
            100.0 * currentStep / totalSteps, currentTime);
    }

    // =========================================================================
    // Accessors
    // =========================================================================

    public double            getCurrentTime()      { return currentTime; }
    public int               getCurrentStep()      { return currentStep; }
    public double            getTotalTime()         { return totalTime; }
    public double            getDt()               { return dt; }
    public int               getOutputEvery()      { return outputEvery; }
    public PhysicalModel     getModel()            { return model; }
    public IStepperStrategy  getStepper()          { return stepper; }
    public SimulationDomain  getDomain()           { return domain; }
    public List<OutputHandler> getOutputHandlers() {
        return Collections.unmodifiableList(outputHandlers);
    }
}