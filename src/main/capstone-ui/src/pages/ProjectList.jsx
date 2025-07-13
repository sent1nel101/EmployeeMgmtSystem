import React, { useState, useEffect, useCallback } from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Paper,
  Chip,
  Alert,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import { Add, Work } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import DataTable from '../components/common/DataTable';
import SearchBar from '../components/common/SearchBar';
import projectService from '../services/projectService';
import { formatDate, formatCurrency } from '../utils/formatters';
import { PROJECT_STATUS, PROJECT_PRIORITY } from '../utils/constants';
import { useAuth } from '../hooks/useAuth.jsx';

const ProjectList = () => {
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [statusFilter, setStatusFilter] = useState('');

  const fetchProjects = useCallback(async () => {
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
      if (searchTerm) {
        response = await projectService.searchProjects(searchTerm, params);
      } else if (statusFilter) {
        response = await projectService.getProjectsByStatus(statusFilter, params);
      } else {
        response = await projectService.getAllProjects(params);
      }

      setProjects(response.content || []);
      setTotalCount(response.totalElements || 0);
    } catch (error) {
      setError('Failed to load projects');
      console.error('Project fetch error:', error);
    } finally {
      setLoading(false);
    }
  }, [page, rowsPerPage, searchTerm, statusFilter]);

  useEffect(() => {
    fetchProjects();
  }, [fetchProjects]);

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

  const handleView = (project) => {
    navigate(`/projects/${project.id}`);
  };

  const handleEdit = (project) => {
    navigate(`/projects/${project.id}`);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PLANNING':
        return 'default';
      case 'IN_PROGRESS':
        return 'primary';
      case 'ON_HOLD':
        return 'warning';
      case 'COMPLETED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'LOW':
        return 'success';
      case 'MEDIUM':
        return 'warning';
      case 'HIGH':
        return 'error';
      case 'CRITICAL':
        return 'error';
      default:
        return 'default';
    }
  };

  const columns = [
    {
      field: 'name',
      headerName: 'Project Name',
      minWidth: 200,
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
      field: 'priority',
      headerName: 'Priority',
      minWidth: 100,
      render: (value) => (
        <Chip
          label={value}
          size="small"
          color={getPriorityColor(value)}
          variant="filled"
        />
      ),
    },
    {
      field: 'startDate',
      headerName: 'Start Date',
      minWidth: 120,
      render: (value) => formatDate(value),
    },
    {
      field: 'endDate',
      headerName: 'End Date',
      minWidth: 120,
      render: (value) => formatDate(value),
    },
    {
      field: 'budget',
      headerName: 'Budget',
      minWidth: 120,
      align: 'right',
      render: (value) => value ? formatCurrency(value) : '-',
    },
    {
      field: 'manager',
      headerName: 'Manager',
      minWidth: 150,
      render: (value, row) => row.manager ? `${row.manager.firstName} ${row.manager.lastName}` : '-',
    },
  ];

  const canAddProject = hasRole('ADMIN') || hasRole('MANAGER');
  const canEditProject = hasRole('ADMIN') || hasRole('MANAGER');

  return (
    <Container maxWidth="xl">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Projects
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Manage project information and timelines
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
              placeholder="Search projects..."
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
                {Object.values(PROJECT_STATUS).map((status) => (
                  <MenuItem key={status} value={status}>
                    {status.replace(/_/g, ' ')}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={5} sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            {canAddProject && (
              <Button
                variant="contained"
                startIcon={<Add />}
                onClick={() => navigate('/projects/new')}
              >
                Add Project
              </Button>
            )}
          </Grid>
        </Grid>
      </Paper>

      <DataTable
        columns={columns}
        data={projects}
        loading={loading}
        page={page}
        rowsPerPage={rowsPerPage}
        totalCount={totalCount}
        onPageChange={handlePageChange}
        onRowsPerPageChange={handleRowsPerPageChange}
        onEdit={canEditProject ? handleEdit : null}
        onView={handleView}
        emptyMessage="No projects found"
      />
    </Container>
  );
};

export default ProjectList;
