import api from './api';

class ReportService {
  // Generate employee report
  async generateEmployeeReport(filters) {
    const response = await api.post('/api/reports/employees', filters);
    return response.data;
  }

  // Generate project report
  async generateProjectReport(filters) {
    const response = await api.post('/api/reports/projects', filters);
    return response.data;
  }

  // Generate department report
  async generateDepartmentReport(filters) {
    const response = await api.post('/api/reports/departments', filters);
    return response.data;
  }

  // Generate performance report
  async generatePerformanceReport(filters) {
    const response = await api.post('/api/reports/performance', filters);
    return response.data;
  }

  // Export to PDF
  async exportToPDF(reportType, filters) {
    const response = await api.post(`/api/reports/${reportType}/export/pdf`, filters, {
      responseType: 'blob'
    });
    return response.data;
  }

  // Export to Excel
  async exportToExcel(reportType, filters) {
    const response = await api.post(`/api/reports/${reportType}/export/excel`, filters, {
      responseType: 'blob'
    });
    return response.data;
  }

  // Export to CSV
  async exportToCSV(reportType, filters) {
    const response = await api.post(`/api/reports/${reportType}/export/csv`, filters, {
      responseType: 'blob'
    });
    return response.data;
  }

  // Get report templates
  async getReportTemplates() {
    const response = await api.get('/api/reports/templates');
    return response.data;
  }

  // Save custom report
  async saveCustomReport(reportData) {
    const response = await api.post('/api/reports/custom', reportData);
    return response.data;
  }

  // Get saved reports
  async getSavedReports() {
    const response = await api.get('/api/reports/saved');
    return response.data;
  }
}

export default new ReportService();
