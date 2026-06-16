/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.controllers;

import com.elearning.entities.Enrolment;
import com.elearning.entities.Progress;
import java.util.*;

/**
 * EnrolmentDatabase
 * Internal static storage for enrolments and progress records.
 * Static maps ensure all controller instances share the same data
 * (matches the pattern of UserController and CourseController).
 * Repackaged from com.mycompany.digitallearningsystems.
 *
 * OOP PRINCIPLES APPLIED IN THIS CLASS:
 * ─────────────────────────────────────
 * 1. ENCAPSULATION    : All fields (enrolDb, progressDb, counter) are private and static.
 * External classes cannot access the HashMaps directly —
 * only through the provided public methods (put, get, remove, etc.).
 *
 * 2. ABSTRACTION      : Hides the internal HashMap structure from EnrolmentController.
 * The controller just calls put(), get(), remove() without
 * knowing how data is stored internally.
 *
 * 3. DEPENDENCY       : Stores and returns Enrolment and Progress objects.
 * This class depends on both entity classes
 * (imported from com.elearning.entities).
 *
 * 4. SINGLE RESPONSIBILITY : This class is responsible only for data storage
 * and retrieval — no business logic here.
 * Business rules stay in EnrolmentController.
 */
public class EnrolmentDatabase {

// ENCAPSULATION: private static fields — shared across all instances but
//                not directly accessible from outside this class
// DEPENDENCY: stores Enrolment and Progress entity objects
private static final HashMap<String, Enrolment> enrolDb    = new HashMap<>();
private static final HashMap<String, Progress>  progressDb = new HashMap<>();

// ENCAPSULATION: private static counter — ID generation is controlled internally
private static int counter = 100;

// ABSTRACTION: exposes ID generation as a simple method — counter logic is hidden
public String generateID()                       { return "ENR-" + (++counter);
}

// ABSTRACTION: simple CRUD interface over the internal HashMap
// DEPENDENCY: accepts and returns Enrolment objects
public void   put(String k, Enrolment e)         { enrolDb.put(k, e); }
public Enrolment get(String k)                   { return enrolDb.get(k);
}
public void   remove(String k)                   { enrolDb.remove(k);
}
public boolean containsKey(String k)             { return enrolDb.containsKey(k); }
public Collection<Enrolment> values()            { return enrolDb.values(); }
public boolean isEmpty()                         { return enrolDb.isEmpty();
}

// ABSTRACTION: simple CRUD interface over the progress HashMap
// DEPENDENCY: accepts and returns Progress objects
public void   putProgress(String k, Progress p)  { progressDb.put(k, p); }
public Progress getProgress(String k)             { return progressDb.get(k); }
public Collection<Progress> allProgress()         { return progressDb.values(); }

// ABSTRACTION: key-building logic is centralised here so callers never
//              need to know the key format ("studentID_courseID")
public String progressKey(String sID, String cID) { return sID + "_" + cID; }
}