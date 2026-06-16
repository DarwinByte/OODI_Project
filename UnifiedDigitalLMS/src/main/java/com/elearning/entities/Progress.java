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
 * Entity: Progress
 * Tracks topics completed vs total for one student in one course.
 * Repackaged from com.mycompany.digitallearningsystems to com.elearning.entities.
 *
 * OOP PRINCIPLES APPLIED IN THIS CLASS:
 * ─────────────────────────────────────
 * 1. ENCAPSULATION    : All fields are private — external classes cannot read or
 * modify them directly.
 * Access is controlled through public
 * getters and setters only.
 * setCompletedTopics() enforces a valid range (0 to totalTopics)
 * — protecting data integrity from invalid input.
 *
 * 2. ABSTRACTION      : getCompletionPct() and isCompleted() hide their calculation
 * logic from the caller.
 * The caller simply asks "what % is done?"
 * or "is it completed?"
 * without knowing how it is computed.
 *
 * 3. OBJECT REPRESENTATION : This class is a plain data entity (POJO) —
 * it models a real-world progress record for one
 * student in one course.
 */
public class Progress {

// ENCAPSULATION: all fields are private — only accessible via getters/setters below
private String studentID;
private String courseID;
private int    completedTopics;
private int    totalTopics;

// ENCAPSULATION: constructor ensures all fields are initialised in a controlled way
public Progress(String studentID, String courseID, int completedTopics, int totalTopics) {
this.studentID       = studentID;
this.courseID        = courseID;
this.completedTopics = completedTopics;
this.totalTopics     = totalTopics;
}

// ENCAPSULATION: public getters provide read-only access to private fields
public String getStudentID()       { return studentID; }
public String getCourseID()        { return courseID; }
public int    getCompletedTopics() { return completedTopics; }
public int    getTotalTopics()     { return totalTopics; }

// ABSTRACTION: hides the percentage calculation — caller just calls getCompletionPct()
//              also guards against division-by-zero internally
public int    getCompletionPct() {
return totalTopics == 0 ? 0 : (completedTopics * 100) / totalTopics;
}

// ABSTRACTION: hides the completion check — caller just calls isCompleted()
public boolean isCompleted() { return completedTopics >= totalTopics; }

// ENCAPSULATION: setter enforces valid range (0 to totalTopics) —
//                prevents completedTopics from going negative or exceeding total
public void setCompletedTopics(int n) {
this.completedTopics = Math.min(Math.max(n, 0), totalTopics);
}

// ENCAPSULATION: controlled write access to totalTopics via setter
public void setTotalTopics(int n) { this.totalTopics = n; }
}