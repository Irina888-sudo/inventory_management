package ui;

import model.Product;
import service.StockService;
import utils.UITheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProductListPage extends JPanel {

    public ProductListPage(StockService service) {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.LIGHT);

        add(UITheme.pageHeader("Produits", "Liste de tous les produits en stock"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UITheme.LIGHT);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Card tableau
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout());

        String[] cols = {"Date", "Nom", "Méthode", "Prix Unitaire", "Qté Entrée", "Stock Actuel", "Valeur Stock"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Largeurs colonnes
        int[] widths = {110, 180, 90, 130, 110, 110, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        try {
            List<Product> products = service.getAllProducts();
            for (Product p : products) {
                double pu      = service.getLastUnitPrice(p.getId());
                double stock   = service.getStock(p.getId());
                double totalIN = service.getTotalIN(p.getId());
                String date    = service.getLastEntryDate(p.getId());
                model.addRow(new Object[]{
                    date, p.getName(), p.getStockMethod().name(),
                    String.format("%.2f €", pu), totalIN, stock,
                    String.format("%.2f €", stock * pu)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        card.add(UITheme.cleanScroll(table), BorderLayout.CENTER);
        body.add(card, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }
}