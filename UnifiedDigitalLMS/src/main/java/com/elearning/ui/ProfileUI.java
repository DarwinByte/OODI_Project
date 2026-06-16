/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.AuthController;
import com.elearning.controllers.UserController;
import com.elearning.dtos.ProfileDTO;
import com.elearning.entities.Instructor;
import com.elearning.entities.Student;
import com.elearning.entities.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UI: ProfileUI
 * Reskinned to match team dark theme (UITheme).
 * Layout preserved: Session ID search, Name, Email (locked), User ID (locked),
 * Status dropdown, role-specific info area, Edit Profile / Save Profile, Logout.
 */
public class ProfileUI extends JFrame {

    private final UserController userController;
    private final AuthController authController;
    private String currentUserId;
    private boolean isEditing = false;

    // ── UI Components ──────────────────────────────────────────────────────
    private UITheme.StyledTextField  txtSessionId;
    private UITheme.StyledButton     btnSearch;

    private UITheme.StyledTextField  txtName;
    private JTextField               txtEmail;      // locked — plain JTextField
    private JTextField               txtUserId;     // locked — plain JTextField
    private UITheme.StyledComboBox   cmbStatus;
    private JTextArea                txtRoleArea;

    private UITheme.StyledButton     btnEditSave;
    private UITheme.StyledButton     btnLogout;

    // ── Role badge ─────────────────────────────────────────────────────────
    private UITheme.BadgeLabel       roleBadge;

    public ProfileUI(String loggedInUserId,
                     UserController userController,
                     AuthController authController) {
        this.userController = userController;
        this.authController = authController;
        this.currentUserId  = loggedInUserId;
        UITheme.applyGlobalDefaults();
        initComponents();
        if (loggedInUserId != null && !loggedInUserId.isEmpty()) {
            txtSessionId.setText(loggedInUserId);
            loadProfile(loggedInUserId);
        }
    }

    private void initComponents() {
        setTitle("My Profile — Unified Digital Learning Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 640);
        setMinimumSize(new Dimension(460, 560));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // ── Gradient header ────────────────────────────────────────────────
        UITheme.GradientHeader header = new UITheme.GradientHeader(
                "My Profile",
                "View and manage your account details");
        add(header, BorderLayout.NORTH);

        // ── Centre wrapper ─────────────────────────────────────────────────
        JPanel centre = new JPanel(new BorderLayout(0, 12));
        centre.setBackground(UITheme.BG_DARK);
        centre.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        // ── Search bar (Session ID) ────────────────────────────────────────
        UITheme.RoundPanel searchCard = new UITheme.RoundPanel(14, UITheme.BG_ELEVATED);
        searchCard.setLayout(new GridBagLayout());
        searchCard.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(0, 6, 0, 6);
        sgbc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel lblSessionLbl = new JLabel("Session ID");
        lblSessionLbl.setFont(UITheme.FONT_LABEL);
        lblSessionLbl.setForeground(UITheme.TEXT_MUTED);
        sgbc.gridx = 0; sgbc.weightx = 0;
        searchCard.add(lblSessionLbl, sgbc);

        txtSessionId = new UITheme.StyledTextField("Enter User ID to search");
        sgbc.gridx = 1; sgbc.weightx = 1.0;
        searchCard.add(txtSessionId, sgbc);

        btnSearch = new UITheme.StyledButton("Search", UITheme.ACCENT_BLUE);
        btnSearch.setPreferredSize(new Dimension(100, 40));
        btnSearch.addActionListener(e -> loadProfile(txtSessionId.getText().trim()));
        sgbc.gridx = 2; sgbc.weightx = 0;
        searchCard.add(btnSearch, sgbc);

        centre.add(searchCard, BorderLayout.NORTH);

        // ── Main profile card ──────────────────────────────────────────────
        UITheme.RoundPanel card = new UITheme.RoundPanel(18);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ── Profile heading row with role badge ────────────────────────────
        JPanel headRow = new JPanel(new BorderLayout(10, 0));
        headRow.setOpaque(false);
        JLabel lblMyProfile = new JLabel("Account Details");
        lblMyProfile.setFont(UITheme.FONT_HEADING);
        lblMyProfile.setForeground(UITheme.TEXT_PRIMARY);
        headRow.add(lblMyProfile, BorderLayout.WEST);
        roleBadge = new UITheme.BadgeLabel("—", UITheme.ACCENT_BLUE);
        headRow.add(roleBadge, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(headRow, gbc);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.DIVIDER_COLOR);
        gbc.gridy = 1; gbc.insets = new Insets(2, 8, 12, 8);
        card.add(sep, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);

        // ── Name ───────────────────────────────────────────────────────────
        addRow(card, gbc, "Name", 2);
        txtName = new UITheme.StyledTextField("—");
        txtName.setEditable(false);
        addField(card, gbc, txtName, 2);

        // ── Email (locked) ─────────────────────────────────────────────────
        addRow(card, gbc, "Email", 3);
        txtEmail = makeLocked();
        addField(card, gbc, txtEmail, 3);

        // ── User ID (locked) ───────────────────────────────────────────────
        addRow(card, gbc, "User ID", 4);
        txtUserId = makeLocked();
        addField(card, gbc, txtUserId, 4);

        // ── Status ─────────────────────────────────────────────────────────
        addRow(card, gbc, "Status", 5);
        cmbStatus = new UITheme.StyledComboBox(new String[]{"Active", "Inactive"});
        cmbStatus.setEnabled(false);
        addField(card, gbc, cmbStatus, 5);

        // ── Role info area ─────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 8, 8);

        txtRoleArea = new JTextArea(3, 20);
        txtRoleArea.setEditable(false);
        txtRoleArea.setFont(UITheme.FONT_MONO);
        txtRoleArea.setForeground(UITheme.ACCENT_TEAL);
        txtRoleArea.setBackground(UITheme.BG_INPUT);
        txtRoleArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        card.add(txtRoleArea, gbc);

        // ── Button row ─────────────────────────────────────────────────────
        gbc.gridy = 7; gbc.insets = new Insets(16, 8, 4, 8);
        gbc.anchor = GridBagConstraints.EAST;

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);

        // --- NEW TOPSIS BUTTON ADDED HERE ---
        UITheme.StyledButton btnRanking = new UITheme.StyledButton("Priority Ranking", UITheme.WARNING_YELLOW);
        btnRanking.setPreferredSize(new Dimension(160, 40));
        btnRanking.addActionListener(e -> new UserRankingUI(userController).setVisible(true));
        btnRow.add(btnRanking);
        // ------------------------------------

        btnEditSave = new UITheme.StyledButton("Edit Profile", UITheme.ACCENT_BLUE);
        btnEditSave.setPreferredSize(new Dimension(150, 40));
        btnEditSave.addActionListener(this::handleEditSave);

        btnLogout = new UITheme.StyledButton("Logout", UITheme.DANGER_RED);
        btnLogout.setPreferredSize(new Dimension(120, 40));
        btnLogout.addActionListener(this::handleLogout);

        btnRow.add(btnEditSave);
        btnRow.add(btnLogout);
        card.add(btnRow, gbc);

        centre.add(card, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);
    }

    // ── Layout helpers ─────────────────────────────────────────────────────
    private void addRow(JPanel p, GridBagConstraints gbc, String text, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(text + "  ");
        lbl.setFont(UITheme.FONT_LABEL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(lbl, gbc);
    }

    private void addField(JPanel p, GridBagConstraints gbc,
                          JComponent field, int row) {
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 1;
        gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        p.add(field, gbc);
    }

    /** Creates a read-only locked field styled to look clearly non-editable. */
    private JTextField makeLocked() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setFont(UITheme.FONT_BODY);
        tf.setForeground(UITheme.TEXT_MUTED);
        tf.setBackground(UITheme.BG_ELEVATED);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.DIVIDER_COLOR, 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        tf.setPreferredSize(new Dimension(200, 40));
        return tf;
    }

    // ── Load profile ───────────────────────────────────────────────────────
    private void loadProfile(String userId) {
        if (userId == null || userId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a Session ID.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = userController.getProfile(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "User not found. Check the Session ID.",
                    "Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUserId = userId;
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtUserId.setText(user.getUserId());
        cmbStatus.setSelectedItem(user.getStatus());

        // Polymorphic getRoleDetails() call — demonstrated here
        System.out.println("[Polymorphism] " + user.getRoleDetails());

        if (user instanceof Student stu) {
            roleBadge = refreshBadge("Student", UITheme.ACCENT_BLUE);
            txtRoleArea.setText(
                    "Role          : Student\n" +
                    "Enrolled Courses : " + stu.getEnrolledCoursesCount() + "\n" +
                    user.getRoleDetails());
        } else if (user instanceof Instructor ins) {
            roleBadge = refreshBadge("Instructor", UITheme.ACCENT_TEAL);
            txtRoleArea.setText(
                    "Role          : Instructor\n" +
                    "Department    : " + ins.getDepartment() + "\n" +
                    "Staff ID      : " + ins.getStaffId());
        }

        setEditMode(false);
    }

    /** Re-creates role badge text (badge already placed in layout). */
    private UITheme.BadgeLabel refreshBadge(String text, Color color) {
        roleBadge.setText(text);
        return roleBadge;
    }

    // ── Edit / Save toggle ─────────────────────────────────────────────────
    private void handleEditSave(ActionEvent e) {
        if (!isEditing) {
            setEditMode(true);
        } else {
            String newName   = txtName.getText().trim();
            String newStatus = (String) cmbStatus.getSelectedItem();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Name cannot be empty.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ProfileDTO dto = new ProfileDTO(newName, txtEmail.getText(), newStatus);
            userController.editProfile(currentUserId, dto);

            JOptionPane.showMessageDialog(this,
                    "Profile Updated Successfully!",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
            setEditMode(false);
        }
    }

    private void setEditMode(boolean editing) {
        isEditing = editing;
        txtName.setEditable(editing);
        txtName.setBackground(editing ? UITheme.BG_INPUT : UITheme.BG_ELEVATED);
        cmbStatus.setEnabled(editing);
        btnEditSave.setText(editing ? "Save Profile" : "Edit Profile");
    }

    // ── Logout ─────────────────────────────────────────────────────────────
    private void handleLogout(ActionEvent e) {
        authController.logout(currentUserId);
        JOptionPane.showMessageDialog(this,
                "You are now logged out.", "Message",
                JOptionPane.INFORMATION_MESSAGE);
        new LoginUI().setVisible(true);
        dispose();
    }
}