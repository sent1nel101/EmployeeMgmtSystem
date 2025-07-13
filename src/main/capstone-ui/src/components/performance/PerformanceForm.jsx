import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Grid,
  Box,
  Typography,
  Rating,
  Divider,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

const PerformanceForm = ({
  open,
  onClose,
  onSubmit,
  review = null,
  employee = null,
  loading = false
}) => {
  const [formData, setFormData] = useState({
    employeeId: '',
    reviewPeriodStart: null,
    reviewPeriodEnd: null,
    overallRating: 0,
    technicalSkills: 0,
    communication: 0,
    teamwork: 0,
    leadership: 0,
    problemSolving: 0,
    goals: '',
    achievements: '',
    areasForImprovement: '',
    managerComments: '',
    employeeComments: '',
    status: 'DRAFT'
  });
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');

  const isEdit = Boolean(review);

  useEffect(() => {
    if (review) {
      setFormData({
        employeeId: review.employeeId || '',
        reviewPeriodStart: review.reviewPeriodStart ? new Date(review.reviewPeriodStart) : null,
        reviewPeriodEnd: review.reviewPeriodEnd ? new Date(review.reviewPeriodEnd) : null,
        overallRating: review.overallRating || 0,
        technicalSkills: review.technicalSkills || 0,
        communication: review.communication || 0,
        teamwork: review.teamwork || 0,
        leadership: review.leadership || 0,
        problemSolving: review.problemSolving || 0,
        goals: review.goals || '',
        achievements: review.achievements || '',
        areasForImprovement: review.areasForImprovement || '',
        managerComments: review.managerComments || '',
        employeeComments: review.employeeComments || '',
        status: review.status || 'DRAFT'
      });
    } else if (employee) {
      setFormData(prev => ({
        ...prev,
        employeeId: employee.id
      }));
    }
  }, [review, employee, open]);

  const handleChange = (name, value) => {
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
    setSubmitError('');
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.employeeId) {
      newErrors.employeeId = 'Employee is required';
    }
    if (!formData.reviewPeriodStart) {
      newErrors.reviewPeriodStart = 'Review period start date is required';
    }
    if (!formData.reviewPeriodEnd) {
      newErrors.reviewPeriodEnd = 'Review period end date is required';
    }
    if (formData.reviewPeriodStart && formData.reviewPeriodEnd && 
        formData.reviewPeriodStart >= formData.reviewPeriodEnd) {
      newErrors.reviewPeriodEnd = 'End date must be after start date';
    }
    if (formData.overallRating === 0) {
      newErrors.overallRating = 'Overall rating is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (!validateForm()) {
      return;
    }

    try {
      await onSubmit(formData);
      handleClose();
    } catch (error) {
      setSubmitError(error.response?.data?.message || 'Failed to save performance review');
    }
  };

  const handleClose = () => {
    setFormData({
      employeeId: '',
      reviewPeriodStart: null,
      reviewPeriodEnd: null,
      overallRating: 0,
      technicalSkills: 0,
      communication: 0,
      teamwork: 0,
      leadership: 0,
      problemSolving: 0,
      goals: '',
      achievements: '',
      areasForImprovement: '',
      managerComments: '',
      employeeComments: '',
      status: 'DRAFT'
    });
    setErrors({});
    setSubmitError('');
    onClose();
  };

  const skillCategories = [
    { key: 'technicalSkills', label: 'Technical Skills' },
    { key: 'communication', label: 'Communication' },
    { key: 'teamwork', label: 'Teamwork' },
    { key: 'leadership', label: 'Leadership' },
    { key: 'problemSolving', label: 'Problem Solving' }
  ];

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Dialog open={open} onClose={handleClose} maxWidth="lg" fullWidth>
        <form onSubmit={handleSubmit}>
          <DialogTitle>
            {isEdit ? 'Edit Performance Review' : 'Create Performance Review'}
          </DialogTitle>
          
          <DialogContent>
            {submitError && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {submitError}
              </Alert>
            )}

            <Grid container spacing={3}>
              {/* Basic Information */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom>
                  Review Period
                </Typography>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <DatePicker
                  label="Review Period Start"
                  value={formData.reviewPeriodStart}
                  onChange={(value) => handleChange('reviewPeriodStart', value)}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                      error: !!errors.reviewPeriodStart,
                      helperText: errors.reviewPeriodStart
                    }
                  }}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <DatePicker
                  label="Review Period End"
                  value={formData.reviewPeriodEnd}
                  onChange={(value) => handleChange('reviewPeriodEnd', value)}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                      error: !!errors.reviewPeriodEnd,
                      helperText: errors.reviewPeriodEnd
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <Divider sx={{ my: 2 }} />
                <Typography variant="h6" gutterBottom>
                  Performance Ratings
                </Typography>
              </Grid>

              {/* Overall Rating */}
              <Grid item xs={12}>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="subtitle1" gutterBottom>
                    Overall Rating *
                  </Typography>
                  <Rating
                    value={formData.overallRating}
                    onChange={(event, newValue) => handleChange('overallRating', newValue)}
                    size="large"
                    precision={0.5}
                  />
                  {errors.overallRating && (
                    <Typography variant="body2" color="error" sx={{ mt: 1 }}>
                      {errors.overallRating}
                    </Typography>
                  )}
                </Box>
              </Grid>

              {/* Skill Ratings */}
              {skillCategories.map((skill) => (
                <Grid item xs={12} sm={6} key={skill.key}>
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="subtitle2" gutterBottom>
                      {skill.label}
                    </Typography>
                    <Rating
                      value={formData[skill.key]}
                      onChange={(event, newValue) => handleChange(skill.key, newValue)}
                      precision={0.5}
                    />
                  </Box>
                </Grid>
              ))}

              <Grid item xs={12}>
                <Divider sx={{ my: 2 }} />
                <Typography variant="h6" gutterBottom>
                  Review Details
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  name="goals"
                  label="Goals for Next Period"
                  value={formData.goals}
                  onChange={(e) => handleChange('goals', e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  name="achievements"
                  label="Key Achievements"
                  value={formData.achievements}
                  onChange={(e) => handleChange('achievements', e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  name="areasForImprovement"
                  label="Areas for Improvement"
                  value={formData.areasForImprovement}
                  onChange={(e) => handleChange('areasForImprovement', e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  name="managerComments"
                  label="Manager Comments"
                  value={formData.managerComments}
                  onChange={(e) => handleChange('managerComments', e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  name="employeeComments"
                  label="Employee Comments"
                  value={formData.employeeComments}
                  onChange={(e) => handleChange('employeeComments', e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel>Status</InputLabel>
                  <Select
                    value={formData.status}
                    onChange={(e) => handleChange('status', e.target.value)}
                    label="Status"
                  >
                    <MenuItem value="DRAFT">Draft</MenuItem>
                    <MenuItem value="IN_REVIEW">In Review</MenuItem>
                    <MenuItem value="COMPLETED">Completed</MenuItem>
                  </Select>
                </FormControl>
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
              {loading ? 'Saving...' : (isEdit ? 'Update Review' : 'Create Review')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </LocalizationProvider>
  );
};

export default PerformanceForm;
