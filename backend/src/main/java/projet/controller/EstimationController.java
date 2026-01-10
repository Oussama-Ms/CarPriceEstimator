package ma.projet.controller;

import ma.projet.model.Vehicule;
import ma.projet.service.ml.PricePredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // Dit à Spring : "Je réponds en JSON"
@RequestMapping("/api/estimation") // Préfixe de l'URL
@CrossOrigin(origins = "http://localhost:5173") // Autorise React (Vite) à nous parler
public class EstimationController {

    private final PricePredictionService predictionService;

    // Injection automatique via constructeur
    public EstimationController(PricePredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<?> estimatePrice(@RequestBody Vehicule vehicule) {
        // @RequestBody convertit automatiquement le JSON reçu en objet Vehicule Java
        try {
            String result = predictionService.predictPriceRange(vehicule);

            // On renvoie un objet JSON simple
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "estimation", result,
                    "vehicule", vehicule // On renvoie les infos pour confirmation
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}