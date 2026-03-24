package controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import model.Administrateur;
import model.Configuration;
import service.ConfigurationService;
import utils.AlertUtil;
import utils.SessionManager;

@Slf4j
public class ConfigurationController {

    @FXML private TextField nomEtablissementField;
    @FXML private TextField adresseField;
    @FXML private TextField anneeAcademiqueField;
    @FXML private TextField prefixeDemandeField;
    @FXML private TextField delaiTraitementField;
    @FXML private TextField nomSignataireField;
    @FXML private TextField fonctionSignataireField;
    @FXML private Button handleRetour;

    private final ConfigurationService configurationService;
    private Administrateur adminConnecte;

    public ConfigurationController() {
        this.configurationService = new ConfigurationService();
    }

    @FXML
    private void initialize() {
        chargerConfiguration();
    }

    public void setAdmin(Administrateur admin) {
        this.adminConnecte = admin;
    }

    private void chargerConfiguration() {
        Configuration config = configurationService.getConfiguration();
        nomEtablissementField.setText(config.getNomEtablissement());
        adresseField.setText(config.getAdresse());
        anneeAcademiqueField.setText(config.getAnneeAcademique());
        prefixeDemandeField.setText(config.getPrefixeDemande());
        delaiTraitementField.setText(String.valueOf(config.getDelaiTraitement()));
        nomSignataireField.setText(config.getNomSignataire());
        fonctionSignataireField.setText(config.getFonctionSignataire());
    }

    @FXML
    private void handleSauvegarder() {
        try {
            int delai = Integer.parseInt(delaiTraitementField.getText().trim());

            Configuration config = Configuration.builder()
                    .id(1)
                    .nomEtablissement(nomEtablissementField.getText().trim())
                    .adresse(adresseField.getText().trim())
                    .anneeAcademique(anneeAcademiqueField.getText().trim())
                    .prefixeDemande(prefixeDemandeField.getText().trim())
                    .delaiTraitement(delai)
                    .nomSignataire(nomSignataireField.getText().trim())
                    .fonctionSignataire(fonctionSignataireField.getText().trim())
                    .build();

            Configuration saved = configurationService.sauvegarder(config);
            if (saved != null) {
                AlertUtil.showSuccess("Configuration sauvegardée avec succès");
            } else {
                AlertUtil.showError("Erreur lors de la sauvegarde");
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Le délai de traitement doit être un nombre entier");
        }
    }

    @FXML
    private void handleAnnuler() {
        chargerConfiguration();
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/admin_dashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setAdmin((Administrateur) SessionManager.getInstance().getUtilisateurConnecte());

            Stage stage = (Stage) nomEtablissementField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.centerOnScreen();
        } catch (Exception e) {
            log.error("Erreur retour dashboard: {}", e.getMessage());
            AlertUtil.showError("Erreur lors du retour");
        }
    }

}
