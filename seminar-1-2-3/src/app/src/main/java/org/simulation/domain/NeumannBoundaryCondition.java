package org.simulation.domain;

import org.simulation.core.SimulationDomain;
import org.simulation.data.Field;

public class NeumannBoundaryCondition implements BoundaryCondition {
    private final double flux;

    public NeumannBoundaryCondition(double flux) { this.flux = flux; }

    @Override
    public void apply(SimulationDomain<?> domain, Field<Double> field) {
        int nx = domain.getNx();
        int ny = domain.getNy();
        double dx = domain.getDx();
        double dy = domain.getDy();

        // Simple Neumann implementation
        for (int i = 0; i < nx; i++) {
            double value = field.getValue(i, 1) + flux * dy;
            field.setValue(i, 0, value);
        }
    }

    @Override public String getType() { return "Neumann (flux = " + flux + ")"; }
}