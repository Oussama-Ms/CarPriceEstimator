package projet.service.ml;

import projet.dao.VehiculeDAOImpl;
import projet.model.Vehicule;
import projet.service.etl.DataCleaningService;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModelTrainerService {

    private final VehiculeDAOImpl dao = new VehiculeDAOImpl();
    private final DataCleaningService cleaner = new DataCleaningService();
    private static final String MODEL_PATH = "src/main/resources/car_price_model.model";

    public void trainAndSaveModel() {
        try {
            System.out.println("\n📊 --- DÉBUT DE L'ENTRAÎNEMENT ---");

            // 1. Load Data
            System.out.println("1. Chargement des données...");
            List<Vehicule> cars = dao.findAll();

            if (cars.isEmpty()) {
                System.err.println("❌ ERREUR: La base de données est vide ! Importez les CSV d'abord.");
                return;
            }

            // 2. Filter Outliers
            int initialSize = cars.size();
            cars.removeIf(c -> c.getPrix() < 10000 || c.getPrix() > 2000000);
            cars.removeIf(c -> c.getAnnee() < 2000);
            System.out.println("2. Filtrage: " + initialSize + " -> " + cars.size() + " véhicules valides.");

            // 3. Convert to Weka
            System.out.println("3. Conversion Weka...");
            Instances trainingData = convertToWekaInstances(cars);
            trainingData.setClassIndex(trainingData.numAttributes() - 1);

            // 4. Configure Model
            RandomForest model = new RandomForest();
            model.setNumIterations(100);
            model.setMaxDepth(20);
            model.setNumFeatures(4);

            // 5. Train
            System.out.println("4. Construction de la forêt aléatoire (Random Forest)...");
            model.buildClassifier(trainingData);

            // 6. Save
            SerializationHelper.writeAll(MODEL_PATH, new Object[]{model, trainingData});
            System.out.println("✅ Modèle sauvegardé avec succès !");

            // 7. Evaluate
            System.out.println("5. Évaluation (Cross-Validation)...");
            Evaluation eval = new Evaluation(trainingData);
            eval.crossValidateModel(model, trainingData, 10, new Random(1));

            System.out.println("\n📈 RÉSULTATS DE PERFORMANCE :");
            System.out.println("==================================");
            System.out.printf("Corrélation (R)         : %.4f%n", eval.correlationCoefficient());
            System.out.printf("Erreur Moyenne (MAE)    : %.2f DH%n", eval.meanAbsoluteError());
            System.out.printf("Erreur Relative (RAE)   : %.2f %%%n", eval.relativeAbsoluteError());
            System.out.println("==================================\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Instances convertToWekaInstances(List<Vehicule> cars) {
        ArrayList<String> marques = new ArrayList<>();
        ArrayList<String> modeles = new ArrayList<>();
        ArrayList<String> carburants = new ArrayList<>();
        ArrayList<String> boites = new ArrayList<>();

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