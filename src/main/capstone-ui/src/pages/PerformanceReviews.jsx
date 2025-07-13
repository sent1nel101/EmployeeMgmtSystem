import React, { useState, useEffect, useCallback } from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Paper,
  Chip,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Rating
} from '@mui/material';
import { Add, Assessment, CheckCircle, Schedule } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import DataTable from '../components/common/DataTable';
import SearchBar from '../components/common/SearchBar';
import PerformanceForm from '../components/performance/PerformanceForm';
import performanceService from '../services/performanceService';
import employeeService from '../services/employeeService';
import { formatDate } from '../utils/formatters';
import { useAuth } from '../hooks/useAuth.jsx';

const PerformanceReviews = () => {
  const navigate = useNavigate();
  const { user, hasRole } = useAuth();
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [formDialogOpen, setFormDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedReview, setSelectedReview] = useState(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [formLoading, setFormLoading] = useState(false);
  const [employees, setEmployees] = useState([]);

  const fetchReviews = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      
      const params = {
        page,
        size: rowsPerPage,
        ...(searchTerm && { search: searchTerm }),
        ...(statusFilter && { status: statusFilter })
      };

      let response;
      if (hasRole('ADMIN') || hasRole('HR')) {
        // Admin/HR can see all reviews
        response = await performanceService.getAllReviews(params);
      } else if (hasRole('MANAGER')) {
        // Managers can see reviews for their team
        response = await performanceService.getReviewsByReviewer(user.id, params);
      } else {
        // Employees can see their own reviews
        response = await performanceService.getEmployeeReviews(user.id, params);
      }

      setReviews(response.content || []);
      setTotalCount(response.totalElements || 0);
    } catch (error) {
      setError('Failed to load performance reviews');
      console.error('Performance reviews fetch error:', error);
    } finally {
      setLoading(false);
    }
  }, [page, rowsPerPage, searchTerm, statusFilter, hasRole, user.id]);

  useEffect(() => {
    fetchReviews();
  }, [fetchReviews]);

  useEffect(() => {
    // Fetch employees for form dropdown
    const fetchEmployees = async () => {
      if (hasRole('ADMIN') || hasRole('HR') || hasRole('MANAGER')) {
        try {
          const response = await employeeService.getAllEmployees({ size: 1000 });
          setEmployees(response.content || []);
        } catch (error) {
          console.error('Failed to fetch employees:', error);
        }
      }
    };

    fetchEmployees();
  }, [hasRole]);

  const handleSearch = (term) => {
    setSearchTerm(term);
    setPage(0);
  };

  const handleStatusFilter = (status) => {
    setStatusFilter(status);
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const handleRowsPerPageChange = (newRowsPerPage) => {
    setRowsPerPage(newRowsPerPage);
    setPage(0);
  };

  const handleAdd = () => {
    setSelectedReview(null);
    setFormDialogOpen(true);
  };

  const handleEdit = (review) => {
    setSelectedReview(review);
    setFormDialogOpen(true);
  };

  const handleView = (review) => {
    navigate(`/performance/${review.id}`);
  };

  const handleDelete = (review) => {
    setSelectedReview(review);
    setDeleteDialogOpen(true);
  };

  const handleSubmitForm = async (formData) => {
    setFormLoading(true);
    try {
      if (selectedReview) {
        await performanceService.updateReview(selectedReview.id, formData);
      } else {
        await performanceService.createReview(formData);
      }
      fetchReviews();
      setFormDialogOpen(false);
    } catch (error) {
      throw error;
    } finally {
      setFormLoading(false);
    }
  };

  const confirmDelete = async () => {
    if (!selectedReview) return;

    try {
      await performanceService.deleteReview(selectedReview.id);
      setDeleteDialogOpen(false);
      setSelectedReview(null);
      fetchReviews();
    } catch (error) {
      setError('Failed to delete performance review');
      console.error('Delete error:', error);
    }
  };

  const handleApprove = async (review) => {
    try {
      await performanceService.approveReview(review.id);
      fetchReviews();
    } catch (error) {
      setError('Failed to approve review');
      console.error('Approve error:', error);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'DRAFT':
        return 'default';
      case 'IN_REVIEW':
        return 'warning';
      case 'COMPLETED':
        return 'success';
      default:
        return 'default';
    }
  };

  const columns = [
    {
      field: 'employeeName',
      headerName: 'Employee',
      minWidth: 180,
      render: (value, row) => `${row.employee?.firstName || ''} ${row.employee?.lastName || ''}`,
    },
    {
      field: 'reviewPeriodStart',
      headerName: 'Period Start',
      minWidth: 120,
      render: (value) => formatDate(value),
    },
    {
      field: 'reviewPeriodEnd',
      headerName: 'Period End',
      minWidth: 120,
      render: (value) => formatDate(value),
    },
    {
      field: 'overallRating',
      headerName: 'Rating',
      minWidth: 150,
      render: (value) => (
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Rating value={value || 0} readOnly size="small" />
          <Typography variant="body2" sx={{ ml: 1 }}>
            ({value || 0})
          </Typography>
        </Box>
      ),
    },
    {
      field: 'status',
      headerName: 'Status',
      minWidth: 120,
      render: (value) => (
        <Chip
          label={value?.replace(/_/g, ' ')}
          size="small"
          color={getStatusColor(value)}
          variant="outlined"
        />
      ),
    },
    {
      field: 'createdAt',
      headerName: 'Created',
      minWidth: 120,
      render: (value) => formatDate(value),
    },
  ];

  const canCreateReview = hasRole('ADMIN') || hasRole('HR') || hasRole('MANAGER');
  const canEditReview = hasRole('ADMIN') || hasRole('HR');
  const canDeleteReview = hasRole('ADMIN');

  return (
    <Container maxWidth="xl">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Performance Reviews
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Manage employee performance reviews and assessments
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={4}>
            <SearchBar
              placeholder="Search reviews..."
              onSearch={handleSearch}
              value={searchTerm}
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Status</InputLabel>
              <Select
                value={statusFilter}
                onChange={(e) => handleStatusFilter(e.target.value)}
                label="Status"
              >
                <MenuItem value="">All Statuses</MenuItem>
                <MenuItem value="DRAFT">Draft</MenuItem>
                <MenuItem value="IN_REVIEW">In Review</MenuItem>
                <MenuItem value="COMPLETED">Completed</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={5} sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            {canCreateReview && (
              <Button
                variant="contained"
                startIcon={<Add />}
                onClick={handleAdd}
              >
                Create Review
              </Button>
            )}
          </Grid>
        </Grid>
      </Paper>

      <DataTable
        columns={columns}
        data={reviews}
        loading={loading}
        page={page}
        rowsPerPage={rowsPerPage}
        totalCount={totalCount}
        onPageChange={handlePageChange}
        onRowsPerPageChange={handleRowsPerPageChange}
        onEdit={canEditReview ? handleEdit : null}
        onView={handleView}
        onDelete={canDeleteReview ? handleDelete : null}
        emptyMessage="No performance reviews found"
      />

      {/* Performance Review Form Dialog */}
      <PerformanceForm
        open={formDialogOpen}
        onClose={() => setFormDialogOpen(false)}
        onSubmit={handleSubmitForm}
        review={selectedReview}
        loading={formLoading}
      />

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete this performance review? This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button onClick={confirmDelete} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default PerformanceReviews;
