package controller.agent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import model.ActeAdministratif;
import model.AgentAdministratif;
import model.Demande;
import model.enums.StatutDemande;
import model.enums.TypeActe;
import service.ActeService;
import service.DemandeService;
import utils.AlertUtil;
import utils.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class RechercheController {

    // ─── Tables actes ───
    @FXML private TableView<ActeAdministratif> resultatActesTable;
    @FXML private TableColumn<ActeAdministratif, String> resNumeroCol;
    @FXML private TableColumn<ActeAdministratif, String> resTypeCol;
    @FXML private TableColumn<ActeAdministratif, String> resUsagerCol;
    @FXML private TableColumn<ActeAdministratif, String> resDateCol;
    @FXML private TableColumn<ActeAdministratif, String> resStatutCol;

    // ─── Filtres actes ───
    @FXML private TextField rechercheNumeroField;
    @FXML private ComboBox<TypeActe> rechercheTypeCombo;
    @FXML private TextField rechercheNomField;
    @FXML private TextField recherchePrenomField;
    @FXML private DatePicker rechercheDateDebut;
    @FXML private DatePicker rechercheDateFin;

    // ─── Tables demandes ───
    @FXML private TableView<Demande> resultatDemandesTable;
    @FXML private TableColumn<Demande, String> resDemNumeroCol;
    @FXML private TableColumn<Demande, String> resDemTypeCol;
    @FXML private TableColumn<Demande, String> resDemUsagerCol;
    @FXML private TableColumn<Demande, String> resDemDateCol;
    @FXML private TableColumn<Demande, String> resDemStatutCol;

    // ─── Filtres demandes ───
    @FXML private TextField rechercheDemandeNumeroField;
    @FXML private ComboBox<StatutDemande> rechercheDemandeStatutCombo;
    @FXML private TextField rechercheDemandeNomField;
    @FXML private ComboBox<TypeActe> rechercheDemandeTypeCombo;
    @FXML private DatePicker rechercheDemandeDateDebut;
    @FXML private DatePicker rechercheDemandeDateFin;

    private final ActeService acteService;
    private final DemandeService demandeService;
    private AgentAdministratif agentConnecte;

    private final ObservableList<ActeAdministratif> acteList = FXCollections.observableArrayList();
    private final ObservableList<Demande> demandeList = FXCollections.observableArrayList();

    public RechercheController() {
        this.acteService = new ActeService();
        this.demandeService = new DemandeService();
    }

    public void setAgent(AgentAdministratif agent) {
        this.agentConnecte = agent;
    }

    @FXML
    private void initialize() {
        setupActesTable();
        setupDemandesTable();

        rechercheTypeCombo.setItems(FXCollections.observableArrayList(TypeActe.values()));
        rechercheDemandeStatutCombo.setItems(FXCollections.observableArrayList(StatutDemande.values()));
        rechercheDemandeTypeCombo.setItems(FXCollections.observableArrayList(TypeActe.values()));
    }

    private void setupActesTable() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        resNumeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroActe"));
        resTypeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTypeActe().toString()));
        resUsagerCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getUsager().getPrenom() + " " + c.getValue().getUsager().getNom()));
        resDateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDateEmission().format(formatter)));
        resStatutCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getStatut().toString()));

        resultatActesTable.setItems(acteList);
        resultatActesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        fixTableHeaders(resultatActesTable);
    }

    private void setupDemandesTable() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        resDemNumeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroDemande"));
        resDemTypeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTypeActeDemande().toString()));
        resDemUsagerCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDemandeur().getPrenom() + " " + c.getValue().getDemandeur().getNom()));
        resDemDateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDateDemande().format(formatter)));
        resDemStatutCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getStatut().toString()));

        resultatDemandesTable.setItems(demandeList);
        resultatDemandesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        fixTableHeaders(resultatDemandesTable);
    }

    @FXML
    private void handleRechercherActes() {
        String numero = rechercheNumeroField.getText().trim().toLowerCase();
        String nom = rechercheNomField.getText().trim().toLowerCase();
        String prenom = recherchePrenomField.getText().trim().toLowerCase();
        TypeActe type = rechercheTypeCombo.getValue();
        LocalDate debut = rechercheDateDebut.getValue();
        LocalDate fin = rechercheDateFin.getValue();

        List<ActeAdministratif> resultats = acteService.listerTous().stream()
                .filter(a -> numero.isEmpty() || a.getNumeroActe().toLowerCase().contains(numero))
                .filter(a -> nom.isEmpty() || a.getUsager().getNom().toLowerCase().contains(nom))
                .filter(a -> prenom.isEmpty() || a.getUsager().getPrenom().toLowerCase().contains(prenom))
                .filter(a -> type == null || a.getTypeActe() == type)
                .filter(a -> debut == null || !a.getDateEmission().toLocalDate().isBefore(debut))
                .filter(a -> fin == null || !a.getDateEmission().toLocalDate().isAfter(fin))
                .toList();

        acteList.setAll(resultats);
    }

    @FXML
    private void handleReset() {
        rechercheNumeroField.clear();
        rechercheNomField.clear();
        recherchePrenomField.clear();
        rechercheTypeCombo.setValue(null);
        rechercheDateDebut.setValue(null);
        rechercheDateFin.setValue(null);
        acteList.clear();
    }

    @FXML
    private void handleRechercherDemandes() {
        String numero = rechercheDemandeNumeroField.getText().trim().toLowerCase();
        String nom = rechercheDemandeNomField.getText().trim().toLowerCase();
        StatutDemande statut = rechercheDemandeStatutCombo.getValue();
        TypeActe type = rechercheDemandeTypeCombo.getValue();
        LocalDate debut = rechercheDemandeDateDebut.getValue();
        LocalDate fin = rechercheDemandeDateFin.getValue();

        List<Demande> resultats = demandeService.listerToutes().stream()
                .filter(d -> numero.isEmpty() || d.getNumeroDemande().toLowerCase().contains(numero))
                .filter(d -> nom.isEmpty() || d.getDemandeur().getNom().toLowerCase().contains(nom))
                .filter(d -> statut == null || d.getStatut() == statut)
                .filter(d -> type == null || d.getTypeActeDemande() == type)
                .filter(d -> debut == null || !d.getDateDemande().toLocalDate().isBefore(debut))
                .filter(d -> fin == null || !d.getDateDemande().toLocalDate().isAfter(fin))
                .toList();

        demandeList.setAll(resultats);
    }

    @FXML
    private void handleResetDemandes() {
        rechercheDemandeNumeroField.clear();
        rechercheDemandeNomField.clear();
        rechercheDemandeStatutCombo.setValue(null);
        rechercheDemandeTypeCombo.setValue(null);
        rechercheDemandeDateDebut.setValue(null);
        rechercheDemandeDateFin.setValue(null);
        demandeList.clear();
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/agent/agent_dashboard.fxml"));
            Parent root = loader.load();

            AgentDashboardController controller = loader.getController();
            controller.setAgent((AgentAdministratif) SessionManager.getInstance().getUtilisateurConnecte());

            Stage stage = (Stage) resultatActesTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.centerOnScreen();
        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour");
        }
    }

    private void fixTableHeaders(TableView<?> table) {
        table.widthProperty().addListener((obs, oldVal, newVal) -> {
            table.lookupAll(".column-header .label").forEach(node ->
                    node.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"));
        });
    }

}