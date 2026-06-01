package ui;

import model.*;
import service.StockService;
import utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MovementFormPage extends JPanel {

    public MovementFormPage(StockService service, MainFrame frame) {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.LIGHT);

        // ── Header ───────────────────────────────────────────
        add(UITheme.pageHeader(
            "Nouveau Mouvement",
            "Enregistrer une entrée ou une sortie"
        ), BorderLayout.NORTH);

        // ── Outer centre ─────────────────────────────────────
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.LIGHT);
        outer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // ── Card ─────────────────────────────────────────────
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(580, 520));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        // ── Titre card ───────────────────────────────────────
        JLabel title = new JLabel("Informations du mouvement");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(UITheme.NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // ── Formulaire ───────────────────────────────────────
        JPanel form = new JPanel(new GridLayout(0, 2, 16, 14));
        form.setOpaque(false);

        // Type IN/OUT
        JComboBox<String> cbType = UITheme.styledCombo();
        cbType.addItem("IN");
        cbType.addItem("OUT");

        // Produit — liste déroulante produits existants
        JComboBox<Product> cbProduit = UITheme.styledCombo();
        try {
            List<Product> products = service.getAllProducts();
            products.forEach(cbProduit::addItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Date
        JTextField tfDate = UITheme.styledField(LocalDate.now().toString());

        // Quantité
        JTextField tfQty = UITheme.styledField();

        // Prix unitaire (visible IN / caché OUT)
        JTextField tfPrix = UITheme.styledField();

        // Stock actuel — lecture seule
        JTextField tfStock = UITheme.readOnlyField();

        // Valeur — lecture seule
        JTextField tfValeur = UITheme.readOnlyField();

        // ── Ajout champs au formulaire ───────────────────────
        form.add(UITheme.formLabel("Type"));             form.add(cbType);
        form.add(UITheme.formLabel("Produit"));          form.add(cbProduit);
        form.add(UITheme.formLabel("Date (YYYY-MM-DD)")); form.add(tfDate);
        form.add(UITheme.formLabel("Quantité"));         form.add(tfQty);
        form.add(UITheme.formLabel("Prix Unitaire (€)")); form.add(tfPrix);
        form.add(UITheme.formLabel("Stock Actuel"));     form.add(tfStock);
        form.add(UITheme.formLabel("Valeur"));           form.add(tfValeur);

        // ── Bouton Enregistrer ───────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 16));
        btnRow.setOpaque(false);
        JButton btnSave = UITheme.primaryButton("Enregistrer");
        btnRow.add(btnSave);

        // ── Assemblage card ──────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        content.add(title,  BorderLayout.NORTH);
        content.add(form,   BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);
        card.add(content,   BorderLayout.CENTER);
        outer.add(card);
        add(outer, BorderLayout.CENTER);



        cbType.addActionListener(e -> {
           String selected = (String) cbType.getSelectedItem();
              if (selected.equals("IN")) {
                  tfPrix.setVisible(true);
              } else {
                  tfPrix.setVisible(false);
              }
        });

Runnable calc = () -> {
    try {
        Product p  = (Product) cbProduit.getSelectedItem();
        if (p == null) return;
        double qty = Double.parseDouble(tfQty.getText().trim());

        if (cbType.getSelectedItem().equals("IN")) {
            double prix = Double.parseDouble(tfPrix.getText().trim());
            tfStock.setText(String.valueOf(
                service.getStock(p.getId()) + qty));
            tfValeur.setText(String.valueOf(qty * prix));
        } else {
            double exitPrice = service.calculateExitPrice(p.getId(), qty);
            tfStock.setText(String.valueOf(
                service.getStock(p.getId()) - qty));
            tfValeur.setText(String.valueOf(qty * exitPrice));
        }
    } catch (Exception ignored) {}
};

cbProduit.addActionListener(e -> calc.run());
tfQty.addActionListener(e -> calc.run());
        

       btnSave.addActionListener(e -> {
    try {
        // Récupère les valeurs
        Product p      = (Product) cbProduit.getSelectedItem();
        String type    = (String) cbType.getSelectedItem();
        LocalDate date = LocalDate.parse(tfDate.getText().trim());

        // Validation 1 — quantité vide
        if (tfQty.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La quantité est requise.", "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        double qty = Double.parseDouble(tfQty.getText().trim());
        double pu  = type.equals("IN") ? Double.parseDouble(tfPrix.getText().trim()) : 0;

        // Validation 2 — quantité négative
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this,
                "La quantité doit être positive.", "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validation 3 — stock insuffisant
        if (type.equals("OUT") && qty > service.getStock(p.getId())) {
            JOptionPane.showMessageDialog(this,
                "Stock insuffisant.", "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Enregistrement
        StockMovement pending = new StockMovement(
    p.getId(),
    MovementType.valueOf(type),
    qty,
    type.equals("IN") ? pu : 0,
    date
);
service.addPending(pending);

        JOptionPane.showMessageDialog(this,
    "✅ Mouvement ajouté en attente !");
frame.showPage(new PendingMovementPage(service, frame));

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "❌ " + ex.getMessage(), "Erreur",
            JOptionPane.ERROR_MESSAGE);
    }
});
    }
}