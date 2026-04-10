package org.simulation.core;

/**
 * @brief Generic interface for numerical solvers.
 * @param <T> The numeric type (Double, Float).
 * @param <M> The physical model type.
 */
public interface NumericalSolver<T extends Number, M extends PhysicalModel<T>> {
    double[] step(M model, SimulationDomain domain, double dt);
    
    String getName();
    
    void setParameter(String paramName, double value);
    
    boolean isStable(double dt, SimulationDomain domain);
}