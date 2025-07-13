// Project Status Constants
export const PROJECT_STATUS = {
  PLANNING: 'PLANNING',
  IN_PROGRESS: 'IN_PROGRESS',
  ON_HOLD: 'ON_HOLD',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED'
};

// Project Priority Constants
export const PROJECT_PRIORITY = {
  LOW: 'LOW',
  MEDIUM: 'MEDIUM',
  HIGH: 'HIGH',
  CRITICAL: 'CRITICAL'
};

// Employee Department Constants
export const DEPARTMENTS = {
  ENGINEERING: 'ENGINEERING',
  MARKETING: 'MARKETING',
  SALES: 'SALES',
  HR: 'HR',
  FINANCE: 'FINANCE',
  OPERATIONS: 'OPERATIONS'
};

// API Routes
export const API_ROUTES = {
  PROJECTS: '/projects',
  EMPLOYEES: '/employees',
  SEARCH: '/search'
};

// Form Validation Messages
export const VALIDATION_MESSAGES = {
  REQUIRED: 'This field is required',
  EMAIL_INVALID: 'Please enter a valid email address',
  DATE_INVALID: 'Please enter a valid date',
  BUDGET_INVALID: 'Please enter a valid budget amount'
};

// Date Formats
export const DATE_FORMATS = {
  DISPLAY: 'MMM dd, yyyy',
  INPUT: 'yyyy-MM-dd',
  FULL: 'MMM dd, yyyy HH:mm'
};

// Pagination Defaults
export const PAGINATION = {
  DEFAULT_SIZE: 10,
  SIZE_OPTIONS: [5, 10, 25, 50]
};
