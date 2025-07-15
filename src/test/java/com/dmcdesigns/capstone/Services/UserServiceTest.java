package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John", "Doe", "john.doe@ourcompany.com", 
                          "123-456-7890", "j.doe", "password123", "IT");
    }

    @Test
    void createUser_WithValidUser_ShouldReturnSavedUser() {
        // Arrange
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User result = userService.saveUser(testUser);

        // Assert
        assertEquals(testUser, result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        // Arrange
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUser_WithValidUser_ShouldReturnUpdatedUser() {
        // Arrange
        testUser = new User("John", "Doe", "john.doe@ourcompany.com", 
                          "123-456-7890", "j.doe", "password123", "IT") {
            @Override
            public int getId() {
                return 1;
            }
        };
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User result = userService.updateUser(testUser);

        // Assert
        assertEquals(testUser, result);
        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteUser_WithValidId_ShouldCallRepository() {
        // Arrange
        Integer userId = 1;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }
}
