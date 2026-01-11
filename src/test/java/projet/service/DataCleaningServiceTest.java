package projet.service;

import projet.service.etl.DataCleaningService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataCleaningServiceTest {

    // Instantiate the service to test it
    private final DataCleaningService cleaner = new DataCleaningService();

    // --- TEST ETL-01: Price Cleaning ---
    @Test
    public void testPriceCleaning() {
        System.out.println("TEST ETL-01: Cleaning standard price");

        String input = "145 000 DH";
        int result = cleaner.nettoyerPrix(input);

        System.out.println("Input: " + input + " | Output: " + result);
        assertEquals(145000, result, "Should remove 'DH' and spaces");
    }

    // --- TEST ETL-02: Price Error Handling ---
    @Test
    public void testPriceInvalid() {
        System.out.println("TEST ETL-02: Handling invalid price");

        String input = "Prix sur demande";
        int result = cleaner.nettoyerPrix(input);

        System.out.println("Input: " + input + " | Output: " + result);
        assertEquals(0, result, "Invalid price should return 0");
    }

    // --- TEST ETL-03: Mileage Normalization ---
    @Test
    public void testMileageCleaning() {
        System.out.println("TEST ETL-03: Cleaning standard mileage");

        String input = "120.000 km";
        int result = cleaner.nettoyerKilometrage(input);

        System.out.println("Input: " + input + " | Output: " + result);
        assertEquals(120000, result);
    }

    // --- TEST ETL-04: Mileage Range Logic ---
    @Test
    public void testMileageRange() {
        System.out.println("TEST ETL-04: Handling mileage range (Average)");

        // Logic: (10000 + 20000) / 2 = 15000
        String input = "10000 - 20000";
        int result = cleaner.nettoyerKilometrage(input);

        System.out.println("Input: " + input + " | Output: " + result);
        assertEquals(15000, result, "Should calculate the average of the range");
    }

    // --- TEST ETL-05: Year Extraction ---
    @Test
    public void testYearExtraction() {
        System.out.println("TEST ETL-05: Extracting year from text");

        String input = "Modèle 2018 - Bon état";
        int result = cleaner.nettoyerAnnee(input);

        System.out.println("Input: " + input + " | Output: " + result);
        assertEquals(2018, result, "Should extract '2018' using Regex");
    }

    // --- TEST ETL-06: Text Normalization ---
    @Test
    public void testTextNormalization() {
        System.out.println("TEST ETL-06: Normalizing text (Uppercase + Safe CSV)");

        String input = "renault;clio";
        String result = cleaner.normaliserTexte(input);

        System.out.println("Input: " + input + " | Output: " + result);

        // Expect Uppercase AND replacement of semicolon ';' with comma ','
        assertEquals("RENAULT,CLIO", result);
    }
}