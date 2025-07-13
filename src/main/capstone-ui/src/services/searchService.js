import api from './api';

class SearchService {
  // Global search across all entities
  async globalSearch(query, params = {}) {
    const response = await api.get('/search', { 
      params: { q: query, ...params } 
    });
    return response.data;
  }

  // Search employees
  async searchEmployees(query, params = {}) {
    const response = await api.get('/search/employees', { 
      params: { q: query, ...params } 
    });
    return response.data;
  }

  // Search projects
  async searchProjects(query, params = {}) {
    const response = await api.get('/search/projects', { 
      params: { q: query, ...params } 
    });
    return response.data;
  }

  // Search with filters
  async advancedSearch(query, filters = {}) {
    const response = await api.post('/search/advanced', {
      query,
      filters
    });
    return response.data;
  }

  // Get search suggestions
  async getSearchSuggestions(query) {
    const response = await api.get('/search/suggestions', {
      params: { q: query }
    });
    return response.data;
  }
}

export default new SearchService();
