package com.dmcdesigns.capstone.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.dmcdesigns.capstone.Entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Get all users
    @Query("SELECT u FROM User u") 
    List<User> getAllUsers();

    // Get all users by department
    @Query("SELECT u FROM User u WHERE u.department = ?1")
    List<User> getUsersByDepartment(String department);

    // Get all users sorted by last name
    @Query("SELECT u FROM User u ORDER BY u.lastName ASC")
    List<User> getAllUsersSortedByLastName();

    // Get users by first name
    @Query("SELECT u FROM User u WHERE u.firstName = ?1")
    List<User> getUsersByFirstName(String firstName);

    // Get users by last name
    @Query("SELECT u FROM User u WHERE u.lastName = ?1")
    List<User> getUsersByLastName(String lastName);

    // Search users by partial name match
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<User> searchUsersByName(String namePattern);

    // Get users by phone number
    @Query("SELECT u FROM User u WHERE u.phoneNumber = ?1")
    User getUserByPhoneNumber(String phoneNumber);

    // Check if phone number exists
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phoneNumber = ?1")
    boolean existsByPhoneNumber(String phoneNumber);

    // Paginated search methods
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.department = :department")
    Page<User> findUsersByDepartment(@Param("department") String department, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Page<User> findUserById(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY u.lastName ASC, u.firstName ASC")
    Page<User> findAllUsersPaginated(Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    Page<User> searchUsersByFirstName(@Param("firstName") String firstName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<User> searchUsersByLastName(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<User> searchUsersByEmail(@Param("email") String email, Pageable pageable);

    // Custom update methods for role fixing - using native SQL for direct database access
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET role = ?2 WHERE user_type = ?1", nativeQuery = true)
    int updateRoleByDiscriminator(String discriminator, String role);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET has_access = true WHERE user_type IN ('ADMIN', 'MANAGER')", nativeQuery = true)
    int updateAccessByDiscriminator();

}
