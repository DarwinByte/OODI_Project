/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: RegisterDTO
// RUBRIC FOCUS: CO3 (Encapsulation)
//
// Q&A DEFENSE:
// "Immutable DTO ensuring that user registration inputs are strictly packaged 
// together before being handed off to the backend."
// ==============================================================================
package com.elearning.dtos;

public class RegisterDTO {

    private final String name;
    private final String email;
    private final String password;
    private final String role;

    public RegisterDTO(String name, String email, String password, String role) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }
}