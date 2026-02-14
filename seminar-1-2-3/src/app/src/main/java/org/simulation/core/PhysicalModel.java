package org.simulation.core;

// whole core is about polymorphism, one main variable for any implementation

// PhysicalModel model;
// model = HeatTransferModel(x);

// whole core is about abstraction, we describe what to do, but not how to do it.

public interface PhysicalModel {
    double[] computeRHS(SimulationDomain domain, double time);

    void initialize(SimulationDomain domain);
    
    String getName();
    
    double[] getFieldValues();
    
    void updateState(double[] newValues);
}