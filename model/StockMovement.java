package model;
import dao.*;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StockMovement {
    private int id;
    private int productId;
    private MovementType movementType;
    private double quantity;
    private double unitPrice;    // IN seulement
    private LocalDate movementDate;

    public StockMovement() {}
    public StockMovement(int productId, MovementType movementType,
                         double quantity, double unitPrice, LocalDate movementDate) {
        this.productId = productId; this.movementType = movementType;
        this.quantity = quantity; this.unitPrice = unitPrice;
        this.movementDate = movementDate;
    }
    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType t) { this.movementType = t; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public LocalDate getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDate movementDate) { this.movementDate = movementDate; }


    public String findLastEntryDate(int productId) throws SQLException {
    String sql = "SELECT movement_date FROM stock_movements WHERE product_id=? AND movement_type='IN' ORDER BY movement_date DESC LIMIT 1";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, productId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDate("movement_date").toString();
    }
    return "-";
}
}