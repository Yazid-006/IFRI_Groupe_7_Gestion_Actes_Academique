package controller.usager;

import model.Demande;
import model.enums.TypeActe;
import service.DemandeService;
import utils.AlertUtil;
import utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class NouvelleDemandeController {

    @FXML private ComboBox<TypeActe> typeDemandeCombo;
    @FXML private TextArea           motifDemandeArea;
    @FXML private Label              infoCreationLabel;
    @FXML private Label              fichierJustificatifLabel;

    private final DemandeService demandeService = new DemandeService();
    private File fichierJustificatif;

    @FXML
    private void initialize() {
        typeDemandeCombo.setItems(FXCollections.observableArrayList(TypeActe.values()));
    }

    @FXML
    private void handleAjouterJustificatif() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un justificatif");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File fichier = fileChooser.showOpenDialog(typeDemandeCombo.getScene().getWindow());
        if (fichier != null) {
            this.fichierJustificatif = fichier;
            fichierJustificatifLabel.setText(fichier.getName());
            fichierJustificatifLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");
        }
    }

    @FXML
    private void handleSoumettreDemande() {
        TypeActe type  = typeDemandeCombo.getValue();
        String   motif = motifDemandeArea.getText().trim();

        if (type == null) {
            showError("Veuillez sélectionner un type d'acte.");
            return;
        }
        if (motif.isEmpty()) {
            showError("Veuillez saisir un motif.");
            return;
        }

        try {
            Demande nouvelle = demandeService.creerDemande(
                    SessionManager.getInstance().getUsager(), type, motif);

            if (nouvelle != null) {
                showSuccess("Demande " + nouvelle.getNumeroDemande() + " soumise avec succès !");
                resetFormulaire();
            } else {
                showError("Erreur lors de la soumission de la demande.");
            }
        } catch (Exception e) {
            log.error("Erreur soumission demande: {}", e.getMessage());
            showError("Une erreur inattendue s'est produite.");
        }
    }

    @FXML
    private void handleAnnuler() {
        resetFormulaire();
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/usager/usager_dashboard.fxml"));
            Parent root = loader.load();

            UsagerDashboardController controller = loader.getController();
            controller.setUsager(SessionManager.getInstance().getUsager());

            Stage stage = (Stage) typeDemandeCombo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour au tableau de bord.");
        }
    }

    private void resetFormulaire() {
        typeDemandeCombo.getSelectionModel().clearSelection();
        motifDemandeArea.clear();
        infoCreationLabel.setText("");
        fichierJustificatif = null;
        fichierJustificatifLabel.setText("Aucun fichier sélectionné");
        fichierJustificatifLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #a0aec0;");
    }

    private void showSuccess(String message) {
        infoCreationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
        infoCreationLabel.setText("✅  " + message);
    }

    private void showError(String message) {
        infoCreationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #e53e3e; -fx-font-weight: bold;");
        infoCreationLabel.setText("⚠️  " + message);
    }
}
