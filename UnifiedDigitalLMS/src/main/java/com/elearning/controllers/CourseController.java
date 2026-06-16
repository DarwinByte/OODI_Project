/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.controllers;

import com.elearning.dtos.CourseDTO;
import com.elearning.dtos.MaterialDTO;
import com.elearning.entities.Course;
import com.elearning.entities.Material;
import com.elearning.entities.Topic;
import com.elearning.entities.User;
import com.elearning.entities.Instructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
* Controller: CourseController
* Purpose: Handles all course/content CRUD operations.
* Uses a static HashMap<String, Course> — mirrors the pattern in UserController
* so that both modules share the same in-memory database approach.
*
* Session-awareness: every write operation accepts the acting User so that
* role-based guards can be applied (Instructors may create/edit/delete;
* Students are read-only).
*
* Static database ensures all controller instances share the SAME map.
*/
public class CourseController {

// ── Central runtime database ──────────────────────────────────────────
// Static: survives across frames, shared by every CourseController instance.
private static final HashMap<String, Course> database = new HashMap<>();
// ↑ ENCAPSULATION (private field — hidden from all other classes)
// ↑ ASSOCIATION (CourseController "has-a" relationship with Course, via the database)
// ↑ AGGREGATION (database holds Course objects but doesn't control their full lifecycle —
//   a Course could exist independently of being stored here)
// ↑ static keyword = shared state across ALL CourseController instances (class-level field,
//   not object-level — a class design decision that allows the User Management module
//   and this module to share one database)

// Validation constants
private static final int    MAX_TOPICS          = 20;
private static final long   MAX_FILE_BYTES       = 50L * 1024 * 1024;
private static final String[] ALLOWED_EXTENSIONS = {"pdf", "pptx", "mp4", "docx"};
// ↑ ENCAPSULATION (private static final — hidden, fixed configuration values)

// Last error code — read by UI after a false-returning method
private String lastError = "";
// ↑ ENCAPSULATION (private field, only exposed via getLastError() below)

// ── Constructor: seed sample data once ───────────────────────────────
public CourseController() {
if (database.isEmpty()) {
createCourse(new CourseDTO("BCN1043",
"Introduction to Programming",
"Fundamentals of programming using Java."), null);
// ↑ DEPENDENCY (constructor creates a temporary CourseDTO object,
//   passes it to createCourse(), does not store it permanently)
createCourse(new CourseDTO("BCN2053",
"Data Structures",
"Arrays, linked lists, trees, and graphs."), null);
createCourse(new CourseDTO("BCN3063",
"Software Engineering",
"SDLC, UML modelling, and agile practices."), null);
addTopic("BCN1043", "Week 1: Introduction to Java", null);
addTopic("BCN1043", "Week 2: Control Structures",   null);
uploadMaterial("BCN1043",
new MaterialDTO("/sample/slides_week1.pdf", "Week 1 Slides"), 0, null);
System.out.println("== Course sample data loaded ==");
}
}

// ── CREATE COURSE ─────────────────────────────────────────────────────

/**
* Creates a new course.
* @param dto    course data from the UI
* @param actor  the logged-in user (null = bypass guard, e.g. seed)
* @return true on success
*/
public boolean createCourse(CourseDTO dto, User actor) {
// ↑ DEPENDENCY (method receives a CourseDTO temporarily — does not retain a reference)
// ↑ ASSOCIATION (method receives a User reference — this is the cross-module link
//   to the User Management module)
if (!isInstructor(actor)) { lastError = "NOT_AUTHORISED"; return false; }
// ↑ POLYMORPHISM (runtime) — instanceof check inside isInstructor() below decides
//   behaviour based on the ACTUAL object type (Instructor vs Student) at runtime,
//   even though the parameter type is the more general "User"

if (dto.getCourseID()          == null || dto.getCourseID().trim().isEmpty()    ||
dto.getCourseTitle()       == null || dto.getCourseTitle().trim().isEmpty() ||
dto.getCourseDescription() == null || dto.getCourseDescription().trim().isEmpty()) {
lastError = "MISSING_FIELDS";
return false;
}
if (!dto.getCourseID().matches("[A-Za-z0-9]{1,10}")) {
lastError = "INVALID_CODE_FORMAT";
return false;
}
if (database.containsKey(dto.getCourseID())) {
lastError = "DUPLICATE_CODE";
return false;
}

database.put(dto.getCourseID(),
new Course(dto.getCourseID(), dto.getCourseTitle(), dto.getCourseDescription()));
// ↑ ENCAPSULATION (this is the ONLY place a raw Course object gets created from a DTO —
//   the controller owns this responsibility, not the UI)
// ↑ ASSOCIATION (controller creates and stores a Course — "uses-a" relationship)
System.out.println("Course created: " + dto.getCourseID());
return true;
}

// ── UPDATE COURSE ─────────────────────────────────────────────────────

public boolean updateCourse(String courseID, CourseDTO dto, User actor) {
if (!isInstructor(actor)) { lastError = "NOT_AUTHORISED"; return false; }

if (dto.getCourseTitle()       == null || dto.getCourseTitle().trim().isEmpty() ||
dto.getCourseDescription() == null || dto.getCourseDescription().trim().isEmpty()) {
lastError = "BLANK_FIELD";
return false;
}
Course course = database.get(courseID);
if (course == null) { lastError = "NOT_FOUND"; return false; }

course.setCourseTitle(dto.getCourseTitle());
course.setDescription(dto.getCourseDescription());
// ↑ ENCAPSULATION (controller can only modify Course through its public setters —
//   it cannot reach into Course's private fields directly)
System.out.println("Course updated: " + courseID);
return true;
}

// ── DELETE COURSE ─────────────────────────────────────────────────────

public boolean deleteCourse(String courseID, User actor) {
if (!isInstructor(actor)) { lastError = "NOT_AUTHORISED"; return false; }
if (!database.containsKey(courseID)) { lastError = "NOT_FOUND"; return false; }

database.remove(courseID);
System.out.println("Course deleted: " + courseID);
return true;
}

// ── ADD TOPIC ─────────────────────────────────────────────────────────

public boolean addTopic(String courseID, String topicTitle, User actor) {
if (!isInstructor(actor)) { lastError = "NOT_AUTHORISED"; return false; }

Course course = database.get(courseID);
if (course == null) { lastError = "NOT_FOUND"; return false; }
if (course.getTopicsList().size() >= MAX_TOPICS) { lastError = "TOPIC_LIMIT"; return false; }

course.appendTopic(new Topic(topicTitle));
// ↑ COMPOSITION (Course owns Topic objects — this line is composition in action;
//   the new Topic is created and immediately placed inside the Course's internal list)
return true;
}

// ── REORDER TOPICS ────────────────────────────────────────────────────

public void reorderTopics(String courseID, ArrayList<Integer> newOrder, User actor) {
if (!isInstructor(actor)) return;

Course course = database.get(courseID);
if (course == null) return;

ArrayList<Topic> original  = course.getTopicsList();
ArrayList<Topic> reordered = new ArrayList<>();
for (int idx : newOrder)
if (idx >= 0 && idx < original.size()) reordered.add(original.get(idx));
original.clear();
original.addAll(reordered);
// ↑ COMPOSITION (still operating on the SAME Topic objects owned by Course —
//   just changing their order inside the owning ArrayList)
}

// ── UPLOAD MATERIAL ───────────────────────────────────────────────────

public boolean uploadMaterial(String courseID, MaterialDTO dto,
int topicIndex, User actor) {
if (!isInstructor(actor)) { lastError = "NOT_AUTHORISED"; return false; }

if (dto.getFilePath() == null || dto.getFilePath().trim().isEmpty()) {
lastError = "INVALID_FILE"; return false;
}
boolean validExt = false;
String lower = dto.getFilePath().toLowerCase();
for (String ext : ALLOWED_EXTENSIONS) {
if (lower.endsWith("." + ext)) { validExt = true; break; }
}
if (!validExt) { lastError = "INVALID_FORMAT"; return false; }

Course course = database.get(courseID);
if (course == null) { lastError = "NOT_FOUND"; return false; }
if (topicIndex < 0 || topicIndex >= course.getTopicsList().size()) {
lastError = "TOPIC_NOT_FOUND"; return false;
}

course.getTopicsList().get(topicIndex)
.appendMaterial(new Material(dto.getFilePath(), dto.getTitle()));
// ↑ COMPOSITION CHAIN (this single line walks Course → Topic → Material —
//   the clearest example of nested composition in this module:
//   Course owns Topics, and that specific Topic owns this new Material)
return true;
}

// ── READ operations (available to all roles) ──────────────────────────

public Collection<Course> getCatalogCourses() {
return database.values();
// ↑ AGGREGATION (returning the actual Course objects held by the database —
//   the caller now also references them, but database still "has" them)
}

public Course viewCourseContent(String courseID) {
return database.get(courseID);
}

/**
* POLYMORPHISM — Method overloading (compile-time):
* filterCatalog(String)        — keyword across all fields
* filterCatalog(String,String) — keyword within a specific field
*/
public List<Course> filterCatalog(String keyword) {
// ↑ POLYMORPHISM (COMPILE-TIME) — method overloading, version 1 of 2
List<Course> result = new ArrayList<>();
String kw = keyword.toLowerCase();
for (Course c : database.values()) {
if (c.getCourseTitle().toLowerCase().contains(kw) ||
c.getCourseID().toLowerCase().contains(kw)    ||
c.getDescription().toLowerCase().contains(kw))
result.add(c);
}
return result;
}

// Overloaded — search within a specific field only
public List<Course> filterCatalog(String keyword, String field) {
// ↑ POLYMORPHISM (COMPILE-TIME) — method overloading, version 2 of 2
//   SAME method name "filterCatalog", DIFFERENT parameter list.
//   Java decides which version to call at COMPILE TIME based on argument count.
List<Course> result = new ArrayList<>();
String kw = keyword.toLowerCase();
for (Course c : database.values()) {
switch (field.toLowerCase()) {
case "id":    if (c.getCourseID().toLowerCase().contains(kw))    result.add(c); break;
case "title": if (c.getCourseTitle().toLowerCase().contains(kw)) result.add(c); break;
case "desc":  if (c.getDescription().toLowerCase().contains(kw)) result.add(c); break;
default:      return filterCatalog(keyword);
// ↑ this calls the OTHER overloaded version — proof both coexist correctly
}
}
return result;
}

// ── HELPERS ───────────────────────────────────────────────────────────

public String getLastError() { return lastError; }
// ↑ ENCAPSULATION (controlled read-only access to private lastError field)

public boolean isTopicLimitReached(String courseID) {
Course c = database.get(courseID);
return c != null && c.getTopicsList().size() >= MAX_TOPICS;
}

/** Expose the static map for cross-module lookups if needed. */
public static HashMap<String, Course> getDatabase() { return database; }
// ↑ Note: this technically WEAKENS encapsulation slightly — it exposes the live HashMap
//   reference to outside classes (likely added so the Enrolment module can cross-check
//   against existing courses). Worth mentioning as a deliberate trade-off for module
//   integration, not an oversight.

/**
* Role guard: returns true if actor is null (seed bypass) OR is an Instructor.
* Students are read-only; they never pass this check.
*/
private boolean isInstructor(User actor) {
return actor == null || actor instanceof Instructor;
// ↑ POLYMORPHISM (RUNTIME) — the clearest runtime polymorphism check in this class.
//   The parameter type is "User" (the general/parent type), but
//   "instanceof Instructor" checks the ACTUAL object type at runtime. This is how
//   Java decides, at runtime, whether the object passed in is really an Instructor
//   or just a generic/Student User. This is the integration point with the
//   User Management module — User and Instructor classes live there,
//   but CourseController consumes them polymorphically here.
}
}