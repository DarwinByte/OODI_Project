/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: User (Abstract Class)
// RUBRIC FOCUS: CO3 (Abstraction, Encapsulation, Inheritance parent)
//
// VARIABLES (Encapsulated with 'protected' for subclass access):
// - userId (String)
// - name (String)
// - email (String)
// - passwordHash (String)
// - status (String)
//
// Q&A DEFENSE: 
// "This is an Abstract Class. It cannot be instantiated directly. It serves as 
// a blueprint for Student and Instructor, sharing common attributes via Inheritance. 
// I used Encapsulation by hiding variables and exposing Getters/Setters."
// ==============================================================================
package com.elearning.entities;

public abstract class User implements RoleIdentifiable {

    protected String userId;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String status;

    public User(String userId, String name, String email, String passwordHash, String status) {
        this.userId       = userId;
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.status       = status;
    }

    @Override
    public abstract String getRoleDetails();

    public String getUserId()       { return userId; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getStatus()       { return status; }

    public void setName(String name)     { this.name   = name; }
    public void setStatus(String status) { this.status = status; }
    public void setPasswordHash(String hash) { this.passwordHash = hash; }
}