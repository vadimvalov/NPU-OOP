package org.simulation.data;

public class ConcentrationField extends Field<Double> {
    private static final String FIELD_NAME = "Concentration";
    private static final String UNITS = "mol/L";

    public ConcentrationField(int nx, int ny) {
        super(nx, ny, 0.0);
    }
    
    public ConcentrationField(int nx, int ny, double initialConcentration) {
        super(nx, ny, initialConcentration);
    }

    @Override public String getFieldName() { return FIELD_NAME; }
    @Override public String getUnits() { return UNITS; }

    @Override
    public Field<Double> copy() {
        ConcentrationField newField = new ConcentrationField(getNx(), getNy());
        for (int i = 0; i < getNx(); i++) {
            for (int j = 0; j < getNy(); j++) {
                newField.setValue(i, j, getValue(i, j));
            }
        }
        return newField;
    }
}