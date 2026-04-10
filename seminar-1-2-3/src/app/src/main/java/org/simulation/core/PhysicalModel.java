package org.simulation.core;

import org.simulation.data.Field;

/**
 * @brief Generic interface for physical models.
 * @param <T> The numeric type (Double, Float).
 */
public interface PhysicalModel<T extends Number> {
    double[] computeRHS(SimulationDomain domain, double time);

    void initialize(SimulationDomain domain);
    
    String getName();
    
    Field<T> getField();
    
    default double[] getFieldValues() {
        T[] values = getField().getValueArray();
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i].doubleValue();
        }
        return result;
    }
    
    void updateState(double[] newValues);
}