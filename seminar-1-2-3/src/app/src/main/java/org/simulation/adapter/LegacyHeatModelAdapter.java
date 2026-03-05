package org.simulation.adapter;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;
import org.simulation.legacy.LegacyHeatModel;

/**
 * ADAPTER PATTERN
 *
 * Incompatible (legacy) side:
 *   LegacyHeatModel.legacyHeatStep(double[] T, int nx, int ny, ...)
 *   — raw arrays, no Field, no State, no interface
 *
 * Target interface (framework side):
 *   PhysicalModel — initialize(), computeRHS(), getFieldValues(), updateState()
 *
 * The adapter translates between the two without touching:
 *   - SimulationController
 *   - PhysicalModel interface
 *   - LegacyHeatModel
 *
 * Key challenge: legacy model does a FULL step at once,
 * but framework splits it into computeRHS() + updateState().
 * Solution: computeRHS() does the legacy step and stores result,
 *           return fake RHS = (Tnext - T) / dt so solver math still works.
 */
public class LegacyHeatModelAdapter implements PhysicalModel {

    private final double alpha;

    private double[] T;      // current state as raw array
    private double[] Tnext;  // result of legacy step, stored until updateState()

    private int    nx, ny;
    private double dx, dy;

    // dt is not in PhysicalModel interface — caller sets it before run()
    private double lastDt = 1e-3;

    public LegacyHeatModelAdapter(double alpha, double dt) {
        this.alpha  = alpha;
        this.lastDt = dt;
    }

    @Override
    public void initialize(SimulationDomain domain) {
        this.nx = domain.getNx();
        this.ny = domain.getNy();
        this.dx = domain.getDx();
        this.dy = domain.getDy();

        T     = new double[nx * ny];
        Tnext = new double[nx * ny];

        // Same initial condition as HeatTransferModel — hot spot in center
        int    centerI = nx / 2;
        int    centerJ = ny / 2;
        double radius  = Math.min(nx, ny) / 8.0;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                double di   = i - centerI;
                double dj   = j - centerJ;
                double dist = Math.sqrt(di * di + dj * dj);

                if (dist <= radius) {
                    double factor = Math.exp(-(dist * dist) / (2 * radius * radius));
                    T[j * nx + i] = 500.0 * factor;
                } else {
                    T[j * nx + i] = 300.0;
                }
            }
        }

        // Dirichlet boundary — same as framework
        for (int i = 0; i < nx; i++) {
            T[i]                = 0.0;
            T[(ny-1) * nx + i]  = 0.0;
        }
        for (int j = 0; j < ny; j++) {
            T[j * nx]           = 0.0;
            T[j * nx + (nx-1)]  = 0.0;
        }

        Tnext = T.clone();

        System.out.println("LegacyHeatModelAdapter initialized:");
        System.out.println("  Thermal diffusivity: " + alpha + " m²/s");
        System.out.println("  Grid: " + nx + "x" + ny);
    }

    /**
     * ADAPTATION — core of the pattern.
     *
     * Framework expects RHS so solver does: u_new = u + dt * rhs
     * Legacy model does:                    Tnew  = legacyHeatStep(T, ..., dt)
     *
     * We call legacyHeatStep here (dt is known), then return:
     *   rhs = (Tnext - T) / dt
     * So solver computes:
     *   u_new = T + dt * (Tnext - T) / dt = Tnext  ✓
     */
    @Override
    public double[] computeRHS(SimulationDomain domain, double time) {
        Tnext = LegacyHeatModel.legacyHeatStep(T, nx, ny, dx, dy, lastDt, alpha);

        double[] rhs = new double[nx * ny];
        for (int k = 0; k < rhs.length; k++) {
            rhs[k] = (Tnext[k] - T[k]) / lastDt;
        }
        return rhs;
    }

    /**
     * Framework calls updateState(T + dt * rhs) which equals Tnext.
     * We just store it as current state.
     */
    @Override
    public void updateState(double[] newValues) {
        T = newValues.clone();
    }

    @Override
    public double[] getFieldValues() {
        return T.clone();
    }

    @Override
    public String getName() {
        return "Legacy Heat Model (Adapter)";
    }
}