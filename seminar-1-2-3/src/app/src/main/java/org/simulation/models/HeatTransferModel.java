package org.simulation.models;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.domain.Grid2D;

public class HeatTransferModel extends AbstractPhysicalModel {

    private final double thermalDiffusivity;  

    public HeatTransferModel(double thermalDiffusivity) {
        super("heat-transfer");
        if (thermalDiffusivity <= 0) {
            throw new IllegalArgumentException("Thermal diffusivity must be positive");
        }
        this.thermalDiffusivity = thermalDiffusivity;
    }

    @Override
    protected void initializeField(SimulationDomain domain) {
        for (int k = 0; k < field.length; k++) field[k] = 300.0;

        int    ci     = nx / 2;
        int    cj     = ny / 2;
        double radius = Math.min(nx, ny) / 8.0;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                double di   = i - ci;
                double dj   = j - cj;
                double dist = Math.sqrt(di * di + dj * dj);
                if (dist <= radius) {
                    double factor = Math.exp(-(dist * dist) / (2 * radius * radius));
                    field[j * nx + i] = 300.0 + 200.0 * factor;
                }
            }
        }
    }

    @Override
    public void applyBoundaryCondition(SimulationDomain domain) {
        for (int i = 0; i < nx; i++) {
            field[i]               = 0.0;   
            field[(ny-1)*nx + i]   = 0.0;   
        }
        for (int j = 0; j < ny; j++) {
            field[j * nx]          = 0.0;   
            field[j * nx + (nx-1)] = 0.0;   
        }
    }

    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        if (!(domain instanceof Grid2D)) {
            throw new IllegalArgumentException("HeatTransferModel requires Grid2D");
        }
        Grid2D grid = (Grid2D) domain;
        double[] rhs = new double[nx * ny];

        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                int k   = grid.getIndex(i, j);
                rhs[k]  = thermalDiffusivity * grid.computeLaplacian(field, i, j);
            }
        }
        return rhs;
    }

    @Override
    public String getName() { return "Heat Transfer (Diffusion)"; }

    public double getThermalDiffusivity() { return thermalDiffusivity; }

    public double computeFourierNumber(double dt, double dxVal) {
        return thermalDiffusivity * dt / (dxVal * dxVal);
    }
}