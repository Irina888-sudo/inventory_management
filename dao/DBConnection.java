package dao;

import java.sql.*;


public class DBConnection {
    private static final String URL  = "jdbc:postgresql://localhost:5432/stock";
    private static final String USER = "postgres";
    private static final String PASS = "stock"; // à modifier

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}