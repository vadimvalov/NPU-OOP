package org.simulation.models;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;

public class ReactiveTransportModel extends AbstractPhysicalModel<Double> {
    public ReactiveTransportModel() { super("react"); }
    @Override protected void initializeField(SimulationDomain domain) {}
    @Override public Double[] computeRHS(SimulationDomain domain, double t) { return new Double[nx*ny]; }
    @Override public String getName() { return "Reactive Transport"; }
}
