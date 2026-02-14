package main.java.org.serialization.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String email;
    private LocalDateTime registrationDate;
    
    private String password;
    private String creditCardNumber;
    private String ssn;
    
    private Department department;
    private List<User> friends;
    
    private transient int loginAttempts;
    private transient String sessionToken;
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = LocalDateTime.now();
        this.friends = new ArrayList<>();
        this.loginAttempts = 0;
    }
    
    public void addFriend(User friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
            if (!friend.getFriends().contains(this)) {
                friend.addFriend(this);
            }
        }
    }
    
    public void setDepartment(Department department) {
        this.department = department;
        if (!department.getEmployees().contains(this)) {
            department.addEmployee(this);
        }
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public Department getDepartment() {
        return department;
    }
    
    public List<User> getFriends() {
        return friends;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public int getLoginAttempts() {
        return loginAttempts;
    }
    
    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }
    
    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && 
               Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
    
    @Override
    public String toString() {
        return String.format("User{username='%s', email='%s', department=%s, friends=%d, registered=%s}",
            username, 
            email,
            department != null ? department.getName() : "none",
            friends.size(),
            registrationDate
        );
    }

    public String toSecureString() {
        return String.format("User{username='%s', email='%s', password='***', cc='%s', ssn='%s'}",
            username,
            email,
            maskCreditCard(creditCardNumber),
            maskSSN(ssn)
        );
    }
    
    private String maskCreditCard(String cc) {
        if (cc == null || cc.length() < 4) return "****";
        return "**** **** **** " + cc.substring(cc.length() - 4);
    }
    
    private String maskSSN(String ssn) {
        if (ssn == null || ssn.length() < 4) return "***-**-****";
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }
}