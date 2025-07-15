package com.dmcdesigns.capstone.Entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;
    private User validUser;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        validUser = new User("John", "Doe", "john.doe@ourcompany.com", 
                           "123-456-7890", "j.doe", "password123", "IT");
    }

    @Test
    void validUser_ShouldPassValidation() {
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        // Assert
        assertTrue(violations.isEmpty(), "Valid user should pass all validations");
    }

    @Test
    void email_WhenInvalid_ShouldFailValidation() {
        // Arrange
        User userWithInvalidEmail = new User("John", "Doe", "invalid-email", 
                                           "123-456-7890", "j.doe", "password123", "IT");

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(userWithInvalidEmail);

        // Assert
        assertFalse(violations.isEmpty(), "Invalid email should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be valid")), 
                  "Should contain email validation error message");
    }

    @Test
    void firstName_WhenBlank_ShouldFailValidation() {
        // Arrange
        User userWithBlankFirstName = new User("", "Doe", "john.doe@ourcompany.com", 
                                             "123-456-7890", "j.doe", "password123", "IT");

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(userWithBlankFirstName);

        // Assert
        assertFalse(violations.isEmpty(), "Blank first name should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("First name is required")), 
                  "Should contain first name validation error message");
    }

    @Test
    void password_WhenTooShort_ShouldFailValidation() {
        // Arrange
        User userWithShortPassword = new User("John", "Doe", "john.doe@ourcompany.com", 
                                            "123-456-7890", "j.doe", "short", "IT");

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(userWithShortPassword);

        // Assert
        assertFalse(violations.isEmpty(), "Short password should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be at least 8 characters")), 
                  "Should contain password validation error message");
    }
}
