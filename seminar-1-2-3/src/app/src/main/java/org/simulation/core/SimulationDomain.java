package org.simulation.core;

public interface SimulationDomain {
    int getSize();
    
    int getNx();
    
    int getNy();
    
    double getDx();
    
    double getDy();
    
    double getX(int i);
    
    double getY(int j);
    
    boolean isBoundary(int i, int j);
    
    void applyBoundaryConditions(double[] field);
    
    int getIndex(int i, int j);
}