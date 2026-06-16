/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.AuthController;
import com.elearning.controllers.CourseController;
import com.elearning.controllers.EnrolmentController;
import com.elearning.controllers.UserController;
import com.elearning.dtos.LoginDTO;
import com.elearning.entities.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LoginUI — Application entry point.
 * Creates all shared controllers once, injects into DashboardUI on login.
 */
public class LoginUI extends JFrame {

    private final UserController        userController;
    private final AuthController        authController;
    private final CourseController      courseController;
    private final EnrolmentController   enrolmentController;

    private UITheme.StyledTextField     txtEmail;
    private UITheme.StyledPasswordField txtPassword;

    public LoginUI() {
        UITheme.applyGlobalDefaults();
        this.userController      = new UserController();
        this.authController      = new AuthController(userController);
        this.courseController    = new CourseController();
        this.enrolmentController = new EnrolmentController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Unified Digital Learning Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 520);
        setMinimumSize(new Dimension(380, 440));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        UITheme.GradientHeader header = new UITheme.GradientHeader(
                "User Management", "Unified Digital Learning Management System");
        add(header, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(UITheme.BG_DARK);
        centre.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        UITheme.RoundPanel card = new UITheme.RoundPanel(18);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel lblWelcome = new JLabel("Welcome back");
        lblWelcome.setFont(UITheme.FONT_TITLE);
        lblWelcome.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.WEST;
        card.add(lblWelcome, gbc);

        JLabel lblSub = new JLabel("Sign in to your account");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy=1; card.add(lblSub, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.DIVIDER_COLOR);
        sep.setBackground(UITheme.DIVIDER_COLOR);
        gbc.gridy=2; gbc.insets=new Insets(4,8,12,8);
        card.add(sep, gbc);
        gbc.insets=new Insets(8,8,8,8);

        // Email
        gbc.gridwidth=1; gbc.gridy=3;
        gbc.gridx=0; gbc.weightx=0; gbc.anchor=GridBagConstraints.EAST;
        card.add(makeLabel("Email"), gbc);
        gbc.gridx=1; gbc.weightx=1.0; gbc.anchor=GridBagConstraints.WEST;
        txtEmail = new UITheme.StyledTextField("Enter your email");
        card.add(txtEmail, gbc);

        // Password
        gbc.gridy=4;
        gbc.gridx=0; gbc.weightx=0; gbc.anchor=GridBagConstraints.EAST;
        card.add(makeLabel("Password"), gbc);
        gbc.gridx=1; gbc.weightx=1.0; gbc.anchor=GridBagConstraints.WEST;
        txtPassword = new UITheme.StyledPasswordField("Enter your password");
        card.add(txtPassword, gbc);

        // Login button
        gbc.gridy=5; gbc.gridx=0; gbc.gridwidth=2;
        gbc.anchor=GridBagConstraints.EAST; gbc.insets=new Insets(16,8,4,8);
        UITheme.StyledButton btnLogin = new UITheme.StyledButton("Login", UITheme.ACCENT_BLUE);
        btnLogin.setPreferredSize(new Dimension(180, 40));
        btnLogin.addActionListener(this::handleLogin);
        card.add(btnLogin, gbc);

        // Register link
        gbc.gridy=6; gbc.insets=new Insets(4,8,8,8); gbc.anchor=GridBagConstraints.CENTER;
        JLabel lblRegLink = new JLabel("Don't have an account? Register Now");
        lblRegLink.setFont(UITheme.FONT_SMALL);
        lblRegLink.setForeground(UITheme.ACCENT_TEAL);
        lblRegLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new RegisterUI(userController).setVisible(true); dispose();
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                lblRegLink.setForeground(UITheme.ACCENT_BLUE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                lblRegLink.setForeground(UITheme.ACCENT_TEAL);
            }
        });
        card.add(lblRegLink, gbc);

        GridBagConstraints cGbc = new GridBagConstraints();
        cGbc.fill=GridBagConstraints.BOTH; cGbc.weightx=1; cGbc.weighty=1;
        centre.add(card, cGbc);
        add(centre, BorderLayout.CENTER);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text + "  ");
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_MUTED);
        return l;
    }

    private void handleLogin(ActionEvent e) {
        String email    = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please fill in both fields.",
                    "Input Error", JOptionPane.WARNING_MESSAGE); return;
        }
        User user = authController.login(new LoginDTO(email, password));
        if (user == null) {
            JOptionPane.showMessageDialog(this,"Invalid credentials or account suspended.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Login Successful! Welcome, " + user.getName() + ".",
                    "Welcome", JOptionPane.INFORMATION_MESSAGE);
            new DashboardUI(user, userController, authController,
                            courseController, enrolmentController).setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}