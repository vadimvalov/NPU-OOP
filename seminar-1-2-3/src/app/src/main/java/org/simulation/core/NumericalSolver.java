package org.simulation.core;

public interface NumericalSolver {
    double[] step(PhysicalModel model, SimulationDomain domain, double dt);
    
    String getName();
    
    void setParameter(String paramName, double value);
    
    boolean isStable(double dt, SimulationDomain domain);
}