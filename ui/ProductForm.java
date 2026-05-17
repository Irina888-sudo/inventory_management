package ui;
import model.Product;
import model.StockMethod;
import service.StockService;
import utils.FormBuilder;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ProductForm extends JDialog {

    public ProductForm(StockService service, MainFrame parent) {
        super(parent, "Nouveau Produit", true);
        setSize(350, 250);
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel();
        // Reflection génère les champs depuis Product.class
        Map<String, JTextField> fields = FormBuilder.buildForm(Product.class, form);

        // stock_method → JComboBox à la place du JTextField
        JComboBox<StockMethod> cbMethod = new JComboBox<>(StockMethod.values());
        // Remplace le champ stockMethod généré par Reflection
        form.remove(fields.get("stockMethod"));
        form.add(cbMethod);

        add(new JScrollPane(form), BorderLayout.CENTER);

        JButton btnSave = new JButton("Enregistrer");
        add(btnSave, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            try {
                Product p = new Product();
                p.setName(fields.get("name").getText().trim());
                p.setStockMethod((StockMethod) cbMethod.getSelectedItem());
                p.setUnit(fields.get("unit").getText().trim());
                service.addProduct(p);
                parent.refresh();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        setLocationRelativeTo(parent);
    }
}