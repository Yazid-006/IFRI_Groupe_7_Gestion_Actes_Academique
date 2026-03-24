package dao.interfaces;

import model.DocumentJustificatif;
import java.util.List;
import java.util.Optional;

public interface IDocumentDAO {

    DocumentJustificatif save(DocumentJustificatif document);

    Optional<DocumentJustificatif> findById(int id);

    List<DocumentJustificatif> findByDemande(int demandeId);

    List<DocumentJustificatif> findAll();

    DocumentJustificatif update(DocumentJustificatif document);

    void delete(int id);

    void deleteByDemande(int demandeId);

}