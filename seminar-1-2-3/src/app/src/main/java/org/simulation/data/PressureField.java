package org.simulation.data;

public class PressureField extends Field<Double> {
    private static final String FIELD_NAME = "Pressure";
    private static final String UNITS = "Pa";

    public PressureField(int nx, int ny) {
        super(nx, ny, 0.0);
    }
    
    public PressureField(int nx, int ny, double initialPressure) {
        super(nx, ny, initialPressure);
    }

    @Override public String getFieldName() { return FIELD_NAME; }
    @Override public String getUnits() { return UNITS; }

    public Field<Double> copy() {
        PressureField newField = new PressureField(getNx(), getNy());
        newField.setValuesFrom1DArray(this.getValuesAs1DArray());
        return newField;
    }
}