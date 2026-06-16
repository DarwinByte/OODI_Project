/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: Student
// RUBRIC FOCUS: CO3 (Inheritance, Polymorphism / Overriding)
//
// VARIABLES (Private Encapsulation):
// - enrolledCoursesCount (int)
//
// Q&A DEFENSE:
// "This class Inherits from User using 'extends'. It demonstrates Dynamic 
// Polymorphism because it @Overrides the getRoleDetails() method to provide 
// a Student-specific output, differing from the Instructor class."
// ==============================================================================
package com.elearning.entities;

public class Student extends User {

    private int enrolledCoursesCount;

    public Student(String userId, String name, String email, String passwordHash, String status, int enrolledCoursesCount) {
        super(userId, name, email, passwordHash, status);
        this.enrolledCoursesCount = enrolledCoursesCount;
    }

    @Override
    public String getRoleDetails() {
        return "Role: Student | Enrolled Courses: " + enrolledCoursesCount;
    }

    public int getEnrolledCoursesCount() { return enrolledCoursesCount; }
    public void setEnrolledCoursesCount(int count) { this.enrolledCoursesCount = count; }
}