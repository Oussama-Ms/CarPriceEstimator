package projet.view.components;

import projet.view.AbstractView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class FormulairePanel extends AbstractView {

    // Composants de l'UI
    private JComboBox<String> cbMarque, cbModele, cbCarburant, cbBoite;
    private JSpinner spAnnee;
    private JTextField txtKm;
    private JLabel lblResultat;
    private JButton btnEstimer;

    public FormulairePanel() {
        super(); // Appelle initLayout() et initStyling() via AbstractView
    }

    @Override
    protected void initLayout() {
        // On utilise un layout null pour placer les éléments précisément comme dans votre design original
        // ou un layout vertical propre. Ici, pour garder votre style "Card", on va centrer un panneau.
        this.setLayout(null);

        // Titre
        JLabel lblTitle = new JLabel("⚡ AUTOVALUE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(40, 30, 300, 40);
        this.add(lblTitle);

        JLabel lblSubtitle = new JLabel("AI Price Estimator");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(ACCENT_COLOR);
        lblSubtitle.setBounds(42, 65, 200, 20);
        this.add(lblSubtitle);

        // Carte Formulaire
        JPanel cardPanel = new RoundedPanel(25, CARD_BG);
        cardPanel.setBounds(30, 100, 420, 450); // Ajusté pour la taille de la fenêtre
        cardPanel.setLayout(new GridLayout(6, 1, 0, 10));
        cardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Initialisation des champs
        String[] marques = {"DACIA", "RENAULT", "PEUGEOT", "VOLKSWAGEN", "HYUNDAI", "TOYOTA", "MERCEDES-BENZ", "BMW"};
        cbMarque = createModernCombo(marques);
        addFormGroup(cardPanel, "MARQUE", cbMarque);

        cbModele = createModernCombo(new String[]{});
        addFormGroup(cardPanel, "MODÈLE", cbModele);

        spAnnee = createModernSpinner();
        addFormGroup(cardPanel, "ANNÉE", spAnnee);

        cbCarburant = createModernCombo(new String[]{"DIESEL", "ESSENCE", "HYBRIDE", "ELECTRIQUE"});
        addFormGroup(cardPanel, "CARBURANT", cbCarburant);

        cbBoite = createModernCombo(new String[]{"MANUELLE", "AUTOMATIQUE"});
        addFormGroup(cardPanel, "BOÎTE", cbBoite);

        txtKm = createModernTextField("100000");
        addFormGroup(cardPanel, "KILOMÉTRAGE", txtKm);

        this.add(cardPanel);

        // Bouton
        btnEstimer = new ModernButton("ESTIMER LE PRIX");
        btnEstimer.setBounds(30, 570, 420, 50);
        this.add(btnEstimer);

        // Résultat
        lblResultat = new JLabel("", SwingConstants.CENTER);
        lblResultat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblResultat.setForeground(new Color(46, 204, 113));
        lblResultat.setBounds(30, 630, 420, 30);
        this.add(lblResultat);
    }

    // --- Méthodes pour le Controller (EstimationController) ---

    public JButton getBtnEstimer() { return btnEstimer; }

    public String getSelectedMarque() { return (String) cbMarque.getSelectedItem(); }

    public String getSelectedModele() { return (String) cbModele.getSelectedItem(); }

    public int getSelectedAnnee() { return (Integer) spAnnee.getValue(); }

    public String getSelectedCarburant() { return (String) cbCarburant.getSelectedItem(); }

    public String getSelectedBoite() { return (String) cbBoite.getSelectedItem(); }

    public String getKmText() { return txtKm.getText(); }

    public void updateResult(String text) {
        lblResultat.setText(text);
        lblResultat.setForeground(new Color(46, 204, 113));
    }

    // Le contrôleur a besoin d'écouter les changements de marque pour mettre à jour les modèles
    public JComboBox<String> getCbMarque() { return cbMarque; }
    public JComboBox<String> getCbModele() { return cbModele; }


    // --- Helpers graphiques (Recopiés de votre AutoValueUI) ---

    private void addFormGroup(JPanel parent, String title, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(LABEL_COLOR);
        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        parent.add(p);
    }

    private JComboBox<String> createModernCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setUI(new DarkComboBoxUI());
        cb.setRenderer(new DarkListRenderer());
        cb.setBackground(BG_DARK);
        cb.setForeground(TEXT_COLOR);
        return cb;
    }

    private JTextField createModernTextField(String text) {
        JTextField txt = new JTextField(text);
        txt.setBackground(BG_DARK);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(ACCENT_COLOR);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    private JSpinner createModernSpinner() {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(2019, 1990, 2025, 1));
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor de = (JSpinner.DefaultEditor) editor;
            de.getTextField().setBackground(BG_DARK);
            de.getTextField().setForeground(Color.WHITE);
        }
        return sp;
    }

    // --- Classes Internes pour le Design ---

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
            if (!isEnabled()) {
                g2.setColor(Color.GRAY);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_COLOR, getWidth(), 0, new Color(40, 60, 180));
                g2.setPaint(gp);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
        }
    }

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

    // ComboBox UI Customization
    class DarkComboBoxUI extends BasicComboBoxUI {
        @Override protected JButton createArrowButton() {
            JButton btn = new JButton() {
                @Override public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(BG_DARK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(ACCENT_COLOR);
                    int w=10, h=6; int x=(getWidth()-w)/2, y=(getHeight()-h)/2;
                    g2.fillPolygon(new int[]{x, x+w/2, x+w}, new int[]{y, y+h, y}, 3);
                }
            };
            btn.setBorder(BorderFactory.createEmptyBorder());
            return btn;
        }
    }

    class DarkListRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) { setBackground(ACCENT_COLOR); setForeground(Color.WHITE); }
            else { setBackground(new Color(40, 42, 50)); setForeground(TEXT_COLOR); }
            return this;
        }
    }
}