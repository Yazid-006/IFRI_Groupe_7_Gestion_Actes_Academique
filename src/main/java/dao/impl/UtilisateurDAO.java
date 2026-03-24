package dao.impl;

import dao.interfaces.IUtilisateurDAO;
import model.*;
import dao.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UtilisateurDAO implements IUtilisateurDAO {

    @Override
    public Usager saveUsager(Usager usager) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(usager);
            transaction.commit();
            log.info("Usager créé avec succès: {}", usager.getNumeroUsager());
            return usager;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la création de l'usager: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Usager> findUsagerById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Usager usager = session.get(Usager.class, id);
            return Optional.ofNullable(usager);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'usager par ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usager> findUsagerByNumero(String numeroUsager) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usager> query = session.createQuery(
                    "FROM Usager WHERE numeroUsager = :numero", Usager.class);
            query.setParameter("numero", numeroUsager);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'usager par numéro: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Usager> findAllUsagers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usager> query = session.createQuery("FROM Usager", Usager.class);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des usagers: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public Usager updateUsager(Usager usager) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(usager);
            transaction.commit();
            log.info("Usager mis à jour avec succès: {}", usager.getNumeroUsager());
            return usager;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la mise à jour de l'usager: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteUsager(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Usager usager = session.get(Usager.class, id);
            if (usager != null) {
                session.remove(usager);
                log.info("Usager supprimé avec succès: ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression de l'usager: {}", e.getMessage());
        }
    }

    @Override
    public AgentAdministratif saveAgent(AgentAdministratif agent) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Définir un mot de passe par défaut si non fourni
            if (agent.getMotDePasse() == null || agent.getMotDePasse().isEmpty()) {
                agent.setMotDePasse("agent123");
            }

            session.persist(agent);
            transaction.commit();
            log.info("Agent créé avec succès: {}", agent.getMatricule());
            return agent;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la création de l'agent: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<AgentAdministratif> findAgentById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AgentAdministratif agent = session.get(AgentAdministratif.class, id);
            return Optional.ofNullable(agent);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'agent par ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<AgentAdministratif> findAgentByMatricule(String matricule) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AgentAdministratif> query = session.createQuery(
                    "FROM AgentAdministratif WHERE matricule = :matricule", AgentAdministratif.class);
            query.setParameter("matricule", matricule);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'agent par matricule: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<AgentAdministratif> findAllAgents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AgentAdministratif> query = session.createQuery("FROM AgentAdministratif", AgentAdministratif.class);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des agents: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public AgentAdministratif updateAgent(AgentAdministratif agent) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(agent);
            transaction.commit();
            log.info("Agent mis à jour avec succès: {}", agent.getMatricule());
            return agent;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la mise à jour de l'agent: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteAgent(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            AgentAdministratif agent = session.get(AgentAdministratif.class, id);
            if (agent != null) {
                session.remove(agent);
                log.info("Agent supprimé avec succès: ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression de l'agent: {}", e.getMessage());
        }
    }

    @Override
    public Administrateur saveAdmin(Administrateur admin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(admin);
            transaction.commit();
            log.info("Administrateur créé avec succès: {}", admin.getLogin());
            return admin;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la création de l'administrateur: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Administrateur> findAdminById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Administrateur admin = session.get(Administrateur.class, id);
            return Optional.ofNullable(admin);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'administrateur par ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Administrateur> findAdminByLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Administrateur> query = session.createQuery(
                    "FROM Administrateur WHERE login = :login", Administrateur.class);
            query.setParameter("login", login);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'administrateur par login: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Administrateur> findAllAdmins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Administrateur> query = session.createQuery("FROM Administrateur", Administrateur.class);
            return query.list();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des administrateurs: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public Administrateur updateAdmin(Administrateur admin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(admin);
            transaction.commit();
            log.info("Administrateur mis à jour avec succès: {}", admin.getLogin());
            return admin;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la mise à jour de l'administrateur: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteAdmin(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Administrateur admin = session.get(Administrateur.class, id);
            if (admin != null) {
                session.remove(admin);
                log.info("Administrateur supprimé avec succès: ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression de l'administrateur: {}", e.getMessage());
        }
    }

    @Override
    public Optional<Personne> authenticate(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Chercher dans les administrateurs

            Query<Administrateur> adminQuery = session.createQuery(
                    "FROM Administrateur WHERE email = :email AND motDePasse = :password",
                    Administrateur.class);
            adminQuery.setParameter("email", email);
            adminQuery.setParameter("password", password);
            Optional<Administrateur> admin = adminQuery.uniqueResultOptional();
            if (admin.isPresent()) {
                return Optional.of(admin.get());
            }

            // Chercher dans les agents

            Query<AgentAdministratif> agentQuery = session.createQuery(
                    "FROM AgentAdministratif WHERE email = :email AND motDePasse = :password",
                    AgentAdministratif.class);
            agentQuery.setParameter("email", email);
            agentQuery.setParameter("password", password);
            Optional<AgentAdministratif> agent = agentQuery.uniqueResultOptional();
            if (agent.isPresent()) {
                return Optional.of(agent.get());
            }

            // Chercher dans les usagers

            Query<Usager> usagerQuery = session.createQuery(
                    "FROM Usager WHERE email = :email",
                    Usager.class);
            usagerQuery.setParameter("email", email);
            Optional<Usager> usager = usagerQuery.uniqueResultOptional();
            return Optional.ofNullable(usager.orElse(null));

        } catch (Exception e) {
            log.error("Erreur lors de l'authentification: {}", e.getMessage());
            return Optional.empty();
        }
    }

}