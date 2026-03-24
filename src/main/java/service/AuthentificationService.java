package service;

import dao.interfaces.IUtilisateurDAO;
import dao.impl.UtilisateurDAO;
import dao.utils.HibernateUtil;
import model.Personne;
import lombok.extern.slf4j.Slf4j;
import model.Usager;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

@Slf4j
public class AuthentificationService {

    private final IUtilisateurDAO utilisateurDAO;

    public AuthentificationService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public Optional<Personne> login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            log.warn("Tentative de connexion avec email/mot de passe vide");
            return Optional.empty();
        }

        Optional<Personne> personne = utilisateurDAO.authenticate(email, password);

        if (personne.isPresent()) {
            log.info("Connexion réussie pour: {}", email);
        } else {
            log.warn("Échec de connexion pour: {}", email);
        }

        return personne;
    }

    public Optional<Personne> loginUsager(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.warn("Tentative de connexion usager avec email vide");
            return Optional.empty();
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usager> query = session.createQuery(
                    "FROM Usager WHERE email = :email", Usager.class);
            query.setParameter("email", email);
            Optional<Usager> usager = query.uniqueResultOptional();

            if (usager.isPresent()) {
                log.info("Connexion usager réussie pour: {}", email);
                return Optional.of(usager.get());
            } else {
                log.warn("Usager non trouvé avec email: {}", email);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'authentification usager: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void logout(Personne personne) {
        if (personne != null) {
            log.info("Déconnexion de: {}", personne.getEmail());
        }
    }

}