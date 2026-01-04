package projet;

import projet.model.Vehicule;
import projet.service.etl.CsvImportService;
import projet.service.ml.ModelTrainerService;
import projet.service.ml.PricePredictionService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Hide ARPACK Warnings
        System.setProperty("com.github.fommil.netlib.BLAS", "com.github.fommil.netlib.F2jBLAS");
        System.setProperty("com.github.fommil.netlib.LAPACK", "com.github.fommil.netlib.F2jLAPACK");

        Scanner scanner = new Scanner(System.in);
        System.out.println("===========================================");
        System.out.println("   🚀 AUTOVALUE - SYSTÈME DE GESTION 🚀   ");
        System.out.println("===========================================");
        System.out.println("1. Mode ADMINISTRATEUR (Réinitialiser BDD, Importer CSV & Entraîner)");
        System.out.println("2. Mode UTILISATEUR (Estimer un prix)");
        System.out.print("👉 Choix : ");

        String mode = scanner.nextLine();

        if (mode.equals("1")) {
            // --- ADMIN MODE ---
            System.out.println("\n🛠️ Démarrage du processus ETL & Training...");

            // Step 1: Import CSV to DB (Will TRUNCATE first)
            System.out.println("--- ÉTAPE 1 : IMPORTATION CSV ---");
            CsvImportService importService = new CsvImportService();
            importService.demarrerImportation();

            // Step 2: Train Model
            System.out.println("\n--- ÉTAPE 2 : ENTRAÎNEMENT IA ---");
            ModelTrainerService trainer = new ModelTrainerService();
            trainer.trainAndSaveModel();

            System.out.println("\n✅ Terminé ! Vous pouvez relancer en mode Utilisateur.");

        } else {
            // --- USER MODE ---
            System.out.println("\n🚗 Lancement du module de prédiction...");
            PricePredictionService predictor = new PricePredictionService();

            while (true) {
                try {
                    Vehicule userCar = new Vehicule();

                    System.out.println("\n📝 INFORMATIONS VÉHICULE");

                    System.out.print("👉 Marque (ex: Dacia) [ou 'exit']: ");
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("exit")) break;
                    userCar.setMarque(input);

                    System.out.print("👉 Modèle (ex: Dokker): ");
                    userCar.setModele(scanner.nextLine());

                    System.out.print("👉 Année (ex: 2019): ");
                    String anneeInput = scanner.nextLine();
                    try {
                        userCar.setAnnee(Integer.parseInt(anneeInput));
                    } catch (NumberFormatException e) {
                        System.out.println("   ⚠️ Année invalide -> 2015 par défaut");
                        userCar.setAnnee(2015);
                    }

                    System.out.println("\n--- Options (Entrée pour valeurs par défaut) ---");

                    System.out.print("👉 Kilométrage (Défaut: 120 000): ");
                    String km = scanner.nextLine();
                    userCar.setKilometrage(km.isEmpty() ? 120000 : Integer.parseInt(km));

                    System.out.print("👉 Carburant (Défaut: Diesel): ");
                    String fuel = scanner.nextLine();
                    userCar.setCarburant(fuel.isEmpty() ? "Diesel" : fuel);

                    System.out.print("👉 Boite (Défaut: Manuelle): ");
                    String box = scanner.nextLine();
                    userCar.setBoiteVitesse(box.isEmpty() ? "Manuelle" : box);

                    System.out.println("\n⏳ Calcul en cours...");
                    String result = predictor.predictPriceRange(userCar);

                    System.out.println("*******************************************");
                    System.out.println("💰 ESTIMATION : " + result);
                    System.out.println("*******************************************");

                } catch (Exception e) {
                    System.out.println("❌ Erreur : " + e.getMessage());
                }
            }
        }
        scanner.close();
    }
}