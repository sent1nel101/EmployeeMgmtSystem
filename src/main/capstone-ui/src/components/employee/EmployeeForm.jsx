import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Grid,
  MenuItem,
  Box,
  CircularProgress,
  Alert
} from '@mui/material';
import { validateEmployeeForm } from '../../utils/validators';
import { DEPARTMENTS } from '../../utils/constants';
import { formatDate } from '../../utils/formatters';

const EmployeeForm = ({
  open,
  onClose,
  onSubmit,
  employee = null,
  loading = false,
  title = null
}) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    department: '',
    userRole: 'EMPLOYEE', // Access level: ADMIN/MANAGER/EMPLOYEE
    position: '', // Job title: Senior Developer, etc.
    hireDate: '',
    salary: '',
    phoneNumber: '',
    password: ''
  });
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');

  const isEdit = Boolean(employee);
  const dialogTitle = title || (isEdit ? 'Edit Employee' : 'Add New Employee');

  useEffect(() => {
    if (employee) {
      setFormData({
        firstName: employee.firstName || '',
        lastName: employee.lastName || '',
        department: employee.department || '',
        userRole: employee.userType || 'EMPLOYEE', // Access level from userType
        position: employee.position || '', // Job title from position
        hireDate: employee.hireDate ? formatDate(employee.hireDate, 'input') : '',
        salary: employee.salary?.toString() || '',
        phoneNumber: employee.phoneNumber || '',
        password: '' // Don't populate password for security
      });
    } else {
      // Reset form for new employee
      setFormData({
        firstName: '',
        lastName: '',
        department: '',
        userRole: 'EMPLOYEE',
        position: '',
        hireDate: '',
        salary: '',
        phoneNumber: '',
        password: ''
      });
    }
    setErrors({});
    setSubmitError('');
  }, [employee, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
    setSubmitError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    const validation = validateEmployeeForm(formData, isEdit);
    if (!validation.isValid) {
      setErrors(validation.errors);
      return;
    }

    try {
      const submitData = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        department: formData.department,
        userRole: formData.userRole, // Access level (ADMIN/MANAGER/EMPLOYEE)
        position: formData.position, // Job title
        hireDate: formData.hireDate,
        salary: formData.salary ? parseFloat(formData.salary) : null,
        phoneNumber: formData.phoneNumber
      };
      
      // Add password only if provided
      if (formData.password) {
        submitData.password = formData.password;
      }

      // Auto-generate username and email for new employees
      if (!isEdit) {
        const username = formData.firstName.substring(0, 1).toLowerCase() + '.' + formData.lastName.toLowerCase();
        submitData.username = username;
        submitData.email = username + '@ourcompany.com';
      }

      await onSubmit(submitData);
      handleClose();
    } catch (error) {
      setSubmitError(error.response?.data?.message || 'Failed to save employee');
    }
  };

  const handleClose = () => {
    setFormData({
      firstName: '',
      lastName: '',
      department: '',
      role: '',
      hireDate: '',
      salary: '',
      phoneNumber: '',
      password: ''
    });
    setErrors({});
    setSubmitError('');
    onClose();
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleClose} 
      maxWidth="md" 
      fullWidth
      sx={{
        '& .MuiDialog-paper': {
          margin: { xs: 2, sm: 3 },
          width: { xs: 'calc(100% - 32px)', sm: 'auto' },
          maxHeight: { xs: 'calc(100% - 64px)', sm: 'calc(100% - 96px)' }
        }
      }}
    >
      <form onSubmit={handleSubmit}>
        <DialogTitle>{dialogTitle}</DialogTitle>
        
        <DialogContent>
          {submitError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {submitError}
            </Alert>
          )}
          
          <Grid container spacing={{ xs: 2, sm: 3 }} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                name="firstName"
                label="First Name"
                value={formData.firstName}
                onChange={handleChange}
                error={!!errors.firstName}
                helperText={errors.firstName}
                disabled={loading}
                sx={{
                  '& .MuiInputBase-root': {
                    fontSize: { xs: '1rem', xl: '1.1rem' }
                  }
                }}
              />
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                name="lastName"
                label="Last Name"
                value={formData.lastName}
                onChange={handleChange}
                error={!!errors.lastName}
                helperText={errors.lastName}
                disabled={loading}
                sx={{
                  '& .MuiInputBase-root': {
                    fontSize: { xs: '1rem', xl: '1.1rem' }
                  }
                }}
              />
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                select
                name="department"
                label="Department"
                value={formData.department}
                onChange={handleChange}
                error={!!errors.department}
                helperText={errors.department}
                disabled={loading}
              >
                {Object.values(DEPARTMENTS).map((dept) => (
                  <MenuItem key={dept} value={dept}>
                    {dept.replace(/_/g, ' ')}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                select
                required
                fullWidth
                name="userRole"
                label="Role (Access Level)"
                value={formData.userRole}
                onChange={handleChange}
                error={!!errors.userRole}
                helperText={errors.userRole || "Select the employee's access level"}
                disabled={loading}
              >
                <MenuItem value="EMPLOYEE">Employee</MenuItem>
                <MenuItem value="MANAGER">Manager</MenuItem>
                <MenuItem value="ADMIN">Admin</MenuItem>
              </TextField>
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                name="position"
                label="Position / Job Title"
                placeholder="e.g., Senior Developer, Marketing Coordinator"
                value={formData.position}
                onChange={handleChange}
                error={!!errors.position}
                helperText={errors.position || "Enter the employee's job title or position"}
                disabled={loading}
              />
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                name="hireDate"
                label="Hire Date"
                type="date"
                value={formData.hireDate}
                onChange={handleChange}
                error={!!errors.hireDate}
                helperText={errors.hireDate}
                disabled={loading}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                name="salary"
                label="Salary"
                type="number"
                value={formData.salary}
                onChange={handleChange}
                error={!!errors.salary}
                helperText={errors.salary}
                disabled={loading}
                InputProps={{
                  startAdornment: '$'
                }}
              />
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                name="phoneNumber"
                label="Phone Number"
                value={formData.phoneNumber}
                onChange={handleChange}
                error={!!errors.phoneNumber}
                helperText={errors.phoneNumber}
                disabled={loading}
              />
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <TextField
                required={!isEdit}
                fullWidth
                name="password"
                label={isEdit ? "New Password (leave blank to keep current)" : "Password"}
                type="password"
                value={formData.password}
                onChange={handleChange}
                error={!!errors.password}
                helperText={errors.password}
                disabled={loading}
              />
            </Grid>
          </Grid>
        </DialogContent>
        
        <DialogActions>
          <Button onClick={handleClose} disabled={loading}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={loading}
            startIcon={loading ? <CircularProgress size={20} /> : null}
          >
            {loading ? 'Saving...' : (isEdit ? 'Update' : 'Create')}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default EmployeeForm;
