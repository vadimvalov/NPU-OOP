package org.simulation.domain;

import org.simulation.core.SimulationDomain;
import org.simulation.data.Field;

public class DirichletBoundaryCondition implements BoundaryCondition {
    private final double value;
    
    public DirichletBoundaryCondition(double value) {
        this.value = value;
    }
    
    @Override
    public void apply(SimulationDomain<?> domain, Field<Double> field) {
        int nx = domain.getNx();
        int ny = domain.getNy();
        
        for (int i = 0; i < nx; i++) {
            field.setValue(i, 0, value);
        }
        for (int i = 0; i < nx; i++) {
            field.setValue(i, ny - 1, value);
        }
        for (int j = 0; j < ny; j++) {
            field.setValue(0, j, value);
        }
        for (int j = 0; j < ny; j++) {
            field.setValue(nx - 1, j, value);
        }
    }
    
    @Override public String getType() { return "Dirichlet (u = " + value + ")"; }
}