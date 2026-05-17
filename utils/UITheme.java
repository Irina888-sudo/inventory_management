package utils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class UITheme {

    // ── Palette ──────────────────────────────────────────────
    public static final Color NAVY        = new Color(0x00, 0x33, 0x66);
    public static final Color NAVY_DARK   = new Color(0x00, 0x22, 0x44);
    public static final Color NAVY_LIGHT  = new Color(0x00, 0x4C, 0x99);
    public static final Color LIGHT       = new Color(0xF4, 0xF4, 0xF4);
    public static final Color ORANGE      = new Color(0xFF, 0xA5, 0x00);
    public static final Color ORANGE_DARK = new Color(0xE6, 0x94, 0x00);
    public static final Color WHITE       = Color.WHITE;
    public static final Color DARK_TEXT   = new Color(0x1A, 0x1A, 0x2E);
    public static final Color MUTED       = new Color(0x8A, 0x9B, 0xB0);
    public static final Color BORDER      = new Color(0xE0, 0xE6, 0xED);
    public static final Color SUCCESS     = new Color(0x16, 0x7A, 0x3C);
    public static final Color SUCCESS_BG  = new Color(0xE8, 0xF8, 0xEE);
    public static final Color DANGER      = new Color(0xC0, 0x25, 0x25);
    public static final Color DANGER_BG   = new Color(0xFD, 0xEC, 0xEC);
    public static final Color ROW_ALT     = new Color(0xF8, 0xFA, 0xFC);
    public static final Color SIDEBAR_BG  = new Color(0x00, 0x2A, 0x55);

    // ── Fonts ────────────────────────────────────────────────
    public static final Font FONT_H1     = new Font("Georgia", Font.BOLD, 26);
    public static final Font FONT_H2     = new Font("Georgia", Font.BOLD, 18);
    public static final Font FONT_LABEL  = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_BODY   = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_BTN    = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_NAV    = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 12);

    // ── Look & Feel global ──────────────────────────────────
    public static void apply() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Panel.background", LIGHT);
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("OptionPane.messageFont", FONT_BODY);
    }

    // ── Bouton primaire ──────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleBtn(btn, NAVY, WHITE);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(NAVY_LIGHT); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(NAVY); }
        });
        return btn;
    }

    // ── Bouton accent ────────────────────────────────────────
    public static JButton accentButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleBtn(btn, ORANGE, WHITE);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ORANGE_DARK); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(ORANGE); }
        });
        return btn;
    }

    // ── Bouton danger ────────────────────────────────────────
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleBtn(btn, DANGER, WHITE);
        return btn;
    }

    // ── Bouton outline ───────────────────────────────────────
    public static JButton outlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleBtn(btn, WHITE, DARK_TEXT);
        btn.setForeground(DARK_TEXT);
        return btn;
    }

    private static void styleBtn(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(FONT_BTN);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── Champ texte ──────────────────────────────────────────
    public static JTextField styledField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBackground(WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER, 6),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(0, 38));
        return tf;
    }

    public static JTextField styledField(String text) {
        JTextField tf = styledField();
        tf.setText(text);
        return tf;
    }

    // ── Champ lecture seule ──────────────────────────────────
    public static JTextField readOnlyField() {
        JTextField tf = styledField();
        tf.setEditable(false);
        tf.setBackground(new Color(0xEEF3F8));
        tf.setForeground(NAVY);
        tf.setFont(new Font("SansSerif", Font.BOLD, 13));
        return tf;
    }

    // ── ComboBox stylé ───────────────────────────────────────
    public static <T> JComboBox<T> styledCombo() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(FONT_BODY);
        cb.setBackground(WHITE);
        cb.setPreferredSize(new Dimension(0, 38));
        return cb;
    }

    // ── Label de formulaire ──────────────────────────────────
    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(new Color(0x4A, 0x5A, 0x6A));
        return lbl;
    }

    // ── Header de page ───────────────────────────────────────
    public static JPanel pageHeader(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(ORANGE);
                g2.fillRect(0, getHeight()-3, getWidth(), 3);
            }
        };
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(24, 32, 20, 32));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(NAVY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(MUTED);

        left.add(lblTitle);
        left.add(Box.createVerticalStrut(2));
        left.add(lblSub);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    // ── Card panel ───────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    // ── Table professionnelle ────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(0xE8, 0xF0, 0xFA));
        table.setSelectionForeground(DARK_TEXT);
        table.setFocusable(false);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(new Color(0x4A, 0x5A, 0x6A));
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));
        header.setReorderingAllowed(false);

        // Renderer lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(FONT_BODY);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF0, 0xF3, 0xF6)),
                    BorderFactory.createEmptyBorder(0, 16, 0, 16)
                ));
                if (sel) {
                    setBackground(new Color(0xE8, 0xF0, 0xFA));
                    setForeground(DARK_TEXT);
                } else {
                    setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                    setForeground(DARK_TEXT);
                }
                // Badge IN/OUT
                if (v != null && v.toString().equals("IN")) {
                    setForeground(SUCCESS);
                    setFont(new Font("SansSerif", Font.BOLD, 12));
                } else if (v != null && v.toString().equals("OUT")) {
                    setForeground(DANGER);
                    setFont(new Font("SansSerif", Font.BOLD, 12));
                }
                return this;
            }
        });
    }

    // ── ScrollPane propre ────────────────────────────────────
    public static JScrollPane cleanScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(WHITE);
        sp.setBackground(WHITE);
        return sp;
    }

    // ── Stat card (dashboard) ────────────────────────────────
    public static JPanel statCard(String label, String value, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 16));

        JLabel lVal = new JLabel(value);
        lVal.setFont(new Font("Georgia", Font.BOLD, 22));
        lVal.setForeground(NAVY);

        JLabel lLbl = new JLabel(label.toUpperCase());
        lLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lLbl.setForeground(MUTED);

        p.add(lLbl, BorderLayout.NORTH);
        p.add(lVal, BorderLayout.CENTER);
        return p;
    }

    // ── Séparateur ───────────────────────────────────────────
    public static JSeparator thinSep() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        return s;
    }

    // ── RoundBorder utilitaire ───────────────────────────────
    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        public RoundBorder(Color c, int r) { color = c; radius = r; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(1,1,1,1); }
    }
}