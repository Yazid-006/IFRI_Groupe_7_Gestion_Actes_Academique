package dao.impl;

import dao.interfaces.IDocumentDAO;
import model.DocumentJustificatif;
import dao.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
public class DocumentDAO implements IDocumentDAO {

    @Override
    public DocumentJustificatif save(DocumentJustificatif document) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(document);
            transaction.commit();
            log.info("Document créé avec succès: {}", document.getNomFichier());
            return document;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la création du document: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<DocumentJustificatif> findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            DocumentJustificatif document = session.get(DocumentJustificatif.class, id);
            return Optional.ofNullable(document);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche du document par ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<DocumentJustificatif> findByDemande(int demandeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DocumentJustificatif> query = session.createQuery(
                    "FROM DocumentJustificatif WHERE demande.id = :demandeId", DocumentJustificatif.class);
            query.setParameter("demandeId", demandeId);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des documents par demande: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<DocumentJustificatif> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DocumentJustificatif> query = session.createQuery("FROM DocumentJustificatif", DocumentJustificatif.class);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des documents: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public DocumentJustificatif update(DocumentJustificatif document) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(document);
            transaction.commit();
            log.info("Document mis à jour avec succès: {}", document.getNomFichier());
            return document;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la mise à jour du document: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            DocumentJustificatif document = session.get(DocumentJustificatif.class, id);
            if (document != null) {
                session.remove(document);
                log.info("Document supprimé avec succès: ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression du document: {}", e.getMessage());
        }
    }

    @Override
    public void deleteByDemande(int demandeId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<?> query = session.createQuery(
                    "DELETE FROM DocumentJustificatif WHERE demande.id = :demandeId");
            query.setParameter("demandeId", demandeId);
            int deletedCount = query.executeUpdate();
            transaction.commit();
            log.info("{} documents supprimés pour la demande ID {}", deletedCount, demandeId);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression des documents par demande: {}", e.getMessage());
        }
    }

}