package org.simulation.domain;

import org.simulation.core.SimulationDomain;
import org.simulation.data.Field;

public interface BoundaryCondition {
    /** Apply BC to a generic Field. */
    void apply(SimulationDomain<?> domain, Field<Double> field);
    
    String getType();
}