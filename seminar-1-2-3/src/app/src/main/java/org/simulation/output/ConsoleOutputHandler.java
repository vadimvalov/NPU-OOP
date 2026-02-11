package org.simulation.output;

import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.util.HashMap;
import java.util.Map;

public class ConsoleOutputHandler implements OutputHandler {
    private String fieldName  = "Field";
    private String fieldUnits = "";
    private final Map<String, Object> parameters = new HashMap<>();

    private boolean verbose = false;

    @Override
    public void initialize(SimulationDomain domain, PhysicalModel model) {
        this.fieldName = model.getName();
        System.out.println("[Console] Output handler ready for: " + fieldName);
    }

    @Override
    public void write(double time, int step, double[] fieldValues) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        double sum = 0.0;

        for (double v : fieldValues) {
            if (v < min) min = v;
            if (v > max) max = v;
            sum += v;
        }

        double mean = sum / fieldValues.length;

        if (verbose) {
            System.out.printf("[%s] step=%5d  t=%.6f  min=%12.4f  max=%12.4f  mean=%12.4f  %s%n",
                    fieldName, step, time, min, max, mean, fieldUnits);
        } else {
            System.out.printf("  t=%.6f  [min=%.4f  max=%.4f  mean=%.4f]%n",
                    time, min, max, mean);
        }
    }

    @Override
    public void write(SimulationDomain domain, double time, int step) {
        System.out.printf("[%s] step=%5d  t=%.6f  (domain-level write)%n",
                fieldName, step, time);
    }

    @Override
    public void finalize() {
        System.out.println("[Console] Output complete for: " + fieldName);
    }

    @Override
    public String getName() {
        return "ConsoleOutputHandler";
    }

    @Override
    public void setParameter(String paramName, Object value) {
        parameters.put(paramName, value);
        if ("verbose".equals(paramName)) {
            this.verbose = Boolean.parseBoolean(value.toString());
        }
        if ("units".equals(paramName)) {
            this.fieldUnits = value.toString();
        }
    }
}