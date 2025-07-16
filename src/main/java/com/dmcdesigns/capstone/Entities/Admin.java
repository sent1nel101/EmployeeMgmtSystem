package com.dmcdesigns.capstone.Entities;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Employee {
    
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

    @Override
    public String toString() {
        return "Admin{" +
                "role='" + role + '\'' +
                ", hasAccess=" + hasAccess +
                '}';
    }
}
