package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.Entities.Department;
import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Project;
import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.DepartmentRepository;
import com.dmcdesigns.capstone.Repositories.EmployeeRepository;
import com.dmcdesigns.capstone.Repositories.ProjectRepository;
import com.dmcdesigns.capstone.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Global search across all entities
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> globalSearch(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Map<String, Object> results = new HashMap<>();
        
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        Page<Department> departments = departmentRepository.searchDepartments(searchTerm, pageable);
        Page<Project> projects = projectRepository.searchProjects(searchTerm, pageable);

        results.put("users", users);
        results.put("departments", departments);
        results.put("projects", projects);
        results.put("searchTerm", searchTerm);
        results.put("totalResults", users.getTotalElements() + departments.getTotalElements() + projects.getTotalElements());

        return ResponseEntity.ok(results);
    }

    // Search users
    @GetMapping("/users")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        return ResponseEntity.ok(users);
    }

    // Search employees
    @GetMapping("/employees")
    public ResponseEntity<Page<Employee>> searchEmployees(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // Support both 'q' and 'searchTerm' parameters
        String term = searchTerm != null ? searchTerm : (q != null ? q : "");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employees = employeeRepository.searchEmployees(term, pageable);
        return ResponseEntity.ok(employees);
    }

    // Search departments
    @GetMapping("/departments")
    public ResponseEntity<Page<Department>> searchDepartments(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Department> departments = departmentRepository.searchDepartments(searchTerm, pageable);
        return ResponseEntity.ok(departments);
    }

    // Search projects
    @GetMapping("/projects")
    public ResponseEntity<Page<Project>> searchProjects(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // Support both 'q' and 'searchTerm' parameters
        String term = searchTerm != null ? searchTerm : (q != null ? q : "");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Project> projects = projectRepository.searchProjects(term, pageable);
        return ResponseEntity.ok(projects);
    }

    // Search users by department
    @GetMapping("/users/department/{department}")
    public ResponseEntity<Page<User>> searchUsersByDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userRepository.findUsersByDepartment(department, pageable);
        return ResponseEntity.ok(users);
    }

    // Search employees by department
    @GetMapping("/employees/department/{department}")
    public ResponseEntity<Page<Employee>> searchEmployeesByDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employees = employeeRepository.findEmployeesByDepartment(department, pageable);
        return ResponseEntity.ok(employees);
    }

    // Search user by ID
    @GetMapping("/users/id/{userId}")
    public ResponseEntity<Page<User>> searchUserById(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findUserById(userId, pageable);
        return ResponseEntity.ok(users);
    }

    // Search employees by role
    @GetMapping("/employees/role/{role}")
    public ResponseEntity<Page<Employee>> searchEmployeesByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employees = employeeRepository.findEmployeesByRole(role, pageable);
        return ResponseEntity.ok(employees);
    }

    // Search employees by access status
    @GetMapping("/employees/access/{hasAccess}")
    public ResponseEntity<Page<Employee>> searchEmployeesByAccessStatus(
            @PathVariable boolean hasAccess,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employees = employeeRepository.findEmployeesByAccessStatus(hasAccess, pageable);
        return ResponseEntity.ok(employees);
    }

    // Search projects by status
    @GetMapping("/projects/status/{status}")
    public ResponseEntity<Page<Project>> searchProjectsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Project> projects = projectRepository.findProjectsByStatus(status, pageable);
        return ResponseEntity.ok(projects);
    }

    // Search projects by active status
    @GetMapping("/projects/active/{isActive}")
    public ResponseEntity<Page<Project>> searchProjectsByActiveStatus(
            @PathVariable boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Project> projects = projectRepository.findProjectsByActiveStatus(isActive, pageable);
        return ResponseEntity.ok(projects);
    }

    // Advanced search with multiple filters
    @GetMapping("/advanced")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean hasAccess,
            @RequestParam(required = false) String projectStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Map<String, Object> results = new HashMap<>();

        if (name != null && !name.trim().isEmpty()) {
            Page<User> users = userRepository.searchUsers(name, pageable);
            results.put("users", users);
        }

        if (department != null && !department.trim().isEmpty()) {
            Page<User> usersByDept = userRepository.findUsersByDepartment(department, pageable);
            Page<Department> departments = departmentRepository.searchDepartmentsByName(department, pageable);
            results.put("usersByDepartment", usersByDept);
            results.put("departments", departments);
        }

        if (role != null && !role.trim().isEmpty()) {
            Page<Employee> employees = employeeRepository.findEmployeesByRole(role, pageable);
            results.put("employeesByRole", employees);
        }

        if (hasAccess != null) {
            Page<Employee> employeesByAccess = employeeRepository.findEmployeesByAccessStatus(hasAccess, pageable);
            results.put("employeesByAccess", employeesByAccess);
        }

        if (projectStatus != null && !projectStatus.trim().isEmpty()) {
            Page<Project> projects = projectRepository.findProjectsByStatus(projectStatus, pageable);
            results.put("projectsByStatus", projects);
        }

        return ResponseEntity.ok(results);
    }
}
