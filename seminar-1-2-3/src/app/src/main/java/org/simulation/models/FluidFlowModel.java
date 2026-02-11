package org.simulation.models;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.data.PressureField;
import org.simulation.domain.Grid2D;

public class FluidFlowModel implements PhysicalModel {
    private final double kinematicViscosity;
    private final double density;
    private PressureField pressure;
    
    public FluidFlowModel(double kinematicViscosity, double density) {
        if (kinematicViscosity <= 0 || density <= 0) {
            throw new IllegalArgumentException("Viscosity and density must be positive");
        }
        this.kinematicViscosity = kinematicViscosity;
        this.density = density;
    }
    
    @Override
    public void initialize(SimulationDomain domain) {
        int nx = domain.getNx();
        int ny = domain.getNy();
        
        pressure = new PressureField(nx, ny, 101325.0);
        
        pressure.setLinearGradient(102000.0, 101000.0);
        
        System.out.println("Fluid Flow Model initialized:");
        System.out.println("  Kinematic viscosity: " + kinematicViscosity + " m²/s");
        System.out.println("  Density: " + density + " kg/m³");
        System.out.println("  Reynolds number (approx): " + computeReynoldsNumber(domain));
        System.out.println("  " + pressure);
    }
    
    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        if (!(domain instanceof Grid2D)) {
            throw new IllegalArgumentException("FluidFlowModel requires Grid2D domain");
        }
        
        Grid2D grid = (Grid2D) domain;
        int nx = grid.getNx();
        int ny = grid.getNy();
        
        double[] currentPressure = pressure.getValuesAs1DArray();
        double[] rhs = new double[nx * ny];
        
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double laplacian = grid.computeLaplacian(currentPressure, i, j);
                
                int index = grid.getIndex(i, j);
                rhs[index] = kinematicViscosity * laplacian;
            }
        }
        
        for (int i = 0; i < nx; i++) {
            rhs[grid.getIndex(i, 0)] = 0.0;
            rhs[grid.getIndex(i, ny - 1)] = 0.0;
        }
        
        for (int j = 0; j < ny; j++) {
            rhs[grid.getIndex(0, j)] = 0.0;
            rhs[grid.getIndex(nx - 1, j)] = 0.0;
        }
        
        return rhs;
    }
    
    @Override
    public double[] getFieldValues() {
        return pressure.getValuesAs1DArray();
    }
    
    @Override
    public void updateState(double[] newValues) {
        pressure.setValuesFrom1DArray(newValues);
    }
    
    @Override
    public String getName() {
        return "Fluid Flow (Viscous)";
    }
    
    public PressureField getPressureField() {
        return pressure;
    }
    
    private double computeReynoldsNumber(SimulationDomain domain) {
        double U = 1.0;
        double L = domain.getNx() * domain.getDx();
        return U * L / kinematicViscosity;
    }
    
    public double getKinematicViscosity() {
        return kinematicViscosity;
    }
    
    public double getDensity() {
        return density;
    }
}