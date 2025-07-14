# Frontend Setup Instructions

## Quick Start

1. **Install Dependencies** (run ONE of these commands):
   ```bash
   # Option 1: Use the batch file (Windows)
   install-deps.bat
   
   # Option 2: Install manually
   npm install @mui/icons-material@^6.2.0 @mui/x-date-pickers@^7.22.2 chartjs-adapter-date-fns@^3.0.0 date-fns@^4.1.0
   ```

2. **Start the Development Server**:
   ```bash
   npm start
   ```

3. **Open Browser**: Navigate to `http://localhost:3000`

## Available Routes

- `/login` - Login page
- `/register` - Registration page  
- `/dashboard` - Main dashboard (default)
- `/employees` - Employee management
- `/projects` - Project management
- `/departments` - Department overview
- `/search` - Global search

- `/performance` - Performance reviews

## Test Login

Since the backend authentication isn't fully connected yet, you can:

1. Go to `/dashboard` directly (authentication is bypassed for demo)
2. Or implement a mock login in the AuthService

## Known Issues

- Backend API endpoints need to be implemented
- Authentication is currently mocked for demo purposes
- Some advanced features require backend integration

## Build for Production

```bash
npm run build
```

The built files will be in the `dist/` directory.
