package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.PerformanceReview;
import com.dmcdesigns.capstone.Repositories.EmployeeRepository;
import com.dmcdesigns.capstone.Repositories.PerformanceReviewRepository;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Generate Employee Roster Report
     */
    @Transactional(readOnly = true)
    public byte[] generateEmployeeRosterReport() throws IOException {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Add title and timestamp
            addReportHeader(contentStream, "Employee Roster Report");
            
            float yPosition = 720;
            
            // Table headers
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("ID");
            contentStream.newLineAtOffset(40, 0);
            contentStream.showText("Name");
            contentStream.newLineAtOffset(120, 0);
            contentStream.showText("Email");
            contentStream.newLineAtOffset(150, 0);
            contentStream.showText("Department");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Role");
            contentStream.newLineAtOffset(80, 0);
            contentStream.showText("Salary");
            contentStream.endText();
            
            yPosition -= 20;
            
            // Employee data
            List<Employee> employees = employeeRepository.findAllEmployees();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            
            for (Employee employee : employees) {
                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = 750;
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(String.valueOf(employee.getId()));
                contentStream.newLineAtOffset(40, 0);
                contentStream.showText(employee.getFirstName() + " " + employee.getLastName());
                contentStream.newLineAtOffset(120, 0);
                contentStream.showText(employee.getEmail());
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText(employee.getDepartment());
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(employee.getRole());
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("$" + (employee.getSalary() != null ? employee.getSalary().toString() : "0.00"));
                contentStream.endText();
                
                yPosition -= 15;
            }
            
            contentStream.close();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
            
        } finally {
            document.close();
        }
    }

    /**
     * Generate Salary Analysis Report with Charts
     */
    @Transactional(readOnly = true)
    public byte[] generateSalaryAnalysisReport() throws IOException {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Add title and timestamp
            addReportHeader(contentStream, "Salary Analysis Report");
            
            float yPosition = 680;
            
            // Salary statistics
            List<Employee> employees = employeeRepository.findAllEmployees();
            Map<String, List<Employee>> departmentGroups = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
            
            // Department salary summary
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Salary Summary by Department");
            contentStream.endText();
            yPosition -= 25;
            
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            for (Map.Entry<String, List<Employee>> entry : departmentGroups.entrySet()) {
                String department = entry.getKey();
                List<Employee> deptEmployees = entry.getValue();
                
                BigDecimal totalSalary = deptEmployees.stream()
                    .map(Employee::getSalary)
                    .filter(salary -> salary != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal avgSalary = deptEmployees.size() > 0 ? 
                    totalSalary.divide(new BigDecimal(deptEmployees.size()), 2, RoundingMode.HALF_UP) : 
                    BigDecimal.ZERO;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText(department + " - Employees: " + deptEmployees.size() + 
                    ", Total: $" + totalSalary + ", Average: $" + avgSalary);
                contentStream.endText();
                yPosition -= 15;
            }
            
            // Add salary distribution chart
            if (yPosition > 300) {
                yPosition -= 30;
                addSalaryDistributionChart(document, page, contentStream, yPosition, departmentGroups);
            }
            
            contentStream.close();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
            
        } finally {
            document.close();
        }
    }

    /**
     * Generate Performance Summary Report
     */
    @Transactional(readOnly = true)
    public byte[] generatePerformanceSummaryReport() throws IOException {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Add title and timestamp
            addReportHeader(contentStream, "Performance Summary Report");
            
            float yPosition = 680;
            
            // Performance statistics
            List<PerformanceReview> reviews = performanceReviewRepository.findAll();
            Map<String, List<PerformanceReview>> departmentReviews = reviews.stream()
                .collect(Collectors.groupingBy(PerformanceReview::getDepartment));
            
            // Department performance summary
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Performance Summary by Department");
            contentStream.endText();
            yPosition -= 25;
            
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            for (Map.Entry<String, List<PerformanceReview>> entry : departmentReviews.entrySet()) {
                String department = entry.getKey();
                List<PerformanceReview> deptReviews = entry.getValue();
                
                double avgRating = deptReviews.stream()
                    .mapToInt(PerformanceReview::getRating)
                    .average()
                    .orElse(0.0);
                
                long completedReviews = deptReviews.stream()
                    .filter(review -> "COMPLETED".equals(review.getStatus()))
                    .count();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText(String.format("%s - Reviews: %d, Completed: %d, Avg Rating: %.2f", 
                    department, deptReviews.size(), completedReviews, avgRating));
                contentStream.endText();
                yPosition -= 15;
            }
            
            // Status distribution
            yPosition -= 20;
            Map<String, Long> statusCounts = reviews.stream()
                .collect(Collectors.groupingBy(PerformanceReview::getStatus, Collectors.counting()));
            
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Review Status Distribution");
            contentStream.endText();
            yPosition -= 25;
            
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText(entry.getKey() + ": " + entry.getValue() + " reviews");
                contentStream.endText();
                yPosition -= 15;
            }
            
            // Add performance rating chart
            if (yPosition > 200) {
                yPosition -= 30;
                addPerformanceRatingChart(document, page, contentStream, yPosition, reviews);
            }
            
            contentStream.close();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
            
        } finally {
            document.close();
        }
    }

    /**
     * Add report header with title and timestamp
     */
    private void addReportHeader(PDPageContentStream contentStream, String title) throws IOException {
        // Title
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText(title);
        contentStream.endText();
        
        // Timestamp
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 730);
        contentStream.showText("Generated on: " + LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        contentStream.endText();
        
        // Line separator
        contentStream.moveTo(50, 720);
        contentStream.lineTo(550, 720);
        contentStream.stroke();
    }

    /**
     * Add salary distribution chart to PDF
     */
    private void addSalaryDistributionChart(PDDocument document, PDPage page, PDPageContentStream contentStream, 
                                          float yPosition, Map<String, List<Employee>> departmentGroups) throws IOException {
        try {
            // Create pie chart data
            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
            for (Map.Entry<String, List<Employee>> entry : departmentGroups.entrySet()) {
                BigDecimal totalSalary = entry.getValue().stream()
                    .map(Employee::getSalary)
                    .filter(salary -> salary != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                dataset.setValue(entry.getKey(), totalSalary.doubleValue());
            }
            
            // Create chart
            JFreeChart chart = ChartFactory.createPieChart(
                "Salary Distribution by Department",
                dataset,
                true, true, false
            );
            
            // Convert chart to image and add to PDF
            addChartToPDF(document, contentStream, chart, 50, yPosition - 200, 400, 200);
            
        } catch (Exception e) {
            // If chart creation fails, add error text
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition - 20);
            contentStream.showText("Chart could not be generated: " + e.getMessage());
            contentStream.endText();
        }
    }

    /**
     * Add performance rating chart to PDF
     */
    private void addPerformanceRatingChart(PDDocument document, PDPage page, PDPageContentStream contentStream, 
                                         float yPosition, List<PerformanceReview> reviews) throws IOException {
        try {
            // Create bar chart data
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            Map<Integer, Long> ratingCounts = reviews.stream()
                .collect(Collectors.groupingBy(PerformanceReview::getRating, Collectors.counting()));
            
            for (int i = 1; i <= 5; i++) {
                dataset.addValue(ratingCounts.getOrDefault(i, 0L), "Reviews", "Rating " + i);
            }
            
            // Create chart
            JFreeChart chart = ChartFactory.createBarChart(
                "Performance Rating Distribution",
                "Rating",
                "Number of Reviews",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
            );
            
            // Convert chart to image and add to PDF
            addChartToPDF(document, contentStream, chart, 50, yPosition - 150, 400, 150);
            
        } catch (Exception e) {
            // If chart creation fails, add error text
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition - 20);
            contentStream.showText("Chart could not be generated: " + e.getMessage());
            contentStream.endText();
        }
    }

    /**
     * Convert JFreeChart to image and add to PDF
     */
    private void addChartToPDF(PDDocument document, PDPageContentStream contentStream, JFreeChart chart, 
                              float x, float y, int width, int height) throws IOException {
        try {
            // Convert chart to BufferedImage
            BufferedImage chartImage = chart.createBufferedImage(width, height);
            
            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            // Create PDImageXObject from byte array
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                document, imageBytes, "chart.png");
            
            // Draw image on PDF
            contentStream.drawImage(pdImage, x, y, width, height);
            
        } catch (Exception e) {
            // If image creation fails, silently continue
            System.err.println("Failed to add chart to PDF: " + e.getMessage());
        }
    }

    /**
     * Get employee data for frontend reports
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEmployeeReportData() {
        List<Employee> employees = employeeRepository.findAllEmployees();
        return employees.stream()
            .map(emp -> {
                Map<String, Object> empMap = new HashMap<>();
                empMap.put("id", emp.getId());
                empMap.put("firstName", emp.getFirstName());
                empMap.put("lastName", emp.getLastName());
                empMap.put("email", emp.getEmail());
                empMap.put("department", emp.getDepartment());
                empMap.put("role", emp.getRole() != null ? emp.getRole() : "Employee");
                empMap.put("salary", emp.getSalary() != null ? emp.getSalary().toString() : "N/A");
                empMap.put("hireDate", emp.getHireDate() != null ? emp.getHireDate() : "N/A");
                return empMap;
            })
            .collect(Collectors.toList());
    }

    /**
     * Get project data for frontend reports
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjectReportData() {
        // This is a placeholder - you would implement project repository calls here
        List<Map<String, Object>> projects = new ArrayList<>();
        
        Map<String, Object> project1 = new HashMap<>();
        project1.put("id", 1);
        project1.put("name", "Sample Project");
        project1.put("status", "ACTIVE");
        project1.put("department", "ENGINEERING");
        projects.add(project1);
        
        Map<String, Object> project2 = new HashMap<>();
        project2.put("id", 2);
        project2.put("name", "Another Project");
        project2.put("status", "PLANNING");
        project2.put("department", "MARKETING");
        projects.add(project2);
        
        return projects;
    }

    /**
     * Get employee count
     */
    @Transactional(readOnly = true)
    public long getEmployeeCount() {
        return employeeRepository.count();
    }

    /**
     * Get department count
     */
    @Transactional(readOnly = true)
    public long getDepartmentCount() {
        return employeeRepository.findAllEmployees().stream()
            .map(Employee::getDepartment)
            .distinct()
            .count();
    }

    /**
     * Get project count
     */
    @Transactional(readOnly = true)
    public long getProjectCount() {
        // This is a placeholder - you would implement project repository calls here
        return 5;
    }

    /**
     * Get active project count
     */
    @Transactional(readOnly = true)
    public long getActiveProjectCount() {
        // This is a placeholder - you would implement project repository calls here
        return 3;
    }
}
