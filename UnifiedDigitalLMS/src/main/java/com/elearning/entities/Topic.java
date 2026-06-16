/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.entities;

import java.util.ArrayList;

/**
* Entity: Topic
* INHERITANCE  : extends ContentItem
* POLYMORPHISM : implements Displayable + overrides getSummary()
*/
public class Topic extends ContentItem implements Displayable {
// ↑ INHERITANCE ("extends ContentItem" — Topic inherits the protected
//   title field and the getTitle()/toString() methods from ContentItem,
//   the same parent class Course also extends from)
// ↑ POLYMORPHISM ("implements Displayable" — Topic commits to the same
//   interface contract as Course, so both can be handled uniformly
//   wherever a Displayable reference is expected)

private ArrayList<Material> materialList;
// ↑ ENCAPSULATION (private field, no direct external access)
// ↑ COMPOSITION (Topic owns a list of Material objects — this is the
//   second link in the Course → Topic → Material composition chain;
//   the ArrayList lives entirely inside this Topic object)

public Topic(String topicTitle) {
super(topicTitle);
// ↑ INHERITANCE (calls the ContentItem parent constructor to
//   initialise the inherited "title" field with topicTitle)
this.materialList = new ArrayList<>();
// ↑ COMPOSITION (the Material container is created here, inside
//   Topic's own constructor — its lifecycle is tied to this Topic)
}

public String getTopicTitle()                  { return title;
}
// ↑ INHERITANCE (returns "title", a field declared in the parent class
//   ContentItem, not in Topic itself)
public ArrayList<Material> getMaterialList()   { return materialList; }
// ↑ COMPOSITION (exposes the owned Material collection so the controller
//   can reach the final link in the chain: Course → Topic → Material)
public void appendMaterial(Material material)  { materialList.add(material); }
// ↑ COMPOSITION (this is the action that performs ownership — Topic
//   takes a Material object and adds it into its own internal
//   ArrayList; the Material now lives inside this Topic)

@Override
public String getSummary() {
return title + " (" + materialList.size() + " material(s))";
}
// ↑ POLYMORPHISM (RUNTIME) — overrides the abstract getSummary()
//   declared in ContentItem. Same method name as Course.getSummary()
//   and Material.getSummary(), but each returns a completely different
//   result depending on which object Java is actually working with
//   at runtime — this is the core demonstration of runtime polymorphism

@Override public String getDisplayName()        { return title; }
// ↑ POLYMORPHISM (fulfils the first half of the Displayable interface
//   contract — called when a Topic is passed around as a Displayable
//   reference, exactly like Course.getDisplayName() is for Course)
@Override public String getDisplayDescription() { return materialList.size() + " material(s)"; }
// ↑ POLYMORPHISM (second half of the Displayable interface contract —
//   notice this is intentionally different content than Course's
//   version, proving each class decides its own implementation)
}