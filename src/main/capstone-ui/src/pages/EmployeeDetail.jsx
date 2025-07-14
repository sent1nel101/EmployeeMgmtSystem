import React, { useState, useEffect, useCallback } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Grid,
  Button,
  Chip,
  Alert,
  Card,
  CardContent,
  Divider,
  CircularProgress
} from '@mui/material';
import {
  Edit,
  ArrowBack,
  Person,
  Email,
  Phone,
  Business,
  CalendarToday,
  AttachMoney,
  AdminPanelSettings
} from '@mui/icons-material';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import employeeService from '../services/employeeService';
import projectService from '../services/projectService';
import { formatDate, formatCurrency } from '../utils/formatters';
import { useAuth } from '../hooks/useAuth.jsx';
import EmployeeForm from '../components/employee/EmployeeForm';

const EmployeeDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { hasRole } = useAuth();
  const [employee, setEmployee] = useState(null);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [updateLoading, setUpdateLoading] = useState(false);
  const [promoteLoading, setPromoteLoading] = useState(false);

  useEffect(() => {
    const fetchEmployeeData = async () => {
      // Check if we're on the "new" route
      const isNewEmployee = location.pathname === '/employees/new';
      
      if (isNewEmployee || (!id || id === 'new')) {
        // For new employee, set loading to false and show create form
        setLoading(false);
        setEmployee(null);
        setProjects([]);
        return;
      }
      
      try {
        setLoading(true);
        setError('');
        
        const [employeeResponse, projectsResponse] = await Promise.all([
          employeeService.getEmployeeById(id),
          employeeService.getEmployeeProjects(id)
        ]);
        
        setEmployee(employeeResponse);
        setProjects(projectsResponse || []);
      } catch (error) {
        setError('Failed to load employee data');
        console.error('Employee detail error:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchEmployeeData();
  }, [id, location.pathname]);

  const handleEdit = () => {
    setEditDialogOpen(true);
  };

  const handleCreateEmployee = async (employeeData) => {
    setUpdateLoading(true);
    try {
      const newEmployee = await employeeService.createEmployee(employeeData);
      // Redirect to the new employee's detail page
      navigate(`/employees/${newEmployee.id}`);
    } catch (error) {
      throw error; // Let the form handle the error
    } finally {
      setUpdateLoading(false);
    }
  };

  const handleUpdateEmployee = async (updatedData) => {
    setUpdateLoading(true);
    try {
      const updatedEmployee = await employeeService.updateEmployee(id, updatedData);
      setEmployee(updatedEmployee);
      setEditDialogOpen(false);
    } catch (error) {
      throw error; // Let the form handle the error
    } finally {
      setUpdateLoading(false);
    }
  };

  const handlePromoteToAdmin = async () => {
    if (!window.confirm(`Are you sure you want to promote ${employee.firstName} ${employee.lastName} to Admin? This will grant them full administrative access to the system.`)) {
      return;
    }

    setPromoteLoading(true);
    try {
      const updatedEmployee = await employeeService.promoteToAdmin(id);
      setEmployee(updatedEmployee);
      // Show success message
      alert(`${employee.firstName} ${employee.lastName} has been successfully promoted to Admin!`);
    } catch (error) {
      console.error('Error promoting employee:', error);
      alert('Failed to promote employee to admin. Please try again.');
    } finally {
      setPromoteLoading(false);
    }
  };

  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: 400,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md">
        <Alert severity="error" sx={{ mt: 4 }}>
          {error}
        </Alert>
      </Container>
    );
  }

  // Handle new employee creation
  const isNewEmployee = location.pathname === '/employees/new';
  if (isNewEmployee || id === 'new') {
    return (
      <Container maxWidth="md">
        <Box sx={{ mb: 4 }}>
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate('/employees')}
            sx={{ mb: 2 }}
          >
            Back to Employees
          </Button>
          
          <Typography variant="h4" component="h1" gutterBottom>
            Add New Employee
          </Typography>
          <Typography variant="body1" color="text.secondary" gutterBottom>
            Fill in the details below to create a new employee record
          </Typography>
        </Box>
        
        <Paper sx={{ p: 3 }}>
          <EmployeeForm
            open={true}
            onClose={() => navigate('/employees')}
            onSubmit={handleCreateEmployee}
            loading={updateLoading}
            title="Create Employee"
          />
        </Paper>
      </Container>
    );
  }

  if (!employee) {
    return (
      <Container maxWidth="md">
        <Alert severity="info" sx={{ mt: 4 }}>
          Employee not found
        </Alert>
      </Container>
    );
  }

  const canEdit = hasRole('ADMIN') || hasRole('HR');

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/employees')}
          sx={{ mb: 2 }}
        >
          Back to Employees
        </Button>
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h4" component="h1">
            {employee.firstName} {employee.lastName}
          </Typography>
          <Box sx={{ display: 'flex', gap: 2 }}>
            {canEdit && (
              <Button
                variant="contained"
                startIcon={<Edit />}
                onClick={handleEdit}
              >
                Edit Employee
              </Button>
            )}
            {hasRole('ADMIN') && employee && employee.userType !== 'ADMIN' && (
              <Button
                variant="outlined"
                color="secondary"
                startIcon={<AdminPanelSettings />}
                onClick={handlePromoteToAdmin}
                disabled={promoteLoading}
              >
                {promoteLoading ? 'Promoting...' : 'Promote to Admin'}
              </Button>
            )}
          </Box>
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Personal Information */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Personal Information
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Person sx={{ mr: 1, color: 'text.secondary' }} />
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        Full Name
                      </Typography>
                      <Typography variant="body1">
                        {employee.firstName} {employee.lastName}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Email sx={{ mr: 1, color: 'text.secondary' }} />
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        Email
                      </Typography>
                      <Typography variant="body1">
                        {employee.email}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>
                
                {employee.phone && (
                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <Phone sx={{ mr: 1, color: 'text.secondary' }} />
                      <Box>
                        <Typography variant="body2" color="text.secondary">
                          Phone
                        </Typography>
                        <Typography variant="body1">
                          {employee.phone}
                        </Typography>
                      </Box>
                    </Box>
                  </Grid>
                )}
                
                {employee.address && (
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Address
                    </Typography>
                    <Typography variant="body1">
                      {employee.address}
                    </Typography>
                  </Grid>
                )}
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Employment Information */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Employment Details
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Department
                </Typography>
                <Chip
                  label={employee.department?.replace(/_/g, ' ')}
                  color="primary"
                  variant="outlined"
                  sx={{ mt: 0.5 }}
                />
              </Box>
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Position
                </Typography>
                <Typography variant="body1">
                  {employee.position}
                </Typography>
              </Box>
              
              {employee.hireDate && (
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <CalendarToday sx={{ mr: 1, color: 'text.secondary' }} />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Hire Date
                    </Typography>
                    <Typography variant="body1">
                      {formatDate(employee.hireDate)}
                    </Typography>
                  </Box>
                </Box>
              )}
              
              {employee.salary && (
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <AttachMoney sx={{ mr: 1, color: 'text.secondary' }} />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Salary
                    </Typography>
                    <Typography variant="body1">
                      {formatCurrency(employee.salary)}
                    </Typography>
                  </Box>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Projects */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Assigned Projects
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              {projects.length === 0 ? (
                <Typography variant="body2" color="text.secondary">
                  No projects assigned
                </Typography>
              ) : (
                <Grid container spacing={2}>
                  {projects.map((project) => (
                    <Grid item xs={12} sm={6} md={4} key={project.id}>
                      <Paper
                        sx={{
                          p: 2,
                          cursor: 'pointer',
                          '&:hover': {
                            boxShadow: 2,
                          },
                        }}
                        onClick={() => navigate(`/projects/${project.id}`)}
                      >
                        <Typography variant="subtitle1" gutterBottom>
                          {project.name}
                        </Typography>
                        <Chip
                          label={project.status?.replace(/_/g, ' ')}
                          size="small"
                          color={project.status === 'COMPLETED' ? 'success' : 'primary'}
                        />
                      </Paper>
                    </Grid>
                  ))}
                </Grid>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Edit Employee Dialog */}
      <EmployeeForm
        open={editDialogOpen}
        onClose={() => setEditDialogOpen(false)}
        onSubmit={handleUpdateEmployee}
        employee={employee}
        loading={updateLoading}
      />
    </Container>
  );
};

export default EmployeeDetail;
