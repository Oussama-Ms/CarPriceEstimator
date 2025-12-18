package ma.projet.service.etl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCleaningService {

    // 1. Nettoyage du PRIX
    public int nettoyerPrix(String prixBrut) {
        if (prixBrut == null || prixBrut.isEmpty()) return 0;
        String prixPropre = prixBrut.replaceAll("[^0-9]", "");
        try {
            if (prixPropre.isEmpty()) return 0;
            return Integer.parseInt(prixPropre);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 2. Nettoyage du KILOMÉTRAGE
    public int nettoyerKilometrage(String kmBrut) {
        if (kmBrut == null || kmBrut.isEmpty()) return 150000; // Default average
        String clean = kmBrut.toLowerCase().trim();

        if (clean.contains("plus de")) return 500000;

        // Handle range "35 000 - 39 999"
        if (clean.contains("-")) {
            try {
                String[] parts = clean.split("-");
                String minStr = parts[0].replaceAll("[^0-9]", "");
                String maxStr = parts[1].replaceAll("[^0-9]", "");
                int min = Integer.parseInt(minStr);
                int max = Integer.parseInt(maxStr);
                return (min + max) / 2;
            } catch (Exception e) {
                return 150000;
            }
        }

        String digitsOnly = clean.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) return 150000;

        try {
            long val = Long.parseLong(digitsOnly);
            if (val < 1000 && val > 0) return (int) val * 1000; // Fix short inputs like "150"
            return (int) val;
        } catch (Exception e) {
            return 150000;
        }
    }

    // 3. Nettoyage de l'ANNÉE (Missing Method)
    public int nettoyerAnnee(String anneeBrut) {
        if (anneeBrut == null) return 2015;
        try {
            // Extract first 4 digits found
            Pattern p = Pattern.compile("(19|20)\\d{2}");
            Matcher m = p.matcher(anneeBrut);
            if (m.find()) {
                return Integer.parseInt(m.group());
            }
            return Integer.parseInt(anneeBrut.trim());
        } catch (Exception e) {
            return 2015; // Default fallback
        }
    }

    // 4. Normalisation Texte (Missing Method)
    public String normaliserTexte(String texte) {
        if (texte == null) return "N/A";
        return texte.trim().toUpperCase().replaceAll(";", ","); // remove CSV delimiters from content
    }
}