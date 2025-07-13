import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Button,
  Grid,
  Card,
  CardContent,
  FormControl,
  FormControlLabel,
  Radio,
  RadioGroup,
  Switch,
  Divider,
  Alert,
  Paper,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  ArrowBack,
  Settings as SettingsIcon,
  Palette,
  Brightness4,
  Brightness7,
  Save,
  Refresh
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { useThemeMode } from '../hooks/useTheme';

const Settings = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { mode, setThemeMode } = useThemeMode();
  const [saveSuccess, setSaveSuccess] = useState(false);

  // Local state for settings
  const [settings, setSettings] = useState({
    themeMode: mode,
    compactMode: localStorage.getItem('compactMode') === 'true',
    animationsEnabled: localStorage.getItem('animationsEnabled') !== 'false', // default true
    autoSave: localStorage.getItem('autoSave') !== 'false', // default true
  });

  const handleThemeChange = (event) => {
    const newMode = event.target.value;
    setSettings(prev => ({ ...prev, themeMode: newMode }));
  };

  const handleSwitchChange = (setting) => (event) => {
    setSettings(prev => ({
      ...prev,
      [setting]: event.target.checked
    }));
  };

  const handleSave = () => {
    // Apply theme change
    setThemeMode(settings.themeMode);
    
    // Save other settings to localStorage
    localStorage.setItem('compactMode', settings.compactMode);
    localStorage.setItem('animationsEnabled', settings.animationsEnabled);
    localStorage.setItem('autoSave', settings.autoSave);
    
    // Show success message
    setSaveSuccess(true);
    setTimeout(() => setSaveSuccess(false), 3000);
  };

  const handleReset = () => {
    const defaultSettings = {
      themeMode: 'dark',
      compactMode: false,
      animationsEnabled: true,
      autoSave: true,
    };
    setSettings(defaultSettings);
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ mb: 4 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/dashboard')}
          sx={{ mb: 2 }}
        >
          Back to Dashboard
        </Button>
        
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <SettingsIcon sx={{ mr: 1, fontSize: '2rem' }} />
          <Typography variant="h4" component="h1">
            Settings
          </Typography>
        </Box>
        
        <Typography variant="body1" color="text.secondary">
          Customize your experience with the Employee Management System
        </Typography>
      </Box>

      {saveSuccess && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Settings saved successfully!
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Theme Settings */}
        <Grid item xs={12}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Palette sx={{ mr: 1, color: 'primary.main' }} />
                <Typography variant="h6">
                  Appearance
                </Typography>
              </Box>

              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <FormControl component="fieldset">
                    <Typography variant="subtitle2" gutterBottom>
                      Theme Mode
                    </Typography>
                    <RadioGroup
                      value={settings.themeMode}
                      onChange={handleThemeChange}
                    >
                      <FormControlLabel
                        value="light"
                        control={<Radio />}
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <Brightness7 sx={{ mr: 1 }} />
                            Light Mode
                          </Box>
                        }
                      />
                      <FormControlLabel
                        value="dark"
                        control={<Radio />}
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <Brightness4 sx={{ mr: 1 }} />
                            Dark Mode
                          </Box>
                        }
                      />
                    </RadioGroup>
                  </FormControl>
                </Grid>

                <Grid item xs={12} md={6}>
                  <Box>
                    <Typography variant="subtitle2" gutterBottom>
                      Interface Options
                    </Typography>
                    
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.compactMode}
                          onChange={handleSwitchChange('compactMode')}
                        />
                      }
                      label="Compact Mode"
                      sx={{ display: 'block', mb: 1 }}
                    />
                    
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.animationsEnabled}
                          onChange={handleSwitchChange('animationsEnabled')}
                        />
                      }
                      label="Enable Animations"
                      sx={{ display: 'block' }}
                    />
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Application Settings */}
        <Grid item xs={12}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <SettingsIcon sx={{ mr: 1, color: 'primary.main' }} />
                <Typography variant="h6">
                  Application Preferences
                </Typography>
              </Box>

              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={settings.autoSave}
                        onChange={handleSwitchChange('autoSave')}
                      />
                    }
                    label="Auto-save Changes"
                  />
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                    Automatically save form changes as you type
                  </Typography>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* User Information */}
        <Grid item xs={12}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Account Information
              </Typography>
              
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Name
                  </Typography>
                  <Typography variant="body1">
                    {user ? `${user.firstName} ${user.lastName}` : 'Not available'}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Email
                  </Typography>
                  <Typography variant="body1">
                    {user?.email || 'Not available'}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    Role
                  </Typography>
                  <Typography variant="body1">
                    {user?.roles?.[0] || 'Employee'}
                  </Typography>
                </Grid>
              </Grid>

              <Divider sx={{ my: 3 }} />
              
              <Box sx={{ display: 'flex', gap: 2 }}>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/profile')}
                >
                  View Profile
                </Button>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/forgot-password')}
                >
                  Change Password
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Action Buttons */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', gap: 2, justifyContent: 'space-between', alignItems: 'center' }}>
              <Box sx={{ display: 'flex', gap: 2 }}>
                <Button
                  variant="contained"
                  startIcon={<Save />}
                  onClick={handleSave}
                >
                  Save Changes
                </Button>
                
                <Button
                  variant="outlined"
                  startIcon={<Refresh />}
                  onClick={handleReset}
                >
                  Reset to Defaults
                </Button>
              </Box>
              
              <Typography variant="body2" color="text.secondary">
                Settings are automatically saved to your browser
              </Typography>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Settings;
