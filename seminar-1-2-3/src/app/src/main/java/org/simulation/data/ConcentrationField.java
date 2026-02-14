package org.simulation.data;

// extends - inheritance
public class ConcentrationField extends Field {
    
    private static final String FIELD_NAME = "Concentration";
    private static final String UNITS = "mol/L";
    
    public ConcentrationField(int nx, int ny) {
        super(nx, ny);
    }
    
    public ConcentrationField(int nx, int ny, double initialConcentration) {
        super(nx, ny);
        initializeWithConstant(initialConcentration);
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
        return new ConcentrationField(getNx(), getNy());
    }
    
    public void setSource(int sourceI, int sourceJ, double radius, double concentration) {
        for (int i = 0; i < getNx(); i++) {
            for (int j = 0; j < getNy(); j++) {
                double dx = i - sourceI;
                double dy = j - sourceJ;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance <= radius) {
                    setValue(i, j, concentration);
                }
            }
        }
    }
    
    public double getTotalMass(double cellVolume) {
        double totalMass = 0.0;
        
        for (int i = 0; i < getNx(); i++) {
            for (int j = 0; j < getNy(); j++) {
                totalMass += getValue(i, j) * cellVolume;
            }
        }
        
        return totalMass;
    }
    
    public boolean isPhysicallyValid() {
        return getMinValue() >= 0.0;
    }
    
    public void applyFirstOrderReaction(double reactionRate, double dt) {
        double decayFactor = Math.exp(-reactionRate * dt);
        
        for (int i = 0; i < getNx(); i++) {
            for (int j = 0; j < getNy(); j++) {
                double oldValue = getValue(i, j);
                setValue(i, j, oldValue * decayFactor);
            }
        }
    }
}