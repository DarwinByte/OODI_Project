/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.CourseController;
import com.elearning.controllers.EnrolmentController;
import com.elearning.entities.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import com.elearning.entities.Enrolment;
import com.elearning.entities.Progress;
import java.util.List;

/**
 * UI: StudentDashboard
 * Shows the logged-in student's enrolled courses with progress bars.
 * Opened from DashboardUI when Student clicks "My Enrolments".
 *
 * SESSION INJECTION: receives EnrolmentController + CourseController + User from DashboardUI.
 * ECOSYSTEM: "Browse Catalog" button opens CourseCatalogUI (Module 2) directly.
 *
 * OOP PRINCIPLES APPLIED IN THIS CLASS:
 * ─────────────────────────────────────
 * 1. INHERITANCE      : StudentDashboard extends JFrame — it IS-A JFrame window.
 * Inherits all window behaviour (setTitle, setSize, dispose, etc.)
 * from the JFrame superclass without rewriting them.
 *
 * 2. ENCAPSULATION    : All fields (ctrl, courseCtrl, activeUser, tableModel, tblEnrolments)
 * are private — UI internals are hidden from outside classes.
 * Private methods (initComponents, dropSelected, openEnrolForm,
 * styleTable) keep implementation details internal.
 *
 * 3. ABSTRACTION      : refreshTable(), dropSelected(), openEnrolForm(), and styleTable()
 * each hide their internal complexity behind a single method call.
 * The constructor simply calls initComponents() and refreshTable()
 * without exposing how the UI is built.
 *
 * 4. DEPENDENCY       : Depends on EnrolmentController for enrolment/progress data.
 * Depends on CourseController to open CourseCatalogUI.
 * Depends on User entity to get the logged-in student's details.
 * Depends on Enrolment and Progress entities to populate the table.
 * Depends on EnrolFormFrame (your module) to open the enrolment form.
 * Depends on CourseCatalogUI (Module 2) for the Browse Catalog button.
 *
 * 5. POLYMORPHISM     : Anonymous inner class overrides isCellEditable() in JTable
 * to make all cells non-editable.
 * Anonymous inner class overrides isCellEditable() in DefaultTableModel.
 * Anonymous inner class overrides getTableCellRendererComponent() in
 * DefaultTableCellRenderer — custom progress bar and row styling
 * replace the default cell rendering behaviour.
 */
public class StudentDashboard extends JFrame {

// ENCAPSULATION: private fields — not directly accessible from outside
// DEPENDENCY: holds references to EnrolmentController, CourseController, and User
private final EnrolmentController ctrl;
private final CourseController    courseCtrl;
private final User                activeUser;

// ENCAPSULATION: private UI state fields
private DefaultTableModel tableModel;
private JTable            tblEnrolments;

// DEPENDENCY: constructor receives all required controllers and user via injection
//             (passed in from DashboardUI — session injection pattern)
public StudentDashboard(EnrolmentController ctrl, CourseController courseCtrl, User activeUser) {
this.ctrl       = ctrl;
this.courseCtrl = courseCtrl;
this.activeUser = activeUser;
UITheme.applyGlobalDefaults();
initComponents();
refreshTable();
}

// ENCAPSULATION: private method — UI construction detail hidden from outside
// ABSTRACTION: all layout and component setup is contained here
private void initComponents() {
setTitle("Student Dashboard — " + activeUser.getName());
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setSize(860, 580);
setMinimumSize(new Dimension(700, 480));
setLocationRelativeTo(null);
getContentPane().setBackground(UITheme.BG_DARK);
setLayout(new BorderLayout());

// ── Header ───────────────────────────────────────────────
UITheme.GradientHeader header = new UITheme.GradientHeader(
"My Enrolments",
"Welcome, " + activeUser.getName()
+ "  |  ID: " + activeUser.getUserId()
+ "  |  View & manage your courses"
);
add(header, BorderLayout.NORTH);

// ── Card ─────────────────────────────────────────────────
UITheme.RoundPanel card = new UITheme.RoundPanel(18);
card.setLayout(new BorderLayout(0, 14));
card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

JPanel wrap = new JPanel(new BorderLayout());
wrap.setOpaque(false);
wrap.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
wrap.add(card);
add(wrap, BorderLayout.CENTER);

// Top row: label + Logout
JPanel topRow = new JPanel(new BorderLayout(12, 0));
topRow.setOpaque(false);
JLabel lbl = new JLabel("Enrolled Courses");
lbl.setFont(UITheme.FONT_HEADING);
lbl.setForeground(UITheme.TEXT_PRIMARY);

UITheme.StyledButton btnLogout = new UITheme.StyledButton("Logout", UITheme.BG_ELEVATED);
btnLogout.setPreferredSize(new Dimension(100, 36));
btnLogout.addActionListener(e -> dispose());

topRow.add(lbl, BorderLayout.WEST);
topRow.add(btnLogout, BorderLayout.EAST);
card.add(topRow, BorderLayout.NORTH);

// POLYMORPHISM: anonymous class overrides isCellEditable() — table cells are read-only
tblEnrolments = new JTable() {
@Override public boolean isCellEditable(int r, int c) { return false; }
};
styleTable(tblEnrolments);

// POLYMORPHISM: anonymous class overrides isCellEditable() on the model as well
tableModel = new DefaultTableModel(
new String[]{"Enrol ID", "Course", "Enrolled Date", "Status", "Progress"}, 0
) { @Override public boolean isCellEditable(int r, int c) { return false; } };
tblEnrolments.setModel(tableModel);

int[] widths = {100, 220, 120, 100, 120};
for (int i = 0; i < widths.length; i++)
tblEnrolments.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

// POLYMORPHISM: anonymous class overrides getTableCellRendererComponent()
//               to render column 4 as a JProgressBar instead of plain text
tblEnrolments.getColumnModel().getColumn(4).setCellRenderer(
new DefaultTableCellRenderer() {
@Override public Component getTableCellRendererComponent(
JTable t, Object v, boolean sel, boolean foc, int row, int col) {
String val = v == null ? "0%" : v.toString();
int pct = 0;
try { pct = Integer.parseInt(val.replace("%", "")); } catch (Exception ig) {}
JPanel p = new JPanel(new BorderLayout(6, 0));
p.setBackground(sel ? new Color(82,153,255,60)
: (row%2==0 ? UITheme.BG_PANEL : UITheme.BG_ELEVATED));
p.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
JProgressBar pb = new JProgressBar(0, 100);
pb.setValue(pct);
pb.setStringPainted(true);
pb.setString(pct + "%");
pb.setFont(UITheme.FONT_SMALL);
pb.setForeground(pct == 100 ? UITheme.SUCCESS_GREEN : UITheme.ACCENT_BLUE);
pb.setBackground(UITheme.BG_ELEVATED);
pb.setBorderPainted(false);
p.add(pb, BorderLayout.CENTER);
return p;
}
});

JScrollPane sp = new JScrollPane(tblEnrolments);
UITheme.styleScrollPane(sp);
sp.setBorder(BorderFactory.createMatteBorder(1,1,1,1, UITheme.BORDER_COLOR));
card.add(sp, BorderLayout.CENTER);

// Buttons: Browse Catalog | Drop Course | + Enrol in Course
JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
btnRow.setOpaque(false);

UITheme.StyledButton btnCatalog = new UITheme.StyledButton("Browse Catalog", UITheme.ACCENT_TEAL);
UITheme.StyledButton btnDrop    = new UITheme.StyledButton("Drop Course",    UITheme.DANGER_RED);
UITheme.StyledButton btnEnrol   = new UITheme.StyledButton("+ Enrol in Course", UITheme.SUCCESS_GREEN);

btnCatalog.setPreferredSize(new Dimension(155, 40));
btnDrop.setPreferredSize(new Dimension(130, 40));
btnEnrol.setPreferredSize(new Dimension(165, 40));

// DEPENDENCY: opens CourseCatalogUI (Module 2) — inter-module dependency
btnCatalog.addActionListener(e ->
new CourseCatalogUI(courseCtrl, activeUser).setVisible(true));
btnDrop.addActionListener(e  -> dropSelected());
btnEnrol.addActionListener(e -> openEnrolForm());

btnRow.add(btnCatalog);
btnRow.add(btnDrop);
btnRow.add(btnEnrol);
card.add(btnRow, BorderLayout.SOUTH);
}

// ABSTRACTION: hides table-population logic — called after any data change
// DEPENDENCY: calls EnrolmentController and reads Enrolment + Progress entities
public void refreshTable() {
tableModel.setRowCount(0);
try {
List<Enrolment> enrolments = ctrl.viewEnrolmentStatus(activeUser.getUserId());
if (enrolments != null) {
for (Enrolment e : enrolments) {
Progress p = ctrl.getProgress(activeUser.getUserId(), e.getCourseID());
int pct = (p != null) ? p.getCompletionPct() : 0;
tableModel.addRow(new Object[]{
e.getEnrolID(), e.getCourseName(), e.getEnrolDate(), e.getStatus(), pct + "%"
});
}
}
} catch (Exception ex) {
System.out.println("Data fetch error: " + ex.getMessage());
}
}

// ENCAPSULATION: private method — drop logic is hidden inside the dashboard
// ABSTRACTION: UI delegates the actual drop operation to EnrolmentController
// DEPENDENCY: calls ctrl.dropCourse() — depends on EnrolmentController
private void dropSelected() {
int row = tblEnrolments.getSelectedRow();
if (row == -1) {
JOptionPane.showMessageDialog(this, "Select a course to drop.",
"No Selection", JOptionPane.WARNING_MESSAGE); return;
}
String enrolID    = (String) tableModel.getValueAt(row, 0);
String courseName = (String) tableModel.getValueAt(row, 1);
int confirm = JOptionPane.showConfirmDialog(this,
"Drop \"" + courseName + "\"?", "Confirm Drop", JOptionPane.YES_NO_OPTION);
if (confirm == JOptionPane.YES_OPTION) {
if (!ctrl.dropCourse(enrolID))
JOptionPane.showMessageDialog(this, ctrl.getLastError(),
"Cannot Drop", JOptionPane.ERROR_MESSAGE);
else refreshTable();
}
}

// ENCAPSULATION: private method — navigation logic is hidden inside the dashboard
// DEPENDENCY: creates EnrolFormFrame (your module) — passes 'this' so the form
//             can call refreshTable() on return
private void openEnrolForm() {
new EnrolFormFrame(ctrl, activeUser.getUserId(),
activeUser.getName(), this).setVisible(true);
setVisible(false);
}

// ENCAPSULATION: private method — table styling detail is hidden from initComponents
// POLYMORPHISM: anonymous class overrides getTableCellRendererComponent()
//               to apply custom row colours and status-based text colours
private void styleTable(JTable t) {
t.setFont(UITheme.FONT_BODY);
t.setForeground(UITheme.TEXT_PRIMARY);
t.setBackground(UITheme.BG_PANEL);
t.setRowHeight(38);
t.setShowGrid(false);
t.setIntercellSpacing(new Dimension(0, 0));
t.setSelectionBackground(new Color(82, 153, 255, 60));
t.setSelectionForeground(UITheme.TEXT_PRIMARY);
JTableHeader h = t.getTableHeader();
h.setFont(UITheme.FONT_LABEL);
h.setBackground(UITheme.BG_ELEVATED);
h.setForeground(UITheme.TEXT_MUTED);
h.setPreferredSize(new Dimension(0, 38));
h.setBorder(BorderFactory.createMatteBorder(0,0,1,0, UITheme.DIVIDER_COLOR));
h.setReorderingAllowed(false);
t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
@Override public Component getTableCellRendererComponent(JTable tbl, Object v,
boolean sel, boolean foc, int row, int col) {
super.getTableCellRendererComponent(tbl, v, sel, foc, row, col);
setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
setFont(UITheme.FONT_BODY);
if (col == 3 && v != null) {
switch (v.toString()) {
case "Active":    setForeground(UITheme.SUCCESS_GREEN);  break;
case "Completed": setForeground(UITheme.ACCENT_BLUE);    break;
case "Dropped":   setForeground(UITheme.DANGER_RED);     break;
case "Pending":   setForeground(UITheme.WARNING_YELLOW); break;
default:          setForeground(UITheme.TEXT_PRIMARY);
}
} else { setForeground(UITheme.TEXT_PRIMARY); }
setBackground(!sel ? (row%2==0 ? UITheme.BG_PANEL : UITheme.BG_ELEVATED)
: new Color(82,153,255,60));
return this;
}
});
}
}