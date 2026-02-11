package org.simulation.data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FieldStorage {
    private final String storageDirectory;

    private final Map<String, double[]> cache = new HashMap<>();

    public FieldStorage(String storageDirectory) {
        this.storageDirectory = storageDirectory;
        new File(storageDirectory).mkdirs();
    }

    public FieldStorage() {
        this("simulation_output");
    }

    public void save(String fieldName, double[] values, int nx, int ny, double time, int step) {
        String fileName = buildFileName(fieldName, step);
        File file = new File(storageDirectory, fileName);

        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {

            dos.writeInt(nx);
            dos.writeInt(ny);
            dos.writeDouble(time);
            dos.writeInt(step);

            for (double v : values) {
                dos.writeDouble(v);
            }

            cache.put(fieldName, values.clone());

        } catch (IOException e) {
            throw new RuntimeException("Failed to save field: " + fieldName, e);
        }
    }

    public double[] load(String fieldName, int step) {
        String fileName = buildFileName(fieldName, step);
        File file = new File(storageDirectory, fileName);

        if (!file.exists()) {
            throw new RuntimeException("Snapshot not found: " + file.getAbsolutePath());
        }

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {

            int nx     = dis.readInt();
            int ny     = dis.readInt();

            double[] values = new double[nx * ny];
            for (int k = 0; k < values.length; k++) {
                values[k] = dis.readDouble();
            }

            return values;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load field: " + fieldName, e);
        }
    }

    public void putToCache(String fieldName, double[] values) {
        cache.put(fieldName, values.clone());
    }

    public double[] getFromCache(String fieldName) {
        double[] cached = cache.get(fieldName);
        if (cached == null) {
            throw new RuntimeException("No cached snapshot for field: " + fieldName);
        }
        return cached.clone();
    }

    public boolean isCached(String fieldName) {
        return cache.containsKey(fieldName);
    }

    public void clearCache() {
        cache.clear();
    }

    private String buildFileName(String fieldName, int step) {
        return fieldName.replaceAll("\\s+", "_") + "_step_" + String.format("%06d", step) + ".bin";
    }

    public String getStorageDirectory() {
        return storageDirectory;
    }
}