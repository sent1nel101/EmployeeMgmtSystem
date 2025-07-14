import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Card,
  CardContent,
  CardActions,
  TextField,
  Divider,
  Alert,
  CircularProgress,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip
} from '@mui/material';
import {
  Assessment,
  Download,
  PictureAsPdf,
  TableChart,
  Description,
  Save,
  Schedule,
  People,
  Work,
  Business
} from '@mui/icons-material';

import reportService from '../services/reportService';
import { DEPARTMENTS, PROJECT_STATUS } from '../utils/constants';
import { useAuth } from '../hooks/useAuth.jsx';

const ReportGenerator = () => {
  const { hasRole } = useAuth();
  const [selectedReportType, setSelectedReportType] = useState('');
  const [filters, setFilters] = useState({
    startDate: null,
    endDate: null,
    department: '',
    status: '',
    employeeId: null
  });
  const [loading, setLoading] = useState(false);
  const [exportLoading, setExportLoading] = useState(false);
  const [error, setError] = useState('');
  const [reportData, setReportData] = useState(null);
  const [savedReports, setSavedReports] = useState([]);

  const reportTypes = [
    {
      id: 'employees',
      name: 'Employee Report',
      description: 'Comprehensive employee data and statistics',
      icon: <People />,
      color: 'primary'
    },
    {
      id: 'projects',
      name: 'Project Report',
      description: 'Project status, timelines, and resource allocation',
      icon: <Work />,
      color: 'secondary'
    },
    {
      id: 'departments',
      name: 'Department Report',
      description: 'Department performance and employee distribution',
      icon: <Business />,
      color: 'success'
    },
    {
      id: 'performance',
      name: 'Performance Report',
      description: 'Employee performance metrics and reviews',
      icon: <Assessment />,
      color: 'warning'
    }
  ];

  useEffect(() => {
    if (hasRole('ADMIN') || hasRole('MANAGER')) {
      fetchSavedReports();
    }
  }, [hasRole]);

  const fetchSavedReports = async () => {
    try {
      const reports = await reportService.getSavedReports();
      setSavedReports(reports);
    } catch (error) {
      console.error('Failed to fetch saved reports:', error);
    }
  };

  const handleFilterChange = (name, value) => {
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const generateReport = async () => {
    if (!selectedReportType) {
      setError('Please select a report type');
      return;
    }

    try {
      setLoading(true);
      setError('');

      let data;
      switch (selectedReportType) {
        case 'employees':
          data = await reportService.generateEmployeeReport(filters);
          break;
        case 'projects':
          data = await reportService.generateProjectReport(filters);
          break;
        case 'departments':
          data = await reportService.generateDepartmentReport(filters);
          break;
        case 'performance':
          data = await reportService.generatePerformanceReport(filters);
          break;
        default:
          throw new Error('Invalid report type');
      }

      setReportData(data);
    } catch (error) {
      setError('Failed to generate report. Please try again.');
      console.error('Report generation error:', error);
    } finally {
      setLoading(false);
    }
  };

  const exportReport = async (format) => {
    if (!selectedReportType) {
      setError('Please generate a report first');
      return;
    }

    try {
      setExportLoading(true);
      
      let blob;
      let filename;
      
      switch (format) {
        case 'pdf':
          blob = await reportService.exportToPDF(selectedReportType, filters);
          filename = `${selectedReportType}_report.pdf`;
          break;
        case 'excel':
          blob = await reportService.exportToExcel(selectedReportType, filters);
          filename = `${selectedReportType}_report.xlsx`;
          break;
        case 'csv':
          blob = await reportService.exportToCSV(selectedReportType, filters);
          filename = `${selectedReportType}_report.csv`;
          break;
        default:
          throw new Error('Invalid export format');
      }

      // Create download link
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      setError('Failed to export report. Please try again.');
      console.error('Report export error:', error);
    } finally {
      setExportLoading(false);
    }
  };

  const saveReport = async () => {
    if (!reportData || !selectedReportType) {
      setError('Please generate a report first');
      return;
    }

    try {
      const reportToSave = {
        type: selectedReportType,
        filters,
        data: reportData,
        createdAt: new Date().toISOString()
      };

      await reportService.saveCustomReport(reportToSave);
      fetchSavedReports();
      alert('Report saved successfully!');
    } catch (error) {
      setError('Failed to save report. Please try again.');
      console.error('Report save error:', error);
    }
  };

  const canGenerateReports = hasRole('ADMIN') || hasRole('MANAGER') || hasRole('HR');

  if (!canGenerateReports) {
    return (
      <Container maxWidth="md">
        <Alert severity="warning" sx={{ mt: 4 }}>
          You don't have permission to generate reports. Please contact your administrator.
        </Alert>
      </Container>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Container maxWidth="xl">
        <Box sx={{ mb: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            Report Generator
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Generate comprehensive reports for employees, projects, and departments
          </Typography>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <Grid container spacing={3}>
          {/* Report Type Selection */}
          <Grid item xs={12} md={8}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Select Report Type
              </Typography>
              <Grid container spacing={2}>
                {reportTypes.map((type) => (
                  <Grid item xs={12} sm={6} key={type.id}>
                    <Card
                      sx={{
                        cursor: 'pointer',
                        border: selectedReportType === type.id ? 2 : 1,
                        borderColor: selectedReportType === type.id ? `${type.color}.main` : 'divider',
                        '&:hover': {
                          boxShadow: 4,
                        },
                      }}
                      onClick={() => setSelectedReportType(type.id)}
                    >
                      <CardContent>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                          <Box sx={{ color: `${type.color}.main`, mr: 1 }}>
                            {type.icon}
                          </Box>
                          <Typography variant="h6" component="div">
                            {type.name}
                          </Typography>
                        </Box>
                        <Typography variant="body2" color="text.secondary">
                          {type.description}
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>

              <Divider sx={{ my: 3 }} />

              {/* Filters */}
              <Typography variant="h6" gutterBottom>
                Filters
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Start Date"
                    type="date"
                    value={filters.startDate || ''}
                    onChange={(e) => handleFilterChange('startDate', e.target.value)}
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="End Date"
                    type="date"
                    value={filters.endDate || ''}
                    onChange={(e) => handleFilterChange('endDate', e.target.value)}
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth>
                    <InputLabel>Department</InputLabel>
                    <Select
                      value={filters.department}
                      onChange={(e) => handleFilterChange('department', e.target.value)}
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
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth>
                    <InputLabel>Status</InputLabel>
                    <Select
                      value={filters.status}
                      onChange={(e) => handleFilterChange('status', e.target.value)}
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
              </Grid>

              <Box sx={{ mt: 3, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Button
                  variant="contained"
                  onClick={generateReport}
                  disabled={loading || !selectedReportType}
                  startIcon={loading ? <CircularProgress size={20} /> : <Assessment />}
                >
                  {loading ? 'Generating...' : 'Generate Report'}
                </Button>

                {reportData && (
                  <>
                    <Button
                      variant="outlined"
                      onClick={() => exportReport('pdf')}
                      disabled={exportLoading}
                      startIcon={<PictureAsPdf />}
                    >
                      Export PDF
                    </Button>
                    <Button
                      variant="outlined"
                      onClick={() => exportReport('excel')}
                      disabled={exportLoading}
                      startIcon={<TableChart />}
                    >
                      Export Excel
                    </Button>
                    <Button
                      variant="outlined"
                      onClick={() => exportReport('csv')}
                      disabled={exportLoading}
                      startIcon={<Description />}
                    >
                      Export CSV
                    </Button>
                    <Button
                      variant="outlined"
                      onClick={saveReport}
                      startIcon={<Save />}
                    >
                      Save Report
                    </Button>
                  </>
                )}
              </Box>
            </Paper>
          </Grid>

          {/* Saved Reports */}
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Saved Reports
              </Typography>
              {savedReports.length === 0 ? (
                <Typography variant="body2" color="text.secondary">
                  No saved reports yet
                </Typography>
              ) : (
                <List>
                  {savedReports.slice(0, 5).map((report, index) => (
                    <ListItem key={index} divider>
                      <ListItemIcon>
                        <Schedule />
                      </ListItemIcon>
                      <ListItemText
                        primary={report.type.replace(/^\w/, c => c.toUpperCase()) + ' Report'}
                        secondary={new Date(report.createdAt).toLocaleDateString()}
                      />
                    </ListItem>
                  ))}
                </List>
              )}
            </Paper>
          </Grid>

          {/* Report Preview */}
          {reportData && (
            <Grid item xs={12}>
              <Paper sx={{ p: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Report Preview
                </Typography>
                <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
                  <pre style={{ whiteSpace: 'pre-wrap', fontSize: '0.875rem' }}>
                    {JSON.stringify(reportData, null, 2)}
                  </pre>
                </Box>
              </Paper>
            </Grid>
          )}
        </Grid>
      </Container>
    </LocalizationProvider>
  );
};

export default ReportGenerator;
