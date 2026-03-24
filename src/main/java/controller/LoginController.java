package controller;

import controller.agent.AgentDashboardController;
import controller.usager.UsagerDashboardController;
import dao.utils.HibernateUtil;
import model.Personne;
import model.Administrateur;
import model.AgentAdministratif;
import model.Usager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import service.AuthentificationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import lombok.extern.slf4j.Slf4j;

import controller.admin.AdminDashboardController;

import java.util.Optional;

@Slf4j
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private final AuthentificationService authService;

    public LoginController() {
        this.authService = new AuthentificationService();
    }

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        loginButton.setDefaultButton(true);

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            verifierTypeUtilisateur(newVal);
        });

    }

    private void verifierTypeUtilisateur(String email) {
        if (email == null || email.trim().isEmpty()) {
            passwordField.setDisable(false);
            passwordField.setPromptText("Mot de passe");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Vérifier si c'est un usager
            Query<Usager> usagerQuery = session.createQuery(
                    "FROM Usager WHERE email = :email", Usager.class);
            usagerQuery.setParameter("email", email);
            boolean isUsager = usagerQuery.uniqueResultOptional().isPresent();

            if (isUsager) {
                passwordField.setDisable(true);
                passwordField.setPromptText("Pas de mot de passe requis");
                passwordField.clear();
            } else {
                passwordField.setDisable(false);
                passwordField.setPromptText("Mot de passe");
            }

        } catch (Exception e) {
            log.error("Erreur vérification email: {}", e.getMessage());
            passwordField.setDisable(false);
            passwordField.setPromptText("Mot de passe");
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showError("Veuillez saisir votre email");
            return;
        }

        Optional<Personne> personneOpt;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usager> usagerQuery = session.createQuery(
                    "FROM Usager WHERE email = :email", Usager.class);
            usagerQuery.setParameter("email", email);
            boolean isUsager = usagerQuery.uniqueResultOptional().isPresent();

            if (isUsager) {
                // Usager : pas besoin de mot de passe
                personneOpt = authService.loginUsager(email);
            } else {
                String password = passwordField.getText().trim();
                if (password.isEmpty()) {
                    showError("Veuillez saisir le mot de passe");
                    return;
                }
                personneOpt = authService.login(email, password);
            }

            if (personneOpt.isPresent()) {
                Personne personne = personneOpt.get();
                redirectToDashboard(personne);
            } else {
                showError("Email incorrect");
            }

        } catch (Exception e) {
            log.error("Erreur connexion: {}", e.getMessage());
            showError("Erreur lors de la connexion");
        }
    }

    private void redirectToDashboard(Personne personne) {
        try {
            String fxmlFile;
            String title;

            switch (personne) {
                case Administrateur administrateur -> {
                    fxmlFile = "/fxml/admin/admin_dashboard.fxml";
                    title = "Tableau de bord Administrateur";
                }
                case AgentAdministratif agentAdministratif -> {
                    fxmlFile = "/fxml/agent/agent_dashboard.fxml";
                    title = "Tableau de bord Agent";
                }
                case Usager usager -> {
                    fxmlFile = "/fxml/usager/usager_dashboard.fxml";
                    title = "Espace Usager";
                }
                case null, default -> {
                    showError("Type d'utilisateur inconnu");
                    return;
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Passer l'utilisateur connecté au contrôleur du dashboard
            Object controller = loader.getController();
            if (controller instanceof AdminDashboardController) {
                ((AdminDashboardController) controller).setAdmin((Administrateur) personne);
            } else if (controller instanceof AgentDashboardController) {
                ((AgentDashboardController) controller).setAgent((AgentAdministratif) personne);
            } else if (controller instanceof UsagerDashboardController) {
                ((UsagerDashboardController) controller).setUsager((Usager) personne);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Définir la taille
            stage.setWidth(1024);
            stage.setHeight(768);
            stage.setMinWidth(1024);
            stage.setMinHeight(768);

            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (Exception e) {
            log.error("Erreur lors de la redirection: ", e);
            e.printStackTrace();
            showError("Erreur de navigation: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

}