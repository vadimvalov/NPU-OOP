package org.simulation.controller;

import org.simulation.controller.state.IdleState;
import org.simulation.controller.state.SimulationState;
import org.simulation.core.NumericalSolver;
import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SimulationController — refactored with the State pattern.
 *
 * Public API (initialize / run / pause / resume) is now just:
 *     state.operation(this)
 *
 * The controller itself holds no if/else lifecycle logic —
 * that responsibility has moved entirely into the State objects.
 *
 * Package-private helpers (writeOutput, closeOutputs, etc.) are
 * exposed so that State implementations can drive the loop without
 * needing reflection or tight coupling.
 */
public class SimulationController {

    // ── State machine ────────────────────────────────────────────────────────
    private SimulationState state = new IdleState();

    /** Called by State objects to perform a transition. */
    public void setState(SimulationState newState) {
        this.state = newState;
    }

    public SimulationState getState() { return state; }

    // ── Collaborators (injected via builder-style setters) ───────────────────
    private PhysicalModel    model;
    private NumericalSolver  solver;
    private SimulationDomain domain;
    private final List<OutputHandler> outputHandlers = new ArrayList<>();

    // ── Simulation parameters ────────────────────────────────────────────────
    private double totalTime   = 1.0;
    private double dt          = 1e-3;
    private int    outputEvery = 10;

    // ── Runtime counters ─────────────────────────────────────────────────────
    private double  currentTime  = 0.0;
    private int     currentStep  = 0;

    // ── Pause flag (set by pause(), checked inside RunningState loop) ────────
    private volatile boolean pauseRequested = false;

    // =========================================================================
    // Public lifecycle API — all delegated to the current state
    // =========================================================================

    public void initialize() { state.initialize(this); }

    public void run()        { state.run(this); }

    public void pause()      { state.pause(this); }

    public void resume()     { state.resume(this); }

    // =========================================================================
    // Builder-style configuration setters
    // =========================================================================

    public SimulationController setModel(PhysicalModel model) {
        this.model = model;
        this.state = new IdleState();   // reset state if reconfigured
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
    // Package-accessible helpers used by State implementations
    // =========================================================================

    /** Validates that model, solver, and domain are all set. */
    public void validateConfiguration() {
        if (model  == null) throw new IllegalStateException("PhysicalModel is not set");
        if (solver == null) throw new IllegalStateException("NumericalSolver is not set");
        if (domain == null) throw new IllegalStateException("SimulationDomain is not set");
    }

    /** Resets time counters (called during initialization). */
    public void resetTime() {
        currentTime = 0.0;
        currentStep = 0;
    }

    /** Writes a snapshot to all output handlers. */
    public void writeOutput() {
        for (OutputHandler handler : outputHandlers) {
            handler.write(currentTime, currentStep, model.getFieldValues());
        }
    }

    /** Closes all output handlers. */
    public void closeOutputs() {
        for (OutputHandler handler : outputHandlers) {
            handler.close();
        }
    }

    /** Advances simulation time and step counter by one step. */
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

    // ── Pause flag helpers ───────────────────────────────────────────────────

    public void requestPause()      { pauseRequested = true; }
    public void clearPauseRequest() { pauseRequested = false; }
    public boolean isPauseRequested() { return pauseRequested; }

    // =========================================================================
    // Accessors
    // =========================================================================

    public double          getCurrentTime()    { return currentTime; }
    public int             getCurrentStep()    { return currentStep; }
    public double          getTotalTime()      { return totalTime; }
    public double          getDt()             { return dt; }
    public int             getOutputEvery()    { return outputEvery; }
    public PhysicalModel   getModel()          { return model; }
    public NumericalSolver getSolver()         { return solver; }
    public SimulationDomain getDomain()        { return domain; }
    public List<OutputHandler> getOutputHandlers() {
        return Collections.unmodifiableList(outputHandlers);
    }
}