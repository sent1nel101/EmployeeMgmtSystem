package com.dmcdesigns.capstone.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Employee entity representing a standard employee in the system.
 * Extends User with additional fields for role, access permissions, salary, and hire date.
 * 
 * @author DMC Designs
 */
@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {
    @Column(name = "role", nullable = false)
    @NotBlank(message = "Role is required")
    @Size(min = 2, max = 50, message = "Role must be between 2 and 50 characters")
    protected String role = "Employee";
    @Column(name = "has_access", nullable = false)
    protected boolean hasAccess = false;
    
    @Column(name = "salary", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Salary must be positive")
    protected BigDecimal salary = BigDecimal.ZERO;
    
    @Column(name = "hire_date")
    private String hireDate;

    /** Default constructor required by JPA */
    public Employee() {
        super();
    }

    /**
     * Constructor to create an Employee with all required fields
     * 
     * @param firstName Employee's first name
     * @param lastName Employee's last name
     * @param email Employee's email address
     * @param phoneNumber Employee's phone number
     * @param username Unique username for login
     * @param password Encrypted password
     * @param department Department the employee belongs to
     */
    public Employee(String firstName, String lastName, String email, String phoneNumber, String username, String password, String department) {
        super(firstName, lastName, email, phoneNumber, username, password, department);
    }

    public String getRole() {
        return role;
    }

    public boolean hasAccess() {
        return hasAccess;
    }

    public void setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // ============ Access Management ============
    
    /**
     * Grants system access to this employee
     */
    public void grantAccess() {
        this.hasAccess = true;
    }

    /**
     * Revokes system access from this employee
     */
    public void revokeAccess() {
        this.hasAccess = false;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public String getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }
    
    @Override
    public String getPosition() {
        // For regular employees, return their job title (stored in role field)
        // For Admin/Manager subclasses, this will be overridden
        return this.role;
    }
    
    @Override
    public String toString() {
        return "Employee{" +
                "role='" + role + '\'' +
                ", hasAccess=" + hasAccess +
                ", department='" + department + '\'' +
                '}';
    }
}