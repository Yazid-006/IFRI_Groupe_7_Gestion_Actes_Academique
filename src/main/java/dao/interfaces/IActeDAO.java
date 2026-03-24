package dao.interfaces;

import model.ActeAdministratif;
import model.enums.TypeActe;
import model.enums.StatutActe;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IActeDAO {

    ActeAdministratif save(ActeAdministratif acte);

    Optional<ActeAdministratif> findById(int id);

    Optional<ActeAdministratif> findByNumero(String numeroActe);

    List<ActeAdministratif> findAll();

    List<ActeAdministratif> findByUsager(int usagerId);

    List<ActeAdministratif> findByType(TypeActe typeActe);

    List<ActeAdministratif> findByStatut(StatutActe statut);

    List<ActeAdministratif> findByDateEmission(LocalDate date);

    List<ActeAdministratif> findByPeriode(LocalDate debut, LocalDate fin);

    ActeAdministratif update(ActeAdministratif acte);

    void delete(int id);

    long countByType(TypeActe typeActe);

}