package ui;

import model.Product;
import service.StockService;
import utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ExitProductPage extends JPanel {

    public ExitProductPage(StockService service, MainFrame frame) {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.LIGHT);

        add(UITheme.pageHeader("Sortir un Produit (OUT)", "Enregistrer une sortie de stock"), BorderLayout.NORTH);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.LIGHT);
        outer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(560, 460));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel title = new JLabel("Sélectionner un produit");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(UITheme.NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel form = new JPanel(new GridLayout(0, 2, 16, 14));
        form.setOpaque(false);

        JTextField tfDate  = UITheme.styledField(LocalDate.now().toString());
        JComboBox<Product> cbProd = UITheme.styledCombo();
        JTextField tfQty   = UITheme.styledField();
        JTextField tfPU    = UITheme.readOnlyField();
        JTextField tfStock = UITheme.readOnlyField();
        JTextField tfPrix  = UITheme.readOnlyField();

        try {
            List<Product> products = service.getAllProducts();
            products.forEach(cbProd::addItem);
        } catch (Exception e) { e.printStackTrace(); }

        form.add(UITheme.formLabel("Date (YYYY-MM-DD)")); form.add(tfDate);
        form.add(UITheme.formLabel("Produit"));           form.add(cbProd);
        form.add(UITheme.formLabel("Quantité"));          form.add(tfQty);
        form.add(UITheme.formLabel("Prix unitaire"));     form.add(tfPU);
        form.add(UITheme.formLabel("Stock après sortie")); form.add(tfStock);
        form.add(UITheme.formLabel("Valeur sortie"));     form.add(tfPrix);

        Runnable calc = () -> {
            try {
                Product p  = (Product) cbProd.getSelectedItem();
                if (p == null) return;
                double qty = Double.parseDouble(tfQty.getText().trim());
                double pu  = service.calculateExitPrice(p.getId(), qty);
                tfPU.setText(String.format("%.2f €", pu));
                tfStock.setText(String.format("%.0f", service.getStock(p.getId()) - qty));
                tfPrix.setText(String.format("%.2f €", pu * qty));
            } catch (Exception ignored) {}
        };
        cbProd.addActionListener(e -> calc.run());
        tfQty.addActionListener(e -> calc.run());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 16));
        btnRow.setOpaque(false);
        JButton btnSave = UITheme.dangerButton("Enregistrer la sortie");
        btnRow.add(btnSave);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        content.add(title, BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);
        card.add(content, BorderLayout.CENTER);

        outer.add(card);
        add(outer, BorderLayout.CENTER);

        btnSave.addActionListener(e -> {
            try {
                Product p  = (Product) cbProd.getSelectedItem();
                double qty = Double.parseDouble(tfQty.getText().trim());
                LocalDate date = LocalDate.parse(tfDate.getText().trim());
                service.addExit(p.getId(), qty, date);
                JOptionPane.showMessageDialog(this, "✅  Sortie enregistrée !");
                frame.showPage(new DashboardPage(service));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌  " + ex.getMessage());
            }
        });
    }
}