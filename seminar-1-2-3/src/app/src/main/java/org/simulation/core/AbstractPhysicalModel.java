package org.simulation.core;


public abstract class AbstractPhysicalModel extends SimulationComponent implements PhysicalModel {

    protected double[] field;
    protected int      nx, ny;
    protected double   dx, dy;

    protected AbstractPhysicalModel(String componentId) {
        super(componentId);
    }


    @Override
    public void initialize(SimulationDomain domain) {
        onInitialize();                        

        this.nx = domain.getNx();
        this.ny = domain.getNy();
        this.dx = domain.getDx();
        this.dy = domain.getDy();

        this.field = new double[nx * ny];

        initializeField(domain);              

        applyBoundaryCondition(domain);       
    }

    protected abstract void initializeField(SimulationDomain domain);

    public void applyBoundaryCondition(SimulationDomain domain) {
        domain.applyBoundaryConditions(field);
    }

    public double[] computeStep(SimulationDomain domain, double time, double dt) {
        requireInitialized();
        double[] rhs  = computeRHS(domain, time);
        double[] next = new double[field.length];
        for (int k = 0; k < field.length; k++) {
            next[k] = field[k] + dt * rhs[k];
        }
        applyBoundaryCondition(domain);
        updateState(next);
        return next;
    }

    @Override
    public double[] getFieldValues() {
        requireInitialized();
        return field.clone();
    }

    @Override
    public void updateState(double[] newValues) {
        requireInitialized();
        if (newValues.length != field.length) {
            throw new IllegalArgumentException(
                "updateState: size mismatch — expected " + field.length
                + ", got " + newValues.length);
        }
        System.arraycopy(newValues, 0, field, 0, field.length);
    }

    public double getMin() {
        double m = Double.MAX_VALUE;
        for (double v : field) if (v < m) m = v;
        return m;
    }

    public double getMax() {
        double m = -Double.MAX_VALUE;
        for (double v : field) if (v > m) m = v;
        return m;
    }

    public double getMean() {
        double s = 0;
        for (double v : field) s += v;
        return s / field.length;
    }

    @Override
    public String describe() {
        if (!isInitialized()) return "(not yet initialized)";
        return String.format("grid=%dx%d  dx=%.4f  dy=%.4f  min=%.3f  max=%.3f  mean=%.3f",
            nx, ny, dx, dy, getMin(), getMax(), getMean());
    }
}