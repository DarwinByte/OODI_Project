/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.controllers;

import com.elearning.entities.Course;
import com.elearning.entities.Enrolment;
import com.elearning.entities.Progress;
import java.util.*;

/**
 * EnrolmentController
 * Handles all enrolment and progress-tracking business logic.
 *
 * KEY INTEGRATION CHANGE vs original:
 * availableCourses() now reads LIVE data from CourseController.getDatabase()
 * so the student's enrolment form always shows what the instructor actually created.
 *
 * Database pattern: uses EnrolmentDatabase (static HashMaps) —
 * consistent with UserController and CourseController.
 *
 * OOP PRINCIPLES APPLIED IN THIS CLASS:
 * ─────────────────────────────────────
 * 1. ENCAPSULATION    : Fields 'db' and 'lastError' are private; accessed only
 * through public methods (getLastError(), getDb()).
 * Internal logic (seedData) is private — hidden from outside.
 *
 * 2. ABSTRACTION      : Exposes only meaningful operations to the outside
 * (enrolStudent, dropCourse, updateProgress, etc.).
 * Callers do not need to know how storage works internally.
 *
 * 3. COMPOSITION      : EnrolmentController HAS-A EnrolmentDatabase (db).
 * The controller owns and delegates storage tasks to it.
 *
 * 4. DEPENDENCY       : Depends on entity classes Enrolment, Progress, and Course
 * (imported from com.elearning.entities).
 * Also depends on CourseController (inter-module dependency)
 * to read live course data via CourseController.getDatabase().
 *
 * 5. SINGLE RESPONSIBILITY : This class handles only enrolment & progress logic.
 * Storage is delegated to EnrolmentDatabase.
 */
public class EnrolmentController {

// ENCAPSULATION: private field — external classes cannot access db directly
// COMPOSITION: EnrolmentController HAS-A EnrolmentDatabase
private final EnrolmentDatabase db = new EnrolmentDatabase();

// ENCAPSULATION: private field — only readable via getLastError()
private String lastError = "";

public EnrolmentController() {
if (db.isEmpty()) {
seedData();
}
}

// ── SEED ────────────────────────────────────────────────────
// ENCAPSULATION: private method — internal initialisation, hidden from outside
private void seedData() {
enrolStudent("S001", "Ahmad Zaki",  "C001", "Introduction to Java",  "2025-01-10");
enrolStudent("S001", "Ahmad Zaki",  "C002", "Web Development",        "2025-01-15");
enrolStudent("S002", "Siti Aisyah", "C001", "Introduction to Java",  "2025-02-01");
enrolStudent("S003", "Hafiz Razak", "C003", "Database Systems",       "2025-02-10");
enrolStudent("S002", "Siti Aisyah", "C003", "Database Systems",       "2025-03-01");

db.putProgress(db.progressKey("S001","C001"), new Progress("S001","C001",  9, 12));
db.putProgress(db.progressKey("S001","C002"), new Progress("S001","C002",  4, 10));
db.putProgress(db.progressKey("S002","C001"), new Progress("S002","C001", 12, 12));
db.putProgress(db.progressKey("S003","C003"), new Progress("S003","C003",  3,  8));
db.putProgress(db.progressKey("S002","C003"), new Progress("S002","C003",  0,  8));
System.out.println("== Enrolment sample data loaded ==");
}

// ── ENROL ────────────────────────────────────────────────────
// ABSTRACTION: hides duplicate-check and object creation details from the caller
// DEPENDENCY: creates Enrolment and Progress objects (depends on entity classes)
// DEPENDENCY: calls CourseController.getDatabase() — inter-module dependency
public boolean enrolStudent(String studentID, String studentName,
String courseID,  String courseName, String date) {
for (Enrolment e : db.values()) {
if (e.getStudentID().equals(studentID) && e.getCourseID().equals(courseID)) {
lastError = "Already enrolled in " + courseName + ".";
return false;
}
}
String id = db.generateID();
db.put(id, new Enrolment(id, studentID, studentName, courseID, courseName, date, "Active"));

String pk = db.progressKey(studentID, courseID);
if (db.getProgress(pk) == null) {
// totalTopics = number of topics in the live course, default 10 if not found
int total = 10;
Course live = CourseController.getDatabase().get(courseID);
if (live != null) total = Math.max(live.getTopicsList().size(), 1);
db.putProgress(pk, new Progress(studentID, courseID, 0, total));
}
return true;
}

// ── DROP ─────────────────────────────────────────────────────
// ABSTRACTION: business rule (cannot drop completed course) is hidden inside this method
// DEPENDENCY: uses Enrolment and Progress objects to enforce the rule
public boolean dropCourse(String enrolID) {
Enrolment e = db.get(enrolID);
if (e == null) { lastError = "Enrolment not found."; return false; }
Progress p = db.getProgress(db.progressKey(e.getStudentID(), e.getCourseID()));
if (p != null && p.isCompleted()) {
lastError = "Cannot drop a completed course."; return false;
}
db.remove(enrolID);
return true;
}

// ── VIEW STUDENT ENROLMENTS ───────────────────────────────────
// ABSTRACTION: caller simply asks for a student's enrolments — filter logic is hidden
// DEPENDENCY: returns List<Enrolment> — depends on Enrolment entity
public List<Enrolment> viewEnrolmentStatus(String studentID) {
List<Enrolment> list = new ArrayList<>();
for (Enrolment e : db.values())
if (e.getStudentID().equals(studentID)) list.add(e);
return list;
}

// ── GET PROGRESS ──────────────────────────────────────────────
// ABSTRACTION: delegates key-building and lookup to EnrolmentDatabase
// DEPENDENCY: returns a Progress object — depends on Progress entity
public Progress getProgress(String studentID, String courseID) {
return db.getProgress(db.progressKey(studentID, courseID));
}

// ── UPDATE PROGRESS (instructor) ──────────────────────────────
// ENCAPSULATION: status update logic is contained here, not scattered in the UI
// DEPENDENCY: directly calls setCompletedTopics() and isCompleted() on Progress object
//             calls setStatus() on Enrolment object — depends on both entity classes
public boolean updateProgress(String studentID, String courseID, int completedTopics) {
String pk = db.progressKey(studentID, courseID);
Progress p = db.getProgress(pk);
if (p == null) { lastError = "Progress record not found."; return false; }
p.setCompletedTopics(completedTopics);
if (p.isCompleted()) {
for (Enrolment e : db.values())
if (e.getStudentID().equals(studentID) && e.getCourseID().equals(courseID))
e.setStatus("Completed");
}
return true;
}

// ── MONITOR (instructor) ──────────────────────────────────────
// ABSTRACTION: assembles a progress report — UI does not need to know how it is built
// DEPENDENCY: reads from Enrolment and Progress objects via the database
public List<Object[]> monitorStudentProgress(String courseID) {
List<Object[]> report = new ArrayList<>();
for (Enrolment e : db.values()) {
if (e.getCourseID().equals(courseID)) {
Progress p = db.getProgress(db.progressKey(e.getStudentID(), courseID));
int pct   = (p != null) ? p.getCompletionPct()   : 0;
int done  = (p != null) ? p.getCompletedTopics() : 0;
int total = (p != null) ? p.getTotalTopics()      : 0;
report.add(new Object[]{
e.getEnrolID(), e.getStudentID(), e.getStudentName(),
e.getStatus(), done + "/" + total, pct + "%"
});
}
}
return report;
}

// ── REMOVE STUDENT FROM COURSE (instructor) ───────────────────
// ENCAPSULATION: removal validation is kept inside the controller, not in the UI
public boolean removeStudentFromCourse(String enrolID) {
if (db.get(enrolID) == null) { lastError = "Enrolment not found."; return false; }
db.remove(enrolID);
return true;
}

// ── AVAILABLE COURSES (from LIVE CourseController) ────────────
/**
* Returns [[courseID, courseName], ...] from the live CourseController HashMap.
* Falls back to hardcoded list if no courses exist yet.
*
* DEPENDENCY: calls CourseController.getDatabase() — inter-module dependency
* reads Course objects from the Course module (Module 2)
*/
public String[][] availableCourses() {
Map<String, Course> live = CourseController.getDatabase();
if (live == null || live.isEmpty()) {
return new String[][]{
{"C001","Introduction to Java"},{"C002","Web Development"},
{"C003","Database Systems"},    {"C004","Data Structures"},
{"C005","Software Engineering"}
};
}
List<String[]> list = new ArrayList<>();
for (Course c : live.values())
list.add(new String[]{c.getCourseID(), c.getCourseTitle()});
return list.toArray(new String[0][]);
}

// ENCAPSULATION: controlled read-only access to private fields via getters
public String getLastError() { return lastError; }
public EnrolmentDatabase getDb() { return db; }
}