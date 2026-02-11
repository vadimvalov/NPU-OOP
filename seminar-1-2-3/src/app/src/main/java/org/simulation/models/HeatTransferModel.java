package org.simulation.models;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.data.TemperatureField;
import org.simulation.domain.Grid2D;

public class HeatTransferModel implements PhysicalModel {
    private final double thermalDiffusivity;
    private TemperatureField temperature;
    
    public HeatTransferModel(double thermalDiffusivity) {
        if (thermalDiffusivity <= 0) {
            throw new IllegalArgumentException("Thermal diffusivity must be positive");
        }
        this.thermalDiffusivity = thermalDiffusivity;
    }
    
    @Override
    public void initialize(SimulationDomain domain) {
        int nx = domain.getNx();
        int ny = domain.getNy();
        
        temperature = new TemperatureField(nx, ny, 300.0);
        
        int centerI = nx / 2;
        int centerJ = ny / 2;
        double radius = Math.min(nx, ny) / 8.0;
        
        temperature.setHotSpot(centerI, centerJ, radius, 500.0);
        
        System.out.println("Heat Transfer Model initialized:");
        System.out.println("  Thermal diffusivity: " + thermalDiffusivity + " m²/s");
        System.out.println("  Initial hot spot at (" + centerI + ", " + centerJ + ")");
        System.out.println("  " + temperature);
    }
    
    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        if (!(domain instanceof Grid2D)) {
            throw new IllegalArgumentException("HeatTransferModel requires Grid2D domain");
        }
        
        Grid2D grid = (Grid2D) domain;
        int nx = grid.getNx();
        int ny = grid.getNy();
        
        double[] currentTemp = temperature.getValuesAs1DArray();
        double[] rhs = new double[nx * ny];
        
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double laplacian = grid.computeLaplacian(currentTemp, i, j);
                
                int index = grid.getIndex(i, j);
                rhs[index] = thermalDiffusivity * laplacian;
            }
        }
        
        for (int i = 0; i < nx; i++) {
            rhs[grid.getIndex(i, 0)] = 0.0;
        }
        for (int i = 0; i < nx; i++) {
            rhs[grid.getIndex(i, ny - 1)] = 0.0;
        }
        for (int j = 0; j < ny; j++) {
            rhs[grid.getIndex(0, j)] = 0.0;
        }
        for (int j = 0; j < ny; j++) {
            rhs[grid.getIndex(nx - 1, j)] = 0.0;
        }
        
        return rhs;
    }
    
    @Override
    public double[] getFieldValues() {
        return temperature.getValuesAs1DArray();
    }
    
    @Override
    public void updateState(double[] newValues) {
        temperature.setValuesFrom1DArray(newValues);
    }
    
    @Override
    public String getName() {
        return "Heat Transfer (Diffusion)";
    }
    
    public TemperatureField getTemperatureField() {
        return temperature;
    }
    
    public double getThermalDiffusivity() {
        return thermalDiffusivity;
    }
    
    public double computeFourierNumber(double dt, double dx) {
        return thermalDiffusivity * dt / (dx * dx);
    }
}