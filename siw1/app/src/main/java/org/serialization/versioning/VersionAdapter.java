package org.serialization.versioning;

import java.io.*;
import java.time.LocalDateTime;

public class VersionAdapter {
    
    public static UserV2 convertV1toV2(UserV1 v1) {
        if (v1 == null) {
            return null;
        }
        
        LocalDateTime dateOfBirth = LocalDateTime.now().minusYears(v1.getAge());
        
        UserV2 v2 = new UserV2(
            v1.getUsername(),
            v1.getEmail(),
            dateOfBirth
        );
        
        v2.setPhoneNumber("not provided");
        v2.setAddress("not provided");
        v2.setPremium(false);
        
        System.out.println("✅ Converted V1 → V2: " + v1.getUsername());
        
        return v2;
    }
    
    public static UserV1 convertV2toV1(UserV2 v2) {
        if (v2 == null) {
            return null;
        }
        
        UserV1 v1 = new UserV1(
            v2.getUsername(),
            v2.getEmail(),
            v2.getAge()
        );
        
        System.out.println("⚠️  Converted V2 → V1: " + v2.getUsername() + " (data loss!)");
        System.out.println("   Lost: phoneNumber, address, isPremium");
        
        return v1;
    }
    
    public static UserV2 migrateV1toV2ViaFile(UserV1 v1, String filename) {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(v1);
            }
            
            UserV1 loadedV1;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                loadedV1 = (UserV1) ois.readObject();
            }
            
            return convertV1toV2(loadedV1);
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Migration failed: " + e.getMessage());
            return null;
        }
    }
    
    public static UserV2 deserializeAsV2(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            
            if (obj instanceof UserV2) {
                System.out.println("✅ File contains UserV2");
                return (UserV2) obj;
            }
            
        } catch (InvalidClassException e) {
            System.out.println("⚠️  File is not UserV2, trying V1...");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Read error: " + e.getMessage());
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            UserV1 v1 = (UserV1) ois.readObject();
            System.out.println("✅ File contains UserV1, converting to V2...");
            return convertV1toV2(v1);
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Failed to read as V1: " + e.getMessage());
            return null;
        }
    }
}