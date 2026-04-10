package org.simulation.data;

/**
 * @brief Generic class representing a physical field.
 * Supports different precision types (Float, Double) via generic type T.
 */
public abstract class Field<T extends Number> {
    private double[] data1D;
    private int nx;
    private int ny;
    private double minValue;
    private double maxValue;
    
    public Field(int nx, int ny, T initialValue) {
        if (nx <= 0 || ny <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
        
        this.nx = nx;
        this.ny = ny;
        this.data1D = new double[nx * ny];
        this.minValue = Double.MAX_VALUE;
        this.maxValue = -Double.MAX_VALUE;
        
        initializeWithValue(initialValue);
    }
    
    @SuppressWarnings("unchecked")
    public T getValue(int i, int j) {
        validateIndices(i, j);
        return (T) Double.valueOf(data1D[j * nx + i]);
    }
    
    public double getDoubleValue(int i, int j) {
        validateIndices(i, j);
        return data1D[j * nx + i];
    }
    
    public void setValue(int i, int j, T value) {
        setDoubleValue(i, j, value.doubleValue());
    }
    
    public void setDoubleValue(int i, int j, double v) {
        validateIndices(i, j);
        data1D[j * nx + i] = v;
        if (v < minValue) minValue = v;
        if (v > maxValue) maxValue = v;
    }
    
    private void initializeWithValue(T value) {
        double v = value.doubleValue();
        for (int k = 0; k < data1D.length; k++) {
            data1D[k] = v;
        }
        minValue = v;
        maxValue = v;
    }
    
    private void validateIndices(int i, int j) {
        if (i < 0 || i >= nx || j < 0 || j >= ny) {
            throw new IndexOutOfBoundsException(
                "Invalid indices (" + i + ", " + j + ") for grid size (" + nx + ", " + ny + ")"
            );
        }
    }
    
    public int getNx() { return nx; }
    public int getNy() { return ny; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    
    public abstract String getFieldName();
    public abstract String getUnits();
    
    @SuppressWarnings("unchecked")
    public T[] getValueArray() {
        T[] flat = (T[]) new Number[nx * ny];
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                flat[j * nx + i] = (T) Double.valueOf(data1D[j * nx + i]);
            }
        }
        return flat;
    }
    
    public double[] getValuesAs1DArray() {
        return data1D.clone();
    }
    
    public void setValuesFrom1DArray(double[] values) {
        if (values.length != data1D.length) {
            throw new IllegalArgumentException("Length mismatch");
        }
        System.arraycopy(values, 0, this.data1D, 0, values.length);
        
        // Update min/max
        minValue = Double.MAX_VALUE;
        maxValue = -Double.MAX_VALUE;
        for(double v : this.data1D) {
            if(v < minValue) minValue = v;
            if(v > maxValue) maxValue = v;
        }
    }
    
    public abstract Field<T> copy();


    
    @Override
    public String toString() {
        return String.format("%s Field [%dx%d]: min=%.3f, max=%.3f %s",
            getFieldName(), nx, ny, minValue, maxValue, getUnits());
    }
}