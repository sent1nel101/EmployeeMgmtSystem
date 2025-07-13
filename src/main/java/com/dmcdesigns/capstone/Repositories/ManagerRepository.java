package com.dmcdesigns.capstone.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.dmcdesigns.capstone.Entities.Manager;

public interface ManagerRepository extends UserRepository {
    // Find manager by username
    Manager findByUsername(String username);

    // Find manager by email  
    Manager findByEmail(String email);

    // Check if manager exists by username
    boolean existsByUsername(String username);
    
    // Check if manager exists by email
    boolean existsByEmail(String email);

    // Get all managers by department
    @Query("SELECT m FROM Manager m WHERE m.department = ?1")
    List<Manager> findByDepartment(String department);

    // Get managers with access
    @Query("SELECT m FROM Manager m WHERE m.hasAccess = true")
    List<Manager> findManagersWithAccess();

    // Get managers without access
    @Query("SELECT m FROM Manager m WHERE m.hasAccess = false")
    List<Manager> findManagersWithoutAccess();
}
