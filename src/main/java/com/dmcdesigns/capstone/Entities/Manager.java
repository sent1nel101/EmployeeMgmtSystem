package com.dmcdesigns.capstone.Entities;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANAGER")
public class Manager extends Employee {

    public Manager(String firstName, String lastName, String email, String phoneNumber, String username, String password, String department) {
        super(firstName, lastName, email, phoneNumber, username, password, department);
        this.role = "Manager";
        this.hasAccess = true;
    }

    // Default constructor required by JPA
    public Manager() {
        super();
        this.role = "Manager";
        this.hasAccess = true;
    }

     @Override
    public String toString() {
        return "Manager{" +
                "role='" + role + '\'' +
                ", hasAccess=" + hasAccess +
                ", department='" + department + '\'' +
                '}';
    }
}
