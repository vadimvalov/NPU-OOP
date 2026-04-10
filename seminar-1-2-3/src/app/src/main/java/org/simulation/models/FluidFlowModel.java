package org.simulation.models;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.data.PressureField;

public class FluidFlowModel extends AbstractPhysicalModel<Double> {
    private final double viscosity;
    private final double density;

    public FluidFlowModel(double v, double d) {
        super("fluid-flow");
        this.viscosity = v;
        this.density = d;
    }

    @Override
    protected void initializeField(SimulationDomain domain) {
        this.field = new PressureField(nx, ny, 101325.0);
    }

    @Override
    public double[] computeRHS(SimulationDomain domain, double t) {
        double[] rhs = new double[nx * ny];
        
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                int k = i + j*nx;
                double u_ij = field.getDoubleValue(i, j);
                double lap = (field.getDoubleValue(i + 1, j) - 2.0 * u_ij + field.getDoubleValue(i - 1, j)) / (dx * dx)
                           + (field.getDoubleValue(i, j + 1) - 2.0 * u_ij + field.getDoubleValue(i, j - 1)) / (dy * dy);
                rhs[k] = viscosity * lap;
            }
        }
        return rhs;
    }

    @Override public String getName() { return "Fluid Flow (Viscous)"; }
}