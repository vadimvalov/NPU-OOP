package org.simulation.models;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;

public class WavePropagationModel extends AbstractPhysicalModel<Double> {
    public WavePropagationModel(double c) { super("wave"); }
    @Override protected void initializeField(SimulationDomain domain) {}
    @Override public double[] computeRHS(SimulationDomain domain, double t) { return new double[nx*ny]; }
    @Override public String getName() { return "Wave Propagation"; }
}