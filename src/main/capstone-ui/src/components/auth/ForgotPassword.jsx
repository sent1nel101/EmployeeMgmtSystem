import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
    Container,
    Paper,
    TextField,
    Button,
    Typography,
    Box,
    Alert,
    CircularProgress
} from '@mui/material';
import { ArrowBack } from '@mui/icons-material';
import authService from '../../services/authService';
import { validateEmail, validateRequired } from '../../utils/validators';

const ForgotPassword = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const [resetToken, setResetToken] = useState('');

    const validateForm = () => {
        if (!validateRequired(email)) {
            setError('Email is required');
            return false;
        } else if (!validateEmail(email)) {
            setError('Please enter a valid email address');
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!validateForm()) {
            return;
        }

        setLoading(true);

        try {
            const response = await authService.requestPasswordReset(email);
            setResetToken(response.resetToken);
            setSuccess(true);
        } catch (error) {
            setError(
                error.response?.data?.message ||
                'Failed to send password reset request. Please try again.'
            );
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <Container component="main" maxWidth="sm">
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
                        <Typography component="h1" variant="h4" align="center" gutterBottom>
                            Password Reset Request Sent
                        </Typography>

                        <Alert severity="success" sx={{ mb: 2 }}>
                            A password reset link has been generated for your account.
                        </Alert>

                        <Box sx={{ mb: 3 }}>
                            <Typography variant="body1" gutterBottom>
                                <strong>For demonstration purposes, here is your reset token:</strong>
                            </Typography>
                            <TextField
                                fullWidth
                                value={resetToken}
                                InputProps={{
                                    readOnly: true,
                                }}
                                sx={{
                                    backgroundColor: '#f5f5f5',
                                    '& .MuiInputBase-input': {
                                        fontFamily: 'monospace',
                                        fontSize: '0.9rem'
                                    }
                                }}
                            />
                            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                In a real application, this would be sent via email. Copy this token to reset your password.
                            </Typography>
                        </Box>

                        <Button
                            fullWidth
                            variant="contained"
                            onClick={() => navigate(`/reset-password?token=${resetToken}`)}
                            sx={{ mb: 2 }}
                        >
                            Reset Password Now
                        </Button>

                        <Box textAlign="center">
                            <Link to="/login" style={{ textDecoration: 'none' }}>
                                <Button startIcon={<ArrowBack />} color="primary">
                                    Back to Login
                                </Button>
                            </Link>
                        </Box>
                    </Paper>
                </Box>
            </Container>
        );
    }

    return (
        <Container component="main" maxWidth="sm">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
                    <Typography component="h1" variant="h4" align="center" gutterBottom>
                        Reset Password
                    </Typography>

                    <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 3 }}>
                        Enter your email address and we'll send you a link to reset your password.
                    </Typography>

                    {error && (
                        <Alert severity="error" sx={{ mb: 2 }}>
                            {error}
                        </Alert>
                    )}

                    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="email"
                            label="Email Address"
                            name="email"
                            autoComplete="email"
                            autoFocus
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            error={!!error && !loading}
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                            disabled={loading}
                        >
                            {loading ? <CircularProgress size={24} /> : 'Send Reset Link'}
                        </Button>
                        <Box textAlign="center">
                            <Link to="/login" style={{ textDecoration: 'none' }}>
                                <Button startIcon={<ArrowBack />} color="primary">
                                    Back to Login
                                </Button>
                            </Link>
                        </Box>
                    </Box>
                </Paper>
            </Box>
        </Container>
    );
};

export default ForgotPassword;
