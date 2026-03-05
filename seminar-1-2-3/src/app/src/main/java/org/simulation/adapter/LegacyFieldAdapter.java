package org.simulation.adapter;

/**
 * ADAPTER PATTERN
 *
 * Legacy interface  →  double[] fieldValues  (what we have NOW)
 * Target interface  →  FieldSnapshot         (what new system expects)
 *
 * The adapter sits between the old OutputHandler contract
 * and the new JSON-based world — neither side needs to change.
 */
public class LegacyFieldAdapter {

    private final String modelName;
    private final int    nx;
    private final int    ny;

    /**
     * @param modelName  model.getName()   — metadata missing in raw double[]
     * @param nx         domain.getNx()    — metadata missing in raw double[]
     * @param ny         domain.getNy()    — metadata missing in raw double[]
     */
    public LegacyFieldAdapter(String modelName, int nx, int ny) {
        this.modelName = modelName;
        this.nx        = nx;
        this.ny        = ny;
    }

    /**
     * Converts legacy double[] + step/time into a FieldSnapshot.
     *
     * This is the core adaptation:
     *   double[], int, double  →  FieldSnapshot
     *
     * The caller (JSONOutputHandler) doesn't know or care about
     * how the array is structured internally.
     */
    public FieldSnapshot adapt(double[] legacyField, int step, double time) {
        if (legacyField == null || legacyField.length == 0) {
            throw new IllegalArgumentException("Legacy field must not be null or empty");
        }
        if (legacyField.length != nx * ny) {
            throw new IllegalArgumentException(
                String.format("Field length %d does not match nx*ny = %d*%d = %d",
                    legacyField.length, nx, ny, nx * ny)
            );
        }

        double min  =  Double.MAX_VALUE;
        double max  = -Double.MAX_VALUE;
        double sum  = 0.0;

        for (double v : legacyField) {
            if (v < min) min = v;
            if (v > max) max = v;
            sum += v;
        }

        double mean = sum / legacyField.length;

        // Defensive copy so caller's array and snapshot are independent
        double[] fieldCopy = legacyField.clone();

        return new FieldSnapshot(modelName, step, time, nx, ny, min, max, mean, fieldCopy);
    }
}