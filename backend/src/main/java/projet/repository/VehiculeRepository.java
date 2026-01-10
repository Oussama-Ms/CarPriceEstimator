package projet.repository;

import ma.projet.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    // C'est tout ! Spring génère automatiquement :
    // - save()
    // - findById()
    // - findAll()
    // - delete()

    // Vous pouvez ajouter des méthodes personnalisées juste en les nommant correctement :
    List<Vehicule> findByMarque(String marque);
    List<Vehicule> findByMarqueAndModele(String marque, String modele);
}