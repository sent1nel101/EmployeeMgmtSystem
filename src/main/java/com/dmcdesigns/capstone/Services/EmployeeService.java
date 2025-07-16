package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Admin;
import com.dmcdesigns.capstone.Entities.Manager;
import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    public Optional<Employee> getEmployeeById(Integer id) {
        Optional<User> user = employeeRepository.findById(id);
        return user.map(u -> u instanceof Employee ? (Employee) u : null);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setPassword(employeeDetails.getPassword());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setRole(employeeDetails.getRole());

        if (employeeDetails.hasAccess()) {
            employee.grantAccess();
        } else {
            employee.revokeAccess();
        }

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Integer id) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    /**
     * Create employee from form data with separate userRole and position
     */
    public Employee createEmployeeFromData(Map<String, Object> data) {
        String userRole = (String) data.get("userRole");
        String position = (String) data.get("position");
        
        Employee employee;
        
        // Create the appropriate type based on userRole
        switch (userRole) {
            case "ADMIN":
                employee = new Admin();
                break;
            case "MANAGER":
                employee = new Manager();
                break;
            case "EMPLOYEE":
            default:
                employee = new Employee();
                break;
        }
        
        // Set common fields
        employee.setFirstName((String) data.get("firstName"));
        employee.setLastName((String) data.get("lastName"));
        employee.setDepartment((String) data.get("department"));
        employee.setRole(position); // Set job title in role field
        employee.setPhoneNumber((String) data.get("phoneNumber"));
        employee.setHireDate((String) data.get("hireDate"));
        
        // Set salary
        if (data.get("salary") != null) {
            Number salaryNum = (Number) data.get("salary");
            employee.setSalary(new BigDecimal(salaryNum.toString()));
        }
        
        // Set auto-generated fields
        employee.setUsername((String) data.get("firstName"), (String) data.get("lastName"));
        employee.createEmail(employee.getUsername());
        
        // Encode password
        if (data.get("password") != null) {
            employee.setPassword(passwordEncoder.encode((String) data.get("password")));
        }
        
        return employeeRepository.save(employee);
    }

    /**
     * Update employee from form data with separate userRole and position
     */
    public Employee updateEmployeeFromData(Integer id, Map<String, Object> data) {
        Employee existingEmployee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        String userRole = (String) data.get("userRole");
        String position = (String) data.get("position");
        
        // Check if we need to change the employee type
        String currentType = existingEmployee.getClass().getSimpleName();
        boolean needsTypeChange = false;
        
        if ("ADMIN".equals(userRole) && !"Admin".equals(currentType)) {
            needsTypeChange = true;
        } else if ("MANAGER".equals(userRole) && !"Manager".equals(currentType)) {
            needsTypeChange = true;
        } else if ("EMPLOYEE".equals(userRole) && !"Employee".equals(currentType)) {
            needsTypeChange = true;
        }
        
        if (needsTypeChange) {
            // Delete old and create new with correct type
            String existingUsername = existingEmployee.getUsername();
            String existingEmail = existingEmployee.getEmail();
            String existingPassword = existingEmployee.getPassword();
            
            employeeRepository.delete(existingEmployee);
            
            // Create new employee with correct type
            Employee newEmployee = createEmployeeFromTypeChange(data, userRole, existingUsername, existingEmail, existingPassword);
            return newEmployee;
        } else {
            // Update existing employee
            existingEmployee.setFirstName((String) data.get("firstName"));
            existingEmployee.setLastName((String) data.get("lastName"));
            existingEmployee.setDepartment((String) data.get("department"));
            existingEmployee.setRole(position); // Set job title in role field
            existingEmployee.setPhoneNumber((String) data.get("phoneNumber"));
            existingEmployee.setHireDate((String) data.get("hireDate"));
            
            // Set salary
            if (data.get("salary") != null) {
                Number salaryNum = (Number) data.get("salary");
                existingEmployee.setSalary(new BigDecimal(salaryNum.toString()));
            }
            
            // Update password only if provided
            if (data.get("password") != null && !((String) data.get("password")).trim().isEmpty()) {
                existingEmployee.setPassword(passwordEncoder.encode((String) data.get("password")));
            }
            
            return employeeRepository.save(existingEmployee);
        }
    }

    /**
     * Helper method to create new employee when changing type
     */
    private Employee createEmployeeFromTypeChange(Map<String, Object> data, String userRole, 
                                                String existingUsername, String existingEmail, String existingPassword) {
        Employee employee;
        
        // Create the appropriate type based on userRole
        switch (userRole) {
            case "ADMIN":
                employee = new Admin();
                break;
            case "MANAGER":
                employee = new Manager();
                break;
            case "EMPLOYEE":
            default:
                employee = new Employee();
                break;
        }
        
        // Set common fields
        employee.setFirstName((String) data.get("firstName"));
        employee.setLastName((String) data.get("lastName"));
        employee.setDepartment((String) data.get("department"));
        employee.setRole((String) data.get("position")); // Set job title in role field
        employee.setPhoneNumber((String) data.get("phoneNumber"));
        employee.setHireDate((String) data.get("hireDate"));
        
        // Set salary
        if (data.get("salary") != null) {
            Number salaryNum = (Number) data.get("salary");
            employee.setSalary(new BigDecimal(salaryNum.toString()));
        }
        
        // Keep existing username and email
        employee.setUsername(existingUsername);
        employee.setEmail(existingEmail);
        
        // Keep existing password or update if provided
        if (data.get("password") != null && !((String) data.get("password")).trim().isEmpty()) {
            employee.setPassword(passwordEncoder.encode((String) data.get("password")));
        } else {
            employee.setPassword(existingPassword);
        }
        
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.getUsersByDepartment(department).stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    public Employee grantEmployeeAccess(Integer id) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employee.grantAccess();
        return employeeRepository.save(employee);
    }

    public Employee revokeEmployeeAccess(Integer id) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employee.revokeAccess();
        return employeeRepository.save(employee);
    }
}
