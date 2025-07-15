import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Typography,
  Box,
  Paper,
  Alert
} from '@mui/material';
import {
  People,
  Work,
  Business,
  TrendingUp,
  Assignment,
  Schedule
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import SummaryCard from '../components/dashboard/SummaryCard';
import DepartmentChart from '../components/charts/DepartmentChart';
import ProjectStatusChart from '../components/charts/ProjectStatusChart';
import ProjectTimelineChart from '../components/charts/ProjectTimelineChart';
import employeeService from '../services/employeeService';
import projectService from '../services/projectService';
import { useAuth } from '../hooks/useAuth.jsx';
import { DEPARTMENTS, PROJECT_STATUS } from '../utils/constants';

const Dashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [dashboardData, setDashboardData] = useState({
    totalEmployees: 0,
    totalProjects: 0,
    activeProjects: 0,
    completedProjects: 0,
    totalDepartments: 0,
    recentActivities: []
  });
  const [chartData, setChartData] = useState({
    departments: [],
    projectStatus: [],
    projectTimeline: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Fetch employees data
      const employeesResponse = await employeeService.getAllEmployees({ size: 1 });
      
      // Fetch projects data
      const projectsResponse = await projectService.getAllProjects({ size: 1 });
      const activeProjectsResponse = await projectService.getProjectsByStatus('ACTIVE', { size: 1 });
      const completedProjectsResponse = await projectService.getProjectsByStatus('COMPLETED', { size: 1 });
      
      // Get unique departments count
      const allEmployeesResponse = await employeeService.getAllEmployees({ size: 1000 });
      const departments = new Set(allEmployeesResponse.content?.map(emp => emp.department) || []);

      setDashboardData({
        totalEmployees: employeesResponse.totalElements || 0,
        totalProjects: projectsResponse.totalElements || 0,
        activeProjects: activeProjectsResponse.totalElements || 0,
        completedProjects: completedProjectsResponse.totalElements || 0,
        totalDepartments: departments.size,
        recentActivities: []
      });

      // Prepare chart data
      await fetchChartData(allEmployeesResponse.content, projectsResponse.content);
    } catch (error) {
      setError('Failed to load dashboard data');
      console.error('Dashboard error:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchChartData = async (employees = [], projects = []) => {
    try {
      // Department distribution
      const departmentCounts = {};
      Object.values(DEPARTMENTS).forEach(dept => {
        departmentCounts[dept] = 0;
      });
      
      employees.forEach(emp => {
        if (emp.department && departmentCounts.hasOwnProperty(emp.department)) {
          departmentCounts[emp.department]++;
        }
      });

      const departmentData = Object.entries(departmentCounts)
        .filter(([dept, count]) => count > 0)
        .map(([dept, count]) => ({
          department: dept,
          count
        }));

      // Project status distribution
      const statusCounts = {};
      Object.values(PROJECT_STATUS).forEach(status => {
        statusCounts[status] = 0;
      });

      projects.forEach(project => {
        if (project.status && statusCounts.hasOwnProperty(project.status)) {
          statusCounts[project.status]++;
        }
      });

      const projectStatusData = Object.entries(statusCounts)
        .filter(([status, count]) => count > 0)
        .map(([status, count]) => ({
          status,
          count
        }));

      // Project timeline data (sample for recent projects)
      const timelineData = projects
        .filter(p => p.startDate && p.endDate)
        .slice(0, 10) // Show recent 10 projects
        .map(project => ({
          id: project.id,
          name: project.name,
          startDate: project.startDate,
          endDate: project.endDate,
          status: project.status
        }));

      setChartData({
        departments: departmentData,
        projectStatus: projectStatusData,
        projectTimeline: timelineData
      });
    } catch (error) {
      console.error('Chart data error:', error);
    }
  };

  const calculateProjectProgress = () => {
    const total = dashboardData.totalProjects;
    if (total === 0) return 0;
    return Math.round((dashboardData.completedProjects / total) * 100);
  };

  return (
    <Container maxWidth="xl">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Welcome back, {user?.firstName}! Here's your system overview.
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={{ xs: 2, sm: 3 }}>
        {/* Summary Cards */}
        <Grid item xs={12} sm={6} md={4} lg={3} xl={2}>
          <SummaryCard
            title="Total Employees"
            value={dashboardData.totalEmployees}
            icon={<People />}
            color="primary"
            loading={loading}
            onClick={() => navigate('/employees')}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={3} xl={2}>
          <SummaryCard
            title="Total Projects"
            value={dashboardData.totalProjects}
            icon={<Work />}
            color="secondary"
            loading={loading}
            onClick={() => navigate('/projects')}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={3} xl={2}>
          <SummaryCard
            title="Active Projects"
            value={dashboardData.activeProjects}
            icon={<Assignment />}
            color="success"
            loading={loading}
            trend="up"
            trendValue="+12%"
            onClick={() => navigate('/projects?status=ACTIVE')}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={3} xl={2}>
          <SummaryCard
            title="Completed Projects"
            value={dashboardData.completedProjects}
            icon={<Schedule />}
            color="info"
            loading={loading}
            onClick={() => navigate('/projects?status=COMPLETED')}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={3} xl={2}>
          <SummaryCard
            title="Departments"
            value={dashboardData.totalDepartments}
            icon={<Business />}
            color="warning"
            loading={loading}
            onClick={() => navigate('/departments')}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={4} lg={3} xl={2}>
          <SummaryCard
            title="Project Progress"
            value={`${calculateProjectProgress()}%`}
            icon={<TrendingUp />}
            color="success"
            loading={loading}
          />
        </Grid>

        {/* Department Distribution Chart */}
        <Grid item xs={12} md={6} xl={4}>
          <Paper sx={{ 
            p: { xs: 2, sm: 3 }, 
            height: { xs: '350px', sm: '400px', xl: '450px' },
            minHeight: '300px'
          }}>
            <DepartmentChart data={chartData.departments} />
          </Paper>
        </Grid>

        {/* Project Status Chart */}
        <Grid item xs={12} md={6} xl={4}>
          <Paper sx={{ 
            p: { xs: 2, sm: 3 }, 
            height: { xs: '350px', sm: '400px', xl: '450px' },
            minHeight: '300px'
          }}>
            <ProjectStatusChart data={chartData.projectStatus} />
          </Paper>
        </Grid>

        {/* Project Timeline Chart */}
        <Grid item xs={12} xl={4}>
          <Paper sx={{ 
            p: { xs: 2, sm: 3 }, 
            height: { xs: '350px', sm: '400px', xl: '450px' },
            minHeight: '300px'
          }}>
            <ProjectTimelineChart data={chartData.projectTimeline} />
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Dashboard;
