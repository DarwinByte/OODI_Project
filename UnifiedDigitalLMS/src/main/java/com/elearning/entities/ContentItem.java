/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.entities;

/**
* Abstract Entity: ContentItem
* INHERITANCE  : Course, Topic, Material all extend this
* POLYMORPHISM : forces every subclass to implement getSummary()
*/
public abstract class ContentItem {
// ↑ INHERITANCE (this is the single parent class that Course, Topic,
//   and Material all extend — the root of the entire entity hierarchy
//   in the module)
// ↑ the "abstract" keyword means this class can NEVER be instantiated
//   directly — you cannot write "new ContentItem(...)" anywhere in the
//   codebase. It exists only to be extended. This is what enables the
//   abstract method below to enforce polymorphism on every subclass

protected String title;
// ↑ ENCAPSULATION ("protected" — not fully private, but still hidden
//   from any class outside this package/hierarchy. The choice of
//   "protected" instead of "private" is deliberate: it allows Course,
//   Topic, and Material to access "title" directly inside their own
//   methods, e.g. Course's getCourseTitle() returns "title" directly)
// ↑ INHERITANCE (this single field declaration is what Course, Topic,
//   and Material all inherit — none of them re-declare a "title" field
//   themselves, proving the whole point of putting it here once)

public ContentItem(String title) { this.title = title; }
// ↑ INHERITANCE (this is the constructor that every subclass's "super(...)"
//   call invokes — Course's "super(courseTitle)", Topic's
//   "super(topicTitle)", and Material's "super(title)" all run THIS
//   exact line to set their inherited title field)

public String getTitle() { return title; }
// ↑ INHERITANCE (a concrete, ready-to-use method that Course, Topic,
//   and Material all receive automatically without writing any code
//   themselves — none of the three subclasses re-implement this method)
// ↑ ENCAPSULATION (controlled read access to the protected title field,
//   available uniformly to all three entity types)

public abstract String getSummary();
// ↑ POLYMORPHISM — this single line is the mechanism that FORCES
//   Course, Topic, and Material to each provide their own getSummary()
//   implementation. Java will refuse to compile any subclass that
//   forgets to override this. This is what makes runtime polymorphism
//   possible later: when this method is called on a ContentItem
//   reference, Java looks at the ACTUAL object type at runtime and
//   runs the correct subclass version

@Override
public String toString() { return title; }
// ↑ POLYMORPHISM (overrides Object's own toString() method — this is
//   the FIRST override in the chain; Course and Material then further
//   override this again with their own versions, while Topic does not
//   override it at all and simply inherits this exact implementation)
// ↑ INHERITANCE (this concrete implementation is what Topic actually
//   uses, since Topic never provides its own toString())
}