import api from './api';

class EmployeeService {
  // Get all employees with optional pagination and filters
  async getAllEmployees(params = {}) {
    const response = await api.get('/employees', { params });
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

  // Get employee by ID
  async getEmployeeById(id) {
    const response = await api.get(`/employees/${id}`);
    return response.data;
  }

  // Create new employee
  async createEmployee(employeeData) {
    const response = await api.post('/employees', employeeData);
    return response.data;
  }

  // Update employee
  async updateEmployee(id, employeeData) {
    const response = await api.put(`/employees/${id}`, employeeData);
    return response.data;
  }

  // Delete employee
  async deleteEmployee(id) {
    const response = await api.delete(`/employees/${id}`);
    return response.data;
  }

  // Get employees by department
  async getEmployeesByDepartment(department, params = {}) {
    const response = await api.get(`/employees/department/${department}`, { 
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

  // Get employee projects
  async getEmployeeProjects(id) {
    const response = await api.get(`/employees/${id}/projects`);
    return response.data;
  }

  // Search employees
  async searchEmployees(searchTerm, params = {}) {
    const response = await api.get('/search/employees', { 
      params: { searchTerm, ...params } 
    });
    return response.data;
  }

  // Promote employee to admin
  async promoteToAdmin(employeeId) {
    const response = await api.post(`/employees/${employeeId}/promote-to-admin`);
    return response.data;
  }
}

export default new EmployeeService();
