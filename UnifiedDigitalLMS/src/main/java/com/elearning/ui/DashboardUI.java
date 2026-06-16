/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.AuthController;
import com.elearning.controllers.CourseController;
import com.elearning.controllers.EnrolmentController;
import com.elearning.controllers.UserController;
import com.elearning.entities.Instructor;
import com.elearning.entities.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.RadialGradientPaint;

/**
 * DashboardUI — Central hub after login.
 *
 * INSTRUCTOR cards: Manage Courses | Course Catalog | Student Progress
 * STUDENT    cards: My Enrolments  | Browse Catalog
 * Both roles: My Profile (footer) | Logout (footer)
 *
 * SESSION INJECTION: all controllers passed from LoginUI.
 */
public class DashboardUI extends JFrame {

    private final User                 activeUser;
    private final UserController       userController;
    private final AuthController       authController;
    private final CourseController     courseController;
    private final EnrolmentController  enrolmentController;

    private JPanel cardsPanel;

    public DashboardUI(User activeUser,
                       UserController userController,
                       AuthController authController,
                       CourseController courseController,
                       EnrolmentController enrolmentController) {
        this.activeUser          = activeUser;
        this.userController      = userController;
        this.authController      = authController;
        this.courseController    = courseController;
        this.enrolmentController = enrolmentController;
        UITheme.applyGlobalDefaults();
        initComponents();
    }

    private void initComponents() {
        setTitle("Dashboard — Unified Digital Learning Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel root = new RadialBgPanel();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        String roleLabel = (activeUser instanceof Instructor) ? "Instructor" : "Student";
        UITheme.GradientHeader header = new UITheme.GradientHeader(
                "Dashboard", "Welcome back, " + activeUser.getName() + "  ·  " + roleLabel);

        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        infoRow.setOpaque(false);
        infoRow.add(new UITheme.BadgeLabel("ID: " + activeUser.getUserId(), UITheme.ACCENT_BLUE));
        infoRow.add(new UITheme.BadgeLabel(roleLabel,
                activeUser instanceof Instructor ? UITheme.ACCENT_TEAL : UITheme.ACCENT_BLUE));
        infoRow.add(new UITheme.BadgeLabel("● " + activeUser.getStatus(), UITheme.SUCCESS_GREEN));

        JPanel topBlock = new JPanel(new BorderLayout());
        topBlock.setOpaque(false);
        topBlock.add(header,  BorderLayout.NORTH);
        topBlock.add(infoRow, BorderLayout.SOUTH);
        root.add(topBlock, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(1, 0, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(28, 40, 20, 40));
        buildRoleCards();
        root.add(cardsPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 40, 18, 40));

        UITheme.StyledButton btnProfile = new UITheme.StyledButton("My Profile", UITheme.ACCENT_TEAL);
        btnProfile.setPreferredSize(new Dimension(140, 38));
        btnProfile.addActionListener(this::openProfile);

        UITheme.StyledButton btnLogout = new UITheme.StyledButton("Logout", UITheme.DANGER_RED);
        btnLogout.setPreferredSize(new Dimension(110, 38));
        btnLogout.addActionListener(this::handleLogout);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnProfile);
        btnRow.add(btnLogout);

        JLabel lblFooter = new JLabel(
                "E-Learning Management System  ·  Session: " + activeUser.getUserId());
        lblFooter.setFont(UITheme.FONT_SMALL);
        lblFooter.setForeground(UITheme.TEXT_MUTED);

        footer.add(lblFooter, BorderLayout.WEST);
        footer.add(btnRow, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        // Size: 3 cards for instructor, 2 for student
        boolean isInstructor = activeUser instanceof Instructor;
        setSize(isInstructor ? 980 : 700, 520);
        setMinimumSize(new Dimension(isInstructor ? 800 : 580, 440));
        setLocationRelativeTo(null);
    }

    private void buildRoleCards() {
        cardsPanel.removeAll();
        if (activeUser instanceof Instructor) {
            cardsPanel.add(buildNavCard("📝", "Manage Courses",
                    "Create, edit, and delete courses in the system.",
                    UITheme.ACCENT_BLUE,   e -> openCourseManager()));
            cardsPanel.add(buildNavCard("📚", "Course Catalog",
                    "Browse, add topics and upload learning materials.",
                    UITheme.ACCENT_TEAL,   e -> openCourseCatalog()));
            cardsPanel.add(buildNavCard("📊", "Student Progress",
                    "Monitor enrolments and update student progress.",
                    new Color(160,100,240), e -> openInstructorDashboard()));
        } else {
            cardsPanel.add(buildNavCard("🎓", "My Enrolments",
                    "View your enrolled courses and progress.",
                    UITheme.SUCCESS_GREEN, e -> openStudentDashboard()));
            cardsPanel.add(buildNavCard("📚", "Browse Catalog",
                    "Explore all available courses and their content.",
                    UITheme.ACCENT_TEAL,  e -> openCourseCatalog()));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    // ── Navigation ────────────────────────────────────────────────
    private void openCourseManager() {
        new CourseUI(courseController, activeUser).setVisible(true);
    }
    private void openCourseCatalog() {
        new CourseCatalogUI(courseController, activeUser).setVisible(true);
    }
    private void openStudentDashboard() {
        new StudentDashboard(enrolmentController, courseController, activeUser).setVisible(true);
    }
    private void openInstructorDashboard() {
        new InstructorDashboard(enrolmentController, courseController, activeUser).setVisible(true);
    }
    private void openProfile(ActionEvent e) {
        new ProfileUI(activeUser.getUserId(), userController, authController).setVisible(true);
    }
    private void handleLogout(ActionEvent e) {
        authController.logout(activeUser.getUserId());
        JOptionPane.showMessageDialog(this,"You have been logged out.","Goodbye",
                JOptionPane.INFORMATION_MESSAGE);
        new LoginUI().setVisible(true);
        dispose();
    }

    // ── Nav-card builder ──────────────────────────────────────────
    private UITheme.RoundPanel buildNavCard(String icon, String title, String desc,
                                            Color accent, java.awt.event.ActionListener action) {
        UITheme.RoundPanel card = new UITheme.RoundPanel(18);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon, SwingConstants.LEFT);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_HEADING);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel(
                "<html><body style='width:140px;color:#7887AF'>" + desc + "</body></html>");
        lblDesc.setFont(UITheme.FONT_BODY);
        lblDesc.setBorder(BorderFactory.createEmptyBorder(4, 0, 14, 0));

        UITheme.StyledButton btn = new UITheme.StyledButton("Open  →", accent);
        btn.setPreferredSize(new Dimension(120, 38));
        btn.addActionListener(action);

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 4));
        top.setOpaque(false);
        top.add(lblIcon);
        top.add(lblTitle);

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(btn);

        card.add(top,     BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(btnWrap, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                        new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),120),2,true),
                    BorderFactory.createEmptyBorder(24, 24, 24, 24)));
                card.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(26,26,26,26));
                card.repaint();
            }
        });
        return card;
    }

    private static class RadialBgPanel extends JPanel {
        RadialBgPanel() { setOpaque(true); setBackground(UITheme.BG_DARK); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            RadialGradientPaint rp = new RadialGradientPaint(
                new Point2D.Float(0, 0), 520, new float[]{0f,1f},
                new Color[]{new Color(25,55,120,65), UITheme.BG_DARK});
            g2.setPaint(rp); g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose();
        }
    }
}