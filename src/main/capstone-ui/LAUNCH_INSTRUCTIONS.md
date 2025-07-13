# 🚀 Launch Instructions for React Frontend

## Prerequisites
- Node.js (v16 or higher)
- npm or yarn

## Step-by-Step Launch Process

### 1. Navigate to Frontend Directory
```bash
cd src/main/capstone-ui
```

### 2. Fix Dependencies (IMPORTANT)
**Option A: Run the fix script (Windows)**
```bash
fix-dependencies.bat
```

**Option B: Run the fix script (Mac/Linux)**
```bash
chmod +x fix-dependencies.sh
./fix-dependencies.sh
```

**Option C: Manual fix**
```bash
# Clean existing installation
rm -rf node_modules package-lock.json

# Install with legacy peer deps
npm install --legacy-peer-deps
```

### 3. Start the Development Server
```bash
npm start
```

### 4. Open Browser
Navigate to: `http://localhost:3000`

## ✅ What Should Work

### 🎯 **Working Features:**
- ✅ Responsive layout with sidebar navigation
- ✅ Dashboard with summary cards and charts
- ✅ Employee list and detail views
- ✅ Project list interface
- ✅ Department overview
- ✅ Search functionality (UI only)
- ✅ Report generator interface
- ✅ Performance review forms
- ✅ Mobile-responsive design
- ✅ Material-UI theming

### 📱 **Navigation:**
- Dashboard (`/dashboard`) - Main overview
- Employees (`/employees`) - Employee management
- Projects (`/projects`) - Project listings
- Departments (`/departments`) - Department stats
- Search (`/search`) - Global search interface
- Reports (`/reports`) - Report generator
- Performance (`/performance`) - Performance reviews

### 🎨 **UI Features:**
- Modern Material-UI design
- Dark/light theme support
- Responsive tables that become cards on mobile
- Interactive charts and visualizations
- Form validation and error handling

## ⚠️ **Known Limitations (Backend Required)**

### 🔌 **API Integration:**
- API calls will fail (backend not running)
- Authentication is mocked for demo
- Data is simulated for display purposes

### 🛠 **To Fix Later:**
1. Connect to Spring Boot backend
2. Implement real authentication
3. Add actual data persistence
4. Enable real CRUD operations

## 🔧 **Demo Mode Settings**

I've temporarily modified the authentication to allow demo access:
- `authService.js` - Returns demo user and bypasses auth
- `Sidebar.jsx` - Shows all menu items regardless of roles
- `App.jsx` - Removed role restrictions from routes

## 🎯 **Expected Behavior**

1. **Homepage loads** with a clean, professional interface
2. **Sidebar navigation** shows all available features
3. **Dashboard** displays summary cards and placeholder charts
4. **Employee pages** show table layouts and forms
5. **All pages** are responsive and mobile-friendly
6. **API calls will show errors** but UI remains functional

## 🚨 **Troubleshooting**

### Common Issues:

#### 1. **ERESOLVE Dependency Conflict Error**
```
npm error ERESOLVE unable to resolve dependency tree
```
**Solution:** Use the dependency fix scripts provided:
- Windows: `fix-dependencies.bat`
- Mac/Linux: `./fix-dependencies.sh`
- Manual: `npm install --legacy-peer-deps`

#### 2. **Module not found errors** 
→ Run `npm install --legacy-peer-deps` again

#### 3. **Port 3000 busy** 
→ Use different port: `npm start -- --port 3001`

#### 4. **Build errors** 
→ Check console for specific missing dependencies

#### 5. **React version conflicts**
→ The packages are now set to compatible versions (React 18, MUI v6)

### Success Indicators:
- ✅ No compilation errors in terminal
- ✅ Browser opens to clean interface
- ✅ Navigation works smoothly
- ✅ Pages load without crashes
- ✅ Responsive design adapts to window size

You should now be able to browse the complete UI and see all the features working!
