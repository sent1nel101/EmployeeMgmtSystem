import React from 'react';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Box,
  Toolbar
} from '@mui/material';
import {
  Dashboard,
  People,
  Work,
  Business,
  Assessment,
  Search,
  Settings,
  AccountBox
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth.jsx';

const drawerWidth = 240;

const Sidebar = ({ open, onClose }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { hasRole, hasPermission } = useAuth();

  const navigationItems = [
    {
      text: 'Dashboard',
      icon: <Dashboard />,
      path: '/dashboard',
      roles: null, // Available to all authenticated users
    },
    {
      text: 'Employees',
      icon: <People />,
      path: '/employees',
      roles: ['ADMIN', 'HR', 'MANAGER'],
    },
    {
      text: 'Projects',
      icon: <Work />,
      path: '/projects',
      roles: ['ADMIN', 'MANAGER', 'PROJECT_MANAGER'],
    },
    {
      text: 'Departments',
      icon: <Business />,
      path: '/departments',
      roles: ['ADMIN', 'HR'],
    },
    {
      text: 'Reports',
      icon: <Assessment />,
      path: '/reports',
      roles: ['ADMIN', 'MANAGER', 'HR'],
    },
    {
      text: 'Search',
      icon: <Search />,
      path: '/search',
      roles: null, // Available to all authenticated users
    },
  ];

  const settingsItems = [
    {
      text: 'Profile',
      icon: <AccountBox />,
      path: '/profile',
      roles: null,
    },
    {
      text: 'Settings',
      icon: <Settings />,
      path: '/settings',
      roles: ['ADMIN'],
    },
  ];

  const handleNavigation = (path) => {
    navigate(path);
    if (onClose) onClose();
  };

  const isItemAccessible = (item) => {
    if (!item.roles) return true; // Available to all
    return item.roles.some(role => hasRole(role));
  };

  const renderNavigationItem = (item) => {
    if (!isItemAccessible(item)) return null;

    const isSelected = location.pathname === item.path;

    return (
      <ListItem key={item.text} disablePadding>
        <ListItemButton
          selected={isSelected}
          onClick={() => handleNavigation(item.path)}
          sx={{
            '&.Mui-selected': {
              backgroundColor: 'primary.light',
              color: 'primary.contrastText',
              '&:hover': {
                backgroundColor: 'primary.main',
              },
            },
          }}
        >
          <ListItemIcon
            sx={{
              color: isSelected ? 'inherit' : 'text.primary',
            }}
          >
            {item.icon}
          </ListItemIcon>
          <ListItemText primary={item.text} />
        </ListItemButton>
      </ListItem>
    );
  };

  const drawerContent = (
    <Box>
      <Toolbar />
      <List>
        {navigationItems.map(renderNavigationItem)}
      </List>
      <Divider />
      <List>
        {settingsItems.map(renderNavigationItem)}
      </List>
    </Box>
  );

  return (
    <>
      {/* Mobile drawer */}
      <Drawer
        variant="temporary"
        open={open}
        onClose={onClose}
        ModalProps={{
          keepMounted: true, // Better open performance on mobile
        }}
        sx={{
          display: { xs: 'block', sm: 'none' },
          '& .MuiDrawer-paper': {
            boxSizing: 'border-box',
            width: drawerWidth,
          },
        }}
      >
        {drawerContent}
      </Drawer>
      
      {/* Desktop drawer */}
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', sm: 'block' },
          '& .MuiDrawer-paper': {
            boxSizing: 'border-box',
            width: drawerWidth,
          },
        }}
        open
      >
        {drawerContent}
      </Drawer>
    </>
  );
};

export default Sidebar;
