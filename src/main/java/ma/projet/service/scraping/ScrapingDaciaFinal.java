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

public class ScrapingDaciaFinal {

    private static final String CSV_SEPARATOR = ";";
    private static final int ID_MARQUE_DACIA = 13;

    // --- NOUVEAU : DOSSIER DE SAUVEGARDE ---
    private static final String DOSSIER_CIBLE = "src/main/resources/data/";

    // Liste des modèles à scraper
    private static final List<String> MODELES = Arrays.asList(
            "Logan", "Duster", "Sandero", "Dokker", "Lodgy", "Spring", "Bigster"
    );

    public static void main(String[] args) {
        // 1. Création du dossier s'il n'existe pas
        File repertoire = new File(DOSSIER_CIBLE);
        if (!repertoire.exists()) {
            boolean created = repertoire.mkdirs();
            if(created) System.out.println("📂 Dossier créé : " + DOSSIER_CIBLE);
        }

        System.out.println("=================================================");
        System.out.println("🚀  SCRAPING AVITO - DACIA (CORRIGÉ & COMPLET)");
        System.out.println("📂  Sauvegarde dans : " + DOSSIER_CIBLE);
        System.out.println("=================================================");

        for (String modele : MODELES) {
            scraperModele(modele);
            pause(5000);
        }

        System.out.println("\n✅ SCRAPING TERMINÉ POUR TOUS LES MODÈLES.");
    }

    private static void scraperModele(String modele) {
        String slug = modele.toLowerCase();
        String baseUrl = "https://www.avito.ma/fr/maroc/voitures_d_occasion-%C3%A0_vendre";

        // --- MODIFICATION : CHEMIN DU FICHIER ---
        String nomFichier = DOSSIER_CIBLE + "Dacia_" + modele + "_RealData.csv";

        System.out.println("\n-------------------------------------------------");
        System.out.println("➡️  Traitement du modèle : " + modele);
        System.out.println("-------------------------------------------------");

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier, false))) {
            writer.println("Titre;Prix;Image_URL;Année-Modèle;Boite de vitesses;Type de carburant;Kilométrage;Marque;Modèle;Lien");

            int page = 1;
            int totalAnnoncesModele = 0;
            boolean continuer = true;

            while (continuer) {
                String params = String.format("brand=%d&model=%s&brand_model=%d_%s",
                        ID_MARQUE_DACIA, slug, ID_MARQUE_DACIA, slug);

                String currentUrl;
                if (page == 1) {
                    currentUrl = baseUrl + "?" + params;
                } else {
                    currentUrl = baseUrl + "?o=" + page + "&" + params;
                }

                System.out.println("   📄 Page " + page + " en cours...");

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
                        System.out.println("      [INFO] Page vide ou fin des résultats. Passage au modèle suivant.");
                        continuer = false;
                        break;
                    }

                    int countPage = 0;

                    for (Element lienAnnonce : annonces) {
                        String urlAnnonce = lienAnnonce.attr("href");
                        if (!urlAnnonce.startsWith("http")) {
                            urlAnnonce = "https://www.avito.ma" + urlAnnonce;
                        }

                        boolean success = traiterAnnonceDetaillee(urlAnnonce, writer, modele);
                        if (success) {
                            countPage++;
                            pause(1000);
                        }
                    }

                    totalAnnoncesModele += countPage;
                    System.out.println("      -> " + countPage + " annonces extraites sur la page " + page);

                    if (countPage < 5 && page > 1) {
                        continuer = false;
                    }

                    page++;

                } catch (IOException e) {
                    System.err.println("      [ERREUR PAGE] " + e.getMessage() + ". On tente la page suivante.");
                    page++;
                }
            }

            System.out.println("✅ Terminé pour " + modele + " : " + totalAnnoncesModele + " annonces.");

        } catch (IOException e) {
            System.err.println("🔥 Erreur critique fichier : " + e.getMessage());
        }
    }

    private static boolean traiterAnnonceDetaillee(String url, PrintWriter writer, String modeleImpose) {
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

            String safeTitre = escapeCsv(titre);
            String safeAnnee = escapeCsv(details.get("Année-Modèle"));
            String safeBoite = escapeCsv(details.get("Boite de vitesses"));
            String safeCarbu = escapeCsv(details.get("Type de carburant"));
            String safeKm = escapeCsv(details.get("Kilométrage"));

            StringBuilder sb = new StringBuilder();
            sb.append(safeTitre).append(CSV_SEPARATOR)
                    .append(prix).append(CSV_SEPARATOR)
                    .append(imageUrl).append(CSV_SEPARATOR)
                    .append(safeAnnee).append(CSV_SEPARATOR)
                    .append(safeBoite).append(CSV_SEPARATOR)
                    .append(safeCarbu).append(CSV_SEPARATOR)
                    .append(safeKm).append(CSV_SEPARATOR)
                    .append("Dacia").append(CSV_SEPARATOR)
                    .append(modeleImpose).append(CSV_SEPARATOR)
                    .append(url);

            writer.println(sb.toString());
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
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}