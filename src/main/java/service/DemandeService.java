package service;

import dao.interfaces.IDemandeDAO;
import dao.interfaces.IActeDAO;
import dao.impl.DemandeDAO;
import dao.impl.ActeDAO;
import dao.utils.HibernateUtil;
import lombok.NonNull;
import model.Demande;
import model.ActeAdministratif;
import model.Usager;
import model.AgentAdministratif;
import model.enums.StatutDemande;
import model.enums.TypeActe;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DemandeService {

    private final IDemandeDAO demandeDAO;
    private final IActeDAO acteDAO;
    private final ActeService acteService;

    public DemandeService() {
        this.demandeDAO = new DemandeDAO();
        this.acteDAO = new ActeDAO();
        this.acteService = new ActeService();
    }

    public Demande creerDemande(Usager usager, TypeActe typeActe, String motif) {
        if (usager == null) {
            log.error("Tentative de création d'une demande avec usager null");
            return null;
        }

        Demande demande = Demande.builder()
                .numeroDemande(genererNumeroDemande())
                .typeActeDemande(typeActe)
                .dateDemande(LocalDateTime.now())
                .statut(StatutDemande.EN_ATTENTE)
                .motif(motif)
                .demandeur(usager)
                .build();

        return demandeDAO.save(demande);
    }

    public Demande validerDemande(int idDemande, AgentAdministratif agent) {
        Optional<Demande> optionalDemande = demandeDAO.findById(idDemande);

        if (optionalDemande.isEmpty()) {
            log.error("Demande non trouvée avec ID: {}", idDemande);
            return null;
        }

        Demande demande = optionalDemande.get();

        if (demande.getStatut() != StatutDemande.EN_ATTENTE &&
                demande.getStatut() != StatutDemande.EN_COURS_TRAITEMENT) {
            log.error("La demande {} ne peut pas être validée (statut: {})",
                    demande.getNumeroDemande(), demande.getStatut());
            return null;
        }

        // Générer l'acte en mémoire seulement — pas de persist ici
        ActeAdministratif acte = acteService.genererActe(demande, agent);
        if (acte == null) {
            log.error("Échec de la génération de l'acte pour la demande {}",
                    demande.getNumeroDemande());
            return null;
        }

        // Une seule session, une seule transaction pour les deux opérations
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // 1. Persister l'acte
            session.persist(acte);

            // 2. Mettre à jour la demande dans la même session
            demande.setStatut(StatutDemande.VALIDEE);
            demande.setDateTraitement(LocalDateTime.now());
            demande.setAgentTraiteur(agent);
            demande.setActeGenere(acte);
            session.merge(demande);

            transaction.commit();
            log.info("Demande {} validée et acte {} généré avec succès",
                    demande.getNumeroDemande(), acte.getNumeroActe());
            return demande;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Erreur lors de la validation de la demande: {}", e.getMessage());
            return null;
        }
    }

    public Demande rejeterDemande(int idDemande, AgentAdministratif agent, String raison) {
        Optional<Demande> optionalDemande = demandeDAO.findById(idDemande);

        if (optionalDemande.isEmpty()) {
            log.error("Demande non trouvée avec ID: {}", idDemande);
            return null;
        }

        Demande demande = optionalDemande.get();

        if (demande.getStatut() != StatutDemande.EN_ATTENTE &&
                demande.getStatut() != StatutDemande.EN_COURS_TRAITEMENT) {
            log.error("La demande {} ne peut pas être rejetée (statut: {})",
                    demande.getNumeroDemande(), demande.getStatut());
            return null;
        }

        demande.setStatut(StatutDemande.REJETEE);
        demande.setDateTraitement(LocalDateTime.now());
        demande.setAgentTraiteur(agent);
        demande.setMotif(raison);

        return demandeDAO.update(demande);
    }

    public Optional<Demande> trouverParId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Demande demande = session.get(Demande.class, id);
            if (demande != null) {
                Hibernate.initialize(demande.getJustificatifs());
            }
            return Optional.ofNullable(demande);
        } catch (Exception e) {
            log.error("Erreur findById: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Demande> trouverParNumero(String numero) {
        return demandeDAO.findByNumero(numero);
    }

    public List<Demande> listerToutes() {
        return demandeDAO.findAll();
    }

    public List<Demande> listerParUsager(int usagerId) {
        return demandeDAO.findByUsager(usagerId);
    }

    public List<Demande> listerParStatut(StatutDemande statut) {
        return demandeDAO.findByStatut(statut);
    }

    public List<Demande> listerParTypeActe(TypeActe typeActe) {
        return demandeDAO.findByTypeActe(typeActe);
    }

    public long compterParStatut(StatutDemande statut) {
        return demandeDAO.countByStatut(statut);
    }

    private @NonNull String genererNumeroDemande() {
        return "DEM-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

}