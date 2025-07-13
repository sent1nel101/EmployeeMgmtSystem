import React from 'react';
import { Pie } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  Title
} from 'chart.js';
import { Box, Typography, useTheme } from '@mui/material';

ChartJS.register(ArcElement, Tooltip, Legend, Title);

const DepartmentChart = ({ data, title = "Employee Distribution by Department" }) => {
  const theme = useTheme();

  if (!data || data.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="text.secondary">No data available</Typography>
      </Box>
    );
  }

  const colors = [
    theme.palette.primary.main,
    theme.palette.secondary.main,
    theme.palette.success.main,
    theme.palette.warning.main,
    theme.palette.info.main,
    theme.palette.error.main,
  ];

  const chartData = {
    labels: data.map(item => item.department.replace(/_/g, ' ')),
    datasets: [
      {
        data: data.map(item => item.count),
        backgroundColor: colors.slice(0, data.length),
        borderColor: colors.slice(0, data.length),
        borderWidth: 2,
        hoverBorderWidth: 3,
      }
    ]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: window.innerWidth < 600 ? 'bottom' : 'right',
        labels: {
          padding: window.innerWidth < 600 ? 15 : 20,
          usePointStyle: true,
          font: {
            size: window.innerWidth < 600 ? 10 : window.innerWidth > 1536 ? 14 : 12
          },
          boxWidth: window.innerWidth < 600 ? 10 : 12
        }
      },
      tooltip: {
        bodyFont: {
          size: window.innerWidth < 600 ? 12 : window.innerWidth > 1536 ? 16 : 14
        },
        titleFont: {
          size: window.innerWidth < 600 ? 12 : window.innerWidth > 1536 ? 16 : 14
        },
        callbacks: {
          label: (context) => {
            const label = context.label || '';
            const value = context.parsed || 0;
            const total = context.dataset.data.reduce((a, b) => a + b, 0);
            const percentage = ((value / total) * 100).toFixed(1);
            return `${label}: ${value} (${percentage}%)`;
          }
        }
      }
    },
    layout: {
      padding: {
        top: window.innerWidth < 600 ? 10 : 20,
        bottom: window.innerWidth < 600 ? 10 : 20,
        left: window.innerWidth < 600 ? 10 : 20,
        right: window.innerWidth < 600 ? 10 : 20
      }
    }
  };

  return (
    <Box sx={{ 
      height: '100%', 
      display: 'flex', 
      flexDirection: 'column',
      minHeight: 250
    }}>
      <Typography 
        variant="h6" 
        gutterBottom 
        textAlign="center"
        sx={{ 
          fontSize: { xs: '1rem', sm: '1.1rem', xl: '1.3rem' },
          mb: { xs: 1, sm: 2 }
        }}
      >
        {title}
      </Typography>
      <Box sx={{ flexGrow: 1, position: 'relative' }}>
        <Pie data={chartData} options={options} />
      </Box>
    </Box>
  );
};

export default DepartmentChart;
