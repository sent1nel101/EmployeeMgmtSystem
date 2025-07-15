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
  TextField,
  Card,
  CardContent,
  Alert,
  CircularProgress,
  Divider,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from '@mui/material';
import {
  Assessment,
  Download,
  PictureAsPdf,
  TableChart,
  Description,
  Save,
  Clear
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth.jsx';
import reportService from '../services/reportService';
import { formatDate, formatCurrency } from '../utils/formatters';

const ReportGenerator = () => {
  const { hasRole } = useAuth();
  const [selectedReportType, setSelectedReportType] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [reportData, setReportData] = useState(null);
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    department: '',
    status: '',
    employee: ''
  });

  // Check if user can generate reports
  const canGenerateReports = hasRole('ADMIN') || hasRole('MANAGER');

  const reportTypes = [
    { value: 'employees', label: 'Employee Report' },
    { value: 'projects', label: 'Project Report' },
    { value: 'departments', label: 'Department Report' },
    { value: 'performance', label: 'Performance Report' }
  ];

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleGenerateReport = async () => {
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
      setError('Failed to generate report: ' + (error.message || 'Unknown error'));
      console.error('Report generation error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async (format) => {
    if (!reportData || !selectedReportType) return;

    try {
      setLoading(true);
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
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      setError('Failed to export report: ' + (error.message || 'Unknown error'));
      console.error('Export error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleClearFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      department: '',
      status: '',
      employee: ''
    });
    setReportData(null);
  };

  const renderReportTable = () => {
    if (!reportData || !reportData.data || reportData.data.length === 0) {
      return (
        <Box sx={{ textAlign: 'center', py: 4 }}>
          <Typography variant="h6" color="text.secondary">
            No data available for this report
          </Typography>
        </Box>
      );
    }

    const data = reportData.data;
    const headers = Object.keys(data[0]);

    return (
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              {headers.map((header) => (
                <TableCell key={header} sx={{ fontWeight: 'bold' }}>
                  {header.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase())}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {data.map((row, index) => (
              <TableRow key={index}>
                {headers.map((header) => (
                  <TableCell key={header}>
                    {typeof row[header] === 'object' && row[header] !== null
                      ? JSON.stringify(row[header])
                      : row[header]?.toString() || '-'}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  };

  if (!canGenerateReports) {
    return (
      <Container maxWidth="lg">
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Assessment sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h5" gutterBottom>
            Access Denied
          </Typography>
          <Typography variant="body1" color="text.secondary">
            You don't have permission to generate reports. Please contact your administrator.
          </Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="xl">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Report Generator
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Generate and export various reports for analysis
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Report Configuration */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Report Configuration
        </Typography>
        
        <Grid container spacing={3}>
          <Grid item xs={12} md={4}>
            <FormControl fullWidth>
              <InputLabel>Report Type</InputLabel>
              <Select
                value={selectedReportType}
                onChange={(e) => setSelectedReportType(e.target.value)}
                label="Report Type"
              >
                {reportTypes.map((type) => (
                  <MenuItem key={type.value} value={type.value}>
                    {type.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Start Date"
              type="date"
              value={filters.startDate}
              onChange={(e) => handleFilterChange('startDate', e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="End Date"
              type="date"
              value={filters.endDate}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Department"
              value={filters.department}
              onChange={(e) => handleFilterChange('department', e.target.value)}
              placeholder="Filter by department..."
            />
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Status"
              value={filters.status}
              onChange={(e) => handleFilterChange('status', e.target.value)}
              placeholder="Filter by status..."
            />
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Employee"
              value={filters.employee}
              onChange={(e) => handleFilterChange('employee', e.target.value)}
              placeholder="Filter by employee..."
            />
          </Grid>
        </Grid>

        <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            onClick={handleGenerateReport}
            disabled={loading}
            startIcon={loading ? <CircularProgress size={20} /> : <Assessment />}
          >
            Generate Report
          </Button>
          <Button
            variant="outlined"
            onClick={handleClearFilters}
            startIcon={<Clear />}
          >
            Clear Filters
          </Button>
        </Box>
      </Paper>

      {/* Report Results */}
      {reportData && (
        <Paper sx={{ p: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Typography variant="h6">
              {reportTypes.find(t => t.value === selectedReportType)?.label || 'Report'} Results
            </Typography>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button
                variant="outlined"
                size="small"
                onClick={() => handleExport('pdf')}
                startIcon={<PictureAsPdf />}
                disabled={loading}
              >
                PDF
              </Button>
              <Button
                variant="outlined"
                size="small"
                onClick={() => handleExport('excel')}
                startIcon={<TableChart />}
                disabled={loading}
              >
                Excel
              </Button>
              <Button
                variant="outlined"
                size="small"
                onClick={() => handleExport('csv')}
                startIcon={<Description />}
                disabled={loading}
              >
                CSV
              </Button>
            </Box>
          </Box>

          <Divider sx={{ mb: 3 }} />

          {/* Report Summary */}
          {reportData.summary && (
            <Box sx={{ mb: 3 }}>
              <Typography variant="subtitle1" gutterBottom>
                Report Summary
              </Typography>
              <Grid container spacing={2}>
                {Object.entries(reportData.summary).map(([key, value]) => (
                  <Grid item xs={12} sm={6} md={3} key={key}>
                    <Card variant="outlined">
                      <CardContent sx={{ textAlign: 'center' }}>
                        <Typography variant="h6" color="primary">
                          {value}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase())}
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {/* Report Table */}
          {renderReportTable()}
        </Paper>
      )}
    </Container>
  );
};

export default ReportGenerator;
