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
import { validateProjectForm } from '../../utils/validators';
import { PROJECT_STATUS, PROJECT_PRIORITY } from '../../utils/constants';
import { formatDate } from '../../utils/formatters';

const ProjectForm = ({
  open,
  onClose,
  onSubmit,
  project = null,
  loading = false,
  title = null
}) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    startDate: '',
    endDate: '',
    budget: '',
    department: '',
    status: 'PLANNING',
    priority: 'MEDIUM',
    progressPercentage: 0
  });
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');

  const isEdit = Boolean(project);
  const dialogTitle = title || (isEdit ? 'Edit Project' : 'Add New Project');

  useEffect(() => {
    if (project) {
      setFormData({
        name: project.name || '',
        description: project.description || '',
        startDate: project.startDate ? formatDate(project.startDate, 'input') : '',
        endDate: project.endDate ? formatDate(project.endDate, 'input') : '',
        budget: project.budget?.toString() || '',
        department: project.department || '',
        status: project.status || 'PLANNING',
        priority: project.priority || 'MEDIUM',
        progressPercentage: project.progressPercentage || 0
      });
    } else {
      // Reset form for new project
      setFormData({
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        budget: '',
        department: '',
        status: 'PLANNING',
        priority: 'MEDIUM',
        progressPercentage: 0
      });
    }
    setErrors({});
    setSubmitError('');
  }, [project, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error for this field when user starts typing
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

    const validation = validateProjectForm(formData);
    if (!validation.isValid) {
      setErrors(validation.errors);
      return;
    }

    try {
      const submitData = {
        ...formData,
        budget: formData.budget ? parseFloat(formData.budget) : null,
        progressPercentage: parseInt(formData.progressPercentage) || 0
      };

      await onSubmit(submitData);
      handleClose();
    } catch (error) {
      setSubmitError(error.response?.data?.message || 'Failed to save project');
    }
  };

  const handleClose = () => {
    setFormData({
      name: '',
      description: '',
      startDate: '',
      endDate: '',
      budget: '',
      department: '',
      status: 'PLANNING',
      priority: 'MEDIUM',
      progressPercentage: 0
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
      PaperProps={{
        component: 'form',
        onSubmit: handleSubmit,
      }}
    >
      <DialogTitle>{dialogTitle}</DialogTitle>
      
      <DialogContent>
        {submitError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {submitError}
          </Alert>
        )}
        
        <Grid container spacing={2} sx={{ mt: 1 }}>
          <Grid item xs={12}>
            <TextField
              required
              fullWidth
              name="name"
              label="Project Name"
              value={formData.name}
              onChange={handleChange}
              error={!!errors.name}
              helperText={errors.name}
              disabled={loading}
            />
          </Grid>
          
          <Grid item xs={12}>
            <TextField
              required
              fullWidth
              name="description"
              label="Description"
              multiline
              rows={3}
              value={formData.description}
              onChange={handleChange}
              error={!!errors.description}
              helperText={errors.description}
              disabled={loading}
            />
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <TextField
              required
              fullWidth
              name="startDate"
              label="Start Date"
              type="date"
              value={formData.startDate}
              onChange={handleChange}
              error={!!errors.startDate}
              helperText={errors.startDate}
              disabled={loading}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <TextField
              required
              fullWidth
              name="endDate"
              label="End Date"
              type="date"
              value={formData.endDate}
              onChange={handleChange}
              error={!!errors.endDate}
              helperText={errors.endDate}
              disabled={loading}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <TextField
              required
              fullWidth
              name="budget"
              label="Budget"
              type="number"
              value={formData.budget}
              onChange={handleChange}
              error={!!errors.budget}
              helperText={errors.budget}
              disabled={loading}
              InputProps={{
                startAdornment: <Box component="span" sx={{ mr: 1 }}>$</Box>,
              }}
            />
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <TextField
              required
              fullWidth
              name="department"
              label="Department"
              value={formData.department}
              onChange={handleChange}
              error={!!errors.department}
              helperText={errors.department}
              disabled={loading}
            />
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <TextField
              required
              fullWidth
              name="status"
              label="Status"
              select
              value={formData.status}
              onChange={handleChange}
              error={!!errors.status}
              helperText={errors.status}
              disabled={loading}
            >
              {Object.values(PROJECT_STATUS).map((status) => (
                <MenuItem key={status} value={status}>
                  {status.replace(/_/g, ' ')}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
          
          <Grid item xs={12} sm={6}>
            <TextField
              required
              fullWidth
              name="priority"
              label="Priority"
              select
              value={formData.priority}
              onChange={handleChange}
              error={!!errors.priority}
              helperText={errors.priority}
              disabled={loading}
            >
              {Object.values(PROJECT_PRIORITY).map((priority) => (
                <MenuItem key={priority} value={priority}>
                  {priority}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
          
          {isEdit && (
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                name="progressPercentage"
                label="Progress Percentage"
                type="number"
                value={formData.progressPercentage}
                onChange={handleChange}
                error={!!errors.progressPercentage}
                helperText={errors.progressPercentage}
                disabled={loading}
                inputProps={{
                  min: 0,
                  max: 100
                }}
                InputProps={{
                  endAdornment: <Box component="span" sx={{ ml: 1 }}>%</Box>,
                }}
              />
            </Grid>
          )}
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
          startIcon={loading && <CircularProgress size={20} />}
        >
          {loading ? 'Saving...' : (isEdit ? 'Update' : 'Create')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ProjectForm;
