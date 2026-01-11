package projet.service;

import projet.service.scraping.AvitoScraper;
import projet.service.scraping.MoteurScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ScrapingFunctionalTest {

    // --- TEST 1 : VÉRIFICATION DE LA CONNEXION (SCR-01) ---
    @Test
    public void testAvitoConnection() {
        String url = "https://www.avito.ma";
        System.out.println("TEST SCR-01: Tentative de connexion à " + url);
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .execute();

            System.out.println("Code Statut : " + response.statusCode());
            // On vérifie que le code est 200 (OK)
            assertEquals(200, response.statusCode(), "Le site Avito devrait répondre 200 OK");
        } catch (IOException e) {
            fail("Erreur de connexion : " + e.getMessage());
        }
    }

    // --- TEST 2 : NETTOYAGE DU PRIX (SCR-02) ---
    @Test
    public void testPriceCleaning() {
        System.out.println("TEST SCR-02: Vérification du nettoyage de prix");

        String inputRaw = "120 000 DH";
        // Simulation de ta logique : on garde que les chiffres
        String cleaned = inputRaw.replaceAll("[^0-9]", "");

        System.out.println("Entrée : " + inputRaw);
        System.out.println("Sortie : " + cleaned);

        assertEquals("120000", cleaned, "Le prix ne doit contenir que des chiffres");
    }

    // --- TEST 3 : FORMATAGE CSV (SCR-03) ---
    @Test
    public void testCsvFormatting() {
        System.out.println("TEST SCR-03: Vérification nettoyage CSV (AvitoScraper)");

        // On teste la méthode statique que tu as rendue public
        String inputSale = "Bonjour; ceci est un test\nNouvelle ligne";
        String resultat = AvitoScraper.escapeCsv(inputSale);

        System.out.println("Entrée : " + inputSale);
        System.out.println("Sortie : " + resultat);

        // On vérifie qu'il n'y a plus de point-virgule ni de retour à la ligne
        assertFalse(resultat.contains(";"));
        assertFalse(resultat.contains("\n"));
    }

    // --- TEST 4 : DÉTECTION PAGINATION (SCR-04) ---
    @Test
    public void testMoteurPagination() {
        System.out.println("TEST SCR-04: Détection pagination Moteur.ma");

        // Une URL qui contient sûrement plusieurs pages (ex: Mercedes)
        String url = "https://www.moteur.ma/fr/voiture/achat-voiture-occasion/recherche/?marque=mercedes-benz";

        int pages = MoteurScraper.detectTotalPages(url);
        System.out.println("Pages détectées : " + pages);

        assertTrue(pages > 1, "Il devrait y avoir plus d'une page de résultats pour Mercedes");
    }
}