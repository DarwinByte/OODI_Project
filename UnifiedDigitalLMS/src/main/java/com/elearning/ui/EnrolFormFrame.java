/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.EnrolmentController;

import javax.swing.*;
import java.awt.*;

/**
 * UI: EnrolFormFrame
 * Course enrolment form — opened from StudentDashboard.
 * Shows student info (read-only), course dropdown (LIVE data), auto-filled date.
 * Repackaged from com.mycompany.digitallearningsystems to com.elearning.ui.
 * BORDER→BORDER_COLOR, DIVIDER→DIVIDER_COLOR updated.
 *
 * OOP PRINCIPLES APPLIED IN THIS CLASS:
 * ─────────────────────────────────────
 * 1. INHERITANCE      : EnrolFormFrame extends JFrame — it IS-A JFrame window.
 * Inherits all window behaviour (setTitle, setSize, dispose, etc.)
 * from the JFrame superclass without rewriting them.
 *
 * 2. ENCAPSULATION    : All fields (ctrl, studentID, studentName, owner, cmbCourse,
 * courseIDs) are private — UI state and dependencies are hidden
 * from outside classes.
 * Private methods (initComponents, confirm) keep implementation
 * details internal.
 *
 * 3. ABSTRACTION      : initComponents() hides all layout and form-building detail.
 * confirm() hides the enrolment submission logic — the button
 * listener simply calls confirm() without knowing what happens inside.
 *
 * 4. DEPENDENCY       : Depends on EnrolmentController to fetch available courses
 * and to submit the enrolment (ctrl.availableCourses(),
 * ctrl.enrolStudent()).
 * Depends on StudentDashboard (owner) to refresh the table
 * and restore visibility after enrolment or cancellation.
 *
 * 5. ASSOCIATION      : Holds a reference to StudentDashboard (owner) — a back-reference
 * to the window that opened this form, used to return control
 * after the form is closed (owner.refreshTable(), owner.setVisible).
 */
public class EnrolFormFrame extends JFrame {

// ENCAPSULATION: private fields — not directly accessible from outside
// DEPENDENCY: ctrl provides enrolment business logic
private final EnrolmentController        ctrl;
private final String                     studentID;
private final String                     studentName;

// ASSOCIATION: back-reference to the parent window (StudentDashboard)
//              used to refresh and restore the dashboard after this form closes
private final StudentDashboard           owner;

// ENCAPSULATION: private UI state — course combo box and course ID array
// FIX: Removed <String> generic
private       UITheme.StyledComboBox     cmbCourse;
private       String[]                   courseIDs;

// DEPENDENCY: constructor receives EnrolmentController and StudentDashboard
//             via injection — this form does not create them itself
public EnrolFormFrame(EnrolmentController ctrl, String studentID,
String studentName, StudentDashboard owner) {
this.ctrl        = ctrl;
this.studentID   = studentID;
this.studentName = studentName;
this.owner       = owner;
UITheme.applyGlobalDefaults();
initComponents();
}

// ENCAPSULATION: private method — all form layout is hidden from outside
// ABSTRACTION: constructor just calls initComponents() without knowing the detail
private void initComponents() {
setTitle("Enrol in Course");
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setSize(500, 360);
setResizable(false);
setLocationRelativeTo(null);
getContentPane().setBackground(UITheme.BG_DARK);
setLayout(new BorderLayout());

UITheme.GradientHeader header = new UITheme.GradientHeader(
"Enrol in a Course",
"Select a course from the list to enrol — " + studentName
);
add(header, BorderLayout.NORTH);

UITheme.RoundPanel card = new UITheme.RoundPanel(18);
card.setLayout(new GridBagLayout());
card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

JPanel wrap = new JPanel(new BorderLayout());
wrap.setOpaque(false);
wrap.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
wrap.add(card);
add(wrap, BorderLayout.CENTER);

GridBagConstraints gc = new GridBagConstraints();
gc.insets = new Insets(8, 8, 8, 8);
gc.fill   = GridBagConstraints.HORIZONTAL;

// Student (read-only)
gc.gridx=0; gc.gridy=0; gc.weightx=0; gc.gridwidth=1; gc.anchor=GridBagConstraints.EAST;
JLabel lStu = new JLabel("Student");
lStu.setFont(UITheme.FONT_LABEL); lStu.setForeground(UITheme.TEXT_MUTED);
card.add(lStu, gc);
gc.gridx=1; gc.weightx=1; gc.anchor=GridBagConstraints.WEST;
JLabel valStu = new JLabel(studentName + "  (" + studentID + ")");
valStu.setFont(UITheme.FONT_BODY); valStu.setForeground(UITheme.TEXT_PRIMARY);
card.add(valStu, gc);

// Course picker (LIVE data)
gc.gridx=0; gc.gridy=1; gc.weightx=0; gc.anchor=GridBagConstraints.EAST;
JLabel lCourse = new JLabel("Course");
lCourse.setFont(UITheme.FONT_LABEL); lCourse.setForeground(UITheme.TEXT_MUTED);
card.add(lCourse, gc);
gc.gridx=1; gc.weightx=1; gc.anchor=GridBagConstraints.WEST;

// DEPENDENCY: calls ctrl.availableCourses() to populate dropdown from live data
String[][] courses = ctrl.availableCourses();
courseIDs = new String[courses.length];
String[] courseNames = new String[courses.length];
for (int i = 0; i < courses.length; i++) {
courseIDs[i]   = courses[i][0];
courseNames[i] = courses[i][1];
}

// FIX: Removed <> generic
cmbCourse = new UITheme.StyledComboBox(courseNames);
cmbCourse.setPreferredSize(new Dimension(260, 40));
card.add(cmbCourse, gc);

// Date (auto)
gc.gridx=0; gc.gridy=2; gc.weightx=0; gc.anchor=GridBagConstraints.EAST;
JLabel lDate = new JLabel("Date");
lDate.setFont(UITheme.FONT_LABEL); lDate.setForeground(UITheme.TEXT_MUTED);
card.add(lDate, gc);
gc.gridx=1; gc.weightx=1; gc.anchor=GridBagConstraints.WEST;
JLabel valDate = new JLabel(java.time.LocalDate.now().toString());
valDate.setFont(UITheme.FONT_BODY); valDate.setForeground(UITheme.TEXT_MUTED);
card.add(valDate, gc);

// Separator
gc.gridx=0; gc.gridy=3; gc.gridwidth=2; gc.insets=new Insets(16,0,16,0);
JSeparator sep = new JSeparator();
sep.setForeground(UITheme.DIVIDER_COLOR);
sep.setBackground(UITheme.DIVIDER_COLOR);
card.add(sep, gc);

// Buttons
gc.gridy=4; gc.insets=new Insets(0,8,8,8);
UITheme.StyledButton btnConfirm = new UITheme.StyledButton("Confirm Enrolment", UITheme.SUCCESS_GREEN);
UITheme.StyledButton btnCancel  = new UITheme.StyledButton("Cancel",             UITheme.BG_ELEVATED);
btnConfirm.setPreferredSize(new Dimension(180, 40));
btnCancel.setPreferredSize(new Dimension(100, 40));

// ABSTRACTION: listener delegates to confirm() — detail is hidden
btnConfirm.addActionListener(e -> confirm());

// ASSOCIATION: Cancel restores the owner (StudentDashboard) window
btnCancel.addActionListener(e  -> { dispose(); owner.setVisible(true); });

JPanel bRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
bRow.setOpaque(false);
bRow.add(btnCancel);
bRow.add(btnConfirm);
card.add(bRow, gc);
}

// ENCAPSULATION: private method — enrolment submission detail is hidden from outside
// ABSTRACTION: button listener just calls confirm() without knowing what happens inside
// DEPENDENCY: calls ctrl.enrolStudent() to perform the enrolment
// ASSOCIATION: calls owner.refreshTable() and owner.setVisible() to return control
//              back to StudentDashboard after a successful enrolment
private void confirm() {
int    idx        = cmbCourse.getSelectedIndex();
String courseID   = courseIDs[idx];
String courseName = (String) cmbCourse.getSelectedItem();
String date       = java.time.LocalDate.now().toString();

if (!ctrl.enrolStudent(studentID, studentName, courseID, courseName, date)) {
JOptionPane.showMessageDialog(this, ctrl.getLastError(),
"Enrolment Failed", JOptionPane.ERROR_MESSAGE);
return;
}
JOptionPane.showMessageDialog(this,
"Successfully enrolled in " + courseName + "!",
"Enrolled", JOptionPane.INFORMATION_MESSAGE);
dispose();
owner.refreshTable();
owner.setVisible(true);
}
}