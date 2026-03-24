import dao.utils.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {

    static {
        // Initialiser Hibernate au chargement de la classe
        try {
            HibernateUtil.getSessionFactory();
            log.info("Hibernate initialisé avec succès");
        } catch (Exception e) {
            log.error("Erreur initialisation Hibernate: ", e);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            primaryStage.setTitle("Gestion des Actes Administratifs - Connexion");
            primaryStage.setScene(scene);
            primaryStage.setWidth(600);
            primaryStage.setHeight(400);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();

            log.info("Application démarrée avec succès");

        } catch (Exception e) {
            log.error("Erreur au démarrage: ", e);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            dao.utils.HibernateUtil.shutdown();
            log.info("Application arrêtée, connexions fermées");
        } catch (Exception e) {
            log.error("Erreur à l'arrêt: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}