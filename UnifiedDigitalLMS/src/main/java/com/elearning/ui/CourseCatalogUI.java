/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import com.elearning.controllers.CourseController;
import com.elearning.entities.Course;
import com.elearning.entities.Instructor;
import com.elearning.entities.Material;
import com.elearning.entities.Topic;
import com.elearning.entities.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * UI: CourseCatalogUI
 * Purpose: Catalog browser for both Students and Instructors.
 *
 * • Every course shows as a card with a "View →" button.
 * • "View →" opens CourseDetailsPanel — a full JFrame with the syllabus
 * tree AND the bottom action bar (Add Topic / Upload Material / Refresh).
 * • Add Topic / Upload Material buttons are only active for Instructors.
 * Students see them hidden entirely.
 *
 * SESSION INJECTION: activeUser is passed through to CourseDetailsPanel
 * and further into MaterialUI / controller calls.
 */
public class CourseCatalogUI extends JFrame {

    private final CourseController controller;
    private final User             activeUser;

    private UITheme.StyledTextField tfSearch;
    private JPanel                  catalogPanel;
    private JLabel                  lblStatus;

    // ── Constructor ───────────────────────────────────────────────────────

    public CourseCatalogUI(CourseController controller, User activeUser) {
        this.controller = controller;
        this.activeUser = activeUser;
        UITheme.applyGlobalDefaults();
        initComponents();
    }

    // ── UI Construction ───────────────────────────────────────────────────

    private void initComponents() {
        setTitle("Course Catalog — " + activeUser.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        // ── Header + search bar block ─────────────────────────────────────
        JPanel northBlock = new JPanel(new BorderLayout());
        northBlock.setOpaque(false);

        UITheme.GradientHeader hdr = new UITheme.GradientHeader(
                "Course Catalog",
                "Browse and search all available courses");
        northBlock.add(hdr, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchRow.setBackground(UITheme.BG_DARK);

        tfSearch = new UITheme.StyledTextField("Search courses...");
        tfSearch.setPreferredSize(new Dimension(240, 38));

        UITheme.StyledButton btnSearch = new UITheme.StyledButton("Search", UITheme.ACCENT_BLUE);
        btnSearch.setPreferredSize(new Dimension(100, 38));
        btnSearch.addActionListener(e -> filterCatalog(tfSearch.getText().trim()));

        UITheme.StyledButton btnAll = new UITheme.StyledButton("Show All", UITheme.ACCENT_TEAL);
        btnAll.setPreferredSize(new Dimension(100, 38));
        btnAll.addActionListener(e -> loadAll());

        searchRow.add(tfSearch);
        searchRow.add(btnSearch);
        searchRow.add(btnAll);
        northBlock.add(searchRow, BorderLayout.SOUTH);

        root.add(northBlock, BorderLayout.NORTH);

        // ── Scrollable catalog grid ───────────────────────────────────────
        catalogPanel = new JPanel();
        catalogPanel.setLayout(new BoxLayout(catalogPanel, BoxLayout.Y_AXIS));
        catalogPanel.setBackground(UITheme.BG_DARK);
        catalogPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JScrollPane sp = new JScrollPane(catalogPanel);
        UITheme.styleScrollPane(sp);
        root.add(sp, BorderLayout.CENTER);

        // ── Status bar ────────────────────────────────────────────────────
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UITheme.BG_ELEVATED);
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));

        lblStatus = new JLabel("Ready.");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        statusBar.add(lblStatus, BorderLayout.WEST);

        if (activeUser instanceof Instructor) {
            UITheme.StyledButton btnManage = new UITheme.StyledButton(
                    "+ Manage Courses", UITheme.ACCENT_BLUE);
            btnManage.setPreferredSize(new Dimension(170, 32));
            btnManage.addActionListener(e ->
                    new CourseUI(controller, activeUser).setVisible(true));
            JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnWrap.setOpaque(false);
            btnWrap.add(btnManage);
            statusBar.add(btnWrap, BorderLayout.EAST);
        }

        root.add(statusBar, BorderLayout.SOUTH);

        loadAll();
    }

    // ── Catalog loading ───────────────────────────────────────────────────

    private void loadAll() {
        Collection<Course> courses = controller.getCatalogCourses();
        renderCards(courses);
        lblStatus.setText("Showing all " + courses.size() + " course(s).");
    }

    private void filterCatalog(String keyword) {
        List<Course> results = controller.filterCatalog(keyword);
        renderCards(results);
        lblStatus.setText("Found " + results.size()
                + " result(s) for: \"" + keyword + "\"");
    }

    private void renderCards(Collection<Course> courses) {
        catalogPanel.removeAll();
        if (courses.isEmpty()) {
            JLabel empty = new JLabel("No courses found.");
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            catalogPanel.add(empty);
        }
        for (Course c : courses) {
            catalogPanel.add(buildCourseCard(c));
            catalogPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        catalogPanel.revalidate();
        catalogPanel.repaint();
    }

    // ── Course card ───────────────────────────────────────────────────────

    private JPanel buildCourseCard(Course course) {
        UITheme.RoundPanel card = new UITheme.RoundPanel(14);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Left accent bar
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.ACCENT_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(5, 0));
        bar.setOpaque(false);

        // Text section
        UITheme.BadgeLabel codeChip  = new UITheme.BadgeLabel(course.getCourseID(), UITheme.ACCENT_BLUE);
        JLabel lblTitle = new JLabel(course.getCourseTitle());
        lblTitle.setFont(UITheme.FONT_HEADING);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel(
                "<html><body style='color:#7887AF'>" + course.getDescription() + "</body></html>");
        lblDesc.setFont(UITheme.FONT_SMALL);

        int topicCount = course.getTopicsList().size();
        UITheme.BadgeLabel topicBadge = new UITheme.BadgeLabel(
                topicCount + " topic" + (topicCount != 1 ? "s" : ""),
                new Color(45, 90, 60));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topRow.setOpaque(false);
        topRow.add(codeChip);
        topRow.add(lblTitle);
        topRow.add(topicBadge);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(topRow);
        textPanel.add(lblDesc);

        // View button
        UITheme.StyledButton btnView = new UITheme.StyledButton("View →", UITheme.ACCENT_TEAL);
        btnView.setPreferredSize(new Dimension(90, 34));
        btnView.addActionListener(e ->
                new CourseDetailsPanel(controller, course.getCourseID(), activeUser)
                        .setVisible(true));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(btnView);

        card.add(bar,        BorderLayout.WEST);
        card.add(textPanel,  BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    // Inner class: CourseDetailsPanel
    //
    // Full-screen syllabus view with:
    //   • Gradient header (course badge + title + description)
    //   • Collapsible JTree (topics → materials)
    //   • Bottom action bar: Add Topic | Upload Material | Refresh
    //
    // SESSION INJECTION: activeUser is forwarded to MaterialUI and
    // to every controller write-call so role guards fire correctly.
    // ══════════════════════════════════════════════════════════════════════

    public class CourseDetailsPanel extends JFrame {

        private final CourseController detailController;
        private final String           detailCourseID;
        private final User             detailUser;

        private JTree                  syllabusTree;
        private DefaultTreeModel       treeModel;
        private DefaultMutableTreeNode rootNode;

        public CourseDetailsPanel(CourseController controller,
                                  String courseID,
                                  User activeUser) {
            this.detailController = controller;
            this.detailCourseID   = courseID;
            this.detailUser       = activeUser;
            UITheme.applyGlobalDefaults();
            initUI();
        }

        private void initUI() {
            Course course = detailController.viewCourseContent(detailCourseID);
            if (course == null) {
                JOptionPane.showMessageDialog(null,
                        "Course not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            setTitle("Course: " + course.getCourseTitle());
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(720, 560);
            setMinimumSize(new Dimension(620, 460));
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(0, 0));
            root.setBackground(UITheme.BG_DARK);
            setContentPane(root);

            // ── Header ───────────────────────────────────────────────────
            UITheme.GradientHeader hdr = new UITheme.GradientHeader(
                    course.getCourseTitle(),
                    "[" + course.getCourseID() + "]  " + course.getDescription());
            root.add(hdr, BorderLayout.NORTH);

            // ── Syllabus tree ─────────────────────────────────────────────
            rootNode  = new DefaultMutableTreeNode(course.getCourseTitle());
            treeModel = new DefaultTreeModel(rootNode);
            syllabusTree = new JTree(treeModel);
            syllabusTree.setRootVisible(true);
            syllabusTree.setShowsRootHandles(true);
            syllabusTree.setBackground(UITheme.BG_ELEVATED);
            syllabusTree.setForeground(UITheme.TEXT_PRIMARY);
            syllabusTree.setFont(UITheme.FONT_BODY);
            syllabusTree.setRowHeight(28);
            syllabusTree.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            // Custom cell renderer
            syllabusTree.setCellRenderer(new DefaultTreeCellRenderer() {
                @Override
                public Component getTreeCellRendererComponent(
                        JTree tree, Object value, boolean sel,
                        boolean exp, boolean leaf, int row, boolean focus) {
                    super.getTreeCellRendererComponent(
                            tree, value, sel, exp, leaf, row, focus);
                    setBackground(sel ? UITheme.ACCENT_BLUE : UITheme.BG_ELEVATED);
                    setForeground(sel ? Color.WHITE
                            : (leaf ? UITheme.TEXT_MUTED : UITheme.TEXT_PRIMARY));
                    setFont(leaf ? UITheme.FONT_BODY : UITheme.FONT_LABEL);
                    setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                    setOpaque(true);
                    return this;
                }
            });

            populateTree(course);

            JScrollPane treeSP = new JScrollPane(syllabusTree);
            UITheme.styleScrollPane(treeSP);
            treeSP.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(16, 24, 0, 24),
                    BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true)));
            root.add(treeSP, BorderLayout.CENTER);

            // ── Bottom action bar ─────────────────────────────────────────
            JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
            btnBar.setBackground(UITheme.BG_DARK);
            btnBar.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));

            // ── Add Topic button ──────────────────────────────────────────
            UITheme.StyledButton btnAddTopic = new UITheme.StyledButton(
                    "＋ Add Topic", UITheme.SUCCESS_GREEN);
            btnAddTopic.setPreferredSize(new Dimension(148, 40));
            btnAddTopic.addActionListener(e -> clickAddTopic());
            
            // FIX: Hides the button entirely if the user is a student
            if (!(detailUser instanceof Instructor)) {
                btnAddTopic.setVisible(false);
            }
            btnBar.add(btnAddTopic);

            // ── Upload Material button ────────────────────────────────────
            UITheme.StyledButton btnUpload = new UITheme.StyledButton(
                    "⬆ Upload Material", UITheme.ACCENT_BLUE);
            btnUpload.setPreferredSize(new Dimension(170, 40));
            btnUpload.addActionListener(e -> clickUploadMaterial());
            
            // FIX: Hides the button entirely if the user is a student
            if (!(detailUser instanceof Instructor)) {
                btnUpload.setVisible(false);
            }
            btnBar.add(btnUpload);

            // ── Refresh button (available to all roles) ───────────────────
            UITheme.StyledButton btnRefresh = new UITheme.StyledButton(
                    "↺ Refresh", UITheme.ACCENT_TEAL);
            btnRefresh.setPreferredSize(new Dimension(110, 40));
            btnRefresh.addActionListener(e -> clickRefresh());
            btnBar.add(btnRefresh);

            root.add(btnBar, BorderLayout.SOUTH);
        }

        // ── Tree rendering ────────────────────────────────────────────────

        private void populateTree(Course course) {
            rootNode.removeAllChildren();
            for (int i = 0; i < course.getTopicsList().size(); i++) {
                Topic topic = course.getTopicsList().get(i);
                DefaultMutableTreeNode tn = new DefaultMutableTreeNode(
                        "📌  " + (i + 1) + ".  " + topic.getTopicTitle());
                for (Material m : topic.getMaterialList())
                    tn.add(new DefaultMutableTreeNode("📄  " + m.getTitle()));
                rootNode.add(tn);
            }
            treeModel.reload();
            // Expand all rows
            for (int i = 0; i < syllabusTree.getRowCount(); i++)
                syllabusTree.expandRow(i);
        }

        // ── Action handlers ───────────────────────────────────────────────

        /**
         * Add Topic — JOptionPane input dialog → controller.addTopic()
         */
        private void clickAddTopic() {
            if (detailController.isTopicLimitReached(detailCourseID)) {
                JOptionPane.showMessageDialog(this,
                        "Maximum topic limit (20) reached for this course.",
                        "Topic Limit", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String title = JOptionPane.showInputDialog(
                    this,
                    "Enter the title for the new topic:",
                    "Add New Topic",
                    JOptionPane.PLAIN_MESSAGE);

            if (title != null && !title.trim().isEmpty()) {
                boolean ok = detailController.addTopic(
                        detailCourseID, title.trim(), detailUser);
                if (ok) {
                    clickRefresh();  // immediately repaint the tree
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not add topic: "
                                    + detailController.getLastError(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        /**
         * Upload Material — opens MaterialUI with session injection.
         * Guards: must have at least one topic first.
         */
        private void clickUploadMaterial() {
            Course course = detailController.viewCourseContent(detailCourseID);
            if (course == null || course.getTopicsList().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add at least one topic before uploading material.",
                        "No Topics", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Open MaterialUI — pass controller, courseID, and activeUser
            new MaterialUI(detailController, detailCourseID, detailUser)
                    .setVisible(true);
        }

        /**
         * Refresh — re-reads the course from the static HashMap and repaints
         * the tree so newly added topics / materials appear immediately.
         */
        private void clickRefresh() {
            Course course = detailController.viewCourseContent(detailCourseID);
            if (course != null) {
                populateTree(course);
            }
        }
    }
}