package com.dmcdesigns.capstone.Repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dmcdesigns.capstone.Entities.Project;

/**
 * Repository interface for Project entity operations.
 * Extends JpaRepository to provide standard CRUD operations plus custom queries
 * for project filtering, searching, and reporting.
 * 
 * @author DMC Designs
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    boolean existsById(long id);

    // ============ Basic Filtering Methods ============
    
    List<Project> findAllByStatus(String status);
    List<Project> findAllByDepartment(String department);
    List<Project> findAllByPriority(String priority);
    List<Project> findAllByProjectManagerId(int managerId);

    // ============ Status-Specific Queries ============
    
    @Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE'")
    List<Project> findActiveProjects();

    @Query("SELECT p FROM Project p WHERE p.status = 'COMPLETED'")
    List<Project> findCompletedProjects();

    // Find projects by date range
    @Query("SELECT p FROM Project p WHERE p.startDate BETWEEN ?1 AND ?2")
    List<Project> findProjectsByDateRange(String startDate, String endDate);

    // Find projects over budget
    @Query("SELECT p FROM Project p WHERE p.budgetUsed > p.budget")
    List<Project> findProjectsOverBudget();

    // Find projects by budget range
    @Query("SELECT p FROM Project p WHERE p.budget BETWEEN ?1 AND ?2")
    List<Project> findProjectsByBudgetRange(BigDecimal minBudget, BigDecimal maxBudget);

    // Find projects by progress percentage range
    @Query("SELECT p FROM Project p WHERE p.progressPercentage BETWEEN ?1 AND ?2")
    List<Project> findProjectsByProgressRange(int minProgress, int maxProgress);

    // Get projects assigned to a specific employee
    @Query("SELECT p FROM Project p JOIN p.assignedEmployees e WHERE e.id = ?1")
    List<Project> findProjectsByEmployeeId(int employeeId);

    // Get project count by status
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = ?1")
    Long countByStatus(String status);

    // Get project count by department
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department = ?1")
    Long countByDepartment(String department);

    // Get average project progress by department
    @Query("SELECT AVG(p.progressPercentage) FROM Project p WHERE p.department = ?1")
    Double getAverageProgressByDepartment(String department);

    // Get total budget by department
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.department = ?1")
    BigDecimal getTotalBudgetByDepartment(String department);

    // Get total budget used by department
    @Query("SELECT SUM(p.budgetUsed) FROM Project p WHERE p.department = ?1")
    BigDecimal getTotalBudgetUsedByDepartment(String department);

    // Find projects ending soon (within specified date)
    @Query("SELECT p FROM Project p WHERE p.endDate <= ?1 AND p.status IN ('ACTIVE', 'PLANNING')")
    List<Project> findProjectsEndingSoon(String endDate);

    // Find projects without assigned employees
    @Query("SELECT p FROM Project p WHERE p.assignedEmployees IS EMPTY")
    List<Project> findProjectsWithoutAssignedEmployees();

    // Find projects with specific milestone
    @Query("SELECT p FROM Project p WHERE ?1 MEMBER OF p.milestones")
    List<Project> findProjectsWithMilestone(String milestone);

    // Get top projects by budget
    @Query("SELECT p FROM Project p ORDER BY p.budget DESC")
    List<Project> findTopProjectsByBudget();

    // Find projects by name (case insensitive)
    @Query("SELECT p FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Project> findProjectsByNameContaining(String name);

    // Find projects by department and status
    List<Project> findAllByDepartmentAndStatus(String department, String status);

    // Find projects by manager and status
    List<Project> findAllByProjectManagerIdAndStatus(int managerId, String status);

    // Count projects assigned to an employee
    @Query("SELECT COUNT(p) FROM Project p JOIN p.assignedEmployees e WHERE e.id = ?1")
    Long countProjectsByEmployeeId(int employeeId);

    // Find most recent projects
    @Query("SELECT p FROM Project p ORDER BY p.startDate DESC")
    List<Project> findRecentProjects();

    // Find projects by priority and status
    List<Project> findAllByPriorityAndStatus(String priority, String status);

    // Search methods for SearchController
    @Query("SELECT p FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(p.department) LIKE LOWER(CONCAT('%', ?1, '%'))")
    org.springframework.data.domain.Page<Project> searchProjects(String searchTerm, org.springframework.data.domain.Pageable pageable);

    // Find projects by status with pagination
    org.springframework.data.domain.Page<Project> findProjectsByStatus(String status, org.springframework.data.domain.Pageable pageable);

    // Find projects by active status
    @Query("SELECT p FROM Project p WHERE (:isActive = true AND p.status = 'ACTIVE') OR (:isActive = false AND p.status != 'ACTIVE')")
    org.springframework.data.domain.Page<Project> findProjectsByActiveStatus(@org.springframework.data.repository.query.Param("isActive") boolean isActive, org.springframework.data.domain.Pageable pageable);
}
