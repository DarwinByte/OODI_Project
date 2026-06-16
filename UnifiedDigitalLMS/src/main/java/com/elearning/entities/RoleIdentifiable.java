/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: RoleIdentifiable (Interface)
// RUBRIC FOCUS: CO3 (Abstraction / Interfaces)
// 
// Q&A DEFENSE: 
// "I used an Interface to enforce a contract. Any class that implements this 
// MUST define how it outputs its role details, ensuring standard behavior."
// ==============================================================================
package com.elearning.entities;

public interface RoleIdentifiable {
    String getRoleDetails();
}