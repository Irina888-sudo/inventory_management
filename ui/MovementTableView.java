package ui;

import model.Product;
import model.StockMovement;
import service.StockService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class MovementTableView extends JPanel {

    private final StockService service;
    private final DefaultTableModel tableModel;

    // Panneau infos produit (droite)
    private final JLabel lblProduit  = new JLabel("-");
    private final JLabel lblStock    = new JLabel("-");
    private final JLabel lblCump     = new JLabel("-");
    private final JLabel lblValeur   = new JLabel("-");

    public MovementTableView(StockService service) {
        this.service = service;
        setLayout(new java.awt.BorderLayout(8, 0));

        // ── Tableau historique (gauche) ───────────────────────
        String[] cols = {"Date", "Produit", "Type", "Quantité", "Prix Unit."};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);

        // Clic sur ligne → infos produit à droite
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                showProductInfo(table.getSelectedRow());
        });

        // ── Panneau infos produit (droite) ───────────────────
        JPanel info = new JPanel(new GridLayout(0, 1, 4, 4));
        info.setBorder(BorderFactory.createTitledBorder("Infos Produit"));
        info.setPreferredSize(new java.awt.Dimension(200, 0));
        info.add(new JLabel("Produit :")); info.add(lblProduit);
        info.add(new JLabel("Stock actuel :")); info.add(lblStock);
        info.add(new JLabel("CUMP :")); info.add(lblCump);
        info.add(new JLabel("Valeur :")); info.add(lblValeur);
        add(info, java.awt.BorderLayout.EAST);

        refresh();
    }

    public void refresh() {
        try {
            tableModel.setRowCount(0);
            List<StockMovement> movements = service.getAllMovements();
            List<Product> products        = service.getAllProducts();

            for (StockMovement m : movements) {
                String name = products.stream()
                    .filter(p -> p.getId() == m.getProductId())
                    .map(Product::getName).findFirst().orElse("?");

                tableModel.addRow(new Object[]{
                    m.getMovementDate(),
                    name,
                    m.getMovementType().name(),
                    m.getQuantity(),
                    String.format("%.2f €", m.getUnitPrice())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void showProductInfo(int row) {
        try {
            String prodName = (String) tableModel.getValueAt(row, 1);
            List<Product> products = service.getAllProducts();
            Product p = products.stream()
                .filter(pr -> pr.getName().equals(prodName))
                .findFirst().orElse(null);
            if (p == null) return;

            lblProduit.setText(p.getName());
            lblStock.setText(String.valueOf(service.getStock(p.getId())));
            lblCump.setText(String.format("%.2f €", service.calculateCUMP(p.getId())));
            lblValeur.setText(String.format("%.2f €", service.getStockValue(p.getId())));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}
