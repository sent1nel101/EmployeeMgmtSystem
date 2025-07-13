import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  Grid,
  Card,
  CardContent,
  Avatar,
  Divider,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  ArrowBack,
  Edit,
  Person,
  Email,
  Security,
  Dashboard as DashboardIcon
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import employeeService from '../services/employeeService';

const Profile = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [employeeData, setEmployeeData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchEmployeeProfile = async () => {
      if (!user?.email) {
        setLoading(false);
        return;
      }

      try {
        // Try to find the employee record by email
        const employeesResponse = await employeeService.getAllEmployees();
        // Handle both array and paginated response formats
        const employees = Array.isArray(employeesResponse) 
          ? employeesResponse 
          : employeesResponse.content || [];
        
        const currentEmployee = employees.find(emp => emp.email === user.email);
        
        if (currentEmployee) {
          setEmployeeData(currentEmployee);
        }
      } catch (error) {
        console.error('Error fetching employee profile:', error);
        setError('Unable to load profile information');
      } finally {
        setLoading(false);
      }
    };

    fetchEmployeeProfile();
  }, [user]);

  const getInitials = (firstName, lastName) => {
    return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`.toUpperCase();
  };

  if (loading) {
    return (
      <Container maxWidth="md">
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '50vh' }}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Box sx={{ mb: 4 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/dashboard')}
          sx={{ mb: 2 }}
        >
          Back to Dashboard
        </Button>
        
        <Typography variant="h4" component="h1" gutterBottom>
          My Profile
        </Typography>
        <Typography variant="body1" color="text.secondary">
          View and manage your profile information
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Profile Overview Card */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ textAlign: 'center', p: 3 }}>
              <Avatar
                sx={{
                  width: 80,
                  height: 80,
                  bgcolor: 'primary.main',
                  fontSize: '2rem',
                  mx: 'auto',
                  mb: 2
                }}
              >
                {user ? getInitials(user.firstName, user.lastName) : <Person />}
              </Avatar>
              
              <Typography variant="h6" gutterBottom>
                {user ? `${user.firstName} ${user.lastName}` : 'User Profile'}
              </Typography>
              
              <Typography variant="body2" color="text.secondary" gutterBottom>
                {user?.email}
              </Typography>
              
              <Typography variant="body2" color="text.secondary">
                Role: {user?.roles?.[0] || 'Employee'}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Profile Details Card */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h6">
                  Account Information
                </Typography>
                {employeeData && (
                  <Button
                    startIcon={<Edit />}
                    variant="outlined"
                    size="small"
                    onClick={() => navigate(`/employees/edit/${employeeData.id}`)}
                  >
                    Edit Profile
                  </Button>
                )}
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      First Name
                    </Typography>
                    <Typography variant="body1">
                      {user?.firstName || 'Not available'}
                    </Typography>
                  </Box>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Last Name
                    </Typography>
                    <Typography variant="body1">
                      {user?.lastName || 'Not available'}
                    </Typography>
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Email Address
                    </Typography>
                    <Typography variant="body1">
                      {user?.email || 'Not available'}
                    </Typography>
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Username
                    </Typography>
                    <Typography variant="body1">
                      {user?.username || 'Not available'}
                    </Typography>
                  </Box>
                </Grid>

                {employeeData && (
                  <>
                    <Grid item xs={12}>
                      <Divider sx={{ my: 2 }} />
                      <Typography variant="h6" gutterBottom>
                        Employee Information
                      </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <Box sx={{ mb: 2 }}>
                        <Typography variant="body2" color="text.secondary">
                          Department
                        </Typography>
                        <Typography variant="body1">
                          {employeeData.department || 'Not assigned'}
                        </Typography>
                      </Box>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <Box sx={{ mb: 2 }}>
                        <Typography variant="body2" color="text.secondary">
                          Phone Number
                        </Typography>
                        <Typography variant="body1">
                          {employeeData.phoneNumber || 'Not provided'}
                        </Typography>
                      </Box>
                    </Grid>

                    {employeeData.hireDate && (
                      <Grid item xs={12} sm={6}>
                        <Box sx={{ mb: 2 }}>
                          <Typography variant="body2" color="text.secondary">
                            Hire Date
                          </Typography>
                          <Typography variant="body1">
                            {new Date(employeeData.hireDate).toLocaleDateString()}
                          </Typography>
                        </Box>
                      </Grid>
                    )}

                    {employeeData.salary && (
                      <Grid item xs={12} sm={6}>
                        <Box sx={{ mb: 2 }}>
                          <Typography variant="body2" color="text.secondary">
                            Salary
                          </Typography>
                          <Typography variant="body1">
                            ${employeeData.salary?.toLocaleString()}
                          </Typography>
                        </Box>
                      </Grid>
                    )}
                  </>
                )}
              </Grid>

              {!employeeData && (
                <Alert severity="info" sx={{ mt: 2 }}>
                  Employee profile information is not available. This may be because your account is not linked to an employee record.
                </Alert>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Quick Actions Card */}
        <Grid item xs={12}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Quick Actions
              </Typography>
              
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6} md={3}>
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<DashboardIcon />}
                    onClick={() => navigate('/dashboard')}
                  >
                    Dashboard
                  </Button>
                </Grid>
                
                {employeeData && (
                  <Grid item xs={12} sm={6} md={3}>
                    <Button
                      fullWidth
                      variant="outlined"
                      startIcon={<Person />}
                      onClick={() => navigate(`/employees/${employeeData.id}`)}
                    >
                      View Full Profile
                    </Button>
                  </Grid>
                )}
                
                <Grid item xs={12} sm={6} md={3}>
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<Security />}
                    onClick={() => navigate('/forgot-password')}
                  >
                    Change Password
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Profile;
