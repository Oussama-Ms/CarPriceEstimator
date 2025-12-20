package ma.projet.dao;

import ma.projet.config.DBConnection;
import ma.projet.model.Vehicule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class VehiculeDAOImpl {

    public void saveAll(List<Vehicule> vehicules) {
        // SQL: 11 Columns
        String sql = "INSERT INTO vehicule (marque, modele, annee, prix, kilometrage, carburant, boite_vitesse, titre, ville, image, lien) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            int count = 0;

            for (Vehicule v : vehicules) {
                // PARAMETERS MUST MATCH SQL ORDER EXACTLY (1 to 11)
                pstmt.setString(1, v.getMarque());
                pstmt.setString(2, v.getModele());
                pstmt.setInt(3, v.getAnnee());
                pstmt.setDouble(4, v.getPrix());
                pstmt.setInt(5, v.getKilometrage());
                pstmt.setString(6, v.getCarburant());     // Check if Getter is getCarburant() or getTypeCarburant()
                pstmt.setString(7, v.getBoiteVitesse());
                pstmt.setString(8, v.getTitre());

                // NEW FIELDS (Ensure Getters exist in Vehicule.java)
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
            System.out.println("✅ Succès : " + count + " véhicules insérés.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void truncate() {
        String sql = "TRUNCATE TABLE vehicule";
        try (Connection conn = DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("🧹 Base de données vidée avec succès.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du vidage de la table : " + e.getMessage());
        }
    }

    // Add findAll() if it's missing (Required for ML Service)
    public List<Vehicule> findAll() {
        List<Vehicule> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM vehicule";

        try (Connection conn = DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setId(rs.getInt("id"));
                v.setMarque(rs.getString("marque"));
                v.setModele(rs.getString("modele"));
                v.setAnnee(rs.getInt("annee"));
                v.setPrix(rs.getInt("prix"));
                v.setKilometrage(rs.getInt("kilometrage"));
                v.setCarburant(rs.getString("carburant"));
                v.setBoiteVitesse(rs.getString("boite_vitesse"));
                v.setTitre(rs.getString("titre"));
                v.setVille(rs.getString("ville"));
                // image and lien if needed
                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}