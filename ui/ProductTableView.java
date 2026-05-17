package ui;
import model.Product;
import service.StockService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class ProductTableView extends JPanel {

    private final StockService service;
    private final DefaultTableModel model;

    public ProductTableView(StockService service) {
        this.service = service;
        setLayout(new java.awt.BorderLayout());

        String[] cols = {"Nom", "Prix Unitaire (dernier IN)", "Prix Stock", "Quantité Entrée", "Stock Actuel"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        add(new JScrollPane(new JTable(model)), java.awt.BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        try {
            model.setRowCount(0);
            List<Product> products = service.getAllProducts();

            for (Product p : products) {
                double lastPU    = service.getLastUnitPrice(p.getId());
                double stockQty  = service.getStock(p.getId());
                double totalIN   = service.getTotalIN(p.getId());   // voir ajout ci-dessous
                double prixStock = stockQty * lastPU;

                model.addRow(new Object[]{
                    p.getName(),
                    String.format("%.2f €", lastPU),
                    String.format("%.2f €", prixStock),
                    totalIN,
                    stockQty
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}