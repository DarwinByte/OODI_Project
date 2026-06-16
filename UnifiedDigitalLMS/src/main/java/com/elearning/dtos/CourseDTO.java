/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.dtos;

/**
* DTO: CourseDTO
* Purpose: Carries course form data between CourseUI and CourseController.
* Encapsulation: private fields — consistent with LoginDTO / RegisterDTO pattern.
*/
public class CourseDTO {
// ↑ DEPENDENCY (this class exists purely as a temporary data carrier —
//   CourseUI creates one of these inside clickCreateCourse()/
//   clickSaveChanges(), passes it into CourseController.createCourse()
//   or updateCourse(), and the object is discarded right after. Neither
//   class keeps a long-term reference to it)
// ↑ this is also the class that PROVES the User Management module and
//   this module follow the same design convention — the class comment
//   explicitly states it mirrors LoginDTO/RegisterDTO, meaning the DTO
//   pattern is consistent across both modules, not just locally invented

private final String courseID;
// ↑ ENCAPSULATION (private — and "final" makes it immutable once the
//   object is constructed, same strict pattern used in Material's
//   filePath and MaterialDTO's fields)
private final String courseTitle;
// ↑ ENCAPSULATION (private final — read-only after construction)
private final String courseDescription;
// ↑ ENCAPSULATION (private final — read-only after construction)

public CourseDTO(String courseID, String courseTitle, String courseDescription) {
this.courseID          = courseID;
this.courseTitle       = courseTitle;
this.courseDescription = courseDescription;
// ↑ ENCAPSULATION (these are the ONLY three lines in the entire
//   class permitted to assign these fields — because they're
//   final, this can only happen once, inside this constructor)
}

public String getCourseID()          { return courseID; }
// ↑ ENCAPSULATION (read-only access — no setCourseID() exists, meaning
//   once a CourseDTO carries a course code, it cannot be altered while
//   in transit between CourseUI and CourseController)
public String getCourseTitle()       { return courseTitle; }
// ↑ ENCAPSULATION (read-only access)
public String getCourseDescription() { return courseDescription; }
// ↑ ENCAPSULATION (read-only access)
}