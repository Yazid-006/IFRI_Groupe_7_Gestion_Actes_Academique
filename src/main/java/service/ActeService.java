package service;

import dao.interfaces.IActeDAO;
import dao.impl.ActeDAO;
import lombok.NonNull;
import model.ActeAdministratif;
import model.Demande;
import model.Usager;
import model.AgentAdministratif;
import model.enums.StatutActe;
import model.enums.TypeActe;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class ActeService {

    private final IActeDAO acteDAO;

    public ActeService() {
        this.acteDAO = new ActeDAO();
    }

    public ActeAdministratif genererActe(Demande demande, AgentAdministratif agent) {
        if (demande == null || agent == null) {
            log.error("Paramètres invalides pour la génération d'acte");
            return null;
        }

        String contenu = genererContenuActe(demande);
        LocalDate dateValidite = calculerDateValidite(demande.getTypeActeDemande());

        return ActeAdministratif.builder()
                .numeroActe(genererNumeroActe())
                .typeActe(demande.getTypeActeDemande())
                .objet("Acte administratif - " + demande.getTypeActeDemande())
                .contenu(contenu)
                .dateEmission(LocalDateTime.now())
                .dateValidite(dateValidite)
                .statut(StatutActe.VALIDE)
                .usager(demande.getDemandeur())
                .agentEmetteur(agent)
                .build();
    }

    public ActeAdministratif creerActe(ActeAdministratif acte) {
        if (acte == null) {
            log.error("Tentative de création d'un acte null");
            return null;
        }

        if (acte.getNumeroActe() == null) {
            acte.setNumeroActe(genererNumeroActe());
        }

        if (acte.getDateEmission() == null) {
            acte.setDateEmission(LocalDateTime.now());
        }

        if (acte.getStatut() == null) {
            acte.setStatut(StatutActe.BROUILLON);
        }

        return acteDAO.save(acte);
    }

    public Optional<ActeAdministratif> trouverParId(int id) {
        return acteDAO.findById(id);
    }

    public Optional<ActeAdministratif> trouverParNumero(String numero) {
        return acteDAO.findByNumero(numero);
    }

    public List<ActeAdministratif> listerTous() {
        return acteDAO.findAll();
    }

    public List<ActeAdministratif> listerParUsager(int usagerId) {
        return acteDAO.findByUsager(usagerId);
    }

    public List<ActeAdministratif> listerParUsager(Usager usager) {
        if (usager == null) return List.of();
        return acteDAO.findByUsager(usager.getId());
    }

    public List<ActeAdministratif> listerParType(TypeActe type) {
        return acteDAO.findByType(type);
    }

    public List<ActeAdministratif> listerParPeriode(LocalDate debut, LocalDate fin) {
        return acteDAO.findByPeriode(debut, fin);
    }

    public ActeAdministratif validerActe(int id) {
        Optional<ActeAdministratif> optional = acteDAO.findById(id);

        if (optional.isEmpty()) {
            log.error("Acte non trouvé avec ID: {}", id);
            return null;
        }

        ActeAdministratif acte = optional.get();
        acte.setStatut(StatutActe.VALIDE);

        return acteDAO.update(acte);
    }

    public ActeAdministratif annulerActe(int id, String raison) {
        Optional<ActeAdministratif> optional = acteDAO.findById(id);

        if (optional.isEmpty()) {
            log.error("Acte non trouvé avec ID: {}", id);
            return null;
        }

        ActeAdministratif acte = optional.get();
        acte.setStatut(StatutActe.ANNULE);
        // On pourrait ajouter un champ raisonAnnulation si nécessaire

        return acteDAO.update(acte);
    }

    public boolean archiverActe(int id) {
        Optional<ActeAdministratif> optional = acteDAO.findById(id);

        if (optional.isEmpty()) {
            log.error("Acte non trouvé avec ID: {}", id);
            return false;
        }

        ActeAdministratif acte = optional.get();

        // Vérifier si l'acte est expiré ou si on force l'archivage
        if (acte.getDateValidite() != null &&
                acte.getDateValidite().isBefore(LocalDate.now())) {
            acte.setStatut(StatutActe.EXPIRE);
        }

        acteDAO.update(acte);
        log.info("Acte {} archivé", acte.getNumeroActe());
        return true;
    }

    public long compterParType(TypeActe type) {
        return acteDAO.countByType(type);
    }

    private @NonNull String genererNumeroActe() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String anneeMois = LocalDate.now().format(formatter);
        return "ACT-" + anneeMois + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private @NonNull String genererContenuActe(@NonNull Demande demande) {
        StringBuilder contenu = new StringBuilder();
        contenu.append("ACTE ADMINISTRATIF\n\n");
        contenu.append("Numéro de demande: ").append(demande.getNumeroDemande()).append("\n");
        contenu.append("Type d'acte: ").append(demande.getTypeActeDemande()).append("\n");
        contenu.append("Demandeur: ").append(demande.getDemandeur().getPrenom())
                .append(" ").append(demande.getDemandeur().getNom()).append("\n");
        contenu.append("Date de demande: ").append(demande.getDateDemande()).append("\n");
        contenu.append("Motif: ").append(demande.getMotif()).append("\n\n");
        contenu.append("Par la présente, il est délivré le présent acte à l'intéressé pour faire valoir ce que de droit.\n");

        return contenu.toString();
    }

    private LocalDate calculerDateValidite(TypeActe type) {
        // Durée de validité selon le type d'acte
        return switch (type) {
            case CERTIFICAT_SCOLARITE -> LocalDate.now().plusMonths(6);
            case ATTESTATION_TRAVAIL -> LocalDate.now().plusMonths(3);
            case AUTORISATION -> LocalDate.now().plusMonths(12);
            case COPIE_ACTE -> null; // Pas de date d'expiration
            default -> LocalDate.now().plusMonths(6);
        };
    }

}