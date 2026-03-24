package controller.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Administrateur;
import service.RapportService;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import utils.AlertUtil;
import utils.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class RapportController {

    @FXML private TabPane rapportTabPane;

    // Onglet Statistiques globales
    @FXML private Label totalDemandesLabel;
    @FXML private Label demandesEnAttenteLabel;
    @FXML private Label demandesValideesLabel;
    @FXML private Label demandesRejeteesLabel;
    @FXML private Label totalActesLabel;
    @FXML private Label actesMoisLabel;

    @FXML private PieChart actesParTypeChart;
    @FXML private CategoryAxis actesTypeAxis;
    @FXML private NumberAxis actesCountAxis;

    @FXML private Label chartPlaceholder;

    // Onglet Rapport périodique
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Label periodeDemandesLabel;
    @FXML private Label periodeValideesLabel;
    @FXML private Label periodeRejeteesLabel;
    @FXML private Label periodeActesLabel;
    @FXML private TableView<Map.Entry<String, Long>> periodeTypeTable;
    @FXML private TableColumn<Map.Entry<String, Long>, String> typeCol;
    @FXML private TableColumn<Map.Entry<String, Long>, Long> countCol;

    // Onglet Export
    @FXML private TextArea rapportTextArea;
    @FXML private ComboBox<String> rapportTypeCombo;

    private final RapportService rapportService;
    private final ObservableList<Map.Entry<String, Long>> typeList = FXCollections.observableArrayList();

    public RapportController() {
        this.rapportService = new RapportService();
    }

    @FXML
    private void initialize() {
        setupPeriodiqueTable();
        loadStatistiquesGlobales();

        dateDebutPicker.setValue(LocalDate.now().withDayOfMonth(1));
        dateFinPicker.setValue(LocalDate.now());

        rapportTypeCombo.setItems(FXCollections.observableArrayList(
                "Statistiques globales", "Rapport périodique", "Rapport par agent"
        ));

        fixTableHeaders(periodeTypeTable);
    }

    private void setupPeriodiqueTable() {
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey()));
        countCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getValue()).asObject());

        periodeTypeTable.setItems(typeList);

        periodeTypeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/admin_dashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setAdmin((Administrateur) SessionManager.getInstance().getUtilisateurConnecte());

            Stage stage = (Stage) rapportTextArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.centerOnScreen();
        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour");
        }
    }

    private void loadStatistiquesGlobales() {
        Map<String, Object> stats = rapportService.statistiquesGlobales();

        totalDemandesLabel.setText(stats.get("totalDemandes").toString());
        demandesEnAttenteLabel.setText(stats.get("demandesEnAttente").toString());
        demandesValideesLabel.setText(stats.get("demandesValidees").toString());
        demandesRejeteesLabel.setText(stats.get("demandesRejetees").toString());
        totalActesLabel.setText(stats.get("totalActes").toString());
        actesMoisLabel.setText(stats.get("actesMois").toString());

        Map<model.enums.TypeActe, Long> actesParType = (Map<model.enums.TypeActe, Long>) stats.get("actesParType");

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<model.enums.TypeActe, Long> entry : actesParType.entrySet()) {
            if (entry.getValue() > 0) {
                pieData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
            }
        }

        if (pieData.isEmpty()) {
            actesParTypeChart.setVisible(false);
            chartPlaceholder.setVisible(true);
        } else {
            actesParTypeChart.setVisible(true);
            chartPlaceholder.setVisible(false);
            actesParTypeChart.setData(pieData);
            actesParTypeChart.setLegendVisible(true);
        }

    }

    @FXML
    private void handleGenererRapportPeriodique() {
        LocalDate debut = dateDebutPicker.getValue();
        LocalDate fin = dateFinPicker.getValue();

        if (debut == null || fin == null) {
            showError("Veuillez sélectionner une période");
            return;
        }

        if (debut.isAfter(fin)) {
            showError("La date de début doit être antérieure à la date de fin");
            return;
        }

        Map<String, Object> rapport = rapportService.rapportPeriodique(debut, fin);

        periodeDemandesLabel.setText(rapport.get("demandesPeriode").toString());
        periodeValideesLabel.setText(rapport.get("demandesValideesPeriode").toString());
        periodeRejeteesLabel.setText(rapport.get("demandesRejeteesPeriode").toString());
        periodeActesLabel.setText(rapport.get("actesPeriode").toString());

        Map<model.enums.TypeActe, Long> actesParType =
                (Map<model.enums.TypeActe, Long>) rapport.get("actesParTypePeriode");

        typeList.clear();
        for (Map.Entry<model.enums.TypeActe, Long> entry : actesParType.entrySet()) {
            typeList.add(Map.entry(entry.getKey().toString(), entry.getValue()));
        }
    }

    @FXML
    private void handleExporterRapport() {
        String type = rapportTypeCombo.getValue();
        if (type == null) {
            showError("Veuillez sélectionner un type de rapport");
            return;
        }

        Map<String, Object> rapport;
        String titre;

        switch (type) {
            case "Statistiques globales":
                rapport = rapportService.statistiquesGlobales();
                titre = "RAPPORT STATISTIQUES GLOBALES";
                break;
            case "Rapport périodique":
                if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
                    showError("Veuillez sélectionner une période");
                    return;
                }
                rapport = rapportService.rapportPeriodique(
                        dateDebutPicker.getValue(), dateFinPicker.getValue());
                titre = "RAPPORT PÉRIODIQUE DU " +
                        dateDebutPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " AU " + dateFinPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            default:
                showError("Type de rapport non disponible");
                return;
        }

        String texteRapport = rapportService.exporterRapportTexte(rapport, titre);
        rapportTextArea.setText(texteRapport);
    }

    @FXML
    private void handleImprimerRapport() {
        String contenu = rapportTextArea.getText();
        if (contenu == null || contenu.trim().isEmpty()) {
            AlertUtil.showWarning("Aucun rapport à imprimer");
            return;
        }

        try {
            // Créer un fichier temporaire
            java.io.File tempFile = java.io.File.createTempFile("rapport_", ".txt");
            java.nio.file.Files.writeString(tempFile.toPath(), contenu);

            // Lancer l'impression (utilisation de l'application par défaut)
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().print(tempFile);
            } else {
                AlertUtil.showError("L'impression n'est pas supportée sur ce système");
            }

            log.info("Impression lancée pour le rapport");
        } catch (Exception e) {
            log.error("Erreur lors de l'impression: {}", e.getMessage());
            AlertUtil.showError("Erreur lors de l'impression");
        }
    }

    @FXML
    private void handleExporterPDF() {
        String contenu = rapportTextArea.getText();
        if (contenu == null || contenu.trim().isEmpty()) {
            AlertUtil.showWarning("Aucun rapport à exporter");
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Exporter le rapport en PDF");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("rapport_" +
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");

        java.io.File file = fileChooser.showSaveDialog(rapportTextArea.getScene().getWindow());

        if (file != null) {
            try {
                String chemin = file.getAbsolutePath();
                if (!chemin.toLowerCase().endsWith(".pdf")) {
                    chemin += ".pdf";
                }

                // Générer le PDF avec iText
                com.itextpdf.kernel.pdf.PdfWriter writer =
                        new com.itextpdf.kernel.pdf.PdfWriter(chemin);
                com.itextpdf.kernel.pdf.PdfDocument pdf =
                        new com.itextpdf.kernel.pdf.PdfDocument(writer);
                com.itextpdf.layout.Document document =
                        new com.itextpdf.layout.Document(pdf);

                // Ajouter le contenu
                document.add(new com.itextpdf.layout.element.Paragraph("RAPPORT")
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                        .setFontSize(16)
                        .setBold());
                document.add(new com.itextpdf.layout.element.Paragraph("\n"));

                String[] lignes = contenu.split("\n");
                for (String ligne : lignes) {
                    document.add(new com.itextpdf.layout.element.Paragraph(ligne));
                }

                document.close();

                AlertUtil.showSuccess("Rapport exporté en PDF avec succès");
                log.info("Rapport PDF exporté vers: {}", chemin);

            } catch (Exception e) {
                log.error("Erreur lors de l'export PDF: {}", e.getMessage());
                AlertUtil.showError("Erreur lors de l'export PDF");
            }
        }
    }

    private void fixTableHeaders(TableView<?> table) {
        table.widthProperty().addListener((obs, oldVal, newVal) -> {
            table.lookupAll(".column-header .label").forEach(node ->
                    node.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"));
        });
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

    public void setAdmin(Administrateur admin) {
        log.info("RapportController chargé par admin: {}", admin.getLogin());
    }

}