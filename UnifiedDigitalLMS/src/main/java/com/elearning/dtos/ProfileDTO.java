/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: ProfileDTO
// RUBRIC FOCUS: CO3 (Encapsulation)
//
// Q&A DEFENSE:
// "Immutable DTO used to securely transport updated profile data from the UI 
// layer down to the UserController for database updates."
// ==============================================================================
package com.elearning.dtos;

public class ProfileDTO {

    private final String name;
    private final String email;
    private final String status;

    public ProfileDTO(String name, String email, String status) {
        this.name   = name;
        this.email  = email;
        this.status = status;
    }

    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public String getStatus() { return status; }
}