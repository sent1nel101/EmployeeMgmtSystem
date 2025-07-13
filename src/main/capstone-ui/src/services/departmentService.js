import api from './api';

class DepartmentService {
  // Get all departments with employee counts
  async getAllDepartments() {
    const response = await api.get('/departments');
    return response.data;
  }

  // Get department by ID
  async getDepartmentById(id) {
    const response = await api.get(`/departments/${id}`);
    return response.data;
  }

  // Get employees by department name
  async getDepartmentEmployees(departmentName, params = {}) {
    const response = await api.get(`/departments/name/${departmentName}/employees`, { params });
    return response.data;
  }

  // Get department by name
  async getDepartmentByName(name) {
    const response = await api.get(`/departments/name/${name}`);
    return response.data;
  }
}

export default new DepartmentService();
