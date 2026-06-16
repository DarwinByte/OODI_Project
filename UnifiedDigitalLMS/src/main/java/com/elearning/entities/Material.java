/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.entities;

/**
* Entity: Material
* INHERITANCE  : extends ContentItem
* POLYMORPHISM : implements Displayable + overrides getSummary()
* ENCAPSULATION: private fields, public getters only (immutable after creation)
*/
public class Material extends ContentItem implements Displayable {
// ↑ INHERITANCE ("extends ContentItem" — Material inherits the protected
//   title field and the getTitle() method from ContentItem, the same
//   parent class Course and Topic also extend from. This completes the
//   three-way inheritance fan-out: Course, Topic, and Material all share
//   one common ancestor)
// ↑ POLYMORPHISM ("implements Displayable" — Material commits to the same
//   interface contract as Course and Topic, completing the three-way
//   implementation of Displayable)

private final String filePath;
// ↑ ENCAPSULATION (private field — and notice the "final" keyword here,
//   which Course and Topic's fields don't have. This is the STRONGEST
//   form of encapsulation in the whole entity layer: filePath cannot be
//   reassigned even by Material's own methods after the constructor
//   runs. No setter exists anywhere in this class — by design, a
//   Material's file path is permanently fixed once uploaded)

public Material(String filePath, String title) {
super(title);
// ↑ INHERITANCE (calls the ContentItem parent constructor to
//   initialise the inherited "title" field with title)
this.filePath = filePath;
// ↑ ENCAPSULATION (this is the ONLY line in the entire class
//   permitted to assign filePath — because it's final, this
//   assignment can only happen once, inside the constructor)
}

public String getFilePath() { return filePath; }
// ↑ ENCAPSULATION (read-only access — getter exists, setter does not,
//   reinforced further by the "final" modifier on the field itself)

@Override public String getSummary()            { return title + "  ·  " + filePath;
}
// ↑ POLYMORPHISM (RUNTIME) — overrides the abstract getSummary()
//   declared in ContentItem. Same method name as Course.getSummary()
//   and Topic.getSummary(), but returns yet another different format.
//   This is the third and final piece of proof that one abstract method
//   signature produces three distinct runtime behaviours depending on
//   the actual object type
@Override public String getDisplayName()        { return title; }
// ↑ POLYMORPHISM (fulfils the first half of the Displayable interface
//   contract — identical implementation to Topic.getDisplayName(),
//   which is fine; polymorphism doesn't require different code, only
//   that each class is free to decide its own implementation)
@Override public String getDisplayDescription() { return "File: " + filePath; }
// ↑ POLYMORPHISM (second half of the Displayable interface contract —
//   this version is unique to Material, referencing filePath which only
//   this class has)

@Override public String toString() { return title; }
// ↑ POLYMORPHISM (overrides Object's toString(), inherited transitively
//   through ContentItem — also INHERITANCE, since ContentItem already
//   supplies a toString() that this further overrides, same pattern
//   as Course.toString())
}