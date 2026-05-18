package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

   
    private static final String URL;
    private static final String USER;
    private static final String PASS;

    static {
        Properties props = new Properties();
        try {
            InputStream in = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("config.properties");

            if (in == null) {
                // Fallback : cherche le fichier dans le dossier de travail
                java.nio.file.Path path = java.nio.file.Paths.get("config.properties");
                if (!java.nio.file.Files.exists(path)) {
                    path = java.nio.file.Paths.get("database", "config.properties");
                }
                if (java.nio.file.Files.exists(path)) {
                    in = java.nio.file.Files.newInputStream(path);
                }
            }

            if (in == null)
                throw new RuntimeException("config.properties introuvable !");

            props.load(in); 
        } catch (Exception e) {
            throw new RuntimeException("Erreur config : " + e.getMessage());
        }
       
        URL  = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASS = props.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}