package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.PerformanceReview;
import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.PerformanceReviewRepository;
import com.dmcdesigns.capstone.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceReviewService {

    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PerformanceReview> getAllPerformanceReviews() {
        return performanceReviewRepository.findAll();
    }

    public Optional<PerformanceReview> getPerformanceReviewById(Integer id) {
        return performanceReviewRepository.findById(id);
    }

    public List<PerformanceReview> getPerformanceReviewsByEmployeeId(Integer employeeId) {
        return performanceReviewRepository.findAllByEmployeeId(employeeId);
    }

    public List<PerformanceReview> getPerformanceReviewsByManagerId(Integer managerId) {
        return performanceReviewRepository.findAllByManagerId(managerId);
    }

    public List<PerformanceReview> getPerformanceReviewsByDepartment(String department) {
        return performanceReviewRepository.findAllByDepartment(department);
    }

    public List<PerformanceReview> getPerformanceReviewsByStatus(String status) {
        return performanceReviewRepository.findAllByStatus(status);
    }

    public List<PerformanceReview> getPerformanceReviewsByReviewPeriod(String reviewPeriod) {
        return performanceReviewRepository.findAllByReviewPeriod(reviewPeriod);
    }

    public List<PerformanceReview> getEmployeeReviewsByStatus(Integer employeeId, String status) {
        return performanceReviewRepository.findAllByEmployeeIdAndStatus(employeeId, status);
    }

    public List<PerformanceReview> getManagerReviewsByStatus(Integer managerId, String status) {
        return performanceReviewRepository.findAllByManagerIdAndStatus(managerId, status);
    }

    public PerformanceReview createPerformanceReview(PerformanceReview performanceReview) {
        // Validate employee and manager exist
        User employee = userRepository.findById(performanceReview.getEmployee().getId())
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + performanceReview.getEmployee().getId()));
        
        User manager = userRepository.findById(performanceReview.getManager().getId())
            .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + performanceReview.getManager().getId()));

        // Set default values if not provided
        if (performanceReview.getReviewDate() == null || performanceReview.getReviewDate().isEmpty()) {
            performanceReview.setReviewDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        if (performanceReview.getDepartment() == null || performanceReview.getDepartment().isEmpty()) {
            performanceReview.setDepartment(employee.getDepartment());
        }

        // Validate manager has permission to review this employee (same department)
        if (!manager.getDepartment().equals(employee.getDepartment())) {
            throw new RuntimeException("Manager can only review employees in the same department");
        }

        return performanceReviewRepository.save(performanceReview);
    }

    public PerformanceReview updatePerformanceReview(Integer id, PerformanceReview reviewDetails) {
        return performanceReviewRepository.findById(id)
            .map(review -> {
                // Only allow updates if review is in DRAFT or SUBMITTED status
                if ("APPROVED".equals(review.getStatus()) || "COMPLETED".equals(review.getStatus())) {
                    throw new RuntimeException("Cannot update review with status: " + review.getStatus());
                }

                if (reviewDetails.getReviewDate() != null) {
                    review.setReviewDate(reviewDetails.getReviewDate());
                }
                if (reviewDetails.getRating() != 0) {
                    review.setRating(reviewDetails.getRating());
                }
                if (reviewDetails.getReviewPeriod() != null) {
                    review.setReviewPeriod(reviewDetails.getReviewPeriod());
                }
                if (reviewDetails.getStatus() != null) {
                    review.setStatus(reviewDetails.getStatus());
                }
                if (reviewDetails.getReviewComments() != null) {
                    review.getReviewComments().clear();
                    review.getReviewComments().addAll(reviewDetails.getReviewComments());
                }
                if (reviewDetails.getReviewGoals() != null) {
                    review.getReviewGoals().clear();
                    review.getReviewGoals().addAll(reviewDetails.getReviewGoals());
                }

                return performanceReviewRepository.save(review);
            })
            .orElseThrow(() -> new RuntimeException("Performance review not found with ID: " + id));
    }

    public PerformanceReview submitReview(Integer id) {
        return performanceReviewRepository.findById(id)
            .map(review -> {
                if (!"DRAFT".equals(review.getStatus())) {
                    throw new RuntimeException("Only draft reviews can be submitted");
                }
                review.setStatus("SUBMITTED");
                return performanceReviewRepository.save(review);
            })
            .orElseThrow(() -> new RuntimeException("Performance review not found with ID: " + id));
    }

    public PerformanceReview approveReview(Integer id) {
        return performanceReviewRepository.findById(id)
            .map(review -> {
                if (!"SUBMITTED".equals(review.getStatus())) {
                    throw new RuntimeException("Only submitted reviews can be approved");
                }
                review.setStatus("APPROVED");
                return performanceReviewRepository.save(review);
            })
            .orElseThrow(() -> new RuntimeException("Performance review not found with ID: " + id));
    }

    public PerformanceReview completeReview(Integer id) {
        return performanceReviewRepository.findById(id)
            .map(review -> {
                if (!"APPROVED".equals(review.getStatus())) {
                    throw new RuntimeException("Only approved reviews can be completed");
                }
                review.setStatus("COMPLETED");
                return performanceReviewRepository.save(review);
            })
            .orElseThrow(() -> new RuntimeException("Performance review not found with ID: " + id));
    }

    public void deletePerformanceReview(Integer id) {
        PerformanceReview review = performanceReviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Performance review not found with ID: " + id));
        
        // Only allow deletion of draft reviews
        if (!"DRAFT".equals(review.getStatus())) {
            throw new RuntimeException("Only draft reviews can be deleted");
        }
        
        performanceReviewRepository.deleteById(id);
    }

    public Double getAverageRatingForEmployee(Integer employeeId) {
        return performanceReviewRepository.getAverageRatingForEmployee(employeeId);
    }

    public Long getReviewCountByDepartment(String department) {
        return performanceReviewRepository.getReviewCountByDepartment(department);
    }

    public Long getReviewCountByStatus(String status) {
        return performanceReviewRepository.countByStatus(status);
    }

    public List<PerformanceReview> getReviewsByRatingRange(int minRating, int maxRating) {
        return performanceReviewRepository.findReviewsByRatingRange(minRating, maxRating);
    }

    public List<PerformanceReview> getReviewsByDateRange(String startDate, String endDate) {
        return performanceReviewRepository.findReviewsByDateRange(startDate, endDate);
    }
}
