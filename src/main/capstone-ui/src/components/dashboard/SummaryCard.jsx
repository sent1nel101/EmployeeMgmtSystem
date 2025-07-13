import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  CircularProgress,
  useTheme
} from '@mui/material';
import { TrendingUp, TrendingDown, TrendingFlat } from '@mui/icons-material';

const SummaryCard = ({
  title,
  value,
  icon,
  trend = null,
  trendValue = null,
  loading = false,
  color = 'primary',
  onClick = null
}) => {
  const theme = useTheme();

  const getTrendIcon = () => {
    if (!trend || !trendValue) return null;
    
    if (trend === 'up') {
      return <TrendingUp sx={{ color: theme.palette.success.main }} />;
    } else if (trend === 'down') {
      return <TrendingDown sx={{ color: theme.palette.error.main }} />;
    } else {
      return <TrendingFlat sx={{ color: theme.palette.grey[500] }} />;
    }
  };

  const getTrendColor = () => {
    if (trend === 'up') return theme.palette.success.main;
    if (trend === 'down') return theme.palette.error.main;
    return theme.palette.grey[500];
  };

  return (
    <Card
      sx={{
        cursor: onClick ? 'pointer' : 'default',
        transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
        '&:hover': onClick ? {
          transform: 'translateY(-2px)',
          boxShadow: theme.shadows[8],
        } : {},
      }}
      onClick={onClick}
    >
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              width: 48,
              height: 48,
              borderRadius: '50%',
              backgroundColor: `${color}.light`,
              color: `${color}.main`,
              mr: 2,
            }}
          >
            {icon}
          </Box>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h6" component="div" color="text.secondary">
              {title}
            </Typography>
          </Box>
        </Box>
        
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Typography variant="h4" component="div" fontWeight="bold">
            {loading ? <CircularProgress size={32} /> : value}
          </Typography>
          
          {trend && trendValue && (
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              {getTrendIcon()}
              <Typography
                variant="body2"
                sx={{ color: getTrendColor(), ml: 0.5, fontWeight: 'medium' }}
              >
                {trendValue}
              </Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default SummaryCard;
