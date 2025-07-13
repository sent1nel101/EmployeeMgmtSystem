package com.dmcdesigns.capstone.Entities;
import com.dmcdesigns.capstone.Interfaces.Searchable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "departments")
public class Department implements Searchable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private int id;
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    private String name;
    
    @Column(name = "description", nullable = false)
    @NotBlank(message = "Department description is required")
    @Size(min = 5, max = 500, message = "Department description must be between 5 and 500 characters")
    private String description;

    // Default constructor required by JPA
    public Department() {
    }

    // Constructor to initialize a Department object with all fields
    public Department(String name, String description) {
        this.name = name;
        this.description = description;
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

    //Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
 * String representation of the Department object for logging and debugging
 */
    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    // Searchable interface implementation
    @Override
    public List<String> getSearchableFields() {
        List<String> fields = new ArrayList<>();
        fields.add("name");
        fields.add("description");
        return fields;
    }

    @Override
    public String getSearchableContent() {
        return String.join(" ", name, description);
    }

    @Override
    public boolean matchesSearch(String searchTerm) {
        String content = getSearchableContent().toLowerCase();
        return content.contains(searchTerm.toLowerCase());
    }

}
