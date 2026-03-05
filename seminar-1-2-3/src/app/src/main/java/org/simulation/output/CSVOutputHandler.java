package org.simulation.output;

import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CSVOutputHandler implements OutputHandler {

    private String filePath;
    private PrintWriter writer;

    private int nx = 0;
    private int ny = 0;

    private boolean writeFullSnapshot = false;
    private final Map<String, Object> parameters = new HashMap<>();

    public CSVOutputHandler(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void initialize(SimulationDomain domain, PhysicalModel model) {
        this.nx = domain.getNx();
        this.ny = domain.getNy();

        try {
            writer = new PrintWriter(new FileWriter(filePath));

            if (writeFullSnapshot) {
                writer.println("step,time,i,j,value");
            } else {
                writer.println("step,time,min,max,mean");
            }

            System.out.println("[CSV] Writing to: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open CSV file: " + filePath, e);
        }
    }

    @Override
    public void write(double time, int step, double[] fieldValues) {
        if (writer == null) {
            throw new IllegalStateException("CSVOutputHandler not initialized");
        }

        if (writeFullSnapshot) {
            writeSnapshot(time, step, fieldValues);
        } else {
            writeStats(time, step, fieldValues);
        }

        writer.flush();
    }

    @Override
    public void write(SimulationDomain domain, double time, int step) {
        if (writer == null) return;
        writer.printf("# domain-write at step=%d t=%.6f%n", step, time);
        writer.flush();
    }

    private void writeStats(double time, int step, double[] fieldValues) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        double sum = 0.0;

        for (double v : fieldValues) {
            if (v < min) min = v;
            if (v > max) max = v;
            sum += v;
        }

        double mean = sum / fieldValues.length;

        writer.printf("%d,%.8f,%.6f,%.6f,%.6f%n", step, time, min, max, mean);
    }

    private void writeSnapshot(double time, int step, double[] fieldValues) {
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                int index = j * nx + i;
                writer.printf("%d,%.8f,%d,%d,%.8f%n",
                        step, time, i, j, fieldValues[index]);
            }
        }
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
            writer = null;
            System.out.println("[CSV] File closed: " + filePath);
        }
    }

    @Override
    public String getName() {
        return "CSVOutputHandler";
    }

    @Override
    public void setParameter(String paramName, Object value) {
        parameters.put(paramName, value);
        if ("filePath".equals(paramName)) {
            this.filePath = value.toString();
        }
        if ("writeFullSnapshot".equals(paramName)) {
            this.writeFullSnapshot = Boolean.parseBoolean(value.toString());
        }
    }
}