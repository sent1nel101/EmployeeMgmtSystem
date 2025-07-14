package com.dmcdesigns.capstone.Config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    
    private final DataSource dataSource;
    
    public DatabaseConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void testDatabaseConnection() {
        System.out.println("=== H2 DATABASE CONNECTION TEST ===");
        System.out.println("Database URL: " + databaseUrl);
        
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("✅ H2 Database connection successful!");
            System.out.println("Connected to: " + connection.getMetaData().getURL());
            System.out.println("Database Product: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("Database Version: " + connection.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("❌ H2 Database connection failed:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== END H2 CONNECTION TEST ===");
    }
}
