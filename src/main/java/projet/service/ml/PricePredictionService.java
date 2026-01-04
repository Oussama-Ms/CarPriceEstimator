package projet.service.ml;

import projet.model.Vehicule;
import projet.service.etl.DataCleaningService;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.File;

public class PricePredictionService {

    private RandomForest model;
    private Instances dataStructure;
    private final DataCleaningService cleaner = new DataCleaningService();
    private static final String MODEL_PATH = "src/main/resources/car_price_model.model";

    public PricePredictionService() {
        try {
            if (new File(MODEL_PATH).exists()) {
                System.out.println("🧠 Chargement du modèle de prédiction...");
                Object[] loaded = SerializationHelper.readAll(MODEL_PATH);
                this.model = (RandomForest) loaded[0];
                this.dataStructure = (Instances) loaded[1];
                System.out.println("✅ Service de prédiction prêt.");
            } else {
                System.out.println("⚠️ Modèle introuvable. Veuillez lancer l'entraînement.");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement modèle : " + e.getMessage());
        }
    }

    public String predictPriceRange(Vehicule car) {
        if (model == null) return "Modèle non disponible. (Entraînez-le d'abord)";

        try {
            Instances singleData = new Instances(this.dataStructure, 0);
            double[] values = new double[singleData.numAttributes()];

            values[0] = findSmartIndex(singleData.attribute(0), car.getMarque());
            values[1] = findSmartIndex(singleData.attribute(1), car.getModele());
            values[2] = car.getAnnee();
            values[3] = car.getKilometrage();
            values[4] = findSmartIndex(singleData.attribute(4), car.getCarburant());
            values[5] = findSmartIndex(singleData.attribute(5), car.getBoiteVitesse());
            values[6] = 0;

            singleData.add(new DenseInstance(1.0, values));
            singleData.setClassIndex(6);

            double predictedPrice = model.classifyInstance(singleData.instance(0));

            // +/- 15% Range
            double min = predictedPrice * 0.85;
            double max = predictedPrice * 1.15;

            return String.format("%,.0f DH - %,.0f DH", min, max);

        } catch (Exception e) {
            return "Estimation impossible : " + e.getMessage();
        }
    }

    private double findSmartIndex(Attribute attr, String userValue) {
        if (userValue == null) return 0;
        String search = cleaner.normaliserTexte(userValue);

        // Exact match
        int idx = attr.indexOfValue(search);
        if (idx != -1) return idx;

        // Soft match (ignore case)
        for (int i = 0; i < attr.numValues(); i++) {
            if (attr.value(i).equalsIgnoreCase(search)) return i;
        }
        return 0; // Fallback
    }
}