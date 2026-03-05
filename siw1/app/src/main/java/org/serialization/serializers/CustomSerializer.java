package org.serialization.serializers;

import java.io.*;

public class CustomSerializer {
    
    private boolean enableLogging;
    private SerializationStats lastStats;
    
    public CustomSerializer() {
        this(false);
    }
    
    public CustomSerializer(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
    
    public SerializationStats serializeWithMetadata(Object object, String filename, Metadata metadata) {
        long startTime = System.currentTimeMillis();
        
        if (enableLogging) {
            System.out.println("📝 Starting serialization...");
            System.out.println("   Object: " + object.getClass().getName());
            System.out.println("   File: " + filename);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            
            oos.writeObject(metadata);
            
            if (enableLogging) {
                System.out.println("   ✓ Metadata written");
            }
            
            oos.writeObject(object);
            
            if (enableLogging) {
                System.out.println("   ✓ Object written");
            }
            
            long endTime = System.currentTimeMillis();
            long fileSize = new File(filename).length();
            
            lastStats = new SerializationStats(
                filename,
                object.getClass().getName(),
                fileSize,
                endTime - startTime,
                true,
                null
            );
            
            if (enableLogging) {
                System.out.println("✅ Serialization complete:");
                System.out.println("   Size: " + fileSize + " bytes");
                System.out.println("   Time: " + (endTime - startTime) + " ms");
            }
            
            return lastStats;
            
        } catch (IOException e) {
            long endTime = System.currentTimeMillis();
            
            lastStats = new SerializationStats(
                filename,
                object.getClass().getName(),
                0,
                endTime - startTime,
                false,
                e.getMessage()
            );
            
            if (enableLogging) {
                System.err.println("❌ Serialization failed: " + e.getMessage());
            }
            
            throw new SerializationException("Serialization failed", e);
        }
    }
    
    public SerializationStats serialize(Object object, String filename) {
        Metadata defaultMetadata = new Metadata(
            object.getClass().getName(),
            "1.0",
            System.currentTimeMillis()
        );
        
        return serializeWithMetadata(object, filename, defaultMetadata);
    }
    
    public DeserializationResult deserializeWithMetadata(String filename) {
        long startTime = System.currentTimeMillis();
        
        if (enableLogging) {
            System.out.println("📖 Starting deserialization...");
            System.out.println("   File: " + filename);
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            
            Metadata metadata = (Metadata) ois.readObject();
            
            if (enableLogging) {
                System.out.println("   ✓ Metadata read: " + metadata);
            }
            
            Object object = ois.readObject();
            
            if (enableLogging) {
                System.out.println("   ✓ Object read: " + object.getClass().getName());
            }
            
            long endTime = System.currentTimeMillis();
            
            if (enableLogging) {
                System.out.println("✅ Deserialization complete: " + (endTime - startTime) + " ms");
            }
            
            return new DeserializationResult(object, metadata, true, null);
            
        } catch (IOException | ClassNotFoundException e) {
            if (enableLogging) {
                System.err.println("❌ Deserialization failed: " + e.getMessage());
            }
            
            return new DeserializationResult(null, null, false, e.getMessage());
        }
    }
    
    public Object deserialize(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }
    }
    
    public SerializationStats getLastStats() {
        return lastStats;
    }
    
    public void setLogging(boolean enabled) {
        this.enableLogging = enabled;
    }
    
    public static class Metadata implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final String className;
        private final String version;
        private final long timestamp;
        private String description;
        
        public Metadata(String className, String version, long timestamp) {
            this.className = className;
            this.version = version;
            this.timestamp = timestamp;
        }
        
        public String getClassName() {
            return className;
        }
        
        public String getVersion() {
            return version;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return String.format("Metadata{class='%s', version='%s', timestamp=%d}",
                className, version, timestamp);
        }
    }
    
    public static class SerializationStats {
        private final String filename;
        private final String className;
        private final long sizeBytes;
        private final long timeMillis;
        private final boolean success;
        private final String errorMessage;
        
        public SerializationStats(String filename, String className, long sizeBytes, 
                                 long timeMillis, boolean success, String errorMessage) {
            this.filename = filename;
            this.className = className;
            this.sizeBytes = sizeBytes;
            this.timeMillis = timeMillis;
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public String getFilename() {
            return filename;
        }
        
        public String getClassName() {
            return className;
        }
        
        public long getSizeBytes() {
            return sizeBytes;
        }
        
        public long getTimeMillis() {
            return timeMillis;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        @Override
        public String toString() {
            if (success) {
                return String.format("SerializationStats{file='%s', class='%s', size=%d bytes, time=%d ms}",
                    filename, className, sizeBytes, timeMillis);
            } else {
                return String.format("SerializationStats{file='%s', FAILED: %s}",
                    filename, errorMessage);
            }
        }
    }
    
    public static class DeserializationResult {
        private final Object object;
        private final Metadata metadata;
        private final boolean success;
        private final String errorMessage;
        
        public DeserializationResult(Object object, Metadata metadata, 
                                    boolean success, String errorMessage) {
            this.object = object;
            this.metadata = metadata;
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public Object getObject() {
            return object;
        }
        
        public Metadata getMetadata() {
            return metadata;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}