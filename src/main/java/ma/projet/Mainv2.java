package ma.projet;

import ma.projet.view.AutoValueUI;

import javax.swing.*;

public class Mainv2 {
    public static void main(String[] args) {
        // Swing doit toujours être lancé dans un thread dédié
        SwingUtilities.invokeLater(() -> {
            try {
                // Optionnel : Donne le look "Windows" ou "Mac" natif au lieu du look Java moche
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            AutoValueUI app = new AutoValueUI();
            app.setVisible(true);
            System.out.println("🖥️ Interface graphique lancée !");
        });
    }
}
