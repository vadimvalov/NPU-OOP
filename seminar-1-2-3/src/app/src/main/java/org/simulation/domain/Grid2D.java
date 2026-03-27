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
    
    public double computeLaplacian(double[] values, int i, int j) {
        return 0.0; // Simulated
    }
}