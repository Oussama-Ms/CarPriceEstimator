package projet;

//import com.formdev.flatlaf.FlatDarkLaf; // Optional: If you have FlatLaf dependency, otherwise standard UI
import projet.controller.EstimationController;
import projet.view.MainFrame;

import javax.swing.*;

public class AutoValueApp {

    public static void main(String[] args) {
        // Setup Look and Feel (Optional but recommended for modern look)
        try {
            // If you added FlatLaf to pom.xml:
            // UIManager.setLookAndFeel(new FlatDarkLaf());

            // Otherwise, use System default:
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch GUI on Event Dispatch Thread (Best Practice)
        SwingUtilities.invokeLater(() -> {
            // 1. Create View
            MainFrame view = new MainFrame();

            // 2. Create Controller (Inject View)
            new EstimationController(view);

            // 3. Show View
            view.setVisible(true);
        });
    }
}