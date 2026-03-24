package utils;

import model.Usager;
import model.Demande;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{8,15}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidNom(String nom) {
        return nom != null && !nom.trim().isEmpty() && nom.length() <= 50;
    }

    public static boolean isValidPrenom(String prenom) {
        return prenom != null && !prenom.trim().isEmpty() && prenom.length() <= 50;
    }

    public static boolean isValidDateNaissance(LocalDate dateNaissance) {
        if (dateNaissance == null) return false;
        LocalDate now = LocalDate.now();
        return dateNaissance.isBefore(now.minusYears(15)) &&
                dateNaissance.isAfter(now.minusYears(120));
    }

    public static boolean isValidMotif(String motif) {
        return motif != null && !motif.trim().isEmpty() && motif.length() <= 500;
    }

    public static boolean isValidUsager(Usager usager) {
        return usager != null &&
                isValidNom(usager.getNom()) &&
                isValidPrenom(usager.getPrenom()) &&
                isValidEmail(usager.getEmail()) &&
                isValidPhone(usager.getTelephone()) &&
                isValidDateNaissance(usager.getDateNaissance());
    }

    public static boolean isValidDemande(Demande demande) {
        return demande != null &&
                demande.getDemandeur() != null &&
                demande.getTypeActeDemande() != null &&
                isValidMotif(demande.getMotif());
    }

    public static String validateAndGetErrors(Usager usager) {
        StringBuilder errors = new StringBuilder();

        if (!isValidNom(usager.getNom())) {
            errors.append("Nom invalide\n");
        }
        if (!isValidPrenom(usager.getPrenom())) {
            errors.append("Prénom invalide\n");
        }
        if (!isValidEmail(usager.getEmail())) {
            errors.append("Email invalide\n");
        }
        if (!isValidPhone(usager.getTelephone())) {
            errors.append("Téléphone invalide\n");
        }
        if (!isValidDateNaissance(usager.getDateNaissance())) {
            errors.append("Date de naissance invalide\n");
        }

        return errors.toString();
    }

}