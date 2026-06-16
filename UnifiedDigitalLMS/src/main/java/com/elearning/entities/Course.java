/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.entities;
import java.util.ArrayList;
/**
* Entity: Course
* INHERITANCE  : extends ContentItem
* POLYMORPHISM : implements Displayable + overrides getSummary()
* ENCAPSULATION: private fields, public getters/setters
*/
public class Course extends ContentItem implements Displayable {
// ↑ INHERITANCE ("extends ContentItem" — Course inherits the protected
//   title field and the getTitle()/toString() methods from ContentItem)
// ↑ POLYMORPHISM ("implements Displayable" — Course commits to providing
//   getDisplayName() and getDisplayDescription(), enabling runtime
//   polymorphism wherever a Displayable reference is used)

private String courseID;
// ↑ ENCAPSULATION (private field — no setter exists for this anywhere in
//   the class, meaning courseID is immutable after construction)
private String description;
// ↑ ENCAPSULATION (private field, exposed only via getter/setter pair below)
private ArrayList<Topic> topicsList;
// ↑ ENCAPSULATION (private field)
// ↑ COMPOSITION (Course owns a list of Topic objects — this is the field
//   that makes Course → Topic a composition relationship; the ArrayList
//   lives entirely inside this Course object)

public Course(String courseID, String courseTitle, String description) {
super(courseTitle);
// ↑ INHERITANCE (calls the ContentItem parent constructor to initialise
//   the inherited "title" field with courseTitle)
this.courseID    = courseID;
this.description = description;
this.topicsList  = new ArrayList<>();
// ↑ COMPOSITION (the ArrayList is instantiated here, inside Course's own
//   constructor — proof that Topics' container is created and destroyed
//   together with the Course object's lifecycle)
}

// ── Getters ───────────────────────────────────────────────────────────
public String getCourseID()          { return courseID; }
// ↑ ENCAPSULATION (read-only access — getter exists, setter does not)
public String getCourseTitle()       { return title;
}         // inherited field
// ↑ INHERITANCE (this getter returns "title", a field declared in the
//   parent class ContentItem, not in Course itself)
public String getDescription()       { return description; }
public ArrayList<Topic> getTopicsList() { return topicsList; }
// ↑ COMPOSITION (this getter exposes the owned Topic collection so the
//   controller can navigate the Course → Topic → Material chain)

// ── Setters ───────────────────────────────────────────────────────────
public void setCourseTitle(String courseTitle) { this.title = courseTitle; }
// ↑ INHERITANCE (setter modifies the inherited "title" field directly)
// ↑ ENCAPSULATION (controlled write access — the only way to change title
//   from outside this class)
public void setDescription(String description) { this.description = description; }
// ↑ ENCAPSULATION (controlled write access to the private description field)

// ── Domain ────────────────────────────────────────────────────────────
public void appendTopic(Topic topic) { topicsList.add(topic); }
// ↑ COMPOSITION (this is the method that actually performs the ownership
//   action — Course takes a Topic object and adds it into its own
//   internal ArrayList; the Topic now lives inside this Course)

// ── Polymorphism ──────────────────────────────────────────────────────
@Override
public String getSummary() {
return "[" + courseID + "] " + title + " — " + topicsList.size() + " topic(s)";
}
// ↑ POLYMORPHISM (RUNTIME) — overrides the abstract getSummary() declared
//   in ContentItem. This is Course's own specific version; Topic and
//   Material will each provide a different version of the same method
//   name, and Java picks the correct one at runtime based on object type

@Override public String getDisplayName()        { return "[" + courseID + "] " + title;
}
// ↑ POLYMORPHISM (fulfils the Displayable interface contract — this is
//   what gets called when a Course is passed around as a Displayable
//   reference elsewhere in the system, e.g. in the controller)
@Override public String getDisplayDescription() { return description; }
// ↑ POLYMORPHISM (second half of the Displayable interface contract)

@Override
public String toString() { return "[" + courseID + "] " + title; }
// ↑ POLYMORPHISM (overrides Object's toString(), inherited transitively
//   through ContentItem — also counts as INHERITANCE since ContentItem
//   already provides a toString() that this one overrides further)
}