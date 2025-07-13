package com.dmcdesigns.capstone.Repositories;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Manager;
import com.dmcdesigns.capstone.Entities.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private Employee testEmployee1;
    private Employee testEmployee2;
    private Manager testManager;
    private Project project1;
    private Project project2;
    private Project project3;

    @BeforeEach
    void setUp() {
        // Create test users
        testEmployee1 = new Employee("John", "Doe", "john.doe@company.com", 
                                   "555-1111", "john.doe", "password123", "IT");
        testEmployee2 = new Employee("Jane", "Smith", "jane.smith@company.com", 
                                   "555-2222", "jane.smith", "password456", "HR");
        testManager = new Manager("Bob", "Manager", "bob.manager@company.com", 
                                "555-3333", "bob.manager", "password789", "IT");
        
        entityManager.persistAndFlush(testEmployee1);
        entityManager.persistAndFlush(testEmployee2);
        entityManager.persistAndFlush(testManager);

        // Create test projects
        project1 = new Project();
        project1.setName("Project Alpha");
        project1.setDescription("First test project");
        project1.setStartDate("2024-01-01");
        project1.setEndDate("2024-06-01");
        project1.setStatus("ACTIVE");
        project1.setPriority("HIGH");
        project1.setDepartment("IT");
        project1.setBudget(new BigDecimal("100000.00"));
        project1.setBudgetUsed(new BigDecimal("50000.00"));
        project1.setProgressPercentage(50);
        project1.setProjectManager(testManager);
        project1.addEmployee(testEmployee1);
        project1.addMilestone("Phase 1 Complete");
        project1.addResource("Development Team");
        project1.addRisk("Budget overrun");

        project2 = new Project();
        project2.setName("Project Beta");
        project2.setDescription("Second test project");
        project2.setStartDate("2024-02-01");
        project2.setEndDate("2024-08-01");
        project2.setStatus("PLANNING");
        project2.setPriority("MEDIUM");
        project2.setDepartment("HR");
        project2.setBudget(new BigDecimal("75000.00"));
        project2.setBudgetUsed(new BigDecimal("10000.00"));
        project2.setProgressPercentage(10);
        project2.addEmployee(testEmployee2);
        project2.addMilestone("Requirements Gathering");
        project2.addResource("HR Team");

        project3 = new Project();
        project3.setName("Project Gamma");
        project3.setDescription("Third test project");
        project3.setStartDate("2024-03-01");
        project3.setEndDate("2024-12-01");
        project3.setStatus("COMPLETED");
        project3.setPriority("LOW");
        project3.setDepartment("IT");
        project3.setBudget(new BigDecimal("200000.00"));
        project3.setBudgetUsed(new BigDecimal("180000.00"));
        project3.setProgressPercentage(100);
        project3.setProjectManager(testManager);
        project3.addEmployee(testEmployee1);
        project3.addEmployee(testEmployee2);
        project3.addMilestone("Project Complete");

        entityManager.persistAndFlush(project1);
        entityManager.persistAndFlush(project2);
        entityManager.persistAndFlush(project3);
    }

    @Test
    void testFindAllByStatus() {
        List<Project> activeProjects = projectRepository.findAllByStatus("ACTIVE");
        assertThat(activeProjects).hasSize(1);
        assertThat(activeProjects.get(0).getName()).isEqualTo("Project Alpha");

        List<Project> planningProjects = projectRepository.findAllByStatus("PLANNING");
        assertThat(planningProjects).hasSize(1);
        assertThat(planningProjects.get(0).getName()).isEqualTo("Project Beta");
    }

    @Test
    void testFindAllByDepartment() {
        List<Project> itProjects = projectRepository.findAllByDepartment("IT");
        assertThat(itProjects).hasSize(2);

        List<Project> hrProjects = projectRepository.findAllByDepartment("HR");
        assertThat(hrProjects).hasSize(1);
        assertThat(hrProjects.get(0).getName()).isEqualTo("Project Beta");
    }

    @Test
    void testFindAllByPriority() {
        List<Project> highPriorityProjects = projectRepository.findAllByPriority("HIGH");
        assertThat(highPriorityProjects).hasSize(1);
        assertThat(highPriorityProjects.get(0).getName()).isEqualTo("Project Alpha");

        List<Project> mediumPriorityProjects = projectRepository.findAllByPriority("MEDIUM");
        assertThat(mediumPriorityProjects).hasSize(1);
        assertThat(mediumPriorityProjects.get(0).getName()).isEqualTo("Project Beta");
    }

    @Test
    void testFindAllByProjectManagerId() {
        List<Project> managerProjects = projectRepository.findAllByProjectManagerId(testManager.getId());
        assertThat(managerProjects).hasSize(2);
        assertThat(managerProjects).extracting(Project::getName)
            .containsExactlyInAnyOrder("Project Alpha", "Project Gamma");
    }

    @Test
    void testFindActiveProjects() {
        List<Project> activeProjects = projectRepository.findActiveProjects();
        assertThat(activeProjects).hasSize(1);
        assertThat(activeProjects.get(0).getName()).isEqualTo("Project Alpha");
    }

    @Test
    void testFindCompletedProjects() {
        List<Project> completedProjects = projectRepository.findCompletedProjects();
        assertThat(completedProjects).hasSize(1);
        assertThat(completedProjects.get(0).getName()).isEqualTo("Project Gamma");
    }

    @Test
    void testFindProjectsByDateRange() {
        List<Project> projects = projectRepository.findProjectsByDateRange("2024-01-01", "2024-02-15");
        assertThat(projects).hasSize(2);
        assertThat(projects).extracting(Project::getName)
            .containsExactlyInAnyOrder("Project Alpha", "Project Beta");
    }

    @Test
    void testFindProjectsOverBudget() {
        // Create a project over budget
        Project overBudgetProject = new Project();
        overBudgetProject.setName("Over Budget Project");
        overBudgetProject.setStartDate("2024-01-01");
        overBudgetProject.setDepartment("IT");
        overBudgetProject.setBudget(new BigDecimal("50000.00"));
        overBudgetProject.setBudgetUsed(new BigDecimal("60000.00"));
        entityManager.persistAndFlush(overBudgetProject);

        List<Project> overBudgetProjects = projectRepository.findProjectsOverBudget();
        assertThat(overBudgetProjects).hasSize(1);
        assertThat(overBudgetProjects.get(0).getName()).isEqualTo("Over Budget Project");
    }

    @Test
    void testFindProjectsByBudgetRange() {
        List<Project> projects = projectRepository.findProjectsByBudgetRange(
            new BigDecimal("70000.00"), new BigDecimal("150000.00"));
        assertThat(projects).hasSize(2);
        assertThat(projects).extracting(Project::getName)
            .containsExactlyInAnyOrder("Project Alpha", "Project Beta");
    }

    @Test
    void testFindProjectsByProgressRange() {
        List<Project> projects = projectRepository.findProjectsByProgressRange(40, 60);
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Project Alpha");
    }

    @Test
    void testFindProjectsByEmployeeId() {
        List<Project> employee1Projects = projectRepository.findProjectsByEmployeeId(testEmployee1.getId());
        assertThat(employee1Projects).hasSize(2);
        assertThat(employee1Projects).extracting(Project::getName)
            .containsExactlyInAnyOrder("Project Alpha", "Project Gamma");

        List<Project> employee2Projects = projectRepository.findProjectsByEmployeeId(testEmployee2.getId());
        assertThat(employee2Projects).hasSize(2);
        assertThat(employee2Projects).extracting(Project::getName)
            .containsExactlyInAnyOrder("Project Beta", "Project Gamma");
    }

    @Test
    void testCountByStatus() {
        Long activeCount = projectRepository.countByStatus("ACTIVE");
        assertThat(activeCount).isEqualTo(1);

        Long planningCount = projectRepository.countByStatus("PLANNING");
        assertThat(planningCount).isEqualTo(1);

        Long completedCount = projectRepository.countByStatus("COMPLETED");
        assertThat(completedCount).isEqualTo(1);
    }

    @Test
    void testCountByDepartment() {
        Long itCount = projectRepository.countByDepartment("IT");
        assertThat(itCount).isEqualTo(2);

        Long hrCount = projectRepository.countByDepartment("HR");
        assertThat(hrCount).isEqualTo(1);
    }

    @Test
    void testGetAverageProgressByDepartment() {
        Double itAvgProgress = projectRepository.getAverageProgressByDepartment("IT");
        assertThat(itAvgProgress).isEqualTo(75.0); // (50 + 100) / 2

        Double hrAvgProgress = projectRepository.getAverageProgressByDepartment("HR");
        assertThat(hrAvgProgress).isEqualTo(10.0);
    }

    @Test
    void testGetTotalBudgetByDepartment() {
        BigDecimal itTotalBudget = projectRepository.getTotalBudgetByDepartment("IT");
        assertThat(itTotalBudget).isEqualTo(new BigDecimal("300000.00")); // 100000 + 200000

        BigDecimal hrTotalBudget = projectRepository.getTotalBudgetByDepartment("HR");
        assertThat(hrTotalBudget).isEqualTo(new BigDecimal("75000.00"));
    }

    @Test
    void testGetTotalBudgetUsedByDepartment() {
        BigDecimal itBudgetUsed = projectRepository.getTotalBudgetUsedByDepartment("IT");
        assertThat(itBudgetUsed).isEqualTo(new BigDecimal("230000.00")); // 50000 + 180000

        BigDecimal hrBudgetUsed = projectRepository.getTotalBudgetUsedByDepartment("HR");
        assertThat(hrBudgetUsed).isEqualTo(new BigDecimal("10000.00"));
    }

    @Test
    void testFindProjectsEndingSoon() {
        List<Project> endingSoon = projectRepository.findProjectsEndingSoon("2024-07-01");
        assertThat(endingSoon).hasSize(1);
        assertThat(endingSoon.get(0).getName()).isEqualTo("Project Alpha");
    }

    @Test
    void testFindProjectsWithoutAssignedEmployees() {
        // Create a project without employees
        Project unassignedProject = new Project();
        unassignedProject.setName("Unassigned Project");
        unassignedProject.setStartDate("2024-01-01");
        unassignedProject.setDepartment("IT");
        unassignedProject.setStatus("PLANNING");
        entityManager.persistAndFlush(unassignedProject);

        List<Project> unassignedProjects = projectRepository.findProjectsWithoutAssignedEmployees();
        assertThat(unassignedProjects).hasSize(1);
        assertThat(unassignedProjects.get(0).getName()).isEqualTo("Unassigned Project");
    }

    @Test
    void testFindProjectsWithMilestone() {
        List<Project> projectsWithMilestone = projectRepository.findProjectsWithMilestone("Phase 1 Complete");
        assertThat(projectsWithMilestone).hasSize(1);
        assertThat(projectsWithMilestone.get(0).getName()).isEqualTo("Project Alpha");
    }

    @Test
    void testFindProjectsByNameContaining() {
        List<Project> projects = projectRepository.findProjectsByNameContaining("Alpha");
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Project Alpha");

        List<Project> projectsProject = projectRepository.findProjectsByNameContaining("Project");
        assertThat(projectsProject).hasSize(3);
    }

    @Test
    void testFindAllByDepartmentAndStatus() {
        List<Project> itActiveProjects = projectRepository.findAllByDepartmentAndStatus("IT", "ACTIVE");
        assertThat(itActiveProjects).hasSize(1);
        assertThat(itActiveProjects.get(0).getName()).isEqualTo("Project Alpha");

        List<Project> hrPlanningProjects = projectRepository.findAllByDepartmentAndStatus("HR", "PLANNING");
        assertThat(hrPlanningProjects).hasSize(1);
        assertThat(hrPlanningProjects.get(0).getName()).isEqualTo("Project Beta");
    }

    @Test
    void testFindAllByProjectManagerIdAndStatus() {
        List<Project> managerActiveProjects = projectRepository.findAllByProjectManagerIdAndStatus(testManager.getId(), "ACTIVE");
        assertThat(managerActiveProjects).hasSize(1);
        assertThat(managerActiveProjects.get(0).getName()).isEqualTo("Project Alpha");

        List<Project> managerCompletedProjects = projectRepository.findAllByProjectManagerIdAndStatus(testManager.getId(), "COMPLETED");
        assertThat(managerCompletedProjects).hasSize(1);
        assertThat(managerCompletedProjects.get(0).getName()).isEqualTo("Project Gamma");
    }

    @Test
    void testCountProjectsByEmployeeId() {
        Long employee1ProjectCount = projectRepository.countProjectsByEmployeeId(testEmployee1.getId());
        assertThat(employee1ProjectCount).isEqualTo(2);

        Long employee2ProjectCount = projectRepository.countProjectsByEmployeeId(testEmployee2.getId());
        assertThat(employee2ProjectCount).isEqualTo(2);
    }

    @Test
    void testFindAllByPriorityAndStatus() {
        List<Project> highActiveProjects = projectRepository.findAllByPriorityAndStatus("HIGH", "ACTIVE");
        assertThat(highActiveProjects).hasSize(1);
        assertThat(highActiveProjects.get(0).getName()).isEqualTo("Project Alpha");

        List<Project> mediumPlanningProjects = projectRepository.findAllByPriorityAndStatus("MEDIUM", "PLANNING");
        assertThat(mediumPlanningProjects).hasSize(1);
        assertThat(mediumPlanningProjects.get(0).getName()).isEqualTo("Project Beta");
    }

    @Test
    void testProjectEntityMethods() {
        assertThat(project1.getRemainingBudget()).isEqualTo(new BigDecimal("50000.00"));
        assertThat(project1.isOverBudget()).isFalse();
        assertThat(project1.isCompleted()).isFalse();
        assertThat(project1.isActive()).isTrue();

        assertThat(project3.isCompleted()).isTrue();
        assertThat(project3.isActive()).isFalse();
    }

    @Test
    void testProjectReportableInterface() {
        assertThat(project1.getReportTitle()).isEqualTo("Project Report: Project Alpha");
        assertThat(project1.getReportSummary()).contains("Project Alpha");
        assertThat(project1.getReportSummary()).contains("IT");
        assertThat(project1.getReportSummary()).contains("ACTIVE");
        assertThat(project1.getReportSummary()).contains("50%");

        var report = project1.generateReport();
        assertThat(report.get("name")).isEqualTo("Project Alpha");
        assertThat(report.get("status")).isEqualTo("ACTIVE");
        assertThat(report.get("department")).isEqualTo("IT");
        assertThat(report.get("progressPercentage")).isEqualTo(50);
    }

    @Test
    void testProjectSearchableInterface() {
        assertThat(project1.getSearchableFields()).contains("name", "description", "department", "status", "priority");
        assertThat(project1.getSearchableContent()).contains("Project Alpha", "IT", "ACTIVE", "HIGH");
        assertThat(project1.matchesSearch("alpha")).isTrue();
        assertThat(project1.matchesSearch("ACTIVE")).isTrue();
        assertThat(project1.matchesSearch("nonexistent")).isFalse();
    }
}
