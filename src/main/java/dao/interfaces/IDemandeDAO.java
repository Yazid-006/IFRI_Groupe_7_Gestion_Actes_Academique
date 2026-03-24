package dao.interfaces;

import model.Demande;
import model.enums.StatutDemande;
import model.enums.TypeActe;
import java.util.List;
import java.util.Optional;

public interface IDemandeDAO {

    Demande save(Demande demande);

    Optional<Demande> findById(int id);

    Optional<Demande> findByNumero(String numeroDemande);

    List<Demande> findAll();

    List<Demande> findByUsager(int usagerId);

    List<Demande> findByStatut(StatutDemande statut);

    List<Demande> findByTypeActe(TypeActe typeActe);

    List<Demande> findByAgentTraiteur(int agentId);

    Demande update(Demande demande);

    void delete(int id);

    long countByStatut(StatutDemande statut);

}