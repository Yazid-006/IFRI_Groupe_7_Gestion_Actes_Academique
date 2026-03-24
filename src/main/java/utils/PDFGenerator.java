package utils;

import model.ActeAdministratif;
import model.Usager;
import lombok.extern.slf4j.Slf4j;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class PDFGenerator {

    public static boolean genererActePDF(ActeAdministratif acte, String chemin) {
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(chemin));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Titre
            Paragraph title = new Paragraph("ACTE ADMINISTRATIF")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold();
            document.add(title);

            document.add(new Paragraph("\n"));

            // Informations de l'acte
            document.add(new Paragraph("Numéro d'acte : " + acte.getNumeroActe()));
            document.add(new Paragraph("Type d'acte    : " + acte.getTypeActe()));
            document.add(new Paragraph("Date d'émission: " +
                    acte.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

            if (acte.getDateValidite() != null) {
                document.add(new Paragraph("Date de validité: " +
                        acte.getDateValidite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }

            document.add(new Paragraph("\n--- INFORMATIONS USAGER ---\n"));

            Usager usager = acte.getUsager();
            document.add(new Paragraph("Nom complet : " + usager.getPrenom() + " " + usager.getNom()));
            document.add(new Paragraph("Date naissance: " +
                    usager.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("N° usager    : " + usager.getNumeroUsager()));

            document.add(new Paragraph("\n--- CONTENU DE L'ACTE ---\n"));
            document.add(new Paragraph(acte.getContenu()));

            document.add(new Paragraph("\n\nFait à Cotonou, le " +
                    java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

            document.add(new Paragraph("\n\nL'agent administratif\n"));
            document.add(new Paragraph(acte.getAgentEmetteur().getPrenom() + " " +
                    acte.getAgentEmetteur().getNom()));

            document.close();
            log.info("PDF généré: {}", chemin);
            return true;

        } catch (Exception e) {
            log.error("Erreur génération PDF: {}", e.getMessage());
            return false;
        }
    }

    public static String genererRecu(ActeAdministratif acte) {
        return "RÉCÉPISSÉ DE DÉLIVRANCE D'ACTE\n" +
                "================================\n\n" +
                "Je soussigné, atteste que l'acte n° " + acte.getNumeroActe() +
                " a été délivré à " + acte.getUsager().getPrenom() + " " +
                acte.getUsager().getNom() + ".\n\n" +
                "Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

}