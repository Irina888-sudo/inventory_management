package ui;

import model.Product;
import service.StockService;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EntryForm extends JDialog {

    public EntryForm(StockService service, MainFrame parent) {
        super(parent, "Ajouter Entrée (IN)", true);
        setSize(350, 280);
        setLayout(new GridLayout(0, 2, 8, 8));

        // Produit
        add(new JLabel("Produit :"));
        JComboBox<Product> cbProduct = new JComboBox<>();
        try {
            List<Product> products = service.getAllProducts();
            products.forEach(cbProduct::addItem); // toString() → nom
        } catch (SQLException e) { e.printStackTrace(); }
        add(cbProduct);

        // Date
        add(new JLabel("Date (YYYY-MM-DD) :"));
        JTextField tfDate = new JTextField(LocalDate.now().toString());
        add(tfDate);

        // Quantité
        add(new JLabel("Quantité :"));
        JTextField tfQty = new JTextField();
        add(tfQty);

        // Prix unitaire
        add(new JLabel("Prix unitaire :"));
        JTextField tfPrice = new JTextField();
        add(tfPrice);

        JButton btnSave = new JButton("Enregistrer");
        add(new JLabel());
        add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                Product selected = (Product) cbProduct.getSelectedItem();
                service.addEntry(
                    selected.getId(),
                    Double.parseDouble(tfQty.getText().trim()),
                    Double.parseDouble(tfPrice.getText().trim()),
                    LocalDate.parse(tfDate.getText().trim())
                );
                parent.refresh();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        setLocationRelativeTo(parent);
    }
}