package projet.view;

import projet.view.components.FormulairePanel;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private FormulairePanel formPanel;

    public MainFrame() {
        setTitle("AutoValue - AI Price Estimator");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use the AbstractView implementation
        formPanel = new FormulairePanel();

        // Add to Frame
        add(formPanel, BorderLayout.CENTER);
    }

    public FormulairePanel getFormPanel() {
        return formPanel;
    }
}