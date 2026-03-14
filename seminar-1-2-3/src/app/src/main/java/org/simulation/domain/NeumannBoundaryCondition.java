package org.simulation.domain;

import org.simulation.core.SimulationDomain;

public class NeumannBoundaryCondition implements BoundaryCondition {

    private final double flux;  
   
    public NeumannBoundaryCondition(double flux) {
        this.flux = flux;
    }

    public NeumannBoundaryCondition() {
        this(0.0);
    }

    @Override
    public void apply(SimulationDomain domain, double[] field) {
        int    nx = domain.getNx();
        int    ny = domain.getNy();
        double dx = domain.getDx();
        double dy = domain.getDy();

        for (int i = 0; i < nx; i++) {
            int bnd = domain.getIndex(i, 0);
            int inn = domain.getIndex(i, 1);
            field[bnd] = field[inn] - flux * dy;
        }

        for (int i = 0; i < nx; i++) {
            int bnd = domain.getIndex(i, ny - 1);
            int inn = domain.getIndex(i, ny - 2);
            field[bnd] = field[inn] + flux * dy;
        }

        for (int j = 0; j < ny; j++) {
            int bnd = domain.getIndex(0, j);
            int inn = domain.getIndex(1, j);
            field[bnd] = field[inn] - flux * dx;
        }

        for (int j = 0; j < ny; j++) {
            int bnd = domain.getIndex(nx - 1, j);
            int inn = domain.getIndex(nx - 2, j);
            field[bnd] = field[inn] + flux * dx;
        }
    }

    @Override
    public String getType() {
        return flux == 0.0
            ? "Neumann (du/dn = 0 — insulating)"
            : String.format("Neumann (du/dn = %.4f)", flux);
    }

    public double getFlux() { return flux; }
}