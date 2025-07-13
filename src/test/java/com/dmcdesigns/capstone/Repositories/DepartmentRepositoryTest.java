package com.dmcdesigns.capstone.Repositories;

import com.dmcdesigns.capstone.Entities.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department itDepartment;
    private Department hrDepartment;
    private Department financeDepartment;

    @BeforeEach
    void setUp() {
        // Create test departments
        itDepartment = new Department("Information Technology", 
                                    "Responsible for technology infrastructure and software development");
        
        hrDepartment = new Department("Human Resources", 
                                    "Manages employee relations, recruitment, and organizational development");
        
        financeDepartment = new Department("Finance", 
                                         "Handles financial planning, accounting, and budget management");
        
        // Persist test data
        entityManager.persistAndFlush(itDepartment);
        entityManager.persistAndFlush(hrDepartment);
        entityManager.persistAndFlush(financeDepartment);
    }

    @Test
    void testFindByName() {
        // Test finding department by name
        Department foundDepartment = departmentRepository.findByName("Information Technology");
        
        assertThat(foundDepartment).isNotNull();
        assertThat(foundDepartment.getName()).isEqualTo("Information Technology");
        assertThat(foundDepartment.getDescription()).contains("technology infrastructure");
    }

    @Test
    void testExistsByName() {
        // Test checking if department exists by name
        assertThat(departmentRepository.existsByName("Information Technology")).isTrue();
        assertThat(departmentRepository.existsByName("Marketing")).isFalse();
    }

    @Test
    void testFindAllDepartmentsSortedByName() {
        // Test finding all departments sorted by name
        List<Department> sortedDepartments = departmentRepository.findAllDepartmentsSortedByName();
        
        assertThat(sortedDepartments).hasSize(3);
        assertThat(sortedDepartments.get(0).getName()).isEqualTo("Finance");
        assertThat(sortedDepartments.get(1).getName()).isEqualTo("Human Resources");
        assertThat(sortedDepartments.get(2).getName()).isEqualTo("Information Technology");
    }

    @Test
    void testSearchDepartmentsByText() {
        // Test searching departments by text in name or description
        List<Department> techDepartments = departmentRepository.searchDepartmentsByText("technology");
        List<Department> managementDepartments = departmentRepository.searchDepartmentsByText("management");
        
        assertThat(techDepartments).hasSize(1);
        assertThat(techDepartments.get(0).getName()).isEqualTo("Information Technology");
        
        assertThat(managementDepartments).hasSize(1);
        assertThat(managementDepartments.get(0).getName()).isEqualTo("Finance");
    }

    @Test
    void testGetTotalDepartmentCount() {
        // Test counting total departments
        Long totalCount = departmentRepository.getTotalDepartmentCount();
        
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    void testFindDepartmentsByNamePattern() {
        // Test finding departments by name pattern
        List<Department> humanDepartments = departmentRepository.findDepartmentsByNamePattern("Human%");
        List<Department> financeDepartments = departmentRepository.findDepartmentsByNamePattern("Finance%");
        
        assertThat(humanDepartments).hasSize(1);
        assertThat(humanDepartments.get(0).getName()).isEqualTo("Human Resources");
        
        assertThat(financeDepartments).hasSize(1);
        assertThat(financeDepartments.get(0).getName()).isEqualTo("Finance");
    }

    @Test
    void testBasicCrudOperations() {
        // Create
        Department newDepartment = new Department("Marketing", 
                                                "Responsible for brand promotion and customer acquisition");
        Department savedDepartment = departmentRepository.save(newDepartment);
        
        assertThat(savedDepartment.getId()).isNotNull();
        assertThat(savedDepartment.getName()).isEqualTo("Marketing");
        
        // Read
        Department foundDepartment = departmentRepository.findById((long) savedDepartment.getId());
        assertThat(foundDepartment).isNotNull();
        assertThat(foundDepartment.getDescription()).contains("brand promotion");
        
        // Update
        savedDepartment.setDescription("Updated description for marketing activities and customer engagement");
        Department updatedDepartment = departmentRepository.save(savedDepartment);
        assertThat(updatedDepartment.getDescription()).contains("customer engagement");
        
        // Delete
        departmentRepository.delete(updatedDepartment);
        Department deletedDepartment = departmentRepository.findById((long) savedDepartment.getId());
        assertThat(deletedDepartment).isNull();
    }

    @Test
    void testFindAll() {
        // Test finding all departments
        List<Department> allDepartments = departmentRepository.findAll();
        
        assertThat(allDepartments).hasSize(3);
        assertThat(allDepartments).extracting(Department::getName)
                                  .containsExactlyInAnyOrder("Information Technology", "Human Resources", "Finance");
    }

    @Test
    void testCount() {
        // Test counting all departments
        long departmentCount = departmentRepository.count();
        
        assertThat(departmentCount).isEqualTo(3);
    }

    @Test
    void testDepartmentNameAndDescription() {
        // Test department setters and getters
        Department department = departmentRepository.findByName("Human Resources");
        
        assertThat(department.getName()).isEqualTo("Human Resources");
        assertThat(department.getDescription()).contains("employee relations");
        
        // Update name and description
        department.setName("People Operations");
        department.setDescription("Modern HR approach focusing on employee experience and culture");
        departmentRepository.save(department);
        
        Department updatedDepartment = departmentRepository.findById((long) department.getId());
        assertThat(updatedDepartment.getName()).isEqualTo("People Operations");
        assertThat(updatedDepartment.getDescription()).contains("employee experience");
    }

    @Test
    void testToStringMethod() {
        // Test the toString method
        Department department = departmentRepository.findByName("Information Technology");
        String departmentString = department.toString();
        
        assertThat(departmentString).contains("Information Technology");
        assertThat(departmentString).contains("technology infrastructure");
        assertThat(departmentString).contains("Department{");
    }

    @Test
    void testSearchCaseInsensitive() {
        // Test case-insensitive search
        List<Department> upperCaseSearch = departmentRepository.searchDepartmentsByText("TECHNOLOGY");
        List<Department> lowerCaseSearch = departmentRepository.searchDepartmentsByText("technology");
        List<Department> mixedCaseSearch = departmentRepository.searchDepartmentsByText("Technology");
        
        assertThat(upperCaseSearch).hasSize(1);
        assertThat(lowerCaseSearch).hasSize(1);
        assertThat(mixedCaseSearch).hasSize(1);
        
        assertThat(upperCaseSearch.get(0).getName()).isEqualTo("Information Technology");
        assertThat(lowerCaseSearch.get(0).getName()).isEqualTo("Information Technology");
        assertThat(mixedCaseSearch.get(0).getName()).isEqualTo("Information Technology");
    }

    @Test
    void testPartialNameSearch() {
        // Test partial name matching
        List<Department> infoSearch = departmentRepository.searchDepartmentsByText("Information");
        List<Department> humanSearch = departmentRepository.searchDepartmentsByText("Human");
        List<Department> resourcesSearch = departmentRepository.searchDepartmentsByText("Resources");
        
        assertThat(infoSearch).hasSize(1);
        assertThat(infoSearch.get(0).getName()).isEqualTo("Information Technology");
        
        assertThat(humanSearch).hasSize(1);
        assertThat(humanSearch.get(0).getName()).isEqualTo("Human Resources");
        
        assertThat(resourcesSearch).hasSize(1);
        assertThat(resourcesSearch.get(0).getName()).isEqualTo("Human Resources");
    }

    @Test
    void testDeleteByName() {
        // Test deleting department by name
        assertThat(departmentRepository.existsByName("Finance")).isTrue();
        
        departmentRepository.deleteByName("Finance");
        
        assertThat(departmentRepository.existsByName("Finance")).isFalse();
        assertThat(departmentRepository.count()).isEqualTo(2);
    }

    @Test
    void testFindByIdLong() {
        // Test finding department by long id (method signature uses long)
        Department department = departmentRepository.findByName("Information Technology");
        Department foundDepartment = departmentRepository.findById((long) department.getId());
        
        assertThat(foundDepartment).isNotNull();
        assertThat(foundDepartment.getName()).isEqualTo("Information Technology");
    }

    @Test
    void testExistsByIdLong() {
        // Test checking existence by long id
        Department department = departmentRepository.findByName("Human Resources");
        
        assertThat(departmentRepository.existsById((long) department.getId())).isTrue();
        assertThat(departmentRepository.existsById(99999L)).isFalse();
    }
}
