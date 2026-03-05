package org.simulation.legacy;

/**
 * LEGACY component — simulates an old external library.
 *
 * Incompatible with the framework:
 *   - works with raw double[] instead of Field/State
 *   - no initialize(), no getName(), no updateState()
 *   - just one static function, like a C-style API
 *
 * We CANNOT modify this class (it's "external").
 */
public class LegacyHeatModel {
    public static double[] legacyHeatStep(double[] T,
                                          int nx, int ny,
                                          double dx, double dy,
                                          double dt, double alpha) {
        double[] Tnew = new double[nx * ny];

        for (int j = 1; j < ny - 1; j++) {
            for (int i = 1; i < nx - 1; i++) {
                int idx  = j * nx + i;
                int ip   = j * nx + (i + 1);
                int im   = j * nx + (i - 1);
                int jp   = (j + 1) * nx + i;
                int jm   = (j - 1) * nx + i;

                double d2Tdx2 = (T[ip] - 2 * T[idx] + T[im]) / (dx * dx);
                double d2Tdy2 = (T[jp] - 2 * T[idx] + T[jm]) / (dy * dy);

                Tnew[idx] = T[idx] + dt * alpha * (d2Tdx2 + d2Tdy2);
            }
        }

        for (int i = 0; i < nx; i++) {
            Tnew[i]              = 0.0; // j=0
            Tnew[(ny-1)*nx + i]  = 0.0; // j=ny-1
        }
        for (int j = 0; j < ny; j++) {
            Tnew[j * nx]          = 0.0; // i=0
            Tnew[j * nx + (nx-1)] = 0.0; // i=nx-1
        }

        return Tnew;
    }
}