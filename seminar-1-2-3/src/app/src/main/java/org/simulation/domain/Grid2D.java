package org.simulation.domain;

import org.simulation.core.SimulationDomain;
import org.simulation.data.Field;
import java.util.ArrayList;
import java.util.List;

public class Grid2D implements SimulationDomain<SimulationDomain.Dim2D> {
    private final int nx, ny;
    private final double dx, dy;
    private final List<BoundaryCondition> bcs = new ArrayList<>();

    public Grid2D(int nx, int ny, double lx, double ly) {
        this.nx = nx;
        this.ny = ny;
        this.dx = lx / (nx - 1);
        this.dy = ly / (ny - 1);
        bcs.add(new DirichletBoundaryCondition(0.0));
    }

    @Override public int getNx() { return nx; }
    @Override public int getNy() { return ny; }
    @Override public double getDx() { return dx; }
    @Override public double getDy() { return dy; }

    @Override
    public void applyBoundaryConditions(Field<Double> field) {
        for (BoundaryCondition bc : bcs) {
            bc.apply(this, field);
        }
    }

    public int getIndex(int i, int j) { return j * nx + i; }
    
    // Demonstrate 2D logic: 5-point stencil
    public double computeLaplacian(Field<Double> field, int i, int j) {
        double u_ij = field.getValue(i, j);
        return (field.getValue(i+1, j) + field.getValue(i-1, j) + 
                field.getValue(i, j+1) + field.getValue(i, j-1) - 4.0 * u_ij) / (dx * dx);
    }
}

/**
 * Demonstrate 1D Grid with specialized 3-point stencil logic.
 */
class Grid1D implements SimulationDomain<SimulationDomain.Dim1D> {
    private final int nx;
    private final double dx;

    public Grid1D(int nx, double lx) {
        this.nx = nx;
        this.dx = lx / (nx - 1);
    }

    @Override public int getNx() { return nx; }
    @Override public int getNy() { return 1; }
    @Override public double getDx() { return dx; }
    @Override public double getDy() { return 0.0; }
    @Override public void applyBoundaryConditions(Field<Double> field) {}

    // Demonstrate 1D logic: 3-point stencil
    public double computeLaplacian(Field<Double> field, int i) {
        return (field.getValue(i+1, 0) + field.getValue(i-1, 0) - 2.0 * field.getValue(i, 0)) / (dx * dx);
    }
}