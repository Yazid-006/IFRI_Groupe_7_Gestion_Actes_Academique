package controller.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import model.enums.NiveauAcces;
import service.AgentService;
import service.UsagerService;
import service.AuthentificationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import utils.AlertUtil;
import utils.SessionManager;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class UtilisateurController {

    @FXML private TabPane utilisateurTabPane;

    // Onglet Usagers
    @FXML private TableView<Usager> usagerTable;
    @FXML private TableColumn<Usager, Integer> usagerIdCol;
    @FXML private TableColumn<Usager, String> usagerNumeroCol;
    @FXML private TableColumn<Usager, String> usagerNomCol;
    @FXML private TableColumn<Usager, String> usagerPrenomCol;
    @FXML private TableColumn<Usager, String> usagerEmailCol;

    @FXML private TextField usagerNomField;
    @FXML private TextField usagerPrenomField;
    @FXML private TextField usagerEmailField;
    @FXML private TextField usagerTelField;
    @FXML private TextField usagerAdresseField;
    @FXML private DatePicker usagerDateNaissancePicker;
    @FXML private TextField usagerPieceIdentiteField;

    // Onglet Agents
    @FXML private TableView<AgentAdministratif> agentTable;
    @FXML private TableColumn<AgentAdministratif, Integer> agentIdCol;
    @FXML private TableColumn<AgentAdministratif, String> agentMatriculeCol;
    @FXML private TableColumn<AgentAdministratif, String> agentNomCol;
    @FXML private TableColumn<AgentAdministratif, String> agentPrenomCol;
    @FXML private TableColumn<AgentAdministratif, String> agentServiceCol;
    @FXML private TableColumn<AgentAdministratif, String> agentFonctionCol;

    @FXML private TextField agentNomField;
    @FXML private TextField agentPrenomField;
    @FXML private TextField agentEmailField;
    @FXML private TextField agentTelField;
    @FXML private TextField agentMatriculeField;
    @FXML private TextField agentServiceField;
    @FXML private TextField agentFonctionField;
    @FXML private PasswordField agentMotDePasseField;
    @FXML private ComboBox<String> agentNiveauAccesCombo;

    private final UsagerService usagerService;
    private final AgentService agentService;
    private AuthentificationService authService;
    private final ObservableList<Usager> usagerList = FXCollections.observableArrayList();
    private final ObservableList<AgentAdministratif> agentList = FXCollections.observableArrayList();

    public UtilisateurController() {
        this.usagerService = new UsagerService();
        this.agentService = new AgentService();
        this.authService = new AuthentificationService();
    }

    @FXML
    private void initialize() {
        setupUsagerTable();
        setupAgentTable();
        loadUsagers();
        loadAgents();

        agentNiveauAccesCombo.setItems(FXCollections.observableArrayList(
                "LECTURE_SEULE", "GESTION_DEMANDE", "GESTION_ACTE", "ADMIN"
        ));

        fixTableHeaders(usagerTable);
        fixTableHeaders(agentTable);

    }

    private void setupUsagerTable() {
        usagerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usagerNumeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroUsager"));
        usagerNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        usagerPrenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        usagerEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        usagerTable.setItems(usagerList);
        usagerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showUsagerDetails(newVal));

        usagerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void setupAgentTable() {
        agentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        agentMatriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        agentNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        agentPrenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        agentServiceCol.setCellValueFactory(new PropertyValueFactory<>("service"));
        agentFonctionCol.setCellValueFactory(new PropertyValueFactory<>("fonction"));

        agentTable.setItems(agentList);
        agentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showAgentDetails(newVal));

        agentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void loadUsagers() {
        List<Usager> usagers = usagerService.listerTous();
        usagerList.setAll(usagers);
    }

    private void loadAgents() {
        List<AgentAdministratif> agents = agentService.listerTous();
        agentList.setAll(agents);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/admin_dashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setAdmin((Administrateur) SessionManager.getInstance().getUtilisateurConnecte());

            Stage stage = (Stage) usagerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.centerOnScreen();
        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour");
        }
    }

    private void showUsagerDetails(Usager usager) {
        if (usager != null) {
            usagerNomField.setText(usager.getNom());
            usagerPrenomField.setText(usager.getPrenom());
            usagerEmailField.setText(usager.getEmail());
            usagerTelField.setText(usager.getTelephone());
            usagerAdresseField.setText(usager.getAdresse());
            usagerDateNaissancePicker.setValue(usager.getDateNaissance());
            usagerPieceIdentiteField.setText(usager.getPieceIdentite());
        } else {
            clearUsagerFields();
        }
    }

    private void showAgentDetails(AgentAdministratif agent) {
        if (agent != null) {
            agentNomField.setText(agent.getNom());
            agentPrenomField.setText(agent.getPrenom());
            agentEmailField.setText(agent.getEmail());
            agentTelField.setText(agent.getTelephone());
            agentMatriculeField.setText(agent.getMatricule());
            agentServiceField.setText(agent.getService());
            agentFonctionField.setText(agent.getFonction());
            if (agent.getNiveauAcces() != null) {
                agentNiveauAccesCombo.setValue(agent.getNiveauAcces().name());
            }
        } else {
            clearAgentFields();
        }
    }

    @FXML
    private void handleNewUsager() {
        clearUsagerFields();
        usagerTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSaveUsager() {
        Usager usager;

        if (usagerTable.getSelectionModel().getSelectedItem() == null) {
            usager = new Usager();
        } else {
            usager = usagerTable.getSelectionModel().getSelectedItem();
        }

        usager.setNom(usagerNomField.getText());
        usager.setPrenom(usagerPrenomField.getText());
        usager.setEmail(usagerEmailField.getText());
        usager.setTelephone(usagerTelField.getText());
        usager.setAdresse(usagerAdresseField.getText());
        usager.setDateNaissance(usagerDateNaissancePicker.getValue());
        usager.setPieceIdentite(usagerPieceIdentiteField.getText());

        Usager saved;
        if (usager.getId() == 0) {
            saved = usagerService.creerUsager(usager);
        } else {
            saved = usagerService.modifierUsager(usager);
        }

        if (saved != null) {
            loadUsagers();
            showSuccess("Usager enregistré avec succès");
        } else {
            showError("Erreur lors de l'enregistrement");
        }
    }

    @FXML
    private void handleDeleteUsager() {
        Usager selected = usagerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean confirmed = showConfirmation("Supprimer", "Voulez-vous vraiment supprimer cet usager ?");
            if (confirmed) {
                boolean deleted = usagerService.supprimerUsager(selected.getId());
                if (deleted) {
                    loadUsagers();
                    clearUsagerFields();
                    showSuccess("Usager supprimé avec succès");
                } else {
                    showError("Erreur lors de la suppression");
                }
            }
        }
    }

    @FXML
    private void handleNewAgent() {
        clearAgentFields();
        agentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSaveAgent() {
        AgentAdministratif agent;

        if (agentTable.getSelectionModel().getSelectedItem() == null) {
            agent = new AgentAdministratif();
        } else {
            agent = agentTable.getSelectionModel().getSelectedItem();
        }

        agent.setNom(agentNomField.getText());
        agent.setPrenom(agentPrenomField.getText());
        agent.setEmail(agentEmailField.getText());
        agent.setTelephone(agentTelField.getText());
        agent.setMatricule(agentMatriculeField.getText());
        agent.setService(agentServiceField.getText());
        agent.setFonction(agentFonctionField.getText());

        // Définir le mot de passe
        String motDePasse = agentMotDePasseField.getText();
        if (motDePasse != null && !motDePasse.trim().isEmpty()) {
            agent.setMotDePasse(motDePasse);
        } else {
            agent.setMotDePasse("agent123");
            AlertUtil.showInfo("Mot de passe par défaut défini: agent123");
        }

        String niveau = agentNiveauAccesCombo.getValue();
        if (niveau != null) {
            agent.setNiveauAcces(NiveauAcces.valueOf(niveau));
        }

        agent.setDateCreation(LocalDateTime.now());

        AgentAdministratif saved;
        if (agent.getId() == 0) {
            saved = agentService.creerAgent(agent);
        } else {
            saved = agentService.modifierAgent(agent);
        }

        if (saved != null) {
            loadAgents();
            AlertUtil.showSuccess("Agent enregistré avec succès");
        } else {
            AlertUtil.showError("Erreur lors de l'enregistrement");
        }
    }

    @FXML
    private void handleDeleteAgent() {
        AgentAdministratif selected = agentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean confirmed = AlertUtil.showConfirmation("Supprimer",
                    "Voulez-vous vraiment supprimer cet agent ?");
            if (confirmed) {
                boolean deleted = agentService.supprimerAgent(selected.getId());
                if (deleted) {
                    loadAgents();
                    clearAgentFields();
                    AlertUtil.showSuccess("Agent supprimé avec succès");
                } else {
                    AlertUtil.showError("Erreur lors de la suppression");
                }
            }
        }
    }

    private void clearUsagerFields() {
        usagerNomField.clear();
        usagerPrenomField.clear();
        usagerEmailField.clear();
        usagerTelField.clear();
        usagerAdresseField.clear();
        usagerDateNaissancePicker.setValue(null);
        usagerPieceIdentiteField.clear();
    }

    private void clearAgentFields() {
        agentNomField.clear();
        agentPrenomField.clear();
        agentEmailField.clear();
        agentTelField.clear();
        agentMatriculeField.clear();
        agentServiceField.clear();
        agentFonctionField.clear();
        agentNiveauAccesCombo.setValue(null);
    }

    private void showSuccess(String message) {
        AlertUtil.showSuccess(message);
    }

    private void showError(String message) {
        AlertUtil.showError(message);
    }

    private void showInfo(String message) {
        AlertUtil.showInfo(message);
    }

    private boolean showConfirmation(String title, String message) {
        return AlertUtil.showConfirmation(title, message);
    }

    private void fixTableHeaders(TableView<?> table) {
        table.widthProperty().addListener((obs, oldVal, newVal) -> {
            table.lookupAll(".column-header .label").forEach(node ->
                    node.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"));
        });
    }

    public void setAdmin(Administrateur admin) {
        log.info("UtilisateurController chargé par admin: {}", admin.getLogin());
    }

}