package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.Project;
import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.ProjectRepository;
import com.dmcdesigns.capstone.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Project management operations.
 * Handles business logic for project CRUD operations, employee assignments,
 * project workflow management, and reporting.
 * 
 * @author DMC Designs
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // ============ Basic CRUD Operations ============

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Integer id) {
        return projectRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findAllByStatus(status);
    }

    public List<Project> getProjectsByDepartment(String department) {
        return projectRepository.findAllByDepartment(department);
    }

    public List<Project> getProjectsByPriority(String priority) {
        return projectRepository.findAllByPriority(priority);
    }

    public List<Project> getProjectsByManagerId(Integer managerId) {
        return projectRepository.findAllByProjectManagerId(managerId);
    }

    public List<Project> getProjectsByEmployeeId(Integer employeeId) {
        return projectRepository.findProjectsByEmployeeId(employeeId);
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findActiveProjects();
    }

    public List<Project> getCompletedProjects() {
        return projectRepository.findCompletedProjects();
    }

    public List<Project> getProjectsOverBudget() {
        return projectRepository.findProjectsOverBudget();
    }

    public List<Project> getProjectsWithoutAssignedEmployees() {
        return projectRepository.findProjectsWithoutAssignedEmployees();
    }

    public Project createProject(Project project) {
        // Validate project manager if provided
        if (project.getProjectManager() != null) {
            User manager = userRepository.findById(project.getProjectManager().getId())
                .orElseThrow(() -> new RuntimeException("Project manager not found with ID: " + project.getProjectManager().getId()));
        }

        // Set default values if not provided
        if (project.getStartDate() == null || project.getStartDate().isEmpty()) {
            project.setStartDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Validate assigned employees exist
        if (project.getAssignedEmployees() != null && !project.getAssignedEmployees().isEmpty()) {
            for (User employee : project.getAssignedEmployees()) {
                userRepository.findById(employee.getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employee.getId()));
            }
        }

        return projectRepository.save(project);
    }

    public Project updateProject(Integer id, Project projectDetails) {
        return projectRepository.findById(id)
            .map(project -> {
                // Only allow updates if project is not completed or cancelled
                if ("COMPLETED".equals(project.getStatus()) || "CANCELLED".equals(project.getStatus())) {
                    throw new RuntimeException("Cannot update project with status: " + project.getStatus());
                }

                if (projectDetails.getName() != null) {
                    project.setName(projectDetails.getName());
                }
                if (projectDetails.getDescription() != null) {
                    project.setDescription(projectDetails.getDescription());
                }
                if (projectDetails.getStartDate() != null) {
                    project.setStartDate(projectDetails.getStartDate());
                }
                if (projectDetails.getEndDate() != null) {
                    project.setEndDate(projectDetails.getEndDate());
                }
                if (projectDetails.getStatus() != null) {
                    project.setStatus(projectDetails.getStatus());
                }
                if (projectDetails.getPriority() != null) {
                    project.setPriority(projectDetails.getPriority());
                }
                if (projectDetails.getDepartment() != null) {
                    project.setDepartment(projectDetails.getDepartment());
                }
                if (projectDetails.getBudget() != null) {
                    project.setBudget(projectDetails.getBudget());
                }
                if (projectDetails.getBudgetUsed() != null) {
                    project.setBudgetUsed(projectDetails.getBudgetUsed());
                }
                if (projectDetails.getProgressPercentage() >= 0) {
                    project.setProgressPercentage(projectDetails.getProgressPercentage());
                }
                if (projectDetails.getProjectManager() != null) {
                    // Validate new project manager exists
                    User manager = userRepository.findById(projectDetails.getProjectManager().getId())
                        .orElseThrow(() -> new RuntimeException("Project manager not found"));
                    project.setProjectManager(manager);
                }

                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public Project assignEmployee(Integer projectId, Integer employeeId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        // Check if project is in a state that allows assignment
        if ("COMPLETED".equals(project.getStatus()) || "CANCELLED".equals(project.getStatus())) {
            throw new RuntimeException("Cannot assign employees to a " + project.getStatus() + " project");
        }

        project.addEmployee(employee);
        return projectRepository.save(project);
    }

    public Project removeEmployee(Integer projectId, Integer employeeId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        // Check if project is in a state that allows removal
        if ("COMPLETED".equals(project.getStatus()) || "CANCELLED".equals(project.getStatus())) {
            throw new RuntimeException("Cannot remove employees from a " + project.getStatus() + " project");
        }

        project.removeEmployee(employee);
        return projectRepository.save(project);
    }

    public Project updateProjectProgress(Integer id, int progressPercentage) {
        return projectRepository.findById(id)
            .map(project -> {
                if (progressPercentage < 0 || progressPercentage > 100) {
                    throw new RuntimeException("Progress percentage must be between 0 and 100");
                }
                
                project.setProgressPercentage(progressPercentage);
                
                // Auto-update status based on progress
                if (progressPercentage == 100 && !"COMPLETED".equals(project.getStatus())) {
                    project.setStatus("COMPLETED");
                } else if (progressPercentage > 0 && "PLANNING".equals(project.getStatus())) {
                    project.setStatus("ACTIVE");
                }
                
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public Project addMilestone(Integer projectId, String milestone) {
        return projectRepository.findById(projectId)
            .map(project -> {
                project.addMilestone(milestone);
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
    }

    public Project addResource(Integer projectId, String resource) {
        return projectRepository.findById(projectId)
            .map(project -> {
                project.addResource(resource);
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
    }

    public Project addRisk(Integer projectId, String risk) {
        return projectRepository.findById(projectId)
            .map(project -> {
                project.addRisk(risk);
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
    }

    public Project updateBudgetUsed(Integer id, BigDecimal budgetUsed) {
        return projectRepository.findById(id)
            .map(project -> {
                if (budgetUsed.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Budget used cannot be negative");
                }
                project.setBudgetUsed(budgetUsed);
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public Project activateProject(Integer id) {
        return projectRepository.findById(id)
            .map(project -> {
                if (!"PLANNING".equals(project.getStatus())) {
                    throw new RuntimeException("Only projects in PLANNING status can be activated");
                }
                project.setStatus("ACTIVE");
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public Project completeProject(Integer id) {
        return projectRepository.findById(id)
            .map(project -> {
                if (!"ACTIVE".equals(project.getStatus())) {
                    throw new RuntimeException("Only active projects can be completed");
                }
                project.setStatus("COMPLETED");
                project.setProgressPercentage(100);
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public Project pauseProject(Integer id) {
        return projectRepository.findById(id)
            .map(project -> {
                if (!"ACTIVE".equals(project.getStatus())) {
                    throw new RuntimeException("Only active projects can be paused");
                }
                project.setStatus("ON_HOLD");
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public Project cancelProject(Integer id) {
        return projectRepository.findById(id)
            .map(project -> {
                if ("COMPLETED".equals(project.getStatus()) || "CANCELLED".equals(project.getStatus())) {
                    throw new RuntimeException("Cannot cancel a " + project.getStatus() + " project");
                }
                project.setStatus("CANCELLED");
                return projectRepository.save(project);
            })
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public void deleteProject(Integer id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
        
        // Only allow deletion of projects in planning or cancelled status
        if (!"PLANNING".equals(project.getStatus()) && !"CANCELLED".equals(project.getStatus())) {
            throw new RuntimeException("Only projects in PLANNING or CANCELLED status can be deleted");
        }
        
        projectRepository.deleteById(id);
    }

    public Long getProjectCountByStatus(String status) {
        return projectRepository.countByStatus(status);
    }

    public Long getProjectCountByDepartment(String department) {
        return projectRepository.countByDepartment(department);
    }

    public Double getAverageProgressByDepartment(String department) {
        return projectRepository.getAverageProgressByDepartment(department);
    }

    public BigDecimal getTotalBudgetByDepartment(String department) {
        return projectRepository.getTotalBudgetByDepartment(department);
    }

    public BigDecimal getTotalBudgetUsedByDepartment(String department) {
        return projectRepository.getTotalBudgetUsedByDepartment(department);
    }

    public List<Project> getProjectsByDateRange(String startDate, String endDate) {
        return projectRepository.findProjectsByDateRange(startDate, endDate);
    }

    public List<Project> getProjectsByBudgetRange(BigDecimal minBudget, BigDecimal maxBudget) {
        return projectRepository.findProjectsByBudgetRange(minBudget, maxBudget);
    }

    public List<Project> getProjectsByProgressRange(int minProgress, int maxProgress) {
        return projectRepository.findProjectsByProgressRange(minProgress, maxProgress);
    }

    public Long getEmployeeProjectCount(Integer employeeId) {
        return projectRepository.countProjectsByEmployeeId(employeeId);
    }

    public List<Project> getProjectsEndingSoon(String endDate) {
        return projectRepository.findProjectsEndingSoon(endDate);
    }

    public List<Project> searchProjectsByName(String name) {
        return projectRepository.findProjectsByNameContaining(name);
    }
}
