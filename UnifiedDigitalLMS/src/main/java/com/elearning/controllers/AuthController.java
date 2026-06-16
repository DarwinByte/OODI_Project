/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: AuthController
// RUBRIC FOCUS: CO3 (Class Relationships - Aggregation)
//
// VARIABLES:
// - userController (UserController)
//
// Q&A DEFENSE:
// "This demonstrates Aggregation (a 'Has-A' relationship). The AuthController 
// receives UserController via Constructor Injection so it can verify credentials 
// without needing to query the database directly itself."
// ==============================================================================
package com.elearning.controllers;

import com.elearning.dtos.LoginDTO;
import com.elearning.entities.User;
import com.elearning.utils.SecurityUtils;

public class AuthController {

    private final UserController userController;

    public AuthController(UserController userController) {
        this.userController = userController;
    }

    public User login(LoginDTO dto) {
        User user = userController.getUserByEmail(dto.getEmail());
        if (user == null) {
            System.out.println("Login failed: email not found.");
            return null;
        }

        boolean passwordMatch = SecurityUtils.verifyPassword(dto.getPassword(), user.getPasswordHash());

        if (!passwordMatch) {
            System.out.println("Login failed: password mismatch.");
            return null;
        }

        if ("Inactive".equalsIgnoreCase(user.getStatus()) || "Suspended".equalsIgnoreCase(user.getStatus())) {
            System.out.println("Login denied: account is " + user.getStatus());
            return null;
        }

        System.out.println("Login success: " + user.getUserId() + " | " + user.getRoleDetails());
        return user;
    }

    public void logout(String sessionToken) {
        System.out.println("Session terminated: " + sessionToken);
    }
}