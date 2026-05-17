package ui;

import model.Product;
import model.StockMovement;
import service.StockService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class StockTableView extends JPanel {

    private final StockService service;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel lblCump;
    private final JLabel lblValue;

    public StockTableView(StockService service) {
        this.service = service;
        setLayout(new java.awt.BorderLayout(0, 8));

        // ── Tableau ──────────────────────────────────────────
        String[] cols = {"Date", "Produit", "Type", "Quantité", "Prix Unit.", "Méthode"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);

        // ── Stats bas ────────────────────────────────────────
        JPanel stats = new JPanel();
        lblCump  = new JLabel("CUMP : -");
        lblValue = new JLabel("Valeur Stock : -");
        stats.add(lblCump);
        stats.add(Box.createHorizontalStrut(30));
        stats.add(lblValue);
        add(stats, java.awt.BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        try {
            tableModel.setRowCount(0);
            List<StockMovement> movements = service.getAllMovements();
            List<Product> products       = service.getAllProducts();

            for (StockMovement m : movements) {
                // Trouve le nom du produit
                String prodName = products.stream()
                    .filter(p -> p.getId() == m.getProductId())
                    .map(Product::getName)
                    .findFirst().orElse("?");

                // Trouve la méthode
                String method = products.stream()
                    .filter(p -> p.getId() == m.getProductId())
                    .map(p -> p.getStockMethod().name())
                    .findFirst().orElse("?");

                tableModel.addRow(new Object[]{
                    m.getMovementDate(),
                    prodName,
                    m.getMovementType().name(),
                    m.getQuantity(),
                    String.format("%.2f €", m.getUnitPrice()),
                    method
                });
            }

            // Stats globales (tous produits)
            double totalValue = 0, totalCump = 0;
            int count = 0;
            for (Product p : products) {
                totalValue += service.getStockValue(p.getId());
                totalCump  += service.calculateCUMP(p.getId());
                count++;
            }
            lblCump.setText(String.format("CUMP : %.2f €", count > 0 ? totalCump / count : 0));
            lblValue.setText(String.format("Valeur Stock : %.2f €", totalValue));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}