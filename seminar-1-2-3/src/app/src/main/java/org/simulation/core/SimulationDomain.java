package org.simulation.core;

import org.simulation.data.Field;

public interface SimulationDomain<D extends SimulationDomain.Dimension> {
    
    interface Dimension {}
    interface Dim1D extends Dimension {}
    interface Dim2D extends Dimension {}
    interface Dim3D extends Dimension {}

    int getNx();
    int getNy();
    double getDx();
    double getDy();
    
    void applyBoundaryConditions(Field<Double> field);
    
    // Bridge for Neumann
    default int getIndex(int i, int j) {
        return j * getNx() + i;
    }
}