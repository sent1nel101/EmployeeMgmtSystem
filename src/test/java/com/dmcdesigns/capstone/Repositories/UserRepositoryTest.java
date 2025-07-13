package com.dmcdesigns.capstone.Repositories;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Manager;
import com.dmcdesigns.capstone.Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Employee testEmployee;
    private Manager testManager;

    @BeforeEach
    void setUp() {
        // Create test data
        testEmployee = new Employee("John", "Doe", "john.doe@company.com", 
                                  "555-1234", "john.doe", "password123", "IT");
        testManager = new Manager("Jane", "Smith", "jane.smith@company.com", 
                                "555-5678", "jane.smith", "password456", "HR");
        
        // Persist test data
        entityManager.persistAndFlush(testEmployee);
        entityManager.persistAndFlush(testManager);
    }

    @Test
    void testFindByUsername() {
        // Test finding user by username
        User foundUser = userRepository.findByUsername("john.doe");
        
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("john.doe");
        assertThat(foundUser.getFirstName()).isEqualTo("John");
        assertThat(foundUser.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testFindByEmail() {
        // Test finding user by email
        User foundUser = userRepository.findByEmail("jane.smith@company.com");
        
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("jane.smith@company.com");
        assertThat(foundUser.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testExistsByUsername() {
        // Test checking if user exists by username
        assertThat(userRepository.existsByUsername("john.doe")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent.user")).isFalse();
    }

    @Test
    void testExistsByEmail() {
        // Test checking if user exists by email
        assertThat(userRepository.existsByEmail("john.doe@company.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@company.com")).isFalse();
    }

    @Test
    void testGetAllUsers() {
        // Test getting all users
        List<User> users = userRepository.getAllUsers();
        
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername)
                          .containsExactlyInAnyOrder("john.doe", "jane.smith");
    }

    @Test
    void testGetUsersByDepartment() {
        // Test getting users by department
        List<User> itUsers = userRepository.getUsersByDepartment("IT");
        List<User> hrUsers = userRepository.getUsersByDepartment("HR");
        
        assertThat(itUsers).hasSize(1);
        assertThat(itUsers.get(0).getUsername()).isEqualTo("john.doe");
        
        assertThat(hrUsers).hasSize(1);
        assertThat(hrUsers.get(0).getUsername()).isEqualTo("jane.smith");
    }

    @Test
    void testGetUsersByFirstName() {
        // Test getting users by first name
        List<User> johns = userRepository.getUsersByFirstName("John");
        
        assertThat(johns).hasSize(1);
        assertThat(johns.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    void testGetUsersByLastName() {
        // Test getting users by last name
        List<User> smiths = userRepository.getUsersByLastName("Smith");
        
        assertThat(smiths).hasSize(1);
        assertThat(smiths.get(0).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testSearchUsersByName() {
        // Test searching users by name pattern
        List<User> searchResults = userRepository.searchUsersByName("jo");
        
        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void testGetUserByPhoneNumber() {
        // Test getting user by phone number
        User foundUser = userRepository.getUserByPhoneNumber("555-1234");
        
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("john.doe");
    }

    @Test
    void testExistsByPhoneNumber() {
        // Test checking if phone number exists
        assertThat(userRepository.existsByPhoneNumber("555-1234")).isTrue();
        assertThat(userRepository.existsByPhoneNumber("555-9999")).isFalse();
    }

    @Test
    void testGetAllUsersSortedByLastName() {
        // Test getting users sorted by last name
        List<User> sortedUsers = userRepository.getAllUsersSortedByLastName();
        
        assertThat(sortedUsers).hasSize(2);
        assertThat(sortedUsers.get(0).getLastName()).isEqualTo("Doe");
        assertThat(sortedUsers.get(1).getLastName()).isEqualTo("Smith");
    }

    @Test
    void testCrudOperations() {
        // Test Create
        Employee newEmployee = new Employee("Bob", "Wilson", "bob.wilson@company.com", 
                                          "555-9876", "bob.wilson", "password789", "Finance");
        User savedUser = userRepository.save(newEmployee);
        
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("bob.wilson");
        
        // Test Read
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("Bob");
        
        // Test Update
        savedUser.setPhoneNumber("555-1111");
        User updatedUser = userRepository.save(savedUser);
        assertThat(updatedUser.getPhoneNumber()).isEqualTo("555-1111");
        
        // Test Delete
        userRepository.delete(savedUser);
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testFindAll() {
        // Test finding all users
        List<User> allUsers = userRepository.findAll();
        
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting(User::getDepartment)
                           .containsExactlyInAnyOrder("IT", "HR");
    }

    @Test
    void testCount() {
        // Test counting users
        long userCount = userRepository.count();
        
        assertThat(userCount).isEqualTo(2);
    }
}
