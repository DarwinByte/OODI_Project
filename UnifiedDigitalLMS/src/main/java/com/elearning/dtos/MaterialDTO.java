/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.dtos;

/**
* DTO: MaterialDTO
* Purpose: Carries material upload data between MaterialUI and CourseController.
*/
public class MaterialDTO {
// ↑ DEPENDENCY (this whole class exists purely as a temporary data
//   carrier — MaterialUI creates one of these, passes it to
//   CourseController.uploadMaterial(), and the object is discarded
//   right after. Neither class stores a long-term reference to it)

private final String filePath;
// ↑ ENCAPSULATION (private — and "final" makes it the strongest form
//   of encapsulation, same pattern as Material entity's filePath field.
//   Once a MaterialDTO is built, its filePath can never change)
private final String title;
// ↑ ENCAPSULATION (private final — same immutability guarantee as
//   filePath above)

public MaterialDTO(String filePath, String title) {
this.filePath = filePath;
this.title    = title;
// ↑ ENCAPSULATION (these are the ONLY two lines in the entire class
//   permitted to assign these fields — because they're final, this
//   can only happen once, inside the constructor, exactly like the
//   Material entity class)
}

public String getFilePath() { return filePath; }
// ↑ ENCAPSULATION (read-only access — no setFilePath() exists anywhere)
public String getTitle()    { return title; }
// ↑ ENCAPSULATION (read-only access — no setTitle() exists anywhere)
}