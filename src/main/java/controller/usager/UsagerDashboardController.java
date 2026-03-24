package controller.usager;

import javafx.scene.control.PasswordField;
import model.Usager;
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
public class UsagerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label mesDemandesLabel;
    @FXML private Label mesActesLabel;
    @FXML private Label demandesEnCoursLabel;

    @FXML private PasswordField agentMotDePasseField;

    @FXML private Button nouvelleDemandeButton;
    @FXML private Button mesDemandesButton;
    @FXML private Button mesActesButton;
    @FXML private Button deconnexionButton;

    private Usager usagerConnecte;
    private final DemandeService demandeService;
    private final ActeService acteService;
    private Thread clockThread;
    private boolean running = true;

    public UsagerDashboardController() {
        this.demandeService = new DemandeService();
        this.acteService = new ActeService();
    }

    public void setUsager(Usager usager) {
        this.usagerConnecte = usager;
        SessionManager.getInstance().login(usager);
        welcomeLabel.setText("Bienvenue, " + usager.getPrenom() + " " + usager.getNom());
        chargerStatistiques();
        demarrerHorloge();
    }

    @FXML
    private void initialize() {
        // Initialisation
    }

    private void chargerStatistiques() {
        try {
            long totalDemandes = demandeService.listerParUsager(usagerConnecte.getId()).size();
            long totalActes = acteService.listerParUsager(usagerConnecte).size();
            long enCours = demandeService.listerParUsager(usagerConnecte.getId()).stream()
                    .filter(d -> d.getStatut() == model.enums.StatutDemande.EN_ATTENTE ||
                            d.getStatut() == model.enums.StatutDemande.EN_COURS_TRAITEMENT)
                    .count();

            mesDemandesLabel.setText(String.valueOf(totalDemandes));
            mesActesLabel.setText(String.valueOf(totalActes));
            demandesEnCoursLabel.setText(String.valueOf(enCours));

        } catch (Exception e) {
            log.error("Erreur chargement stats: {}", e.getMessage());
        }
    }

    private void demarrerHorloge() {
        if (clockThread != null && clockThread.isAlive()) {
            running = false;
            clockThread.interrupt();
        }
        running = true;
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
    private void handleNouvelleDemande() {
        chargerVue("/fxml/usager/nouvelle_demande.fxml", "Nouvelle demande");
    }

    @FXML
    private void handleMesDemandes() {
        chargerVue("/fxml/usager/mes_demandes.fxml", "Mes demandes");
    }

    @FXML
    private void handleMesActes() {
        chargerVue("/fxml/usager/mes_actes.fxml", "Mes actes");
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

            Stage stage = (Stage) nouvelleDemandeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titre);
            stage.centerOnScreen();

        } catch (Exception e) {
            log.error("Erreur chargement vue {}: {}", fxml, e.getMessage());
            AlertUtil.showError("Erreur lors du chargement de la page");
        }
    }

}