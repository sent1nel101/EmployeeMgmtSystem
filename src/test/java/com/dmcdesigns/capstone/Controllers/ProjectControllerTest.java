package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.Entities.Project;
import com.dmcdesigns.capstone.Services.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private Project testProject;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void testGetAllProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"))
                .andExpect(jsonPath("$[0].department").value("IT"))
                .andExpect(jsonPath("$[0].status").value("PLANNING"));
    }

    @Test
    void testGetProjectById() throws Exception {
        when(projectService.getProjectById(1)).thenReturn(Optional.of(testProject));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.department").value("IT"));
    }

    @Test
    void testGetProjectByIdNotFound() throws Exception {
        when(projectService.getProjectById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProject() throws Exception {
        when(projectService.createProject(any(Project.class))).thenReturn(testProject);

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProject)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testCreateProjectBadRequest() throws Exception {
        when(projectService.createProject(any(Project.class)))
                .thenThrow(new RuntimeException("Validation error"));

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProject)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProject() throws Exception {
        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");
        
        when(projectService.updateProject(eq(1), any(Project.class))).thenReturn(testProject);

        mockMvc.perform(put("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProject)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testUpdateProjectNotFound() throws Exception {
        when(projectService.updateProject(eq(1), any(Project.class)))
                .thenThrow(new RuntimeException("Project not found"));

        mockMvc.perform(put("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProject)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProject() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProjectBadRequest() throws Exception {
        doThrow(new RuntimeException("Cannot delete active project"))
                .when(projectService).deleteProject(1);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssignEmployee() throws Exception {
        when(projectService.assignEmployee(1, 2)).thenReturn(testProject);

        mockMvc.perform(post("/api/projects/1/employees/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testRemoveEmployee() throws Exception {
        when(projectService.removeEmployee(1, 2)).thenReturn(testProject);

        mockMvc.perform(delete("/api/projects/1/employees/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testGetProjectsByStatus() throws Exception {
        when(projectService.getProjectsByStatus("ACTIVE")).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetProjectsByDepartment() throws Exception {
        when(projectService.getProjectsByDepartment("IT")).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/department/IT"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].department").value("IT"));
    }

    @Test
    void testGetProjectsByPriority() throws Exception {
        when(projectService.getProjectsByPriority("HIGH")).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetProjectsByManager() throws Exception {
        when(projectService.getProjectsByManagerId(1)).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/manager/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetProjectsByEmployee() throws Exception {
        when(projectService.getProjectsByEmployeeId(1)).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/employee/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetActiveProjects() throws Exception {
        when(projectService.getActiveProjects()).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetCompletedProjects() throws Exception {
        when(projectService.getCompletedProjects()).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/completed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testActivateProject() throws Exception {
        when(projectService.activateProject(1)).thenReturn(testProject);

        mockMvc.perform(put("/api/projects/1/activate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testCompleteProject() throws Exception {
        when(projectService.completeProject(1)).thenReturn(testProject);

        mockMvc.perform(put("/api/projects/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testPauseProject() throws Exception {
        when(projectService.pauseProject(1)).thenReturn(testProject);

        mockMvc.perform(put("/api/projects/1/pause"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testCancelProject() throws Exception {
        when(projectService.cancelProject(1)).thenReturn(testProject);

        mockMvc.perform(put("/api/projects/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testUpdateProjectProgress() throws Exception {
        when(projectService.updateProjectProgress(1, 75)).thenReturn(testProject);

        mockMvc.perform(put("/api/projects/1/progress?progressPercentage=75"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testAddMilestone() throws Exception {
        when(projectService.addMilestone(1, "Phase 1 Complete")).thenReturn(testProject);

        mockMvc.perform(post("/api/projects/1/milestones?milestone=Phase 1 Complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testAddResource() throws Exception {
        when(projectService.addResource(1, "Development Team")).thenReturn(testProject);

        mockMvc.perform(post("/api/projects/1/resources?resource=Development Team"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testAddRisk() throws Exception {
        when(projectService.addRisk(1, "Budget overrun")).thenReturn(testProject);

        mockMvc.perform(post("/api/projects/1/risks?risk=Budget overrun"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testUpdateBudgetUsed() throws Exception {
        BigDecimal budgetUsed = new BigDecimal("50000.00");
        when(projectService.updateBudgetUsed(1, budgetUsed)).thenReturn(testProject);

        mockMvc.perform(put("/api/projects/1/budget-used?budgetUsed=50000.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testGetProjectCountByStatus() throws Exception {
        when(projectService.getProjectCountByStatus("ACTIVE")).thenReturn(5L);

        mockMvc.perform(get("/api/projects/status/ACTIVE/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("5"));
    }

    @Test
    void testGetProjectCountByDepartment() throws Exception {
        when(projectService.getProjectCountByDepartment("IT")).thenReturn(3L);

        mockMvc.perform(get("/api/projects/department/IT/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("3"));
    }

    @Test
    void testGetAverageProgressByDepartment() throws Exception {
        when(projectService.getAverageProgressByDepartment("IT")).thenReturn(75.0);

        mockMvc.perform(get("/api/projects/department/IT/average-progress"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("75.0"));
    }

    @Test
    void testGetTotalBudgetByDepartment() throws Exception {
        BigDecimal totalBudget = new BigDecimal("500000.00");
        when(projectService.getTotalBudgetByDepartment("IT")).thenReturn(totalBudget);

        mockMvc.perform(get("/api/projects/department/IT/total-budget"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("500000.00"));
    }

    @Test
    void testGetEmployeeProjectCount() throws Exception {
        when(projectService.getEmployeeProjectCount(1)).thenReturn(3L);

        mockMvc.perform(get("/api/projects/employee/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("3"));
    }

    @Test
    void testSearchProjectsByName() throws Exception {
        when(projectService.searchProjectsByName("Test")).thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/search?name=Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetProjectsByDateRange() throws Exception {
        when(projectService.getProjectsByDateRange("2024-01-01", "2024-12-31"))
                .thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/date-range?startDate=2024-01-01&endDate=2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetProjectsByBudgetRange() throws Exception {
        when(projectService.getProjectsByBudgetRange(new BigDecimal("50000"), new BigDecimal("200000")))
                .thenReturn(List.of(testProject));

        mockMvc.perform(get("/api/projects/budget-range?minBudget=50000&maxBudget=200000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/projects/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Project management system is running"));
    }
}
