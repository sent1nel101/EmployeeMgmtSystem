# 🔗 Frontend-Backend Connection Guide

## Overview
I've updated the React frontend to connect to your Spring Boot backend. Here's what's been configured:

## ✅ **Backend Configuration**
Your Spring Boot backend is ready with:
- **CORS enabled** for all origins
- **JWT authentication** implemented
- **REST API endpoints** at `/api/*`
- **Sample data seeder** for testing

## ✅ **Frontend Updates**
The React frontend now:
- **Connects to backend** APIs instead of mock data
- **Real JWT authentication** (no more demo mode)
- **Proper API calls** to Spring Boot endpoints
- **Error handling** for backend responses

## 🚀 **Connection Instructions**

### Step 1: Start Backend (Spring Boot)
```bash
# From project root directory
./mvnw spring-boot:run

# Or if using IDE, run the main application class
# The backend will start on http://localhost:8080
```

### Step 2: Start Frontend (React)
```bash
# Navigate to frontend directory
cd src/main/capstone-ui

# Install dependencies (if not done already)
npm install --legacy-peer-deps

# Start frontend development server
npm start

# Frontend will start on http://localhost:3000
```

### Step 3: Test Connection
1. **Open browser**: Go to `http://localhost:3000`
2. **Try login**: Use these test credentials:
   - **Admin**: `admin@company.com` / `admin123`
   - **Manager**: `manager@company.com` / `manager123`
   - **Employee**: `employee@company.com` / `employee123`

## 🔑 **Test Credentials**
The backend now includes sample data:

### Users (for login):
- **Admin User**: 
  - Email: `admin@company.com`
  - Password: `admin123`
  - Role: ADMIN

- **Manager User**: 
  - Email: `manager@company.com` 
  - Password: `manager123`
  - Role: MANAGER

- **Employee User**: 
  - Email: `employee@company.com`
  - Password: `employee123`
  - Role: EMPLOYEE

### Sample Data:
- **5 Employees** with different departments
- **4 Projects** with various statuses
- **Realistic test data** for development

## 🔧 **Key Changes Made**

### AuthService Updated:
- **Real JWT authentication** instead of demo mode
- **Backend-compatible login/register** data format
- **Proper token management** and validation

### API Services Updated:
- **Employee Service**: Handles backend array response format
- **Project Service**: Compatible with backend endpoints
- **Pagination**: Wraps backend arrays for frontend compatibility

### Backend Enhancements:
- **Data Seeder**: Automatically creates test data on startup
- **CORS Configuration**: Allows frontend connections
- **JWT Security**: Full authentication flow

## 📡 **API Endpoints**
The backend provides these endpoints:

### Authentication:
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout

### Employees:
- `GET /api/employees` - List all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee

### Projects:
- `GET /api/projects` - List all projects
- `GET /api/projects/{id}` - Get project by ID
- `POST /api/projects` - Create project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

## 🚨 **Troubleshooting**

### Backend Issues:
- **Port 8080 busy**: Change port in `application.properties`
- **Database errors**: Check H2 console at `http://localhost:8080/h2-console`
- **CORS errors**: Verify SecurityConfig.java CORS settings

### Frontend Issues:
- **Connection refused**: Ensure backend is running on port 8080
- **Login fails**: Check credentials and backend logs
- **API errors**: Verify backend endpoints are accessible

### Success Indicators:
- ✅ Backend starts without errors
- ✅ Frontend loads login page
- ✅ Login works with test credentials
- ✅ Dashboard shows real data from backend
- ✅ Navigation works based on user roles

## 🎯 **Expected Flow**

1. **Start Backend** → See "Started Application" in logs
2. **Start Frontend** → Opens to login page
3. **Login** with test credentials → Redirects to dashboard
4. **Dashboard** shows real employee/project counts
5. **Navigate** to different sections → Data loads from backend
6. **Role-based access** → Menu items appear based on user role

Your frontend and backend are now fully connected! 🎉
