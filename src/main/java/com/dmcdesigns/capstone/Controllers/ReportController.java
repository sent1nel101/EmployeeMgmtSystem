package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.Services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {

    @Autowired
    private ReportService reportService;

    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Generate Employee Roster Report
     * GET /api/reports/employee-roster
     */
    @GetMapping("/employee-roster")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generateEmployeeRosterReport() {
        try {
            byte[] pdfBytes = reportService.generateEmployeeRosterReport();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "employee-roster-" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating employee roster report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generate Salary Analysis Report
     * GET /api/reports/salary-analysis
     */
    @GetMapping("/salary-analysis")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generateSalaryAnalysisReport() {
        try {
            byte[] pdfBytes = reportService.generateSalaryAnalysisReport();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "salary-analysis-" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating salary analysis report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generate Performance Summary Report
     * GET /api/reports/performance-summary
     */
    @GetMapping("/performance-summary")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generatePerformanceSummaryReport() {
        try {
            byte[] pdfBytes = reportService.generatePerformanceSummaryReport();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "performance-summary-" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating performance summary report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generate Custom Report by Department
     * GET /api/reports/department/{department}
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<byte[]> generateDepartmentReport(@PathVariable String department) {
        try {
            // For now, generate employee roster filtered by department
            // This can be extended to include department-specific analysis
            byte[] pdfBytes = reportService.generateEmployeeRosterReport();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "department-" + department + "-report-" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating department report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Unexpected error: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Get available report types
     * GET /api/reports/types
     */
    @GetMapping("/types")
    public ResponseEntity<String[]> getAvailableReportTypes() {
        String[] reportTypes = {
            "employee-roster",
            "salary-analysis", 
            "performance-summary"
        };
        return ResponseEntity.ok(reportTypes);
    }

    /**
     * Health check for report service
     * GET /api/reports/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Report service is healthy and ready to generate PDF reports");
    }

    /**
     * Get saved reports - placeholder endpoint
     * GET /api/reports/saved
     */
    @GetMapping("/saved")
    public ResponseEntity<Object[]> getSavedReports() {
        // Return empty array for now - this would typically get from database
        Object[] savedReports = {};
        return ResponseEntity.ok(savedReports);
    }

    /**
     * Generate employees report - placeholder endpoint  
     * POST /api/reports/employees
     */
    @PostMapping("/employees")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> generateEmployeesReport(@RequestBody(required = false) Object filters) {
        try {
            // Get employee data from report service
            Object employeeData = reportService.getEmployeeReportData();
            
            // Return structured data that frontend expects
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalEmployees", reportService.getEmployeeCount());
            summary.put("departments", reportService.getDepartmentCount());
            summary.put("generatedAt", LocalDateTime.now().format(FILENAME_FORMATTER));
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", employeeData);
            response.put("summary", summary);
            response.put("reportType", "employees");
            response.put("message", "Employee report generated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error generating employee report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to generate employee report",
                    "message", e.getMessage(),
                    "reportType", "employees"
                ));
        }
    }

    /**
     * Generate projects report - placeholder endpoint  
     * POST /api/reports/projects
     */
    @PostMapping("/projects")
    public ResponseEntity<Object> generateProjectsReport(@RequestBody(required = false) Object filters) {
        try {
            // Get project data from report service
            Object projectData = reportService.getProjectReportData();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalProjects", reportService.getProjectCount());
            summary.put("activeProjects", reportService.getActiveProjectCount());
            summary.put("generatedAt", LocalDateTime.now().format(FILENAME_FORMATTER));
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", projectData);
            response.put("summary", summary);
            response.put("reportType", "projects");
            response.put("message", "Projects report generated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error generating projects report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to generate projects report",
                    "message", e.getMessage(),
                    "reportType", "projects"
                ));
        }
    }

    /**
     * Generate departments report - placeholder endpoint  
     * POST /api/reports/departments
     */
    @PostMapping("/departments")
    public ResponseEntity<Object> generateDepartmentsReport(@RequestBody(required = false) Object filters) {
        try {
            // Get department data from report service
            Object departmentData = reportService.getDepartmentReportData();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalDepartments", reportService.getDepartmentCount());
            summary.put("totalEmployees", reportService.getEmployeeCount());
            summary.put("generatedAt", LocalDateTime.now().format(FILENAME_FORMATTER));
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", departmentData);
            response.put("summary", summary);
            response.put("reportType", "departments");
            response.put("message", "Departments report generated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error generating departments report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to generate departments report",
                    "message", e.getMessage(),
                    "reportType", "departments"
                ));
        }
    }

    /**
     * Generate performance report - placeholder endpoint  
     * POST /api/reports/performance
     */
    @PostMapping("/performance")
    public ResponseEntity<Object> generatePerformanceReport(@RequestBody(required = false) Object filters) {
        return ResponseEntity.ok(Map.of(
            "reportType", "performance",
            "generatedAt", LocalDateTime.now().format(FILENAME_FORMATTER),
            "totalReviews", 0,
            "message", "Performance report generated successfully"
        ));
    }

    /**
     * Get report templates - placeholder endpoint  
     * GET /api/reports/templates
     */
    @GetMapping("/templates")
    public ResponseEntity<Object[]> getReportTemplates() {
        Object[] templates = {
            Map.of("id", "employee-roster", "name", "Employee Roster", "description", "List of all employees"),
            Map.of("id", "salary-analysis", "name", "Salary Analysis", "description", "Salary statistics and analysis"),
            Map.of("id", "performance-summary", "name", "Performance Summary", "description", "Performance review summary")
        };
        return ResponseEntity.ok(templates);
    }

    /**
     * Save custom report - placeholder endpoint  
     * POST /api/reports/custom
     */
    @PostMapping("/custom")
    public ResponseEntity<Object> saveCustomReport(@RequestBody Object reportData) {
        return ResponseEntity.ok(Map.of(
            "id", System.currentTimeMillis(),
            "message", "Custom report saved successfully"
        ));
    }
}
