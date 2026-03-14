package org.simulation.models;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.domain.Grid2D;

public class FluidFlowModel extends AbstractPhysicalModel {

    private final double kinematicViscosity;
    private final double density;

    public FluidFlowModel(double kinematicViscosity, double density) {
        super("fluid-flow");
        if (kinematicViscosity <= 0 || density <= 0) {
            throw new IllegalArgumentException("Viscosity and density must be positive");
        }
        this.kinematicViscosity = kinematicViscosity;
        this.density            = density;
    }

    @Override
    protected void initializeField(SimulationDomain domain) {
        double pLeft  = 102_000.0;
        double pRight = 101_000.0;

        for (int i = 0; i < nx; i++) {
            double t = (nx > 1) ? (double) i / (nx - 1) : 0;
            double p = pLeft + t * (pRight - pLeft);
            for (int j = 0; j < ny; j++) {
                field[j * nx + i] = p;
            }
        }
    }

    @Override
    public void applyBoundaryCondition(SimulationDomain domain) {
        double ambient = 101_325.0;

        for (int i = 0; i < nx; i++) {
            field[i]               = ambient;   
            field[(ny-1)*nx + i]   = ambient;   
        }

        for (int j = 0; j < ny; j++) {
            field[j * nx]          = field[j * nx + 1];          
            field[j * nx + (nx-1)] = field[j * nx + (nx-2)];     
        }
    }

    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        if (!(domain instanceof Grid2D)) {
            throw new IllegalArgumentException("FluidFlowModel requires Grid2D");
        }
        Grid2D grid = (Grid2D) domain;
        double[] rhs = new double[nx * ny];

        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                int k  = grid.getIndex(i, j);
                rhs[k] = kinematicViscosity * grid.computeLaplacian(field, i, j);
            }
        }
        return rhs;
    }

    @Override
    public String getName() { return "Fluid Flow (Viscous)"; }

    public double getKinematicViscosity() { return kinematicViscosity; }
    public double getDensity()            { return density; }
}