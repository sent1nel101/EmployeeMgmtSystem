import React from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  TimeScale,
} from 'chart.js';
import 'chartjs-adapter-date-fns';
import { Box, Typography, useTheme } from '@mui/material';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  TimeScale
);

const ProjectTimelineChart = ({ data, title = "Project Timeline Overview" }) => {
  const theme = useTheme();

  if (!data || data.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="text.secondary">No data available</Typography>
      </Box>
    );
  }

  // Transform data for timeline visualization
  const timelineData = data.map(project => ({
    x: new Date(project.startDate),
    y: project.id,
    project: project.name,
    status: project.status,
    endDate: new Date(project.endDate)
  }));

  const chartData = {
    datasets: [
      {
        label: 'Project Start Dates',
        data: timelineData,
        borderColor: theme.palette.primary.main,
        backgroundColor: theme.palette.primary.light,
        pointRadius: window.innerWidth < 600 ? 4 : window.innerWidth > 1536 ? 8 : 6,
        pointHoverRadius: window.innerWidth < 600 ? 6 : window.innerWidth > 1536 ? 10 : 8,
        showLine: false,
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
          title: (context) => {
            const point = context[0];
            return point.raw.project;
          },
          label: (context) => {
            const point = context.raw;
            const startDate = point.x.toLocaleDateString();
            const endDate = point.endDate.toLocaleDateString();
            return [
              `Status: ${point.status.replace(/_/g, ' ')}`,
              `Start: ${startDate}`,
              `End: ${endDate}`
            ];
          }
        }
      }
    },
    scales: {
      x: {
        type: 'time',
        time: {
          unit: window.innerWidth < 600 ? 'quarter' : 'month',
          displayFormats: {
            month: window.innerWidth < 600 ? 'MMM' : 'MMM yyyy',
            quarter: 'MMM yyyy'
          }
        },
        title: {
          display: true,
          text: 'Timeline',
          font: {
            size: window.innerWidth < 600 ? 10 : window.innerWidth > 1536 ? 14 : 12
          }
        },
        ticks: {
          font: {
            size: window.innerWidth < 600 ? 9 : window.innerWidth > 1536 ? 13 : 11
          },
          maxRotation: window.innerWidth < 600 ? 45 : 0
        },
        grid: {
          color: theme.palette.divider,
        }
      },
      y: {
        display: false,
        grid: {
          display: false
        }
      }
    },
    interaction: {
      intersect: false,
      mode: 'point'
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
        <Line data={chartData} options={options} />
      </Box>
    </Box>
  );
};

export default ProjectTimelineChart;
