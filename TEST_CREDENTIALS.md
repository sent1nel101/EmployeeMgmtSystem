# Test User Credentials for Capstone Management System

## Overview
The following test accounts are automatically created when the application starts with an empty database. These accounts are available in both development and production environments for testing and review purposes.

---

## Test User Accounts

### System Administrator
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@ourcompany.com`
- **Role**: Administrator
- **Department**: IT
- **Permissions**: Full system access, can manage all users, projects, and system settings

### HR Director
- **Username**: `hr.director`
- **Password**: `hr123`
- **Email**: `hr@ourcompany.com`
- **Role**: HR Administrator
- **Department**: HR
- **Permissions**: Employee management, performance reviews, department administration

### Project Manager
- **Username**: `manager`
- **Password**: `manager123`
- **Email**: `manager@ourcompany.com`
- **Role**: Manager
- **Department**: Engineering
- **Permissions**: Team management, project oversight, performance reviews for team members

### Sales Manager
- **Username**: `s.manager`
- **Password**: `sales123`
- **Email**: `s.manager@ourcompany.com`
- **Role**: Sales Manager
- **Department**: Sales
- **Permissions**: Sales team management, project access, team performance reviews

### Regular Employee
- **Username**: `employee`
- **Password**: `employee123`
- **Email**: `employee@ourcompany.com`
- **Role**: Employee
- **Department**: Engineering
- **Permissions**: View personal information, project assignments, performance reviews

### Marketing Specialist
- **Username**: `m.specialist`
- **Password**: `marketing123`
- **Email**: `m.specialist@ourcompany.com`
- **Role**: Employee
- **Department**: Marketing
- **Permissions**: View personal information, project assignments, performance reviews

---

## Sample Employee Data

### John Doe (Senior Developer)
- **Username**: `j.doe`
- **Password**: `password123`
- **Email**: `j.doe@ourcompany.com`
- **Department**: Engineering
- **Salary**: $75,000
- **Hire Date**: January 15, 2022

### Jane Smith (Marketing Coordinator)
- **Username**: `j.smith`
- **Password**: `password123`
- **Email**: `j.smith@ourcompany.com`
- **Department**: Marketing
- **Salary**: $65,000
- **Hire Date**: August 20, 2021

### Mike Johnson (Sales Representative)
- **Username**: `m.johnson`
- **Password**: `password123`
- **Email**: `m.johnson@ourcompany.com`
- **Department**: Sales
- **Salary**: $60,000
- **Hire Date**: March 10, 2023

### Sarah Wilson (HR Generalist)
- **Username**: `s.wilson`
- **Password**: `password123`
- **Email**: `s.wilson@ourcompany.com`
- **Department**: HR
- **Salary**: $58,000
- **Hire Date**: November 5, 2020

### David Brown (Financial Analyst)
- **Username**: `d.brown`
- **Password**: `password123`
- **Email**: `d.brown@ourcompany.com`
- **Department**: Finance
- **Salary**: $70,000
- **Hire Date**: June 12, 2022

### Tech Lead (Engineering Manager)
- **Username**: `t.lead`
- **Password**: `techmanager123`
- **Email**: `t.lead@ourcompany.com`
- **Department**: Engineering
- **Salary**: $95,000
- **Hire Date**: May 15, 2019

### Alex Chen (Software Engineer Intern)
- **Username**: `a.chen`
- **Password**: `intern123`
- **Email**: `a.chen@ourcompany.com`
- **Department**: Engineering
- **Salary**: $45,000
- **Hire Date**: January 8, 2024

---

## Sample Projects

### 1. Customer Portal Redesign
- **Department**: Engineering
- **Budget**: $150,000
- **Status**: Active
- **Priority**: High
- **Progress**: 65%
- **Timeline**: January 1, 2024 - June 30, 2024

### 2. Mobile App Development
- **Department**: Engineering
- **Budget**: $200,000
- **Status**: Planning
- **Priority**: Medium
- **Progress**: 10%
- **Timeline**: February 15, 2024 - August 15, 2024

### 3. Data Migration (Completed)
- **Department**: IT
- **Budget**: $100,000
- **Budget Used**: $95,000
- **Status**: Completed
- **Priority**: Critical
- **Progress**: 100%
- **Timeline**: September 1, 2023 - January 31, 2024

### 4. Security Audit (On Hold)
- **Department**: IT
- **Budget**: $75,000
- **Budget Used**: $25,000
- **Status**: On Hold
- **Priority**: High
- **Progress**: 30%
- **Timeline**: March 1, 2024 - April 30, 2024

### 5. Marketing Campaign Platform
- **Department**: Marketing
- **Budget**: $120,000
- **Budget Used**: $80,000
- **Status**: Active
- **Priority**: Medium
- **Progress**: 75%
- **Timeline**: January 15, 2024 - May 30, 2024

---

## Testing Scenarios

### Administrative Testing
1. **Login as `admin`** to test full system administration:
   - Add/edit/delete employees
   - Create departments and projects
   - Generate all types of reports
   - Manage system settings

### HR Testing
1. **Login as `hr.director`** to test HR workflows:
   - Employee onboarding and management
   - Performance review creation
   - Department organization
   - Workforce analytics

### Manager Testing
1. **Login as `manager`** or `s.manager`** to test management features:
   - Team member oversight
   - Project management and assignment
   - Performance review workflow
   - Team reporting

### Employee Testing
1. **Login as `employee`** or any sample employee to test user experience:
   - Personal information access
   - Project assignment viewing
   - Performance review participation
   - Limited system navigation

### Cross-Department Testing
1. **Test different department workflows**:
   - Engineering projects and team management
   - Marketing campaign coordination
   - Sales performance tracking
   - HR employee lifecycle management

---

## Security Notes

⚠️ **Important**: These are test credentials intended for development and review purposes only.

- **Production Deployment**: These credentials are created automatically but should be changed or disabled before going live
- **Password Policy**: All test passwords follow the minimum requirements but should be updated with stronger passwords
- **Access Control**: Each role has different permission levels - test all scenarios to verify proper access restrictions

---

## Automated Data Creation

The test data is automatically created by the `DataSeeder` component when:
1. The application starts
2. The database is empty (no existing users)
3. In both development and production environments

This ensures reviewers always have access to test accounts and sample data for comprehensive testing of all system features.
