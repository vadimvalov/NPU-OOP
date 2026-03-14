package org.simulation.models;

import org.simulation.core.AbstractPhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.domain.Grid2D;
import org.simulation.domain.NeumannBoundaryCondition;

public class WavePropagationModel extends AbstractPhysicalModel {

    private final double waveSpeed;      
    private int          N;              
    private SimulationDomain lastDomain; 

    public WavePropagationModel(double waveSpeed) {
        super("wave-propagation");
        if (waveSpeed <= 0) throw new IllegalArgumentException("Wave speed must be positive");
        this.waveSpeed = waveSpeed;
    }

    @Override
    protected void initializeField(SimulationDomain domain) {
        this.lastDomain = domain;
        N = nx * ny;
        field = new double[2 * N];   

        int    ci     = nx / 2;
        int    cj     = ny / 2;
        double radius = Math.min(nx, ny) / 6.0;

        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                double di   = i - ci;
                double dj   = j - cj;
                double dist = Math.sqrt(di * di + dj * dj);
                int    idx  = j * nx + i;

                field[idx] = Math.exp(-(dist * dist) / (2.0 * radius * radius));
                field[N + idx] = 0.0;
            }
        }

        System.out.println("WavePropagationModel: Gaussian pulse initialised");
        System.out.printf("  Wave speed c = %.1f m/s%n", waveSpeed);
        System.out.printf("  Grid %dx%d, field size %d (u+v)%n", nx, ny, field.length);
    }

    @Override
    public void applyBoundaryCondition(SimulationDomain domain) {
        double[] u = new double[N];
        System.arraycopy(field, 0, u, 0, N);

        new NeumannBoundaryCondition(0.0).apply(domain, u);

        System.arraycopy(u, 0, field, 0, N);

    }

    /**
     * computeRHS() returns the time derivatives of [u | v]:
     *   rhs[k]   = v[k]            for k in 0..N-1  (du/dt = v)
     *   rhs[N+k] = c² * Lap(u)[k]  for k in 0..N-1  (dv/dt = c²∇²u)
     */
    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        if (!(domain instanceof Grid2D)) {
            throw new IllegalArgumentException("WavePropagationModel requires Grid2D");
        }
        Grid2D grid = (Grid2D) domain;

        double[] u   = new double[N];
        double[] v   = new double[N];
        System.arraycopy(field, 0, u, 0, N);
        System.arraycopy(field, N, v, 0, N);

        double[] rhs = new double[2 * N];
        double   c2  = waveSpeed * waveSpeed;

        for (int j = 1; j < ny - 1; j++) {
            for (int i = 1; i < nx - 1; i++) {
                int k = grid.getIndex(i, j);

                rhs[k] = v[k];

                rhs[N + k] = c2 * grid.computeLaplacian(u, i, j);
            }
        }

        return rhs;
    }

    @Override
    public double[] getFieldValues() {
        requireInitialized();
        double[] u = new double[N];
        System.arraycopy(field, 0, u, 0, N);
        return u;
    }

    @Override
    public void updateState(double[] newValues) {
        requireInitialized();
        if (newValues.length == 2 * N) {
            System.arraycopy(newValues, 0, field, 0, 2 * N);
        } else if (newValues.length == N) {
            System.arraycopy(newValues, 0, field, 0, N);
        } else {
            throw new IllegalArgumentException(
                "updateState: expected " + N + " or " + (2 * N) + " values, got " + newValues.length);
        }
        if (lastDomain != null) applyBoundaryCondition(lastDomain);
    }

    @Override
    public String getName() { return "Wave Propagation (Acoustic)"; }

    public double getWaveSpeed() { return waveSpeed; }

    public double getCourantNumber(double dt) {
        return waveSpeed * dt / Math.min(dx, dy);
    }
}