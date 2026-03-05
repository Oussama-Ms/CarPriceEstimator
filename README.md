# 🚗 AutoValue - Full-Stack Car Price Estimator (Morocco)

**AutoValue** is a complete, cloud-deployed Machine Learning web application designed to estimate the market value of used cars in Morocco. 

Originally built as a local Java application, this project has evolved into a modern, decoupled Full-Stack platform. It aggregates data from the country's largest classifieds websites (**Avito.ma** and **Moteur.ma**), processes it via a custom ETL pipeline, and serves real-time predictions through a React frontend connected to a Spring Boot REST API and a Weka Random Forest model.

🌐 **Live Demo:** [https://autovalue-sage.vercel.app/]

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
* **Storage:** Persists clean, structured data into a cloud-hosted relational database.

### 4. 🧠 Machine Learning Integration
* **Algorithm:** Random Forest Regression (Weka).
* **Training:** Builds a predictive model using the cleaned historical data.
* **In-Memory Execution:** The pre-trained `.model` file is loaded directly into the Spring Boot server's RAM on startup for millisecond-latency predictions.

### 5. ☁️ Modern Cloud Architecture (New)
* **Decoupled System:** A React UI that consumes a dedicated Spring Boot REST API.
* **API Proxying:** Configured Vercel rewrites to securely bypass Cross-Origin (CORS) and CSRF restrictions.
* **Automated Deployments:** Fully integrated with GitHub for CI/CD pipelines.

---

## 🛠️ Tech Stack

**Frontend (Client Layer)**
* **Framework:** React.js (Vite)
* **Networking:** Axios
* **Hosting:** Vercel

**Backend (API & ML Layer)**
* **Framework:** Java 17, Spring Boot, Spring Data JPA
* **Machine Learning:** Weka 3.8
* **Testing:** JUnit 5
* **Containerization:** Docker
* **Hosting:** Hugging Face Spaces (16GB RAM Container)

**Data & Pipeline Layer**
* **Database:** MySQL 8 (Hosted on Aiven Cloud)
* **Scraping:** Jsoup 1.17

---

## 📂 Project Structure

```text
AutoValue/
├── frontend/                   # React.js UI Application (Vite)
│   ├── public/
│   │   └── assets/             # Static assets (backgrounds, car brand logos)
│   ├── src/
│   │   ├── components/         # React components (LandingPage, PredictionForm, etc.)
│   │   ├── services/           # Axios API configuration (api.js)
│   │   ├── App.jsx             # Main application component
│   │   └── main.jsx            # React application entry point
│   ├── package.json            # Node.js dependencies
│   ├── vercel.json             # Vercel API proxy configuration for deployment
│   └── vite.config.js          # Vite build configuration
│
├── src/main/java/projet/       # Spring Boot Backend
│   ├── controller/             # REST API Endpoints (PredictionController.java)
│   ├── model/                  # JPA Entities (Vehicule.java)
│   ├── repository/             # Spring Data JPA Interfaces (VehiculeRepository.java)
│   ├── service/
│   │   ├── etl/                # Data cleaning logic (DataCleaningService.java)
│   │   └── ml/                 # Weka Model loading and Prediction (PricePredictionService.java)
│   └── CarPriceEstimatorApplication.java # Spring Boot Entry Point
│
├── src/main/resources/         # Backend Resources
│   ├── data/                   # Raw CSV datasets (BMW, Dacia, Peugeot, etc.)
│   ├── sql/
│   │   └── shema.sql           # Database table initialization script
│   ├── application.properties  # Database credentials & environment variables
│   └── car_price_model.model   # Pre-trained Weka Random Forest binary
│
├── src/test/java/projet/       # Unit & Integration Tests
│
├── Dockerfile                  # Container instructions for Hugging Face deployment
└── pom.xml                     # Maven backend dependencies