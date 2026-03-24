package controller.usager;

import model.ActeAdministratif;
import model.enums.StatutActe;
import service.ActeService;
import utils.AlertUtil;
import utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class MesActesController {

    @FXML private TableView<ActeAdministratif>         acteTable;
    @FXML private TableColumn<ActeAdministratif, String>        acteNumeroCol;
    @FXML private TableColumn<ActeAdministratif, String>        acteTypeCol;
    @FXML private TableColumn<ActeAdministratif, LocalDateTime> acteDateCol;
    @FXML private TableColumn<ActeAdministratif, String>        acteStatutCol;

    @FXML private Label    acteNumeroDetailLabel;
    @FXML private Label    acteTypeDetailLabel;
    @FXML private Label    acteDateDetailLabel;
    @FXML private Label    acteValiditeLabel;
    @FXML private Label    acteStatutDetailLabel;
    @FXML private TextArea acteContenuArea;

    @FXML private Button telechargerActeButton;
    @FXML private Button imprimerActeButton;

    private final ActeService acteService = new ActeService();
    private final ObservableList<ActeAdministratif> acteList = FXCollections.observableArrayList();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        setupActeTable();
        chargerDonnees();
    }

    private void setupActeTable() {
        acteNumeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroActe"));

        acteTypeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTypeActe().toString()));

        acteDateCol.setCellValueFactory(new PropertyValueFactory<>("dateEmission"));
        acteDateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : FORMATTER.format(item));
            }
        });

        acteStatutCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatut().toString()));

        acteTable.setItems(acteList);
        acteTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showActeDetails(newVal));
    }

    private void chargerDonnees() {
        try {
            List<ActeAdministratif> actes = acteService.listerParUsager(
                    SessionManager.getInstance().getUsager());
            acteList.setAll(actes);
        } catch (Exception e) {
            log.error("Erreur chargement actes: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du chargement des actes.");
        }
    }

    private void showActeDetails(ActeAdministratif acte) {
        if (acte == null) {
            clearActeDetails();
            return;
        }

        acteNumeroDetailLabel.setText(acte.getNumeroActe());
        acteTypeDetailLabel.setText(acte.getTypeActe().toString());
        acteDateDetailLabel.setText(FORMATTER.format(acte.getDateEmission()));
        acteValiditeLabel.setText(acte.getDateValidite() != null
                ? FORMATTER.format(acte.getDateValidite())
                : "Sans expiration");
        acteStatutDetailLabel.setText(acte.getStatut().toString());
        acteContenuArea.setText(acte.getContenu());

        boolean valide = acte.getStatut() == StatutActe.VALIDE;
        telechargerActeButton.setDisable(!valide);
        imprimerActeButton.setDisable(!valide);
    }

    private void clearActeDetails() {
        acteNumeroDetailLabel.setText("");
        acteTypeDetailLabel.setText("");
        acteDateDetailLabel.setText("");
        acteValiditeLabel.setText("");
        acteStatutDetailLabel.setText("");
        acteContenuArea.clear();
        telechargerActeButton.setDisable(true);
        imprimerActeButton.setDisable(true);
    }

    @FXML
    private void handleTelechargerActe() {
        ActeAdministratif acte = acteTable.getSelectionModel().getSelectedItem();
        if (acte == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Télécharger l'acte");
        fileChooser.setInitialFileName(acte.getNumeroActe() + ".txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));

        File file = fileChooser.showSaveDialog(acteTable.getScene().getWindow());
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("ACTE ADMINISTRATIF");
            writer.println("==================");
            writer.println("Numéro      : " + acte.getNumeroActe());
            writer.println("Type        : " + acte.getTypeActe());
            writer.println("Date        : " + FORMATTER.format(acte.getDateEmission()));
            writer.println("Statut      : " + acte.getStatut());
            writer.println("\nCONTENU :\n");
            writer.println(acte.getContenu());

            AlertUtil.showInfo("Acte téléchargé avec succès.");
        } catch (Exception e) {
            log.error("Erreur téléchargement acte: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du téléchargement.");
        }
    }

    @FXML
    private void handleImprimerActe() {
        AlertUtil.showInfo("Fonctionnalité d'impression à implémenter.");
    }

    @FXML
    private void handleActualiser() {
        chargerDonnees();
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/usager/usager_dashboard.fxml"));
            Parent root = loader.load();

            UsagerDashboardController controller = loader.getController();
            controller.setUsager(SessionManager.getInstance().getUsager());

            Stage stage = (Stage) acteTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour au tableau de bord.");
        }
    }
}
