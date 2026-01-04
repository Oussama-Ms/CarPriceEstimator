package projet.dao;

import projet.model.Vehicule;
import java.util.List;

public interface IVehiculeDAO {
    void save(Vehicule v);
    void saveAll(List<Vehicule> vehicules);
    List<Vehicule> findAll();
    void truncate();
}