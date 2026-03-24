package dao.impl;

import dao.interfaces.IDemandeDAO;
import model.Demande;
import model.enums.StatutDemande;
import model.enums.TypeActe;
import dao.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
public class DemandeDAO implements IDemandeDAO {

    @Override
    public Demande save(Demande demande) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(demande);
            transaction.commit();
            log.info("Demande créée avec succès: {}", demande.getNumeroDemande());
            return demande;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la création de la demande: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Demande> findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Demande demande = session.get(Demande.class, id);
            return Optional.ofNullable(demande);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de la demande par ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Demande> findByNumero(String numeroDemande) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Demande> query = session.createQuery(
                    "FROM Demande WHERE numeroDemande = :numero", Demande.class);
            query.setParameter("numero", numeroDemande);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de la demande par numéro: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Demande> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Demande> query = session.createQuery("FROM Demande", Demande.class);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des demandes: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Demande> findByUsager(int usagerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Demande> query = session.createQuery(
                    "FROM Demande WHERE demandeur.id = :usagerId", Demande.class);
            query.setParameter("usagerId", usagerId);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des demandes par usager: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Demande> findByStatut(StatutDemande statut) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Demande> query = session.createQuery(
                    "FROM Demande WHERE statut = :statut", Demande.class);
            query.setParameter("statut", statut);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des demandes par statut: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Demande> findByTypeActe(TypeActe typeActe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Demande> query = session.createQuery(
                    "FROM Demande WHERE typeActeDemande = :typeActe", Demande.class);
            query.setParameter("typeActe", typeActe);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des demandes par type d'acte: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Demande> findByAgentTraiteur(int agentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Demande> query = session.createQuery(
                    "FROM Demande WHERE agentTraiteur.id = :agentId", Demande.class);
            query.setParameter("agentId", agentId);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des demandes par agent traitant: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public Demande update(Demande demande) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(demande);
            transaction.commit();
            log.info("Demande mise à jour avec succès: {}", demande.getNumeroDemande());
            return demande;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la mise à jour de la demande: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Demande demande = session.get(Demande.class, id);
            if (demande != null) {
                session.remove(demande);
                log.info("Demande supprimée avec succès: ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression de la demande: {}", e.getMessage());
        }
    }

    @Override
    public long countByStatut(StatutDemande statut) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(d) FROM Demande d WHERE d.statut = :statut", Long.class);
            query.setParameter("statut", statut);
            return query.uniqueResult();
        } catch (Exception e) {
            log.error("Erreur lors du comptage des demandes par statut: {}", e.getMessage());
            return 0;
        }
    }

}