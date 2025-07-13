package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.Entities.Department;
import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Department>> getAllDepartmentsSortedByName() {
        List<Department> departments = departmentService.getAllDepartmentsSortedByName();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Integer id) {
        Optional<Department> department = departmentService.getDepartmentById(id);
        return department.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Department> getDepartmentByName(@PathVariable String name) {
        Department department = departmentService.getDepartmentByName(name);
        if (department != null) {
            return ResponseEntity.ok(department);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody Department department) {
        try {
            Department createdDepartment = departmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Integer id, 
                                                     @Valid @RequestBody Department departmentDetails) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
            return ResponseEntity.ok(updatedDepartment);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Department>> searchDepartments(@RequestParam String term) {
        List<Department> departments = departmentService.searchDepartments(term);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/pattern")
    public ResponseEntity<List<Department>> findDepartmentsByNamePattern(@RequestParam String pattern) {
        List<Department> departments = departmentService.findDepartmentsByNamePattern(pattern);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalDepartmentCount() {
        Long count = departmentService.getTotalDepartmentCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> checkDepartmentExists(@PathVariable String name) {
        boolean exists = departmentService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@PathVariable Integer id) {
        try {
            List<Employee> employees = departmentService.getEmployeesByDepartmentId(id);
            return ResponseEntity.ok(employees);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}/employees")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentName(@PathVariable String name) {
        List<Employee> employees = departmentService.getEmployeesByDepartmentName(name);
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/{fromId}/transfer-to/{toId}")
    public ResponseEntity<Department> transferEmployeesBetweenDepartments(
            @PathVariable Integer fromId, 
            @PathVariable Integer toId) {
        try {
            Department targetDepartment = departmentService.transferEmployeesToDepartment(fromId, toId);
            return ResponseEntity.ok(targetDepartment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
