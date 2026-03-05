package org.serialization.serializers;

import java.io.*;

/**
 * Serializer for complex object graphs (e.g., Company with Departments and cyclic references).
 * Uses Java's built-in ObjectOutputStream which handles object graphs and shared references.
 */
public class GraphSerializer {

    private final boolean enableLogging;

    public GraphSerializer(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }

    public void serializeGraph(Object object, String filename) throws IOException {
        if (enableLogging) {
            System.out.println("📊 Serializing object graph...");
            System.out.println("   Root: " + object.getClass().getSimpleName());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(object);
        }

        if (enableLogging) {
            System.out.println("   ✓ Saved to " + filename);
        }
    }

    public Object deserializeGraph(String filename) throws IOException, ClassNotFoundException {
        if (enableLogging) {
            System.out.println("📊 Deserializing object graph...");
            System.out.println("   File: " + filename);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object result = ois.readObject();
            if (enableLogging) {
                System.out.println("   ✓ Loaded: " + result.getClass().getSimpleName());
            }
            return result;
        }
    }
}
