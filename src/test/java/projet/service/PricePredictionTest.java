package projet.service;

import projet.model.Vehicule;
import projet.service.ml.PricePredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PricePredictionTest {

    private PricePredictionService predictionService;

    // This runs before EACH test to ensure a fresh start
    @BeforeEach
    public void setUp() {
        predictionService = new PricePredictionService();
    }

    // --- TEST ML-01: MODEL LOADING ---
    @Test
    public void testModelLoading() {
        System.out.println("TEST ML-01: Verifying Model Loading...");

        // We create a dummy car just to trigger the service
        Vehicule v = new Vehicule();
        String result = predictionService.predictPriceRange(v);

        System.out.println("Service Status: " + result);

        // If the model wasn't loaded, it would likely return "Modèle non disponible" or throw an error
        assertNotNull(result, "Result should not be null");
        assertFalse(result.contains("introuvable"), "Model file should be found");
    }

    // --- TEST ML-02: STANDARD PREDICTION (The "Happy Path") ---
    @Test
    public void testStandardPrediction() {
        System.out.println("TEST ML-02: Predicting Dacia Logan 2019 (Standard Case)");

        Vehicule v = new Vehicule();
        v.setMarque("DACIA");
        v.setModele("Logan");
        v.setAnnee(2019);
        v.setKilometrage(100000);
        v.setCarburant("Diesel");
        v.setBoiteVitesse("Manuelle");

        String prediction = predictionService.predictPriceRange(v);
        System.out.println("Input: Dacia Logan 2019 | Estimated: " + prediction);

        // We expect a range string like "70,000 DH - 90,000 DH"
        assertNotNull(prediction);
        assertTrue(prediction.contains("-"), "Prediction should be a range (containing '-')");
        assertTrue(prediction.contains("DH"), "Prediction should contain currency 'DH'");
    }

    // --- TEST ML-03: UNKNOWN BRAND (Robustness) ---
    @Test
    public void testUnknownBrand() {
        System.out.println("TEST ML-03: Predicting Unknown Brand (Ferrari)");

        Vehicule v = new Vehicule();
        v.setMarque("FERRARI"); // Not in training data
        v.setModele("Testarossa");
        v.setAnnee(2020);
        v.setKilometrage(5000);
        v.setCarburant("Essence");
        v.setBoiteVitesse("Automatique");

        String prediction = predictionService.predictPriceRange(v);
        System.out.println("Input: Ferrari | Result: " + prediction);

        // The system should NOT crash. It might give a low estimation (fallback), but it must run.
        assertNotNull(prediction, "The system should handle unknown brands gracefully");
    }

    // --- TEST ML-04: ZERO/EMPTY VALUES ---
    @Test
    public void testEmptyValues() {
        System.out.println("TEST ML-04: Predicting Empty Object");

        Vehicule v = new Vehicule(); // Empty object
        // We set only minimal required fields to avoid NullPointer if your logic expects them
        v.setAnnee(2000);
        v.setKilometrage(0);

        String prediction = predictionService.predictPriceRange(v);
        System.out.println("Input: Empty Car | Result: " + prediction);

        assertNotNull(prediction);
    }
}