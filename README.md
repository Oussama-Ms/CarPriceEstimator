# 🚗 AutoValue - Car Price Estimator (Morocco)

**AutoValue** is a Java-based Machine Learning application designed to estimate the market value of used cars in Morocco. It aggregates data from the country's largest classifieds websites (**Avito.ma** and **Moteur.ma**), processes it via a unified ETL pipeline, and predicts prices using a **Random Forest** algorithm powered by the Weka library.

---

## 📋 Features

### 1. 🕷️ Multi-Source Web Scraping
* **Engine:** `Jsoup` for HTML parsing.
* **Sources:**
    * **Avito.ma:** Extracts listings for major brands (Dacia, VW, Peugeot, etc.).
    * **Moteur.ma:** Handles smart pagination and extracts detailed specs (Year, Gearbox, Fuel).
* **Resilience:** Includes anti-blocking measures (random pauses) and error handling for missing attributes.

### 2. 🧬 Data Merging & Standardization
* **Unified Format:** Integrates disjointed CSV files from different sources into a single standard format:
    `Titre;Prix;Année;Kilometrage;Boite;Carburant;Marque;Modele;Ville;Image;Lien`
* **Smart Merging:** Handles different column orderings from Avito and Moteur.ma automatically via `UnifiedCsvMerger`.

### 3. 🔄 ETL Pipeline (Extract, Transform, Load)
* **Cleaning:**
    * Filters out unrealistic prices (e.g., < 5,000 DH) and invalid years (< 1980 or > 2026).
    * Normalizes text (e.g., standardizes "Manuelle" vs "Boite Manuelle").
* **Storage:** Persists clean, structured data into a **MySQL** database.

### 4. 🧠 Machine Learning
* **Algorithm:** Random Forest Regression (Weka).
* **Training:** Builds a predictive model using the cleaned historical data.
* **Validation:** Performs 10-Fold Cross-Validation to ensure accuracy (calculates MAE and Correlation Coefficient).

---

## 🛠️ Tech Stack

* **Language:** Java 17
* **Build Tool:** Maven
* **Database:** MySQL 8.0
* **ML Library:** Weka 3.8
* **Scraping:** Jsoup 1.17
* **Testing:** JUnit 5

---

## 📂 Project Structure

```text
src/main/java/ma/projet/
├── config/
│   ├── AppConstants.java       # File paths & Global constants
│   └── DBConnection.java       # Singleton MySQL connection
├── controller/                 # Bridges View and Service layers
├── dao/                        # Data Access Objects (CRUD operations)
├── model/                      # POJOs (Vehicule.java)
├── service/
│   ├── etl/
│   │   ├── CsvImportService.java    # Reads CSVs & loads DB
│   │   └── DataCleaningService.java # Normalizes strings & numbers
│   ├── ml/
│   │   ├── ModelTrainerService.java # Weka training logic
│   │   └── PricePredictionService.java # Prediction logic
│   └── scraping/
│       ├── AvitoScraper.java   # Scraper for Avito
│       └── MoteurScraper.java  # Scraper for Moteur.ma
├── util/
│   └── UnifiedCsvMerger.java   # Merges CSVs from both scrapers
├── view/                       # GUI / Console Interface components
└── Main.java                   # Application Entry Point (CLI)

src/main/resources/
├── data/                       # Directory for generated CSV files
├── sql/
│   └── shema.sql               # Database creation script
└── car_price_model.model       # The trained binary model (auto-generated)
