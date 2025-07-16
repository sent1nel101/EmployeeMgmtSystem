package com.dmcdesigns.capstone.Entities;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Employee {
    @Column(name = "role", nullable = false)
    private String role = "Admin";
    @Column(name = "has_access", nullable = false)
    private boolean hasAccess = true;
    
    // Default constructor required by JPA
    public Admin() {
        super();
        this.role = "Admin";
        this.hasAccess = true;
    }
    
    public Admin(String firstName, String lastName, String email, String phoneNumber, String username, String password, String department) {
        super(firstName, lastName, email, phoneNumber, username, password, department);
        this.role = "Admin";
        this.hasAccess = true;
    }

    public String getRole() {
        return role;
    }

    public boolean hasAccess() {
        return hasAccess;
    }

    // Admin-specific methods
    public void setAccess(boolean access) {
        this.hasAccess = access;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "role='" + role + '\'' +
                ", hasAccess=" + hasAccess +
                '}';
    }
}
