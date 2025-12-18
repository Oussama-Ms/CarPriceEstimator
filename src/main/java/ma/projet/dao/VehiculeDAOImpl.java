package ma.projet.dao;

import ma.projet.config.DBConnection;
import ma.projet.model.Vehicule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class VehiculeDAOImpl {

    public void saveAll(List<Vehicule> vehicules) {
        String sql = "INSERT INTO vehicule (titre, marque, modele, annee, prix, kilometrage, carburant, boite_vitesse, ville, image, lien) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            int count = 0;

            for (Vehicule v : vehicules) {
                pstmt.setString(1, v.getTitre());
                pstmt.setString(2, v.getMarque());
                pstmt.setString(3, v.getModele());
                pstmt.setInt(4, v.getAnnee());
                pstmt.setInt(5, v.getPrix());
                pstmt.setInt(6, v.getKilometrage());
                pstmt.setString(7, v.getCarburant());
                pstmt.setString(8, v.getBoiteVitesse());
                pstmt.setString(9, v.getVille());
                pstmt.setString(10, v.getImage());
                pstmt.setString(11, v.getLien());

                pstmt.addBatch();
                count++;

                if (count % 1000 == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                }
            }
            pstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("✅ " + count + " véhicules insérés.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}