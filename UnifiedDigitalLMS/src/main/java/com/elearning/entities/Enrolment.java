/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.entities;

/**
 * Entity: Enrolment
 * Represents one student-course enrolment record.
 * Repackaged from com.mycompany.digitallearningsystems to com.elearning.entities.
 *
 * OOP PRINCIPLES APPLIED IN THIS CLASS:
 * ─────────────────────────────────────
 * 1. ENCAPSULATION    : All fields are private — they cannot be accessed or
 * modified directly from outside.
 * Access is only allowed
 * through public getters and the single setter (setStatus).
 * Only 'status' has a setter because it is the only field
 * that changes after creation (Active → Completed/Dropped).
 *
 * 2. ABSTRACTION      : getSummary() provides a simplified, human-readable
 * representation of the object without exposing how
 * the string is built internally.
 *
 * 3. OBJECT REPRESENTATION : This class is a plain data entity (POJO) —
 * it models a real-world enrolment record with
 * clearly defined attributes and behaviours.
 */
public class Enrolment {

// ENCAPSULATION: all fields are private — only accessible via getters/setter below
private String enrolID;
private String studentID;
private String studentName;
private String courseID;
private String courseName;
private String enrolDate;
private String status; // Active | Completed | Dropped

// ENCAPSULATION: constructor initialises all fields in a controlled way —
//                no field can be left uninitialised or set to an arbitrary value
public Enrolment(String enrolID, String studentID, String studentName,
String courseID, String courseName, String enrolDate, String status) {
this.enrolID     = enrolID;
this.studentID   = studentID;
this.studentName = studentName;
this.courseID    = courseID;
this.courseName  = courseName;
this.enrolDate   = enrolDate;
this.status      = status;
}

// ENCAPSULATION: public getters provide read-only access to private fields
public String getEnrolID()     { return enrolID; }
public String getStudentID()   { return studentID; }
public String getStudentName() { return studentName; }
public String getCourseID()    { return courseID; }
public String getCourseName()  { return courseName; }
public String getEnrolDate()   { return enrolDate; }
public String getStatus()      { return status; }

// ENCAPSULATION: only 'status' has a setter — it is the only mutable field
//                (enrolment status changes over time: Active → Completed/Dropped)
public void   setStatus(String s) { this.status = s; }

// ABSTRACTION: hides string-building detail — caller just asks for a summary
public String getSummary() {
return studentName + " | " + courseName + " | " + status;
}
}