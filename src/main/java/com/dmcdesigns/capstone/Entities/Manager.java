package com.dmcdesigns.capstone.Entities;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANAGER")
public class Manager extends Employee {
    @Column(name = "role", nullable = false)
    private String role = "Manager";
    @Column(name = "has_access", nullable = false)
    private boolean hasAccess = true;

    public Manager(String firstName, String lastName, String email, String phoneNumber, String username, String password, String department) {
        super(firstName, lastName, email, phoneNumber, username, password, department);
    }

    public String getRole() {
        return role;
    }

    public boolean hasAccess() {
        return hasAccess;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setRole(String role) {
        this.role = role;
    }

     @Override
    public String toString() {
        return "Manager{" +
                "role='" + role + '\'' +
                ", hasAccess=" + hasAccess +
                ", department='" + department + '\'' +
                '}';
    }

    // Default constructor required by JPA
    public Manager() {
        super();
    }
}
