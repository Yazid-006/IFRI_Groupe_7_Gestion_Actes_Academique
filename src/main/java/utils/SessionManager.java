package utils;

import model.Personne;
import model.Administrateur;
import model.AgentAdministratif;
import model.Usager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionManager {

    private static SessionManager instance;
    private Personne utilisateurConnecte;
    private String token;
    private long derniereActivite;

    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(Personne utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.derniereActivite = System.currentTimeMillis();
        this.token = genererToken();
        log.info("Session démarrée pour: {}", utilisateur.getEmail());
    }

    public void logout() {
        if (utilisateurConnecte != null) {
            log.info("Session terminée pour: {}", utilisateurConnecte.getEmail());
            this.utilisateurConnecte = null;
            this.token = null;
        }
    }

    public boolean isLoggedIn() {
        return utilisateurConnecte != null && !isSessionExpired();
    }

    private boolean isSessionExpired() {
        return System.currentTimeMillis() - derniereActivite > SESSION_TIMEOUT;
    }

    public void updateActivite() {
        this.derniereActivite = System.currentTimeMillis();
    }

    public Personne getUtilisateurConnecte() {
        updateActivite();
        return utilisateurConnecte;
    }

    public boolean isAdmin() {
        return utilisateurConnecte instanceof Administrateur;
    }

    public boolean isAgent() {
        return utilisateurConnecte instanceof AgentAdministratif;
    }

    public boolean isUsager() {
        return utilisateurConnecte instanceof Usager;
    }

    public Administrateur getAdmin() {
        return isAdmin() ? (Administrateur) utilisateurConnecte : null;
    }

    public AgentAdministratif getAgent() {
        return isAgent() ? (AgentAdministratif) utilisateurConnecte : null;
    }

    public Usager getUsager() {
        return isUsager() ? (Usager) utilisateurConnecte : null;
    }

    public String getToken() {
        return token;
    }

    private String genererToken() {
        return java.util.UUID.randomUUID().toString();
    }

}