package ui;

import service.StockService;
import utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    public final StockService service = new StockService();
    private final JPanel contentArea;
    private JButton activeNavBtn = null;

    public MainFrame() {
        UITheme.apply();
        setTitle("Inventory Management");
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(UITheme.LIGHT);

        // ── Sidebar ──────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        // ── Content area ─────────────────────────────────────
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.LIGHT);
        add(contentArea, BorderLayout.CENTER);

        // Page par défaut
        showPage(new DashboardPage(service));

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Accent line droite
                g2.setColor(UITheme.ORANGE);
                g2.fillRect(getWidth()-2, 0, 2, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setOpaque(false);

        // Logo
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(28, 24, 28, 24));
        JLabel logo = new JLabel("INVENTORY");
        logo.setFont(new Font("Georgia", Font.BOLD, 18));
        logo.setForeground(UITheme.WHITE);
        JLabel logoSub = new JLabel("Management System");
        logoSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        logoSub.setForeground(new Color(0xAA, 0xBB, 0xCC));
        JPanel logoText = new JPanel();
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));
        logoText.setOpaque(false);
        logoText.add(logo);
        logoText.add(logoSub);
        logoPanel.add(logoText, BorderLayout.CENTER);

        // Trait orange sous logo
        JPanel logoBox = new JPanel(new BorderLayout());
        logoBox.setOpaque(false);
        logoBox.add(logoPanel, BorderLayout.CENTER);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xFF, 0xA5, 0x00, 80));
        logoBox.add(sep, BorderLayout.SOUTH);

        sidebar.add(logoBox);
        sidebar.add(Box.createVerticalStrut(16));

        // Navigation items
        String[][] navItems = {
            {"📋", "Tableau de bord"},
            {"📦", "Produits"},
            {"➕", "Ajouter (IN)"},
            {"➖", "Sortir (OUT)"},
            {"🕐", "Historique"},
        };

        for (String[] item : navItems) {
            JButton btn = navButton(item[0] + "  " + item[1]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));

            btn.addActionListener(e -> {
                setActiveNav(btn);
                switch (item[1]) {
                    case "Tableau de bord" -> showPage(new DashboardPage(service));
                    case "Produits"        -> showPage(new ProductListPage(service));
                    case "Ajouter (IN)"   -> showPage(new AddProductPage(service, this));
                    case "Sortir (OUT)"   -> showPage(new ExitProductPage(service, MainFrame.this));
                    case "Historique"     -> showPage(new HistoryPage(service));
                }
            });

            // Active le premier par défaut
            if (item[1].equals("Tableau de bord")) {
                setActiveNav(btn);
            }
        }

        sidebar.add(Box.createVerticalGlue());

        // Footer sidebar
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(16, 24, 20, 24));
        JLabel version = new JLabel("v1.0  •  Java Swing");
        version.setFont(new Font("SansSerif", Font.PLAIN, 10));
        version.setForeground(new Color(0x66, 0x77, 0x88));
        footer.add(version, BorderLayout.CENTER);
        sidebar.add(footer);

        return sidebar;
    }

    private JButton navButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getBackground().equals(UITheme.ORANGE)) {
                    g2.setColor(new Color(0xFF, 0xA5, 0x00, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(UITheme.ORANGE);
                    g2.fillRect(0, 0, 3, getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(new Color(0xBB, 0xCC, 0xDD));
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(UITheme.ORANGE))
                    btn.setForeground(UITheme.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(UITheme.ORANGE))
                    btn.setForeground(new Color(0xBB, 0xCC, 0xDD));
            }
        });
        return btn;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(UITheme.SIDEBAR_BG);
            activeNavBtn.setForeground(new Color(0xBB, 0xCC, 0xDD));
            activeNavBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        }
        activeNavBtn = btn;
        btn.setBackground(UITheme.ORANGE);
        btn.setForeground(UITheme.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    public void showPage(JPanel page) {
        contentArea.removeAll();
        contentArea.add(page, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    public void refresh() {
        // Rafraîchit la page active
        Component[] comps = contentArea.getComponents();
        if (comps.length > 0 && comps[0] instanceof ProductListPage)
            showPage(new ProductListPage(service));
        else if (comps.length > 0 && comps[0] instanceof HistoryPage)
            showPage(new HistoryPage(service));
        else
            showPage(new DashboardPage(service));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}