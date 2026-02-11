package org.simulation.domain;

import org.simulation.core.SimulationDomain;

public class DirichletBoundaryCondition implements BoundaryCondition {
    private final double value;
    
    public DirichletBoundaryCondition(double value) {
        this.value = value;
    }
    
    @Override
    public void apply(SimulationDomain domain, double[] field) {
        int nx = domain.getNx();
        int ny = domain.getNy();
        
        for (int i = 0; i < nx; i++) {
            field[domain.getIndex(i, 0)] = value;
        }
        
        for (int i = 0; i < nx; i++) {
            field[domain.getIndex(i, ny - 1)] = value;
        }
        
        for (int j = 0; j < ny; j++) {
            field[domain.getIndex(0, j)] = value;
        }
        
        for (int j = 0; j < ny; j++) {
            field[domain.getIndex(nx - 1, j)] = value;
        }
    }
    
    @Override
    public String getType() {
        return "Dirichlet (u = " + value + ")";
    }
    
    public double getValue() {
        return value;
    }
}