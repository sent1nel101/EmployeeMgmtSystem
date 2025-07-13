package com.dmcdesigns.capstone.Repositories;

import com.dmcdesigns.capstone.Entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee1;
    private Employee testEmployee2;

    @BeforeEach
    void setUp() {
        // Create test employees
        testEmployee1 = new Employee("Alice", "Johnson", "alice.johnson@company.com", 
                                   "555-1111", "alice.johnson", "password123", "Engineering");
        testEmployee1.setSalary(new BigDecimal("75000.00"));
        testEmployee1.setHireDate("2022-01-15");
        
        testEmployee2 = new Employee("Bob", "Brown", "bob.brown@company.com", 
                                   "555-2222", "bob.brown", "password456", "Marketing");
        testEmployee2.setSalary(new BigDecimal("68000.00"));
        testEmployee2.setHireDate("2021-06-10");
        
        // Persist test data
        entityManager.persistAndFlush(testEmployee1);
        entityManager.persistAndFlush(testEmployee2);
    }

    @Test
    void testFindByUsername() {
        // Test finding employee by username
        Employee foundEmployee = employeeRepository.findByUsername("alice.johnson");
        
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getUsername()).isEqualTo("alice.johnson");
        assertThat(foundEmployee.getFirstName()).isEqualTo("Alice");
        assertThat(foundEmployee.getDepartment()).isEqualTo("Engineering");
    }

    @Test
    void testFindByEmail() {
        // Test finding employee by email
        Employee foundEmployee = employeeRepository.findByEmail("bob.brown@company.com");
        
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getEmail()).isEqualTo("bob.brown@company.com");
        assertThat(foundEmployee.getFirstName()).isEqualTo("Bob");
        assertThat(foundEmployee.getDepartment()).isEqualTo("Marketing");
    }

    @Test
    void testExistsByUsername() {
        // Test checking if employee exists by username
        assertThat(employeeRepository.existsByUsername("alice.johnson")).isTrue();
        assertThat(employeeRepository.existsByUsername("nonexistent.employee")).isFalse();
    }

    @Test
    void testExistsByEmail() {
        // Test checking if employee exists by email
        assertThat(employeeRepository.existsByEmail("alice.johnson@company.com")).isTrue();
        assertThat(employeeRepository.existsByEmail("nonexistent@company.com")).isFalse();
    }

    @Test
    void testEmployeeSpecificMethods() {
        // Test employee-specific functionality
        Employee employee = employeeRepository.findByUsername("alice.johnson");
        
        assertThat(employee.getRole()).isEqualTo("Employee");
        assertThat(employee.hasAccess()).isFalse(); // Default access is false for employees
        
        // Test granting access
        employee.grantAccess();
        employeeRepository.save(employee);
        
        Employee updatedEmployee = employeeRepository.findByUsername("alice.johnson");
        assertThat(updatedEmployee.hasAccess()).isTrue();
        
        // Test revoking access
        updatedEmployee.revokeAccess();
        employeeRepository.save(updatedEmployee);
        
        Employee finalEmployee = employeeRepository.findByUsername("alice.johnson");
        assertThat(finalEmployee.hasAccess()).isFalse();
    }

    @Test
    void testInheritedMethods() {
        // Test that EmployeeRepository inherits UserRepository methods
        Employee newEmployee = new Employee("Charlie", "Davis", "charlie.davis@company.com", 
                                          "555-3333", "charlie.davis", "password789", "Sales");
        newEmployee.setSalary(new BigDecimal("55000.00"));
        newEmployee.setHireDate("2023-03-01");
        
        // Save using inherited save method
        Employee savedEmployee = employeeRepository.save(newEmployee);
        assertThat(savedEmployee.getId()).isNotNull();
        
        // Find using inherited findAll method
        assertThat(employeeRepository.findAll()).hasSize(3); // 2 from setup + 1 new
        
        // Count using inherited count method
        assertThat(employeeRepository.count()).isEqualTo(3);
    }

    @Test
    void testEmployeeRoleAndDepartment() {
        // Test setting and getting role and department
        Employee employee = employeeRepository.findByUsername("bob.brown");
        
        // Test initial role
        assertThat(employee.getRole()).isEqualTo("Employee");
        
        // Test changing role
        employee.setRole("Senior Employee");
        employeeRepository.save(employee);
        
        Employee updatedEmployee = employeeRepository.findByUsername("bob.brown");
        assertThat(updatedEmployee.getRole()).isEqualTo("Senior Employee");
        
        // Test department functionality
        assertThat(employee.getDepartment()).isEqualTo("Marketing");
        employee.setDepartment("Sales");
        employeeRepository.save(employee);
        
        Employee employeeWithNewDept = employeeRepository.findByUsername("bob.brown");
        assertThat(employeeWithNewDept.getDepartment()).isEqualTo("Sales");
    }

    @Test
    void testCrudOperations() {
        // Create
        Employee newEmployee = new Employee("Diana", "Wilson", "diana.wilson@company.com", 
                                          "555-4444", "diana.wilson", "password000", "Finance");
        newEmployee.setSalary(new BigDecimal("62000.00"));
        newEmployee.setHireDate("2023-05-15");
        Employee savedEmployee = employeeRepository.save(newEmployee);
        assertThat(savedEmployee.getId()).isNotNull();
        assertThat(savedEmployee.getRole()).isEqualTo("Employee");
        
        // Read
        Employee foundEmployee = employeeRepository.findByUsername("diana.wilson");
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getFirstName()).isEqualTo("Diana");
        
        // Update
        foundEmployee.setPhoneNumber("555-5555");
        foundEmployee.grantAccess();
        Employee updatedEmployee = employeeRepository.save(foundEmployee);
        assertThat(updatedEmployee.getPhoneNumber()).isEqualTo("555-5555");
        assertThat(updatedEmployee.hasAccess()).isTrue();
        
        // Delete
        employeeRepository.delete(updatedEmployee);
        Employee deletedEmployee = employeeRepository.findByUsername("diana.wilson");
        assertThat(deletedEmployee).isNull();
    }

    @Test
    void testEmployeeInheritanceChain() {
        // Test that Employee properly inherits from User
        Employee employee = employeeRepository.findByUsername("alice.johnson");
        
        // Test User properties
        assertThat(employee.getFirstName()).isEqualTo("Alice");
        assertThat(employee.getLastName()).isEqualTo("Johnson");
        assertThat(employee.getEmail()).isEqualTo("alice.johnson@company.com");
        assertThat(employee.getPhoneNumber()).isEqualTo("555-1111");
        assertThat(employee.getUsername()).isEqualTo("alice.johnson");
        assertThat(employee.getDepartment()).isEqualTo("Engineering");
        
        // Test Employee-specific properties
        assertThat(employee.getRole()).isEqualTo("Employee");
        assertThat(employee.hasAccess()).isFalse();
    }

    @Test
    void testSalaryAndHireDate() {
        // Test salary and hire date functionality
        Employee employee = employeeRepository.findByUsername("alice.johnson");
        
        // Test salary
        assertThat(employee.getSalary()).isEqualTo(new BigDecimal("75000.00"));
        
        // Test hire date
        assertThat(employee.getHireDate()).isEqualTo("2022-01-15");
        
        // Test updating salary
        employee.setSalary(new BigDecimal("80000.00"));
        employee.setHireDate("2022-01-16");
        employeeRepository.save(employee);
        
        Employee updatedEmployee = employeeRepository.findByUsername("alice.johnson");
        assertThat(updatedEmployee.getSalary()).isEqualTo(new BigDecimal("80000.00"));
        assertThat(updatedEmployee.getHireDate()).isEqualTo("2022-01-16");
    }
}
