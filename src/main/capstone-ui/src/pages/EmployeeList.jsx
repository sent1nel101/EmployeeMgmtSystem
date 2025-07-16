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
  MenuItem
} from '@mui/material';
import { Add, Person } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import DataTable from '../components/common/DataTable';
import SearchBar from '../components/common/SearchBar';

import employeeService from '../services/employeeService';
import { formatDate } from '../utils/formatters';
import { DEPARTMENTS } from '../utils/constants';
import { useAuth } from '../hooks/useAuth.jsx';

const EmployeeList = () => {
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [departmentFilter, setDepartmentFilter] = useState('');

  const fetchEmployees = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      
      const params = {
        page,
        size: rowsPerPage,
        ...(searchTerm && { search: searchTerm }),
        ...(departmentFilter && { department: departmentFilter })
      };

      let response;
      if (searchTerm) {
        response = await employeeService.searchEmployees(searchTerm, params);
      } else if (departmentFilter) {
        response = await employeeService.getEmployeesByDepartment(departmentFilter, params);
      } else {
        response = await employeeService.getAllEmployees(params);
      }

      setEmployees(response.content || []);
      setTotalCount(response.totalElements || 0);
    } catch (error) {
      setError('Failed to load employees');
      console.error('Employee fetch error:', error);
    } finally {
      setLoading(false);
    }
  }, [page, rowsPerPage, searchTerm, departmentFilter]);

  useEffect(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  const handleSearch = (term) => {
    setSearchTerm(term);
    setPage(0); // Reset to first page on search
  };

  const handleDepartmentFilter = (department) => {
    setDepartmentFilter(department);
    setPage(0); // Reset to first page on filter
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const handleRowsPerPageChange = (newRowsPerPage) => {
    setRowsPerPage(newRowsPerPage);
    setPage(0);
  };

  const handleEdit = (employee) => {
    navigate(`/employees/edit/${employee.id}`);
  };

  const handleView = (employee) => {
    navigate(`/employees/${employee.id}`);
  };

  const handleDelete = (employee) => {
    setSelectedEmployee(employee);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    if (!selectedEmployee) return;

    try {
      await employeeService.deleteEmployee(selectedEmployee.id);
      setDeleteDialogOpen(false);
      setSelectedEmployee(null);
      fetchEmployees(); // Refresh the list
    } catch (error) {
      setError('Failed to delete employee');
      console.error('Delete error:', error);
    }
  };

  const columns = [
    {
      field: 'firstName',
      headerName: 'First Name',
      minWidth: 120,
    },
    {
      field: 'lastName',
      headerName: 'Last Name',
      minWidth: 120,
    },
    {
      field: 'email',
      headerName: 'Email',
      minWidth: 200,
    },
    {
      field: 'department',
      headerName: 'Department',
      minWidth: 150,
      render: (value) => (
        <Chip
          label={value?.replace(/_/g, ' ')}
          size="small"
          color="primary"
          variant="outlined"
        />
      ),
    },
    {
      field: 'userType',
      headerName: 'Role',
      minWidth: 100,
      render: (value, row) => (
        <Chip
          label={value === 'ADMIN' ? 'Admin' : value === 'MANAGER' ? 'Manager' : 'Employee'}
          size="small"
          color={value === 'ADMIN' ? 'error' : value === 'MANAGER' ? 'warning' : 'default'}
          variant={value === 'ADMIN' ? 'filled' : 'outlined'}
        />
      ),
    },
    {
      field: 'position',
      headerName: 'Position',
      minWidth: 150,
    },
    {
      field: 'hireDate',
      headerName: 'Hire Date',
      minWidth: 120,
      render: (value) => formatDate(value),
    },
    {
      field: 'salary',
      headerName: 'Salary',
      minWidth: 120,
      align: 'right',
      render: (value) => value ? `$${value.toLocaleString()}` : '-',
    },
  ];

  const canAddEmployee = hasRole('ADMIN') || hasRole('HR');
  const canEditEmployee = hasRole('ADMIN') || hasRole('HR');
  const canDeleteEmployee = hasRole('ADMIN');

  return (
    <Container maxWidth="xl">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Employees
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Manage employee information and records
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
              placeholder="Search employees..."
              onSearch={handleSearch}
              value={searchTerm}
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Department</InputLabel>
              <Select
                value={departmentFilter}
                onChange={(e) => handleDepartmentFilter(e.target.value)}
                label="Department"
              >
                <MenuItem value="">All Departments</MenuItem>
                {Object.values(DEPARTMENTS).map((dept) => (
                  <MenuItem key={dept} value={dept}>
                    {dept.replace(/_/g, ' ')}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={5} sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            {canAddEmployee && (
              <Button
                variant="contained"
                startIcon={<Add />}
                onClick={() => navigate('/employees/new')}
              >
                Add Employee
              </Button>
            )}
          </Grid>
        </Grid>
      </Paper>

      <DataTable
        columns={columns}
        data={employees}
        loading={loading}
        page={page}
        rowsPerPage={rowsPerPage}
        totalCount={totalCount}
        onPageChange={handlePageChange}
        onRowsPerPageChange={handleRowsPerPageChange}
        onEdit={canEditEmployee ? handleEdit : null}
        onView={handleView}
        onDelete={canDeleteEmployee ? handleDelete : null}
        emptyMessage="No employees found"
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
            Are you sure you want to delete{' '}
            <strong>
              {selectedEmployee?.firstName} {selectedEmployee?.lastName}
            </strong>
            ? This action cannot be undone.
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

export default EmployeeList;
