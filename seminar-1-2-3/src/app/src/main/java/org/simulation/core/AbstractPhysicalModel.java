package org.simulation.core;

import org.simulation.data.Field;

public abstract class AbstractPhysicalModel<T extends Number> extends SimulationComponent implements PhysicalModel<T> {

    protected Field<T> field;
    protected int      nx, ny;
    protected double   dx, dy;

    protected AbstractPhysicalModel(String componentId) {
        super(componentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(SimulationDomain domain) {
        this.nx = domain.getNx();
        this.ny = domain.getNy();
        this.dx = domain.getDx();
        this.dy = domain.getDy();
        initializeField(domain);              
        applyBoundaryCondition(domain);       
        onInitialize();
    }

    protected abstract void initializeField(SimulationDomain domain);

    @SuppressWarnings("unchecked")
    public void applyBoundaryCondition(SimulationDomain domain) {
        // Delegate to domain using our field
        if (field instanceof Field) {
            domain.applyBoundaryConditions((Field<Double>) field);
        }
    }

    @Override
    public Field<T> getField() { return field; }

    public double getMin() { return field != null ? field.getMinValue() : 0.0; }
    public double getMax() { return field != null ? field.getMaxValue() : 0.0; }
    
    @Override
    public void updateState(T[] newValues) {
        for (int i = 0; i < newValues.length; i++) {
            field.setValue(i % nx, i / nx, newValues[i]);
        }
    }

    @Override
    public String describe() {
        if (!isInitialized()) return "(not yet initialized)";
        return String.format("grid=%dx%d", nx, ny);
    }
}