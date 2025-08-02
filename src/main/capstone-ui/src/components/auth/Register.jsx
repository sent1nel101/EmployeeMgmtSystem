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

  // Utility function to clean names for email generation
  const cleanNameForEmail = (name) => {
    // Remove spaces, special characters, and normalize
    return name
      .trim()
      .toLowerCase()
      .replace(/[^a-z0-9]/g, '') // Remove all non-alphanumeric characters
      .substring(0, 20); // Limit length for practical email addresses
  };

  // Utility function to generate email safely
  const generateEmail = (firstName, lastName) => {
    const cleanFirst = cleanNameForEmail(firstName);
    const cleanLast = cleanNameForEmail(lastName);
    
    // Check if we have valid characters left after cleaning
    if (!cleanFirst || !cleanLast) {
      return null; // Invalid name combination
    }
    
    const username = cleanFirst.substring(0, 1) + '.' + cleanLast;
    return username + '@ourcompany.com';
  };

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
        const email = generateEmail(updatedFormData.firstName, updatedFormData.lastName);
        setGeneratedEmail(email || '');
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
    } else if (!cleanNameForEmail(formData.firstName)) {
      newErrors.firstName = 'First name must contain at least one letter or number';
    }
    
    if (!validateRequired(formData.lastName)) {
      newErrors.lastName = 'Last name is required';
    } else if (!cleanNameForEmail(formData.lastName)) {
      newErrors.lastName = 'Last name must contain at least one letter or number';
    }
    
    // Check if email generation is possible
    if (formData.firstName && formData.lastName && !generateEmail(formData.firstName, formData.lastName)) {
      newErrors.firstName = newErrors.firstName || 'Names must contain letters or numbers for email generation';
      newErrors.lastName = newErrors.lastName || 'Names must contain letters or numbers for email generation';
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
      
      // Auto-generate username and email using cleaned names
      const email = generateEmail(formData.firstName, formData.lastName);
      if (!email) {
        setApiError('Unable to generate valid email from provided names. Please use names with letters or numbers.');
        return;
      }
      
      const username = email.split('@')[0]; // Extract username part from email
      registerData.username = username;
      registerData.email = email;
      
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
              
              {(formData.firstName || formData.lastName) && (
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    id="generatedEmail"
                    label="Your Generated Email Address"
                    value={generatedEmail || 'Unable to generate email - please check your names'}
                    InputProps={{
                      readOnly: true,
                    }}
                    error={!generatedEmail && (formData.firstName && formData.lastName)}
                    helperText={
                      generatedEmail 
                        ? "This will be your login email address. Please save it for future logins."
                        : "Email cannot be generated. Names must contain letters or numbers (spaces and special characters will be removed)."
                    }
                    sx={{ 
                      '& .MuiInputBase-root': {
                        backgroundColor: 'action.hover',
                        '&:hover': {
                          backgroundColor: 'action.hover',
                        }
                      },
                      '& .MuiInputBase-input': {
                        color: generatedEmail ? 'text.secondary' : 'error.main',
                        fontStyle: 'italic'
                      },
                      '& .MuiInputLabel-root': {
                        color: generatedEmail ? 'text.secondary' : 'error.main'
                      },
                      '& .MuiFormHelperText-root': {
                        color: generatedEmail ? 'text.secondary' : 'error.main'
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
