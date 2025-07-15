package com.dmcdesigns.capstone.Entities;

import com.dmcdesigns.capstone.Interfaces.Reportable;
import com.dmcdesigns.capstone.Interfaces.Searchable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;

@Entity
@Table(name = "projects")
public class Project implements Reportable, Searchable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @NotBlank(message = "Project name is required")
    @Size(min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @NotBlank(message = "Start date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in YYYY-MM-DD format")
    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in YYYY-MM-DD format")
    @Column(name = "end_date")
    private String endDate;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PLANNING|ACTIVE|ON_HOLD|COMPLETED|CANCELLED)$", 
            message = "Status must be PLANNING, ACTIVE, ON_HOLD, COMPLETED, or CANCELLED")
    @Column(name = "status", nullable = false)
    private String status = "PLANNING";

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$", 
            message = "Priority must be LOW, MEDIUM, HIGH, or CRITICAL")
    @Column(name = "priority", nullable = false)
    private String priority = "MEDIUM";

    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 100, message = "Department must be between 2 and 100 characters")
    @Column(name = "department", nullable = false)
    private String department;

    @DecimalMin(value = "0.0", message = "Budget must be positive")
    @Column(name = "budget", precision = 12, scale = 2)
    private BigDecimal budget = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Budget used must be positive")
    @Column(name = "budget_used", precision = 12, scale = 2)
    private BigDecimal budgetUsed = BigDecimal.ZERO;

    @Min(value = 0, message = "Progress percentage must be between 0 and 100")
    @Max(value = 100, message = "Progress percentage must be between 0 and 100")
    @Column(name = "progress_percentage")
    private int progressPercentage = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_manager_id")
    @JsonIgnore
    private User projectManager;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "project_employees",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @JsonIgnore
    private Set<User> assignedEmployees = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_milestones", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "milestone")
    @JsonIgnore
    private List<String> milestones = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "project_resources", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "resource")
    @JsonIgnore
    private List<String> resources = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "project_risks", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "risk")
    @JsonIgnore
    private List<String> risks = new ArrayList<>();

    // Default constructor for JPA
    public Project() {
    }

    // Constructor
    public Project(String name, String description, String startDate, String department) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.department = department;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getDepartment() {
        return department;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public BigDecimal getBudgetUsed() {
        return budgetUsed;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public User getProjectManager() {
        return projectManager;
    }

    public Set<User> getAssignedEmployees() {
        return assignedEmployees;
    }

    public List<String> getMilestones() {
        return milestones;
    }

    public List<String> getResources() {
        return resources;
    }

    public List<String> getRisks() {
        return risks;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public void setBudgetUsed(BigDecimal budgetUsed) {
        this.budgetUsed = budgetUsed;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public void setProjectManager(User projectManager) {
        this.projectManager = projectManager;
    }

    public void setAssignedEmployees(Set<User> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }

    public void setMilestones(List<String> milestones) {
        this.milestones = milestones;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public void setRisks(List<String> risks) {
        this.risks = risks;
    }

    // Helper methods
    public void addEmployee(User employee) {
        this.assignedEmployees.add(employee);
    }

    public void removeEmployee(User employee) {
        this.assignedEmployees.remove(employee);
    }

    public void addMilestone(String milestone) {
        this.milestones.add(milestone);
    }

    public void addResource(String resource) {
        this.resources.add(resource);
    }

    public void addRisk(String risk) {
        this.risks.add(risk);
    }

    public BigDecimal getRemainingBudget() {
        return budget.subtract(budgetUsed);
    }

    public boolean isOverBudget() {
        return budgetUsed.compareTo(budget) > 0;
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    // Reportable interface implementation
    @Override
    public Map<String, Object> generateReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("id", id);
        report.put("name", name);
        report.put("description", description);
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("status", status);
        report.put("priority", priority);
        report.put("department", department);
        report.put("budget", budget);
        report.put("budgetUsed", budgetUsed);
        report.put("remainingBudget", getRemainingBudget());
        report.put("progressPercentage", progressPercentage);
        report.put("projectManager", projectManager != null ? 
                  projectManager.getFirstName() + " " + projectManager.getLastName() : "N/A");
        report.put("assignedEmployeesCount", assignedEmployees.size());
        report.put("milestones", milestones);
        report.put("resources", resources);
        report.put("risks", risks);
        report.put("reportGeneratedAt", java.time.LocalDateTime.now().toString());
        return report;
    }

    @Override
    public String getReportTitle() {
        return "Project Report: " + name;
    }

    @Override
    public String getReportSummary() {
        return String.format("Project %s (%s) - Status: %s, Progress: %d%%, Budget: $%.2f", 
                           name, department, status, progressPercentage, budget);
    }

    // Searchable interface implementation
    @Override
    public List<String> getSearchableFields() {
        List<String> fields = new ArrayList<>();
        fields.add("name");
        fields.add("description");
        fields.add("department");
        fields.add("status");
        fields.add("priority");
        return fields;
    }

    @Override
    public String getSearchableContent() {
        String managerName = projectManager != null ? 
                           projectManager.getFirstName() + " " + projectManager.getLastName() : "";
        return String.join(" ", name, description != null ? description : "", 
                          department, status, priority, managerName);
    }

    @Override
    public boolean matchesSearch(String searchTerm) {
        String content = getSearchableContent().toLowerCase();
        return content.contains(searchTerm.toLowerCase());
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", department='" + department + '\'' +
                ", progressPercentage=" + progressPercentage +
                '}';
    }
}
