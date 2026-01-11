package projet.view;

import javax.swing.*;
import java.awt.*;

// Implements "Classe Abstraite" requirement
public abstract class AbstractView extends JPanel {

    // Palette de couleurs professionnelle "Executive Dashboard"
    protected static final Color BG_DARK = new Color(15, 20, 30);        // Bleu nuit profond
    protected static final Color CARD_BG = new Color(25, 30, 45);        // Bleu ardoise
    protected static final Color ACCENT_COLOR = new Color(0, 150, 255);  // Bleu professionnel vif
    protected static final Color SECONDARY_COLOR = new Color(100, 200, 255); // Bleu clair pour effets
    protected static final Color TEXT_COLOR = new Color(240, 245, 255);  // Blanc légèrement bleuté
    protected static final Color LABEL_COLOR = new Color(180, 190, 210); // Gris bleuté clair
    protected static final Color BORDER_COLOR = new Color(40, 50, 70);   // Bordure subtile
    protected static final Color SUCCESS_COLOR = new Color(46, 204, 113); // Vert succès
    protected static final Color ERROR_COLOR = new Color(231, 76, 60);   // Rouge erreur

    // Polices professionnelles
    protected static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    protected static final Font FONT_SUBTITLE = new Font("Segoe UI Light", Font.PLAIN, 16);
    protected static final Font FONT_BUTTON = new Font("Segoe UI Semibold", Font.PLAIN, 16);
    protected static final Font FONT_LABEL = new Font("Segoe UI Semibold", Font.PLAIN, 11);
    protected static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    protected static final Font FONT_RESULT = new Font("Segoe UI", Font.BOLD, 18);

    public AbstractView() {
        this.setBackground(BG_DARK);
        initLayout(); // Template method pattern
        initStyling();
    }

    // Abstract method forcing subclasses to define their layout
    protected abstract void initLayout();

    protected void initStyling() {
        // Default styling logic common to all views
        this.setLayout(new BorderLayout());
    }

    // Méthode utilitaire pour créer des gradients
    protected GradientPaint createVerticalGradient(int height, Color topColor, Color bottomColor) {
        return new GradientPaint(0, 0, topColor, 0, height, bottomColor);
    }

    protected GradientPaint createHorizontalGradient(int width, Color leftColor, Color rightColor) {
        return new GradientPaint(0, 0, leftColor, width, 0, rightColor);
    }
}