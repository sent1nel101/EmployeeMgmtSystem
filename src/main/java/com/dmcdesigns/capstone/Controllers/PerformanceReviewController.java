package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.Entities.PerformanceReview;
import com.dmcdesigns.capstone.Services.PerformanceReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/performance-reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PerformanceReviewController {

    @Autowired
    private PerformanceReviewService performanceReviewService;

    @GetMapping
    public ResponseEntity<List<PerformanceReview>> getAllPerformanceReviews() {
        List<PerformanceReview> reviews = performanceReviewService.getAllPerformanceReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceReview> getPerformanceReviewById(@PathVariable Integer id) {
        Optional<PerformanceReview> review = performanceReviewService.getPerformanceReviewById(id);
        return review.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PerformanceReview> createPerformanceReview(@Valid @RequestBody PerformanceReview review) {
        try {
            PerformanceReview createdReview = performanceReviewService.createPerformanceReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceReview> updatePerformanceReview(@PathVariable Integer id, 
                                                                   @Valid @RequestBody PerformanceReview reviewDetails) {
        try {
            PerformanceReview updatedReview = performanceReviewService.updatePerformanceReview(id, reviewDetails);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformanceReview(@PathVariable Integer id) {
        try {
            performanceReviewService.deletePerformanceReview(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Employee review history endpoints
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PerformanceReview>> getEmployeeReviewHistory(@PathVariable Integer employeeId) {
        List<PerformanceReview> reviews = performanceReviewService.getPerformanceReviewsByEmployeeId(employeeId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public ResponseEntity<List<PerformanceReview>> getEmployeeReviewsByStatus(@PathVariable Integer employeeId, 
                                                                            @PathVariable String status) {
        List<PerformanceReview> reviews = performanceReviewService.getEmployeeReviewsByStatus(employeeId, status);
        return ResponseEntity.ok(reviews);
    }

    // Manager review endpoints  
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<PerformanceReview>> getManagerReviews(@PathVariable Integer managerId) {
        List<PerformanceReview> reviews = performanceReviewService.getPerformanceReviewsByManagerId(managerId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/manager/{managerId}/status/{status}")
    public ResponseEntity<List<PerformanceReview>> getManagerReviewsByStatus(@PathVariable Integer managerId, 
                                                                           @PathVariable String status) {
        List<PerformanceReview> reviews = performanceReviewService.getManagerReviewsByStatus(managerId, status);
        return ResponseEntity.ok(reviews);
    }

    // Department and filtering endpoints
    @GetMapping("/department/{department}")
    public ResponseEntity<List<PerformanceReview>> getReviewsByDepartment(@PathVariable String department) {
        List<PerformanceReview> reviews = performanceReviewService.getPerformanceReviewsByDepartment(department);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PerformanceReview>> getReviewsByStatus(@PathVariable String status) {
        List<PerformanceReview> reviews = performanceReviewService.getPerformanceReviewsByStatus(status);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/period/{reviewPeriod}")
    public ResponseEntity<List<PerformanceReview>> getReviewsByPeriod(@PathVariable String reviewPeriod) {
        List<PerformanceReview> reviews = performanceReviewService.getPerformanceReviewsByReviewPeriod(reviewPeriod);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating")
    public ResponseEntity<List<PerformanceReview>> getReviewsByRatingRange(@RequestParam int minRating, 
                                                                          @RequestParam int maxRating) {
        List<PerformanceReview> reviews = performanceReviewService.getReviewsByRatingRange(minRating, maxRating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PerformanceReview>> getReviewsByDateRange(@RequestParam String startDate, 
                                                                        @RequestParam String endDate) {
        List<PerformanceReview> reviews = performanceReviewService.getReviewsByDateRange(startDate, endDate);
        return ResponseEntity.ok(reviews);
    }

    // Review workflow endpoints
    @PutMapping("/{id}/submit")
    public ResponseEntity<PerformanceReview> submitReview(@PathVariable Integer id) {
        try {
            PerformanceReview review = performanceReviewService.submitReview(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<PerformanceReview> approveReview(@PathVariable Integer id) {
        try {
            PerformanceReview review = performanceReviewService.approveReview(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<PerformanceReview> completeReview(@PathVariable Integer id) {
        try {
            PerformanceReview review = performanceReviewService.completeReview(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Analytics endpoints
    @GetMapping("/employee/{employeeId}/average-rating")
    public ResponseEntity<Double> getEmployeeAverageRating(@PathVariable Integer employeeId) {
        Double averageRating = performanceReviewService.getAverageRatingForEmployee(employeeId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }

    @GetMapping("/department/{department}/count")
    public ResponseEntity<Long> getDepartmentReviewCount(@PathVariable String department) {
        Long count = performanceReviewService.getReviewCountByDepartment(department);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> getStatusReviewCount(@PathVariable String status) {
        Long count = performanceReviewService.getReviewCountByStatus(status);
        return ResponseEntity.ok(count);
    }
}
