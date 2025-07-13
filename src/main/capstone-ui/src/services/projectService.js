import api from './api';

class ProjectService {
  // Get all projects with optional pagination and filters
  async getAllProjects(params = {}) {
    const response = await api.get('/projects', { params });
    // Backend returns array directly, wrap it for pagination compatibility
    if (Array.isArray(response.data)) {
      return {
        content: response.data,
        totalElements: response.data.length,
        totalPages: 1,
        number: 0,
        size: response.data.length
      };
    }
    return response.data;
  }

  // Get project by ID
  async getProjectById(id) {
    const response = await api.get(`/projects/${id}`);
    return response.data;
  }

  // Create new project
  async createProject(projectData) {
    const response = await api.post('/projects', projectData);
    return response.data;
  }

  // Update project
  async updateProject(id, projectData) {
    const response = await api.put(`/projects/${id}`, projectData);
    return response.data;
  }

  // Delete project
  async deleteProject(id) {
    const response = await api.delete(`/projects/${id}`);
    return response.data;
  }

  // Get projects by status
  async getProjectsByStatus(status, params = {}) {
    const response = await api.get(`/projects/status/${status}`, { 
      params 
    });
    // Backend returns array directly, wrap it for pagination compatibility
    if (Array.isArray(response.data)) {
      return {
        content: response.data,
        totalElements: response.data.length,
        totalPages: 1,
        number: 0,
        size: response.data.length
      };
    }
    return response.data;
  }

  // Get projects by manager
  async getProjectsByManager(managerId, params = {}) {
    const response = await api.get(`/projects/manager/${managerId}`, { 
      params 
    });
    // Backend returns array directly, wrap it for pagination compatibility
    if (Array.isArray(response.data)) {
      return {
        content: response.data,
        totalElements: response.data.length,
        totalPages: 1,
        number: 0,
        size: response.data.length
      };
    }
    return response.data;
  }

  // Get project timeline
  async getProjectTimeline(id) {
    const response = await api.get(`/projects/${id}/timeline`);
    return response.data;
  }

  // Assign employee to project
  async assignEmployee(projectId, employeeId) {
    const response = await api.post(`/projects/${projectId}/employees/${employeeId}`);
    return response.data;
  }

  // Remove employee from project
  async removeEmployee(projectId, employeeId) {
    const response = await api.delete(`/projects/${projectId}/employees/${employeeId}`);
    return response.data;
  }

  // Get resource allocation for project
  async getResourceAllocation(projectId) {
    const response = await api.get(`/projects/${projectId}/resources`);
    return response.data;
  }

  // Update project workflow status
  async updateWorkflowStatus(projectId, action) {
    const response = await api.patch(`/projects/${projectId}/workflow`, { action });
    return response.data;
  }

  // Search projects
  async searchProjects(searchTerm, params = {}) {
    const response = await api.get('/search/projects', { 
      params: { searchTerm, ...params } 
    });
    return response.data;
  }
}

export default new ProjectService();
