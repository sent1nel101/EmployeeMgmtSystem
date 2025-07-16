// Email validation
export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// Required field validation
export const validateRequired = (value) => {
  return value !== null && value !== undefined && value.toString().trim() !== '';
};

// Date validation
export const validateDate = (date) => {
  if (!date) return false;
  const dateObj = new Date(date);
  return dateObj instanceof Date && !isNaN(dateObj);
};

// Date range validation
export const validateDateRange = (startDate, endDate) => {
  if (!validateDate(startDate) || !validateDate(endDate)) {
    return false;
  }
  return new Date(startDate) <= new Date(endDate);
};

// Budget validation
export const validateBudget = (budget) => {
  const budgetNum = parseFloat(budget);
  return !isNaN(budgetNum) && budgetNum >= 0;
};

// Project form validation
export const validateProjectForm = (project) => {
  const errors = {};

  if (!validateRequired(project.name)) {
    errors.name = 'Project name is required';
  }

  if (!validateRequired(project.description)) {
    errors.description = 'Project description is required';
  }

  if (!validateDate(project.startDate)) {
    errors.startDate = 'Valid start date is required';
  }

  if (!validateDate(project.endDate)) {
    errors.endDate = 'Valid end date is required';
  }

  if (project.startDate && project.endDate && !validateDateRange(project.startDate, project.endDate)) {
    errors.endDate = 'End date must be after start date';
  }

  if (!validateBudget(project.budget)) {
    errors.budget = 'Valid budget amount is required';
  }

  if (!validateRequired(project.department)) {
    errors.department = 'Department is required';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Employee form validation
export const validateEmployeeForm = (employee, isEdit = false) => {
  const errors = {};

  if (!validateRequired(employee.firstName)) {
    errors.firstName = 'First name is required';
  }

  if (!validateRequired(employee.lastName)) {
    errors.lastName = 'Last name is required';
  }

  if (!validateRequired(employee.department)) {
    errors.department = 'Department is required';
  }

  if (!validateRequired(employee.userRole)) {
    errors.userRole = 'Role is required';
  }

  if (!validateRequired(employee.position)) {
    errors.position = 'Position is required';
  }

  if (!validateRequired(employee.phoneNumber)) {
    errors.phoneNumber = 'Phone number is required';
  }

  // Password is only required for new employees
  if (!isEdit && !validateRequired(employee.password)) {
    errors.password = 'Password is required';
  }

  // For edit mode, validate email and username if they exist
  if (isEdit) {
    if (employee.email && !validateEmail(employee.email)) {
      errors.email = 'Valid email address is required';
    }
    
    if (employee.username && !validateRequired(employee.username)) {
      errors.username = 'Username is required';
    }
  }
  // For new employees, email and username are auto-generated, so no validation needed

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Search validation
export const validateSearchTerm = (searchTerm) => {
  return searchTerm && searchTerm.trim().length >= 2;
};
