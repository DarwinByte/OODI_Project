/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // This points directly to the users database from your phpMyAdmin screenshot
    private static final String URL = "jdbc:mysql://localhost:3306/elearning_users";
    private static final String USER = "root"; // Default XAMPP user
    private static final String PASS = "";     // Default XAMPP password is empty

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }

    // Test method to verify the connection without running the whole app
    public static void main(String[] args) {
        System.out.println("Attempting to connect to XAMPP MySQL...");
        Connection conn = getConnection();
        
        if (conn != null) {
            System.out.println("SUCCESS: Connected to the elearning_users database!");
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection.");
            }
        } else {
            System.out.println("FAILURE: Could not connect. Ensure XAMPP MySQL module is running.");
        }
    }
}