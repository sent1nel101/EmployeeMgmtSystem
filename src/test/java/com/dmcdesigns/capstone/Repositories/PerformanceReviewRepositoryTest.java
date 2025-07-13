package com.dmcdesigns.capstone.Repositories;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Manager;
import com.dmcdesigns.capstone.Entities.PerformanceReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PerformanceReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;

    private Employee testEmployee1;
    private Employee testEmployee2;
    private Manager testManager;
    private PerformanceReview review1;
    private PerformanceReview review2;
    private PerformanceReview review3;

    @BeforeEach
    void setUp() {
        // Create test users
        testEmployee1 = new Employee("John", "Doe", "john.doe@company.com", 
                                   "555-1111", "john.doe", "password123", "IT");
        testEmployee2 = new Employee("Jane", "Smith", "jane.smith@company.com", 
                                   "555-2222", "jane.smith", "password456", "HR");
        testManager = new Manager("Bob", "Manager", "bob.manager@company.com", 
                                "555-3333", "bob.manager", "password789", "IT");
        
        entityManager.persistAndFlush(testEmployee1);
        entityManager.persistAndFlush(testEmployee2);
        entityManager.persistAndFlush(testManager);

        // Create test performance reviews
        review1 = new PerformanceReview();
        review1.setEmployee(testEmployee1);
        review1.setManager(testManager);
        review1.setReviewDate("2024-01-15");
        review1.setRating(4);
        review1.setDepartment("IT");
        review1.setReviewPeriod("Annual");
        review1.setStatus("COMPLETED");
        review1.addReviewComment("Excellent performance");
        review1.setReviewGoals("Improve coding skills");

        review2 = new PerformanceReview();
        review2.setEmployee(testEmployee1);
        review2.setManager(testManager);
        review2.setReviewDate("2024-06-15");
        review2.setRating(5);
        review2.setDepartment("IT");
        review2.setReviewPeriod("Mid-Year");
        review2.setStatus("APPROVED");
        review2.addReviewComment("Outstanding work");
        review2.setReviewGoals("Lead a project");

        review3 = new PerformanceReview();
        review3.setEmployee(testEmployee2);
        review3.setManager(testManager);
        review3.setReviewDate("2024-03-15");
        review3.setRating(3);
        review3.setDepartment("HR");
        review3.setReviewPeriod("Quarterly");
        review3.setStatus("SUBMITTED");
        review3.addReviewComment("Good progress");
        review3.setReviewGoals("Improve communication");

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);
        entityManager.persistAndFlush(review3);
    }

    @Test
    void testFindAllByEmployeeId() {
        // Test finding all reviews for an employee
        List<PerformanceReview> reviews = performanceReviewRepository.findAllByEmployeeId(testEmployee1.getId());
        
        assertThat(reviews).hasSize(2);
        assertThat(reviews).extracting(PerformanceReview::getRating)
                          .containsExactlyInAnyOrder(4, 5);
    }

    @Test
    void testFindAllByManagerId() {
        // Test finding all reviews by a manager
        List<PerformanceReview> reviews = performanceReviewRepository.findAllByManagerId(testManager.getId());
        
        assertThat(reviews).hasSize(3);
        assertThat(reviews).extracting(PerformanceReview::getRating)
                          .containsExactlyInAnyOrder(4, 5, 3);
    }

    @Test
    void testFindAllByRating() {
        // Test finding reviews by rating
        List<PerformanceReview> excellentReviews = performanceReviewRepository.findAllByRating(5);
        List<PerformanceReview> goodReviews = performanceReviewRepository.findAllByRating(4);
        
        assertThat(excellentReviews).hasSize(1);
        assertThat(excellentReviews.get(0).getEmployee().getId()).isEqualTo(testEmployee1.getId());
        
        assertThat(goodReviews).hasSize(1);
        assertThat(goodReviews.get(0).getRating()).isEqualTo(4);
    }

    @Test
    void testFindAllByDepartment() {
        // Test finding reviews by department
        List<PerformanceReview> itReviews = performanceReviewRepository.findAllByDepartment("IT");
        List<PerformanceReview> hrReviews = performanceReviewRepository.findAllByDepartment("HR");
        
        assertThat(itReviews).hasSize(2);
        assertThat(hrReviews).hasSize(1);
        assertThat(hrReviews.get(0).getEmployee().getId()).isEqualTo(testEmployee2.getId());
    }

    @Test
    void testFindTopByEmployeeIdOrderByReviewDateDesc() {
        // Test finding latest review for an employee
        PerformanceReview latestReview = performanceReviewRepository
                .findTopByEmployeeIdOrderByReviewDateDesc(testEmployee1.getId());
        
        assertThat(latestReview).isNotNull();
        assertThat(latestReview.getReviewDate()).isEqualTo("2024-06-15");
        assertThat(latestReview.getRating()).isEqualTo(5);
    }

    @Test
    void testFindFirstByEmployeeIdOrderByReviewDateAsc() {
        // Test finding earliest review for an employee
        PerformanceReview earliestReview = performanceReviewRepository
                .findFirstByEmployeeIdOrderByReviewDateAsc(testEmployee1.getId());
        
        assertThat(earliestReview).isNotNull();
        assertThat(earliestReview.getReviewDate()).isEqualTo("2024-01-15");
        assertThat(earliestReview.getRating()).isEqualTo(4);
    }

    @Test
    void testFindReviewsByRatingRange() {
        // Test finding reviews within rating range
        List<PerformanceReview> goodToExcellent = performanceReviewRepository
                .findReviewsByRatingRange(4, 5);
        
        assertThat(goodToExcellent).hasSize(2);
        assertThat(goodToExcellent).extracting(PerformanceReview::getRating)
                                  .containsExactlyInAnyOrder(4, 5);
    }

    @Test
    void testFindReviewsByDateRange() {
        // Test finding reviews by date range
        List<PerformanceReview> reviewsInRange = performanceReviewRepository
                .findReviewsByDateRange("2024-01-01", "2024-03-31");
        
        assertThat(reviewsInRange).hasSize(2);
        assertThat(reviewsInRange).extracting(PerformanceReview::getReviewDate)
                                 .containsExactlyInAnyOrder("2024-01-15", "2024-03-15");
    }

    @Test
    void testGetAverageRatingForEmployee() {
        // Test getting average rating for an employee
        Double averageRating = performanceReviewRepository
                .getAverageRatingForEmployee(testEmployee1.getId());
        
        assertThat(averageRating).isEqualTo(4.5); // (4 + 5) / 2 = 4.5
    }

    @Test
    void testGetReviewCountByDepartment() {
        // Test counting reviews by department
        Long itCount = performanceReviewRepository.getReviewCountByDepartment("IT");
        Long hrCount = performanceReviewRepository.getReviewCountByDepartment("HR");
        
        assertThat(itCount).isEqualTo(2);
        assertThat(hrCount).isEqualTo(1);
    }

    @Test
    void testGetTopRatedEmployees() {
        // Test getting top rated employees
        List<Object[]> topRated = performanceReviewRepository.getTopRatedEmployees();
        
        assertThat(topRated).hasSize(2);
        // First should be employee1 with higher average rating (4.5)
        Object[] first = topRated.get(0);
        assertThat(first[0]).isEqualTo(testEmployee1.getId());
        assertThat((Double) first[1]).isEqualTo(4.5);
    }

    @Test
    void testBasicCrudOperations() {
        // Create
        PerformanceReview newReview = new PerformanceReview();
        newReview.setEmployee(testEmployee2);
        newReview.setManager(testManager);
        newReview.setReviewDate("2024-12-15");
        newReview.setRating(4);
        newReview.setDepartment("HR");
        newReview.setReviewPeriod("Annual");
        newReview.setStatus("DRAFT");
        newReview.addReviewComment("Great improvement");
        newReview.setReviewGoals("Continue growth");
        
        PerformanceReview savedReview = performanceReviewRepository.save(newReview);
        assertThat(savedReview.getId()).isNotNull();
        
        // Read
        PerformanceReview foundReview = performanceReviewRepository.findById((long) savedReview.getId());
        assertThat(foundReview).isNotNull();
        assertThat(foundReview.getRating()).isEqualTo(4);
        
        // Update
        savedReview.setRating(5);
        savedReview.addReviewComment("Exceeded expectations");
        PerformanceReview updatedReview = performanceReviewRepository.save(savedReview);
        assertThat(updatedReview.getRating()).isEqualTo(5);
        assertThat(updatedReview.getReviewComments()).hasSize(2);
        
        // Delete
        performanceReviewRepository.delete(updatedReview);
        PerformanceReview deletedReview = performanceReviewRepository.findById((long) savedReview.getId());
        assertThat(deletedReview).isNull();
    }

    @Test
    void testFindAll() {
        // Test finding all reviews
        List<PerformanceReview> allReviews = performanceReviewRepository.findAll();
        
        assertThat(allReviews).hasSize(3);
    }

    @Test
    void testCount() {
        // Test counting all reviews
        long reviewCount = performanceReviewRepository.count();
        
        assertThat(reviewCount).isEqualTo(3);
    }

    @Test
    void testReviewCommentsAndGoals() {
        // Test review comments and goals functionality
        PerformanceReview review = performanceReviewRepository.findById((long) review1.getId());
        
        assertThat(review).isNotNull();
        assertThat(review.getReviewComments()).containsExactly("Excellent performance");
        assertThat(review.getReviewGoals()).containsExactly("Improve coding skills");
        
        // Add more comments and goals
        review.addReviewComment("Consistent quality work");
        review.setReviewGoals("Learn new framework");
        
        PerformanceReview updatedReview = performanceReviewRepository.save(review);
        assertThat(updatedReview.getReviewComments()).hasSize(2);
        assertThat(updatedReview.getReviewGoals()).hasSize(2);
    }

    @Test
    void testFindAllByStatus() {
        // Test finding reviews by status
        List<PerformanceReview> completedReviews = performanceReviewRepository.findAllByStatus("COMPLETED");
        List<PerformanceReview> approvedReviews = performanceReviewRepository.findAllByStatus("APPROVED");
        List<PerformanceReview> submittedReviews = performanceReviewRepository.findAllByStatus("SUBMITTED");
        
        assertThat(completedReviews).hasSize(1);
        assertThat(approvedReviews).hasSize(1);
        assertThat(submittedReviews).hasSize(1);
        
        assertThat(completedReviews.get(0).getEmployee().getId()).isEqualTo(testEmployee1.getId());
        assertThat(submittedReviews.get(0).getEmployee().getId()).isEqualTo(testEmployee2.getId());
    }

    @Test
    void testFindAllByReviewPeriod() {
        // Test finding reviews by review period
        List<PerformanceReview> annualReviews = performanceReviewRepository.findAllByReviewPeriod("Annual");
        List<PerformanceReview> midYearReviews = performanceReviewRepository.findAllByReviewPeriod("Mid-Year");
        List<PerformanceReview> quarterlyReviews = performanceReviewRepository.findAllByReviewPeriod("Quarterly");
        
        assertThat(annualReviews).hasSize(1);
        assertThat(midYearReviews).hasSize(1);
        assertThat(quarterlyReviews).hasSize(1);
        
        assertThat(annualReviews.get(0).getStatus()).isEqualTo("COMPLETED");
        assertThat(quarterlyReviews.get(0).getDepartment()).isEqualTo("HR");
    }

    @Test
    void testFindAllByEmployeeIdAndStatus() {
        // Test finding reviews by employee and status
        List<PerformanceReview> employee1Completed = performanceReviewRepository
                .findAllByEmployeeIdAndStatus(testEmployee1.getId(), "COMPLETED");
        List<PerformanceReview> employee1Approved = performanceReviewRepository
                .findAllByEmployeeIdAndStatus(testEmployee1.getId(), "APPROVED");
        
        assertThat(employee1Completed).hasSize(1);
        assertThat(employee1Approved).hasSize(1);
        assertThat(employee1Completed.get(0).getReviewPeriod()).isEqualTo("Annual");
        assertThat(employee1Approved.get(0).getReviewPeriod()).isEqualTo("Mid-Year");
    }

    @Test
    void testFindAllByManagerIdAndStatus() {
        // Test finding reviews by manager and status
        List<PerformanceReview> managerCompleted = performanceReviewRepository
                .findAllByManagerIdAndStatus(testManager.getId(), "COMPLETED");
        List<PerformanceReview> managerSubmitted = performanceReviewRepository
                .findAllByManagerIdAndStatus(testManager.getId(), "SUBMITTED");
        
        assertThat(managerCompleted).hasSize(1);
        assertThat(managerSubmitted).hasSize(1);
        assertThat(managerCompleted.get(0).getDepartment()).isEqualTo("IT");
        assertThat(managerSubmitted.get(0).getDepartment()).isEqualTo("HR");
    }

    @Test
    void testCountByStatus() {
        // Test counting reviews by status
        Long completedCount = performanceReviewRepository.countByStatus("COMPLETED");
        Long approvedCount = performanceReviewRepository.countByStatus("APPROVED");
        Long submittedCount = performanceReviewRepository.countByStatus("SUBMITTED");
        Long draftCount = performanceReviewRepository.countByStatus("DRAFT");
        
        assertThat(completedCount).isEqualTo(1);
        assertThat(approvedCount).isEqualTo(1);
        assertThat(submittedCount).isEqualTo(1);
        assertThat(draftCount).isEqualTo(0);
    }

    @Test
    void testFindByReviewPeriodAndEmployeeId() {
        // Test finding reviews by review period and employee
        List<PerformanceReview> employee1Annual = performanceReviewRepository
                .findByReviewPeriodAndEmployeeId("Annual", testEmployee1.getId());
        List<PerformanceReview> employee1MidYear = performanceReviewRepository
                .findByReviewPeriodAndEmployeeId("Mid-Year", testEmployee1.getId());
        List<PerformanceReview> employee2Quarterly = performanceReviewRepository
                .findByReviewPeriodAndEmployeeId("Quarterly", testEmployee2.getId());
        
        assertThat(employee1Annual).hasSize(1);
        assertThat(employee1MidYear).hasSize(1);
        assertThat(employee2Quarterly).hasSize(1);
        
        assertThat(employee1Annual.get(0).getStatus()).isEqualTo("COMPLETED");
        assertThat(employee1MidYear.get(0).getStatus()).isEqualTo("APPROVED");
        assertThat(employee2Quarterly.get(0).getStatus()).isEqualTo("SUBMITTED");
    }
}
