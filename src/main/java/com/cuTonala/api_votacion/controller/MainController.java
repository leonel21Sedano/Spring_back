package com.cuTonala.api_votacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/test-db")
    public String testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && connection.isValid(2)) {
                return "Connection to the database is successful!";
            } else {
                return "Failed to connect to the database.";
            }
        } catch (SQLException e) {
            return "An error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/api/public/test-auth")
    public Map<String, Object> testAuth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Auth system is running");
        response.put("timestamp", new Date());
        
        // Probar conexión a la DB
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && connection.isValid(2)) {
                response.put("database", "connected");
                
                // Verificar usuarios
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios")) {
                    if (rs.next()) {
                        response.put("users_count", rs.getInt(1));
                    }
                }
            } else {
                response.put("database", "disconnected");
            }
        } catch (SQLException e) {
            response.put("database_error", e.getMessage());
        }
        
        return response;
    }
}