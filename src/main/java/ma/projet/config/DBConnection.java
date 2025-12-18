package ma.projet.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            // CORRECTION ICI : On vérifie si elle est null OU fermée
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        AppConstants.DB_URL,
                        AppConstants.DB_USER,
                        AppConstants.DB_PASSWORD
                );
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur de connexion BDD : " + e.getMessage());
            // En cas d'erreur critique, on arrête tout pour ne pas spammer la console
            throw new RuntimeException("Impossible de se connecter à la base de données");
        }
        return connection;
    }
}