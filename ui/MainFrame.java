package ui;
import service.StockService;
import ui.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final StockService service = new StockService();
    final ProductTableView productView;
    final MovementTableView movementView;

    public MainFrame() {
        setTitle("Inventory Management");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ── Toolbar ──────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd     = new JButton("+ Ajouter (IN)");
        JButton btnRemove  = new JButton("- Retirer (OUT)");
        JButton btnProduct = new JButton("+ Nouveau Produit");
        toolbar.add(btnAdd);
        toolbar.add(btnRemove);
        toolbar.add(btnProduct);
        add(toolbar, BorderLayout.NORTH);

        // ── Onglets ──────────────────────────────────────────
        productView  = new ProductTableView(service);
        movementView = new MovementTableView(service);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Produits",    productView);
        tabs.addTab("Mouvements",  movementView);
        add(tabs, BorderLayout.CENTER);

        // ── Actions ──────────────────────────────────────────
        btnAdd.addActionListener(e -> {
            new EntryForm(service, this).setVisible(true);
        });
        btnRemove.addActionListener(e -> {
            new ExitForm(service, this).setVisible(true);
        });
        btnProduct.addActionListener(e -> {
            new ProductForm(service, this).setVisible(true);
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Appelé après chaque action pour rafraîchir les deux onglets
    public void refresh() {
        productView.refresh();
        movementView.refresh();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}