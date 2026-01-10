package projet.service.ml;

import projet.model.Vehicule;
import projet.service.etl.DataCleaningService;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import jakarta.annotation.PostConstruct; // Remplace les constructeurs statiques
import java.io.InputStream;

@Service // <--- Transforme ceci en composant Spring
public class PricePredictionService {

    private RandomForest model;
    private Instances dataStructure;
    private final DataCleaningService cleaner; // Injection de dépendance

    // Spring injecte automatiquement DataCleaningService ici
    public PricePredictionService(DataCleaningService cleaner) {
        this.cleaner = cleaner;
    }

    @PostConstruct // S'exécute automatiquement au démarrage de l'app
    public void init() {
        try {
            System.out.println("🧠 Chargement du modèle de prédiction...");

            // Chargement compatible JAR / Docker / Spring Boot
            InputStream is = getClass().getResourceAsStream("/ml/car_price_model.model");

            if (is == null) {
                System.err.println("⚠️ Fichier modèle introuvable dans /resources/ml/ !");
                return;
            }

            Object[] loaded = SerializationHelper.readAll(is);
            this.model = (RandomForest) loaded[0];
            this.dataStructure = (Instances) loaded[1];
            System.out.println("✅ Modèle chargé avec succès !");

        } catch (Exception e) {
            System.err.println("❌ Erreur critique chargement modèle : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String predictPriceRange(Vehicule car) {
        if (model == null) return "Indisponible";

        try {
            Instances singleData = new Instances(this.dataStructure, 0);
            double[] values = new double[singleData.numAttributes()];

            values[0] = findSmartIndex(singleData.attribute(0), car.getMarque());
            values[1] = findSmartIndex(singleData.attribute(1), car.getModele());
            values[2] = car.getAnnee();
            values[3] = car.getKilometrage();
            values[4] = findSmartIndex(singleData.attribute(4), car.getCarburant());
            values[5] = findSmartIndex(singleData.attribute(5), car.getBoiteVitesse());
            values[6] = 0; // Class (Prix) à prédire

            singleData.add(new DenseInstance(1.0, values));
            singleData.setClassIndex(6);

            double predictedPrice = model.classifyInstance(singleData.instance(0));

            // +/- 10% Range (ajusté pour être plus précis)
            double min = predictedPrice * 0.90;
            double max = predictedPrice * 1.10;

            return String.format("%,.0f - %,.0f DH", min, max);

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur Calcul";
        }
    }

    private double findSmartIndex(Attribute attr, String userValue) {
        if (userValue == null) return 0;
        String search = cleaner.normaliserTexte(userValue);

        // Exact match
        int idx = attr.indexOfValue(search);
        if (idx != -1) return idx;

        // Soft match
        for (int i = 0; i < attr.numValues(); i++) {
            if (attr.value(i).equalsIgnoreCase(search)) return i;
        }
        return 0; // Fallback (Autre)
    }
}