package controller.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Administrateur;
import model.Demande;
import service.RapportService;
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
import java.util.List;

@Slf4j
public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label totalDemandesLabel;
    @FXML private Label demandesEnAttenteLabel;
    @FXML private Label totalActesLabel;

    @FXML private Button utilisateursButton;
    @FXML private Button rapportsButton;
    @FXML private Button configButton;
    @FXML private Button deconnexionButton;

    @FXML private TableView<Demande> activiteTable;
    @FXML private TableColumn<Demande, String> colNumeroDemande;
    @FXML private TableColumn<Demande, String> colTypeActe;
    @FXML private TableColumn<Demande, String> colDemandeur;
    @FXML private TableColumn<Demande, String> colStatut;
    @FXML private TableColumn<Demande, String> colDate;

    private Administrateur adminConnecte;
    private RapportService rapportService;
    private Thread clockThread;
    private boolean running = true;

    public AdminDashboardController() {
        this.rapportService = new RapportService();
    }

    public void setAdmin(Administrateur admin) {
        this.adminConnecte = admin;
        SessionManager.getInstance().login(admin);
        welcomeLabel.setText("Bienvenue, " + admin.getPrenom() + " " + admin.getNom());
        chargerStatistiques();
        demarrerHorloge();
    }

    @FXML
    private void initialize() {
        // Initialisation
    }

    private void configurerTableActivite() {
        colNumeroDemande.setCellValueFactory(new PropertyValueFactory<>("numeroDemande"));
        colTypeActe.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTypeActeDemande().toString()));
        colDemandeur.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDemandeur().getPrenom() + " " + c.getValue().getDemandeur().getNom()));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatut().toString()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

        activiteTable.setStyle("-fx-background-color: white;");

        // Forcer les headers en noir via CSS programmatique
        activiteTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            activiteTable.lookupAll(".column-header .label").forEach(node -> {
                node.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
            });
        });

    }

    private void chargerStatistiques() {
        try {
            var stats = rapportService.statistiquesGlobales();
            totalDemandesLabel.setText(stats.get("totalDemandes").toString());
            demandesEnAttenteLabel.setText(stats.get("demandesEnAttente").toString());
            totalActesLabel.setText(stats.get("totalActes").toString());

            configurerTableActivite();
            List<Demande> dernieres = rapportService.getDernieresDemandes(10);
            activiteTable.setItems(FXCollections.observableArrayList(dernieres));

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
    private void handleUtilisateurs() {
        chargerVue("/fxml/admin/gestion_utilisateurs.fxml", "Gestion des utilisateurs");
    }

    @FXML
    private void handleRapports() {
        chargerVue("/fxml/admin/statistiques.fxml", "Statistiques et rapports");
    }

    @FXML
    private void handleConfiguration() {

        chargerVue("/fxml/admin/configuration.fxml", "Configuration");

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
            if (controller instanceof UtilisateurController) {
                ((UtilisateurController) controller).setAdmin(adminConnecte);
            } else if (controller instanceof RapportController) {
                ((RapportController) controller).setAdmin(adminConnecte);
            }  else if (controller instanceof ConfigurationController) {
                ((ConfigurationController) controller).setAdmin(adminConnecte);
            }

            Stage stage = (Stage) utilisateursButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titre);
            stage.centerOnScreen();

        } catch (Exception e) {
            log.error("Erreur chargement vue {}: {}", fxml, e.getMessage());
            AlertUtil.showError("Erreur lors du chargement de la page");
        }
    }

}