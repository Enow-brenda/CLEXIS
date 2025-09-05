package com.example.clexis.models.entity;

import android.webkit.MimeTypeMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DownloadedFile {
    private String id;
    private String fileName;
    private String filePath;
    private String fileSize;
    private String downloadDate;
    private String sourceUrl;
    private String category; // "document", "image", "audio", "video", "other"
    private String mimeType; // e.g. "image/png"

    public DownloadedFile(String id, String fileName, String filePath, String fileSize, String downloadDate) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.downloadDate = downloadDate;
        this.category = determineCategory(fileName);
        this.mimeType = getMimeType(fileName);
    }

    private String determineCategory(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "other";
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        if (extension.matches("pdf|doc|docx|txt|rtf|odt")) {
            return "document";
        } else if (extension.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return "image";
        } else if (extension.matches("mp3|wav|m4a|aac|flac")) {
            return "audio";
        } else if (extension.matches("mp4|avi|mkv|mov|wmv")) {
            return "video";
        } else {
            return "other";
        }
    }

    private String getMimeType(String fileName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        if (extension != null) {
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
            if (type != null) {
                return type;
            }
        }
        return "application/octet-stream"; // fallback for unknown
    }
}
