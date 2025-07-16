package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Admin;
import com.dmcdesigns.capstone.Services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Map<String, Object> employeeData) {
        try {
            System.out.println("🔍 Debug: Creating employee with data: " + employeeData);
            Employee createdEmployee = employeeService.createEmployeeFromData(employeeData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (Exception e) {
            System.err.println("❌ Error creating employee: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Integer id, 
                                                 @RequestBody Map<String, Object> employeeData) {
        try {
            System.out.println("🔍 Debug: Updating employee " + id);
            System.out.println("🔍 Debug: Employee data: " + employeeData);
            Employee updatedEmployee = employeeService.updateEmployeeFromData(id, employeeData);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            System.err.println("❌ Error updating employee " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/department/{department}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}/grant-access")
    public ResponseEntity<Employee> grantEmployeeAccess(@PathVariable Integer id) {
        try {
            Employee employee = employeeService.grantEmployeeAccess(id);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/revoke-access")
    public ResponseEntity<Employee> revokeEmployeeAccess(@PathVariable Integer id) {
        try {
            Employee employee = employeeService.revokeEmployeeAccess(id);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<Object>> getEmployeeProjects(@PathVariable Integer id) {
        try {
            // For now, return empty list since project assignment isn't fully implemented
            List<Object> projects = new ArrayList<>();
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/promote-to-admin")
    public ResponseEntity<Employee> promoteToAdmin(@PathVariable Integer id) {
        try {
            Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
            if (employeeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Employee currentEmployee = employeeOpt.get();
            
            // Check if already an admin
            if (currentEmployee instanceof Admin) {
                return ResponseEntity.badRequest().build(); // Already an admin
            }

            // Create new Admin with same details but admin privileges
            Admin newAdmin = new Admin(
                currentEmployee.getFirstName(),
                currentEmployee.getLastName(),
                currentEmployee.getEmail(),
                currentEmployee.getPhoneNumber(),
                currentEmployee.getUsername(),
                currentEmployee.getPassword(), // Keep existing password
                currentEmployee.getDepartment()
            );
            
            // Copy additional employee details
            newAdmin.setHireDate(currentEmployee.getHireDate());
            newAdmin.setSalary(currentEmployee.getSalary());

            // Delete old employee record and save new admin
            employeeService.deleteEmployee(id);
            Employee savedAdmin = employeeService.createEmployee(newAdmin);
            
            return ResponseEntity.ok(savedAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
