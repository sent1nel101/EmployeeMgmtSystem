package com.dmcdesigns.capstone.Entities;

import com.dmcdesigns.capstone.Interfaces.Reviewable;
import com.dmcdesigns.capstone.Interfaces.Searchable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Base User entity representing common user information across the system.
 * Uses JPA inheritance to support specialized user types (Employee, Manager, Admin).
 * 
 * @author DMC Designs
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User implements Reviewable, Searchable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Column(name = "phone_number", nullable = false)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[\\d\\-\\(\\)\\+\\s\\.]{8,20}$", message = "Phone number must be valid (8-20 characters, digits, spaces, hyphens, parentheses, plus signs)")
    private String phoneNumber;

    @Column(name = "username", nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
    private String password;

    @Column(name = "department", nullable = false)
    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 100, message = "Department must be between 2 and 100 characters")
    protected String department;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PerformanceReview> performanceReviews = new ArrayList<>();

    /** Default constructor required by JPA */
    public User() {
    }

    /**
     * Constructor to initialize a User with all required fields
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @param phoneNumber User's phone number
     * @param username Unique username for login
     * @param password Encrypted password
     * @param department Department the user belongs to
     */
    public User(String firstName, String lastName, String email, String phoneNumber, String username, String password, String department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.department = department;
    }
    public int getId(){
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDepartment() {
        return department;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartment(String department){
        this.department = department;
    }

    /**
     * Generates a company email based on username
     * 
     * @param username The username to use for email generation
     * @return Generated email address
     */
    public String createEmail(String username) {
        String emailString = username + "@ourcompany.com";
        this.email = emailString;
        return emailString;
    }

    /**
     * Generates username based on first initial and last name
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     */
    public void setUsername(String firstName, String lastName) {
        this.username = firstName.substring(0,1).trim() + "." + lastName;
    }

    /**
     * Direct setter for username (for admin operations)
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Direct setter for email (for admin operations)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    // ============ Reviewable Interface Implementation ============
    
    @Override
    @JsonIgnore
    public List<PerformanceReview> getPerformanceReviews() {
        return performanceReviews;
    }

    @Override
    public void addPerformanceReview(PerformanceReview review) {
        this.performanceReviews.add(review);
        review.setEmployee(this);
    }

    @Override
    @JsonIgnore
    public PerformanceReview getLatestPerformanceReview() {
        if (performanceReviews.isEmpty()) {
            return null;
        }
        return performanceReviews.get(performanceReviews.size() - 1);
    }

    @Override
    @JsonIgnore
    public double getAverageRating() {
        if (performanceReviews.isEmpty()) {
            return 0.0;
        }
        return performanceReviews.stream()
            .mapToInt(PerformanceReview::getRating)
            .average()
            .orElse(0.0);
    }

    /**
     * Get the user type from the JPA discriminator value
     * This will return ADMIN, MANAGER, or EMPLOYEE based on the entity type
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getUserType() {
        String className = this.getClass().getSimpleName();
        switch (className) {
            case "Admin":
                return "ADMIN";
            case "Manager":
                return "MANAGER";
            case "Employee":
            default:
                return "EMPLOYEE";
        }
    }

    /**
     * Get the position/job title for display purposes
     * This is implemented in subclasses to return appropriate position
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getPosition() {
        return "Employee";  // Default implementation
    }

    // ============ Searchable Interface Implementation ============
    
    @Override
    public List<String> getSearchableFields() {
        List<String> fields = new ArrayList<>();
        fields.add("firstName");
        fields.add("lastName");
        fields.add("email");
        fields.add("username");
        fields.add("department");
        return fields;
    }

    @Override
    public String getSearchableContent() {
        return String.join(" ", firstName, lastName, email, username, department);
    }

    @Override
    public boolean matchesSearch(String searchTerm) {
        String content = getSearchableContent().toLowerCase();
        return content.contains(searchTerm.toLowerCase());
    }

}
