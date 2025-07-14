package com.dmcdesigns.capstone.Entities;
import com.dmcdesigns.capstone.Interfaces.Reportable;
import com.dmcdesigns.capstone.Interfaces.Searchable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "performance_reviews")
public class PerformanceReview implements Reportable, Searchable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnore
    private User employee;

    @NotNull(message = "Manager is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    @JsonIgnore
    private User manager;

    @NotBlank(message = "Review date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Review date must be in YYYY-MM-DD format")
    @Column(name = "review_date", nullable = false)
    private String reviewDate;

    @Transient
    @JsonIgnore
    private List<String> reviewComments = new ArrayList<>();

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Column(name = "rating", nullable = false)
    private int rating = 0;

    @Transient
    @JsonIgnore
    private List<String> goals = new ArrayList<>();

    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 100, message = "Department must be between 2 and 100 characters")
    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "review_period", nullable = false)
    @NotBlank(message = "Review period is required")
    @Size(min = 2, max = 50, message = "Review period must be between 2 and 50 characters")
    private String reviewPeriod = "Annual";

    @Column(name = "status", nullable = false)
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(DRAFT|SUBMITTED|APPROVED|COMPLETED)$", message = "Status must be DRAFT, SUBMITTED, APPROVED, or COMPLETED")
    private String status = "DRAFT";

    // Default constructor for JPA
    public PerformanceReview() {
    }

    // Constructor to initialize a PerformanceReview object with all fields
    public PerformanceReview(String reviewDate, List<String> goals, List<String> reviewComments) {
        this.reviewDate = reviewDate;
        this.goals = goals;
        this.reviewComments = reviewComments;
    }

    // Getters
    public int getId() {
        return id;
    }
    
    public User getEmployee() {
        return employee;
    }
    
    public User getManager() {
        return manager;
    }
    
    public String getReviewDate() {
        return reviewDate;
    }
    
    public List<String> getReviewGoals() {
        return goals;
    }
    
    public List<String> getReviewComments() {
        return reviewComments;
    }

    public int getRating() {
        return rating;
    }
    
    public String getDepartment() {
        return department;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setEmployee(User employee) {
        this.employee = employee;
    }
    
    public void setManager(User manager) {
        this.manager = manager;
    }
    
    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public void setReviewGoals(String reviewGoals) {
        this.goals.add(reviewGoals);
    }
    
    public void addReviewComment(String reviewComment) {
        this.reviewComments.add(reviewComment);
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Reportable interface implementation
    @Override
    public Map<String, Object> generateReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("id", id);
        report.put("employeeName", employee != null ? employee.getFirstName() + " " + employee.getLastName() : "N/A");
        report.put("managerName", manager != null ? manager.getFirstName() + " " + manager.getLastName() : "N/A");
        report.put("reviewDate", reviewDate);
        report.put("rating", rating);
        report.put("department", department);
        report.put("reviewPeriod", reviewPeriod);
        report.put("status", status);
        report.put("goals", goals);
        report.put("comments", reviewComments);
        report.put("reportGeneratedAt", java.time.LocalDateTime.now().toString());
        return report;
    }

    @Override
    public String getReportTitle() {
        String employeeName = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown Employee";
        return "Performance Review Report: " + employeeName;
    }

    @Override
    public String getReportSummary() {
        String employeeName = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown Employee";
        return String.format("Performance review for %s conducted on %s with rating %d/5", 
                           employeeName, reviewDate, rating);
    }

    // Searchable interface implementation
    @Override
    public List<String> getSearchableFields() {
        List<String> fields = new ArrayList<>();
        fields.add("reviewDate");
        fields.add("department");
        fields.add("rating");
        return fields;
    }

    @Override
    public String getSearchableContent() {
        String employeeName = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "";
        String managerName = manager != null ? manager.getFirstName() + " " + manager.getLastName() : "";
        return String.join(" ", employeeName, managerName, reviewDate, department, String.valueOf(rating));
    }

    @Override
    public boolean matchesSearch(String searchTerm) {
        String content = getSearchableContent().toLowerCase();
        return content.contains(searchTerm.toLowerCase());
    }
}
