package projet.view;

import projet.view.components.FormulairePanel;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private FormulairePanel formPanel;

    public MainFrame() {
        setTitle("AutoValue - AI Cars Price Estimator");
        setSize(500, 800); // Augmenté légèrement pour le nouveau design
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Créer un fond avec texture subtile
        JPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Use the AbstractView implementation
        formPanel = new FormulairePanel();

        // Add to Frame
        backgroundPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(backgroundPanel);

        // Icone de l'application
        try {
            // Vous pouvez ajouter une icône ici
            // setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Silently ignore si l'icône n'est pas trouvée
        }
    }

    public FormulairePanel getFormPanel() {
        return formPanel;
    }

    // Classe interne pour le fond texturé
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Dégradé principal du fond
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(10, 15, 25),
                    0, getHeight(), new Color(20, 30, 40)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Texture de points très subtile
            g2.setColor(new Color(255, 255, 255, 3));
            for (int i = 0; i < getWidth(); i += 20) {
                for (int j = 0; j < getHeight(); j += 20) {
                    if ((i + j) % 40 == 0) {
                        g2.fillOval(i, j, 1, 1);
                    }
                }
            }

            // Lignes de grille très discrètes
            g2.setColor(new Color(255, 255, 255, 5));
            g2.setStroke(new BasicStroke(0.5f));

            // Lignes verticales
            for (int x = 50; x < getWidth(); x += 50) {
                g2.drawLine(x, 0, x, getHeight());
            }

            // Lignes horizontales
            for (int y = 50; y < getHeight(); y += 50) {
                g2.drawLine(0, y, getWidth(), y);
            }
        }
    }
}