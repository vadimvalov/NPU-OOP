package main.java.org.serialization.versioning;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserV1 implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String email;
    private int age;
    private LocalDateTime registrationDate;
    
    public UserV1(String username, String email, int age) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.registrationDate = LocalDateTime.now();
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
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    @Override
    public String toString() {
        return String.format("UserV1{username='%s', email='%s', age=%d, registered=%s}",
            username, email, age, registrationDate);
    }
}