package org.simulation.data;

public abstract class Field {
    private double[][] data;
    private int nx;
    private int ny;
    private double minValue;
    private double maxValue;
    
    public Field(int nx, int ny) {
        if (nx <= 0 || ny <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
        
        this.nx = nx;
        this.ny = ny;
        this.data = new double[nx][ny];
        this.minValue = Double.MAX_VALUE;
        this.maxValue = -Double.MAX_VALUE;
        
        initializeToZero();
    }
    
    public double getValue(int i, int j) {
        validateIndices(i, j);
        return data[i][j];
    }
    
    public void setValue(int i, int j, double value) {
        validateIndices(i, j);
        data[i][j] = value;
        
        if (value < minValue) minValue = value;
        if (value > maxValue) maxValue = value;
    }
    
    public double[] getValuesAs1DArray() {
        double[] result = new double[nx * ny];
        int index = 0;
        
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                result[index++] = data[i][j];
            }
        }
        
        return result;
    }
    
    public void setValuesFrom1DArray(double[] values) {
        if (values.length != nx * ny) {
            throw new IllegalArgumentException(
                "Array size " + values.length + " does not match grid size " + (nx * ny)
            );
        }
        
        int index = 0;
        minValue = Double.MAX_VALUE;
        maxValue = -Double.MAX_VALUE;
        
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                double value = values[index++];
                data[i][j] = value;
                
                if (value < minValue) minValue = value;
                if (value > maxValue) maxValue = value;
            }
        }
    }
    
    private void initializeToZero() {
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                data[i][j] = 0.0;
            }
        }
        minValue = 0.0;
        maxValue = 0.0;
    }
    
    public void initializeWithConstant(double value) {
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                data[i][j] = value;
            }
        }
        minValue = value;
        maxValue = value;
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
    
    public Field copy() {
        Field newField = createNewInstance();
        
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                newField.setValue(i, j, this.data[i][j]);
            }
        }
        
        return newField;
    }
    
    protected abstract Field createNewInstance();
    
    @Override
    public String toString() {
        return String.format("%s Field [%dx%d]: min=%.3f, max=%.3f %s",
            getFieldName(), nx, ny, minValue, maxValue, getUnits());
    }
}