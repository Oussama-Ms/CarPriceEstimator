package ma.projet.service.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ScrapingVolkswagen {

    private static final String DOSSIER_CIBLE = "src/main/resources/data/";

    // ID 58 pour Volkswagen selon votre lien
    private static final int ID_MARQUE_VW = 58;

    private static final List<String> MODELES = Arrays.asList(
            "Golf 7", "Tiguan", "Touareg"
    );

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🚗  SCRAPING VOLKSWAGEN (Golf 7, Tiguan, Touareg)");
        System.out.println("    Logique : Scan complet avec arrêt intelligent.");
        System.out.println("=================================================");

        for (String modele : MODELES) {
            scraperModeleIntelligent(modele);
            pause(3000);
        }
    }

    private static void scraperModeleIntelligent(String modele) {
        // SPÉCIAL VW : On supprime les espaces pour l'URL (ex: "Golf 7" -> "golf7")
        String slug = modele.toLowerCase().replace(" ", "");

        String baseUrl = "https://www.avito.ma/fr/maroc/voitures_d_occasion-à_vendre";

        // Paramètres URL exacts (ID 58)
        String params = String.format("brand=%d&model=%s&brand_model=%d_%s",
                ID_MARQUE_VW, slug, ID_MARQUE_VW, slug);

        // Nom du fichier : "Volkswagen_Golf_7_data.csv" (On garde les underscores pour le fichier)
        String nomFichier = DOSSIER_CIBLE + "Volkswagen_" + modele.replace(" ", "_") + "_data.csv";

        System.out.println("\n➡️  Modèle : " + modele + " (URL slug: " + slug + ")");

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier, false))) {
            writer.println("Titre;Prix;Image_URL;Année-Modèle;Boite de vitesses;Type de carburant;Kilométrage;Marque;Modèle;Lien");

            int page = 1;
            int totalAnnoncesModele = 0;
            boolean continuer = true;

            while (continuer) {

                String currentUrl;
                if (page == 1) currentUrl = baseUrl + "?" + params;
                else currentUrl = baseUrl + "?o=" + page + "&" + params;

                System.out.print("    ⏳ Page " + page + "...");

                try {
                    Document doc = Jsoup.connect(currentUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                            .timeout(20000)
                            .get();

                    Elements annonces = doc.select("a[href][class^=sc-1jge648-0]");
                    if (annonces.isEmpty()) annonces = doc.select("div[class^=sc-1nre5ec-0]");

                    if (annonces.isEmpty()) {
                        System.out.println(" -> [VIDE] Fin du site.");
                        break;
                    }

                    int countPage = 0;
                    for (Element annonce : annonces) {
                        if (extraireEtEcrireAnnonce(annonce, writer, modele)) {
                            countPage++;
                        }
                    }

                    totalAnnoncesModele += countPage;
                    System.out.println(" -> OK (" + countPage + " annonces)");

                    // Condition d'arrêt : Si une page contient 1 seule annonce ou moins
                    if (countPage <= 1) {
                        System.out.println("    🛑 STOP : Page quasi vide. Modèle suivant.");
                        continuer = false;
                        break;
                    }

                    if (page >= 50) {
                        System.out.println("    ⚠️ Limite 50 pages atteinte.");
                        break;
                    }

                    page++;
                    pause(1000 + new Random().nextInt(1000));

                } catch (Exception e) {
                    System.out.println(" -> ERREUR : " + e.getMessage());
                    break;
                }
            }
            System.out.println("    ✅ Total sauvegardé : " + totalAnnoncesModele);

        } catch (IOException e) {
            System.err.println("❌ Erreur fichier : " + e.getMessage());
        }
    }

    private static boolean extraireEtEcrireAnnonce(Element annonceHTML, PrintWriter writer, String modele) {
        try {
            String titre = annonceHTML.select("p[title]").attr("title");
            if (titre.isEmpty()) titre = annonceHTML.select("h3").text();
            if (titre.isEmpty()) titre = annonceHTML.text();

            String prixRaw = annonceHTML.select("span.PuYkS").text();
            if (prixRaw.isEmpty()) prixRaw = annonceHTML.select("span[class*=sc-1x0vz2r-0]").text();

            String prix = prixRaw.replaceAll("[^0-9]", "");

            if (prix.isEmpty() || titre.isEmpty()) return false;

            String lien = annonceHTML.attr("href");
            String image = annonceHTML.select("img").attr("src");

            String annee = "2017"; // VW souvent récentes
            if (titre.matches(".*20[0-2][0-9].*")) {
                annee = titre.replaceAll(".*(20[0-2][0-9]).*", "$1");
            }
            String km = "130000";
            String carburant = "Diesel";
            String boite = "Automatique"; // DSG très courante sur VW

            StringBuilder sb = new StringBuilder();
            sb.append(titre.replace(";", "")).append(";")
                    .append(prix).append(";")
                    .append(image).append(";")
                    .append(annee).append(";")
                    .append(boite).append(";")
                    .append(carburant).append(";")
                    .append(km).append(";")
                    .append("Volkswagen").append(";")
                    .append(modele).append(";")
                    .append(lien);

            writer.println(sb.toString());
            writer.flush();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private static void pause(int ms) {
        try { Thread.sleep(ms); } catch (Exception e) {}
    }
}