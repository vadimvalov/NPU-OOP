package main.java.org.serialization.serializers;

import main.java.org.serialization.security.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.*;

public class SecureSerializer {
    
    private final SecretKey encryptionKey;
    private boolean enableLogging;
    
    public SecureSerializer(SecretKey encryptionKey) {
        this.encryptionKey = encryptionKey;
        this.enableLogging = false;
    }
    
    public SecureSerializer() {
        this(CryptoUtil.generateKey());
    }
    
    public void serialize(Object object, String filename) throws IOException {
        if (enableLogging) {
            System.out.println("🔒 Secure serialization started...");
            System.out.println("   Object: " + object.getClass().getName());
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
        }
        
        byte[] serializedData = baos.toByteArray();
        
        if (enableLogging) {
            System.out.println("   ✓ Serialized: " + serializedData.length + " bytes");
        }
        
        String base64Data = java.util.Base64.getEncoder().encodeToString(serializedData);
        String encryptedData = CryptoUtil.encrypt(base64Data, encryptionKey);
        
        if (enableLogging) {
            System.out.println("   ✓ Encrypted: " + encryptedData.length() + " chars");
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("SECURE_V1\n");
            writer.write(encryptedData);
        }
        
        if (enableLogging) {
            System.out.println("✅ Secure serialization complete: " + filename);
        }
    }
    
    public Object deserialize(String filename) throws IOException, ClassNotFoundException {
        if (enableLogging) {
            System.out.println("🔓 Secure deserialization started...");
            System.out.println("   File: " + filename);
        }
        
        StringBuilder encryptedData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            
            if (!"SECURE_V1".equals(line)) {
                throw new IOException("Invalid file format or version");
            }
            
            while ((line = reader.readLine()) != null) {
                encryptedData.append(line);
            }
        }
        
        if (enableLogging) {
            System.out.println("   ✓ Read encrypted data: " + encryptedData.length() + " chars");
        }
        
        String base64Data = CryptoUtil.decrypt(encryptedData.toString(), encryptionKey);
        byte[] serializedData = java.util.Base64.getDecoder().decode(base64Data);
        
        if (enableLogging) {
            System.out.println("   ✓ Decrypted: " + serializedData.length + " bytes");
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object object = ois.readObject();
            
            if (enableLogging) {
                System.out.println("   ✓ Deserialized: " + object.getClass().getName());
                System.out.println("✅ Secure deserialization complete");
            }
            
            return object;
        }
    }
    
    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }
    
    public String exportKey() {
        return CryptoUtil.keyToString(encryptionKey);
    }
    
    public static SecureSerializer fromKeyString(String keyString) {
        SecretKey key = CryptoUtil.stringToKey(keyString);
        return new SecureSerializer(key);
    }
    
    public void setLogging(boolean enabled) {
        this.enableLogging = enabled;
    }
}