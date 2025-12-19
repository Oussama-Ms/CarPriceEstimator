-- 1. Create the Database
CREATE DATABASE IF NOT EXISTS car_prediction_db;
USE car_prediction_db;

-- 2. Create the Table
CREATE TABLE IF NOT EXISTS vehicule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255),
    marque VARCHAR(50),
    modele VARCHAR(50),
    annee INT,
    prix INT,
    kilometrage INT,
    carburant VARCHAR(50),
    boite_vitesse VARCHAR(50),
    ville VARCHAR(100),
    image TEXT,
    lien TEXT
);