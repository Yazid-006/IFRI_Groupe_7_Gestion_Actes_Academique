package service;

import dao.interfaces.IDemandeDAO;
import dao.interfaces.IActeDAO;
import dao.impl.DemandeDAO;
import dao.impl.ActeDAO;
import lombok.NonNull;
import model.Demande;
import model.ActeAdministratif;
import model.enums.StatutDemande;
import model.enums.TypeActe;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RapportService {

    private final IDemandeDAO demandeDAO;
    private final IActeDAO acteDAO;

    public RapportService() {
        this.demandeDAO = new DemandeDAO();
        this.acteDAO = new ActeDAO();
    }

    public List<Demande> getDernieresDemandes(int limit) {
        return demandeDAO.findAll().stream()
                .sorted((a, b) -> b.getDateDemande().compareTo(a.getDateDemande()))
                .limit(limit)
                .toList();
    }

    public Map<String, Object> statistiquesGlobales() {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques des demandes

        stats.put("totalDemandes", demandeDAO.findAll().size());
        stats.put("demandesEnAttente", demandeDAO.countByStatut(StatutDemande.EN_ATTENTE));
        stats.put("demandesEnCours", demandeDAO.countByStatut(StatutDemande.EN_COURS_TRAITEMENT));
        stats.put("demandesValidees", demandeDAO.countByStatut(StatutDemande.VALIDEE));
        stats.put("demandesRejetees", demandeDAO.countByStatut(StatutDemande.REJETEE));

        // Statistiques des actes

        stats.put("totalActes", acteDAO.findAll().size());

        Map<TypeActe, Long> actesParType = new HashMap<>();
        for (TypeActe type : TypeActe.values()) {
            actesParType.put(type, acteDAO.countByType(type));
        }
        stats.put("actesParType", actesParType);

        // Actes du mois
        LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
        LocalDate finMois = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);
        stats.put("actesMois", acteDAO.findByPeriode(debutMois, finMois).size());

        return stats;
    }

    public Map<String, Object> rapportPeriodique(LocalDate debut, LocalDate fin) {
        Map<String, Object> rapport = new HashMap<>();

        rapport.put("periodeDebut", debut);
        rapport.put("periodeFin", fin);

        // Demandes sur la période

        List<Demande> demandesPeriode = demandeDAO.findAll().stream()
                .filter(d -> d.getDateDemande().toLocalDate().isAfter(debut.minusDays(1)) &&
                        d.getDateDemande().toLocalDate().isBefore(fin.plusDays(1)))
                .toList();

        rapport.put("demandesPeriode", demandesPeriode.size());
        rapport.put("demandesValideesPeriode", demandesPeriode.stream()
                .filter(d -> d.getStatut() == StatutDemande.VALIDEE).count());
        rapport.put("demandesRejeteesPeriode", demandesPeriode.stream()
                .filter(d -> d.getStatut() == StatutDemande.REJETEE).count());

        // Actes sur la période

        List<ActeAdministratif> actesPeriode = acteDAO.findByPeriode(debut, fin);
        rapport.put("actesPeriode", actesPeriode.size());

        Map<TypeActe, Long> actesParTypePeriode = actesPeriode.stream()
                .collect(Collectors.groupingBy(ActeAdministratif::getTypeActe, Collectors.counting()));
        rapport.put("actesParTypePeriode", actesParTypePeriode);

        return rapport;
    }

    public Map<String, Object> rapportParAgent(int agentId) {
        Map<String, Object> rapport = new HashMap<>();

        List<Demande> demandesTraitees = demandeDAO.findByAgentTraiteur(agentId);

        rapport.put("totalDemandesTraitees", demandesTraitees.size());
        rapport.put("demandesValidees", demandesTraitees.stream()
                .filter(d -> d.getStatut() == StatutDemande.VALIDEE).count());
        rapport.put("demandesRejetees", demandesTraitees.stream()
                .filter(d -> d.getStatut() == StatutDemande.REJETEE).count());

        return rapport;
    }

    public String exporterRapportTexte(@NonNull Map<String, Object> rapport, String titre) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        sb.append("=====================================\n");
        sb.append(titre).append("\n");
        sb.append("Date: ").append(LocalDateTime.now().format(formatter)).append("\n");
        sb.append("=====================================\n\n");

        for (Map.Entry<String, Object> entry : rapport.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        sb.append("\n=====================================\n");

        return sb.toString();
    }

}