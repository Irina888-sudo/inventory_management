package dao;

import model.*;
import model.StockMethod;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductDAO {

    public void insert(Product p) throws SQLException {
        String sql = "INSERT INTO products(name, stock_method, unit) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getStockMethod().name());
            ps.setString(3, p.getUnit());
            ps.executeUpdate();
        }
    }

    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    StockMethod.valueOf(rs.getString("stock_method")),
                    rs.getString("unit")
                ));
            }
        }
        return list;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                StockMethod.valueOf(rs.getString("stock_method")),
                rs.getString("unit")
            );
        }
        return null;
    }
}