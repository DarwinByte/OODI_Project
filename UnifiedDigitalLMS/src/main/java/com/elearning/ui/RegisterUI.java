/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.UserController;
import com.elearning.dtos.RegisterDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UI: RegisterUI
 * Reskinned to match team dark theme (UITheme).
 * Layout preserved: Full Name, Email, Password, Confirm Password,
 * Student/Instructor role selection, Register button, Back to Login link.
 */
public class RegisterUI extends JFrame {

    private final UserController userController;

    private UITheme.StyledTextField     txtName;
    private UITheme.StyledTextField     txtEmail;
    private UITheme.StyledPasswordField txtPassword;
    private UITheme.StyledPasswordField txtConfirmPassword;
    private JRadioButton                rbStudent;
    private JRadioButton                rbInstructor;
    private UITheme.StyledButton        btnRegister;
    private JLabel                      lblBackToLogin;

    public RegisterUI(UserController userController) {
        this.userController = userController;
        UITheme.applyGlobalDefaults();
        initComponents();
    }

    private void initComponents() {
        setTitle("Register — Unified Digital Learning Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setMinimumSize(new Dimension(420, 520));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // ── Gradient header ────────────────────────────────────────────────
        UITheme.GradientHeader header = new UITheme.GradientHeader(
                "Create Account",
                "Register as a Student or Instructor");
        add(header, BorderLayout.NORTH);

        // ── Centre wrapper ─────────────────────────────────────────────────
        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(UITheme.BG_DARK);
        centre.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        UITheme.RoundPanel card = new UITheme.RoundPanel(18);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ── Heading ────────────────────────────────────────────────────────
        JLabel lblHeading = new JLabel("Registration");
        lblHeading.setFont(UITheme.FONT_TITLE);
        lblHeading.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(lblHeading, gbc);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.DIVIDER_COLOR);
        gbc.gridy = 1; gbc.insets = new Insets(2, 8, 12, 8);
        card.add(sep, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);

        // ── Full Name ──────────────────────────────────────────────────────
        addFormRow(card, gbc, "Full Name", 2);
        txtName = new UITheme.StyledTextField("Enter your full name");
        addField(card, gbc, txtName, 2);

        // ── Email ──────────────────────────────────────────────────────────
        addFormRow(card, gbc, "Email", 3);
        txtEmail = new UITheme.StyledTextField("Enter your email address");
        addField(card, gbc, txtEmail, 3);

        // ── Password ───────────────────────────────────────────────────────
        addFormRow(card, gbc, "Password", 4);
        txtPassword = new UITheme.StyledPasswordField("Min. 8 characters");
        addField(card, gbc, txtPassword, 4);

        // ── Confirm Password ───────────────────────────────────────────────
        addFormRow(card, gbc, "Confirm Password", 5);
        txtConfirmPassword = new UITheme.StyledPasswordField("Re-enter password");
        addField(card, gbc, txtConfirmPassword, 5);

        // ── Role selection ─────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblRole = new JLabel("Role  ");
        lblRole.setFont(UITheme.FONT_LABEL);
        lblRole.setForeground(UITheme.TEXT_MUTED);
        card.add(lblRole, gbc);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        radioPanel.setOpaque(false);

        rbStudent    = makeRadio("Student");
        rbInstructor = makeRadio("Instructor");
        rbStudent.setSelected(true);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(rbStudent);
        roleGroup.add(rbInstructor);

        radioPanel.add(rbStudent);
        radioPanel.add(rbInstructor);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1;
        card.add(radioPanel, gbc);

        // ── Register button ────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(18, 8, 4, 8);
        btnRegister = new UITheme.StyledButton("Register", UITheme.SUCCESS_GREEN);
        btnRegister.setPreferredSize(new Dimension(180, 40));
        btnRegister.addActionListener(this::handleRegister);
        card.add(btnRegister, gbc);

        // ── Back to Login link ─────────────────────────────────────────────
        gbc.gridy = 8; gbc.insets = new Insets(4, 8, 8, 8);
        gbc.anchor = GridBagConstraints.CENTER;
        lblBackToLogin = new JLabel("Already have an account? Back to Login");
        lblBackToLogin.setFont(UITheme.FONT_SMALL);
        lblBackToLogin.setForeground(UITheme.ACCENT_TEAL);
        lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBackToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new LoginUI().setVisible(true);
                dispose();
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                lblBackToLogin.setForeground(UITheme.ACCENT_BLUE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                lblBackToLogin.setForeground(UITheme.ACCENT_TEAL);
            }
        });
        card.add(lblBackToLogin, gbc);

        // ── Assemble ───────────────────────────────────────────────────────
        GridBagConstraints cGbc = new GridBagConstraints();
        cGbc.fill = GridBagConstraints.BOTH;
        cGbc.weightx = 1; cGbc.weighty = 1;
        centre.add(card, cGbc);
        add(centre, BorderLayout.CENTER);
    }

    // ── Layout helpers ─────────────────────────────────────────────────────
    private void addFormRow(JPanel p, GridBagConstraints gbc, String text, int row) {
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

    private JRadioButton makeRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(UITheme.FONT_BODY);
        rb.setForeground(UITheme.TEXT_PRIMARY);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    // ── Register handler ───────────────────────────────────────────────────
    private void handleRegister(ActionEvent e) {
        String name     = txtName.getText().trim();
        String email    = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirm  = new String(txtConfirmPassword.getPassword()).trim();
        String role     = rbStudent.isSelected() ? "Student" : "Instructor";

        if (name.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirm.isEmpty()) {
            showMsg("Please fill in all fields.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            showMsg("Passwords do not match.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 8) {
            showMsg("Password must be at least 8 characters.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            RegisterDTO dto    = new RegisterDTO(name, email, password, role);
            String      userId = userController.registerUser(dto);
            showMsg("Registration Successful!\n" +
                    "Your User ID is: " + userId + "\n\n" +
                    "Please save this ID to search your profile later.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginUI().setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            showMsg(ex.getMessage(),
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMsg(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}