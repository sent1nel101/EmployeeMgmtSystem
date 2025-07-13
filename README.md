# Employee Management System

A full-stack web application for managing employee information, projects, and organizational data with role-based access control and modern user interface.

## Technologies Used

### Backend
- **Java 17** with **Spring Boot 3.x**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **MySQL** database (H2 for development)
- **Maven** for dependency management

### Frontend
- **React 18** with modern hooks
- **Vite** for fast development and building
- **Material-UI (MUI)** for component library
- **React Router** for navigation
- **Axios** for API communication

### DevOps & Deployment
- **Docker** with multi-container setup
- **Docker Compose** for development and production
- **Nginx** for frontend serving
- **Environment-based configuration**

## Features

### 🔐 Authentication & Security
- User registration with auto-generated email addresses
- JWT-based authentication with secure password hashing
- Password reset functionality with token validation
- Role-based access control (Admin, Manager, Employee)

### 👥 Employee Management
- Complete CRUD operations for employee records
- Employee profiles with personal and professional information
- Department assignment and management
- Salary and hire date tracking
- Advanced search and filtering capabilities

### 📊 Project Management
- Project creation and assignment
- Progress tracking and status updates
- Budget management and monitoring
- Project timeline visualization
- Team member assignment

### 📈 Dashboard & Analytics
- Executive dashboard with key metrics
- Employee statistics and departmental insights
- Project status overview
- Performance indicators and charts

### ⚙️ User Experience
- **Responsive Design** - Mobile-friendly interface
- **Dark/Light Theme** - User-customizable appearance
- **Profile Management** - Personal settings and information
- **Settings Page** - Interface customization options
- **Search Functionality** - Global search across employees and projects

### 📋 Administrative Features
- **Performance Reviews** - Employee evaluation system
- **Report Generation** - Comprehensive reporting tools
- **Department Management** - Organizational structure control
- **User Role Management** - Permission and access control

### 🛠️ Technical Features
- **Auto-seeded Test Data** - Pre-populated database for development
- **Form Validation** - Client and server-side validation
- **Error Handling** - Comprehensive error management
- **API Documentation** - RESTful endpoint structure
- **Production Ready** - Docker deployment configuration

## Quick Start

### Development
```bash
# Backend
mvn spring-boot:run

# Frontend
cd src/main/capstone-ui
npm install
npm run dev
```

### Production (Docker)
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Test Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@ourcompany.com` | `admin123` |
| Manager | `manager@ourcompany.com` | `manager123` |
| Employee | `j.doe@ourcompany.com` | `password123` |

## Project Structure

```
├── src/main/java/              # Spring Boot backend
├── src/main/capstone-ui/       # React frontend
├── docker-compose.yml          # Development setup
├── docker-compose.prod.yml     # Production setup
└── scripts/                    # Deployment utilities
```

## License

This project is developed as part of a software engineering capstone program.
