package service;
import dao.*;
import model.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StockService {

    private final ProductDAO productDAO = new ProductDAO();
    private final StockMovementDAO movementDAO = new StockMovementDAO();
    private final PendingMovementDAO pendingDAO = new PendingMovementDAO();

    // ── ENTRÉE ──────────────────────────────────────────────
    public void addEntry(int productId, double quantity,
                         double unitPrice, LocalDate date) throws SQLException {
        StockMovement m = new StockMovement(productId, MovementType.IN,
                                            quantity, unitPrice, date);
        movementDAO.insert(m);
    }

    // ── SORTIE ──────────────────────────────────────────────
    public void addExit(int productId, double quantity,
                        LocalDate date) throws SQLException {
        Product product = productDAO.findById(productId);

        // Vérifie stock suffisant
        if (getStock(productId) < quantity)
            throw new IllegalArgumentException("Stock insuffisant.");

        double price = switch (product.getStockMethod()) {
            case FIFO -> calculateFIFO(productId, quantity);
            case LIFO -> calculateLIFO(productId, quantity);
            case CUMP -> calculateCUMP(productId);
        };

        // unitPrice = prix calculé, stocké dans unit_price pour OUT
        StockMovement m = new StockMovement(productId, MovementType.OUT,
                                            quantity, price, date);
        movementDAO.insert(m);
    }

    // ── FIFO : anciens lots d'abord ─────────────────────────
    private double calculateFIFO(int productId, double qtyNeeded) throws SQLException {
        return calculateWeighted(productId, qtyNeeded, true); // ASC
    }

    // ── LIFO : nouveaux lots d'abord ────────────────────────
    private double calculateLIFO(int productId, double qtyNeeded) throws SQLException {
        return calculateWeighted(productId, qtyNeeded, false); // DESC
    }

    // Parcourt les lots IN dans l'ordre et calcule le prix moyen pondéré
    private double calculateWeighted(int productId, double qtyNeeded,
                                     boolean ascending) throws SQLException {
        List<StockMovement> entries = movementDAO.findByProduct(productId, ascending);
        double totalCost = 0;
        double remaining = qtyNeeded;

        for (StockMovement m : entries) {
            if (m.getMovementType() != MovementType.IN) continue;
            if (remaining <= 0) break;

            double take = Math.min(m.getQuantity(), remaining);
            totalCost += take * m.getUnitPrice();
            remaining -= take;
        }

        return totalCost / qtyNeeded; // prix unitaire moyen
    }

    
    public double calculateCUMP(int productId) throws SQLException {
        List<StockMovement> all = movementDAO.findByProduct(productId, true);
        double totalCost = 0, totalQty = 0;
        for (StockMovement m : all) {
            if (m.getMovementType() == MovementType.IN) {
                totalCost += m.getQuantity() * m.getUnitPrice();
                totalQty  += m.getQuantity();
            }
        }
        return totalQty == 0 ? 0 : totalCost / totalQty;
    }

    // ── STOCK ACTUEL ─────────────────────────────────────────
    public double getStock(int productId) throws SQLException {
        List<StockMovement> all = movementDAO.findByProduct(productId, true);
        double stock = 0;
        for (StockMovement m : all) {
            if (m.getMovementType() == MovementType.IN)  stock += m.getQuantity();
            if (m.getMovementType() == MovementType.OUT) stock -= m.getQuantity();
        }
        return stock;
    }

    // ── VALEUR STOCK ─────────────────────────────────────────
    public double getStockValue(int productId) throws SQLException {
        List<StockMovement> all = movementDAO.findByProduct(productId, true);
        double value = 0;
        for (StockMovement m : all) {
            if (m.getMovementType() == MovementType.IN)
                value += m.getQuantity() * m.getUnitPrice();
            if (m.getMovementType() == MovementType.OUT)
                value -= m.getQuantity() * m.getUnitPrice();
        }
        return value;
    }

    // ── ACCÈS DAO (pour UI) ──────────────────────────────────
    public List<Product> getAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    public List<StockMovement> getAllMovements() throws SQLException {
        return movementDAO.findAll();
    }

    public void addProduct(Product p) throws SQLException {
        productDAO.insert(p);
    }

    public void updateProductMinStock(int productId, double minStock) throws SQLException {
        productDAO.updateMinStock(productId, minStock);
    }

    public double getLastUnitPrice(int productId) throws SQLException {
    return movementDAO.findLastUnitPrice(productId);
}

public double getTotalIN(int productId) throws SQLException {
    List<StockMovement> all = movementDAO.findByProduct(productId, true);
    double total = 0;
    for (StockMovement m : all)
        if (m.getMovementType() == MovementType.IN) total += m.getQuantity();
    return total;
}

public Product findProductByName(String name) throws SQLException {
    return productDAO.findByName(name); // plus de stream inutile
}

public String getLastEntryDate(int productId) throws SQLException {
    return movementDAO.findLastEntryDate(productId);
}

public double calculateExitPrice(int id, double qty) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'calculateExitPrice'");
}

public List <Product> searchProducts (String Keyword) throws SQLException {
    return productDAO.findByNameContaining(Keyword);
}

public void addPending(StockMovement m) throws SQLException {
    pendingDAO.insert(m);
}

public List<StockMovement> getPendingMovements() throws SQLException {
    return pendingDAO.findAll();
}

public void deletePending(int id) throws SQLException {
    pendingDAO.delete(id);
}
public void validerMouvement(int pendingId) throws SQLException {
    StockMovement m  = pendingDAO.findById(pendingId);
    StockMovement validated = new StockMovement(m.getProductId(), m.getMovementType(),
                                                m.getQuantity(), m.getUnitPrice(), m.getMovementDate());
    movementDAO.insert(validated);
    pendingDAO.delete(pendingId);
}

}

