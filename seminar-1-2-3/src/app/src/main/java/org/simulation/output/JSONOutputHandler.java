package org.simulation.output;

// Gson removed to fix compilation without dependencies


import org.simulation.adapter.FieldSnapshot;
import org.simulation.adapter.LegacyFieldAdapter;
import org.simulation.core.OutputHandler;
import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JSONOutputHandler implements OutputHandler {

    private final String filePath;
    private PrintWriter writer;

    private LegacyFieldAdapter adapter;

    private boolean isFirst = true;

    // Gson removed to fix compilation without dependencies

    private final Map<String, Object> parameters = new HashMap<>();

    public JSONOutputHandler(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void initialize(SimulationDomain domain, PhysicalModel model) {
        this.adapter = new LegacyFieldAdapter(
                model.getName(),
                domain.getNx(),
                domain.getNy()
        );

        try {
            writer = new PrintWriter(new FileWriter(filePath));
            writer.println("{");
            writer.println("\"snapshots\": [");
            System.out.println("[JSON] Writing to: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open JSON file: " + filePath, e);
        }
    }

    @Override
    public void write(double time, int step, double[] fieldValues) {
    if (writer == null) {
        throw new IllegalStateException("JSONOutputHandler not initialized");
    }

    FieldSnapshot snapshot = adapter.adapt(fieldValues, step, time);

    if (!isFirst) {
        writer.println(",");
    }
    // Manual JSON serialization since Gson is missing
    StringBuilder json = new StringBuilder();
    json.append("{\n");
    json.append("  \"modelName\": \"").append(snapshot.getModelName()).append("\",\n");
    json.append("  \"step\": ").append(snapshot.getStep()).append(",\n");
    json.append("  \"time\": ").append(snapshot.getTime()).append(",\n");
    json.append("  \"nx\": ").append(snapshot.getNx()).append(",\n");
    json.append("  \"ny\": ").append(snapshot.getNy()).append(",\n");
    json.append("  \"min\": ").append(snapshot.getMin()).append(",\n");
    json.append("  \"max\": ").append(snapshot.getMax()).append(",\n");
    json.append("  \"mean\": ").append(snapshot.getMean()).append(",\n");
    json.append("  \"field\": [");
    double[] f = snapshot.getField();
    for (int i = 0; i < f.length; i++) {
        json.append(f[i]);
        if (i < f.length - 1) json.append(",");
    }
    json.append("]\n}");
    writer.println(json.toString());
    isFirst = false;  // ← обязательно
    writer.flush();
}

    @Override
    public void write(SimulationDomain domain, double time, int step) {
        // not used
    }

    @Override
    @SuppressWarnings("removal")
    public void close() {
        if (writer != null) {
            writer.println("]");
            writer.println("}");
            writer.close();
            writer = null;
            System.out.println("[JSON] File closed: " + filePath);
        }
    }

    @Override
    public String getName() {
        return "JSONOutputHandler";
    }

    @Override
    public void setParameter(String paramName, Object value) {
        parameters.put(paramName, value);
    }
}