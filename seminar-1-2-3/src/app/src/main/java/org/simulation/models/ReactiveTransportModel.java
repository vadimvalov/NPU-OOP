package org.simulation.models;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.data.ConcentrationField;
import org.simulation.domain.Grid2D;

public class ReactiveTransportModel implements PhysicalModel {
    private final double diffusionCoefficient;
    private final double reactionRate;
    private ConcentrationField concentration;
    
    public ReactiveTransportModel(double diffusionCoefficient, double reactionRate) {
        if (diffusionCoefficient < 0 || reactionRate < 0) {
            throw new IllegalArgumentException("Coefficients must be non-negative");
        }
        this.diffusionCoefficient = diffusionCoefficient;
        this.reactionRate = reactionRate;
    }
    
    @Override
    public void initialize(SimulationDomain domain) {
        int nx = domain.getNx();
        int ny = domain.getNy();
        
        concentration = new ConcentrationField(nx, ny, 0.0);
        
        int sourceI = nx / 2;
        int sourceJ = ny / 2;
        double sourceRadius = Math.min(nx, ny) / 10.0;
        
        concentration.setSource(sourceI, sourceJ, sourceRadius, 1.0);
        
        System.out.println("Reactive Transport Model initialized:");
        System.out.println("  Diffusion coefficient: " + diffusionCoefficient + " m²/s");
        System.out.println("  Reaction rate: " + reactionRate + " 1/s");
        System.out.println("  Damköhler number: " + computeDamkohlerNumber(domain));
        System.out.println("  " + concentration);
    }
    
    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        if (!(domain instanceof Grid2D)) {
            throw new IllegalArgumentException("ReactiveTransportModel requires Grid2D domain");
        }
        
        Grid2D grid = (Grid2D) domain;
        int nx = grid.getNx();
        int ny = grid.getNy();
        
        double[] currentConc = concentration.getValuesAs1DArray();
        double[] rhs = new double[nx * ny];
        
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                int index = grid.getIndex(i, j);
                
                double laplacian = grid.computeLaplacian(currentConc, i, j);
                double diffusionTerm = diffusionCoefficient * laplacian;
                
                double reactionTerm = -reactionRate * currentConc[index];
                
                rhs[index] = diffusionTerm + reactionTerm;
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
        return concentration.getValuesAs1DArray();
    }
    
    @Override
    public void updateState(double[] newValues) {
        concentration.setValuesFrom1DArray(newValues);
        
        if (!concentration.isPhysicallyValid()) {
            System.err.println("WARNING: Negative concentrations detected!");
        }
    }
    
    @Override
    public String getName() {
        return "Reactive Transport";
    }
    
    public ConcentrationField getConcentrationField() {
        return concentration;
    }
    
    private double computeDamkohlerNumber(SimulationDomain domain) {
        double L = domain.getNx() * domain.getDx();
        if (diffusionCoefficient == 0) return Double.POSITIVE_INFINITY;
        return reactionRate * L * L / diffusionCoefficient;
    }
    
    public double getTotalMass(Grid2D grid) {
        double cellVolume = grid.getCellVolume();
        return concentration.getTotalMass(cellVolume);
    }
    
    public double getDiffusionCoefficient() {
        return diffusionCoefficient;
    }
    
    public double getReactionRate() {
        return reactionRate;
    }
}
