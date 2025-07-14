import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import authService from '../../services/authService';

const ProtectedRoute = ({ children, requiredRole = null, requiredPermission = null }) => {
  const location = useLocation();
  const isAuthenticated = authService.isAuthenticated();
  
  console.log('ProtectedRoute - isAuthenticated:', isAuthenticated);
  console.log('ProtectedRoute - current path:', location.pathname);
  console.log('ProtectedRoute - token:', authService.getToken());

  // Check if user is authenticated
  if (!isAuthenticated) {
    // Redirect to login page with return url
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Check role-based access if required
  if (requiredRole && !authService.hasRole(requiredRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  // Check permission-based access if required
  if (requiredPermission && !authService.hasPermission(requiredPermission)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

export default ProtectedRoute;
