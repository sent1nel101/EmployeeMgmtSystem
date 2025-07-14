import api from './api';

/**
 * Authentication service for handling user login, registration, and session management.
 * Manages JWT tokens and user data in localStorage.
 * 
 * @author DMC Designs
 */
class AuthService {
  /**
   * Authenticates user with email/username and password
   * 
   * @param {Object} credentials - User credentials
   * @param {string} credentials.email - User's email (used as username)
   * @param {string} credentials.password - User's password
   * @returns {Promise<Object>} Authentication response with token
   */
  async login(credentials) {
    const loginData = {
      username: credentials.email, // Backend expects username field
      password: credentials.password
    };
    
    const response = await api.post('/auth/login', loginData);
    
    if (response.data.token) {
      this.setToken(response.data.token);
      
      const user = {
        username: response.data.username,
        email: response.data.email,
        firstName: response.data.firstName,
        lastName: response.data.lastName,
        roles: [response.data.role],
        permissions: ['ALL']
      };
      
      this.setUser(user);
    }
    return response.data;
  }

  /**
   * Registers a new user account
   * 
   * @param {Object} userData - User registration data
   * @returns {Promise<Object>} Registration response
   */
  async register(userData) {
    const registerData = {
      firstName: userData.firstName,
      lastName: userData.lastName,
      email: userData.email,
      phoneNumber: userData.phoneNumber || '',
      username: userData.username,
      password: userData.password,
      department: userData.department,
      userType: 'EMPLOYEE'
    };
    
    console.log('Sending registration data:', registerData);
    
    try {
      const response = await api.post('/auth/register', registerData);
      return response.data;
    } catch (error) {
      console.error('Registration error details:', error.response?.data);
      throw error;
    }
  }

  /**
   * Logs out the current user by removing tokens and user data
   */
  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  /**
   * Requests a password reset for the given email
   * 
   * @param {string} email - User's email address
   * @returns {Promise<Object>} Reset token response
   */
  async requestPasswordReset(email) {
    const response = await api.post('/auth/forgot-password', { email });
    return response.data;
  }

  /**
   * Resets password using the provided token
   * 
   * @param {string} token - Reset token
   * @param {string} newPassword - New password
   * @returns {Promise<Object>} Reset confirmation
   */
  async resetPassword(token, newPassword) {
    const response = await api.post('/auth/reset-password', { 
      token, 
      newPassword 
    });
    return response.data;
  }

  /**
   * Gets the currently logged-in user data
   * 
   * @returns {Object|null} User object or null if not logged in
   */
  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * Checks if user is authenticated with a valid JWT token
   * 
   * @returns {boolean} True if authenticated, false otherwise
   */
  isAuthenticated() {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch (error) {
      return false;
    }
  }

  /**
   * Retrieves the stored authentication token
   * 
   * @returns {string|null} JWT token or null
   */
  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Stores authentication token in localStorage
   * 
   * @param {string} token - JWT token to store
   */
  setToken(token) {
    localStorage.setItem('authToken', token);
  }

  /**
   * Stores user data in localStorage
   * 
   * @param {Object} user - User object to store
   */
  setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
  }

  // Refresh token
  async refreshToken() {
    try {
      const response = await api.post('/auth/refresh');
      if (response.data.token) {
        this.setToken(response.data.token);
        return response.data.token;
      }
    } catch (error) {
      this.logout();
      throw error;
    }
  }

  // Check user role/permissions
  hasRole(role) {
    const user = this.getCurrentUser();
    return user && user.roles && user.roles.includes(role);
  }

  // Check if user has permission
  hasPermission(permission) {
    const user = this.getCurrentUser();
    return user && user.permissions && user.permissions.includes(permission);
  }
}

export default new AuthService();
