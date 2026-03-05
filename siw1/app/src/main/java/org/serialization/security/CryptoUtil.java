package org.serialization.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, secureRandom);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new CryptoException("Failed to generate encryption key", e);
        }
    }
    
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    public static SecretKey stringToKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
    
    public static String encrypt(String plaintext, SecretKey key) {
        if (plaintext == null || plaintext.isEmpty()) {
            return null;
        }
        
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);
            
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);
            
            return Base64.getEncoder().encodeToString(byteBuffer.array());
            
        } catch (Exception e) {
            throw new CryptoException("Encryption failed", e);
        }
    }
    
    public static String decrypt(String ciphertext, SecretKey key) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return null;
        }
        
        try {
            byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
            
            ByteBuffer byteBuffer = ByteBuffer.wrap(ciphertextBytes);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            
            byte[] plaintext = cipher.doFinal(encrypted);
            
            return new String(plaintext, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            throw new CryptoException("Decryption failed", e);
        }
    }
    
    public static String quickEncrypt(String plaintext) {
        SecretKey key = generateKey();
        return encrypt(plaintext, key);
    }
    
    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            ByteBuffer buffer = ByteBuffer.allocate(salt.length + hash.length);
            buffer.put(salt);
            buffer.put(hash);
            
            return Base64.getEncoder().encodeToString(buffer.array());
            
        } catch (Exception e) {
            throw new CryptoException("Password hashing failed", e);
        }
    }
    
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] decoded = Base64.getDecoder().decode(storedHash);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            
            byte[] salt = new byte[16];
            buffer.get(salt);
            
            byte[] storedHashBytes = new byte[buffer.remaining()];
            buffer.get(storedHashBytes);
            
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] computedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            return java.security.MessageDigest.isEqual(computedHash, storedHashBytes);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String mask(String data, int visibleChars) {
        if (data == null || data.isEmpty()) {
            return "****";
        }
        
        if (data.length() <= visibleChars) {
            return "*".repeat(data.length());
        }
        
        String visible = data.substring(data.length() - visibleChars);
        String masked = "*".repeat(data.length() - visibleChars);
        return masked + visible;
    }
    
    public static String generateToken(int length) {
        byte[] token = new byte[length];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}