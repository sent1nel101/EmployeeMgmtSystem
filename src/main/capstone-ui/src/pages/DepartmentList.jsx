import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Chip,
  Alert,
  CircularProgress,
  Paper
} from '@mui/material';
import {
  People,
  Business,
  TrendingUp,
  Visibility
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import employeeService from '../services/employeeService';
import { DEPARTMENTS } from '../utils/constants';
import { useAuth } from '../hooks/useAuth.jsx';

const DepartmentList = () => {
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      setLoading(true);
      setError('');
      
      // Get employee counts for each department
      const departmentPromises = Object.values(DEPARTMENTS).map(async (dept) => {
        try {
          const response = await employeeService.getEmployeesByDepartment(dept, { size: 1 });
          return {
            name: dept,
            displayName: dept.replace(/_/g, ' '),
            employeeCount: response.totalElements || 0,
            employees: response.content || []
          };
        } catch (error) {
          console.error(`Error fetching ${dept} data:`, error);
          return {
            name: dept,
            displayName: dept.replace(/_/g, ' '),
            employeeCount: 0,
            employees: []
          };
        }
      });

      const departmentData = await Promise.all(departmentPromises);
      setDepartments(departmentData);
    } catch (error) {
      setError('Failed to load departments');
      console.error('Department fetch error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleViewDepartment = (department) => {
    navigate(`/employees?department=${department.name}`);
  };

  const getDepartmentColor = (index) => {
    const colors = ['primary', 'secondary', 'success', 'warning', 'info', 'error'];
    return colors[index % colors.length];
  };

  const getDepartmentIcon = (departmentName) => {
    const icons = {
      ENGINEERING: '🔧',
      MARKETING: '📈',
      SALES: '💼',
      HR: '👥',
      FINANCE: '💰',
      OPERATIONS: '⚙️'
    };
    return icons[departmentName] || '🏢';
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

  const canViewEmployees = hasRole('ADMIN') || hasRole('HR') || hasRole('MANAGER');

  return (
    <Container maxWidth="xl">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Departments
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Overview of company departments and employee distribution
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Department Statistics */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={4}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h3" color="primary">
                {departments.length}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Total Departments
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h3" color="success.main">
                {departments.reduce((sum, dept) => sum + dept.employeeCount, 0)}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Total Employees
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h3" color="warning.main">
                {departments.length > 0 ? Math.round(departments.reduce((sum, dept) => sum + dept.employeeCount, 0) / departments.length) : 0}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Average per Department
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Department Cards */}
      <Grid container spacing={3}>
        {departments.map((department, index) => (
          <Grid item xs={12} sm={6} md={4} key={department.name}>
            <Card
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
                '&:hover': {
                  transform: 'translateY(-4px)',
                  boxShadow: 8,
                },
              }}
            >
              <CardContent sx={{ flexGrow: 1 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Box
                    sx={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      width: 48,
                      height: 48,
                      borderRadius: '50%',
                      backgroundColor: `${getDepartmentColor(index)}.light`,
                      mr: 2,
                      fontSize: '24px'
                    }}
                  >
                    {getDepartmentIcon(department.name)}
                  </Box>
                  <Typography variant="h6" component="div">
                    {department.displayName}
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <People sx={{ mr: 1, color: 'text.secondary' }} />
                  <Typography variant="h4" component="div" color="primary">
                    {department.employeeCount}
                  </Typography>
                  <Typography variant="body2" sx={{ ml: 1 }} color="text.secondary">
                    employees
                  </Typography>
                </Box>

                <Chip
                  label={department.employeeCount > 0 ? 'Active' : 'No Employees'}
                  color={department.employeeCount > 0 ? 'success' : 'default'}
                  size="small"
                  variant="outlined"
                />
              </CardContent>

              <CardActions>
                {canViewEmployees && (
                  <Button
                    size="small"
                    startIcon={<Visibility />}
                    onClick={() => handleViewDepartment(department)}
                    disabled={department.employeeCount === 0}
                  >
                    View Employees
                  </Button>
                )}
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Empty State */}
      {departments.length === 0 && !loading && (
        <Paper sx={{ p: 8, textAlign: 'center' }}>
          <Business sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No Departments Found
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Departments will appear here as employees are added to the system.
          </Typography>
        </Paper>
      )}
    </Container>
  );
};

export default DepartmentList;
