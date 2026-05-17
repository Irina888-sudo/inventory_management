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

public class HistoryPage extends JPanel {

    public HistoryPage(StockService service) {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.LIGHT);

        add(UITheme.pageHeader("Historique des Mouvements", "Toutes les entrées et sorties enregistrées"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UITheme.LIGHT);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout());

        String[] cols = {"Date", "Produit", "Type", "Quantité", "Prix Unitaire", "Méthode"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Largeurs
        int[] widths = {110, 180, 80, 100, 120, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        try {
            List<StockMovement> movements = service.getAllMovements();
            List<Product> products        = service.getAllProducts();
            for (StockMovement m : movements) {
                Product prod = products.stream()
                    .filter(p -> p.getId() == m.getProductId())
                    .findFirst().orElse(null);
                String name   = prod != null ? prod.getName() : "?";
                String method = prod != null ? prod.getStockMethod().name() : "?";
                model.addRow(new Object[]{
                    m.getMovementDate(), name,
                    m.getMovementType().name(),
                    m.getQuantity(),
                    String.format("%.2f €", m.getUnitPrice()),
                    method
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        card.add(UITheme.cleanScroll(table), BorderLayout.CENTER);
        body.add(card, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        // ── Stats globales ───────────────────────────────────
JPanel statsBar = new JPanel(new GridLayout(1, 2, 16, 0));
statsBar.setOpaque(false);
statsBar.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

try {
    List<Product> products = service.getAllProducts();
    double totalValue = 0, totalCump = 0;
    int count = 0;
    for (Product p : products) {
        totalValue += service.getStockValue(p.getId());
        totalCump  += service.calculateCUMP(p.getId());
        count++;
    }
    statsBar.add(UITheme.statCard("CUMP Global",
        String.format("%.2f €", count > 0 ? totalCump / count : 0),
        UITheme.NAVY));
    statsBar.add(UITheme.statCard("Valeur Totale du Stock",
        String.format("%.2f €", totalValue),
        UITheme.ORANGE));
} catch (SQLException e) { /* silencieux */ }

body.add(statsBar, BorderLayout.SOUTH);
    }

    
}