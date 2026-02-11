package org.simulation.core;

public interface PhysicalModel {
    double[] computeRHS(SimulationDomain domain, double time);
    
    void initialize(SimulationDomain domain);
    
    String getName();
    
    double[] getFieldValues();
    
    void updateState(double[] newValues);
}