package projet.dao;

import projet.config.DBConnection;
import projet.model.Vehicule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VehiculeDAOImpl implements IVehiculeDAO {

    @Override
    public void save(Vehicule v) {
        List<Vehicule> list = new ArrayList<>();
        list.add(v);
        saveAll(list);
    }

    @Override
    public void saveAll(List<Vehicule> vehicules) {
        String sql = "INSERT INTO vehicule (marque, modele, annee, prix, kilometrage, carburant, boite_vitesse, titre, ville, image, lien) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            int count = 0;

            for (Vehicule v : vehicules) {
                pstmt.setString(1, v.getMarque());
                pstmt.setString(2, v.getModele());
                pstmt.setInt(3, v.getAnnee());
                pstmt.setDouble(4, v.getPrix());
                pstmt.setInt(5, v.getKilometrage());
                pstmt.setString(6, v.getCarburant());
                pstmt.setString(7, v.getBoiteVitesse());
                pstmt.setString(8, v.getTitre());
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

    @Override
    public void truncate() {
        String sql = "TRUNCATE TABLE vehicule";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("🧹 Base de données vidée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Vehicule> findAll() {
        List<Vehicule> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicule";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}