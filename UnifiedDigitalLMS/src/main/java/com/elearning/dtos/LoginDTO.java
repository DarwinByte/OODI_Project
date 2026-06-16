/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: LoginDTO
// RUBRIC FOCUS: CO3 (Encapsulation)
//
// Q&A DEFENSE:
// "These are pure DTOs. I used strictly private final variables. Once the UI 
// creates this object, the data is immutable, preventing accidental tampering 
// as it travels to the Controller layer."
// ==============================================================================
package com.elearning.dtos;

public class LoginDTO {
    private final String email;
    private final String password;
    public LoginDTO(String email, String password) {
        this.email    = email;
        this.password = password;
    }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}