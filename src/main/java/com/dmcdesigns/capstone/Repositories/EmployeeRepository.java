package com.dmcdesigns.capstone.Repositories;

import com.dmcdesigns.capstone.Entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EmployeeRepository extends UserRepository {
    // Find employee by username
    Employee findByUsername(String username);

    // Find employee by email
    Employee findByEmail(String email);

    // Check if employee exists by username
    boolean existsByUsername(String username);
    
    // Check if employee exists by email
    boolean existsByEmail(String email);

    // Paginated employee-specific search methods
    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(e.role) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Employee> searchEmployees(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.department = :department")
    Page<Employee> findEmployeesByDepartment(@Param("department") String department, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.hasAccess = :hasAccess")
    Page<Employee> findEmployeesByAccessStatus(@Param("hasAccess") boolean hasAccess, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.role) LIKE LOWER(CONCAT('%', :role, '%'))")
    Page<Employee> findEmployeesByRole(@Param("role") String role, Pageable pageable);

    // Find all employees (override the inherited findAll to return Employee objects)
    @Query("SELECT e FROM Employee e")
    List<Employee> findAllEmployees();

}
