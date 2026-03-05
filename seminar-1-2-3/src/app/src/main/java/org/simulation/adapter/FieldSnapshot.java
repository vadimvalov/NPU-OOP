package org.simulation.adapter;

/**
 * NEW FORMAT — target representation.
 * This is what the modern system works with.
 * Gson will serialize this directly to JSON.
 */
public class FieldSnapshot {
    private final String modelName;
    private final int    step;
    private final double time;
    private final int    nx;
    private final int    ny;
    private final double min;
    private final double max;
    private final double mean;
    private final double[] field;

    public FieldSnapshot(String modelName,
                         int step, double time,
                         int nx, int ny,
                         double min, double max, double mean,
                         double[] field) {
        this.modelName = modelName;
        this.step      = step;
        this.time      = time;
        this.nx        = nx;
        this.ny        = ny;
        this.min       = min;
        this.max       = max;
        this.mean      = mean;
        this.field     = field;
    }

    // Getters — нужны Gson при десериализации
    public String   getModelName() { return modelName; }
    public int      getStep()      { return step; }
    public double   getTime()      { return time; }
    public int      getNx()        { return nx; }
    public int      getNy()        { return ny; }
    public double   getMin()       { return min; }
    public double   getMax()       { return max; }
    public double   getMean()      { return mean; }
    public double[] getField()     { return field; }
}