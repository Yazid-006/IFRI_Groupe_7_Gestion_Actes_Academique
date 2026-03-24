// utils/Constants.java
package utils;

public class Constants {

    // Base de données
    public static final String DB_URL = "jdbc:mysql://localhost:3306/gestion_actes_db";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Chemins
    public static final String DOSSIER_UPLOAD = "uploads/";
    public static final String DOSSIER_ACTES = "actes/";
    public static final String DOSSIER_TEMP = "temp/";

    // Formats
    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_DATE_HEURE = "dd/MM/yyyy HH:mm";
    public static final String FORMAT_FICHIER_DATE = "yyyyMMdd_HHmmss";

    // Messages
    public static final String MSG_ERREUR_CONNEXION = "Erreur de connexion à la base de données";
    public static final String MSG_ERREUR_SAUVEGARDE = "Erreur lors de la sauvegarde";
    public static final String MSG_ERREUR_SUPPRESSION = "Erreur lors de la suppression";
    public static final String MSG_SUCCES_SAUVEGARDE = "Enregistrement effectué avec succès";
    public static final String MSG_SUCCES_SUPPRESSION = "Suppression effectuée avec succès";
    public static final String MSG_CONFIRMATION_SUPPRESSION = "Voulez-vous vraiment supprimer ?";

    // Valeurs par défaut
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int SESSION_TIMEOUT_MINUTES = 30;

    // Statuts
    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_VALIDE = "VALIDE";
    public static final String STATUT_REJETE = "REJETE";
    public static final String STATUT_ANNULE = "ANNULE";

    // Types d'actes (libellés)
    public static final String ACTE_CERTIFICAT_SCOLARITE = "Certificat de scolarité";
    public static final String ACTE_ATTESTATION_TRAVAIL = "Attestation de travail";
    public static final String ACTE_AUTORISATION = "Autorisation";
    public static final String ACTE_COPIE = "Copie d'acte";

    // Fichiers FXML
    public static final String FXML_LOGIN = "/fxml/login.fxml";
    public static final String FXML_ADMIN_DASHBOARD = "/fxml/admin/admin_dashboard.fxml";
    public static final String FXML_AGENT_DASHBOARD = "/fxml/agent/agent_dashboard.fxml";
    public static final String FXML_USAGER_DASHBOARD = "/fxml/usager/usager_dashboard.fxml";

}