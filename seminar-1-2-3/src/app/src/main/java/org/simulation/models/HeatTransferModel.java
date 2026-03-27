package org.simulation.models;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.data.TemperatureField;

public class HeatTransferModel extends AbstractPhysicalModel<Double> {
    private final double thermalDiffusivity;

    public HeatTransferModel(double thermalDiffusivity) {
        super("heat-transfer");
        this.thermalDiffusivity = thermalDiffusivity;
    }

    @Override
    protected void initializeField(SimulationDomain domain) {
        this.field = new TemperatureField(nx, ny, 300.0);
    }

    @Override
    public Double[] computeRHS(SimulationDomain domain, double time) {
        Double[] rhs = new Double[nx * ny];
        for (int i = 0; i < rhs.length; i++) rhs[i] = 0.0; // Avoid Nulls
        
        // Simple 2D Heat Equation RHS (simplified)
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                int k = i + j*nx;
                double u_ij = field.getValue(i, j);
                double lap = (field.getValue(i + 1, j) - 2.0 * u_ij + field.getValue(i - 1, j)) / (dx * dx)
                           + (field.getValue(i, j + 1) - 2.0 * u_ij + field.getValue(i, j - 1)) / (dy * dy);
                rhs[k] = thermalDiffusivity * lap;
            }
        }
        return rhs;
    }

    @Override public String getName() { return "Heat Transfer (Diffusion)"; }
}