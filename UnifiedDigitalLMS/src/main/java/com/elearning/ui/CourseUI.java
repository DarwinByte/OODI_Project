/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.CourseController;
import com.elearning.dtos.CourseDTO;
import com.elearning.entities.Course;
import com.elearning.entities.Instructor;
import com.elearning.entities.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
* UI: CourseUI
* Purpose: Instructor-facing CRUD screen for Course Management.
*
* SESSION INJECTION: The active User is injected via the constructor.
* The controller uses the user reference for role-based guards, so the
* UI never has to hard-code permission checks.
*
* Tabs: Create | Edit | Delete
*
* Uses the UNIFIED UITheme (com.elearning.ui.UITheme) — same dark palette
* the rest of the team uses.
*/
public class CourseUI extends JFrame {
// ↑ INHERITANCE ("extends JFrame" — CourseUI inherits all window
//   behaviour from Swing's JFrame, the same pattern used by
//   MaterialUI and every other window in the system)

// ── Session fields ────────────────────────────────────────────────────
private final CourseController controller;
// ↑ ENCAPSULATION (private final — cannot be reassigned or read
//   directly from outside this class)
// ↑ ASSOCIATION (CourseUI "has-a" CourseController — the structural
//   link that lets the UI delegate all CRUD logic instead of
//   performing it itself)
private final User             activeUser;
// ↑ ENCAPSULATION (private final)
// ↑ ASSOCIATION (CourseUI holds a reference to the logged-in User —
//   the cross-module link to the User Management module; CourseUI
//   never inspects whether this is an Instructor or Student, it just
//   carries the reference and forwards it onward)

// ── Create tab widgets ────────────────────────────────────────────────
private UITheme.StyledTextField  tfCreateID;
private UITheme.StyledTextField  tfCreateTitle;
private JTextArea                taCreateDesc;

// ── Edit tab widgets ──────────────────────────────────────────────────
private UITheme.StyledTextField  tfEditID;
private UITheme.StyledTextField  tfEditTitle;
private JTextArea                taEditDesc;

// ── Delete tab widgets ────────────────────────────────────────────────
private UITheme.StyledTextField  tfDeleteID;

// ── Tabs ──────────────────────────────────────────────────────────────
private JTabbedPane tabs;

// ── Constructor: SESSION INJECTION ────────────────────────────────────

/**
* @param controller  Shared CourseController (carries the static course DB)
* @param activeUser  The authenticated user — passed to every controller
* write-call so role guards can fire.
*/
public CourseUI(CourseController controller, User activeUser) {
this.controller = controller;
this.activeUser = activeUser;
// ↑ DEPENDENCY (constructor receives both objects from whichever
//   class opened this window — the launcher screen after login —
//   and stores them as fields for the lifetime of this window)
UITheme.applyGlobalDefaults();
initComponents();
}

// ── UI Construction ───────────────────────────────────────────────────

private void initComponents() {
setTitle("Course Manager — " + activeUser.getName());
// ↑ ASSOCIATION (calls a method on the held User reference —
//   direct evidence the "has-a" relationship is actually exercised,
//   not just declared)
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setSize(640, 520);
setMinimumSize(new Dimension(560, 460));
setLocationRelativeTo(null);
setResizable(false);

getContentPane().setBackground(UITheme.BG_DARK);
setLayout(new BorderLayout());

// ── Header ───────────────────────────────────────────────────────
UITheme.GradientHeader header = new UITheme.GradientHeader(
"Course Manager",
"Create · Edit · Delete  |  " + activeUser.getName());
// ↑ ASSOCIATION (again calling activeUser.getName() — the
//   instructor's name is displayed directly in the header,
//   confirming the session data flows all the way into the view)
add(header, BorderLayout.NORTH);

// ── Tabs ─────────────────────────────────────────────────────────
tabs = new JTabbedPane(JTabbedPane.TOP);
tabs.setBackground(UITheme.BG_DARK);
tabs.setForeground(UITheme.TEXT_PRIMARY);
tabs.setFont(UITheme.FONT_LABEL);

tabs.addTab("  + Create  ", buildCreateTab());
tabs.addTab("  ✎ Edit    ", buildEditTab());
tabs.addTab("  ✕ Delete  ", buildDeleteTab());

add(tabs, BorderLayout.CENTER);
}

// ── CREATE TAB ────────────────────────────────────────────────────────

private JPanel buildCreateTab() {
JPanel wrap = tabWrapper();

UITheme.RoundPanel card = new UITheme.RoundPanel(16);
card.setLayout(new GridBagLayout());
card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

GridBagConstraints g = gbc();

tfCreateID    = new UITheme.StyledTextField("e.g. BCN1043");
tfCreateTitle = new UITheme.StyledTextField("e.g. Introduction to Programming");
taCreateDesc  = makeTextArea("Describe the course...");

addFormRow(card, g, 0, "Course Code",  tfCreateID);
addFormRow(card, g, 1, "Course Title", tfCreateTitle);

g.gridx = 0; g.gridy = 2; g.weightx = 0; g.anchor = GridBagConstraints.EAST;
card.add(fieldLabel("Description"), g);
JScrollPane sp = styledScroll(taCreateDesc, 300, 100);
g.gridx = 1; g.weightx = 1; g.gridy = 2; g.anchor = GridBagConstraints.WEST;
card.add(sp, g);

UITheme.StyledButton btnSubmit = new UITheme.StyledButton(
"Submit Course", UITheme.SUCCESS_GREEN);
btnSubmit.setPreferredSize(new Dimension(170, 42));
btnSubmit.addActionListener(e -> clickCreateCourse());
g.gridx = 1; g.gridy = 3; g.weightx = 0; g.anchor = GridBagConstraints.EAST;
card.add(btnSubmit, g);

wrap.add(card, BorderLayout.CENTER);
return wrap;
}

// ── EDIT TAB ──────────────────────────────────────────────────────────

private JPanel buildEditTab() {
JPanel wrap = tabWrapper();

UITheme.RoundPanel card = new UITheme.RoundPanel(16);
card.setLayout(new GridBagLayout());
card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

GridBagConstraints g = gbc();

tfEditID    = new UITheme.StyledTextField("Enter Course Code to load");
tfEditTitle = new UITheme.StyledTextField("Updated title...");
taEditDesc  = makeTextArea("Updated description...");

addFormRow(card, g, 0, "Course Code", tfEditID);

UITheme.StyledButton btnLoad = new UITheme.StyledButton("Load", UITheme.ACCENT_BLUE);
g.gridwidth = 2; g.gridx = 0;
card.add(btnLoad, g);
btnLoad.addActionListener(e -> clickLoadForEdit());
g.gridx = 1; g.gridy = 1; g.weightx = 0; g.anchor = GridBagConstraints.WEST;
card.add(btnLoad, g);

addFormRow(card, g, 2, "New Title", tfEditTitle);

g.gridx = 0; g.gridy = 3; g.weightx = 0; g.anchor = GridBagConstraints.EAST;
card.add(fieldLabel("New Description"), g);
JScrollPane sp = styledScroll(taEditDesc, 300, 100);
g.gridx = 1; g.weightx = 1; g.gridy = 3; g.anchor = GridBagConstraints.WEST;
card.add(sp, g);

UITheme.StyledButton btnSave = new UITheme.StyledButton(
"Save Changes", UITheme.ACCENT_BLUE);
btnSave.setPreferredSize(new Dimension(160, 42));
btnSave.addActionListener(e -> clickSaveChanges());
g.gridx = 1; g.gridy = 4; g.weightx = 0; g.anchor = GridBagConstraints.EAST;
card.add(btnSave, g);

wrap.add(card, BorderLayout.CENTER);
return wrap;
}

// ── DELETE TAB ────────────────────────────────────────────────────────

private JPanel buildDeleteTab() {
JPanel wrap = tabWrapper();

UITheme.RoundPanel card = new UITheme.RoundPanel(16);
card.setLayout(new GridBagLayout());
card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

GridBagConstraints g = gbc();

tfDeleteID = new UITheme.StyledTextField("Enter Course Code to delete");
addFormRow(card, g, 0, "Course Code", tfDeleteID);

// Warning label
JLabel warn = new JLabel(
"<html><body style='color:#FF808A'>" +
"⚠  This action is permanent and cannot be undone.</body></html>");
warn.setFont(UITheme.FONT_SMALL);
g.gridx = 1; g.gridy = 1;
card.add(warn, g);

UITheme.StyledButton btnDel = new UITheme.StyledButton(
"Delete Course", UITheme.DANGER_RED);
btnDel.setPreferredSize(new Dimension(160, 42));
btnDel.addActionListener(e -> clickDeleteCourse(tfDeleteID.getText().trim()));
g.gridx = 1; g.gridy = 2; g.weightx = 0; g.anchor = GridBagConstraints.EAST;
card.add(btnDel, g);

wrap.add(card, BorderLayout.CENTER);
return wrap;
}

// ── Action handlers ───────────────────────────────────────────────────

/**
* Passes activeUser to controller — role guard fires inside controller.
*/
public void clickCreateCourse() {
CourseDTO dto = new CourseDTO(
tfCreateID.getText().trim(),
tfCreateTitle.getText().trim(),
taCreateDesc.getText().trim());
// ↑ DEPENDENCY (CourseUI creates a temporary CourseDTO purely to
//   carry form data — it is discarded right after the call below)

if (controller.createCourse(dto, activeUser)) {
// ↑ ASSOCIATION (calls the controller's createCourse() method —
//   the "uses-a" relationship in action)
// ↑ this is also the exact point where activeUser is forwarded
//   into the role guard — CourseUI itself contains ZERO
//   if/else logic checking whether activeUser is an Instructor
//   or Student; that decision is entirely the controller's job,
//   which is RUNTIME POLYMORPHISM happening one layer down via
//   isInstructor(actor)'s "instanceof Instructor" check
showMsg("Course created successfully!", "Success",
JOptionPane.INFORMATION_MESSAGE);
tfCreateID.setText("");
tfCreateTitle.setText("");
taCreateDesc.setText("");
} else {
handleError(controller.getLastError());
// ↑ ASSOCIATION (calls another controller method to retrieve
//   the private lastError field via its public getter —
//   ENCAPSULATION is respected here too: CourseUI never reads
//   a private field directly, only through getLastError())
}
}

public void clickLoadForEdit() {
Course c = controller.viewCourseContent(tfEditID.getText().trim());
// ↑ ASSOCIATION (controller call returns a Course object reference)
if (c == null) {
showMsg("No course found with that code.", "Not Found",
JOptionPane.ERROR_MESSAGE);
return;
}
tfEditTitle.setText(c.getCourseTitle());
// ↑ ENCAPSULATION (CourseUI reads Course's title only through its
//   public getter getCourseTitle() — it cannot access the
//   inherited "title" field directly since it's protected, not
//   public; this also indirectly touches INHERITANCE, since
//   getCourseTitle() internally returns the field Course inherited
//   from ContentItem)
taEditDesc.setText(c.getDescription());
// ↑ ENCAPSULATION (same pattern — public getter, private field)
}

public void clickSaveChanges() {
String id = tfEditID.getText().trim();
CourseDTO dto = new CourseDTO(
id, tfEditTitle.getText().trim(), taEditDesc.getText().trim());
// ↑ DEPENDENCY (another temporary CourseDTO created just to carry
//   the updated form values to the controller)

if (controller.updateCourse(id, dto, activeUser)) {
// ↑ ASSOCIATION + role guard forwarding, same pattern as
//   clickCreateCourse() above
showMsg("Course updated successfully!", "Updated",
JOptionPane.INFORMATION_MESSAGE);
} else {
handleError(controller.getLastError());
}
}

public void clickDeleteCourse(String courseID) {
int confirm = JOptionPane.showConfirmDialog(this,
"Permanently delete course \"" + courseID + "\"?",
"Confirm Delete", JOptionPane.YES_NO_OPTION,
JOptionPane.WARNING_MESSAGE);

if (confirm == JOptionPane.YES_OPTION) {
if (controller.deleteCourse(courseID, activeUser)) {
// ↑ ASSOCIATION + role guard forwarding, same pattern again
showMsg("Course deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
tfDeleteID.setText("");
} else {
handleError(controller.getLastError());
}
}
}

// ── Error dispatcher ──────────────────────────────────────────────────

private void handleError(String errorCode) {
switch (errorCode) {
case "NOT_AUTHORISED" ->
// ↑ this is the visible UI-side EFFECT of the runtime
//   polymorphism check that happened inside the controller —
//   if isInstructor(actor) returned false (meaning the
//   actual object passed was NOT an Instructor instance),
//   this exact message fires
showMsg("Only Instructors can perform this action.", "Access Denied",
JOptionPane.WARNING_MESSAGE);
case "MISSING_FIELDS", "INVALID_CODE_FORMAT" ->
showMsg("Fill all fields. Course Code: alphanumeric, max 10 chars.",
"Validation Error", JOptionPane.ERROR_MESSAGE);
case "DUPLICATE_CODE" ->
showMsg("Course Code already exists. Use a unique code.",
"Duplicate Code", JOptionPane.ERROR_MESSAGE);
case "BLANK_FIELD" ->
showMsg("Title and description cannot be blank.",
"Blank Field", JOptionPane.ERROR_MESSAGE);
case "ACTIVE_ENROLLMENTS" ->
showMsg("Cannot delete a course with active enrolments.",
"Deletion Blocked", JOptionPane.ERROR_MESSAGE);
default ->
showMsg("Operation failed: " + errorCode,
"Error", JOptionPane.ERROR_MESSAGE);
}
}

private void showMsg(String msg, String title, int type) {
JOptionPane.showMessageDialog(this, msg, title, type);
}

// ── Layout helpers ────────────────────────────────────────────────────

private JPanel tabWrapper() {
JPanel wrap = new JPanel(new BorderLayout());
wrap.setBackground(UITheme.BG_DARK);
wrap.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
return wrap;
}

private GridBagConstraints gbc() {
GridBagConstraints g = new GridBagConstraints();
g.insets = new Insets(8, 8, 8, 8);
g.fill   = GridBagConstraints.HORIZONTAL;
return g;
}

private void addFormRow(JPanel p, GridBagConstraints g,
int row, String labelText, JComponent field) {
g.gridx = 0; g.gridy = row; g.gridwidth = 1;
g.weightx = 0; g.anchor = GridBagConstraints.EAST;
p.add(fieldLabel(labelText), g);
g.gridx = 1; g.weightx = 1; g.anchor = GridBagConstraints.WEST;
p.add(field, g);
}

private JLabel fieldLabel(String text) {
JLabel l = new JLabel(text + "  ");
l.setFont(UITheme.FONT_LABEL);
l.setForeground(UITheme.TEXT_MUTED);
return l;
}

private JTextArea makeTextArea(String placeholder) {
JTextArea ta = new JTextArea(4, 20);
ta.setFont(UITheme.FONT_BODY);
ta.setForeground(UITheme.TEXT_PRIMARY);
ta.setBackground(UITheme.BG_INPUT);
ta.setCaretColor(UITheme.ACCENT_BLUE);
ta.setLineWrap(true);
ta.setWrapStyleWord(true);
ta.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
return ta;
}

private JScrollPane styledScroll(JTextArea ta, int w, int h) {
JScrollPane sp = new JScrollPane(ta);
sp.setPreferredSize(new Dimension(w, h));
sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
sp.getViewport().setBackground(UITheme.BG_INPUT);
UITheme.styleScrollPane(sp);
return sp;
}
}