package controller.agent;

import model.AgentAdministratif;
import service.DemandeService;
import service.ActeService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import lombok.extern.slf4j.Slf4j;
import utils.AlertUtil;
import utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class AgentDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label demandesEnAttenteLabel;
    @FXML private Label demandesTraiteesLabel;
    @FXML private Label actesEmisLabel;

    @FXML private Button demandesButton;
    @FXML private Button actesButton;
    @FXML private Button rechercheButton;
    @FXML private Button deconnexionButton;

    private AgentAdministratif agentConnecte;
    private final DemandeService demandeService;
    private final ActeService acteService;
    private Thread clockThread;
    private boolean running = true;

    public AgentDashboardController() {
        this.demandeService = new DemandeService();
        this.acteService = new ActeService();
    }

    public void setAgent(AgentAdministratif agent) {
        this.agentConnecte = agent;
        SessionManager.getInstance().login(agent);
        welcomeLabel.setText("Bienvenue, " + agent.getPrenom() + " " + agent.getNom());
        chargerStatistiques();
        demarrerHorloge();
    }

    @FXML
    private void initialize() {
        // Initialisation
    }

    private void chargerStatistiques() {
        try {
            long enAttente = demandeService.compterParStatut(model.enums.StatutDemande.EN_ATTENTE);
            long traitees = demandeService.listerToutes().stream()
                    .filter(d -> d.getAgentTraiteur() != null &&
                            d.getAgentTraiteur().getId() == agentConnecte.getId())
                    .count();
            long actesEmis = acteService.listerTous().stream()
                    .filter(a -> a.getAgentEmetteur() != null &&
                            a.getAgentEmetteur().getId() == agentConnecte.getId())
                    .count();

            demandesEnAttenteLabel.setText(String.valueOf(enAttente));
            demandesTraiteesLabel.setText(String.valueOf(traitees));
            actesEmisLabel.setText(String.valueOf(actesEmis));

        } catch (Exception e) {
            log.error("Erreur chargement stats: {}", e.getMessage());
        }
    }

    private void demarrerHorloge() {
        clockThread = new Thread(() -> {
            while (running) {
                try {
                    String time = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy HH:mm:ss"));
                    javafx.application.Platform.runLater(() ->
                            dateTimeLabel.setText(time));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        clockThread.setDaemon(true);
        clockThread.start();
    }

    @FXML
    private void handleDemandes() {
        chargerVue("/fxml/agent/gestion_demandes.fxml", "Gestion des demandes");
    }

    @FXML
    private void handleActes() {
        chargerVue("/fxml/agent/gestion_actes.fxml", "Gestion des actes");
    }

    @FXML
    private void handleRecherche() {
        chargerVue("/fxml/agent/recherche.fxml", "Recherche");
    }

    @FXML
    private void handleDeconnexion() {
        boolean confirm = AlertUtil.showConfirmation("Déconnexion",
                "Voulez-vous vraiment vous déconnecter ?");

        if (confirm) {
            try {
                running = false;
                if (clockThread != null) {
                    clockThread.interrupt();
                }

                SessionManager.getInstance().logout();

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) deconnexionButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion - Gestion des actes");
                stage.centerOnScreen();

            } catch (Exception e) {
                log.error("Erreur déconnexion: {}", e.getMessage());
                AlertUtil.showError("Erreur lors de la déconnexion");
            }
        }
    }

    private void chargerVue(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DemandeController) {
                ((DemandeController) controller).setAgent(agentConnecte);
            } else if (controller instanceof ActeController) {
                ((ActeController) controller).setAgent(agentConnecte);
            } else if (controller instanceof RechercheController) {
                ((RechercheController) controller).setAgent(agentConnecte);
            }

            Stage stage = (Stage) demandesButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titre);
            stage.centerOnScreen();

        } catch (Exception e) {
            log.error("Erreur chargement vue {}: {}", fxml, e.getMessage());
            AlertUtil.showError("Erreur lors du chargement de la page");
        }
    }

}