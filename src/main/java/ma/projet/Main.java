package ma.projet;

import ma.projet.service.etl.CsvImportService;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Démarrage de l'application...");

        // 1. Lancer l'importation ETL
        CsvImportService service = new CsvImportService();
        service.demarrerImportation();
    }
}
