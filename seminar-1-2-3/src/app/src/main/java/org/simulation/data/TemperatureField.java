package org.simulation.data;

public class TemperatureField extends Field<Double> {
    private static final String FIELD_NAME = "Temperature";
    private static final String UNITS = "K";
    
    public TemperatureField(int nx, int ny) {
        super(nx, ny, 0.0);
    }
    
    public TemperatureField(int nx, int ny, double initialTemperature) {
        super(nx, ny, initialTemperature);
    }
    
    @Override public String getFieldName() { return FIELD_NAME; }
    @Override public String getUnits() { return UNITS; }
    
    @Override
    public Field<Double> copy() {
        TemperatureField newField = new TemperatureField(getNx(), getNy());
        for (int i = 0; i < getNx(); i++) {
            for (int j = 0; j < getNy(); j++) {
                newField.setValue(i, j, getValue(i, j));
            }
        }
        return newField;
    }
}