# 🔧 Backend Setup and Manual Data Creation

## Backend Compilation Fixed ✅

I've temporarily disabled the DataSeeder component because it had entity structure mismatches. The backend will now compile successfully.

## 🚀 Start the Backend

```bash
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080` with H2 database.

## 📋 Manual Test Data Creation

Since the DataSeeder is disabled, you can create test data manually using the API endpoints or H2 console.

### Option 1: Using H2 Console

1. **Access H2 Console**: Go to `http://localhost:8080/h2-console`
2. **Connection Details**:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (leave empty)

3. **Create Admin User**:
```sql
INSERT INTO users (user_type, first_name, last_name, email, phone_number, username, password, department) 
VALUES ('ADMIN', 'Admin', 'User', 'admin@company.com', '555-0001', 'admin@company.com', '$2a$10$...', 'IT');
```

### Option 2: Using API Endpoints

1. **Register a user** via `POST /api/auth/register`:
```json
{
  "firstName": "Admin",
  "lastName": "User", 
  "email": "admin@company.com",
  "phoneNumber": "555-0001",
  "username": "admin@company.com",
  "password": "admin123",
  "department": "IT",
  "userType": "ADMIN"
}
```

2. **Login** via `POST /api/auth/login`:
```json
{
  "username": "admin@company.com",
  "password": "admin123"
}
```

3. **Create Employees** via `POST /api/employees`:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@company.com",
  "phoneNumber": "555-1001",
  "department": "ENGINEERING",
  "hireDate": "2022-01-15",
  "salary": 75000
}
```

4. **Create Projects** via `POST /api/projects`:
```json
{
  "name": "Customer Portal Redesign",
  "description": "Complete redesign of the customer-facing web portal",
  "startDate": "2024-01-01",
  "endDate": "2024-06-30",
  "budget": 150000,
  "status": "ACTIVE",
  "priority": "HIGH"
}
```

## 🔄 Enable DataSeeder Later

To re-enable automatic data seeding:

1. **Fix entity constructor calls** in `DataSeeder.java`
2. **Uncomment** the `@Component` annotation
3. **Restart** the application

```java
@Component  // Uncomment this line
public class DataSeeder implements CommandLineRunner {
```

## ✅ Test Connection Now

With the backend running:

1. **Start Backend**: `./mvnw spring-boot:run`
2. **Start Frontend**: `cd src/main/capstone-ui && npm start`  
3. **Test Login**: Use manually created credentials

## 🎯 Expected Behavior

- ✅ Backend compiles and starts successfully
- ✅ H2 database is accessible
- ✅ API endpoints are available
- ✅ Frontend can connect to backend
- ✅ Manual data creation works
- ⚠️ Auto data seeding disabled (can be re-enabled later)

The application should now run successfully for frontend-backend testing!
