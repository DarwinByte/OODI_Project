/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: ContentFactory
// RUBRIC FOCUS: CO3 (Factory Design Pattern - Creational)
//
// Q&A DEFENSE:
// "This is our Factory Pattern. Instead of controllers using the 'new' keyword 
// directly, they pass parameters to this Factory. The Factory centralizes 
// object creation logic. If we ever add new content types (like 'Quiz' or 
// 'Assignment'), we only update the Factory, adhering to the Open/Closed Principle."
// ==============================================================================
package com.elearning.utils;

import com.elearning.entities.Course;
import com.elearning.entities.Material;
import com.elearning.entities.Topic;

public class ContentFactory {
    
    // Centralised creation for Courses
    public static Course createCourse(String id, String title, String desc) {
        return new Course(id, title, desc);
    }

    // Centralised creation for Topics
    public static Topic createTopic(String title) {
        return new Topic(title);
    }

    // Centralised creation for Materials
    public static Material createMaterial(String path, String title) {
        return new Material(path, title);
    }
}