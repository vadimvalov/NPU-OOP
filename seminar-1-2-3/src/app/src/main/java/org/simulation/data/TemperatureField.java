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
    
    public Field<Double> copy() {
        TemperatureField newField = new TemperatureField(getNx(), getNy());
        newField.setValuesFrom1DArray(this.getValuesAs1DArray());
        return newField;
    }
}