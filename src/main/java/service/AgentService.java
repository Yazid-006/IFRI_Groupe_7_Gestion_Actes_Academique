package service;

import dao.interfaces.IUtilisateurDAO;
import dao.impl.UtilisateurDAO;
import model.AgentAdministratif;
import model.enums.NiveauAcces;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class AgentService {
    private final IUtilisateurDAO utilisateurDAO;

    public AgentService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public AgentAdministratif creerAgent(AgentAdministratif agent) {
        if (agent == null) {
            log.error("Tentative de création d'un agent null");
            return null;
        }

        if (agent.getMatricule() == null || agent.getMatricule().trim().isEmpty()) {
            agent.setMatricule(genererMatricule());
        }

        if (agent.getNiveauAcces() == null) {
            agent.setNiveauAcces(NiveauAcces.LECTURE_SEULE);
        }

        agent.setDateCreation(LocalDateTime.now());

        return utilisateurDAO.saveAgent(agent);
    }

    public Optional<AgentAdministratif> trouverParId(int id) {
        return utilisateurDAO.findAgentById(id);
    }

    public Optional<AgentAdministratif> trouverParMatricule(String matricule) {
        return utilisateurDAO.findAgentByMatricule(matricule);
    }

    public List<AgentAdministratif> listerTous() {
        return utilisateurDAO.findAllAgents();
    }

    public AgentAdministratif modifierAgent(AgentAdministratif agent) {
        if (agent == null || agent.getId() <= 0) {
            log.error("Tentative de modification d'un agent invalide");
            return null;
        }
        return utilisateurDAO.updateAgent(agent);
    }

    public boolean supprimerAgent(int id) {
        try {
            utilisateurDAO.deleteAgent(id);
            return true;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'agent {}: {}", id, e.getMessage());
            return false;
        }
    }

    public List<AgentAdministratif> rechercherParService(String service) {
        return listerTous().stream()
                .filter(a -> a.getService().toLowerCase().contains(service.toLowerCase()))
                .toList();
    }

    private String genererMatricule() {
        return "AGT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}