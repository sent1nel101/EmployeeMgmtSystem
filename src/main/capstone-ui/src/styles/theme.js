import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#4a90e2',
      light: '#6bb6ff',
      dark: '#0066cc',
    },
    secondary: {
      main: '#50e3c2',
      light: '#7fffd4',
      dark: '#26c6da',
    },
    success: {
      main: '#4caf50',
      light: '#81c784',
      dark: '#388e3c',
    },
    warning: {
      main: '#f5a623',
      light: '#ffb74d',
      dark: '#f57c00',
    },
    error: {
      main: '#f44336',
      light: '#ef5350',
      dark: '#d32f2f',
    },
    background: {
      default: '#1f1f1f',
      paper: '#2d2d2d',
    },
    text: {
      primary: '#c3c2c2',
      secondary: '#a0a0a0',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontSize: 'clamp(1.8rem, 4vw, 2.5rem)',
      fontWeight: 500,
    },
    h2: {
      fontSize: 'clamp(1.5rem, 3.5vw, 2rem)',
      fontWeight: 500,
    },
    h3: {
      fontSize: 'clamp(1.3rem, 3vw, 1.75rem)',
      fontWeight: 500,
    },
    h4: {
      fontSize: 'clamp(1.1rem, 2.5vw, 1.5rem)',
      fontWeight: 500,
    },
    h5: {
      fontSize: 'clamp(1rem, 2vw, 1.25rem)',
      fontWeight: 500,
    },
    h6: {
      fontSize: 'clamp(0.9rem, 1.5vw, 1rem)',
      fontWeight: 500,
    },
  },
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 900,
      lg: 1200,
      xl: 1536,
      // Custom breakpoints for TV screens
      xxl: 1920,
      tv: 2560,
    },
  },
  shape: {
    borderRadius: 8,
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: ({ theme }) => ({
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
          padding: theme.spacing(1),
          [theme.breakpoints.up('sm')]: {
            padding: theme.spacing(2),
          },
          [theme.breakpoints.up('md')]: {
            padding: theme.spacing(3),
          },
          '&:hover': {
            boxShadow: '0 4px 16px rgba(0,0,0,0.15)',
          },
        }),
      },
    },
    MuiButton: {
      styleOverrides: {
        root: ({ theme }) => ({
          textTransform: 'none',
          borderRadius: 8,
          minHeight: '44px', // Touch-friendly
          [theme.breakpoints.down('sm')]: {
            fontSize: '0.875rem',
            padding: '8px 16px',
          },
          [theme.breakpoints.up('xl')]: {
            fontSize: '1.1rem',
            padding: '12px 24px',
          },
        }),
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: ({ theme }) => ({
          '& .MuiInputBase-root': {
            minHeight: '44px', // Touch-friendly
            [theme.breakpoints.up('xl')]: {
              fontSize: '1.1rem',
            },
          },
        }),
      },
    },
    MuiChip: {
      styleOverrides: {
        root: ({ theme }) => ({
          borderRadius: 16,
          [theme.breakpoints.down('sm')]: {
            fontSize: '0.75rem',
            height: '28px',
          },
          [theme.breakpoints.up('xl')]: {
            fontSize: '0.95rem',
            height: '36px',
          },
        }),
      },
    },
    MuiContainer: {
      styleOverrides: {
        root: ({ theme }) => ({
          paddingLeft: theme.spacing(2),
          paddingRight: theme.spacing(2),
          [theme.breakpoints.up('sm')]: {
            paddingLeft: theme.spacing(3),
            paddingRight: theme.spacing(3),
          },
          [theme.breakpoints.up('lg')]: {
            paddingLeft: theme.spacing(4),
            paddingRight: theme.spacing(4),
          },
        }),
      },
    },
  },
});

// Status colors for projects
export const statusColors = {
  PLANNING: '#1976d2',
  IN_PROGRESS: '#2e7d32',
  ON_HOLD: '#ed6c02',
  COMPLETED: '#388e3c',
  CANCELLED: '#d32f2f',
};

// Priority colors
export const priorityColors = {
  LOW: '#4caf50',
  MEDIUM: '#ff9800',
  HIGH: '#f44336',
  CRITICAL: '#9c27b0',
};
