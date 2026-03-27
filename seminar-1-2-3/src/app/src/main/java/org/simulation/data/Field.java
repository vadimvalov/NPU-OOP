package org.simulation.data;

/**
 * @brief Generic class representing a physical field.
 * Supports different precision types (Float, Double) via generic type T.
 */
public abstract class Field<T extends Number> {
    private T[][] data;
    private int nx;
    private int ny;
    private double minValue;
    private double maxValue;
    
    @SuppressWarnings("unchecked")
    public Field(int nx, int ny, T initialValue) {
        if (nx <= 0 || ny <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
        
        this.nx = nx;
        this.ny = ny;
        this.data = (T[][]) new Number[nx][ny];
        this.minValue = Double.MAX_VALUE;
        this.maxValue = -Double.MAX_VALUE;
        
        initializeWithValue(initialValue);
    }
    
    public T getValue(int i, int j) {
        validateIndices(i, j);
        return data[i][j];
    }
    
    public void setValue(int i, int j, T value) {
        validateIndices(i, j);
        data[i][j] = value;
        
        double v = value.doubleValue();
        if (v < minValue) minValue = v;
        if (v > maxValue) maxValue = v;
    }
    
    private void initializeWithValue(T value) {
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                data[i][j] = value;
            }
        }
        minValue = value.doubleValue();
        maxValue = value.doubleValue();
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
        int k = 0;
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                flat[k++] = data[i][j];
            }
        }
        return flat;
    }
    
    public double[] getValuesAs1DArray() {
        T[] values = getValueArray();
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i].doubleValue();
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public void setValuesFrom1DArray(double[] values) {
        // This is a bridge for double[]. In practice, many fields should use T[] or specific Double types.
        for (int i = 0; i < values.length; i++) {
            int row = i % nx;
            int col = i / nx;
            this.setValue(row, col, (T) Double.valueOf(values[i]));
        }
    }
    
    public abstract Field<T> copy();


    
    @Override
    public String toString() {
        return String.format("%s Field [%dx%d]: min=%.3f, max=%.3f %s",
            getFieldName(), nx, ny, minValue, maxValue, getUnits());
    }
}