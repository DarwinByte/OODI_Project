/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elearning.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * UITheme.java — Shared design system for OODI User Management Module.
 * All colours, fonts, and custom Swing components live here.
 * Pure Java Swing only — no external libraries.
 */
public class UITheme {

    // ── Colour Palette ─────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(13,  17,  30);
    public static final Color BG_PANEL      = new Color(22,  28,  48);
    public static final Color BG_ELEVATED   = new Color(30,  38,  62);
    public static final Color BG_INPUT      = new Color(18,  24,  42);
    public static final Color ACCENT_BLUE   = new Color(82,  153, 255);
    public static final Color ACCENT_TEAL   = new Color(0,   210, 170);
    public static final Color DANGER_RED    = new Color(255,  80,  90);
    public static final Color SUCCESS_GREEN = new Color(50,  210, 130);
    public static final Color WARNING_YELLOW= new Color(255, 185,  50);
    public static final Color TEXT_PRIMARY  = new Color(225, 232, 255);
    public static final Color TEXT_MUTED    = new Color(120, 135, 175);
    public static final Color BORDER_COLOR  = new Color(45,   58,  95);
    public static final Color DIVIDER_COLOR = new Color(35,   45,  75);

    // ── Typography ─────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN, 12);

    // ── Global LAF defaults ────────────────────────────────────────────────
    public static void applyGlobalDefaults() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Panel.background",          BG_PANEL);
        UIManager.put("OptionPane.background",      BG_PANEL);
        UIManager.put("OptionPane.messageForeground",TEXT_PRIMARY);
        UIManager.put("Button.background",          BG_ELEVATED);
        UIManager.put("Button.foreground",          TEXT_PRIMARY);
        UIManager.put("Label.foreground",           TEXT_PRIMARY);
        UIManager.put("TextField.background",       BG_INPUT);
        UIManager.put("TextField.foreground",       TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",  ACCENT_BLUE);
        UIManager.put("PasswordField.background",   BG_INPUT);
        UIManager.put("PasswordField.foreground",   TEXT_PRIMARY);
        UIManager.put("PasswordField.caretForeground", ACCENT_BLUE);
        UIManager.put("ComboBox.background",        BG_INPUT);
        UIManager.put("ComboBox.foreground",        TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",ACCENT_BLUE);
        UIManager.put("ComboBox.selectionForeground",Color.WHITE);
        UIManager.put("ScrollPane.background",      BG_PANEL);
        UIManager.put("Viewport.background",        BG_PANEL);
    }

    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    // ══════════════════════════════════════════════════════════════════════
    // GradientHeader — top banner with title + subtitle
    // ══════════════════════════════════════════════════════════════════════
    public static class GradientHeader extends JPanel {
        private final String title;
        private final String subtitle;

        public GradientHeader(String title, String subtitle) {
            this.title    = title;
            this.subtitle = subtitle;
            setPreferredSize(new Dimension(0, 90));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            // Gradient: dark navy → slightly lighter blue-dark
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(15, 20, 45),
                getWidth(), 0, new Color(28, 40, 80));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Accent line at bottom
            g2.setColor(ACCENT_BLUE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);

            // Title
            g2.setFont(FONT_TITLE);
            g2.setColor(TEXT_PRIMARY);
            g2.drawString(title, 32, 48);

            // Subtitle
            g2.setFont(FONT_SMALL);
            g2.setColor(TEXT_MUTED);
            g2.drawString(subtitle, 34, 68);

            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // RoundPanel — card panel with rounded corners + subtle shadow
    // ══════════════════════════════════════════════════════════════════════
    public static class RoundPanel extends JPanel {
        private final int arc;
        private final Color bg;

        public RoundPanel(int arc) {
            this(arc, BG_PANEL);
        }

        public RoundPanel(int arc, Color bg) {
            this.arc = arc;
            this.bg  = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            // Shadow
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fill(new RoundRectangle2D.Float(4, 6, getWidth()-4, getHeight()-6, arc, arc));
            // Panel bg
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-4, getHeight()-6, arc, arc));
            // Border
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-5, getHeight()-7, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // StyledButton — rounded gradient button with hover glow
    // ══════════════════════════════════════════════════════════════════════
    public static class StyledButton extends JButton {
        private final Color baseColor;
        private boolean hovered = false;

        public StyledButton(String text, Color baseColor) {
            super(text);
            this.baseColor = baseColor;
            setFont(FONT_BUTTON);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(getPreferredSize().width, 40));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hovered = true; repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    hovered = false; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Glow on hover
            if (hovered) {
                g2.setColor(new Color(baseColor.getRed(),
                                      baseColor.getGreen(),
                                      baseColor.getBlue(), 60));
                g2.fill(new RoundRectangle2D.Float(-3, -3, w+6, h+6, 18, 18));
            }

            // Shadow
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fill(new RoundRectangle2D.Float(3, 5, w-3, h-5, 12, 12));

            // Gradient fill
            Color top = hovered ? baseColor.brighter() : baseColor;
            Color bot = baseColor.darker();
            GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bot);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, w-3, h-5, 12, 12));

            // Label
            g2.setFont(getFont());
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 2;
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // StyledTextField — rounded dark text field with focus accent border
    // ══════════════════════════════════════════════════════════════════════
    public static class StyledTextField extends JTextField {
        private final String placeholder;
        private boolean focused = false;

        public StyledTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setForeground(TEXT_PRIMARY);
            setBackground(BG_INPUT);
            setCaretColor(ACCENT_BLUE);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            setOpaque(false);
            setPreferredSize(new Dimension(200, 40));

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    focused = true; repaint();
                }
                @Override public void focusLost(FocusEvent e) {
                    focused = false; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_INPUT);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(focused ? ACCENT_BLUE : BORDER_COLOR);
            g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
            super.paintComponent(g);

            // Placeholder
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setFont(FONT_BODY.deriveFont(Font.ITALIC));
                g2.setColor(TEXT_MUTED);
                g2.drawString(placeholder, 14,
                        getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 1);
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // StyledPasswordField — same as StyledTextField but for passwords
    // ══════════════════════════════════════════════════════════════════════
    public static class StyledPasswordField extends JPasswordField {
        private final String placeholder;
        private boolean focused = false;

        public StyledPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setForeground(TEXT_PRIMARY);
            setBackground(BG_INPUT);
            setCaretColor(ACCENT_BLUE);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            setOpaque(false);
            setPreferredSize(new Dimension(200, 40));

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    focused = true; repaint();
                }
                @Override public void focusLost(FocusEvent e) {
                    focused = false; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_INPUT);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(focused ? ACCENT_BLUE : BORDER_COLOR);
            g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
            super.paintComponent(g);

            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setFont(FONT_BODY.deriveFont(Font.ITALIC));
                g2.setColor(TEXT_MUTED);
                g2.drawString(placeholder, 14,
                        getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 1);
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // StyledComboBox — dark combo with accent selection
    // ══════════════════════════════════════════════════════════════════════
    public static class StyledComboBox extends JComboBox<String> {
        public StyledComboBox(String[] items) {
            super(items);
            setFont(FONT_BODY);
            setForeground(TEXT_PRIMARY);
            setBackground(BG_INPUT);
            setOpaque(true);
            setPreferredSize(new Dimension(200, 40));
            setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list,
                        Object value, int index, boolean sel, boolean focus) {
                    super.getListCellRendererComponent(list,value,index,sel,focus);
                    setBackground(sel ? ACCENT_BLUE : BG_INPUT);
                    setForeground(TEXT_PRIMARY);
                    setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                    setFont(FONT_BODY);
                    return this;
                }
            });
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // BadgeLabel — pill-shaped coloured chip label
    // ══════════════════════════════════════════════════════════════════════
    public static class BadgeLabel extends JLabel {
        private final Color bg;

        public BadgeLabel(String text, Color bg) {
            super(text, SwingConstants.CENTER);
            this.bg = bg;
            setFont(FONT_SMALL);
            setForeground(Color.WHITE);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Thin dark scrollbar
    // ══════════════════════════════════════════════════════════════════════
    public static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_PANEL);
        sp.setBackground(BG_PANEL);
        JScrollBar vsb = sp.getVerticalScrollBar();
        vsb.setPreferredSize(new Dimension(8, 0));
        vsb.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = ACCENT_BLUE;
                trackColor = BG_ELEVATED;
            }
            @Override protected JButton createDecreaseButton(int o) {
                return zeroButton();
            }
            @Override protected JButton createIncreaseButton(int o) {
                return zeroButton();
            }
            private JButton zeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0,0));
                return b;
            }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_BLUE);
                g2.fill(new RoundRectangle2D.Float(r.x+1, r.y, r.width-2, r.height, 6, 6));
                g2.dispose();
            }
        });
    }
}