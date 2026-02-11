package org.simulation.domain;

import org.simulation.core.SimulationDomain;

public interface BoundaryCondition {
    void apply(SimulationDomain domain, double[] field);
    
    String getType();
}