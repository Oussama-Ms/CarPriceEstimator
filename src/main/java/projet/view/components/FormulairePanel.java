package projet.view.components;

import projet.view.AbstractView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Calendar;

public class FormulairePanel extends AbstractView {

    private JComboBox<String> cbMarque, cbModele, cbCarburant, cbBoite, cbAnnee;
    private JTextField txtKm;
    private JLabel lblResultat;
    private JButton btnEstimer;

    // Constantes de design avancées
    private static final Color GLASS_BG = new Color(30, 35, 45, 200); // Semi-transparent
    private static final Color GLASS_BORDER = new Color(255, 255, 255, 30); // Bordure subtile
    // Nouvelle couleur pour le survol (hover) - un bleu cyan plus clair
    private static final Color HOVER_COLOR = new Color(100, 220, 255);


    public FormulairePanel() {
        super();
    }

    @Override
    protected void initLayout() {
        this.setLayout(null);

        // --- HEADER ---
        JPanel headerPanel = new HeaderPanel();
        headerPanel.setBounds(0, 0, 500, 100);
        headerPanel.setLayout(null);

        JLabel lblTitle = new JLabel("AUTOVALUE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(40, 25, 300, 45);
        headerPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("AI Price Estimator Pro");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(100, 200, 255));
        lblSubtitle.setBounds(42, 65, 250, 20);
        headerPanel.add(lblSubtitle);

        this.add(headerPanel);

        // --- CARTE "GLASSMORPHISM" ---
        JPanel cardPanel = new GlassPanel(25);
        cardPanel.setBounds(35, 120, 430, 490);
        cardPanel.setLayout(new GridLayout(6, 1, 0, 18));
        cardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Initialisation des champs
        String[] marques = {"DACIA", "RENAULT", "PEUGEOT", "VOLKSWAGEN", "HYUNDAI", "TOYOTA", "MERCEDES-BENZ", "BMW"};
        cbMarque = createNeonCombo(marques);
        addFormGroup(cardPanel, "MARQUE", cbMarque);

        cbModele = createNeonCombo(new String[]{});
        addFormGroup(cardPanel, "MODELE", cbModele);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] annees = new String[currentYear - 1990 + 1];
        for (int i = 0; i < annees.length; i++) annees[i] = String.valueOf(currentYear - i);
        cbAnnee = createNeonCombo(annees);
        addFormGroup(cardPanel, "ANNEE", cbAnnee);

        cbCarburant = createNeonCombo(new String[]{"DIESEL", "ESSENCE", "HYBRIDE", "ELECTRIQUE"});
        addFormGroup(cardPanel, "CARBURANT", cbCarburant);

        cbBoite = createNeonCombo(new String[]{"MANUELLE", "AUTOMATIQUE"});
        addFormGroup(cardPanel, "BOITE DE VITESSE", cbBoite);

        txtKm = createNeonTextField("100000");
        addFormGroup(cardPanel, "KILOMETRAGE", txtKm);

        this.add(cardPanel);

        // --- BOUTON "CALL TO ACTION" ---
        btnEstimer = new GlowButton("ESTIMER LE PRIX");
        btnEstimer.setBounds(35, 630, 430, 55);
        this.add(btnEstimer);

        // --- RESULTAT ---
        lblResultat = new JLabel("", SwingConstants.CENTER);
        lblResultat.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblResultat.setForeground(SUCCESS_COLOR);
        lblResultat.setBounds(35, 700, 430, 30);
        this.add(lblResultat);
    }

    // --- Getters et Logique ---
    public JButton getBtnEstimer() { return btnEstimer; }
    public String getSelectedMarque() { return (String) cbMarque.getSelectedItem(); }
    public String getSelectedModele() { return (String) cbModele.getSelectedItem(); }
    public int getSelectedAnnee() {
        try { return Integer.parseInt((String) cbAnnee.getSelectedItem()); }
        catch (Exception e) { return 2020; }
    }
    public String getSelectedCarburant() { return (String) cbCarburant.getSelectedItem(); }
    public String getSelectedBoite() { return (String) cbBoite.getSelectedItem(); }
    public String getKmText() { return txtKm != null ? txtKm.getText() : ""; }
    public JTextField getKmTextField() { return txtKm; }
    public JComboBox<String> getCbMarque() { return cbMarque; }
    public JComboBox<String> getCbModele() { return cbModele; }

    public void updateResult(String text, boolean isError) {
        lblResultat.setText(text);
        lblResultat.setForeground(isError ? ERROR_COLOR : SUCCESS_COLOR);
    }
    public void updateResult(String text) { updateResult(text, false); }
    public void showLoading() { lblResultat.setText("Analyse IA en cours..."); lblResultat.setForeground(Color.LIGHT_GRAY); }

    // --- NOUVEAUX HELPERS GRAPHIQUES "NEON" ---

    private void addFormGroup(JPanel parent, String title, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(150, 160, 180));
        l.setBorder(new EmptyBorder(0, 2, 0, 0));
        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        parent.add(p);
    }

    private JTextField createNeonTextField(String text) {
        return new NeonTextField(text);
    }

    private JComboBox<String> createNeonCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setUI(new NeonComboBoxUI());
        cb.setRenderer(new DarkListRenderer());
        cb.setBackground(new Color(20, 25, 35));
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Ajout d'un listener pour détecter le survol sur le JComboBox lui-même
        cb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cb.putClientProperty("isHovered", true);
                cb.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cb.putClientProperty("isHovered", false);
                cb.repaint();
            }
        });
        return cb;
    }

    // --- CLASSES GRAPHIQUES AVANCÉES ---

    // 1. Panneau Effet Verre (Glassmorphism)
    class GlassPanel extends JPanel {
        private int radius;
        public GlassPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fond semi-transparent
            g2.setColor(GLASS_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Bordure brillante subtile
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(GLASS_BORDER);
            g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, radius, radius);
        }
    }

    // 2. TextField avec effet de Lueur au focus ET au survol
    class NeonTextField extends JTextField {
        private boolean isFocused = false;
        private boolean isHovered = false; // Nouvel état

        public NeonTextField(String text) {
            super(text);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(Color.WHITE);
            setCaretColor(ACCENT_COLOR);
            setBorder(new EmptyBorder(10, 15, 10, 15));

            // Listeners pour Focus et Hover
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { isFocused = true; repaint(); }
                public void focusLost(FocusEvent e) { isFocused = false; repaint(); }
            });
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fond
            g2.setColor(new Color(20, 25, 35));
            g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);

            // Bordure & Glow
            if (isFocused) {
                // État Focus (Clic) : Gros Glow Bleu
                g2.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 11, 11);

                g2.setColor(ACCENT_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
            } else if (isHovered) {
                // État Hover (Survol) : Bordure Cyan claire
                g2.setColor(HOVER_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
            } else {
                // État Normal : Bordure Grise
                g2.setColor(new Color(60, 70, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
            }
            super.paintComponent(g);
        }
    }

    // 3. UI ComboBox avec le même style Neon + Hover
    class NeonComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton() {
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Vérifie si le combobox est survolé (propriété client définie dans le createNeonCombo)
                    boolean isHovered = Boolean.TRUE.equals(comboBox.getClientProperty("isHovered"));
                    Color arrowColor;

                    if (isPopupVisible(comboBox) || comboBox.isFocusOwner()) {
                        arrowColor = ACCENT_COLOR;
                    } else if (isHovered) {
                        arrowColor = HOVER_COLOR; // Couleur de survol
                    } else {
                        arrowColor = Color.GRAY;
                    }

                    g2.setColor(arrowColor);
                    int w = 10, h = 6;
                    int x = (getWidth() - w) / 2;
                    int y = (getHeight() - h) / 2;

                    Path2D p = new Path2D.Double();
                    p.moveTo(x, y);
                    p.lineTo(x + w/2.0, y + h);
                    p.lineTo(x + w, y);

                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(p);
                }
            };
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setContentAreaFilled(false);
            return btn;
        }

        @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fond
            g2.setColor(new Color(20, 25, 35));
            g2.fillRoundRect(2, 2, c.getWidth()-4, c.getHeight()-4, 10, 10);

            // Vérifie si le combobox est survolé
            boolean isHovered = Boolean.TRUE.equals(comboBox.getClientProperty("isHovered"));

            // Bordure
            if (comboBox.isFocusOwner() || isPopupVisible(comboBox)) {
                g2.setColor(ACCENT_COLOR); // Focus
                g2.setStroke(new BasicStroke(1.5f));
            } else if (isHovered) {
                g2.setColor(HOVER_COLOR); // Hover
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(new Color(60, 70, 90)); // Normal
                g2.setStroke(new BasicStroke(1f));
            }
            g2.drawRoundRect(2, 2, c.getWidth()-4, c.getHeight()-4, 10, 10);

            Rectangle r = rectangleForCurrentValue();
            paintCurrentValue(g, r, false);
        }
    }

    // 4. Bouton avec Ombre Portée (Drop Shadow) - Déjà ok
    class GlowButton extends JButton {
        private boolean hover = false;
        public GlowButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int offset = hover ? 2 : 4;

            // Ombre portée (Glow sous le bouton)
            g2.setColor(new Color(0, 100, 255, 50));
            g2.fillRoundRect(4, 4 + (4-offset), getWidth()-8, getHeight()-8, 20, 20);

            // Dégradé bouton (Plus clair au survol)
            GradientPaint gp = new GradientPaint(0, 0,
                    hover ? new Color(80, 180, 255) : ACCENT_COLOR,
                    getWidth(), getHeight(),
                    hover ? new Color(0, 120, 240) : new Color(0, 80, 180));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight()-offset, 16, 16);

            // Texte centré
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(getText(), g2);
            int x = (getWidth() - (int) r.getWidth()) / 2;
            int y = (getHeight() - offset - (int) r.getHeight()) / 2 + fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), x, y);
        }
    }

    // 5. Header Simple
    class HeaderPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 50), getWidth(), getHeight(), new Color(10, 15, 25));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255,255,255,20));
            g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
        }
    }

    // 6. Renderer pour la liste déroulante (Pop-up) - Survol amélioré
    class DarkListRenderer extends DefaultListCellRenderer {
        private Font originalFont;
        private Font boldFont;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(new EmptyBorder(8, 15, 8, 15));

            // Initialisation des polices si nécessaire
            if (originalFont == null) originalFont = getFont();
            if (boldFont == null) boldFont = originalFont.deriveFont(Font.BOLD);

            if (isSelected) {
                // Survol dans la liste : Bleu plus fort et texte en gras
                setBackground(new Color(0, 140, 255));
                setForeground(Color.WHITE);
                setFont(boldFont);
            } else {
                // Normal
                setBackground(new Color(30, 35, 45));
                setForeground(Color.LIGHT_GRAY);
                setFont(originalFont);
            }
            return this;
        }
    }
}