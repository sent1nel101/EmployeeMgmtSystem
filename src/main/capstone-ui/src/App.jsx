import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';


// Theme and providers
import { CustomThemeProvider, useThemeMode } from './hooks/useTheme.jsx';
import { AuthProvider } from './hooks/useAuth.jsx';

// Layout components
import Layout from './components/layout/Layout';
import ProtectedRoute from './components/auth/ProtectedRoute';

// Auth pages
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import ForgotPassword from './components/auth/ForgotPassword';
import ResetPassword from './components/auth/ResetPassword';
import Unauthorized from './components/auth/Unauthorized';

// Main pages
import Dashboard from './pages/Dashboard';
import EmployeeList from './pages/EmployeeList';
import EmployeeDetail from './pages/EmployeeDetail';
import DepartmentList from './pages/DepartmentList';
import SearchPage from './pages/SearchPage';
import ReportGenerator from './pages/ReportGenerator';
import PerformanceReviews from './pages/PerformanceReviews';
import ProjectList from './pages/ProjectList';
import ProjectDetail from './pages/ProjectDetail';
import Profile from './pages/Profile';
import Settings from './pages/Settings';

// Inner component that uses the theme
const AppContent = () => {
  const { theme } = useThemeMode();
  
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Routes>
            {/* Public routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />
            <Route path="/unauthorized" element={<Unauthorized />} />
            
            {/* Protected routes */}
            <Route path="/" element={
              <ProtectedRoute>
                <Layout>
                  <Dashboard />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <Layout>
                  <Dashboard />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/employees" element={
              <ProtectedRoute>
                <Layout>
                  <EmployeeList />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/employees/new" element={
              <ProtectedRoute>
                <Layout>
                  <EmployeeDetail />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/employees/edit/:id" element={
              <ProtectedRoute>
                <Layout>
                  <EmployeeDetail />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/employees/:id" element={
              <ProtectedRoute>
                <Layout>
                  <EmployeeDetail />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/projects" element={
              <ProtectedRoute>
                <Layout>
                  <ProjectList />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/projects/:id" element={
              <ProtectedRoute>
                <Layout>
                  <ProjectDetail />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/departments" element={
              <ProtectedRoute>
                <Layout>
                  <DepartmentList />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/search" element={
              <ProtectedRoute>
                <Layout>
                  <SearchPage />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/reports" element={
              <ProtectedRoute>
                <Layout>
                  <ReportGenerator />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/performance" element={
              <ProtectedRoute>
                <Layout>
                  <PerformanceReviews />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/profile" element={
              <ProtectedRoute>
                <Layout>
                  <Profile />
                </Layout>
              </ProtectedRoute>
            } />
            
            <Route path="/settings" element={
              <ProtectedRoute>
                <Layout>
                  <Settings />
                </Layout>
              </ProtectedRoute>
            } />
            
            {/* Catch all route */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
};

// Main App component with CustomThemeProvider
function App() {
  return (
    <CustomThemeProvider>
      <AppContent />
    </CustomThemeProvider>
  );
}

export default App;
