import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
  Grid,
  MenuItem
} from '@mui/material';
import authService from '../../services/authService';
import { validateEmail, validateRequired } from '../../utils/validators';
import { DEPARTMENTS } from '../../utils/constants';

const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    password: '',
    confirmPassword: '',
    department: '',
    position: '',
    phoneNumber: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  const [generatedEmail, setGeneratedEmail] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    const updatedFormData = {
      ...formData,
      [name]: value
    };
    setFormData(updatedFormData);
    
    // Generate email when first or last name changes
    if (name === 'firstName' || name === 'lastName') {
      if (updatedFormData.firstName && updatedFormData.lastName) {
        const username = updatedFormData.firstName.substring(0, 1).toLowerCase() + '.' + updatedFormData.lastName.toLowerCase();
        setGeneratedEmail(username + '@ourcompany.com');
      } else {
        setGeneratedEmail('');
      }
    }
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!validateRequired(formData.firstName)) {
      newErrors.firstName = 'First name is required';
    }
    
    if (!validateRequired(formData.lastName)) {
      newErrors.lastName = 'Last name is required';
    }
    
    if (!validateRequired(formData.password)) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }
    
    if (!validateRequired(formData.confirmPassword)) {
      newErrors.confirmPassword = 'Please confirm your password';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }
    
    if (!validateRequired(formData.department)) {
      newErrors.department = 'Department is required';
    }
    
    if (!validateRequired(formData.position)) {
      newErrors.position = 'Position is required';
    }
    
    if (!validateRequired(formData.phoneNumber)) {
      newErrors.phoneNumber = 'Phone number is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    
    if (!validateForm()) {
      return;
    }
    
    setLoading(true);
    
    try {
      const { confirmPassword, ...registerData } = formData;
      
      // Auto-generate username and email
      const username = formData.firstName.substring(0, 1).toLowerCase() + '.' + formData.lastName.toLowerCase();
      registerData.username = username;
      registerData.email = username + '@ourcompany.com';
      
      await authService.register(registerData);
      
      // Show success message with login credentials
      alert(`Registration successful!\n\nYour login credentials:\nEmail: ${registerData.email}\nPassword: [as entered]\n\nPlease save these credentials for future logins.`);
      
      navigate('/login');
    } catch (error) {
      setApiError(
        error.response?.data?.message || 
        'Registration failed. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="md">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
          <Typography component="h1" variant="h4" align="center" gutterBottom>
            Sign Up
          </Typography>
          
          {apiError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {apiError}
            </Alert>
          )}
          
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="firstName"
                  label="First Name"
                  name="firstName"
                  autoComplete="given-name"
                  value={formData.firstName}
                  onChange={handleChange}
                  error={!!errors.firstName}
                  helperText={errors.firstName}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="lastName"
                  label="Last Name"
                  name="lastName"
                  autoComplete="family-name"
                  value={formData.lastName}
                  onChange={handleChange}
                  error={!!errors.lastName}
                  helperText={errors.lastName}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  name="password"
                  label="Password"
                  type="password"
                  id="password"
                  autoComplete="new-password"
                  value={formData.password}
                  onChange={handleChange}
                  error={!!errors.password}
                  helperText={errors.password}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  name="confirmPassword"
                  label="Confirm Password"
                  type="password"
                  id="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  error={!!errors.confirmPassword}
                  helperText={errors.confirmPassword}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  select
                  id="department"
                  label="Department"
                  name="department"
                  value={formData.department}
                  onChange={handleChange}
                  error={!!errors.department}
                  helperText={errors.department}
                >
                  {Object.values(DEPARTMENTS).map((dept) => (
                    <MenuItem key={dept} value={dept}>
                      {dept.toLowerCase().replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase())}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="position"
                  label="Position"
                  name="position"
                  value={formData.position}
                  onChange={handleChange}
                  error={!!errors.position}
                  helperText={errors.position}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="phoneNumber"
                  label="Phone Number"
                  name="phoneNumber"
                  type="tel"
                  autoComplete="tel"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                  error={!!errors.phoneNumber}
                  helperText={errors.phoneNumber}
                />
              </Grid>
              
              {generatedEmail && (
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    id="generatedEmail"
                    label="Your Generated Email Address"
                    value={generatedEmail}
                    InputProps={{
                      readOnly: true,
                    }}
                    helperText="This will be your login email address. Please save it for future logins."
                    sx={{ 
                      '& .MuiInputBase-root': {
                        backgroundColor: 'action.hover',
                        '&:hover': {
                          backgroundColor: 'action.hover',
                        }
                      },
                      '& .MuiInputBase-input': {
                        color: 'text.secondary',
                        fontStyle: 'italic'
                      },
                      '& .MuiInputLabel-root': {
                        color: 'text.secondary'
                      },
                      '& .MuiFormHelperText-root': {
                        color: 'text.secondary'
                      }
                    }}
                  />
                </Grid>
              )}
            </Grid>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
            >
              {loading ? <CircularProgress size={24} /> : 'Sign Up'}
            </Button>
            <Box textAlign="center">
              <Link to="/login" style={{ textDecoration: 'none' }}>
                <Typography variant="body2" color="primary">
                  Already have an account? Sign In
                </Typography>
              </Link>
            </Box>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Register;
