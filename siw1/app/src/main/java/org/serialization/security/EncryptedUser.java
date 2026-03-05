package org.serialization.security;

import org.serialization.entities.User;
import org.serialization.entities.Department;

import javax.crypto.SecretKey;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EncryptedUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String email;
    private LocalDateTime registrationDate;
    
    private transient String password;
    private transient String creditCardNumber;
    private transient String ssn;
    
    private String passwordHash;
    
    private Department department;
    private List<User> friends;
    
    private transient SecretKey encryptionKey;
    
    public EncryptedUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordHash = CryptoUtil.hashPassword(password);
        this.registrationDate = LocalDateTime.now();
        this.friends = new ArrayList<>();
        
        this.encryptionKey = CryptoUtil.generateKey();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        
        if (encryptionKey == null) {
            encryptionKey = CryptoUtil.generateKey();
        }
        
        String encryptedPassword = CryptoUtil.encrypt(password, encryptionKey);
        out.writeObject(encryptedPassword);
        
        String encryptedCC = CryptoUtil.encrypt(creditCardNumber, encryptionKey);
        out.writeObject(encryptedCC);
        
        String encryptedSSN = CryptoUtil.encrypt(ssn, encryptionKey);
        out.writeObject(encryptedSSN);
        
        String keyString = CryptoUtil.keyToString(encryptionKey);
        out.writeObject(keyString);
        
        System.out.println("✅ Serialized (encrypted): " + username);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        String encryptedPassword = (String) in.readObject();
        
        String encryptedCC = (String) in.readObject();
        
        String encryptedSSN = (String) in.readObject();
        
        String keyString = (String) in.readObject();
        this.encryptionKey = CryptoUtil.stringToKey(keyString);
        
        this.password = CryptoUtil.decrypt(encryptedPassword, encryptionKey);
        this.creditCardNumber = CryptoUtil.decrypt(encryptedCC, encryptionKey);
        this.ssn = CryptoUtil.decrypt(encryptedSSN, encryptionKey);
        
        System.out.println("✅ Deserialized (decrypted): " + username);
    }
    
    public boolean verifyPassword(String inputPassword) {
        return CryptoUtil.verifyPassword(inputPassword, passwordHash);
    }
    
    public void setDepartment(Department department) {
        this.department = department;
    }
    
    public void addFriend(User friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        this.passwordHash = CryptoUtil.hashPassword(password);
    }
    
    public String getCreditCardNumber() {
        return creditCardNumber;
    }
    
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
    
    public String getSsn() {
        return ssn;
    }
    
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public Department getDepartment() {
        return department;
    }
    
    public List<User> getFriends() {
        return friends;
    }
    
    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }
    
    public void setEncryptionKey(SecretKey encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
    
    @Override
    public String toString() {
        return String.format("EncryptedUser{username='%s', email='%s', registered=%s}",
            username, email, registrationDate);
    }
    
    public String toSecureString() {
        return String.format(
            "EncryptedUser{username='%s', email='%s', password='***', cc='%s', ssn='%s'}",
            username,
            email,
            CryptoUtil.mask(creditCardNumber, 4),
            CryptoUtil.mask(ssn, 4)
        );
    }
}