package projet.service.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class for scraping car data from Avito.ma.
 * It handles multiple brands, specific URL formatting, and details extraction.
 */
public class AvitoScraper {

    // Separator used for the CSV file (Excel usually prefers ';')
    private static final String CSV_SEPARATOR = ";";

    // Directory where the CSV files will be saved
    private static final String DOSSIER_CIBLE = "src/main/resources/data/";

    // --- CONFIGURATION CLASS ---
    // Helper class to store Brand Name, Avito Brand ID, and list of Models
    static class BrandConfig {
        String name;
        int id;
        List<String> models;

        public BrandConfig(String name, int id, List<String> models) {
            this.name = name;
            this.id = id;
            this.models = models;
        }
    }

    // --- MASTER LIST ---
    // Contains all the brands and models we want to scrape.
    // The IDs (e.g., 13 for Dacia, 58 for VW) are specific to Avito's database.
    private static final List<BrandConfig> TOUTES_LES_MARQUES = Arrays.asList(
            new BrandConfig("Dacia", 13, Arrays.asList("Logan", "Duster", "Sandero", "Dokker", "Lodgy", "Spring", "Bigster")),
            new BrandConfig("Volkswagen", 58, Arrays.asList("Golf 7", "Tiguan", "Touareg")),
            new BrandConfig("BMW", 5, Arrays.asList("Serie 1", "Serie 3", "Serie 5")),
            new BrandConfig("Hyundai", 24, Arrays.asList("Accent", "i 10", "Tucson")),
            new BrandConfig("Mercedes", 41, Arrays.asList("Classe A", "Classe C", "Classe E")),
            new BrandConfig("Peugeot", 46, Arrays.asList("206", "208", "Partner")),
            new BrandConfig("Renault", 49, Arrays.asList("Clio", "Megane", "Kangoo")),
            new BrandConfig("Toyota", 56, Arrays.asList("Corolla", "RAV 4", "Yaris"))
    );

    public static void main(String[] args) {
        // 1. Create the output directory if it doesn't exist
        File repertoire = new File(DOSSIER_CIBLE);
        if (!repertoire.exists()) {
            boolean created = repertoire.mkdirs();
            if(created) System.out.println("📂 Dossier créé : " + DOSSIER_CIBLE);
        }

        System.out.println("=================================================");
        System.out.println("🚀  SCRAPING UNIFIÉ (DACIA + VW + MULTIMARQUES)");
        System.out.println("📂  Sauvegarde dans : " + DOSSIER_CIBLE);
        System.out.println("=================================================");

        // 2. Iterate through every Brand in the master list
        for (BrandConfig marque : TOUTES_LES_MARQUES) {
            System.out.println("\n#################################################");
            System.out.println("💎 MARQUE : " + marque.name.toUpperCase());
            System.out.println("#################################################");

            // 3. Iterate through every Model of the current Brand
            for (String modele : marque.models) {
                // Launch the scraping logic for this specific model
                scraperModele(marque.name, marque.id, modele);

                // Pause between models to be polite to the server
                System.out.println("⏳ Pause de 3s avant le prochain modèle...");
                pause(3000);
            }
        }

        System.out.println("\n✅ SCRAPING TOTAL TERMINÉ.");
    }

    /**
     * Scrapes all pages for a specific model and saves to a specific CSV file.
     */
    private static void scraperModele(String brandName, int brandId, String modelName) {
        // LOGIC MERGE: Handle URL differences
        // Volkswagen URLs on Avito usually don't have underscores (e.g. "golf7")
        // Other brands usually use underscores (e.g. "serie_1")
        String modelSlug;
        if (brandId == 58) { // Volkswagen ID
            modelSlug = modelName.toLowerCase().replace(" ", "");
        } else {
            modelSlug = modelName.toLowerCase().replace(" ", "_");
        }

        String baseUrl = "https://www.avito.ma/fr/maroc/voitures_d_occasion-%C3%A0_vendre";
        // Define the output file name (e.g., "Dacia_Logan_RealData.csv")
        String nomFichier = DOSSIER_CIBLE + brandName + "_" + modelName.replace(" ", "_") + "_RealData.csv";

        System.out.println("\n➡️  Traitement : " + modelName + " (" + brandName + ")");

        // Try-with-resources: Automatically closes the PrintWriter when done
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier, false))) {
            // Write the CSV Header
            writer.println("Titre;Prix;Image_URL;Année-Modèle;Boite de vitesses;Type de carburant;Kilométrage;Marque;Modèle;Lien");

            int page = 1;
            int totalAnnoncesModele = 0;
            boolean continuer = true;

            // Loop through pagination (Page 1, Page 2, etc.)
            while (continuer) {
                // Construct URL parameters with Brand ID and Model Slug
                String params = String.format("brand=%d&model=%s&brand_model=%d_%s",
                        brandId, modelSlug, brandId, modelSlug);

                String currentUrl = (page == 1) ? baseUrl + "?" + params : baseUrl + "?o=" + page + "&" + params;

                System.out.print("   📄 Page " + page + " : ");

                try {
                    // Jsoup: Connect to the listings page
                    Document doc = Jsoup.connect(currentUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                            .timeout(15000)
                            .get();

                    // CSS Selector to find ads (this selector might change if Avito updates their site)
                    Elements annonces = doc.select("div[class*=sc-1nre5ec-0] a[href]");
                    // Fallback selector
                    if (annonces.isEmpty()) {
                        annonces = doc.select("a[href][class^=sc-1jge648-0]");
                    }

                    // If no ads found, stop the loop
                    if (annonces.isEmpty()) {
                        System.out.println("[FIN] Plus de résultats.");
                        break;
                    }

                    int countPage = 0;
                    // Loop through every ad found on the current page
                    for (Element lienAnnonce : annonces) {
                        String urlAnnonce = lienAnnonce.attr("href");
                        // Fix relative URLs
                        if (!urlAnnonce.startsWith("http")) urlAnnonce = "https://www.avito.ma" + urlAnnonce;

                        // Go to the detail page to extract precise data
                        if (traiterAnnonceDetaillee(urlAnnonce, writer, brandName, modelName)) {
                            countPage++;
                            pause(1000); // Short pause between ads to mimic human behavior
                        }
                    }

                    System.out.println(countPage + " annonces récupérées.");
                    totalAnnoncesModele += countPage;

                    // Stop condition: If a page has very few results, it's likely the end
                    if (countPage < 2 && page > 1) {
                        continuer = false;
                    }
                    page++;

                } catch (IOException e) {
                    System.out.println("[ERREUR PAGE] " + e.getMessage() + ". On continue.");
                    page++;
                }
            }

            System.out.println("   ✅ Terminé pour " + modelName + " : " + totalAnnoncesModele + " annonces.");

        } catch (IOException e) {
            System.err.println("🔥 Erreur critique fichier : " + e.getMessage());
        }
    }

    /**
     * Visits a specific ad URL, extracts details, and writes a line to the CSV.
     */
    private static boolean traiterAnnonceDetaillee(String url, PrintWriter writer, String brand, String model) {
        try {
            // Connect to the specific ad page
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // Extract Title
            String titre = doc.select("h1").text();
            if(titre.isEmpty()) titre = doc.title();

            // Extract Price
            String prix = doc.select("p.sc-1x0vz2r-0.lnEFFR").text();
            if (prix.isEmpty()) {
                // Try finding div containing "DH"
                prix = doc.select("div:contains(DH)").last() != null ? doc.select("div:contains(DH)").last().text() : "0";
            }
            prix = prix.replaceAll("[^0-9]", ""); // Keep only numbers

            // Extract Image
            String imageUrl = "N/A";
            Element imgElement = doc.selectFirst("img[src*='classifieds/images']");
            if (imgElement != null) imageUrl = imgElement.attr("src");

            // Initialize details map with default values
            Map<String, String> details = new HashMap<>();
            details.put("Année-Modèle", "N/A");
            details.put("Kilométrage", "N/A");
            details.put("Type de carburant", "N/A");
            details.put("Boite de vitesses", "N/A");

            // Loop through info divs (Year, Fuel, etc.) based on icons/text
            Elements infoDivs = doc.select("div[class^=sc-]");

            for (Element div : infoDivs) {
                Elements icons = div.select("img[alt]");
                if (!icons.isEmpty()) {
                    String label = icons.attr("alt");
                    String value = div.text().replace(label, "").trim();

                    // Match label to our CSV columns
                    if (label.contains("Année")) details.put("Année-Modèle", value);
                    if (label.contains("Kilo")) details.put("Kilométrage", value);
                    if (label.contains("carburant")) details.put("Type de carburant", value);
                    if (label.contains("vitesses")) details.put("Boite de vitesses", value);
                }
            }

            // Build the CSV line
            StringBuilder sb = new StringBuilder();
            sb.append(escapeCsv(titre)).append(CSV_SEPARATOR)
                    .append(prix).append(CSV_SEPARATOR)
                    .append(imageUrl).append(CSV_SEPARATOR)
                    .append(escapeCsv(details.get("Année-Modèle"))).append(CSV_SEPARATOR)
                    .append(escapeCsv(details.get("Boite de vitesses"))).append(CSV_SEPARATOR)
                    .append(escapeCsv(details.get("Type de carburant"))).append(CSV_SEPARATOR)
                    .append(escapeCsv(details.get("Kilométrage"))).append(CSV_SEPARATOR)
                    .append(brand).append(CSV_SEPARATOR)
                    .append(model).append(CSV_SEPARATOR)
                    .append(url);

            // Write to file immediately
            writer.println(sb.toString());
            writer.flush();
            return true;

        } catch (Exception e) {
            return false; // Skip this ad if scraping fails
        }
    }

    /**
     * Cleans strings to avoid breaking the CSV format (removes semicolons and newlines).
     */
    private static String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace(";", " ").replace("\n", " ").trim();
    }

    /**
     * Pauses the execution for X milliseconds.
     */
    private static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}