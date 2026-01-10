package projet.util;

import java.io.*;
import java.util.*;

public class UnifiedCsvMerger {

    // 🔧 UPDATE THESE PATHS
    private static final String MY_FOLDER = "C:/Users/lenovo/Desktop/ENSA/S7/JAVA/Projet/Scraping/MoteurScraper/Data";      // Your Moteur.ma files
    private static final String HIS_FOLDER = "C:/Users/lenovo/Downloads/CarPriceEstimator_v2/src/main/resources/data"; // His Avito files
    private static final String OUTPUT_FOLDER = "C:/Users/lenovo/Downloads/FinalMerged"; // Output

    public static void main(String[] args) {
        new File(OUTPUT_FOLDER).mkdirs();
        File myDir = new File(MY_FOLDER);
        File hisDir = new File(HIS_FOLDER);

        if (myDir.listFiles() == null || hisDir.listFiles() == null) {
            System.err.println("❌ Error: Check your folder paths.");
            return;
        }

        // Get all unique filenames from both folders
        Set<String> allFilenames = new HashSet<>();
        for (File f : myDir.listFiles()) if (f.getName().endsWith(".csv")) allFilenames.add(f.getName());
        for (File f : hisDir.listFiles()) if (f.getName().endsWith(".csv")) allFilenames.add(f.getName());

        System.out.println("🔍 Found " + allFilenames.size() + " unique models to process.");

        for (String filename : allFilenames) {
            File myFile = new File(myDir, filename);
            File hisFile = new File(hisDir, filename);

            boolean hasMine = myFile.exists();
            boolean hasHis = hisFile.exists();

            if (hasMine && hasHis) {
                System.out.println("🔗 MERGING: " + filename);
                mergeFiles(myFile, hisFile, filename);
            } else if (hasMine) {
                System.out.println("👤 ONLY YOURS: " + filename);
                mergeFiles(myFile, null, filename);
            } else {
                System.out.println("👤 ONLY HIS: " + filename);
                mergeFiles(null, hisFile, filename);
            }
        }
    }

    private static void mergeFiles(File myFile, File hisFile, String outputName) {
        File outFile = new File(OUTPUT_FOLDER + File.separator + outputName);

        try (PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
            // UNIFIED HEADER
            pw.println("Titre;Prix;Année;Kilometrage;Boite;Carburant;Marque;Modele;Ville;Image;Lien");

            // Deduce default Marque/Modele from filename (e.g. "bmw_serie_3.csv")
            String[] parts = outputName.replace(".csv", "").split("[_\\-]");
            String defaultMarque = capitalize(parts.length > 0 ? parts[0] : "Inconnu");
            String defaultModele = capitalize(parts.length > 1 ? parts[1] : "Modele");
            if (parts.length > 2) defaultModele += " " + capitalize(parts[2]);

            // Process My File
            if (myFile != null) processFile(myFile, pw, defaultMarque, defaultModele, true);

            // Process His File
            if (hisFile != null) processFile(hisFile, pw, defaultMarque, defaultModele, false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(File file, PrintWriter pw, String defaultMarque, String defaultModele, boolean isMine) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip Header
            while ((line = br.readLine()) != null) {
                String[] col = line.split(";");
                // Safety: Skip empty lines or headers repeated
                if (col.length < 5 || line.startsWith("Titre")) continue;

                String titre="", prix="", annee="", km="", boite="", carburant="", marque="", modele="", ville="", image="", lien="";

                if (isMine) {
                    // YOUR MAPPING (bmw_serie-3.csv)
                    // 0:Titre, 1:Prix, 2:Annee, 3:Km, 4:Boite, 5:Carburant, 6:Ville, 7:Image, 8:Lien
                    titre = safeGet(col, 0);
                    prix = safeGet(col, 1);
                    annee = safeGet(col, 2);
                    km = safeGet(col, 3);
                    boite = safeGet(col, 4);
                    carburant = safeGet(col, 5);
                    ville = safeGet(col, 6);
                    image = safeGet(col, 7);
                    lien = safeGet(col, 8);
                    marque = defaultMarque;
                    modele = defaultModele;
                } else {
                    // HIS MAPPING (BMW_Serie_3_data.csv)
                    // 0:Titre, 1:Prix, 2:Image_URL, 3:Année-Modèle, 4:Boite, 5:Carburant, 6:Kilométrage, 7:Marque, 8:Modèle, 9:Lien
                    titre = safeGet(col, 0);
                    prix = safeGet(col, 1);
                    image = safeGet(col, 2);
                    annee = safeGet(col, 3);
                    boite = safeGet(col, 4);
                    carburant = safeGet(col, 5);
                    km = safeGet(col, 6);
                    // Use columns if present, otherwise fallback to defaults
                    marque = safeGet(col, 7).isEmpty() ? defaultMarque : safeGet(col, 7);
                    modele = safeGet(col, 8).isEmpty() ? defaultModele : safeGet(col, 8);
                    lien = safeGet(col, 9);
                    ville = "N/A"; // He does not have city
                }

                // OUTPUT: Titre;Prix;Année;Kilometrage;Boite;Carburant;Marque;Modele;Ville;Image;Lien
                pw.println(
                        titre + ";" + prix + ";" + annee + ";" + km + ";" +
                                boite + ";" + carburant + ";" + marque + ";" + modele + ";" +
                                ville + ";" + image + ";" + lien
                );
            }
        }
    }

    private static String safeGet(String[] arr, int index) {
        if (index < arr.length && arr[index] != null) return arr[index];
        return "";
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}