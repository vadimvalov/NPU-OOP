package org.simulation.data;

public class TemperatureField extends Field {
    
    private static final String FIELD_NAME = "Temperature";
    private static final String UNITS = "K";
    
    public TemperatureField(int nx, int ny) {
        super(nx, ny);
    }
    
    public TemperatureField(int nx, int ny, double initialTemperature) {
        super(nx, ny);
        initializeWithConstant(initialTemperature);
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
        return new TemperatureField(getNx(), getNy());
    }
    
    public void setHotSpot(int centerI, int centerJ, double radius, double temperature) {
        for (int i = 0; i < getNx(); i++) {
            for (int j = 0; j < getNy(); j++) {
                double dx = i - centerI;
                double dy = j - centerJ;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance <= radius) {
                    double factor = Math.exp(-(distance * distance) / (2 * radius * radius));
                    setValue(i, j, temperature * factor);
                }
            }
        }
    }
    
    public boolean isPhysicallyValid() {
        return getMinValue() >= 0.0;
    }
}