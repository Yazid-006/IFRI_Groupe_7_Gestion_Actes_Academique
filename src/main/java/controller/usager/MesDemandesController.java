package controller.usager;

import model.Demande;
import model.enums.StatutDemande;
import service.DemandeService;
import utils.SessionManager;
import utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class MesDemandesController {

    @FXML private TableView<Demande> demandeTable;
    @FXML private TableColumn<Demande, String>        demandeNumeroCol;
    @FXML private TableColumn<Demande, String>        demandeTypeCol;
    @FXML private TableColumn<Demande, LocalDateTime> demandeDateCol;
    @FXML private TableColumn<Demande, StatutDemande> demandeStatutCol;

    @FXML private Label   demandeNumeroDetailLabel;
    @FXML private Label   demandeTypeDetailLabel;
    @FXML private Label   demandeDateDetailLabel;
    @FXML private Label   demandeStatutDetailLabel;
    @FXML private Label   demandeTraitementLabel;
    @FXML private TextArea demandeMotifArea;

    private final DemandeService demandeService = new DemandeService();
    private final ObservableList<Demande> demandeList = FXCollections.observableArrayList();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        setupDemandeTable();
        chargerDonnees();
    }

    private void setupDemandeTable() {
        demandeNumeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroDemande"));

        demandeTypeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTypeActeDemande().toString()));

        demandeDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDemande"));
        demandeDateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : FORMATTER.format(item));
            }
        });

        demandeStatutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        demandeStatutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(StatutDemande item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        demandeTable.setItems(demandeList);
        demandeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showDemandeDetails(newVal));
    }

    private void chargerDonnees() {
        try {
            List<Demande> demandes = demandeService.listerParUsager(
                    SessionManager.getInstance().getUsager().getId());
            demandeList.setAll(demandes);
        } catch (Exception e) {
            log.error("Erreur chargement demandes: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du chargement des demandes");
        }
    }

    private void showDemandeDetails(Demande demande) {
        if (demande == null) {
            clearDemandeDetails();
            return;
        }

        demandeNumeroDetailLabel.setText(demande.getNumeroDemande());
        demandeTypeDetailLabel.setText(demande.getTypeActeDemande().toString());
        demandeDateDetailLabel.setText(FORMATTER.format(demande.getDateDemande()));
        demandeStatutDetailLabel.setText(demande.getStatut().toString());
        demandeMotifArea.setText(demande.getMotif());
        demandeTraitementLabel.setText(
                demande.getDateTraitement() != null
                        ? "Traitée le : " + FORMATTER.format(demande.getDateTraitement())
                        : "En attente de traitement");
    }

    private void clearDemandeDetails() {
        demandeNumeroDetailLabel.setText("");
        demandeTypeDetailLabel.setText("");
        demandeDateDetailLabel.setText("");
        demandeStatutDetailLabel.setText("");
        demandeTraitementLabel.setText("");
        demandeMotifArea.clear();
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

            Stage stage = (Stage) demandeTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour au tableau de bord");
        }
    }
}
