import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Paper,
  Box,
  CircularProgress,
  Typography,
  IconButton,
  Tooltip,
  useMediaQuery,
  useTheme,
  Card,
  CardContent,
  Stack
} from '@mui/material';
import { Edit, Delete, Visibility } from '@mui/icons-material';

const DataTable = ({
  columns,
  data,
  loading = false,
  page = 0,
  rowsPerPage = 10,
  totalCount = 0,
  onPageChange,
  onRowsPerPageChange,
  onEdit,
  onDelete,
  onView,
  emptyMessage = 'No data available',
  actions = true
}) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const handlePageChange = (event, newPage) => {
    if (onPageChange) {
      onPageChange(newPage);
    }
  };

  const handleRowsPerPageChange = (event) => {
    const newRowsPerPage = parseInt(event.target.value, 10);
    if (onRowsPerPageChange) {
      onRowsPerPageChange(newRowsPerPage);
    }
  };

  const renderCellContent = (column, row) => {
    if (column.render) {
      return column.render(row[column.field], row);
    }
    return row[column.field] || '-';
  };

  const renderActions = (row) => {
    if (!actions) return null;

    return (
      <TableCell>
        <Box sx={{ display: 'flex', gap: 1 }}>
          {onView && (
            <Tooltip title="View">
              <IconButton
                size="small"
                onClick={() => onView(row)}
                color="primary"
              >
                <Visibility />
              </IconButton>
            </Tooltip>
          )}
          {onEdit && (
            <Tooltip title="Edit">
              <IconButton
                size="small"
                onClick={() => onEdit(row)}
                color="primary"
              >
                <Edit />
              </IconButton>
            </Tooltip>
          )}
          {onDelete && (
            <Tooltip title="Delete">
              <IconButton
                size="small"
                onClick={() => onDelete(row)}
                color="error"
              >
                <Delete />
              </IconButton>
            </Tooltip>
          )}
        </Box>
      </TableCell>
    );
  };

  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: 200,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  // Mobile Card View
  if (isMobile) {
    return (
      <Box>
        {data.length === 0 ? (
          <Paper sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              {emptyMessage}
            </Typography>
          </Paper>
        ) : (
          <Stack spacing={2}>
            {data.map((row, index) => (
              <Card key={row.id || index}>
                <CardContent>
                  {columns.slice(0, 3).map((column) => (
                    <Box key={column.field} sx={{ mb: 1 }}>
                      <Typography variant="caption" color="text.secondary">
                        {column.headerName}:
                      </Typography>
                      <Box sx={{ ml: 1 }}>
                        {renderCellContent(column, row)}
                      </Box>
                    </Box>
                  ))}
                  {actions && (
                    <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                      {onView && (
                        <IconButton size="small" onClick={() => onView(row)} color="primary">
                          <Visibility />
                        </IconButton>
                      )}
                      {onEdit && (
                        <IconButton size="small" onClick={() => onEdit(row)} color="primary">
                          <Edit />
                        </IconButton>
                      )}
                      {onDelete && (
                        <IconButton size="small" onClick={() => onDelete(row)} color="error">
                          <Delete />
                        </IconButton>
                      )}
                    </Box>
                  )}
                </CardContent>
              </Card>
            ))}
          </Stack>
        )}
        
        {totalCount > 0 && (
          <TablePagination
            rowsPerPageOptions={[5, 10, 25, 50]}
            component="div"
            count={totalCount}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={handlePageChange}
            onRowsPerPageChange={handleRowsPerPageChange}
          />
        )}
      </Box>
    );
  }

  // Desktop Table View
  return (
    <Paper sx={{ width: '100%', overflow: 'hidden' }}>
      <TableContainer>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell
                  key={column.field}
                  align={column.align || 'left'}
                  style={{ minWidth: column.minWidth }}
                  sx={{ fontWeight: 'bold' }}
                >
                  {column.headerName}
                </TableCell>
              ))}
              {actions && (
                <TableCell align="center" sx={{ fontWeight: 'bold' }}>
                  Actions
                </TableCell>
              )}
            </TableRow>
          </TableHead>
          <TableBody>
            {data.length === 0 ? (
              <TableRow>
                <TableCell
                  colSpan={columns.length + (actions ? 1 : 0)}
                  align="center"
                  sx={{ py: 8 }}
                >
                  <Typography variant="body2" color="text.secondary">
                    {emptyMessage}
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              data.map((row, index) => (
                <TableRow hover key={row.id || index} tabIndex={-1}>
                  {columns.map((column) => (
                    <TableCell
                      key={column.field}
                      align={column.align || 'left'}
                    >
                      {renderCellContent(column, row)}
                    </TableCell>
                  ))}
                  {renderActions(row)}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
      
      {totalCount > 0 && (
        <TablePagination
          rowsPerPageOptions={[5, 10, 25, 50]}
          component="div"
          count={totalCount}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handlePageChange}
          onRowsPerPageChange={handleRowsPerPageChange}
        />
      )}
    </Paper>
  );
};

export default DataTable;
