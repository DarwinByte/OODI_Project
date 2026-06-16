/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.entities;

/**
* Interface: Displayable
* Purpose: Runtime polymorphism — any content entity can be displayed
* uniformly in the UI regardless of concrete type.
*/
public interface Displayable {
// ↑ POLYMORPHISM — this interface is the foundation of interface-based
//   runtime polymorphism in the whole module. Course, Topic, and
//   Material all implement this same interface, which means any of the
//   three can be referred to and handled through the single type
//   "Displayable" — without the calling code needing to know which one
//   it actually is

String getDisplayName();
// ↑ POLYMORPHISM (this is the first half of the contract — an
//   abstract method signature with NO body here, but Course, Topic,
//   and Material each supply their own different implementation when
//   they implement this interface)

String getDisplayDescription();
// ↑ POLYMORPHISM (second half of the contract — same idea: zero
//   implementation here, three different implementations across the
//   three entity classes)
}