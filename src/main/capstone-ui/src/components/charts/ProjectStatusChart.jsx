import React from 'react';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Box, Typography, useTheme } from '@mui/material';
import { statusColors } from '../../styles/theme';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const ProjectStatusChart = ({ data, title = "Projects by Status" }) => {
  const theme = useTheme();

  if (!data || data.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="text.secondary">No data available</Typography>
      </Box>
    );
  }

  const chartData = {
    labels: data.map(item => item.status.replace(/_/g, ' ')),
    datasets: [
      {
        label: 'Number of Projects',
        data: data.map(item => item.count),
        backgroundColor: data.map(item => statusColors[item.status] || theme.palette.primary.main),
        borderColor: data.map(item => statusColors[item.status] || theme.palette.primary.main),
        borderWidth: 1,
        borderRadius: 4,
        borderSkipped: false,
      }
    ]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
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
            const value = context.parsed.y || 0;
            return `${label}: ${value} project${value !== 1 ? 's' : ''}`;
          }
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1,
          font: {
            size: window.innerWidth < 600 ? 10 : window.innerWidth > 1536 ? 14 : 12
          }
        },
        grid: {
          color: theme.palette.divider,
        },
        title: {
          display: true,
          text: 'Number of Projects',
          font: {
            size: window.innerWidth < 600 ? 10 : window.innerWidth > 1536 ? 14 : 12
          }
        }
      },
      x: {
        ticks: {
          font: {
            size: window.innerWidth < 600 ? 9 : window.innerWidth > 1536 ? 13 : 11
          },
          maxRotation: window.innerWidth < 600 ? 45 : 0
        },
        grid: {
          display: false
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
        <Bar data={chartData} options={options} />
      </Box>
    </Box>
  );
};

export default ProjectStatusChart;
