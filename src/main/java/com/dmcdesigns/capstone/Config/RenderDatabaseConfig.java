package com.dmcdesigns.capstone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("render")
public class RenderDatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        System.out.println("=== CONFIGURING DATABASE FOR RENDER.COM ===");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new RuntimeException("DATABASE_URL environment variable is not set!");
        }
        
        System.out.println("Raw DATABASE_URL: " + databaseUrl);
        
        try {
            URI dbUri = new URI(databaseUrl);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            int port = dbUri.getPort();
            String database = dbUri.getPath().substring(1); // Remove leading slash
            
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            
            System.out.println("Parsed JDBC URL: " + jdbcUrl);
            System.out.println("Database: " + database);
            System.out.println("Host: " + host);
            System.out.println("Port: " + port);
            System.out.println("Username: " + username);
            System.out.println("Password: " + (password != null ? "[HIDDEN]" : "NULL"));
            
            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
                    
        } catch (URISyntaxException e) {
            System.err.println("Failed to parse DATABASE_URL: " + e.getMessage());
            throw new RuntimeException("Invalid DATABASE_URL format", e);
        } catch (Exception e) {
            System.err.println("Failed to create DataSource: " + e.getMessage());
            throw new RuntimeException("Database configuration failed", e);
        }
    }
}
