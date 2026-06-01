package ui;

import utils.*;
import model.*;
import service.StockService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*; 
import java.sql.SQLException;
import java.util.List;

public class PendingMovementPage extends JPanel {

    private final StockService service;
    private final MainFrame frame;
    private final DefaultTableModel model;
    private final JTable table;


    public PendingMovementPage(StockService service, MainFrame frame) {
        this.service = service;
        this.frame   = frame;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.LIGHT);

        // ── Header ───────────────────────────────────────────
        add(UITheme.pageHeader(
            "Mouvements en attente",
            "Valider ou supprimer les mouvements avant application"
        ), BorderLayout.NORTH);

        // ── Body ─────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.LIGHT);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // ── Colonnes ─────────────────────────────────────────
        String[] cols = {"ID", "Produit", "Type", "Quantité",
                         "Prix Unit.", "Date", "Valider", "Supprimer"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UITheme.styleTable(table);

        // Largeurs colonnes
        int[] widths = {50, 160, 70, 90, 100, 110, 90, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // ── Boutons par ligne ─────────────────────────────────
        // Colonne Valider
        table.getColumn("Valider").setCellRenderer(new ButtonRenderer("✅ Valider", UITheme.SUCCESS));
        table.getColumn("Valider").setCellEditor(new ButtonEditor(
            new JCheckBox(), "✅ Valider", UITheme.SUCCESS, row -> {
                int id = (int) model.getValueAt(row, 0);
                try {
                    service.validerMouvement(id);
                    refresh();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        ));

        // Colonne Supprimer
        table.getColumn("Supprimer").setCellRenderer(new ButtonRenderer("🗑 Supprimer", UITheme.DANGER));
        table.getColumn("Supprimer").setCellEditor(new ButtonEditor(
            new JCheckBox(), "🗑 Supprimer", UITheme.DANGER, row -> {
                int id = (int) model.getValueAt(row, 0);
                try {       
                    service.deletePending(id);
                    refresh();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        ));

        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout());
        card.add(UITheme.cleanScroll(table), BorderLayout.CENTER);

        body.add(card, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        // Chargement initial
        refresh();
    }

    // Recharge la liste depuis la base
    public void refresh() {
        try {
            model.setRowCount(0);
            List<StockMovement> list = service.getPendingMovements();
            List<Product> products   = service.getAllProducts();

            for (StockMovement m : list) {
                String name = products.stream()
                    .filter(p -> p.getId() == m.getProductId())
                    .map(Product::getName).findFirst().orElse("?");

                model.addRow(new Object[]{
                    m.getId(), name,
                    m.getMovementType().name(),
                    m.getQuantity(),
                    String.format("%,.0f Ar", m.getUnitPrice()),
                    m.getMovementDate(),
                    "✅ Valider",
                    "🗑 Supprimer"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}