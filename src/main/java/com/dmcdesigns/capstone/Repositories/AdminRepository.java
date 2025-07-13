package com.dmcdesigns.capstone.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dmcdesigns.capstone.Entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    // Find admin by username
    @Query("SELECT a FROM Admin a WHERE a.username = ?1")
    Admin findByUsername(String username);

    // Find admin by email
    @Query("SELECT a FROM Admin a WHERE a.email = ?1")
    Admin findByEmail(String email);

    // Check if admin exists by username
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Admin a WHERE a.username = ?1")
    boolean existsByUsername(String username);

    // Check if admin exists by email
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Admin a WHERE a.email = ?1")
    boolean existsByEmail(String email);
    
    // Get all admins
    @Query("SELECT a FROM Admin a")
    List<Admin> getAllAdmins();
}
