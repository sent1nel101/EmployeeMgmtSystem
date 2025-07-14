# Validation Error Response Handling

This document describes the comprehensive validation error handling system implemented in the application.

## Error Response Types

### Standard Error Response
Used for general errors, runtime exceptions, and non-validation issues.

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request parameter",
  "path": "/api/employees"
}
```

### Validation Error Response
Used specifically for field validation failures with detailed field-level errors.

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "path": "/api/employees",
  "errors": [
    {
      "field": "firstName",
      "rejectedValue": "A",
      "message": "First name must be between 2 and 50 characters"
    },
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email must be valid"
    },
    {
      "field": "phoneNumber",
      "rejectedValue": "123",
      "message": "Phone number must be valid (8-20 characters, digits, spaces, hyphens, parentheses, plus signs)"
    }
  ]
}
```

## Exception Types Handled

### 1. Field Validation Errors (`@Valid` annotations)
- **Trigger**: `@NotBlank`, `@Size`, `@Email`, `@Pattern` validation failures
- **Response**: `ValidationErrorResponse` with detailed field errors
- **HTTP Status**: 400 Bad Request

### 2. Constraint Violations
- **Trigger**: Direct constraint violations on entity fields
- **Response**: `ValidationErrorResponse` with constraint details
- **HTTP Status**: 400 Bad Request

### 3. Method Argument Type Mismatch
- **Trigger**: Wrong parameter types (e.g., string passed for integer)
- **Response**: `ErrorResponse` with parameter type information
- **HTTP Status**: 400 Bad Request

### 4. Missing Request Parameters
- **Trigger**: Required parameters not provided
- **Response**: `ErrorResponse` with missing parameter name
- **HTTP Status**: 400 Bad Request

### 5. Malformed JSON
- **Trigger**: Invalid JSON in request body
- **Response**: `ErrorResponse` with JSON error message
- **HTTP Status**: 400 Bad Request

### 6. Data Integrity Violations
- **Trigger**: Database constraint violations (duplicates, foreign keys)
- **Response**: `ErrorResponse` with user-friendly conflict message
- **HTTP Status**: 409 Conflict

### 7. Runtime Exceptions
- **Trigger**: Business logic exceptions with contextual messages
- **Response**: `ErrorResponse` with appropriate status based on message content
- **HTTP Status**: 404 Not Found, 409 Conflict, 403 Forbidden, or 500 Internal Server Error

## Entity Validation Rules

### User Entity
- **firstName**: Required, 2-50 characters
- **lastName**: Required, 2-50 characters  
- **email**: Required, valid email format, max 100 characters
- **phoneNumber**: Required, 8-20 characters (digits, spaces, hyphens, parentheses, plus signs, dots)
- **username**: Required, 3-30 characters, alphanumeric with dots, underscores, hyphens
- **password**: Required, minimum 8 characters
- **department**: Required, 2-100 characters

### Employee Entity (extends User)
- **role**: Required, 2-50 characters

### Department Entity
- **name**: Required, 2-100 characters
- **description**: Required, 5-500 characters

### Project Entity
- **name**: Required, 2-100 characters
- **description**: Required, 10-500 characters
- **startDate**: Required, YYYY-MM-DD format
- **endDate**: Required, YYYY-MM-DD format
- **status**: Required, 2-50 characters

## Example API Responses

### Successful Employee Creation
```
POST /api/employees
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "(555) 123-4567",
  "username": "john.doe",
  "password": "securePassword123",
  "department": "Engineering",
  "role": "Software Developer"
}

Response: 201 Created
```

### Validation Error Example
```
POST /api/employees
Content-Type: application/json

{
  "firstName": "J",
  "lastName": "",
  "email": "invalid-email",
  "phoneNumber": "123",
  "username": "jo",
  "password": "weak",
  "department": "",
  "role": ""
}

Response: 400 Bad Request
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "path": "/api/employees",
  "errors": [
    {
      "field": "firstName",
      "rejectedValue": "J",
      "message": "First name must be between 2 and 50 characters"
    },
    {
      "field": "lastName",
      "rejectedValue": "",
      "message": "Last name is required"
    },
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email must be valid"
    },
    {
      "field": "phoneNumber",
      "rejectedValue": "123",
      "message": "Phone number must be valid (8-20 characters, digits, spaces, hyphens, parentheses, plus signs)"
    },
    {
      "field": "username",
      "rejectedValue": "jo",
      "message": "Username must be between 3 and 30 characters"
    },
    {
      "field": "password",
      "rejectedValue": "weak",
      "message": "Password must be at least 8 characters"
    },
    {
      "field": "department",
      "rejectedValue": "",
      "message": "Department is required"
    },
    {
      "field": "role",
      "rejectedValue": "",
      "message": "Role is required"
    }
  ]
}
```

### Data Conflict Example
```
POST /api/departments
Content-Type: application/json

{
  "name": "Engineering",
  "description": "Software development team"
}

Response: 409 Conflict (if department already exists)
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 409,
  "error": "Data Conflict",
  "message": "Duplicate entry - this record already exists",
  "path": "/api/departments"
}
```

## Implementation Notes

- All validation errors include the rejected value for debugging purposes
- Error messages are user-friendly and actionable
- HTTP status codes follow REST conventions
- Path information helps identify the problematic endpoint
- Timestamps are in ISO format for consistency
- Security: Sensitive information (like passwords) are not exposed in error messages
