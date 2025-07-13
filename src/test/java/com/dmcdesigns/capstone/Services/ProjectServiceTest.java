package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Manager;
import com.dmcdesigns.capstone.Entities.Project;
import com.dmcdesigns.capstone.Repositories.ProjectRepository;
import com.dmcdesigns.capstone.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Employee testEmployee;
    private Manager testManager;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("John", "Doe", "john.doe@company.com", 
                                  "555-1111", "john.doe", "password123", "IT");
        // Note: IDs will be set by JPA when persisted

        testManager = new Manager("Bob", "Manager", "bob.manager@company.com", 
                                "555-3333", "bob.manager", "password789", "IT");

        testProject = new Project();
        testProject.setId(1);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStartDate("2024-01-01");
        testProject.setStatus("PLANNING");
        testProject.setPriority("MEDIUM");
        testProject.setDepartment("IT");
        testProject.setBudget(new BigDecimal("100000.00"));
        testProject.setBudgetUsed(new BigDecimal("25000.00"));
        testProject.setProgressPercentage(25);
        testProject.setProjectManager(testManager);
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = List.of(testProject);
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getAllProjects();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Project");
        verify(projectRepository).findAll();
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        Optional<Project> result = projectService.getProjectById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Project");
        verify(projectRepository).findById(1);
    }

    @Test
    void testCreateProject() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testManager));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.createProject(testProject);

        assertThat(result.getName()).isEqualTo("Test Project");
        verify(userRepository).findById(testManager.getId());
        verify(projectRepository).save(testProject);
    }

    @Test
    void testCreateProjectWithInvalidManager() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.createProject(testProject))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Project manager not found");

        verify(userRepository).findById(testManager.getId());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testUpdateProject() {
        Project updatedDetails = new Project();
        updatedDetails.setName("Updated Project");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setProgressPercentage(50);

        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.updateProject(1, updatedDetails);

        assertThat(result.getName()).isEqualTo("Updated Project");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testUpdateCompletedProject() {
        testProject.setStatus("COMPLETED");
        Project updatedDetails = new Project();
        updatedDetails.setName("Updated Project");

        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.updateProject(1, updatedDetails))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Cannot update project with status: COMPLETED");

        verify(projectRepository).findById(1);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAssignEmployee() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(testEmployee.getId())).thenReturn(Optional.of(testEmployee));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.assignEmployee(1, testEmployee.getId());

        assertThat(result.getAssignedEmployees()).contains(testEmployee);
        verify(projectRepository).findById(1);
        verify(userRepository).findById(testEmployee.getId());
        verify(projectRepository).save(testProject);
    }

    @Test
    void testAssignEmployeeToCompletedProject() {
        testProject.setStatus("COMPLETED");
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(testEmployee.getId())).thenReturn(Optional.of(testEmployee));

        assertThatThrownBy(() -> projectService.assignEmployee(1, testEmployee.getId()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Cannot assign employees to a COMPLETED project");

        verify(projectRepository).findById(1);
        verify(userRepository).findById(testEmployee.getId());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testRemoveEmployee() {
        testProject.addEmployee(testEmployee);
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(testEmployee.getId())).thenReturn(Optional.of(testEmployee));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.removeEmployee(1, testEmployee.getId());

        assertThat(result.getAssignedEmployees()).doesNotContain(testEmployee);
        verify(projectRepository).findById(1);
        verify(userRepository).findById(testEmployee.getId());
        verify(projectRepository).save(testProject);
    }

    @Test
    void testUpdateProjectProgress() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.updateProjectProgress(1, 75);

        assertThat(result.getProgressPercentage()).isEqualTo(75);
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testUpdateProjectProgressTo100() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.updateProjectProgress(1, 100);

        assertThat(result.getProgressPercentage()).isEqualTo(100);
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testUpdateProjectProgressInvalidRange() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.updateProjectProgress(1, 150))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Progress percentage must be between 0 and 100");

        verify(projectRepository).findById(1);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAddMilestone() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.addMilestone(1, "Phase 1 Complete");

        assertThat(result.getMilestones()).contains("Phase 1 Complete");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testAddResource() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.addResource(1, "Development Team");

        assertThat(result.getResources()).contains("Development Team");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testAddRisk() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.addRisk(1, "Budget overrun");

        assertThat(result.getRisks()).contains("Budget overrun");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testUpdateBudgetUsed() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        BigDecimal newBudgetUsed = new BigDecimal("50000.00");
        Project result = projectService.updateBudgetUsed(1, newBudgetUsed);

        assertThat(result.getBudgetUsed()).isEqualTo(newBudgetUsed);
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testUpdateBudgetUsedNegative() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        BigDecimal negativeBudget = new BigDecimal("-1000.00");
        assertThatThrownBy(() -> projectService.updateBudgetUsed(1, negativeBudget))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Budget used cannot be negative");

        verify(projectRepository).findById(1);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testActivateProject() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.activateProject(1);

        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testActivateNonPlanningProject() {
        testProject.setStatus("ACTIVE");
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.activateProject(1))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Only projects in PLANNING status can be activated");

        verify(projectRepository).findById(1);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testCompleteProject() {
        testProject.setStatus("ACTIVE");
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.completeProject(1);

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        assertThat(result.getProgressPercentage()).isEqualTo(100);
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testPauseProject() {
        testProject.setStatus("ACTIVE");
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.pauseProject(1);

        assertThat(result.getStatus()).isEqualTo("ON_HOLD");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testCancelProject() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.cancelProject(1);

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
        verify(projectRepository).findById(1);
        verify(projectRepository).save(testProject);
    }

    @Test
    void testDeleteProject() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        projectService.deleteProject(1);

        verify(projectRepository).findById(1);
        verify(projectRepository).deleteById(1);
    }

    @Test
    void testDeleteActiveProject() {
        testProject.setStatus("ACTIVE");
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        assertThatThrownBy(() -> projectService.deleteProject(1))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Only projects in PLANNING or CANCELLED status can be deleted");

        verify(projectRepository).findById(1);
        verify(projectRepository, never()).deleteById(anyInt());
    }

    @Test
    void testGetProjectsByStatus() {
        List<Project> projects = List.of(testProject);
        when(projectRepository.findAllByStatus("PLANNING")).thenReturn(projects);

        List<Project> result = projectService.getProjectsByStatus("PLANNING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PLANNING");
        verify(projectRepository).findAllByStatus("PLANNING");
    }

    @Test
    void testGetProjectsByDepartment() {
        List<Project> projects = List.of(testProject);
        when(projectRepository.findAllByDepartment("IT")).thenReturn(projects);

        List<Project> result = projectService.getProjectsByDepartment("IT");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartment()).isEqualTo("IT");
        verify(projectRepository).findAllByDepartment("IT");
    }

    @Test
    void testGetProjectsByEmployeeId() {
        List<Project> projects = List.of(testProject);
        when(projectRepository.findProjectsByEmployeeId(testEmployee.getId())).thenReturn(projects);

        List<Project> result = projectService.getProjectsByEmployeeId(testEmployee.getId());

        assertThat(result).hasSize(1);
        verify(projectRepository).findProjectsByEmployeeId(testEmployee.getId());
    }

    @Test
    void testGetProjectCountByStatus() {
        when(projectRepository.countByStatus("ACTIVE")).thenReturn(5L);

        Long result = projectService.getProjectCountByStatus("ACTIVE");

        assertThat(result).isEqualTo(5L);
        verify(projectRepository).countByStatus("ACTIVE");
    }

    @Test
    void testGetAverageProgressByDepartment() {
        when(projectRepository.getAverageProgressByDepartment("IT")).thenReturn(75.0);

        Double result = projectService.getAverageProgressByDepartment("IT");

        assertThat(result).isEqualTo(75.0);
        verify(projectRepository).getAverageProgressByDepartment("IT");
    }

    @Test
    void testGetTotalBudgetByDepartment() {
        BigDecimal budget = new BigDecimal("500000.00");
        when(projectRepository.getTotalBudgetByDepartment("IT")).thenReturn(budget);

        BigDecimal result = projectService.getTotalBudgetByDepartment("IT");

        assertThat(result).isEqualTo(budget);
        verify(projectRepository).getTotalBudgetByDepartment("IT");
    }

    @Test
    void testGetEmployeeProjectCount() {
        when(projectRepository.countProjectsByEmployeeId(testEmployee.getId())).thenReturn(3L);

        Long result = projectService.getEmployeeProjectCount(testEmployee.getId());

        assertThat(result).isEqualTo(3L);
        verify(projectRepository).countProjectsByEmployeeId(testEmployee.getId());
    }

    @Test
    void testSearchProjectsByName() {
        List<Project> projects = List.of(testProject);
        when(projectRepository.findProjectsByNameContaining("Test")).thenReturn(projects);

        List<Project> result = projectService.searchProjectsByName("Test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");
        verify(projectRepository).findProjectsByNameContaining("Test");
    }
}
