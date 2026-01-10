package projet.service.etl;

import projet.config.AppConstants;
import projet.dao.VehiculeDAOImpl;
import projet.model.Vehicule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
@Service
public class CsvImportService {

    private final VehiculeDAOImpl dao = new VehiculeDAOImpl();
    private final DataCleaningService cleaner = new DataCleaningService();
    private final Set<Vehicule> cacheVehicules = new HashSet<>();

    public void demarrerImportation() {
        File folder = new File(AppConstants.CSV_DIRECTORY_PATH);
        File[] fileList = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (fileList == null || fileList.length == 0) {
            System.out.println("❌ Aucun fichier CSV trouvé.");
            return;
        }
        // --- Empty the database before importing ---
        System.out.println("🔄 Réinitialisation de la base de données...");
        dao.truncate();
        // -------------------------------------------------------

        System.out.println("\nFICHIER                               | LUS        | GARDÉS     | IGNORÉS    | STATUS");
        System.out.println("--------------------------------------|------------|------------|------------|---------");

        int totalLus = 0;
        int totalPrixFiltre = 0;
        int totalAnneeFiltre = 0;

        for (File file : fileList) {
            int avant = cacheVehicules.size();

            // On récupère les stats détaillées du fichier
            int[] stats = traiterFichierAvecStats(file);

            int lus = stats[0];
            int prixFiltre = stats[1];
            int anneeFiltre = stats[2];
            int gardes = cacheVehicules.size() - avant;
            int ignores = lus - gardes;

            totalLus += lus;
            totalPrixFiltre += prixFiltre;
            totalAnneeFiltre += anneeFiltre;

            System.out.println(String.format("%-37s | %-10d | %-10d | %-10d | ✅ OK",
                    file.getName(), lus, gardes, ignores));
        }

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("\n📊 RÉSUMÉ DU NETTOYAGE :");
        System.out.println("   ❌ Annonces écartées car Prix < 5000 DH : " + totalPrixFiltre);
        System.out.println("   ❌ Annonces écartées car Année invalide : " + totalAnneeFiltre);
        System.out.println("   👯 Doublons/Lignes vides ignorés        : " + (totalLus - totalPrixFiltre - totalAnneeFiltre - cacheVehicules.size()));

        System.out.println("\n💾 Sauvegarde de " + cacheVehicules.size() + " véhicules uniques...");
        dao.saveAll(new ArrayList<>(cacheVehicules));
        System.out.println("🚀 Importation terminée avec succès !");
    }

    private int[] traiterFichierAvecStats(File file) {
        int lus = 0;
        int prixFiltre = 0;
        int anneeFiltre = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // Sauter le Header
            String line;
            while ((line = br.readLine()) != null) {
                lus++;
                String[] data = line.split(";", -1);
                if (data.length < 11) continue;

                int prix = cleaner.nettoyerPrix(data[1]);
                int annee = cleaner.nettoyerAnnee(data[2]);

                // Application des filtres stricts
                if (prix < 5000) {
                    prixFiltre++;
                    continue;
                }
                if (annee < 1980 || annee > 2026) {
                    anneeFiltre++;
                    continue;
                }

                // Création et normalisation de l'objet Vehicule
                Vehicule v = new Vehicule(
                        cleaner.normaliserTexte(data[6]), // Marque
                        cleaner.normaliserTexte(data[7]), // Modele
                        annee,
                        prix,
                        cleaner.nettoyerKilometrage(data[3]), // Moyenne locale si plage
                        cleaner.normaliserTexte(data[5]), // Carburant
                        cleaner.normaliserTexte(data[4]), // Boite
                        data[0],                         // Titre
                        (data[8].isEmpty() ? "MAROC" : cleaner.normaliserTexte(data[8])), // Ville
                        data[9],                         // Image
                        data[10]                         // Lien
                );

                cacheVehicules.add(v);
            }
        } catch (Exception e) {
            System.err.println("Erreur de lecture : " + file.getName());
        }

        return new int[]{lus, prixFiltre, anneeFiltre};
    }
}