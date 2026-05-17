package dao;
import model.*;
import dao.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockMovementDAO {

    public void insert(StockMovement m) throws SQLException {
        String sql = "INSERT INTO stock_movements(product_id, movement_type, quantity, unit_price, movement_date) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.getProductId());
            ps.setString(2, m.getMovementType().name());
            ps.setDouble(3, m.getQuantity());
            ps.setDouble(4, m.getUnitPrice());
            ps.setDate(5, Date.valueOf(m.getMovementDate()));
            ps.executeUpdate();
        }
    }

    // Tous les mouvements d'un produit, ordre ASC (FIFO) ou DESC (LIFO)
    public List<StockMovement> findByProduct(int productId, boolean ascending) throws SQLException {
        String order = ascending ? "ASC" : "DESC";
        String sql = "SELECT * FROM stock_movements WHERE product_id = ? ORDER BY movement_date " + order;
        return query(sql, productId);
    }

    // Tous les mouvements (historique page principale)
    public List<StockMovement> findAll() throws SQLException {
        String sql = "SELECT * FROM stock_movements ORDER BY movement_date DESC";
        return query(sql, -1);
    }

    private List<StockMovement> query(String sql, int productId) throws SQLException {
        List<StockMovement> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (productId != -1) ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockMovement m = new StockMovement();
                m.setId(rs.getInt("id"));
                m.setProductId(rs.getInt("product_id"));
                m.setMovementType(MovementType.valueOf(rs.getString("movement_type")));
                m.setQuantity(rs.getDouble("quantity"));
                m.setUnitPrice(rs.getDouble("unit_price"));
                m.setMovementDate(rs.getDate("movement_date").toLocalDate());
                list.add(m);
            }
        }
        return list;
    }

    public double findLastUnitPrice(int productId) throws SQLException {
    String sql = "SELECT unit_price FROM stock_movements WHERE product_id=? AND movement_type='IN' ORDER BY movement_date DESC LIMIT 1";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, productId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDouble("unit_price");
    }
    return 0;
}

    public String findLastEntryDate(int productId) throws SQLException {
    String sql = "SELECT movement_date FROM stock_movements " +
                 "WHERE product_id=? AND movement_type='IN' " +
                 "ORDER BY movement_date DESC LIMIT 1";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, productId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDate("movement_date").toString();
    }
    return "-";
}
}