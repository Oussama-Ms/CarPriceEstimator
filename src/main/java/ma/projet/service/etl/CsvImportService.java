package ma.projet.service.etl;

import ma.projet.config.AppConstants;
import ma.projet.dao.VehiculeDAOImpl;
import ma.projet.model.Vehicule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CsvImportService {

    private final VehiculeDAOImpl dao = new VehiculeDAOImpl();
    private final DataCleaningService cleaner = new DataCleaningService();
    private final Set<Vehicule> cacheVehicules = new HashSet<>();

    public void demarrerImportation() {
        File folder = new File(AppConstants.CSV_DIRECTORY_PATH);
        if (folder.listFiles() == null) {
            System.out.println("❌ Dossier CSV vide : " + AppConstants.CSV_DIRECTORY_PATH);
            return;
        }

        System.out.println("📂 Lecture depuis : " + folder.getAbsolutePath());

        for (File file : folder.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".csv")) {
                traiterFichier(file);
            }
        }

        System.out.println("💾 Sauvegarde de " + cacheVehicules.size() + " véhicules...");
        dao.saveAll(new ArrayList<>(cacheVehicules));
    }

    private void traiterFichier(File file) {
        System.out.println("Processing: " + file.getName());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Header
            if (line == null) return;

            // UNIFIED CSV STRUCTURE:
            // 0:Titre; 1:Prix; 2:Année; 3:Kilometrage; 4:Boite; 5:Carburant; 6:Marque; 7:Modele; 8:Ville; 9:Image; 10:Lien

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length < 5) continue;

                try {
                    // 1. Parse Basic Fields
                    String titre = data[0];
                    int prix = cleaner.nettoyerPrix(data[1]);

                    int annee = 0;
                    try { annee = Integer.parseInt(data[2].trim()); } catch(Exception e) { annee = 2015; }

                    int km = cleaner.nettoyerKilometrage(data[3]);

                    // 2. Parse Text Fields
                    String boite = cleaner.normaliserTexte(data[4]);
                    String carburant = cleaner.normaliserTexte(data[5]);
                    String marque = cleaner.normaliserTexte(data[6]);
                    String modele = cleaner.normaliserTexte(data[7]);

                    // 3. Handle Optional Fields (Ville, Image, Lien) safely
                    String ville = (data.length > 8) ? data[8] : "N/A";
                    String image = (data.length > 9) ? data[9] : "";
                    String lien = (data.length > 10) ? data[10] : "";

                    // 4. Filtering
                    if (prix < 5000) continue;
                    if (annee < 1980 || annee > 2026) continue;

                    // 5. Creation - STRICTLY MATCHING CONSTRUCTOR ORDER
                    // (Marque, Modele, Annee, Prix, Km, Carburant, Boite, Titre, Ville, Image, Lien)
                    Vehicule v = new Vehicule(
                            marque,      // 1
                            modele,      // 2
                            annee,       // 3
                            prix,        // 4
                            km,          // 5
                            carburant,   // 6
                            boite,       // 7
                            titre,       // 8
                            ville,       // 9
                            image,       // 10
                            lien         // 11
                    );

                    cacheVehicules.add(v);

                } catch (Exception e) {
                    // System.err.println("Skipped line: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}