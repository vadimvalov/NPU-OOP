package org.serialization.versioning;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

public class UserV2 implements Serializable {
    
    private static final long serialVersionUID = 2L;
    
    private String username;
    private String email;
    private LocalDateTime registrationDate;
    
    private String phoneNumber;
    private String address;
    private boolean isPremium;
    private LocalDateTime dateOfBirth;
    
    public UserV2(String username, String email, LocalDateTime dateOfBirth) {
        this.username = username;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = LocalDateTime.now();
        this.isPremium = false;
    }
    
    public UserV2(String username, String email, LocalDateTime dateOfBirth, 
                  String phoneNumber, String address, boolean isPremium) {
        this.username = username;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isPremium = isPremium;
        this.registrationDate = LocalDateTime.now();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        if (phoneNumber == null) {
            phoneNumber = "not provided";
            System.out.println("  ⚠️  phoneNumber was null, set to default");
        }
        
        if (address == null) {
            address = "not provided";
            System.out.println("  ⚠️  address was null, set to default");
        }
        
        if (dateOfBirth == null) {
            dateOfBirth = LocalDateTime.now().minusYears(25);
            System.out.println("  ⚠️  dateOfBirth was null, set to default (25 years ago)");
        }
        
        System.out.println("✅ Deserialized UserV2 with backward compatibility handling");
    }
    
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDateTime.now().getYear() - dateOfBirth.getYear();
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean premium) {
        isPremium = premium;
    }
    
    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    @Override
    public String toString() {
        return String.format(
            "UserV2{username='%s', email='%s', age=%d, phone='%s', address='%s', premium=%b, registered=%s}",
            username, email, getAge(), phoneNumber, address, isPremium, registrationDate
        );
    }
}