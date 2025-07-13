package com.dmcdesigns.capstone.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dmcdesigns.capstone.Entities.PerformanceReview;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Integer> {
    PerformanceReview findById(long id);
    PerformanceReview findByEmployeeId(int employeeId);
    boolean existsById(long id);
    boolean existsByEmployeeId(int employeeId);
    void deleteById(long id);
    void deleteByEmployeeId(int employeeId);


    // Additional custom queries can be added here as needed
    PerformanceReview findTopByEmployeeIdOrderByReviewDateDesc(int employeeId); // Get latest review for an employee
    PerformanceReview findFirstByEmployeeIdOrderByReviewDateAsc(int employeeId); // Get earliest review for an employee

    // Get reviews by rating
    PerformanceReview findPerformanceReviewByRating(int rating);

    //Get all reviews by employee ID
    List<PerformanceReview> findAllByEmployeeId(int userId);

    // Get all reviews by manager ID
    List<PerformanceReview> findAllByManagerId(int userId);

    // Get all reviews of specific rating
    List<PerformanceReview> findAllByRating(int rating);

    // Get all review by department
    List<PerformanceReview> findAllByDepartment(String department);

    // Get reviews within rating range using @Query
    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.rating BETWEEN ?1 AND ?2")
    List<PerformanceReview> findReviewsByRatingRange(int minRating, int maxRating);

    // Get reviews by date range using @Query  
    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.reviewDate BETWEEN ?1 AND ?2")
    List<PerformanceReview> findReviewsByDateRange(String startDate, String endDate);

    // Get average rating for an employee using @Query
    @Query("SELECT AVG(pr.rating) FROM PerformanceReview pr WHERE pr.employee.id = ?1")
    Double getAverageRatingForEmployee(int employeeId);

    // Get review count by department using @Query
    @Query("SELECT COUNT(pr) FROM PerformanceReview pr WHERE pr.department = ?1")
    Long getReviewCountByDepartment(String department);

    // Get top rated employees using @Query
    @Query("SELECT pr.employee.id, AVG(pr.rating) FROM PerformanceReview pr GROUP BY pr.employee.id ORDER BY AVG(pr.rating) DESC")
    List<Object[]> getTopRatedEmployees();

    // Find reviews by status
    List<PerformanceReview> findAllByStatus(String status);

    // Find reviews by review period
    List<PerformanceReview> findAllByReviewPeriod(String reviewPeriod);

    // Find reviews by employee and status
    List<PerformanceReview> findAllByEmployeeIdAndStatus(int employeeId, String status);

    // Find reviews by manager and status
    List<PerformanceReview> findAllByManagerIdAndStatus(int managerId, String status);

    // Count reviews by status
    @Query("SELECT COUNT(pr) FROM PerformanceReview pr WHERE pr.status = ?1")
    Long countByStatus(String status);

    // Get reviews for a specific review period
    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.reviewPeriod = ?1 AND pr.employee.id = ?2")
    List<PerformanceReview> findByReviewPeriodAndEmployeeId(String reviewPeriod, int employeeId);
}
