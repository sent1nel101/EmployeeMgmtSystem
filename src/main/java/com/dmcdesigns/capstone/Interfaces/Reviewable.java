package com.dmcdesigns.capstone.Interfaces;

import com.dmcdesigns.capstone.Entities.PerformanceReview;
import java.util.List;

public interface Reviewable {
    List<PerformanceReview> getPerformanceReviews();
    void addPerformanceReview(PerformanceReview review);
    PerformanceReview getLatestPerformanceReview();
    double getAverageRating();
}
