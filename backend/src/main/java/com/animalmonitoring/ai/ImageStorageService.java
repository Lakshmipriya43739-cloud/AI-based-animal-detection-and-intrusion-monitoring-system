package com.animalmonitoring.ai;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves detection evidence images to the configured storage directory.
 *
 * <p>Only the <em>relative file path</em> is stored in the database; the
 * actual image bytes are written to the filesystem.  This avoids storing
 * large BLOBs in MySQL while keeping evidence retrievable.
 *
 * <p>The storage directory is configured via {@code ai.image.storage-path}
 * (default: {@code uploads/detections}).  It is created automatically on
 * first use.
 */
@Service
public class ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class);
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private final AiModelConfiguration config;

    public ImageStorageService(AiModelConfiguration config) {
        this.config = config;
    }

    /**
     * Save raw image bytes to the storage directory.
     *
     * @param imageBytes JPEG or PNG encoded image data.
     * @param animalName Used as part of the filename for easy browsing.
     * @return Relative path of the saved file (suitable for storing in the DB),
     *         or {@code null} if saving failed.
     */
    public String saveImageBytes(byte[] imageBytes, String animalName) {
        if (imageBytes == null || imageBytes.length == 0) return null;

        String fileName = buildFileName(animalName, "jpg");
        Path storagePath = resolveStorageDir();
        if (storagePath == null) return null;

        Path dest = storagePath.resolve(fileName);
        try {
            Files.write(dest, imageBytes);
            log.debug("Saved detection image: {}", dest);
            return dest.toString();
        } catch (IOException e) {
            log.error("Failed to save detection image: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Save an OpenCV Mat to the storage directory as JPEG.
     *
     * @param mat        BGR image to save.
     * @param animalName Used as part of the filename.
     * @return Relative path, or {@code null} if saving failed.
     */
    public String saveMat(Mat mat, String animalName) {
        if (mat == null || mat.empty()) return null;

        String fileName = buildFileName(animalName, "jpg");
        Path storagePath = resolveStorageDir();
        if (storagePath == null) return null;

        Path dest = storagePath.resolve(fileName);
        boolean ok = Imgcodecs.imwrite(dest.toString(), mat);
        if (ok) {
            log.debug("Saved detection Mat: {}", dest);
            return dest.toString();
        } else {
            log.warn("Imgcodecs.imwrite failed for: {}", dest);
            return null;
        }
    }

    // ------------------------------------------------------------------ helpers
    private String buildFileName(String animalName, String extension) {
        String safeAnimal = animalName.replaceAll("[^a-zA-Z0-9_]", "_");
        String ts = LocalDateTime.now().format(TIMESTAMP_FMT);
        return safeAnimal + "_" + ts + "." + extension;
    }

    private Path resolveStorageDir() {
        String dirPath = config.getImage().getStoragePath();
        Path dir = Paths.get(dirPath);
        try {
            Files.createDirectories(dir);
            return dir;
        } catch (IOException e) {
            log.error("Cannot create image storage directory '{}': {}", dirPath, e.getMessage());
            return null;
        }
    }

    /** Returns the configured storage directory path. */
    public String getStoragePath() {
        return config.getImage().getStoragePath();
    }
}
