package projet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projet.model.Vehicule;
import projet.service.ml.PricePredictionService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${frontend.url:http://localhost:5173}") // Allow requests from React frontend, configurable for
                                                                // hosting
public class PredictionController {

    private final PricePredictionService predictionService;

    public PredictionController(PricePredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<String> predictPrice(@RequestBody Vehicule vehicule) {
        try {
            String prediction = predictionService.predictPriceRange(vehicule);
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
