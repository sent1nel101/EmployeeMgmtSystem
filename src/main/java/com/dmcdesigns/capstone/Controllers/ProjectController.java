package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.Entities.Project;
import com.dmcdesigns.capstone.Services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing Project entities.
 * Provides comprehensive CRUD operations and project-specific business logic.
 * 
 * @author DMC Designs
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // ============ Basic CRUD Operations ============

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(project);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Integer id, 
                                               @Valid @RequestBody Project projectDetails) {
        try {
            Project updatedProject = projectService.updateProject(id, projectDetails);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============ Employee Assignment Management ============
    
    /**
     * Assigns an employee to a project
     * 
     * @param projectId ID of the project
     * @param employeeId ID of the employee to assign
     * @return Updated project with assigned employee
     */
    @PostMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<Project> assignEmployee(@PathVariable Integer projectId, 
                                                @PathVariable Integer employeeId) {
        try {
            Project project = projectService.assignEmployee(projectId, employeeId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Removes an employee from a project
     * 
     * @param projectId ID of the project
     * @param employeeId ID of the employee to remove
     * @return Updated project without the employee
     */
    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<Project> removeEmployee(@PathVariable Integer projectId, 
                                                @PathVariable Integer employeeId) {
        try {
            Project project = projectService.removeEmployee(projectId, employeeId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============ Project Filtering and Search ============
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable String status) {
        List<Project> projects = projectService.getProjectsByStatus(status);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Project>> getProjectsByDepartment(@PathVariable String department) {
        List<Project> projects = projectService.getProjectsByDepartment(department);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Project>> getProjectsByPriority(@PathVariable String priority) {
        List<Project> projects = projectService.getProjectsByPriority(priority);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<Project>> getProjectsByManager(@PathVariable Integer managerId) {
        List<Project> projects = projectService.getProjectsByManagerId(managerId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Project>> getProjectsByEmployee(@PathVariable Integer employeeId) {
        List<Project> projects = projectService.getProjectsByEmployeeId(employeeId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Project>> getActiveProjects() {
        List<Project> projects = projectService.getActiveProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Project>> getCompletedProjects() {
        List<Project> projects = projectService.getCompletedProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/over-budget")
    public ResponseEntity<List<Project>> getProjectsOverBudget() {
        List<Project> projects = projectService.getProjectsOverBudget();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<Project>> getProjectsWithoutAssignedEmployees() {
        List<Project> projects = projectService.getProjectsWithoutAssignedEmployees();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Project>> searchProjectsByName(@RequestParam String name) {
        List<Project> projects = projectService.searchProjectsByName(name);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Project>> getProjectsByDateRange(@RequestParam String startDate, 
                                                              @RequestParam String endDate) {
        List<Project> projects = projectService.getProjectsByDateRange(startDate, endDate);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/budget-range")
    public ResponseEntity<List<Project>> getProjectsByBudgetRange(@RequestParam BigDecimal minBudget, 
                                                                @RequestParam BigDecimal maxBudget) {
        List<Project> projects = projectService.getProjectsByBudgetRange(minBudget, maxBudget);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/progress-range")
    public ResponseEntity<List<Project>> getProjectsByProgressRange(@RequestParam int minProgress, 
                                                                   @RequestParam int maxProgress) {
        List<Project> projects = projectService.getProjectsByProgressRange(minProgress, maxProgress);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/ending-soon")
    public ResponseEntity<List<Project>> getProjectsEndingSoon(@RequestParam String endDate) {
        List<Project> projects = projectService.getProjectsEndingSoon(endDate);
        return ResponseEntity.ok(projects);
    }

    // ============ Project Workflow Management ============
    
    /**
     * Activates a project (moves from PLANNING to ACTIVE status)
     * 
     * @param id Project ID to activate
     * @return Updated project with ACTIVE status
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Project> activateProject(@PathVariable Integer id) {
        try {
            Project project = projectService.activateProject(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Project> completeProject(@PathVariable Integer id) {
        try {
            Project project = projectService.completeProject(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/pause")
    public ResponseEntity<Project> pauseProject(@PathVariable Integer id) {
        try {
            Project project = projectService.pauseProject(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Project> cancelProject(@PathVariable Integer id) {
        try {
            Project project = projectService.cancelProject(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Project progress and timeline endpoints
    @PutMapping("/{id}/progress")
    public ResponseEntity<Project> updateProjectProgress(@PathVariable Integer id, 
                                                        @RequestParam int progressPercentage) {
        try {
            Project project = projectService.updateProjectProgress(id, progressPercentage);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/milestones")
    public ResponseEntity<Project> addMilestone(@PathVariable Integer id, 
                                              @RequestParam String milestone) {
        try {
            Project project = projectService.addMilestone(id, milestone);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Resource allocation endpoints
    @PostMapping("/{id}/resources")
    public ResponseEntity<Project> addResource(@PathVariable Integer id, 
                                             @RequestParam String resource) {
        try {
            Project project = projectService.addResource(id, resource);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/risks")
    public ResponseEntity<Project> addRisk(@PathVariable Integer id, 
                                         @RequestParam String risk) {
        try {
            Project project = projectService.addRisk(id, risk);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/budget-used")
    public ResponseEntity<Project> updateBudgetUsed(@PathVariable Integer id, 
                                                   @RequestParam BigDecimal budgetUsed) {
        try {
            Project project = projectService.updateBudgetUsed(id, budgetUsed);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Analytics endpoints
    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> getProjectCountByStatus(@PathVariable String status) {
        Long count = projectService.getProjectCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/department/{department}/count")
    public ResponseEntity<Long> getProjectCountByDepartment(@PathVariable String department) {
        Long count = projectService.getProjectCountByDepartment(department);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/department/{department}/average-progress")
    public ResponseEntity<Double> getAverageProgressByDepartment(@PathVariable String department) {
        Double avgProgress = projectService.getAverageProgressByDepartment(department);
        return ResponseEntity.ok(avgProgress != null ? avgProgress : 0.0);
    }

    @GetMapping("/department/{department}/total-budget")
    public ResponseEntity<BigDecimal> getTotalBudgetByDepartment(@PathVariable String department) {
        BigDecimal totalBudget = projectService.getTotalBudgetByDepartment(department);
        return ResponseEntity.ok(totalBudget != null ? totalBudget : BigDecimal.ZERO);
    }

    @GetMapping("/department/{department}/total-budget-used")
    public ResponseEntity<BigDecimal> getTotalBudgetUsedByDepartment(@PathVariable String department) {
        BigDecimal totalBudgetUsed = projectService.getTotalBudgetUsedByDepartment(department);
        return ResponseEntity.ok(totalBudgetUsed != null ? totalBudgetUsed : BigDecimal.ZERO);
    }

    @GetMapping("/employee/{employeeId}/count")
    public ResponseEntity<Long> getEmployeeProjectCount(@PathVariable Integer employeeId) {
        Long count = projectService.getEmployeeProjectCount(employeeId);
        return ResponseEntity.ok(count);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Project management system is running");
    }
}
