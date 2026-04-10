package org.simulation.adapter;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;

public class LegacyHeatModelAdapter extends AbstractPhysicalModel<Double> {
    public LegacyHeatModelAdapter(double diff, double dt) { super("legacy"); }
    @Override protected void initializeField(SimulationDomain domain) {}
    @Override public double[] computeRHS(SimulationDomain domain, double t) { return new double[nx*ny]; }
    @Override public String getName() { return "Legacy Heat Adapter"; }
}