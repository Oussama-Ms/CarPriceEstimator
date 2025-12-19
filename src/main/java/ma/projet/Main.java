package ma.projet;

import ma.projet.model.Vehicule;
import ma.projet.service.etl.CsvImportService;
import ma.projet.service.ml.MLRegressionService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Démarrage de l'application AutoValue...");

        // 1. ETL (Extraction, Transformation, Loading)
        // Only run this if your database is empty or you have new data
        // CsvImportService importService = new CsvImportService();
        // importService.demarrerImportation();

        // 2. Machine Learning (Training)
        MLRegressionService mlService = new MLRegressionService();

        // Uncomment this line ONLY if you need to re-train the model on new data
        mlService.trainModel();

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n-------------------------------------------");
        System.out.println("   🚗 ESTIMATEUR DE PRIX (AutoValue) 🚗   ");
        System.out.println("-------------------------------------------");

        while (true) {
            try {
                Vehicule userCar = new Vehicule();

                // --- MANDATORY FIELDS ---
                System.out.println("\n🔹 [OBLIGATOIRE] Informations principales :");

                System.out.print("   Marque (ex: Dacia) [ou 'exit']: ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) break;
                userCar.setMarque(input);

                System.out.print("   Modèle (ex: Dokker): ");
                userCar.setModele(scanner.nextLine());

                System.out.print("   Année (ex: 2019): ");
                try {
                    userCar.setAnnee(Integer.parseInt(scanner.nextLine()));
                } catch (NumberFormatException e) {
                    System.out.println("   ⚠️ Année invalide. Par défaut: 2015");
                    userCar.setAnnee(2015);
                }

                // --- OPTIONAL FIELDS (Press Enter to skip) ---
                System.out.println("\n🔹 [OPTIONNEL] Appuyez sur ENTRÉE pour ignorer :");

                System.out.print("   Kilométrage (Défaut: 150 000): ");
                String kmStr = scanner.nextLine();
                if (kmStr.trim().isEmpty()) {
                    userCar.setKilometrage(150000); // Default Average
                } else {
                    try { userCar.setKilometrage(Integer.parseInt(kmStr)); }
                    catch (Exception e) { userCar.setKilometrage(150000); }
                }

                System.out.print("   Carburant (Défaut: Diesel): ");
                String fuel = scanner.nextLine();
                userCar.setCarburant(fuel.trim().isEmpty() ? "Diesel" : fuel);

                System.out.print("   Boite (Défaut: Manuelle): ");
                String boite = scanner.nextLine();
                userCar.setBoiteVitesse(boite.trim().isEmpty() ? "Manuelle" : boite);

                // --- PREDICTION ---
                System.out.println("\n⏳ Analyse en cours...");
                String priceRange = mlService.predictPriceRange(userCar);

                System.out.println("-------------------------------------------");
                System.out.println("💰 ESTIMATION : " + priceRange);
                System.out.println("-------------------------------------------");

            } catch (Exception e) {
                System.out.println("❌ Erreur : " + e.getMessage());
            }
        }

        System.out.println("👋 Au revoir !");
        scanner.close();
    }
}