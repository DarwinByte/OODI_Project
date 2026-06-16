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
import java.util.List;

/**
 * UI: InstructorDashboard  (Student Progress Monitor)
 * Opened from DashboardUI when Instructor clicks "Student Progress".
 *
 * Matches screenshots exactly:
 * Title bar  : "Instructor Dashboard — <Name>"
 * Header     : "Student Progress Monitor"
 * Course drop: populated from LIVE CourseController data
 * Table      : Enrol ID | Student ID | Student Name | Status |
 * Topics Done | Progress
 * Progress   : colour-coded badge (green=100%, blue=<100%)
 * Buttons    : Remove Student (red) | Update Progress (blue)
 * Logout     : top-right inside card
 *
 * SESSION INJECTION: receives all controllers + User from DashboardUI.
 *
 * OOP PRINCIPLES APPLIED IN THIS CLASS:
 * ─────────────────────────────────────
 * 1. INHERITANCE      : InstructorDashboard extends JFrame — it IS-A JFrame window.
 * Inherits all window behaviour (setTitle, setSize, dispose, etc.)
 * from the JFrame superclass without rewriting them.
 *
 * 2. ENCAPSULATION    : All fields (ctrl, courseCtrl, activeUser, tableModel,
 * tblProgress, cmbCourse, courseIDs) are private — internal
 * state and dependencies are hidden from outside classes.
 * Private methods (initComponents, removeSelected, updateSelected,
 * styleTable) keep implementation details internal.
 *
 * 3. ABSTRACTION      : initComponents() hides all layout and component setup.
 * refreshTable(), removeSelected(), updateSelected(), and
 * styleTable() each hide their complexity behind a single call.
 * Button listeners simply call these methods without knowing
 * what happens inside.
 *
 * 4. DEPENDENCY       : Depends on EnrolmentController to fetch progress data,
 * remove students, and update progress
 * (ctrl.availableCourses(), ctrl.monitorStudentProgress(),
 * ctrl.removeStudentFromCourse(), ctrl.updateProgress()).
 * Depends on CourseController (courseCtrl) — held for potential
 * future use and passed in via session injection.
 * Depends on User entity to display the instructor's name and ID.
 *
 * 5. POLYMORPHISM     : Anonymous inner class overrides isCellEditable() in JTable
 * to make all cells non-editable.
 * Anonymous inner class overrides isCellEditable() in DefaultTableModel.
 * Anonymous inner class overrides getTableCellRendererComponent() in
 * DefaultTableCellRenderer — renders column 5 as a colour-coded
 * rounded badge instead of plain text.
 * Anonymous inner class overrides paintComponent() in JLabel
 * to draw the rounded badge background with Graphics2D.
 * Anonymous inner class overrides getTableCellRendererComponent()
 * in styleTable() for custom row colours and status-based text colours.
 */
public class InstructorDashboard extends JFrame {

// ENCAPSULATION: private fields — not directly accessible from outside
// DEPENDENCY: holds references to EnrolmentController, CourseController, and User
private final EnrolmentController ctrl;
private final CourseController    courseCtrl;
private final User                activeUser;

// ENCAPSULATION: private UI state fields
private DefaultTableModel         tableModel;
private JTable                    tblProgress;

// ENCAPSULATION: private combo box and course ID array for course selection
// FIX: Removed <String> generic
private UITheme.StyledComboBox    cmbCourse;
private String[]                  courseIDs;

// DEPENDENCY: constructor receives all controllers and User via session injection
//             (passed in from DashboardUI — not created here)
public InstructorDashboard(EnrolmentController ctrl, CourseController courseCtrl, User activeUser) {
this.ctrl       = ctrl;
this.courseCtrl = courseCtrl;
this.activeUser = activeUser;
UITheme.applyGlobalDefaults();
initComponents();
}

// ENCAPSULATION: private method — all UI construction is hidden from outside
// ABSTRACTION: constructor just calls initComponents() without knowing the detail
private void initComponents() {
setTitle("Instructor Dashboard — " + activeUser.getName());
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setSize(880, 580);
setMinimumSize(new Dimension(720, 480));
setLocationRelativeTo(null);
getContentPane().setBackground(UITheme.BG_DARK);
setLayout(new BorderLayout());

// ── Header ───────────────────────────────────────────────
UITheme.GradientHeader header = new UITheme.GradientHeader(
"Student Progress Monitor",
"Instructor: " + activeUser.getName()
+ "  |  ID: " + activeUser.getUserId()
+ "  |  Monitor & update student progress"
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

// ── Top row: Course selector + Logout ────────────────────
JPanel topRow = new JPanel(new BorderLayout(12, 0));
topRow.setOpaque(false);

JPanel courseRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
courseRow.setOpaque(false);
JLabel lCourse = new JLabel("Course:");
lCourse.setFont(UITheme.FONT_LABEL);
lCourse.setForeground(UITheme.TEXT_MUTED);

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
cmbCourse.setPreferredSize(new Dimension(220, 38));

// ABSTRACTION: listener delegates to refreshTable() — detail is hidden
cmbCourse.addActionListener(e -> refreshTable());

courseRow.add(lCourse);
courseRow.add(cmbCourse);

UITheme.StyledButton btnLogout = new UITheme.StyledButton("Logout", UITheme.BG_ELEVATED);
btnLogout.setPreferredSize(new Dimension(100, 36));
btnLogout.addActionListener(e -> dispose());

topRow.add(courseRow, BorderLayout.WEST);
topRow.add(btnLogout, BorderLayout.EAST);
card.add(topRow, BorderLayout.NORTH);

// ── Table ─────────────────────────────────────────────────
// POLYMORPHISM: anonymous class overrides isCellEditable() — all cells read-only
tblProgress = new JTable() {
@Override public boolean isCellEditable(int r, int c) { return false; }
};
styleTable(tblProgress);

// POLYMORPHISM: anonymous class overrides isCellEditable() on the model as well
tableModel = new DefaultTableModel(
new String[]{"Enrol ID","Student ID","Student Name","Status","Topics Done","Progress"}, 0
) { @Override public boolean isCellEditable(int r, int c) { return false; } };
tblProgress.setModel(tableModel);

int[] widths = {90, 90, 160, 90, 100, 120};
for (int i = 0; i < widths.length; i++)
tblProgress.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

// POLYMORPHISM: anonymous class overrides getTableCellRendererComponent()
//               to render column 5 as a colour-coded rounded badge
tblProgress.getColumnModel().getColumn(5).setCellRenderer(
new DefaultTableCellRenderer() {
@Override public Component getTableCellRendererComponent(
JTable t, Object v, boolean sel, boolean foc, int row, int col) {
String val = v == null ? "0%" : v.toString();
int tempPct = 0;
try { tempPct = Integer.parseInt(val.replace("%","")); } catch (Exception ig) {}
final int pct = tempPct;
JPanel p = new JPanel(new BorderLayout(4, 0));
p.setBackground(sel ? new Color(82,153,255,60)
: (row%2==0 ? UITheme.BG_PANEL : UITheme.BG_ELEVATED));
p.setBorder(BorderFactory.createEmptyBorder(5,14,5,14));
// POLYMORPHISM: anonymous class overrides paintComponent() in JLabel
//               to draw a rounded rectangle badge background with Graphics2D
JLabel badge = new JLabel(pct + "%") {
@Override protected void paintComponent(Graphics g) {
Graphics2D g2 = (Graphics2D) g.create();
g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
RenderingHints.VALUE_ANTIALIAS_ON);
Color bg = pct == 100 ? UITheme.SUCCESS_GREEN : UITheme.ACCENT_BLUE;
g2.setColor(bg);
g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
g2.dispose();
super.paintComponent(g);
}
};
badge.setFont(UITheme.FONT_SMALL);
badge.setForeground(Color.WHITE);
badge.setHorizontalAlignment(SwingConstants.CENTER);
badge.setOpaque(false);
badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
badge.setPreferredSize(new Dimension(60, 24));
p.add(badge, BorderLayout.CENTER);
return p;
}
});

JScrollPane sp = new JScrollPane(tblProgress);
UITheme.styleScrollPane(sp);
sp.setBorder(BorderFactory.createMatteBorder(1,1,1,1, UITheme.BORDER_COLOR));
card.add(sp, BorderLayout.CENTER);

// ── Buttons: Remove Student | Update Progress ─────────────
JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
btnRow.setOpaque(false);

UITheme.StyledButton btnRemove = new UITheme.StyledButton("Remove Student",  UITheme.DANGER_RED);
UITheme.StyledButton btnUpdate = new UITheme.StyledButton("Update Progress", UITheme.ACCENT_BLUE);
btnRemove.setPreferredSize(new Dimension(160, 40));
btnUpdate.setPreferredSize(new Dimension(170, 40));

// ABSTRACTION: listeners delegate to private methods — detail is hidden
btnRemove.addActionListener(e -> removeSelected());
btnUpdate.addActionListener(e -> updateSelected());

btnRow.add(btnRemove);
btnRow.add(btnUpdate);
card.add(btnRow, BorderLayout.SOUTH);

// Load initial data
if (courseNames.length > 0) refreshTable();
}

// ABSTRACTION: hides table-population logic — called whenever the selected course changes
// DEPENDENCY: calls ctrl.monitorStudentProgress() to fetch rows from EnrolmentController
public void refreshTable() {
tableModel.setRowCount(0);
int idx = cmbCourse.getSelectedIndex();
if (idx < 0 || idx >= courseIDs.length) return;
String courseID = courseIDs[idx];
for (Object[] row : ctrl.monitorStudentProgress(courseID)) {
tableModel.addRow(row);
}
}

// ENCAPSULATION: private method — remove logic is hidden inside the dashboard
// ABSTRACTION: button listener just calls removeSelected() without knowing the detail
// DEPENDENCY: calls ctrl.removeStudentFromCourse() — delegates to EnrolmentController
private void removeSelected() {
int row = tblProgress.getSelectedRow();
if (row == -1) {
JOptionPane.showMessageDialog(this,"Select a student to remove.",
"No Selection", JOptionPane.WARNING_MESSAGE); return;
}
String enrolID  = (String) tableModel.getValueAt(row, 0);
String stuName  = (String) tableModel.getValueAt(row, 2);
int confirm = JOptionPane.showConfirmDialog(this,
"Remove " + stuName + " from this course?",
"Confirm Remove", JOptionPane.YES_NO_OPTION);
if (confirm == JOptionPane.YES_OPTION) {
if (!ctrl.removeStudentFromCourse(enrolID))
JOptionPane.showMessageDialog(this, ctrl.getLastError(),
"Error", JOptionPane.ERROR_MESSAGE);
else refreshTable();
}
}

// ENCAPSULATION: private method — update logic is hidden inside the dashboard
// ABSTRACTION: button listener just calls updateSelected() without knowing the detail
// DEPENDENCY: calls ctrl.updateProgress() — delegates to EnrolmentController
private void updateSelected() {
int row = tblProgress.getSelectedRow();
if (row == -1) {
JOptionPane.showMessageDialog(this,"Select a student to update.",
"No Selection", JOptionPane.WARNING_MESSAGE); return;
}
String enrolID  = (String) tableModel.getValueAt(row, 0);
String stuID    = (String) tableModel.getValueAt(row, 1);
String stuName  = (String) tableModel.getValueAt(row, 2);
String topicStr = (String) tableModel.getValueAt(row, 4); // e.g. "5/10"
int    current  = 0;
int    total    = 10;
try {
String[] parts = topicStr.split("/");
current = Integer.parseInt(parts[0].trim());
total   = Integer.parseInt(parts[1].trim());
} catch (Exception ignored) {}

int    idx      = cmbCourse.getSelectedIndex();
String courseID = courseIDs[idx];

String input = (String) JOptionPane.showInputDialog(this,
"Enter completed topics for " + stuName + "\n(current: " + current + "/" + total + ")",
"Update Progress", JOptionPane.PLAIN_MESSAGE, null, null, current);

if (input != null) {
try {
int val = Integer.parseInt(input.trim());
if (!ctrl.updateProgress(stuID, courseID, val))
JOptionPane.showMessageDialog(this, ctrl.getLastError(),
"Error", JOptionPane.ERROR_MESSAGE);
else {
JOptionPane.showMessageDialog(this, stuName + "'s progress updated!",
"Done", JOptionPane.INFORMATION_MESSAGE);
refreshTable();
}
} catch (NumberFormatException ex) {
JOptionPane.showMessageDialog(this,"Please enter a valid number.",
"Invalid", JOptionPane.ERROR_MESSAGE);
}
}
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
t.setSelectionBackground(new Color(82,153,255,60));
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
setBorder(BorderFactory.createEmptyBorder(0,14,0,14));
setFont(UITheme.FONT_BODY);
if (col == 3 && v != null) {
switch (v.toString()) {
case "Active":    setForeground(UITheme.SUCCESS_GREEN);  break;
case "Completed": setForeground(UITheme.ACCENT_BLUE);    break;
case "Dropped":   setForeground(UITheme.DANGER_RED);     break;
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