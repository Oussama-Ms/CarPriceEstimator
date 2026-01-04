package projet.view;

import javax.swing.*;
import java.awt.*;

// Implements "Classe Abstraite" requirement
public abstract class AbstractView extends JPanel {

    // Common colors for consistency (Inheritance benefit)
    protected static final Color BG_DARK = new Color(18, 18, 24);
    protected static final Color CARD_BG = new Color(30, 32, 40);
    protected static final Color ACCENT_COLOR = new Color(75, 110, 255);
    protected static final Color TEXT_COLOR = new Color(230, 230, 230);
    protected static final Color LABEL_COLOR = new Color(140, 145, 160);

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
}