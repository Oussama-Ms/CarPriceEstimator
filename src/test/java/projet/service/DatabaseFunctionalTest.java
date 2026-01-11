package projet.service;

import projet.config.DBConnection;
import projet.dao.IVehiculeDAO;
import projet.dao.VehiculeDAOImpl;
import projet.model.Vehicule;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// On force l'ordre des tests (1. Connexion -> 2. Vider -> 3. Insérer -> 4. Lire)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseFunctionalTest {

    private final IVehiculeDAO vehiculeDAO = new VehiculeDAOImpl();

    // --- TEST DB-01 : CONNEXION ---
    @Test
    @Order(1)
    public void testDatabaseConnection() {
        System.out.println("TEST DB-01: Vérification Connexion MySQL");
        Connection conn = DBConnection.getConnection();

        assertNotNull(conn, "La connexion ne doit pas être null");
        System.out.println("✅ Connexion établie avec succès !");
    }

    // --- TEST DB-02 : NETTOYAGE (TRUNCATE) ---
    @Test
    @Order(2)
    public void testTruncate() {
        System.out.println("TEST DB-02: Vidage de la table vehicule");

        // Action
        vehiculeDAO.truncate();

        // Vérification
        List<Vehicule> list = vehiculeDAO.findAll();
        System.out.println("Taille après truncate : " + list.size());

        assertEquals(0, list.size(), "La table doit être vide après truncate");
    }

    // --- TEST DB-03 : INSERTION (SAVE) ---
    @Test
    @Order(3)
    public void testSaveVehicule() {
        System.out.println("TEST DB-03: Insertion d'un véhicule de test");

        Vehicule v = new Vehicule();
        v.setMarque("TEST-BRAND");
        v.setModele("Model-X");
        v.setAnnee(2024);
        v.setPrix(150000);
        v.setKilometrage(5000);
        v.setCarburant("Electrique");
        v.setBoiteVitesse("Automatique");
        v.setTitre("Test Unit Auto");
        v.setVille("Casablanca");

        vehiculeDAO.save(v);

        // On vérifie qu'il y a maintenant 1 élément
        List<Vehicule> list = vehiculeDAO.findAll();
        System.out.println("Véhicules en base : " + list.size());

        assertTrue(list.size() >= 1, "Il doit y avoir au moins un véhicule en base");
    }

    // --- TEST DB-04 : LECTURE ET VÉRIFICATION ---
    @Test
    @Order(4)
    public void testFindData() {
        System.out.println("TEST DB-04: Lecture des données");

        List<Vehicule> list = vehiculeDAO.findAll();
        boolean found = false;

        for (Vehicule v : list) {
            System.out.println("Lu en base : " + v.getMarque() + " " + v.getModele());
            if (v.getMarque().equals("TEST-BRAND") && v.getModele().equals("Model-X")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Le véhicule inséré 'TEST-BRAND' doit être retrouvé en base");
    }
}