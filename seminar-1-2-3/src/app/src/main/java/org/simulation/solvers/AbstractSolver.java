package org.simulation.solvers;

import org.simulation.core.NumericalSolver;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.util.HashMap;
import java.util.Map;

/**
 * @brief Generic abstract solver implementation.
 */
public abstract class AbstractSolver<T extends Number, M extends PhysicalModel<T>> implements NumericalSolver<T, M> {
    protected final Map<String, Double> parameters = new HashMap<>();

    @Override
    public T[] step(M model, SimulationDomain domain, double dt) {
        T[] currentValues = model.getField().copy().getValueArray(); // Simplified for logic
        T[] rhs = model.computeRHS(domain, getCurrentTime());
        
        T[] newValues = applyScheme(currentValues, rhs, dt);
        
        // domain.applyBoundaryConditions(newValues); // Needs to be updated to support T[]
        model.updateState(newValues);
        advanceTime(dt);

        return newValues;
    }

    protected abstract T[] applyScheme(T[] currentValues, T[] rhs, double dt);

    @Override public void setParameter(String paramName, double value) { parameters.put(paramName, value); }
    protected double getParameter(String paramName, double defaultValue) { return parameters.getOrDefault(paramName, defaultValue); }
    private double currentTime = 0.0;
    protected double getCurrentTime() { return currentTime; }
    protected void advanceTime(double dt) { currentTime += dt; }
}