package projet.view;

import projet.model.Vehicule;
import projet.service.ml.PricePredictionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class AutoValueUI extends JFrame {

    private final PricePredictionService predictionService;

    // Palette de couleurs "Luxury Car Dashboard"
    private static final Color BG_DARK = new Color(18, 18, 24);       // Fond très sombre
    private static final Color CARD_BG = new Color(30, 32, 40);       // Gris bleuté
    private static final Color ACCENT_COLOR = new Color(75, 110, 255); // Bleu néon moderne
    private static final Color TEXT_COLOR = new Color(230, 230, 230);
    private static final Color LABEL_COLOR = new Color(140, 145, 160);

    private JComboBox<String> cbMarque, cbModele, cbCarburant, cbBoite;
    private JSpinner spAnnee;
    private JTextField txtKm;
    private JLabel lblResultat;
    private Map<String, String[]> modelesMap;

    public AutoValueUI() {
        this.predictionService = new PricePredictionService();
        initModelData();

        // Configuration Fenêtre
        setTitle("AutoValue Pro");
        setSize(480, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Taille fixe pour garder le design propre

        // Panel Principal (Fond dégradé subtil)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, 0, getHeight(), new Color(10, 10, 15));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null); // Layout Absolu pour un contrôle au pixel près (Design Pro)
        setContentPane(mainPanel);

        // --- 1. HEADER (Logo + Titre) ---
        JLabel lblLogo = new JLabel("⚡ AUTOVALUE");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBounds(40, 40, 300, 40);
        mainPanel.add(lblLogo);

        JLabel lblSubtitle = new JLabel("AI Price Estimator");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(ACCENT_COLOR);
        lblSubtitle.setBounds(42, 75, 200, 20);
        mainPanel.add(lblSubtitle);

        // --- 2. LA CARTE FORMULAIRE ---
        // On dessine un panneau arrondi manuellement
        JPanel formPanel = new RoundedPanel(25, CARD_BG);
        formPanel.setBounds(30, 120, 405, 480);
        formPanel.setLayout(new GridLayout(6, 1, 0, 15)); // 6 lignes, espacement vertical
        formPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Ajout des champs
        String[] ordreMarques = {"DACIA", "RENAULT", "PEUGEOT", "VOLKSWAGEN", "HYUNDAI", "TOYOTA", "MERCEDES-BENZ", "BMW"};

        cbMarque = createModernCombo(ordreMarques);
        addFormGroup(formPanel, "MARQUE DU VÉHICULE", cbMarque);

        cbModele = createModernCombo(new String[]{});
        addFormGroup(formPanel, "MODÈLE", cbModele);

        spAnnee = createModernSpinner();
        addFormGroup(formPanel, "ANNÉE DE MISE EN CIRCULATION", spAnnee);

        cbCarburant = createModernCombo(new String[]{"DIESEL", "ESSENCE", "HYBRIDE", "ELECTRIQUE"});
        addFormGroup(formPanel, "TYPE DE CARBURANT", cbCarburant);

        cbBoite = createModernCombo(new String[]{"MANUELLE", "AUTOMATIQUE"});
        addFormGroup(formPanel, "BOÎTE DE VITESSE", cbBoite);

        txtKm = createModernTextField("100000");
        addFormGroup(formPanel, "KILOMÉTRAGE ACTUEL", txtKm);

        mainPanel.add(formPanel);

        // --- 3. BOUTON & RESULTAT ---
        JButton btnEstimer = new ModernButton("ESTIMER LE PRIX");
        btnEstimer.setBounds(30, 620, 405, 55);
        mainPanel.add(btnEstimer);

        lblResultat = new JLabel("", SwingConstants.CENTER);
        lblResultat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblResultat.setForeground(new Color(46, 204, 113)); // Vert Emeraude
        lblResultat.setBounds(30, 680, 405, 30);
        mainPanel.add(lblResultat);

        // --- LOGIQUE ---
        cbMarque.addActionListener(e -> updateModeles());
        btnEstimer.addActionListener(e -> estimer());
        updateModeles();
    }

    // --- HELPER : Création de Groupes (Label + Input) ---
    private void addFormGroup(JPanel parent, String title, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);

        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(LABEL_COLOR);
        l.setBorder(new EmptyBorder(0, 5, 0, 0));

        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        parent.add(p);
    }

    // --- CUSTOM COMPONENTS (C'est ici que la magie opère) ---

    private JComboBox<String> createModernCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setUI(new DarkComboBoxUI()); // Installation du look custom
        cb.setRenderer(new DarkListRenderer()); // Liste déroulante sombre
        cb.setBackground(BG_DARK);
        cb.setForeground(TEXT_COLOR);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return cb;
    }

    private JTextField createModernTextField(String text) {
        JTextField txt = new JTextField(text);
        txt.setBackground(BG_DARK);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(ACCENT_COLOR);
        txt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    private JSpinner createModernSpinner() {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(2019, 1990, 2025, 1));
        sp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor de = (JSpinner.DefaultEditor) editor;
            de.getTextField().setBackground(BG_DARK);
            de.getTextField().setForeground(Color.WHITE);
            de.getTextField().setCaretColor(ACCENT_COLOR);
        }
        return sp;
    }

    // --- LOGIQUE MÉTIER ---

    private void initModelData() {
        modelesMap = new HashMap<>();
        modelesMap.put("DACIA", new String[]{"Logan", "Sandero", "Duster", "Dokker", "Lodgy"});
        modelesMap.put("RENAULT", new String[]{"Clio", "Megane", "Kangoo"});
        modelesMap.put("PEUGEOT", new String[]{"206", "208", "Partner"});
        modelesMap.put("VOLKSWAGEN", new String[]{"T-roc", "Tiguan", "Touareg"});
        modelesMap.put("HYUNDAI", new String[]{"i10",  "Accent", "Tucson"});
        modelesMap.put("TOYOTA", new String[]{"Yaris", "Corolla", "RAV4"});
        modelesMap.put("MERCEDES-BENZ", new String[]{"Classe A", "Classe C", "Classe E"});
        modelesMap.put("BMW", new String[]{"Serie 1", "Serie 3", "Serie 5"});
    }

    private void updateModeles() {
        String mq = (String) cbMarque.getSelectedItem();
        cbModele.removeAllItems();
        if (mq != null && modelesMap.containsKey(mq)) {
            for (String m : modelesMap.get(mq)) cbModele.addItem(m);
        } else { cbModele.addItem("Autre"); }
    }

    private void estimer() {
        try {
            lblResultat.setText("Calcul en cours...");
            lblResultat.setForeground(new Color(200, 200, 200));

            Vehicule v = new Vehicule();
            v.setMarque(cbMarque.getSelectedItem().toString());
            v.setModele(cbModele.getSelectedItem().toString());
            v.setAnnee((int) spAnnee.getValue());
            v.setCarburant(cbCarburant.getSelectedItem().toString());
            v.setBoiteVitesse(cbBoite.getSelectedItem().toString());
            v.setKilometrage((int) Double.parseDouble(txtKm.getText()));

            // Simulation d'un petit délai pour l'effet "Calcul"
            new Timer(500, e -> {
                String res = predictionService.predictPriceRange(v);
                lblResultat.setText(res);
                lblResultat.setForeground(new Color(46, 204, 113));
                ((Timer)e.getSource()).stop();
            }).start();

        } catch (Exception ex) {
            lblResultat.setText("Erreur de saisie");
            lblResultat.setForeground(new Color(231, 76, 60));
        }
    }

    // =========================================================================
    // CLASSES INTERNES POUR LE DESIGN (LE SECRET DU LOOK PRO)
    // =========================================================================

    // 1. Bouton "Glow" avec dégradé
    class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Dégradé Horizontal
            GradientPaint gp = new GradientPaint(0, 0, ACCENT_COLOR, getWidth(), 0, new Color(40, 60, 180));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            // Effet Brillance
            g2.setColor(new Color(255,255,255,30));
            g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 15, 15);
            super.paintComponent(g);
        }
    }

    // 2. Panneau Arrondi
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }

    // 3. UI Custom pour ComboBox (Pour enlever la flèche moche de Windows)
    class DarkComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton() {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_DARK); // Fond du bouton
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(ACCENT_COLOR); // Couleur de la flèche
                    // Dessin de la flèche vers le bas
                    int w = 10, h = 6;
                    int x = (getWidth() - w) / 2;
                    int y = (getHeight() - h) / 2;
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(x, y, x + w / 2, y + h);
                    g2.drawLine(x + w / 2, y + h, x + w, y);
                }
            };
            btn.setBorder(BorderFactory.createEmptyBorder());
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(BG_DARK);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    // 4. Rendu de la liste déroulante (Pop-up sombre)
    class DarkListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(new EmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                setBackground(ACCENT_COLOR);
                setForeground(Color.WHITE);
            } else {
                setBackground(new Color(40, 42, 50));
                setForeground(TEXT_COLOR);
            }
            return this;
        }
    }
}
