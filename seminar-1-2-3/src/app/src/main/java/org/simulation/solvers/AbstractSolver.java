package org.simulation.solvers;

import org.simulation.core.NumericalSolver;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSolver implements NumericalSolver {
    protected final Map<String, Double> parameters = new HashMap<>();

    @Override
    public double[] step(PhysicalModel model, SimulationDomain domain, double dt) {
        validateInputs(model, domain, dt);
        
        double[] currentValues = model.getFieldValues();
        double[] rhs = model.computeRHS(domain, getCurrentTime());
        
        double[] newValues = applyScheme(currentValues, rhs, dt);

        domain.applyBoundaryConditions(newValues);
        model.updateState(newValues);

        advanceTime(dt);

        return newValues;
    }

    protected abstract double[] applyScheme(double[] currentValues, double[] rhs, double dt);

    @Override
    public void setParameter(String paramName, double value) {
        parameters.put(paramName, value);
    }

    protected double getParameter(String paramName, double defaultValue) {
        return parameters.getOrDefault(paramName, defaultValue);
    }

    private double currentTime = 0.0;

    protected double getCurrentTime() {
        return currentTime;
    }

    protected void advanceTime(double dt) {
        currentTime += dt;
    }

    public void resetTime() {
        currentTime = 0.0;
    }

    private void validateInputs(PhysicalModel model, SimulationDomain domain, double dt) {
        if (model == null) {
            throw new IllegalArgumentException("PhysicalModel must not be null");
        }
        if (domain == null) {
            throw new IllegalArgumentException("SimulationDomain must not be null");
        }
        if (dt <= 0) {
            throw new IllegalArgumentException("Time step dt must be positive, got: " + dt);
        }
        if (model.getFieldValues() == null) {
            throw new IllegalStateException("Model is not initialized: getFieldValues() returned null");
        }
        if (model.getFieldValues().length != domain.getSize()) {
            throw new IllegalStateException(String.format(
                "Field size %d does not match domain size %d",
                model.getFieldValues().length, domain.getSize()
            ));
        }
    }
}