import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Grid,
  Button,
  Chip,
  Alert,
  Card,
  CardContent,
  Divider,
  CircularProgress
} from '@mui/material';
import {
  Edit,
  ArrowBack,
  Work,
  CalendarToday,
  AttachMoney,
  People
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import projectService from '../services/projectService';
import { formatDate, formatCurrency } from '../utils/formatters';
import { useAuth } from '../hooks/useAuth.jsx';
import ProjectForm from '../components/project/ProjectForm';

const ProjectDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [updateLoading, setUpdateLoading] = useState(false);

  useEffect(() => {
    console.log('ProjectDetail useEffect - id:', id);
    if (id && id !== 'new') {
      fetchProjectData();
    } else {
      // For new project, set loading to false and show create form
      setLoading(false);
      setProject(null);
    }
  }, [id]);

  const fetchProjectData = async () => {
    console.log('fetchProjectData called with id:', id);
    if (id === 'new') {
      console.warn('fetchProjectData called with id=new, this should not happen');
      return;
    }
    
    try {
      setLoading(true);
      setError('');
      
      const projectResponse = await projectService.getProjectById(id);
      setProject(projectResponse);
    } catch (error) {
      setError('Failed to load project data');
      console.error('Project detail error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = () => {
    setEditDialogOpen(true);
  };

  const handleCreateProject = async (projectData) => {
    setUpdateLoading(true);
    try {
      const newProject = await projectService.createProject(projectData);
      // Redirect to the new project's detail page
      navigate(`/projects/${newProject.id}`);
    } catch (error) {
      throw error; // Let the form handle the error
    } finally {
      setUpdateLoading(false);
    }
  };

  const handleUpdateProject = async (updatedData) => {
    setUpdateLoading(true);
    try {
      const updatedProject = await projectService.updateProject(id, updatedData);
      setProject(updatedProject);
      setEditDialogOpen(false);
    } catch (error) {
      throw error; // Let the form handle the error
    } finally {
      setUpdateLoading(false);
    }
  };

  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: 400,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md">
        <Alert severity="error" sx={{ mt: 4 }}>
          {error}
        </Alert>
      </Container>
    );
  }

  // Handle new project creation
  if (id === 'new') {
    return (
      <Container maxWidth="md">
        <Box sx={{ mb: 4 }}>
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate('/projects')}
            sx={{ mb: 2 }}
          >
            Back to Projects
          </Button>
          
          <Typography variant="h4" component="h1" gutterBottom>
            Add New Project
          </Typography>
          <Typography variant="body1" color="text.secondary" gutterBottom>
            Fill in the details below to create a new project
          </Typography>
        </Box>
        
        <Paper sx={{ p: 3 }}>
          <ProjectForm
            open={true}
            onClose={() => navigate('/projects')}
            onSubmit={handleCreateProject}
            loading={updateLoading}
            title="Create Project"
          />
        </Paper>
      </Container>
    );
  }

  if (!project) {
    return (
      <Container maxWidth="md">
        <Alert severity="info" sx={{ mt: 4 }}>
          Project not found
        </Alert>
      </Container>
    );
  }

  const canEdit = hasRole('ADMIN') || hasRole('MANAGER');

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/projects')}
          sx={{ mb: 2 }}
        >
          Back to Projects
        </Button>
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h4" component="h1">
            {project.name}
          </Typography>
          {canEdit && (
            <Button
              variant="contained"
              startIcon={<Edit />}
              onClick={handleEdit}
            >
              Edit Project
            </Button>
          )}
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Project Information Card */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Project Information
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Work sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body1">
                      <strong>Description:</strong> {project.description || 'No description provided'}
                    </Typography>
                  </Box>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <CalendarToday sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2">
                      <strong>Start Date:</strong> {formatDate(project.startDate)}
                    </Typography>
                  </Box>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <CalendarToday sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2">
                      <strong>End Date:</strong> {formatDate(project.endDate)}
                    </Typography>
                  </Box>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <AttachMoney sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2">
                      <strong>Budget:</strong> {formatCurrency(project.budget)}
                    </Typography>
                  </Box>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <People sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2">
                      <strong>Department:</strong> {project.department}
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Project Status Card */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Project Status
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Status
                </Typography>
                <Chip
                  label={project.status}
                  color={
                    project.status === 'COMPLETED' ? 'success' :
                    project.status === 'ACTIVE' ? 'primary' :
                    project.status === 'ON_HOLD' ? 'warning' : 'default'
                  }
                  variant="outlined"
                />
              </Box>
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Priority
                </Typography>
                <Chip
                  label={project.priority}
                  color={
                    project.priority === 'CRITICAL' ? 'error' :
                    project.priority === 'HIGH' ? 'warning' :
                    project.priority === 'MEDIUM' ? 'info' : 'default'
                  }
                  variant="outlined"
                />
              </Box>
              
              <Box>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Progress
                </Typography>
                <Typography variant="h6">
                  {project.progressPercentage || 0}%
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Edit Project Dialog */}
      <ProjectForm
        open={editDialogOpen}
        onClose={() => setEditDialogOpen(false)}
        onSubmit={handleUpdateProject}
        project={project}
        loading={updateLoading}
        title="Edit Project"
      />
    </Container>
  );
};

export default ProjectDetail;
