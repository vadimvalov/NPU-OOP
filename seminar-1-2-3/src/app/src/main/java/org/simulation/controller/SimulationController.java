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

    private SimulationState state = new IdleState();
    public void setState(SimulationState newState) { this.state = newState; }
    public SimulationState getState()              { return state; }

    private final SimulationEventBus eventBus = new SimulationEventBus();
    public SimulationController addObserver(ISimulationObserver observer) {
        eventBus.subscribe(observer);
        return this;
    }
    public SimulationEventBus getEventBus() { return eventBus; }

    private PhysicalModel<?> model;
    private IStepperStrategy stepper;
    private SimulationDomain<?> domain;
    private final List<OutputHandler> outputHandlers = new ArrayList<>();

    private double totalTime   = 1.0;
    private double dt          = 1e-3;
    private int    outputEvery = 10;
    private double currentTime = 0.0;
    private int    currentStep = 0;

    public void initialize() { state.handle(this); }
    public void run()        { state.handle(this); }
    public void resume()     { state.handle(this); }
    
    public void pause() {
        if (state instanceof RunningState) {
            ((RunningState) state).requestPause();
        } else {
            throw new IllegalStateException("Can only pause");
        }
    }

    public SimulationController setModel(PhysicalModel<?> model) {
        this.model = model;
        this.state = new IdleState();
        return this;
    }

    public SimulationController setStepper(IStepperStrategy stepper) {
        this.stepper = stepper;
        return this;
    }

    public SimulationController setDomain(SimulationDomain<?> domain) {
        this.domain = domain;
        this.state  = new IdleState();
        return this;
    }

    public SimulationController addOutputHandler(OutputHandler handler) {
        outputHandlers.add(handler);
        return this;
    }

    public SimulationController setTotalTime(double totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    public SimulationController setDt(double dt) {
        this.dt = dt;
        return this;
    }

    public SimulationController setOutputEvery(int steps) {
        this.outputEvery = steps;
        return this;
    }

    public void validateConfiguration() {
        if (model   == null) throw new IllegalStateException("Model null");
        if (stepper == null) throw new IllegalStateException("Stepper null");
        if (domain  == null) throw new IllegalStateException("Domain null");
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

    public void swapStepper(IStepperStrategy stepper) {
        this.stepper = stepper;
    }
    
    public int computeTotalSteps() {
        return (int) Math.ceil(totalTime / dt);
    }

    public void advanceTime(double actualDt) {

        currentTime += actualDt;
        currentStep++;
    }

    public double getCurrentTime()      { return currentTime; }
    public int    getCurrentStep()      { return currentStep; }
    public double getTotalTime()        { return totalTime; }
    public double getDt()               { return dt; }
    public int    getOutputEvery()      { return outputEvery; }
    public PhysicalModel<?> getModel()  { return model; }
    public IStepperStrategy getStepper() { return stepper; }
    public SimulationDomain<?> getDomain() { return domain; }
    public List<OutputHandler> getOutputHandlers() {
        return Collections.unmodifiableList(outputHandlers);
    }
}