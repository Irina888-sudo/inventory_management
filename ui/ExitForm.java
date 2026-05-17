package ui;

import model.Product;
import service.StockService;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ExitForm extends JDialog {

    public ExitForm(StockService service, MainFrame parent) {
        super(parent, "Retirer Sortie (OUT)", true);
        setSize(350, 220);
        setLayout(new GridLayout(0, 2, 8, 8));

        add(new JLabel("Produit :"));
        JComboBox<Product> cbProduct = new JComboBox<>();
        try {
            List<Product> products = service.getAllProducts();
            products.forEach(cbProduct::addItem);
        } catch (SQLException e) { e.printStackTrace(); }
        add(cbProduct);

        add(new JLabel("Date (YYYY-MM-DD) :"));
        JTextField tfDate = new JTextField(LocalDate.now().toString());
        add(tfDate);

        add(new JLabel("Quantité :"));
        JTextField tfQty = new JTextField();
        add(tfQty);

        // Info : prix calculé automatiquement
        add(new JLabel("Prix :"));
        add(new JLabel("calculé automatiquement"));

        JButton btnSave = new JButton("Enregistrer");
        add(new JLabel());
        add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                Product selected = (Product) cbProduct.getSelectedItem();
                service.addExit(
                    selected.getId(),
                    Double.parseDouble(tfQty.getText().trim()),
                    LocalDate.parse(tfDate.getText().trim())
                );
                parent.refresh();
                dispose();
            } catch (Exception ex) {
                // Affiche "Stock insuffisant." si besoin
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        setLocationRelativeTo(parent);
    }
}
