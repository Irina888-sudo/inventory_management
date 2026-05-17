package dao;

import model.MovementType;
import model.StockMovement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockMovementDAO {

    // Enregistre un mouvement IN ou OUT
    public void insert(StockMovement m) throws SQLException {
        String sql = "INSERT INTO stock_movements" +
                     "(product_id, movement_type, quantity, unit_price, movement_date)" +
                     " VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.getProductId());
            ps.setString(2, m.getMovementType().name()); // enum → String
            ps.setDouble(3, m.getQuantity());
            ps.setDouble(4, m.getUnitPrice());
            ps.setDate(5, Date.valueOf(m.getMovementDate())); // LocalDate → SQL Date
            ps.executeUpdate();
        }
    }

    // Retourne tous les mouvements d'un produit
    // ascending=true  → ORDER BY date ASC  (FIFO : anciens d'abord)
    // ascending=false → ORDER BY date DESC (LIFO : récents d'abord)
    public List<StockMovement> findByProduct(int productId,
                                             boolean ascending) throws SQLException {
        String order = ascending ? "ASC" : "DESC";
        String sql = "SELECT * FROM stock_movements " +
                     "WHERE product_id = ? " +
                     "ORDER BY movement_date " + order;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return extractList(ps);
        }
    }

    // Retourne tous les mouvements (historique global)
    public List<StockMovement> findAll() throws SQLException {
        String sql = "SELECT * FROM stock_movements ORDER BY movement_date DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            return extractList(ps);
        }
    }

    // Dernier prix unitaire d'un produit (pour affichage)
    public double findLastUnitPrice(int productId) throws SQLException {
        String sql = "SELECT unit_price FROM stock_movements " +
                     "WHERE product_id=? AND movement_type='IN' " +
                     "ORDER BY movement_date DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("unit_price");
        }
        return 0;
    }

    // Dernière date d'entrée d'un produit
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

    // Convertit un ResultSet → StockMovement
    // Même principe que mapRow dans ProductDAO
    private StockMovement mapRow(ResultSet rs) throws SQLException {
        StockMovement m = new StockMovement();
        m.setId(rs.getInt("id"));
        m.setProductId(rs.getInt("product_id"));
        m.setMovementType(MovementType.valueOf(rs.getString("movement_type")));
        m.setQuantity(rs.getDouble("quantity"));
        m.setUnitPrice(rs.getDouble("unit_price"));
        m.setMovementDate(rs.getDate("movement_date").toLocalDate());
        return m;
    }

    // Exécute un PreparedStatement et retourne une liste
    // Pourquoi ? → évite de répéter while(rs.next()) partout
    private List<StockMovement> extractList(PreparedStatement ps) throws SQLException {
        List<StockMovement> list = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(mapRow(rs));
        return list;
    }
}