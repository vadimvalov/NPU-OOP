package org.simulation.core;

public interface OutputHandler {    
    void initialize(SimulationDomain domain, PhysicalModel model);
    
    void write(double time, int step, double[] fieldValues);
    
    void write(SimulationDomain domain, double time, int step);
    
    void finalize();
    
    String getName();
    
    void setParameter(String paramName, Object value);
}
