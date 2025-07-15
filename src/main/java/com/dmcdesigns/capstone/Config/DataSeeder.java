package com.dmcdesigns.capstone.Config;

import com.dmcdesigns.capstone.Entities.*;
import com.dmcdesigns.capstone.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * DataSeeder class responsible for populating the database with initial test data.
 * Runs automatically on application startup if the database is empty.
 * 
 * @author DMC Designs
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 DataSeeder starting - checking database state...");
        long userCount = userRepository.count();
        System.out.println("📊 Current user count: " + userCount);
        
        // Only seed if database is empty to avoid duplicates
        if (userCount == 0) {
            System.out.println("🌱 Seeding database with test users and sample data...");
            try {
                seedUsers();
                seedEmployees(); // Seed sample employees after test users
                System.out.println("✅ Test users created successfully! Check TEST_CREDENTIALS.md for login details.");
                System.out.println("🔐 Admin login: admin@ourcompany.com / admin123");
            } catch (Exception e) {
                System.err.println("❌ ERROR during database seeding: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ℹ️ Database already contains " + userCount + " users - skipping user seeding");
        }
        
        if (projectRepository.count() == 0) {
            seedProjects();
            System.out.println("✅ Sample projects created successfully!");
        } else {
            System.out.println("ℹ️ Database already contains projects - skipping project seeding");
        }
        
        // Display summary
        long finalUserCount = userRepository.count();
        long employeeCount = employeeRepository.count();
        long projectCount = projectRepository.count();
        
        System.out.println("📊 Database Summary:");
        System.out.println("   - Users: " + finalUserCount);
        System.out.println("   - Employees: " + employeeCount);
        System.out.println("   - Projects: " + projectCount);
        System.out.println("🚀 Application ready for testing!");
    }

    /**
     * Seeds the database with basic user accounts for testing authentication
     * These accounts are created for testing and review purposes
     */
    private void seedUsers() {
        // Admin User - Full system access
        Admin admin = new Admin("System", "Administrator", "admin@ourcompany.com", "555-0001", 
                               "admin", passwordEncoder.encode("admin123"), "IT");
        userRepository.save(admin);

        // Manager User - Team management and project oversight
        Manager manager = new Manager("Project", "Manager", "manager@ourcompany.com", "555-0002", 
                                     "manager", passwordEncoder.encode("manager123"), "ENGINEERING");
        userRepository.save(manager);

        // HR User - Human resources management
        Admin hrUser = new Admin("HR", "Director", "hr@ourcompany.com", "555-0003", 
                                "hr.director", passwordEncoder.encode("hr123"), "HR");
        userRepository.save(hrUser);

        // Regular Employee User - Limited access
        Employee empUser = new Employee("Regular", "Employee", "employee@ourcompany.com", "555-0004", 
                                       "employee", passwordEncoder.encode("employee123"), "ENGINEERING");
        userRepository.save(empUser);

        // Demo users for different departments
        Manager salesManager = new Manager("Sales", "Manager", "s.manager@ourcompany.com", "555-0005",
                                          "s.manager", passwordEncoder.encode("sales123"), "SALES");
        userRepository.save(salesManager);

        Employee marketingEmp = new Employee("Marketing", "Specialist", "m.specialist@ourcompany.com", "555-0006",
                                           "m.specialist", passwordEncoder.encode("marketing123"), "MARKETING");
        userRepository.save(marketingEmp);
    }

    /**
     * Seeds the database with sample employees across different departments
     */
    private void seedEmployees() {
        Employee emp1 = new Employee("John", "Doe", "j.doe@ourcompany.com", "555-1001",
                                    "j.doe", passwordEncoder.encode("password123"), "ENGINEERING");
        emp1.setHireDate("2022-01-15");
        emp1.setSalary(new BigDecimal("75000"));
        emp1.setRole("Senior Developer");
        employeeRepository.save(emp1);

        Employee emp2 = new Employee("Jane", "Smith", "j.smith@ourcompany.com", "555-1002",
                                    "j.smith", passwordEncoder.encode("password123"), "MARKETING");
        emp2.setHireDate("2021-08-20");
        emp2.setSalary(new BigDecimal("65000"));
        emp2.setRole("Marketing Coordinator");
        employeeRepository.save(emp2);

        Employee emp3 = new Employee("Mike", "Johnson", "m.johnson@ourcompany.com", "555-1003",
                                    "m.johnson", passwordEncoder.encode("password123"), "SALES");
        emp3.setHireDate("2023-03-10");
        emp3.setSalary(new BigDecimal("60000"));
        emp3.setRole("Sales Representative");
        employeeRepository.save(emp3);

        Employee emp4 = new Employee("Sarah", "Wilson", "s.wilson@ourcompany.com", "555-1004",
                                    "s.wilson", passwordEncoder.encode("password123"), "HR");
        emp4.setHireDate("2020-11-05");
        emp4.setSalary(new BigDecimal("58000"));
        emp4.setRole("HR Generalist");
        employeeRepository.save(emp4);

        Employee emp5 = new Employee("David", "Brown", "d.brown@ourcompany.com", "555-1005",
                                    "d.brown", passwordEncoder.encode("password123"), "FINANCE");
        emp5.setHireDate("2022-06-12");
        emp5.setSalary(new BigDecimal("70000"));
        emp5.setRole("Financial Analyst");
        employeeRepository.save(emp5);

        // Additional sample employees for more comprehensive testing
        Manager engManager = new Manager("Tech", "Lead", "t.lead@ourcompany.com", "555-1006",
                                       "t.lead", passwordEncoder.encode("techmanager123"), "ENGINEERING");
        engManager.setHireDate("2019-05-15");
        engManager.setSalary(new BigDecimal("95000"));
        engManager.setRole("Engineering Manager");
        employeeRepository.save(engManager);

        Employee intern = new Employee("Alex", "Chen", "a.chen@ourcompany.com", "555-1007",
                                     "a.chen", passwordEncoder.encode("intern123"), "ENGINEERING");
        intern.setHireDate("2024-01-08");
        intern.setSalary(new BigDecimal("45000"));
        intern.setRole("Software Engineer Intern");
        employeeRepository.save(intern);
    }

    /**
     * Seeds the database with sample projects in different phases and departments
     */
    private void seedProjects() {
        Project project1 = new Project("Customer Portal Redesign", 
                                      "Complete redesign of the customer-facing web portal", 
                                      "2024-01-01", 
                                      "ENGINEERING");
        project1.setEndDate("2024-06-30");
        project1.setBudget(new BigDecimal("150000"));
        project1.setStatus("ACTIVE");
        project1.setPriority("HIGH");
        project1.setProgressPercentage(65);
        projectRepository.save(project1);

        Project project2 = new Project("Mobile App Development", 
                                      "Development of iOS and Android mobile applications", 
                                      "2024-02-15", 
                                      "ENGINEERING");
        project2.setEndDate("2024-08-15");
        project2.setBudget(new BigDecimal("200000"));
        project2.setStatus("PLANNING");
        project2.setPriority("MEDIUM");
        project2.setProgressPercentage(10);
        projectRepository.save(project2);

        Project project3 = new Project("Data Migration", 
                                      "Migration of legacy data to new cloud infrastructure", 
                                      "2023-09-01", 
                                      "IT");
        project3.setEndDate("2024-01-31");
        project3.setBudget(new BigDecimal("100000"));
        project3.setBudgetUsed(new BigDecimal("95000"));
        project3.setStatus("COMPLETED");
        project3.setPriority("CRITICAL");
        project3.setProgressPercentage(100);
        projectRepository.save(project3);

        Project project4 = new Project("Security Audit", 
                                      "Comprehensive security audit and vulnerability assessment", 
                                      "2024-03-01", 
                                      "IT");
        project4.setEndDate("2024-04-30");
        project4.setBudget(new BigDecimal("75000"));
        project4.setBudgetUsed(new BigDecimal("25000"));
        project4.setStatus("ON_HOLD");
        project4.setPriority("HIGH");
        project4.setProgressPercentage(30);
        projectRepository.save(project4);
        
        Project project5 = new Project("Marketing Campaign Platform", 
                                      "Development of automated marketing campaign management system", 
                                      "2024-01-15", 
                                      "MARKETING");
        project5.setEndDate("2024-05-30");
        project5.setBudget(new BigDecimal("120000"));
        project5.setBudgetUsed(new BigDecimal("80000"));
        project5.setStatus("ACTIVE");
        project5.setPriority("MEDIUM");
        project5.setProgressPercentage(75);
        projectRepository.save(project5);
    }
}
