package projet.model;

import java.util.Objects;

public class Vehicule {
    private int id;
    private String titre;
    private String marque;
    private String modele;
    private int annee;
    private int prix;
    private int kilometrage;
    private String carburant; // Changed from typeCarburant to match DB column conceptually
    private String boiteVitesse;
    private String ville;
    private String image;
    private String lien;

    public Vehicule() {}

    public Vehicule(String marque, String modele, int annee, int prix, int kilometrage,
                    String carburant, String boiteVitesse, String titre,
                    String ville, String image, String lien) {
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.prix = prix;
        this.kilometrage = kilometrage;
        this.carburant = carburant;
        this.boiteVitesse = boiteVitesse;
        this.titre = titre;
        this.ville = ville;
        this.image = image;
        this.lien = lien;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public int getPrix() { return prix; }
    public void setPrix(int prix) { this.prix = prix; }

    public int getKilometrage() { return kilometrage; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }

    public String getCarburant() { return carburant; }
    public void setCarburant(String carburant) { this.carburant = carburant; }

    public String getBoiteVitesse() { return boiteVitesse; }
    public void setBoiteVitesse(String boiteVitesse) { this.boiteVitesse = boiteVitesse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicule vehicule = (Vehicule) o;
        // Check uniqueness based on Link first (most reliable)
        if (this.lien != null && vehicule.lien != null) {
            return Objects.equals(lien, vehicule.lien);
        }
        // Fallback to fields
        return annee == vehicule.annee &&
                prix == vehicule.prix &&
                kilometrage == vehicule.kilometrage &&
                Objects.equals(marque, vehicule.marque) &&
                Objects.equals(modele, vehicule.modele) &&
                Objects.equals(titre, vehicule.titre);
    }

    @Override
    public int hashCode() {
        if (lien != null) return Objects.hash(lien);
        return Objects.hash(titre, marque, modele, annee, prix, kilometrage);
    }
}