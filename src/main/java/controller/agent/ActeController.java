package controller.agent;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ActeAdministratif;
import model.AgentAdministratif;
import model.Usager;
import model.enums.TypeActe;
import model.enums.StatutActe;
import service.ActeService;
import service.UsagerService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import utils.SessionManager;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ActeController {

    @FXML private TableView<ActeAdministratif> acteTable;
    @FXML private TableColumn<ActeAdministratif, String> numeroCol;
    @FXML private TableColumn<ActeAdministratif, String> typeCol;
    @FXML private TableColumn<ActeAdministratif, String> usagerCol;
    @FXML private TableColumn<ActeAdministratif, LocalDateTime> dateCol;
    @FXML private TableColumn<ActeAdministratif, StatutActe> statutCol;

    @FXML private TextField rechercheField;
    @FXML private ComboBox<TypeActe> typeFiltreCombo;
    @FXML private ComboBox<StatutActe> statutFiltreCombo;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;

    @FXML private Label numeroActeLabel;
    @FXML private Label typeActeLabel;
    @FXML private Label dateEmissionLabel;
    @FXML private Label dateValiditeLabel;
    @FXML private Label usagerInfoLabel;
    @FXML private Label agentEmetteurLabel;
    @FXML private Label statutActeLabel;
    @FXML private TextArea contenuActeArea;

    @FXML private Button visualiserButton;
    @FXML private Button imprimerButton;
    @FXML private Button exporterButton;
    @FXML private Button annulerButton;

    private final ActeService acteService;
    private UsagerService usagerService;
    private AgentAdministratif agentConnecte;
    private final ObservableList<ActeAdministratif> acteList = FXCollections.observableArrayList();
    private ActeAdministratif acteSelectionne;

    public ActeController() {
        this.acteService = new ActeService();
        this.usagerService = new UsagerService();
    }

    public void setAgent(AgentAdministratif agent) {
        this.agentConnecte = agent;
    }

    @FXML
    private void initialize() {
        setupActeTable();
        loadActes();

        typeFiltreCombo.setItems(FXCollections.observableArrayList(TypeActe.values()));
        statutFiltreCombo.setItems(FXCollections.observableArrayList(StatutActe.values()));

        dateDebutPicker.setValue(LocalDate.now().minusMonths(1));
        dateFinPicker.setValue(LocalDate.now());

        fixTableHeaders(acteTable);

        acteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void setupActeTable() {
        numeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroActe"));
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTypeActe().toString()));
        usagerCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getUsager().getPrenom() + " " +
                                cellData.getValue().getUsager().getNom()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateEmission"));
        dateCol.setCellFactory(column -> new TableCell<ActeAdministratif, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

        statutCol.setCellFactory(column -> new TableCell<ActeAdministratif, StatutActe>() {
            @Override
            protected void updateItem(StatutActe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    switch (item) {
                        case VALIDE:
                            setStyle("-fx-background-color: #C8E6C9;");
                            break;
                        case ANNULE:
                            setStyle("-fx-background-color: #FFCDD2;");
                            break;
                        case EXPIRE:
                            setStyle("-fx-background-color: #E0E0E0;");
                            break;
                        default:
                            setStyle("-fx-background-color: #FFF9C4;");
                    }
                }
            }
        });

        acteTable.setItems(acteList);
        acteTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showActeDetails(newVal));
    }

    private void loadActes() {
        List<ActeAdministratif> actes = acteService.listerTous();
        appliquerFiltres(actes);
    }

    private void appliquerFiltres(List<ActeAdministratif> actes) {
        TypeActe typeFiltre = typeFiltreCombo.getValue();
        StatutActe statutFiltre = statutFiltreCombo.getValue();
        LocalDate debut = dateDebutPicker.getValue();
        LocalDate fin = dateFinPicker.getValue();

        List<ActeAdministratif> filtres = actes.stream()
                .filter(a -> typeFiltre == null || a.getTypeActe() == typeFiltre)
                .filter(a -> statutFiltre == null || a.getStatut() == statutFiltre)
                .filter(a -> debut == null || !a.getDateEmission().toLocalDate().isBefore(debut))
                .filter(a -> fin == null || !a.getDateEmission().toLocalDate().isAfter(fin))
                .toList();

        acteList.setAll(filtres);
    }

    private void showActeDetails(ActeAdministratif acte) {
        if (acte == null) {
            clearDetails();
            return;
        }

        this.acteSelectionne = acte;

        numeroActeLabel.setText(acte.getNumeroActe());
        typeActeLabel.setText(acte.getTypeActe().toString());
        dateEmissionLabel.setText(
                acte.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dateValiditeLabel.setText(
                acte.getDateValidite() != null ?
                        acte.getDateValidite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                        "Sans expiration");

        Usager usager = acte.getUsager();
        usagerInfoLabel.setText(
                usager.getPrenom() + " " + usager.getNom() + "\n" +
                        "Né(e) le: " + usager.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                        "Email: " + usager.getEmail());

        if (acte.getAgentEmetteur() != null) {
            agentEmetteurLabel.setText(
                    acte.getAgentEmetteur().getPrenom() + " " +
                            acte.getAgentEmetteur().getNom() + " (" +
                            acte.getAgentEmetteur().getService() + ")");
        } else {
            agentEmetteurLabel.setText("Inconnu");
        }

        statutActeLabel.setText(acte.getStatut().toString());
        contenuActeArea.setText(acte.getContenu());

        // Activer/désactiver les boutons selon le statut
        boolean modifiable = acte.getStatut() == StatutActe.VALIDE;
        annulerButton.setDisable(!modifiable);
    }

    private void clearDetails() {
        numeroActeLabel.setText("");
        typeActeLabel.setText("");
        dateEmissionLabel.setText("");
        dateValiditeLabel.setText("");
        usagerInfoLabel.setText("");
        agentEmetteurLabel.setText("");
        statutActeLabel.setText("");
        contenuActeArea.clear();
        annulerButton.setDisable(true);
    }

    @FXML
    private void handleRechercher() {
        String critere = rechercheField.getText().trim().toLowerCase();

        if (critere.isEmpty()) {
            loadActes();
            return;
        }

        List<ActeAdministratif> resultats = acteService.listerTous().stream()
                .filter(a -> a.getNumeroActe().toLowerCase().contains(critere) ||
                        a.getUsager().getNom().toLowerCase().contains(critere) ||
                        a.getUsager().getPrenom().toLowerCase().contains(critere) ||
                        a.getTypeActe().toString().toLowerCase().contains(critere))
                .toList();

        appliquerFiltres(resultats);
    }

    @FXML
    private void handleFiltrer() {
        loadActes();
    }

    @FXML
    private void handleVisualiser() {
        if (acteSelectionne == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Visualisation de l'acte");
        dialog.setHeaderText(acteSelectionne.getNumeroActe() + " — " + acteSelectionne.getTypeActe());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(12);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setPrefWidth(600);

        // En-tête acte
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(16);
        grid.setVgap(8);
        grid.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-padding: 16;");

        String[][] infos = {
                {"N° Acte :", acteSelectionne.getNumeroActe()},
                {"Type :", acteSelectionne.getTypeActe().toString()},
                {"Date émission :", acteSelectionne.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))},
                {"Statut :", acteSelectionne.getStatut().toString()},
                {"Usager :", acteSelectionne.getUsager().getPrenom() + " " + acteSelectionne.getUsager().getNom()}
        };

        for (int i = 0; i < infos.length; i++) {
            Label cle = new Label(infos[i][0]);
            cle.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 12px;");
            Label val = new Label(infos[i][1]);
            val.setStyle("-fx-text-fill: #1a2035; -fx-font-size: 12px;");
            javafx.scene.layout.GridPane.setColumnIndex(cle, 0);
            javafx.scene.layout.GridPane.setRowIndex(cle, i);
            javafx.scene.layout.GridPane.setColumnIndex(val, 1);
            javafx.scene.layout.GridPane.setRowIndex(val, i);
            grid.getChildren().addAll(cle, val);
        }

        Label contenuLabel = new Label("Contenu :");
        contenuLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 13px;");

        TextArea contenu = new TextArea(acteSelectionne.getContenu());
        contenu.setEditable(false);
        contenu.setWrapText(true);
        contenu.setPrefHeight(200);
        contenu.setStyle("-fx-background-color: #f7fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12px;");

        content.getChildren().addAll(grid, contenuLabel, contenu);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    @FXML
    private void handleImprimer() {
        if (acteSelectionne == null) return;

        try {
            java.io.File tempFile = java.io.File.createTempFile("acte_", ".txt");

            StringBuilder sb = new StringBuilder();
            sb.append("ACTE ADMINISTRATIF\n");
            sb.append("==================\n\n");
            sb.append("Numéro     : ").append(acteSelectionne.getNumeroActe()).append("\n");
            sb.append("Type       : ").append(acteSelectionne.getTypeActe()).append("\n");
            sb.append("Date       : ").append(acteSelectionne.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
            sb.append("Statut     : ").append(acteSelectionne.getStatut()).append("\n");
            sb.append("Usager     : ").append(acteSelectionne.getUsager().getPrenom()).append(" ").append(acteSelectionne.getUsager().getNom()).append("\n");
            sb.append("\nCONTENU :\n\n");
            sb.append(acteSelectionne.getContenu());

            java.nio.file.Files.writeString(tempFile.toPath(), sb.toString());

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().print(tempFile);
                log.info("Impression lancée pour l'acte: {}", acteSelectionne.getNumeroActe());
            } else {
                showError("L'impression n'est pas supportée sur ce système");
            }

        } catch (Exception e) {
            log.error("Erreur impression: {}", e.getMessage());
            showError("Erreur lors de l'impression");
        }
    }

    @FXML
    private void handleExporter() {
        if (acteSelectionne == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter l'acte");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File file = fileChooser.showSaveDialog(acteTable.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("ACTE ADMINISTRATIF");
                writer.println("==================");
                writer.println("Numéro: " + acteSelectionne.getNumeroActe());
                writer.println("Type: " + acteSelectionne.getTypeActe());
                writer.println("Date d'émission: " +
                        acteSelectionne.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                writer.println("Usager: " + acteSelectionne.getUsager().getPrenom() + " " +
                        acteSelectionne.getUsager().getNom());
                writer.println("\nCONTENU:\n");
                writer.println(acteSelectionne.getContenu());

                showSuccess("Acte exporté avec succès");
            } catch (Exception e) {
                log.error("Erreur lors de l'export: {}", e.getMessage());
                showError("Erreur lors de l'export");
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        if (acteSelectionne == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Annuler l'acte");
        confirm.setHeaderText("Annulation de l'acte " + acteSelectionne.getNumeroActe());
        confirm.setContentText("Êtes-vous sûr de vouloir annuler cet acte ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ActeAdministratif annule = acteService.annulerActe(
                    acteSelectionne.getId(), "Annulé par agent");

            if (annule != null) {
                showSuccess("Acte annulé avec succès");
                loadActes();
            } else {
                showError("Erreur lors de l'annulation");
            }
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/agent/agent_dashboard.fxml"));
            Parent root = loader.load();

            AgentDashboardController controller = loader.getController();
            controller.setAgent((AgentAdministratif) SessionManager.getInstance().getUtilisateurConnecte());

            Stage stage = (Stage) acteTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.centerOnScreen();
        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            showError("Erreur lors du retour");
        }
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}