package service;

import dao.interfaces.IUtilisateurDAO;
import dao.impl.UtilisateurDAO;
import lombok.NonNull;
import model.Usager;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UsagerService {

    private final IUtilisateurDAO utilisateurDAO;

    public UsagerService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public Usager creerUsager(Usager usager) {
        if (usager == null) {
            log.error("Tentative de création d'un usager null");
            return null;
        }

        // Générer un numéro unique
        usager.setNumeroUsager(genererNumeroUsager());
        usager.setDateCreation(LocalDateTime.now());

        return utilisateurDAO.saveUsager(usager);
    }

    public Optional<Usager> trouverParId(int id) {
        return utilisateurDAO.findUsagerById(id);
    }

    public Optional<Usager> trouverParNumero(String numero) {
        return utilisateurDAO.findUsagerByNumero(numero);
    }

    public List<Usager> listerTous() {
        return utilisateurDAO.findAllUsagers();
    }

    public Usager modifierUsager(Usager usager) {
        if (usager == null || usager.getId() <= 0) {
            log.error("Tentative de modification d'un usager invalide");
            return null;
        }
        return utilisateurDAO.updateUsager(usager);
    }

    public boolean supprimerUsager(int id) {
        try {
            utilisateurDAO.deleteUsager(id);
            return true;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'usager {}: {}", id, e.getMessage());
            return false;
        }
    }

    private @NonNull String genererNumeroUsager() {
        return "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}