package com.cuTonala.api_votacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
}