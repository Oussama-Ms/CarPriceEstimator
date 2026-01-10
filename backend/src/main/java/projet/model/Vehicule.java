package projet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity // Dit à Spring : "Ceci est une table dans la base de données"
@Table(name = "vehicule")
@Data // Lombok : Génère Getters, Setters, toString automatiquement
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // On préfère Long à int pour les ID en base de données

    private String marque;
    private String modele;

    @Column(name = "annee_modele") // Renommé pour éviter conflit SQL éventuel
    private int annee;

    private double prix;
    private int kilometrage;
    private String carburant;

    @Column(name = "boite_vitesse")
    private String boiteVitesse;

    // Champs optionnels (selon notre ancien projet)
    private String titre;
    private String ville;
    private String image;
    private String lien;
}