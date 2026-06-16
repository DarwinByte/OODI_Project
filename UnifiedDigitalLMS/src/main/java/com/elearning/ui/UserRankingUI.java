/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;
//implement by ca25091 (TOPSIS)

import com.elearning.controllers.UserController;
import com.elearning.entities.Instructor;
import com.elearning.entities.User;
import com.elearning.utils.TopsisEngine;
import com.elearning.utils.TopsisEngine.Result;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UI: UserRankingUI
 * Purpose: Displays a TOPSIS-ranked priority list of all registered users.
 *          Opened via the "Priority Ranking" button in ProfileUI.
 *
 * BONUS IMPLEMENTATION — TOPSIS (Multi-Criteria Decision Making)
 * ──────────────────────────────────────────────────────────────
 * Theory: TOPSIS ranks alternatives by their geometric distance from an
 * Ideal Best solution and an Ideal Worst solution.
 *
 * Applied here to rank Users by 3 criteria:
 *
 *   Criterion 1 — Role Weight         (weight: 40%)
 *     Instructor = 2.0  |  Student = 1.0
 *     isBenefit = true  (higher role privilege = better rank)
 *
 *   Criterion 2 — Status Weight       (weight: 40%)
 *     Active = 3.0  |  Pending = 2.0  |  Suspended = 1.0
 *     isBenefit = true  (active accounts rank higher)
 *
 *   Criterion 3 — User ID Score       (weight: 20%)
 *     Derived from the numeric portion of the User ID string.
 *     e.g. "INS-FA5A" → hex parse of "FA5A" → 64090 → normalized.
 *     isBenefit = false (lower numeric ID = registered earlier = ranks higher)
 *
 * Result: The table shows each user's rank, TOPSIS score (0–1), and the
 * three raw criterion values used, so the ranking is transparent and
 * academically defensible.
 *
 * Package: com.elearning.ui  (unified structure)
 */
public class UserRankingUI extends JFrame {

    // ── Criterion weights — must sum to 1.0 ──────────────────────────────
    private static final double W_ROLE   = 0.40;
    private static final double W_STATUS = 0.40;
    private static final double W_ID     = 0.20;

    // ── Dependencies ──────────────────────────────────────────────────────
    private final UserController userController;

    // ── Widgets ───────────────────────────────────────────────────────────
    private JTable            tblRanking;
    private DefaultTableModel tableModel;
    private JLabel            lblBest;

    // ── Constructor ───────────────────────────────────────────────────────

    public UserRankingUI(UserController userController) {
        this.userController = userController;
        UITheme.applyGlobalDefaults();
        initComponents();
        runTopsis();
    }

    // ── UI Construction ───────────────────────────────────────────────────

    private void initComponents() {
        setTitle("User Priority Ranking — TOPSIS");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(640, 440));
        setLocationRelativeTo(null);
        setResizable(true);

        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // ── Header ───────────────────────────────────────────────────────
        UITheme.GradientHeader header = new UITheme.GradientHeader(
                "User Priority Ranking",
                "TOPSIS — Multi-Criteria Decision Making  |  3 Criteria · 2 Roles");
        add(header, BorderLayout.NORTH);

        // ── Main card ─────────────────────────────────────────────────────
        UITheme.RoundPanel card = new UITheme.RoundPanel(18);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel outerPad = new JPanel(new BorderLayout());
        outerPad.setOpaque(false);
        outerPad.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        outerPad.add(card, BorderLayout.CENTER);
        add(outerPad, BorderLayout.CENTER);

        // ── Criteria legend ───────────────────────────────────────────────
        JPanel legendRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        legendRow.setOpaque(false);

        JLabel legendTitle = new JLabel("Criteria Weights:");
        legendTitle.setFont(UITheme.FONT_LABEL);
        legendTitle.setForeground(UITheme.TEXT_MUTED);
        legendRow.add(legendTitle);

        legendRow.add(new UITheme.BadgeLabel("Role 40%",   UITheme.ACCENT_BLUE));
        legendRow.add(new UITheme.BadgeLabel("Status 40%", UITheme.ACCENT_TEAL));
        legendRow.add(new UITheme.BadgeLabel("User ID 20%", new Color(130, 80, 200)));

        card.add(legendRow, BorderLayout.NORTH);

        // ── Ranking table ─────────────────────────────────────────────────
        tblRanking = new JTable() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRanking.setFont(UITheme.FONT_BODY);
        tblRanking.setForeground(UITheme.TEXT_PRIMARY);
        tblRanking.setBackground(UITheme.BG_ELEVATED);
        tblRanking.setRowHeight(36);
        tblRanking.setShowGrid(false);
        tblRanking.setIntercellSpacing(new Dimension(0, 0));
        tblRanking.setSelectionBackground(new Color(82, 153, 255, 60));
        tblRanking.setSelectionForeground(UITheme.TEXT_PRIMARY);
        tblRanking.setFocusable(false);

        // Header
        JTableHeader th = tblRanking.getTableHeader();
        th.setFont(UITheme.FONT_LABEL);
        th.setBackground(UITheme.BG_ELEVATED);
        th.setForeground(UITheme.TEXT_MUTED);
        th.setPreferredSize(new Dimension(0, 38));
        th.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, UITheme.DIVIDER_COLOR));
        th.setReorderingAllowed(false);

        // Cell renderer — rank 1 highlighted, alternating rows
        tblRanking.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                setFont(UITheme.FONT_BODY);

                if (!isSelected) {
                    // Rank 1 row — subtle gold highlight
                    if (row == 0) {
                        setBackground(new Color(60, 50, 20));
                        setForeground(new Color(255, 210, 80));
                    } else {
                        setBackground(row % 2 == 0 ? UITheme.BG_ELEVATED
                                : new Color(30, 40, 68));
                        setForeground(UITheme.TEXT_PRIMARY);
                    }
                } else {
                    setBackground(new Color(82, 153, 255, 60));
                    setForeground(UITheme.TEXT_PRIMARY);
                }

                // Score column (col 2) — colour by score range
                if (col == 2 && value != null) {
                    try {
                        double s = Double.parseDouble(value.toString());
                        if      (s >= 0.70) setForeground(UITheme.SUCCESS_GREEN);
                        else if (s >= 0.40) setForeground(UITheme.ACCENT_BLUE);
                        else                setForeground(UITheme.DANGER_RED);
                    } catch (NumberFormatException ignored) {}
                }
                return this;
            }
        });

        // Setup model
        tableModel = new DefaultTableModel(
                new String[]{"Rank", "User Name", "Score",
                             "Role", "Status", "User ID", "Role Wt", "Status Wt"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRanking.setModel(tableModel);

        int[] widths = {55, 150, 80, 100, 90, 100, 70, 75};
        for (int i = 0; i < widths.length; i++)
            tblRanking.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane sp = new JScrollPane(tblRanking);
        UITheme.styleScrollPane(sp);
        sp.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, UITheme.BORDER_COLOR));
        card.add(sp, BorderLayout.CENTER);

        // ── Footer — best result display + Recalculate button ────────────
        JPanel footer = new JPanel(new BorderLayout(0, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        lblBest = new JLabel("Best ranked user will appear here after calculation.");
        lblBest.setFont(UITheme.FONT_LABEL);
        lblBest.setForeground(new Color(255, 210, 80));

        UITheme.StyledButton btnRecalc = new UITheme.StyledButton(
                "↺  Recalculate", UITheme.ACCENT_TEAL);
        btnRecalc.setPreferredSize(new Dimension(150, 36));
        btnRecalc.addActionListener(e -> runTopsis());

        UITheme.StyledButton btnInfo = new UITheme.StyledButton(
                "? How it works", UITheme.BG_ELEVATED);
        btnInfo.setPreferredSize(new Dimension(140, 36));
        btnInfo.addActionListener(e -> showExplanationDialog());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnInfo);
        btnRow.add(btnRecalc);

        footer.add(lblBest, BorderLayout.WEST);
        footer.add(btnRow,  BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);
    }

    // ── TOPSIS execution ──────────────────────────────────────────────────

    /**
     * Pulls all users from UserController's static HashMap,
     * builds the decision matrix, runs TopsisEngine, and populates the table.
     *
     * CRITERIA MATRIX (3 columns per user):
     *   Col 0: roleScore    — Instructor=2.0, Student=1.0
     *   Col 1: statusScore  — Active=3.0, Pending=2.0, Suspended/other=1.0
     *   Col 2: idScore      — derived from userId string (lower = better → cost criterion)
     */
    public void runTopsis() {
        Map<String, User> allUsers = UserController.getDatabase();

        if (allUsers == null || allUsers.isEmpty()) {
            tableModel.setRowCount(0);
            lblBest.setText("No users registered yet.");
            return;
        }

        // Convert map to list for stable ordering
        List<User> userList = new ArrayList<>(allUsers.values());
        int m = userList.size();

        String[]   names  = new String[m];
        double[][] matrix = new double[m][3];

        for (int i = 0; i < m; i++) {
            User u = userList.get(i);
            names[i] = u.getName();

            // Criterion 1: Role
            matrix[i][0] = (u instanceof Instructor) ? 2.0 : 1.0;

            // Criterion 2: Status
            matrix[i][1] = switch (u.getStatus().toLowerCase()) {
                case "active"    -> 3.0;
                case "pending"   -> 2.0;
                default          -> 1.0;   // suspended / unknown
            };

            // Criterion 3: User ID numeric score
            // Extract trailing alphanumeric portion and parse as hex.
            // e.g. "INS-FA5A" → "FA5A" → 64090.0
            // e.g. "STU-99FE" → "99FE" → 39422.0
            matrix[i][2] = extractIdScore(u.getUserId());
        }

        // Weights: Role 40%, Status 40%, UserID 20%
        double[]  weights   = { W_ROLE, W_STATUS, W_ID };
        // isBenefit: Role=true, Status=true, UserID=false (lower = registered earlier = better)
        boolean[] isBenefit = { true, true, false };

        Result[] results = TopsisEngine.rank(names, matrix, weights, isBenefit);

        // ── Populate table ────────────────────────────────────────────────
        tableModel.setRowCount(0);
        for (Result r : results) {
            // Find the original User to show raw values
            User u = findUserByName(userList, r.alternativeName);
            String roleLabel   = (u instanceof Instructor) ? "Instructor" : "Student";
            String statusLabel = u.getStatus();
            double roleWt   = (u instanceof Instructor) ? 2.0 : 1.0;
            double statusWt = switch (u.getStatus().toLowerCase()) {
                case "active" -> 3.0; case "pending" -> 2.0; default -> 1.0;
            };

            tableModel.addRow(new Object[]{
                r.rank,
                r.alternativeName,
                String.format("%.4f", r.score),
                roleLabel,
                statusLabel,
                u.getUserId(),
                String.format("%.1f", roleWt),
                String.format("%.1f", statusWt)
            });
        }

        // Best user announcement
        if (results.length > 0) {
            lblBest.setText("🏆  Best Ranked: "
                    + results[0].alternativeName
                    + "   (Score: " + String.format("%.4f", results[0].score) + ")");
        }

        System.out.println("\n=== TOPSIS User Ranking ===");
        for (Result r : results)
            System.out.println(r);
    }

    // ── Explanation dialog ────────────────────────────────────────────────

    private void showExplanationDialog() {
        String msg =
            "TOPSIS — Technique for Order of Preference by Similarity to Ideal Solution\n\n" +
            "CRITERIA USED:\n" +
            "  1. Role Weight     (40%)  — Instructor=2, Student=1\n" +
            "  2. Status Weight   (40%)  — Active=3, Pending=2, Suspended=1\n" +
            "  3. User ID Score   (20%)  — Hex value of ID suffix (lower = better)\n\n" +
            "ALGORITHM STEPS:\n" +
            "  1. Normalize the decision matrix using vector normalization\n" +
            "  2. Apply criterion weights to get the weighted matrix\n" +
            "  3. Find Ideal Best (A+) and Ideal Worst (A-) per criterion\n" +
            "  4. Measure Euclidean distance from each user to A+ and A-\n" +
            "  5. Score = D- / (D+ + D-)   →   range: 0.0 (worst) to 1.0 (best)\n" +
            "  6. Rank users highest score first\n\n" +
            "Score colour coding:\n" +
            "  Green  ≥ 0.70  |  Blue  0.40–0.69  |  Red  < 0.40";

        JOptionPane.showMessageDialog(this, msg,
                "TOPSIS — How It Works", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Extracts a comparable numeric score from a User ID string.
     * Parses the last 4 hex characters; falls back to hash code if not hex.
     * Examples: "INS-FA5A" → 64090.0 | "STU-99FE" → 39422.0
     */
    private double extractIdScore(String userId) {
        if (userId == null || userId.isEmpty()) return 0.0;
        String clean = userId.replaceAll("[^A-Fa-f0-9]", "");
        if (clean.isEmpty()) return Math.abs((double) userId.hashCode());
        try {
            // Take at most last 4 hex chars to keep numbers comparable
            String hex = clean.length() > 4
                    ? clean.substring(clean.length() - 4) : clean;
            return (double) Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return Math.abs((double) userId.hashCode());
        }
    }

    /** Finds a User in the list by name (name is used as the TOPSIS label). */
    private User findUserByName(List<User> list, String name) {
        for (User u : list)
            if (u.getName().equals(name)) return u;
        return list.get(0); // fallback
    }
}