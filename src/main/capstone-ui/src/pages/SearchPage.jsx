import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Tabs,
  Tab,
  Grid,
  Card,
  CardContent,
  Chip,
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  ListItemButton,
  Divider,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button
} from '@mui/material';
import {
  Search,
  Person,
  Work,
  Business,
  Clear
} from '@mui/icons-material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import SearchBar from '../components/common/SearchBar';
import searchService from '../services/searchService';
import { formatDate, formatCurrency } from '../utils/formatters';
import { DEPARTMENTS, PROJECT_STATUS } from '../utils/constants';

const SearchPage = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState(searchParams.get('q') || '');
  const [activeTab, setActiveTab] = useState(0);
  const [results, setResults] = useState({
    employees: [],
    projects: [],
    total: 0
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    department: '',
    projectStatus: '',
    dateRange: ''
  });

  useEffect(() => {
    const query = searchParams.get('q');
    if (query) {
      setSearchQuery(query);
      performSearch(query);
    }
  }, [searchParams]);

  const performSearch = async (query) => {
    if (!query.trim()) {
      setResults({ employees: [], projects: [], total: 0 });
      return;
    }

    try {
      setLoading(true);
      setError('');

      const [employeeResults, projectResults] = await Promise.all([
        searchService.searchEmployees(query, { size: 50 }),
        searchService.searchProjects(query, { size: 50 })
      ]);

      setResults({
        employees: employeeResults.content || [],
        projects: projectResults.content || [],
        total: (employeeResults.totalElements || 0) + (projectResults.totalElements || 0)
      });
    } catch (error) {
      setError('Search failed. Please try again.');
      console.error('Search error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (query) => {
    setSearchQuery(query);
    if (query.trim()) {
      setSearchParams({ q: query });
    } else {
      setSearchParams({});
    }
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const handleClearFilters = () => {
    setFilters({
      department: '',
      projectStatus: '',
      dateRange: ''
    });
  };

  const getInitials = (firstName, lastName) => {
    return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`.toUpperCase();
  };

  const renderEmployeeResult = (employee) => (
    <ListItem key={employee.id} disablePadding>
      <ListItemButton onClick={() => navigate(`/employees/${employee.id}`)}>
        <ListItemAvatar>
          <Avatar sx={{ bgcolor: 'primary.main' }}>
            {getInitials(employee.firstName, employee.lastName)}
          </Avatar>
        </ListItemAvatar>
        <ListItemText
          primary={`${employee.firstName} ${employee.lastName}`}
          secondary={
            <Box>
              <Typography variant="body2" color="text.secondary">
                {employee.position} • {employee.department?.replace(/_/g, ' ')}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {employee.email}
              </Typography>
            </Box>
          }
        />
        <Box sx={{ ml: 2 }}>
          <Chip
            label={employee.department?.replace(/_/g, ' ')}
            size="small"
            color="primary"
            variant="outlined"
          />
        </Box>
      </ListItemButton>
    </ListItem>
  );

  const renderProjectResult = (project) => (
    <ListItem key={project.id} disablePadding>
      <ListItemButton onClick={() => navigate(`/projects/${project.id}`)}>
        <ListItemAvatar>
          <Avatar sx={{ bgcolor: 'secondary.main' }}>
            <Work />
          </Avatar>
        </ListItemAvatar>
        <ListItemText
          primary={project.name}
          secondary={
            <Box>
              <Typography variant="body2" color="text.secondary">
                {project.description}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {formatDate(project.startDate)} - {formatDate(project.endDate)}
              </Typography>
              {project.budget && (
                <Typography variant="body2" color="text.secondary">
                  Budget: {formatCurrency(project.budget)}
                </Typography>
              )}
            </Box>
          }
        />
        <Box sx={{ ml: 2 }}>
          <Chip
            label={project.status?.replace(/_/g, ' ')}
            size="small"
            color={project.status === 'COMPLETED' ? 'success' : 'primary'}
            variant="outlined"
          />
        </Box>
      </ListItemButton>
    </ListItem>
  );

  const tabData = [
    {
      label: `All (${results.total})`,
      content: (
        <Box>
          {results.employees.length > 0 && (
            <Box sx={{ mb: 3 }}>
              <Typography variant="h6" gutterBottom>
                Employees ({results.employees.length})
              </Typography>
              <List>
                {results.employees.slice(0, 5).map(renderEmployeeResult)}
              </List>
              {results.employees.length > 5 && (
                <Button
                  variant="text"
                  onClick={() => setActiveTab(1)}
                  sx={{ mt: 1 }}
                >
                  View all {results.employees.length} employees
                </Button>
              )}
            </Box>
          )}
          
          {results.projects.length > 0 && (
            <Box>
              <Typography variant="h6" gutterBottom>
                Projects ({results.projects.length})
              </Typography>
              <List>
                {results.projects.slice(0, 5).map(renderProjectResult)}
              </List>
              {results.projects.length > 5 && (
                <Button
                  variant="text"
                  onClick={() => setActiveTab(2)}
                  sx={{ mt: 1 }}
                >
                  View all {results.projects.length} projects
                </Button>
              )}
            </Box>
          )}
        </Box>
      )
    },
    {
      label: `Employees (${results.employees.length})`,
      content: (
        <List>
          {results.employees.map(renderEmployeeResult)}
        </List>
      )
    },
    {
      label: `Projects (${results.projects.length})`,
      content: (
        <List>
          {results.projects.map(renderProjectResult)}
        </List>
      )
    }
  ];

  return (
    <Container sx={{ width: '70vw', maxWidth: 'none' }}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Search
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Search across employees, projects, and other data
        </Typography>
      </Box>

      {/* Search Bar */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6}>
            <SearchBar
              placeholder="Search employees, projects..."
              onSearch={handleSearch}
              value={searchQuery}
            />
          </Grid>
          
          {/* Filters */}
          <Grid item xs={12} md={2}>
            <FormControl fullWidth size="small">
              <InputLabel>Department</InputLabel>
              <Select
                value={filters.department}
                onChange={(e) => setFilters(prev => ({ ...prev, department: e.target.value }))}
                label="Department"
              >
                <MenuItem value="">All</MenuItem>
                {Object.values(DEPARTMENTS).map((dept) => (
                  <MenuItem key={dept} value={dept}>
                    {dept.replace(/_/g, ' ')}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={2}>
            <FormControl fullWidth size="small">
              <InputLabel>Project Status</InputLabel>
              <Select
                value={filters.projectStatus}
                onChange={(e) => setFilters(prev => ({ ...prev, projectStatus: e.target.value }))}
                label="Project Status"
              >
                <MenuItem value="">All</MenuItem>
                {Object.values(PROJECT_STATUS).map((status) => (
                  <MenuItem key={status} value={status}>
                    {status.replace(/_/g, ' ')}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={2}>
            <Button
              variant="outlined"
              startIcon={<Clear />}
              onClick={handleClearFilters}
              fullWidth
            >
              Clear Filters
            </Button>
          </Grid>
        </Grid>
      </Paper>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Loading State */}
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {/* Search Results */}
      {!loading && searchQuery && (
        <Paper sx={{ p: 0 }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={activeTab} onChange={handleTabChange}>
              {tabData.map((tab, index) => (
                <Tab key={index} label={tab.label} />
              ))}
            </Tabs>
          </Box>
          
          <Box sx={{ p: 3 }}>
            {results.total === 0 ? (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <Search sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No results found
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Try adjusting your search terms or filters
                </Typography>
              </Box>
            ) : (
              tabData[activeTab].content
            )}
          </Box>
        </Paper>
      )}

      {/* Empty State */}
      {!loading && !searchQuery && (
        <Paper sx={{ p: 8, textAlign: 'center' }}>
          <Search sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" color="text.secondary" gutterBottom>
            Start searching
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Enter a search term to find employees, projects, and more
          </Typography>
        </Paper>
      )}
    </Container>
  );
};

export default SearchPage;
