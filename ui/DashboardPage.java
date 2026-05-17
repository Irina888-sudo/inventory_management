package ui;

import model.Product;
import model.StockMovement;
import service.StockService;
import utils.UITheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardPage extends JPanel {

    public DashboardPage(StockService service) {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.LIGHT);

        // ── Header ───────────────────────────────────────────
        add(UITheme.pageHeader("Tableau de bord", "Vue globale du stock"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(UITheme.LIGHT);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // ── Stat cards ───────────────────────────────────────
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);

        try {
            List<Product> products = service.getAllProducts();
            double totalValue = 0;
            double totalStock = 0;
            for (Product p : products) {
                double pu    = service.getLastUnitPrice(p.getId());
                double stock = service.getStock(p.getId());
                totalValue  += stock * pu;
                totalStock  += stock;
            }

            cards.add(UITheme.statCard("Produits enregistrés",
                String.valueOf(products.size()), UITheme.NAVY));
            cards.add(UITheme.statCard("Unités en stock",
                String.format("%.0f", totalStock), UITheme.ORANGE));
            cards.add(UITheme.statCard("Valeur totale du stock",
                String.format("%.2f €", totalValue), UITheme.SUCCESS));

        } catch (SQLException e) {
            cards.add(UITheme.statCard("Produits", "—", UITheme.NAVY));
            cards.add(UITheme.statCard("Stock", "—", UITheme.ORANGE));
            cards.add(UITheme.statCard("Valeur", "—", UITheme.SUCCESS));
        }
        body.add(cards, BorderLayout.NORTH);

        // ── Derniers mouvements ──────────────────────────────
        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout(0, 0));
        tableCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        JLabel lblRecent = new JLabel("Derniers mouvements");
        lblRecent.setFont(new Font("Georgia", Font.BOLD, 15));
        lblRecent.setForeground(UITheme.NAVY);
        tableHeader.add(lblRecent, BorderLayout.WEST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(UITheme.thinSep(), BorderLayout.CENTER);

        String[] cols = {"Date", "Produit", "Type", "Quantité", "Prix Unit."};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        try {
            List<StockMovement> movements = service.getAllMovements();
            List<Product> products        = service.getAllProducts();
            int count = 0;
            for (StockMovement m : movements) {
                if (count++ >= 8) break; // derniers 8
                String name = products.stream()
                    .filter(p -> p.getId() == m.getProductId())
                    .map(Product::getName).findFirst().orElse("?");
                model.addRow(new Object[]{
                    m.getMovementDate(), name,
                    m.getMovementType().name(),
                    m.getQuantity(),
                    String.format("%.2f €", m.getUnitPrice())
                });
            }
        } catch (SQLException e) { /* silencieux */ }

        tableCard.add(UITheme.cleanScroll(table), BorderLayout.SOUTH);
        body.add(tableCard, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }
}