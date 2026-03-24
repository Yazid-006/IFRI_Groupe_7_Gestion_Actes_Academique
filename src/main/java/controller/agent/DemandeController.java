package controller.agent;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import model.enums.StatutDemande;
import model.enums.TypeActe;
import service.DemandeService;
import service.UsagerService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import utils.AlertUtil;
import utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DemandeController {

    @FXML private TabPane demandeTabPane;

    // Onglet Liste des demandes
    @FXML private TableView<Demande> demandeTable;
    @FXML private TableColumn<Demande, String> numeroCol;
    @FXML private TableColumn<Demande, String> typeCol;
    @FXML private TableColumn<Demande, String> usagerCol;
    @FXML private TableColumn<Demande, LocalDateTime> dateCol;
    @FXML private TableColumn<Demande, StatutDemande> statutCol;
    @FXML private ComboBox<StatutDemande> filtreStatutCombo;

    // Onglet Détail demande
    @FXML private Label numeroDemandeLabel;
    @FXML private Label dateDemandeLabel;
    @FXML private Label typeActeLabel;
    @FXML private Label usagerInfoLabel;
    @FXML private TextArea motifTextArea;
    @FXML private Label statutLabel;
    @FXML private TextArea traitementTextArea;

    @FXML private Button validerButton;
    @FXML private Button rejeterButton;
    @FXML private Button voirJustificatifsButton;

    // Onglet Nouvelle demande
    @FXML private TextField rechercheUsagerField;
    @FXML private TableView<Usager> usagerSearchTable;
    @FXML private TableColumn<Usager, String> usagerNumeroCol;
    @FXML private TableColumn<Usager, String> usagerNomCompletCol;
    @FXML private TableColumn<Usager, String> usagerEmailCol;
    @FXML private ComboBox<TypeActe> nouveauTypeCombo;
    @FXML private TextArea nouveauMotifArea;
    @FXML private Button enregistrerDemandeButton;

    private final DemandeService demandeService;
    private final UsagerService usagerService;
    private AgentAdministratif agentConnecte;
    private final ObservableList<Demande> demandeList = FXCollections.observableArrayList();
    private final ObservableList<Usager> usagerSearchList = FXCollections.observableArrayList();
    private Demande demandeSelectionnee;

    public DemandeController() {
        this.demandeService = new DemandeService();
        this.usagerService = new UsagerService();
    }

    public void setAgent(AgentAdministratif agent) {
        this.agentConnecte = agent;
    }

    @FXML
    private void initialize() {
        setupDemandeTable();
        setupUsagerSearchTable();
        loadDemandes();

        filtreStatutCombo.setItems(FXCollections.observableArrayList(StatutDemande.values()));
        filtreStatutCombo.getSelectionModel().select(StatutDemande.EN_ATTENTE);

        nouveauTypeCombo.setItems(FXCollections.observableArrayList(TypeActe.values()));

        demandeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usagerSearchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        fixTableHeaders(demandeTable);
        fixTableHeaders(usagerSearchTable);

    }

    private void setupDemandeTable() {
        numeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroDemande"));
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTypeActeDemande().toString()));
        usagerCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDemandeur().getPrenom() + " " +
                                cellData.getValue().getDemandeur().getNom()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateDemande"));
        dateCol.setCellFactory(column -> new TableCell<Demande, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        statutCol.setCellFactory(column -> new TableCell<Demande, StatutDemande>() {
            @Override
            protected void updateItem(StatutDemande item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    switch (item) {
                        case EN_ATTENTE:
                            setStyle("-fx-background-color: #FFE082;");
                            break;
                        case VALIDEE:
                            setStyle("-fx-background-color: #C8E6C9;");
                            break;
                        case REJETEE:
                            setStyle("-fx-background-color: #FFCDD2;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        demandeTable.setItems(demandeList);
        demandeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showDemandeDetails(newVal));
    }

    private void setupUsagerSearchTable() {
        usagerNumeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroUsager"));
        usagerNomCompletCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getPrenom() + " " + cellData.getValue().getNom()));
        usagerEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        usagerSearchTable.setItems(usagerSearchList);
        usagerSearchTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> selectUsagerForNewDemande(newVal));
    }

    private void loadDemandes() {
        StatutDemande filtre = filtreStatutCombo.getValue();
        List<Demande> demandes;

        if (filtre != null) {
            demandes = demandeService.listerParStatut(filtre);
        } else {
            demandes = demandeService.listerToutes();
        }

        demandeList.setAll(demandes);
    }

    private void showDemandeDetails(Demande demande) {
        if (demande == null) {
            clearDetails();
            return;
        }

        this.demandeSelectionnee = demande;

        numeroDemandeLabel.setText(demande.getNumeroDemande());
        dateDemandeLabel.setText(
                demande.getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        typeActeLabel.setText(demande.getTypeActeDemande().toString());
        usagerInfoLabel.setText(
                demande.getDemandeur().getPrenom() + " " + demande.getDemandeur().getNom() + "\n" +
                        "Email: " + demande.getDemandeur().getEmail() + "\n" +
                        "N°: " + demande.getDemandeur().getNumeroUsager());
        motifTextArea.setText(demande.getMotif());
        statutLabel.setText(demande.getStatut().toString());

        if (demande.getDateTraitement() != null) {
            traitementTextArea.setText(
                    "Traitée le: " + demande.getDateTraitement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                            "Par: " + (demande.getAgentTraiteur() != null ?
                            demande.getAgentTraiteur().getPrenom() + " " + demande.getAgentTraiteur().getNom() : "Inconnu"));
        } else {
            traitementTextArea.clear();
        }

        // Activer/désactiver les boutons selon le statut
        boolean modifiable = demande.getStatut() == StatutDemande.EN_ATTENTE ||
                demande.getStatut() == StatutDemande.EN_COURS_TRAITEMENT;
        validerButton.setDisable(!modifiable);
        rejeterButton.setDisable(!modifiable);
    }

    private void clearDetails() {
        numeroDemandeLabel.setText("");
        dateDemandeLabel.setText("");
        typeActeLabel.setText("");
        usagerInfoLabel.setText("");
        motifTextArea.clear();
        statutLabel.setText("");
        traitementTextArea.clear();
        validerButton.setDisable(true);
        rejeterButton.setDisable(true);
    }

    @FXML
    private void handleFiltrerDemandes() {
        loadDemandes();
    }

    @FXML
    private void handleValiderDemande() {
        if (demandeSelectionnee == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Valider la demande");
        confirm.setHeaderText("Validation de la demande " + demandeSelectionnee.getNumeroDemande());
        confirm.setContentText("Êtes-vous sûr de vouloir valider cette demande ? Un acte sera généré automatiquement.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Demande validee = demandeService.validerDemande(
                    demandeSelectionnee.getId(), agentConnecte);

            if (validee != null) {
                showSuccess("Demande validée avec succès");
                loadDemandes();
                showDemandeDetails(validee);
            } else {
                showError("Erreur lors de la validation");
            }
        }
    }

    @FXML
    private void handleRejeterDemande() {
        if (demandeSelectionnee == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejeter la demande");
        dialog.setHeaderText("Rejet de la demande " + demandeSelectionnee.getNumeroDemande());
        dialog.setContentText("Motif du rejet:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            Demande rejetee = demandeService.rejeterDemande(
                    demandeSelectionnee.getId(), agentConnecte, result.get());

            if (rejetee != null) {
                showSuccess("Demande rejetée");
                loadDemandes();
                showDemandeDetails(rejetee);
            } else {
                showError("Erreur lors du rejet");
            }
        }
    }

    @FXML
    private void handleRechercherUsager() {
        String critere = rechercheUsagerField.getText().trim();
        if (critere.isEmpty()) {
            usagerSearchList.clear();
            return;
        }

        // Recherche simple (à améliorer)
        List<Usager> usagers = usagerService.listerTous().stream()
                .filter(u -> u.getNom().toLowerCase().contains(critere.toLowerCase()) ||
                        u.getPrenom().toLowerCase().contains(critere.toLowerCase()) ||
                        u.getEmail().toLowerCase().contains(critere.toLowerCase()) ||
                        u.getNumeroUsager().contains(critere))
                .limit(10)
                .toList();

        usagerSearchList.setAll(usagers);
    }

    private void selectUsagerForNewDemande(Usager usager) {
        if (usager != null) {
            enregistrerDemandeButton.setDisable(false);
        }
    }

    @FXML
    private void handleEnregistrerDemande() {
        Usager selected = usagerSearchTable.getSelectionModel().getSelectedItem();
        TypeActe type = nouveauTypeCombo.getValue();
        String motif = nouveauMotifArea.getText();

        if (selected == null) {
            showError("Veuillez sélectionner un usager");
            return;
        }

        if (type == null) {
            showError("Veuillez sélectionner un type d'acte");
            return;
        }

        if (motif.trim().isEmpty()) {
            showError("Veuillez saisir un motif");
            return;
        }

        Demande nouvelle = demandeService.creerDemande(selected, type, motif);

        if (nouvelle != null) {
            showSuccess("Demande créée avec succès");
            nouveauMotifArea.clear();
            rechercheUsagerField.clear();
            usagerSearchList.clear();
            nouveauTypeCombo.getSelectionModel().clearSelection();
            enregistrerDemandeButton.setDisable(true);

            // Recharger la liste des demandes
            loadDemandes();
            demandeTabPane.getSelectionModel().select(0); // Retour à la liste
        } else {
            showError("Erreur lors de la création de la demande");
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/agent/agent_dashboard.fxml"));
            Parent root = loader.load();

            AgentDashboardController controller = loader.getController();
            controller.setAgent((AgentAdministratif) SessionManager.getInstance().getUtilisateurConnecte());

            Stage stage = (Stage) demandeTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.centerOnScreen();
        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour");
        }
    }

    @FXML
    private void handleVoirJustificatifs() {
        Demande selected = demandeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showInfo("Sélectionnez une demande d'abord");
            return;
        }

        Demande demande = demandeService.trouverParId(selected.getId()).orElse(null);
        if (demande == null) {
            AlertUtil.showError("Erreur lors du chargement de la demande");
            return;
        }

        List<DocumentJustificatif> docs = demande.getJustificatifs();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Justificatifs");
        dialog.setHeaderText("Demande : " + demande.getNumeroDemande());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(16));

        if (docs == null || docs.isEmpty()) {
            Label vide = new Label("Aucun justificatif pour cette demande.");
            vide.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 13px;");
            content.getChildren().add(vide);
        } else {
            for (DocumentJustificatif doc : docs) {
                HBox ligne = new HBox(12);
                ligne.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                ligne.setStyle("-fx-border-color: #edf2f7; -fx-border-width: 0 0 1 0; -fx-padding: 8 0 8 0;");

                VBox infos = new VBox(3);
                Label nom = new Label("📎 " + doc.getNomFichier());
                nom.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
                Label meta = new Label(doc.getTypeFichier() + " · " +
                        doc.getDateUpload().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                meta.setStyle("-fx-font-size: 11px; -fx-text-fill: #a0aec0;");
                infos.getChildren().addAll(nom, meta);

                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                Button ouvrir = new Button("Ouvrir");
                ouvrir.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14 6 14;");
                ouvrir.setOnAction(e -> {
                    try {
                        java.awt.Desktop.getDesktop().open(new java.io.File(doc.getChemin()));
                    } catch (Exception ex) {
                        AlertUtil.showError("Impossible d'ouvrir : " + doc.getNomFichier());
                    }
                });

                ligne.getChildren().addAll(infos, spacer, ouvrir);
                content.getChildren().add(ligne);
            }
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(520);
        dialog.showAndWait();
    }

    private void fixTableHeaders(TableView<?> table) {
        table.widthProperty().addListener((obs, oldVal, newVal) -> {
            table.lookupAll(".column-header .label").forEach(node ->
                    node.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"));
        });
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}