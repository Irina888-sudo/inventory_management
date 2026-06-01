package dao;

import model.MovementType;
import model.StockMovement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PendingMovementDAO {

    // INSERT un mouvement en attente
    public void insert(StockMovement m) throws SQLException {
        String sql = "INSERT INTO pending_movements" +
                     "(product_id, movement_type, quantity, unit_price, movement_date)" +
                     " VALUES(?,?,?,?,?)";
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

    // SELECT tous les mouvements en attente
    public List<StockMovement> findAll() throws SQLException {
        String sql = "SELECT * FROM pending_movements ORDER BY movement_date DESC";
        List<StockMovement> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        }
        return list;
    }

    // DELETE un mouvement en attente par id
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pending_movements WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // SELECT un mouvement par id
    public StockMovement findById(int id) throws SQLException {
        String sql = "SELECT * FROM pending_movements WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

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
}