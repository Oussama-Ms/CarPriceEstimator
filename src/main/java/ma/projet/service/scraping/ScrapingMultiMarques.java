package ma.projet.service.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ScrapingMultiMarques {

    private static final String CSV_SEPARATOR = ";";
    // Dossier de sauvegarde
    private static final String DOSSIER_CIBLE = "src/main/resources/data2/";

    // Configuration des marques
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

    // Liste complète des véhicules demandés
    private static final List<BrandConfig> TOUTES_LES_MARQUES = Arrays.asList(
            new BrandConfig("BMW", 5, Arrays.asList("Serie 1", "Serie 3", "Serie 5")),
            new BrandConfig("Hyundai", 24, Arrays.asList("Accent", "i 10", "Tucson")),
            new BrandConfig("Mercedes", 41, Arrays.asList("Classe A", "Classe C", "Classe E")),
            new BrandConfig("Peugeot", 46, Arrays.asList("206", "208", "Partner")),
            new BrandConfig("Renault", 49, Arrays.asList("Clio", "Megane", "Kangoo")),
            new BrandConfig("Toyota", 56, Arrays.asList("Corolla", "RAV 4", "Yaris")),
            new BrandConfig("Volkswagen", 58, Arrays.asList("Golf 7", "Tiguan", "Touareg"))
    );

    public static void main(String[] args) {
        // Création du dossier si nécessaire
        File repertoire = new File(DOSSIER_CIBLE);
        if (!repertoire.exists()) {
            repertoire.mkdirs();
        }

        System.out.println("=================================================");
        System.out.println("🚀  SCRAPING SÉQUENTIEL (SAUVEGARDE PAR MODÈLE)");
        System.out.println("📂  Dossier : " + DOSSIER_CIBLE);
        System.out.println("=================================================");

        for (BrandConfig marque : TOUTES_LES_MARQUES) {
            System.out.println("\n#################################################");
            System.out.println("💎 MARQUE : " + marque.name.toUpperCase());
            System.out.println("#################################################");

            for (String modele : marque.models) {
                // 1. Lance le scraping du modèle
                scraperModele(marque.name, marque.id, modele);

                // 2. Une fois scraperModele fini, le fichier est GARANTI enregistré.
                // On fait une pause avant d'attaquer le modèle suivant
                System.out.println("⏳ Pause de 5s avant le prochain modèle...");
                pause(5000);
            }
        }

        System.out.println("\n✅ TOUS LES SCRAPINGS SONT TERMINÉS AVEC SUCCÈS.");
    }

    private static void scraperModele(String brandName, int brandId, String modelName) {
        String modelSlug = modelName.toLowerCase().replace(" ", "_");
        String baseUrl = "https://www.avito.ma/fr/maroc/voitures_d_occasion-%C3%A0_vendre";

        // Nom du fichier spécifique à ce modèle
        String nomFichier = DOSSIER_CIBLE + brandName + "_" + modelName.replace(" ", "_") + "_RealData.csv";

        System.out.println("\n➡️  Démarrage : " + modelName + " (" + brandName + ")");

        // Le bloc try(...) ferme automatiquement le fichier à la fin de l'accolade }
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier, false))) {
            writer.println("Titre;Prix;Image_URL;Année-Modèle;Boite de vitesses;Type de carburant;Kilométrage;Marque;Modèle;Lien");

            int page = 1;
            int totalAnnoncesModele = 0;
            boolean continuer = true;

            // Boucle des pages (jusqu'à la fin)
            while (continuer) {
                String params = String.format("brand=%d&model=%s&brand_model=%d_%s",
                        brandId, modelSlug, brandId, modelSlug);

                String currentUrl = (page == 1) ? baseUrl + "?" + params : baseUrl + "?o=" + page + "&" + params;

                System.out.print("   📄 Page " + page + " : ");

                try {
                    Document doc = Jsoup.connect(currentUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                            .timeout(15000)
                            .get();

                    Elements annonces = doc.select("div[class*=sc-1nre5ec-0] a[href]");
                    if (annonces.isEmpty()) {
                        annonces = doc.select("a[href][class^=sc-1jge648-0]");
                    }

                    if (annonces.isEmpty()) {
                        System.out.println("[FIN] Plus de résultats.");
                        continuer = false;
                        break;
                    }

                    int countPage = 0;
                    for (Element lienAnnonce : annonces) {
                        String urlAnnonce = lienAnnonce.attr("href");
                        if (!urlAnnonce.startsWith("http")) urlAnnonce = "https://www.avito.ma" + urlAnnonce;

                        if (traiterAnnonceDetaillee(urlAnnonce, writer, brandName, modelName)) {
                            countPage++;
                            // Pause courte entre les annonces
                            pause(1000);
                        }
                    }

                    System.out.println(countPage + " annonces récupérées.");
                    totalAnnoncesModele += countPage;

                    if (countPage < 5 && page > 1) {
                        continuer = false; // Fin probable
                    }
                    page++;

                } catch (IOException e) {
                    System.out.println("[ERREUR] " + e.getMessage() + " -> On passe à la page suivante.");
                    page++;
                }
            } // Fin While (Pages)

            System.out.println("   ✅ Terminé pour " + modelName + " : " + totalAnnoncesModele + " annonces au total.");

        } catch (IOException e) {
            System.err.println("🔥 Erreur critique lors de la création du fichier : " + e.getMessage());
        }

        // ICI : Le fichier est officiellement fermé et sauvegardé sur le disque.
        System.out.println("💾 Fichier enregistré : " + nomFichier);
    }

    private static boolean traiterAnnonceDetaillee(String url, PrintWriter writer, String brand, String model) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            String titre = doc.select("h1").text();
            if(titre.isEmpty()) titre = doc.title();

            String prix = doc.select("p.sc-1x0vz2r-0.lnEFFR").text();
            if (prix.isEmpty()) {
                prix = doc.select("div:contains(DH)").last() != null ? doc.select("div:contains(DH)").last().text() : "0";
            }
            prix = prix.replaceAll("[^0-9]", "");

            String imageUrl = "N/A";
            Element imgElement = doc.selectFirst("img[src*='classifieds/images']");
            if (imgElement != null) imageUrl = imgElement.attr("src");

            Map<String, String> details = new HashMap<>();
            details.put("Année-Modèle", "N/A");
            details.put("Kilométrage", "N/A");
            details.put("Type de carburant", "N/A");
            details.put("Boite de vitesses", "N/A");

            Elements infoDivs = doc.select("div[class^=sc-]");
            for (Element div : infoDivs) {
                Elements icons = div.select("img[alt]");
                if (!icons.isEmpty()) {
                    String label = icons.attr("alt");
                    String value = div.text().replace(label, "").trim();

                    if (label.contains("Année")) details.put("Année-Modèle", value);
                    if (label.contains("Kilo")) details.put("Kilométrage", value);
                    if (label.contains("carburant")) details.put("Type de carburant", value);
                    if (label.contains("vitesses")) details.put("Boite de vitesses", value);
                }
            }

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

            writer.println(sb.toString());
            // IMPORTANT : flush() assure que la ligne est écrite immédiatement sur le disque
            writer.flush();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace(";", " ").replace("\n", " ").trim();
    }

    private static void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}