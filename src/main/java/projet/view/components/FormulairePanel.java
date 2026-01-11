package projet.view.components;

import projet.view.AbstractView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;

public class FormulairePanel extends AbstractView {

    // Composants de l'UI
    private JComboBox<String> cbMarque, cbModele, cbCarburant, cbBoite, cbAnnee; // cbAnnee remplace spAnnee
    private JTextField txtKm;
    private JLabel lblResultat;
    private JButton btnEstimer;

    public FormulairePanel() {
        super(); // Appelle initLayout() et initStyling() via AbstractView
    }

    @Override
    protected void initLayout() {
        // Layout null pour contrôle précis du design
        this.setLayout(null);

        // En-tête avec effet de gradient
        JPanel headerPanel = new HeaderPanel();
        headerPanel.setBounds(0, 0, 500, 120);
        headerPanel.setLayout(null);

        // Titre avec effet
        JLabel lblTitle = new JLabel("AUTOVALUE");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(40, 30, 300, 40);
        headerPanel.add(lblTitle);

        // Sous-titre (Emojis retirés pour éviter les glitchs d'affichage)
        JLabel lblSubtitle = new JLabel("AI Price Estimator");
        lblSubtitle.setFont(FONT_SUBTITLE);
        lblSubtitle.setForeground(SECONDARY_COLOR);
        lblSubtitle.setBounds(42, 70, 250, 25);
        headerPanel.add(lblSubtitle);

        this.add(headerPanel);

        // Carte Formulaire avec ombre
        JPanel cardPanel = new RoundedPanel(20, CARD_BG, true);
        cardPanel.setBounds(40, 130, 420, 480);
        cardPanel.setLayout(new GridLayout(6, 1, 0, 15));
        cardPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Initialisation des champs

        // 1. MARQUE
        String[] marques = {"DACIA", "RENAULT", "PEUGEOT", "VOLKSWAGEN", "HYUNDAI", "TOYOTA", "MERCEDES-BENZ", "BMW"};
        cbMarque = createModernCombo(marques);
        addFormGroup(cardPanel, "MARQUE", cbMarque);

        // 2. MODÈLE
        cbModele = createModernCombo(new String[]{});
        addFormGroup(cardPanel, "MODELE", cbModele);

        // 3. ANNÉE (Remplacé par ComboBox pour le style)
        // Génération dynamique des années (Année en cours -> 1990)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = 1990;
        String[] annees = new String[currentYear - startYear + 1];
        for (int i = 0; i < annees.length; i++) {
            annees[i] = String.valueOf(currentYear - i);
        }
        cbAnnee = createModernCombo(annees);
        addFormGroup(cardPanel, "ANNEE", cbAnnee);

        // 4. CARBURANT
        cbCarburant = createModernCombo(new String[]{"DIESEL", "ESSENCE", "HYBRIDE", "ELECTRIQUE"});
        addFormGroup(cardPanel, "CARBURANT", cbCarburant);

        // 5. BOÎTE
        cbBoite = createModernCombo(new String[]{"MANUELLE", "AUTOMATIQUE"});
        addFormGroup(cardPanel, "BOITE DE VITESSE", cbBoite);

        // 6. KILOMÉTRAGE
        txtKm = createModernTextField("100000");
        addFormGroup(cardPanel, "KILOMETRAGE", txtKm);

        this.add(cardPanel);

        // Bouton avec effet de survol
        btnEstimer = new ModernButton("ESTIMER LE PRIX");
        btnEstimer.setBounds(40, 630, 420, 50);
        this.add(btnEstimer);

        // Zone de résultat avec animation
        lblResultat = new JLabel("", SwingConstants.CENTER);
        lblResultat.setFont(FONT_RESULT);
        lblResultat.setForeground(new Color(SUCCESS_COLOR.getRed(), SUCCESS_COLOR.getGreen(), SUCCESS_COLOR.getBlue(), 0));
        lblResultat.setBounds(40, 690, 420, 30);
        this.add(lblResultat);
    }

    // --- Méthodes pour le Controller (EstimationController) ---

    public JButton getBtnEstimer() { return btnEstimer; }

    public String getSelectedMarque() { return (String) cbMarque.getSelectedItem(); }

    public String getSelectedModele() { return (String) cbModele.getSelectedItem(); }

    // Correction : Conversion String -> int pour l'année
    public int getSelectedAnnee() {
        try {
            return Integer.parseInt((String) cbAnnee.getSelectedItem());
        } catch (NumberFormatException e) {
            return 2020; // Valeur par défaut en cas d'erreur
        }
    }

    public String getSelectedCarburant() { return (String) cbCarburant.getSelectedItem(); }

    public String getSelectedBoite() { return (String) cbBoite.getSelectedItem(); }

    public String getKmText() {
        if (txtKm != null) {
            return txtKm.getText();
        }
        return "";
    }

    public JTextField getKmTextField() {
        return txtKm;
    }

    // Le contrôleur a besoin d'écouter les changements de marque pour mettre à jour les modèles
    public JComboBox<String> getCbMarque() { return cbMarque; }
    public JComboBox<String> getCbModele() { return cbModele; }

    public void updateResult(String text, boolean isError) {
        Timer fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(new ActionListener() {
            float opacity = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    fadeTimer.stop();
                }

                Color baseColor = isError ? ERROR_COLOR : SUCCESS_COLOR;
                Color fadedColor = new Color(
                        baseColor.getRed(),
                        baseColor.getGreen(),
                        baseColor.getBlue(),
                        (int)(opacity * 255)
                );

                lblResultat.setText(text);
                lblResultat.setForeground(fadedColor);
            }
        });
        fadeTimer.start();
    }

    // Surcharge pour compatibilité simple (par défaut pas d'erreur)
    public void updateResult(String text) {
        updateResult(text, false);
    }

    public void showLoading() {
        lblResultat.setText("Calcul en cours...");
        lblResultat.setForeground(new Color(200, 200, 200, 200));
    }

    // --- Helpers graphiques améliorés ---

    private void addFormGroup(JPanel parent, String title, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JLabel l = new JLabel(title.toUpperCase()); // Conversion majuscule automatique
        l.setFont(FONT_LABEL);
        l.setForeground(LABEL_COLOR);
        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        parent.add(p);
    }

    private JComboBox<String> createModernCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setUI(new DarkComboBoxUI());
        cb.setRenderer(new DarkListRenderer());
        cb.setBackground(new Color(30, 40, 60));
        cb.setForeground(TEXT_COLOR);
        cb.setFont(FONT_INPUT);
        cb.setFocusable(true);
        return cb;
    }

    private JTextField createModernTextField(String text) {
        JTextField txt = new JTextField(text) {
            private boolean focused = false;

            {
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        focused = true;
                        repaint();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        focused = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond
                g2.setColor(new Color(30, 40, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Bordure
                if (focused) {
                    g2.setColor(ACCENT_COLOR);
                    g2.setStroke(new BasicStroke(2f));
                } else {
                    g2.setColor(BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1.5f));
                }
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);

                // Texte
                super.paintComponent(g);
            }
        };

        txt.setForeground(TEXT_COLOR);
        txt.setCaretColor(ACCENT_COLOR);
        txt.setFont(FONT_INPUT);
        txt.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        txt.setOpaque(false);

        return txt;
    }

    // --- Classes Internes pour le Design ---

    class HeaderPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dégradé de fond
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(20, 30, 50),
                    getWidth(), 0, new Color(15, 20, 35)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Ligne d'accent en bas
            g2.setColor(ACCENT_COLOR);
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(0, getHeight()-2, getWidth(), getHeight()-2);

            // Texture de points subtile
            g2.setColor(new Color(255, 255, 255, 10));
            for (int i = 0; i < getWidth(); i += 20) {
                for (int j = 0; j < getHeight(); j += 20) {
                    if ((i + j) % 40 == 0) {
                        g2.fillOval(i, j, 1, 1);
                    }
                }
            }
        }
    }

    class ModernButton extends JButton {
        private boolean hover = false;

        public ModernButton(String text) {
            super(text);
            setFont(FONT_BUTTON);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Effet de survol
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Dégradé principal
            GradientPaint gp;
            if (hover) {
                gp = new GradientPaint(0, 0, new Color(0, 170, 255),
                        getWidth(), 0, new Color(0, 100, 200));
            } else {
                gp = new GradientPaint(0, 0, ACCENT_COLOR,
                        getWidth(), 0, new Color(0, 120, 220));
            }
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            // Effet de lumière interne
            if (hover) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(1, 1, getWidth()-2, getHeight()/2, 12, 12);
            }

            // Bordure subtile
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);

            // Texte avec ombre
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(getText(), g2);
            int x = (getWidth() - (int) r.getWidth()) / 2;
            int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();

            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawString(getText(), x+1, y+1);

            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        private boolean shadow;

        public RoundedPanel(int radius, Color bgColor) {
            this(radius, bgColor, true);
        }

        public RoundedPanel(int radius, Color bgColor, boolean shadow) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.shadow = shadow;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Effet d'ombre
            if (shadow) {
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(2, 3, getWidth()-4, getHeight()-4, radius, radius);
            }

            // Fond principal
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Effet de brillance subtile en haut
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 20),
                    0, getHeight()/3, new Color(255, 255, 255, 0)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight()/3, radius, radius);

            super.paintComponent(g);
        }
    }

    // ComboBox UI Customization
    class DarkComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton() {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Fond du bouton
                    g2.setColor(new Color(30, 40, 60));
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    // Flèche moderne
                    g2.setColor(ACCENT_COLOR);
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    int x = getWidth() / 2;
                    int y = getHeight() / 2;
                    int size = 5;

                    g2.drawLine(x - size, y - size/2, x, y + size/2);
                    g2.drawLine(x, y + size/2, x + size, y - size/2);
                }
            };
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    bounds.x, bounds.y, new Color(35, 45, 65),
                    bounds.x, bounds.y + bounds.height, new Color(30, 40, 60)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);

            if (hasFocus) {
                g2.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 50));
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);
            }
        }
    }

    class DarkListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setFont(FONT_INPUT);

            if (isSelected) {
                setBackground(ACCENT_COLOR);
                setForeground(Color.WHITE);
            } else {
                setBackground(new Color(40, 45, 60));
                setForeground(TEXT_COLOR);
            }
            return this;
        }
    }
}