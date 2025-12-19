package ma.projet.service.ml;

import ma.projet.dao.VehiculeDAOImpl;
import ma.projet.model.Vehicule;
import ma.projet.service.etl.DataCleaningService;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MLRegressionService {

    private final VehiculeDAOImpl dao = new VehiculeDAOImpl();
    private final DataCleaningService cleaner = new DataCleaningService();
    private RandomForest model;
    private Instances dataStructure;
    private static final String MODEL_PATH = "src/main/resources/car_price_model.model";

    public MLRegressionService() {
        // Try loading model on startup, but don't auto-train blindly
        try {
            if (new File(MODEL_PATH).exists()) {
                System.out.println("🧠 Modèle chargé depuis le fichier.");
                Object[] loaded = SerializationHelper.readAll(MODEL_PATH);
                this.model = (RandomForest) loaded[0];
                this.dataStructure = (Instances) loaded[1];
            }
        } catch (Exception e) {
            System.out.println("⚠️ Pas de modèle trouvé. Prêt pour l'entraînement.");
        }
    }

    public void trainModel() {
        try {
            System.out.println("\n📊 --- DÉBUT DE L'ENTRAÎNEMENT ---");
            System.out.println("1. Chargement des données...");
            List<Vehicule> cars = dao.findAll();

            if (cars.isEmpty()) {
                System.err.println("❌ ERREUR: La base de données est vide ! Importez les CSV d'abord.");
                return;
            }

            // FILTER: Remove outliers (Critical for good metrics)
            int initialSize = cars.size();
            cars.removeIf(c -> c.getPrix() < 10000 || c.getPrix() > 2000000);
            System.out.println("2. Filtrage: " + initialSize + " -> " + cars.size() + " véhicules valides.");

            System.out.println("3. Conversion Weka...");
            Instances trainingData = convertToWekaInstances(cars);
            trainingData.setClassIndex(trainingData.numAttributes() - 1);

            model = new RandomForest();
            model.setNumIterations(100);
            model.setMaxDepth(0); // Unlimited depth for better accuracy

            System.out.println("4. Construction de la forêt aléatoire (Random Forest)...");
            model.buildClassifier(trainingData);

            // Save
            SerializationHelper.writeAll(MODEL_PATH, new Object[]{model, trainingData});
            this.dataStructure = trainingData;

            System.out.println("✅ Modèle sauvegardé !");

            // --- FORCE METRICS DISPLAY ---
            System.out.println("5. Évaluation (Cross-Validation)...");
            Evaluation eval = new Evaluation(trainingData);
            eval.crossValidateModel(model, trainingData, 10, new Random(1));

            System.out.println("\n📈 RÉSULTATS DE PERFORMANCE :");
            System.out.println("==================================");
            System.out.printf("Corrélation (R)         : %.4f  (Cible > 0.85)%n", eval.correlationCoefficient());
            System.out.printf("Erreur Moyenne (MAE)    : %.2f DH%n", eval.meanAbsoluteError());
            System.out.printf("Erreur Relative (RAE)   : %.2f %%  (Cible < 20%%)%n", eval.relativeAbsoluteError());
            System.out.println("==================================\n");

        } catch (Exception e) {
            e.printStackTrace();
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

            // +/- 10% Range
            double min = predictedPrice * 0.90;
            double max = predictedPrice * 1.10;

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

    private Instances convertToWekaInstances(List<Vehicule> cars) {
        ArrayList<String> marques = new ArrayList<>();
        ArrayList<String> modeles = new ArrayList<>();
        ArrayList<String> carburants = new ArrayList<>();
        ArrayList<String> boites = new ArrayList<>();

        // Add dummy to prevent empty list errors
        marques.add("AUTRE"); modeles.add("AUTRE"); carburants.add("AUTRE"); boites.add("AUTRE");

        for (Vehicule v : cars) {
            safeAdd(marques, v.getMarque());
            safeAdd(modeles, v.getModele());
            safeAdd(carburants, v.getCarburant());
            safeAdd(boites, v.getBoiteVitesse());
        }

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("marque", marques));
        attributes.add(new Attribute("modele", modeles));
        attributes.add(new Attribute("annee"));
        attributes.add(new Attribute("kilometrage"));
        attributes.add(new Attribute("carburant", carburants));
        attributes.add(new Attribute("boite", boites));
        attributes.add(new Attribute("price"));

        Instances dataset = new Instances("CarData", attributes, cars.size());

        for (Vehicule v : cars) {
            double[] val = new double[dataset.numAttributes()];
            val[0] = marques.indexOf(cleaner.normaliserTexte(v.getMarque()));
            val[1] = modeles.indexOf(cleaner.normaliserTexte(v.getModele()));
            val[2] = v.getAnnee();
            val[3] = v.getKilometrage();
            val[4] = carburants.indexOf(cleaner.normaliserTexte(v.getCarburant()));
            val[5] = boites.indexOf(cleaner.normaliserTexte(v.getBoiteVitesse()));
            val[6] = v.getPrix();

            if (val[0] == -1) val[0] = 0;
            if (val[1] == -1) val[1] = 0;
            if (val[4] == -1) val[4] = 0;
            if (val[5] == -1) val[5] = 0;

            dataset.add(new DenseInstance(1.0, val));
        }
        return dataset;
    }

    private void safeAdd(ArrayList<String> list, String val) {
        String clean = cleaner.normaliserTexte(val);
        if (!list.contains(clean)) list.add(clean);
    }
}