package com.dmcdesigns.capstone.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmcdesigns.capstone.Entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findById(long id);
    Department findByName(String name);
    boolean existsById(long id);
    boolean existsByName(String name);
    void deleteById(long id);
    void deleteByName(String name);

    // Get all departments sorted by name using @Query
    @Query("SELECT d FROM Department d ORDER BY d.name ASC")
    List<Department> findAllDepartmentsSortedByName();

    // Search departments by partial name or description using @Query
    @Query("SELECT d FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Department> searchDepartmentsByText(String searchTerm);

    // Count departments using @Query
    @Query("SELECT COUNT(d) FROM Department d")
    Long getTotalDepartmentCount();

    // Find departments with specific name pattern using @Query
    @Query("SELECT d FROM Department d WHERE d.name LIKE ?1")
    List<Department> findDepartmentsByNamePattern(String namePattern);

    // Paginated search methods
    @Query("SELECT d FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Department> searchDepartments(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT d FROM Department d ORDER BY d.name ASC")
    Page<Department> findAllDepartmentsPaginated(Pageable pageable);

    @Query("SELECT d FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Department> searchDepartmentsByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT d FROM Department d WHERE LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Department> searchDepartmentsByDescription(@Param("description") String description, Pageable pageable);

}
