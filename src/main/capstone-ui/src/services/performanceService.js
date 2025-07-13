import api from './api';

class PerformanceService {
  // Get all performance reviews
  async getAllReviews(params = {}) {
    const response = await api.get('/performance', { params });
    return response.data;
  }

  // Get performance review by ID
  async getReviewById(id) {
    const response = await api.get(`/performance/${id}`);
    return response.data;
  }

  // Create new performance review
  async createReview(reviewData) {
    const response = await api.post('/performance', reviewData);
    return response.data;
  }

  // Update performance review
  async updateReview(id, reviewData) {
    const response = await api.put(`/performance/${id}`, reviewData);
    return response.data;
  }

  // Delete performance review
  async deleteReview(id) {
    const response = await api.delete(`/performance/${id}`);
    return response.data;
  }

  // Get reviews by employee
  async getEmployeeReviews(employeeId, params = {}) {
    const response = await api.get(`/performance/employee/${employeeId}`, { params });
    return response.data;
  }

  // Get reviews by reviewer
  async getReviewsByReviewer(reviewerId, params = {}) {
    const response = await api.get(`/performance/reviewer/${reviewerId}`, { params });
    return response.data;
  }

  // Submit review for approval
  async submitReview(id) {
    const response = await api.patch(`/performance/${id}/submit`);
    return response.data;
  }

  // Approve review
  async approveReview(id) {
    const response = await api.patch(`/performance/${id}/approve`);
    return response.data;
  }

  // Get performance metrics
  async getPerformanceMetrics(employeeId, dateRange = {}) {
    const response = await api.get(`/performance/metrics/${employeeId}`, { 
      params: dateRange 
    });
    return response.data;
  }

  // Get team performance summary
  async getTeamPerformance(managerId, params = {}) {
    const response = await api.get(`/performance/team/${managerId}`, { params });
    return response.data;
  }
}

export default new PerformanceService();
