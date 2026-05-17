package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnexion {
    public static void main(String[] args) {
        // Paramètres de connexion
        String url = "jdbc:postgresql://localhost:5432/stock";
        String utilisateur = "postgres";
        String motDePasse = "stock"; // Celui défini à l'étape précédente

        System.out.println("Tentative de connexion...");

        try (Connection connexion = DriverManager.getConnection(url, utilisateur, motDePasse)) {
            if (connexion != null) {
                System.out.println("SUCCÈS : Connexion établie avec la base 'stock' !");
            }
        } catch (SQLException e) {
            System.err.println("ÉCHEC : Vérifiez le driver JAR ou vos identifiants.");
            e.printStackTrace();
        }
    }
}
