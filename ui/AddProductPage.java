package ui;

import model.*;
import service.StockService;
import utils.UITheme;
import utils.FormBuilder;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class AddProductPage extends JPanel {

    public AddProductPage(StockService service, MainFrame frame) {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.LIGHT);

        add(UITheme.pageHeader(
            "Ajouter un Produit (IN)",
            "Enregistrer une nouvelle entrée de stock"
        ), BorderLayout.NORTH);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.LIGHT);
        outer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(580, 560));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        // Titre
        JLabel title = new JLabel("Informations du produit");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(UITheme.NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // Sous-titre Reflection
        JLabel subtitle = new JLabel("⚡ Formulaire généré par Java Reflection");
        subtitle.setFont(new Font("SansSerif", Font.ITALIC, 11));
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        // ── Formulaire généré par Reflection ────────────────
        // Product.class a : id, name, stockMethod, unit
        // On exclut "id" → généré auto par PostgreSQL
        JPanel form = new JPanel(new GridLayout(0, 2, 16, 14));
        form.setOpaque(false);

        Map<String, JTextField> reflFields =
            FormBuilder.buildForm(Product.class, form, "id");
        // reflFields contient : "name", "stockMethod", "unit"

        // stockMethod → remplace JTextField par JComboBox
        JComboBox<StockMethod> cbMethod = UITheme.styledCombo();
        for (StockMethod m : StockMethod.values()) cbMethod.addItem(m);

        // Retire le JTextField "stockMethod" du form et met le ComboBox
        JTextField tfMethodPlaceholder = reflFields.get("stockMethod");
        replaceComponent(form, tfMethodPlaceholder, cbMethod);

        // Champs manuels (non présents dans Product.class)
        JTextField tfDate  = UITheme.styledField(LocalDate.now().toString());
        JTextField tfQty   = UITheme.styledField();
        JTextField tfPU    = UITheme.styledField();
        JTextField tfStock = UITheme.readOnlyField();
        JTextField tfPrix  = UITheme.readOnlyField();

        form.add(UITheme.formLabel("Date (YYYY-MM-DD)")); form.add(tfDate);
        form.add(UITheme.formLabel("Prix Unitaire (€)")); form.add(tfPU);
        form.add(UITheme.formLabel("Quantité"));          form.add(tfQty);
        form.add(UITheme.formLabel("Stock Actuel"));      form.add(tfStock);
        form.add(UITheme.formLabel("Valeur du Stock"));   form.add(tfPrix);

        // ── Calcul automatique ───────────────────────────────
        Runnable calc = () -> {
            try {
                double pu  = Double.parseDouble(tfPU.getText().trim());
                double qty = Double.parseDouble(tfQty.getText().trim());
                tfPrix.setText(String.format("%.2f €", pu * qty));
                String name = reflFields.get("name").getText().trim();
                Product existing = service.findProductByName(name);
                double cur = existing != null
                    ? service.getStock(existing.getId()) : 0;
                tfStock.setText(String.format("%.0f", cur + qty));
            } catch (Exception ignored) {}
        };
        tfPU.addActionListener(e -> calc.run());
        tfQty.addActionListener(e -> calc.run());

        // ── Bouton ───────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 16));
        btnRow.setOpaque(false);
        JButton btnSave = UITheme.primaryButton("Enregistrer l'entrée");
        btnRow.add(btnSave);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        content.add(titleBox,  BorderLayout.NORTH);
        content.add(form,      BorderLayout.CENTER);
        content.add(btnRow,    BorderLayout.SOUTH);

        card.add(content, BorderLayout.CENTER);
        outer.add(card);
        add(outer, BorderLayout.CENTER);
       

        // ── Action enregistrer ───────────────────────────────
        btnSave.addActionListener(e -> {
            try {
                // Récupère les valeurs via la Map Reflection
                String name   = reflFields.get("name").getText().trim();
                String unit   = reflFields.get("unit").getText().trim();
                double minStock = 0;
                try {
                    String minStockText = reflFields.get("minStock").getText().trim();
                    if (!minStockText.isEmpty())
                        minStock = Double.parseDouble(minStockText);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Stock minimum invalide.");
                }
                StockMethod method = (StockMethod) cbMethod.getSelectedItem();
                double pu     = Double.parseDouble(tfPU.getText().trim());
                double qty    = Double.parseDouble(tfQty.getText().trim());
                LocalDate date = LocalDate.parse(tfDate.getText().trim());

                Product p = service.findProductByName(name);
                if (p == null) {
                    p = new Product(0, name, method, unit);
                    p.setMinStock(minStock);
                    service.addProduct(p);
                    p = service.findProductByName(name);
                } else if (!reflFields.get("minStock").getText().trim().isEmpty()) {
                    service.updateProductMinStock(p.getId(), minStock);
                }
                service.addEntry(p.getId(), qty, pu, date);
                JOptionPane.showMessageDialog(this, "✅  Entrée enregistrée !");
                frame.showPage(new DashboardPage(service));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌  " + ex.getMessage());
            }
        });
    }

    // Remplace un composant dans un JPanel GridLayout
    private void replaceComponent(JPanel panel, JComponent old, JComponent replacement) {
        Component[] comps = panel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] == old) {
                panel.remove(i);
                panel.add(replacement, i);
                break;
            }
        }
    }
}