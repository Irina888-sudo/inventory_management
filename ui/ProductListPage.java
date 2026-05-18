package ui;

import model.*;
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
        JTextField tfSearch = UITheme.styledField();
        JButton btnSearch = UITheme.primaryButton("Rechercher");

        add(UITheme.pageHeader("Produits", "Liste de tous les produits en stock"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UITheme.LIGHT);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Card tableau
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);
       

        String[] cols = {"Date", "Nom", "Méthode", "Prix Unitaire", "Qté Entrée", "Stock Actuel", "Valeur Stock", "Stock minimum", "Statut"};
        final DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) return c;
                Object status = getValueAt(row, 8);
                boolean alerte = status != null && status.toString().contains("ALERTE");
                c.setBackground(alerte ? UITheme.DANGER_BG : (row % 2 == 0 ? UITheme.WHITE : UITheme.ROW_ALT));
                c.setForeground(alerte ? UITheme.DANGER : UITheme.DARK_TEXT);
                return c;
            }
        };
        UITheme.styleTable(table);

        // Largeurs colonnes
    int[] widths = {110, 180, 90, 130, 110, 110, 130, 120, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        try {
            List<Product> products = service.getAllProducts();
            for (Product p : products) {
                double pu      = service.getLastUnitPrice(p.getId());
                double stock   = service.getStock(p.getId());
                double totalIN = service.getTotalIN(p.getId());
                String date    = service.getLastEntryDate(p.getId());
                double minStock = p.getMinStock();
                boolean alerte  = minStock > 0 && stock <=minStock;
                model.addRow(new Object[]{
                    date, p.getName(), p.getStockMethod().name(),
                    String.format("%,.0f Ar", pu), totalIN, stock,
                    String.format("%,.0f Ar", stock * pu),
                    minStock > 0 ? String.format("%,.0f", minStock) : "-",
                    alerte ? "⚠ ALERTE" : "OK"
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