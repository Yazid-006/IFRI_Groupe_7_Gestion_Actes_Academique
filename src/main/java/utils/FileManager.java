package utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class FileManager {

    private static final String BASE_PATH = "storage/";
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(Constants.FORMAT_FICHIER_DATE);

    static {
        createDirectories();
    }

    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(BASE_PATH + Constants.DOSSIER_UPLOAD));
            Files.createDirectories(Paths.get(BASE_PATH + Constants.DOSSIER_ACTES));
            Files.createDirectories(Paths.get(BASE_PATH + Constants.DOSSIER_TEMP));
        } catch (IOException e) {
            log.error("Erreur lors de la création des dossiers: {}", e.getMessage());
        }
    }

    public static String saveUploadedFile(InputStream inputStream, String originalFilename)
            throws IOException {
        String extension = getFileExtension(originalFilename);
        String filename = generateFileName("upload", extension);
        Path path = Paths.get(BASE_PATH + Constants.DOSSIER_UPLOAD + filename);

        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        log.info("Fichier sauvegardé: {}", filename);

        return filename;
    }

    public static String saveActe(String contenu, String numeroActe) throws IOException {
        String filename = numeroActe + ".txt";
        Path path = Paths.get(BASE_PATH + Constants.DOSSIER_ACTES + filename);

        Files.writeString(path, contenu, StandardOpenOption.CREATE);
        log.info("Acte sauvegardé: {}", filename);

        return filename;
    }

    public static String readActe(String filename) throws IOException {
        Path path = Paths.get(BASE_PATH + Constants.DOSSIER_ACTES + filename);
        return Files.readString(path);
    }

    public static boolean deleteFile(String filename, String dossier) {
        try {
            Path path = Paths.get(BASE_PATH + dossier + filename);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier: {}", e.getMessage());
            return false;
        }
    }

    public static byte[] getFileBytes(String filename, String dossier) throws IOException {
        Path path = Paths.get(BASE_PATH + dossier + filename);
        return Files.readAllBytes(path);
    }

    public static boolean fileExists(String filename, String dossier) {
        Path path = Paths.get(BASE_PATH + dossier + filename);
        return Files.exists(path);
    }

    private static String generateFileName(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(formatter);
        return prefix + "_" + timestamp + extension;
    }

    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot);
        }
        return "";
    }

    public static long getFolderSize(String dossier) throws IOException {
        Path folder = Paths.get(BASE_PATH + dossier);
        return Files.walk(folder)
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
    }

}