package dao.impl;

import dao.interfaces.IActeDAO;
import model.ActeAdministratif;
import model.enums.TypeActe;
import model.enums.StatutActe;
import dao.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ActeDAO implements IActeDAO {

    @Override
    public ActeAdministratif save(ActeAdministratif acte) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(acte);
            transaction.commit();
            log.info("Acte créé avec succès: {}", acte.getNumeroActe());
            return acte;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la création de l'acte: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<ActeAdministratif> findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ActeAdministratif acte = session.get(ActeAdministratif.class, id);
            return Optional.ofNullable(acte);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'acte par ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ActeAdministratif> findByNumero(String numeroActe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActeAdministratif> query = session.createQuery(
                    "FROM ActeAdministratif WHERE numeroActe = :numero", ActeAdministratif.class);
            query.setParameter("numero", numeroActe);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'acte par numéro: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<ActeAdministratif> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActeAdministratif> query = session.createQuery("FROM ActeAdministratif", ActeAdministratif.class);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des actes: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ActeAdministratif> findByUsager(int usagerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActeAdministratif> query = session.createQuery(
                    "FROM ActeAdministratif WHERE usager.id = :usagerId", ActeAdministratif.class);
            query.setParameter("usagerId", usagerId);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des actes par usager: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ActeAdministratif> findByType(TypeActe typeActe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActeAdministratif> query = session.createQuery(
                    "FROM ActeAdministratif WHERE typeActe = :typeActe", ActeAdministratif.class);
            query.setParameter("typeActe", typeActe);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des actes par type: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ActeAdministratif> findByStatut(StatutActe statut) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActeAdministratif> query = session.createQuery(
                    "FROM ActeAdministratif WHERE statut = :statut", ActeAdministratif.class);
            query.setParameter("statut", statut);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des actes par statut: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ActeAdministratif> findByDateEmission(LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActeAdministratif> query = session.createQuery(
                    "FROM ActeAdministratif WHERE DATE(dateEmission) = :date", ActeAdministratif.class);
            query.setParameter("date", date);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des actes par date d'émission: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ActeAdministratif> findByPeriode(LocalDate debut, LocalDate fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActeAdministratif> query = session.createQuery(
                    "FROM ActeAdministratif WHERE DATE(dateEmission) BETWEEN :debut AND :fin",
                    ActeAdministratif.class);
            query.setParameter("debut", debut);
            query.setParameter("fin", fin);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des actes par période: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public ActeAdministratif update(ActeAdministratif acte) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(acte);
            transaction.commit();
            log.info("Acte mis à jour avec succès: {}", acte.getNumeroActe());
            return acte;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la mise à jour de l'acte: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ActeAdministratif acte = session.get(ActeAdministratif.class, id);
            if (acte != null) {
                session.remove(acte);
                log.info("Acte supprimé avec succès: ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression de l'acte: {}", e.getMessage());
        }
    }

    @Override
    public long countByType(TypeActe typeActe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(a) FROM ActeAdministratif a WHERE a.typeActe = :typeActe", Long.class);
            query.setParameter("typeActe", typeActe);
            return query.uniqueResult();
        } catch (Exception e) {
            log.error("Erreur lors du comptage des actes par type: {}", e.getMessage());
            return 0;
        }
    }

}