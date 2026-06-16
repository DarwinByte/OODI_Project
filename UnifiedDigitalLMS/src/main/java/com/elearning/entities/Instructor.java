/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: Instructor
// RUBRIC FOCUS: CO3 (Inheritance, Polymorphism / Overriding)
//
// VARIABLES (Private Encapsulation):
// - staffId (String)
// - department (String)
//
// Q&A DEFENSE:
// "Similar to Student, this extends User and Polymorphically overrides 
// getRoleDetails() to return Instructor-specific data like Department."
// ==============================================================================
package com.elearning.entities;

public class Instructor extends User {

    private String staffId;
    private String department;

    public Instructor(String userId, String name, String email, String passwordHash, String status, String staffId, String department) {
        super(userId, name, email, passwordHash, status);
        this.staffId    = staffId;
        this.department = department;
    }

    @Override
    public String getRoleDetails() {
        return "Role: Instructor | Dept: " + department + " | Staff ID: " + staffId;
    }

    public String getStaffId()    { return staffId; }
    public String getDepartment() { return department; }
    public void setStaffId(String staffId)       { this.staffId    = staffId; }
    public void setDepartment(String department) { this.department = department; }
}