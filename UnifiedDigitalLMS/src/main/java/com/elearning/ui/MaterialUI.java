/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.CourseController;
import com.elearning.dtos.MaterialDTO;
import com.elearning.entities.Course;
import com.elearning.entities.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
* UI: MaterialUI
* Purpose: Upload dialog — attach a learning material file to a course topic.
*
* SESSION INJECTION: activeUser is passed in and forwarded to every
* controller write-call so the role guard fires correctly.
*
* Package: com.elearning.ui  (unified structure)
* Uses   : com.elearning.ui.UITheme  (single design system)
*/
public class MaterialUI extends JFrame {
// ↑ INHERITANCE ("extends JFrame" — MaterialUI inherits all window
//   behaviour from Swing's JFrame: title bar, close button, layout
//   management, event dispatch — without writing any of that code
//   itself. This is the same inheritance pattern used by every other
//   UI window in the system)

// ── Session / dependencies ────────────────────────────────────────────
private final CourseController controller;
// ↑ ENCAPSULATION (private final — MaterialUI cannot reassign its
//   controller reference after construction, and no other class can
//   read or modify it directly)
// ↑ ASSOCIATION (MaterialUI "has-a" CourseController — this is the
//   structural link that lets the UI delegate all business logic
//   instead of doing it itself)
private final String           courseID;
// ↑ ENCAPSULATION (private final)
private final User             activeUser;
// ↑ ENCAPSULATION (private final)
// ↑ ASSOCIATION (MaterialUI also holds a reference to the logged-in
//   User — this is the cross-module link to the User Management
//   module; MaterialUI doesn't know or care if it's a Student or
//   Instructor object, it just forwards it onward)

// ── Widgets ───────────────────────────────────────────────────────────
private UITheme.StyledTextField tfFilePath;
private UITheme.StyledTextField tfTitle;
// ↑ ASSOCIATION (MaterialUI uses UITheme's custom components — another
//   "has-a" relationship between this class and the design system)
private JComboBox<String>       cmbTopics;

// ── Constructor ───────────────────────────────────────────────────────

/**
* @param controller  Shared CourseController (carries the static course DB)
* @param courseID    ID of the course whose topic will receive the material
* @param activeUser  Authenticated user — forwarded to controller for role guard
*/
public MaterialUI(CourseController controller, String courseID, User activeUser) {
this.controller = controller;
this.courseID   = courseID;
this.activeUser = activeUser;
// ↑ DEPENDENCY (the constructor receives all three values from
//   whichever class opened this window — likely CourseDetailsUI —
//   and stores them for the lifetime of this dialog)
UITheme.applyGlobalDefaults();
initComponents();
}

// ── UI Construction ───────────────────────────────────────────────────

private void initComponents() {
setTitle("Upload Learning Material");
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setSize(560, 400);
setMinimumSize(new Dimension(480, 360));
setLocationRelativeTo(null);
setResizable(false);

JPanel root = new JPanel(new BorderLayout());
root.setBackground(UITheme.BG_DARK);
setContentPane(root);

// ── Header ───────────────────────────────────────────────────────
UITheme.GradientHeader hdr = new UITheme.GradientHeader(
"Upload Learning Material",
"Attach a file to a course topic");
// ↑ ASSOCIATION (instantiates a UITheme inner class — MaterialUI
//   depends on UITheme's component library to build its screen)
root.add(hdr, BorderLayout.NORTH);

// ── Form card ─────────────────────────────────────────────────────
UITheme.RoundPanel card = new UITheme.RoundPanel(16);
card.setLayout(new GridBagLayout());
card.setBorder(new EmptyBorder(28, 32, 28, 32));

GridBagConstraints g = new GridBagConstraints();
g.insets = new Insets(10, 8, 10, 8);
g.fill   = GridBagConstraints.HORIZONTAL;

// ── Topic dropdown ────────────────────────────────────────────────
Course course = controller.viewCourseContent(courseID);
// ↑ ASSOCIATION (MaterialUI calls a method on its CourseController
//   reference to retrieve a Course object — direct evidence of the
//   "uses-a" relationship declared in the field above)
String[] topicLabels;
if (course != null && !course.getTopicsList().isEmpty()) {
topicLabels = new String[course.getTopicsList().size()];
for (int i = 0; i < topicLabels.length; i++)
topicLabels[i] = (i + 1) + ".  " + course.getTopicsList().get(i).getTopicTitle();
// ↑ COMPOSITION (this line walks INTO the Course object and
//   reads its owned Topic list — MaterialUI is observing the
//   Course → Topic composition relationship from outside,
//   without owning either object itself)
} else {
topicLabels = new String[]{"(no topics — add one first)"};
}

cmbTopics = new UITheme.StyledComboBox(topicLabels);
cmbTopics.setPreferredSize(new Dimension(300, 40));
addRow(card, g, 0, "Topic", cmbTopics);

// ── File path + Browse ────────────────────────────────────────────
tfFilePath = new UITheme.StyledTextField("Click Browse to select a file...");
tfFilePath.setEditable(false);
tfFilePath.setPreferredSize(new Dimension(180, 40));

UITheme.StyledButton btnBrowse = new UITheme.StyledButton("Browse", UITheme.ACCENT_BLUE);
btnBrowse.setPreferredSize(new Dimension(100, 40));
btnBrowse.addActionListener(e -> browseFile());

JPanel fileRow = new JPanel(new BorderLayout(8, 0));
fileRow.setOpaque(false);
fileRow.add(tfFilePath, BorderLayout.CENTER);
fileRow.add(btnBrowse,  BorderLayout.EAST);
addRow(card, g, 1, "File", fileRow);

// ── Title field ───────────────────────────────────────────────────
tfTitle = new UITheme.StyledTextField("Display name for the file...");
tfTitle.setPreferredSize(new Dimension(300, 40));
addRow(card, g, 2, "Title", tfTitle);

// ── Format hint badges ────────────────────────────────────────────
JPanel hints = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
hints.setOpaque(false);
for (String ext : new String[]{"PDF", "PPTX", "MP4", "DOCX"})
hints.add(new UITheme.BadgeLabel(ext, new Color(35, 50, 90)));
JLabel limLbl = new JLabel("  Max 50 MB");
limLbl.setFont(UITheme.FONT_SMALL);
limLbl.setForeground(UITheme.TEXT_MUTED);
hints.add(limLbl);
g.gridx = 1; g.gridy = 3; g.weightx = 1;
card.add(hints, g);

// ── Confirm button ────────────────────────────────────────────────
UITheme.StyledButton btnUpload = new UITheme.StyledButton(
"Confirm Upload", UITheme.ACCENT_BLUE);
btnUpload.setPreferredSize(new Dimension(170, 42));
btnUpload.addActionListener(e -> clickConfirmUpload());
g.gridx = 1; g.gridy = 4; g.weightx = 0;
g.anchor = GridBagConstraints.EAST;
card.add(btnUpload, g);

// ── Card wrapper ──────────────────────────────────────────────────
JPanel cardWrap = new JPanel(new BorderLayout());
cardWrap.setOpaque(false);
cardWrap.setBorder(new EmptyBorder(20, 28, 20, 28));
cardWrap.add(card, BorderLayout.CENTER);
root.add(cardWrap, BorderLayout.CENTER);
}

// ── Layout helper ─────────────────────────────────────────────────────

private void addRow(JPanel p, GridBagConstraints g,
int row, String labelText, JComponent field) {
g.gridx = 0; g.gridy = row; g.weightx = 0;
g.anchor = GridBagConstraints.EAST;
JLabel lbl = new JLabel(labelText + "  ");
lbl.setFont(UITheme.FONT_LABEL);
lbl.setForeground(UITheme.TEXT_MUTED);
p.add(lbl, g);
g.gridx = 1; g.weightx = 1;
g.anchor = GridBagConstraints.WEST;
p.add(field, g);
}

// ── Actions ───────────────────────────────────────────────────────────

private void browseFile() {
JFileChooser fc = new JFileChooser();
fc.setDialogTitle("Select Learning Material File");
if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
File f = fc.getSelectedFile();
tfFilePath.setText(f.getAbsolutePath());
if (tfTitle.getText().trim().isEmpty())
tfTitle.setText(f.getName());
}
}

public void clickConfirmUpload() {
String path  = tfFilePath.getText().trim();
String title = tfTitle.getText().trim();
int    idx   = cmbTopics.getSelectedIndex();

if (path.isEmpty() || title.isEmpty()) {
JOptionPane.showMessageDialog(this,
"File path and title are both required.",
"Missing Input", JOptionPane.WARNING_MESSAGE);
return;
}

// Client-side size check (if file actually exists locally)
File f = new File(path);
if (f.exists() && f.length() > 50L * 1024 * 1024) {
showInvalidFileAlert();
return;
}

MaterialDTO dto = new MaterialDTO(path, title);
// ↑ DEPENDENCY (MaterialUI creates a temporary MaterialDTO purely to
//   pass form data onward — it does not keep this object around
//   after the method call below returns)
if (controller.uploadMaterial(courseID, dto, idx, activeUser)) {
// ↑ ASSOCIATION (MaterialUI calls a method on its
//   CourseController reference — this is the actual moment the
//   "uses-a" relationship is exercised)
// ↑ this call also forwards "activeUser" straight through to
//   the controller's role-guard check (isInstructor(actor)),
//   which is where the RUNTIME POLYMORPHISM (instanceof
//   Instructor) actually happens — MaterialUI itself doesn't
//   need to know or check the role; it just passes the User
//   object along and lets the controller decide
JOptionPane.showMessageDialog(this,
"Material uploaded successfully!",
"Upload Successful", JOptionPane.INFORMATION_MESSAGE);
dispose();
} else {
showInvalidFileAlert();
}
}

private void showInvalidFileAlert() {
JOptionPane.showMessageDialog(this,
"Invalid file type or size exceeded.\n" +
"Allowed: PDF, PPTX, MP4, DOCX  |  Max: 50 MB",
"Upload Rejected", JOptionPane.ERROR_MESSAGE);
tfFilePath.setText("");
}
}