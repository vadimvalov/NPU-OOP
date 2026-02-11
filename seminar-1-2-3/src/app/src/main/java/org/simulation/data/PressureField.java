package org.simulation.data;

public class PressureField extends Field {
    
    private static final String FIELD_NAME = "Pressure";
    private static final String UNITS = "Pa";
    
    public PressureField(int nx, int ny) {
        super(nx, ny);
    }
    
    public PressureField(int nx, int ny, double initialPressure) {
        super(nx, ny);
        initializeWithConstant(initialPressure);
    }
    
    @Override
    public String getFieldName() {
        return FIELD_NAME;
    }
    
    @Override
    public String getUnits() {
        return UNITS;
    }
    
    @Override
    protected Field createNewInstance() {
        return new PressureField(getNx(), getNy());
    }
    
    public void setLinearGradient(double pressureLeft, double pressureRight) {
        int nx = getNx();
        int ny = getNy();
        
        for (int i = 0; i < nx; i++) {
            double t = (double) i / (nx - 1);
            double pressure = pressureLeft + t * (pressureRight - pressureLeft);
            
            for (int j = 0; j < ny; j++) {
                setValue(i, j, pressure);
            }
        }
    }
    
    public double getAverageGradient() {
        int nx = getNx();
        double leftPressure = 0.0;
        double rightPressure = 0.0;
        
        for (int j = 0; j < getNy(); j++) {
            leftPressure += getValue(0, j);
        }
        leftPressure /= getNy();
        
        for (int j = 0; j < getNy(); j++) {
            rightPressure += getValue(nx - 1, j);
        }
        rightPressure /= getNy();
        
        return (rightPressure - leftPressure) / nx;
    }
}