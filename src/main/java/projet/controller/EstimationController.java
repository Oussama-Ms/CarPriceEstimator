package projet.controller;

import projet.model.Vehicule;
import projet.service.ml.PricePredictionService;
import projet.view.MainFrame;
import projet.view.components.FormulairePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class EstimationController {

    private final MainFrame view;
    private PricePredictionService model;
    private boolean isModelLoaded = false;
    private Map<String, String[]> modelesMap;

    public EstimationController(MainFrame view) {
        this.view = view;
        initModelData(); // Initialisation des données des modèles
        initController();
        loadModelAsync(); // Chargement en arrière-plan
    }

    private void initModelData() {
        modelesMap = new HashMap<>();

        // TOUTES LES MARQUES AVEC LEURS MODÈLES (version complète)
        modelesMap.put("DACIA", new String[]{"Logan", "Sandero", "Duster", "Dokker", "Lodgy"});
        modelesMap.put("RENAULT", new String[]{"Clio", "Megane", "Kangoo"});
        modelesMap.put("PEUGEOT", new String[]{"206", "208", "Partner"});
        modelesMap.put("VOLKSWAGEN", new String[]{ "T-Roc", "Tiguan", "Touareg"});
        modelesMap.put("HYUNDAI", new String[]{"i10", "Accent", "Tucson"});
        modelesMap.put("TOYOTA", new String[]{"Yaris", "Corolla", "RAV4"});
        modelesMap.put("MERCEDES-BENZ", new String[]{"Classe A", "Classe C", "Classe E"});
        modelesMap.put("BMW", new String[]{"Série 1", "Série 3", "Série 5"});
    }

    private void initController() {
        FormulairePanel form = view.getFormPanel();

        // Désactiver le bouton au démarrage tant que le modèle n'est pas chargé
        form.getBtnEstimer().setEnabled(false);
        form.getBtnEstimer().setText("Chargement du modèle...");

        // Définir les écouteurs
        form.getBtnEstimer().addActionListener(new CalculateListener());
        form.getCbMarque().addActionListener(e -> updateModeles());

        // Appel initial pour peupler les modèles
        updateModeles();
    }

    private void updateModeles() {
        FormulairePanel form = view.getFormPanel();
        String mq = form.getSelectedMarque();
        JComboBox<String> cbModele = form.getCbModele();
        cbModele.removeAllItems();

        if (mq != null && modelesMap.containsKey(mq)) {
            for (String m : modelesMap.get(mq)) {
                cbModele.addItem(m);
            }
        } else {
            cbModele.addItem("Autre");
        }
    }

    // Méthode pour charger le modèle dans un Thread séparé
    private void loadModelAsync() {
        new Thread(() -> {
            System.out.println("⏳ Démarrage du chargement du modèle en arrière-plan...");
            try {
                // Cette opération lourde ne bloque plus l'interface
                this.model = new PricePredictionService();
                this.isModelLoaded = true;

                // Une fois chargé, on met à jour l'interface via SwingUtilities
                SwingUtilities.invokeLater(() -> {
                    FormulairePanel form = view.getFormPanel();
                    form.getBtnEstimer().setEnabled(true);
                    form.getBtnEstimer().setText("ESTIMER LE PRIX");
                    System.out.println("✅ Modèle chargé et UI mise à jour.");
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(view,
                            "Erreur lors du chargement du modèle IA.",
                            "Erreur Fatale",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    // --- INNER CLASS ---
    private class CalculateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            estimatePrice();
        }
    }

    private void estimatePrice() {
        if (!isModelLoaded || model == null) {
            JOptionPane.showMessageDialog(view, "Le modèle est encore en cours de chargement, veuillez patienter.");
            return;
        }

        FormulairePanel form = view.getFormPanel();

        try {
            // Afficher l'indication de calcul en cours (utilisez la méthode à un paramètre)
            form.updateResult("Calcul en cours...");

            // 1. Build DTO
            Vehicule userCar = new Vehicule();
            userCar.setMarque(form.getSelectedMarque());
            userCar.setModele(form.getSelectedModele());
            userCar.setAnnee(form.getSelectedAnnee());
            userCar.setCarburant(form.getSelectedCarburant());
            userCar.setBoiteVitesse(form.getSelectedBoite());

            // Validate Km
            String kmStr = form.getKmText();
            if (kmStr == null || kmStr.trim().isEmpty()) {
                userCar.setKilometrage(100000);
            } else {
                try {
                    userCar.setKilometrage(Integer.parseInt(kmStr.trim()));
                } catch (NumberFormatException ex) {
                    userCar.setKilometrage(100000);
                }
            }

            // 2. Call Logic
            // On utilise un SwingWorker ou un Thread ici aussi pour ne pas figer l'interface pendant la prédiction
            // (Bien que la prédiction soit souvent plus rapide que le chargement)
            String result = model.predictPriceRange(userCar);

            // 3. Update View (un seul paramètre comme dans votre code original)
            form.updateResult("Estimation : " + result);

        } catch (Exception ex) {
            form.updateResult("Erreur : " + ex.getMessage());
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}