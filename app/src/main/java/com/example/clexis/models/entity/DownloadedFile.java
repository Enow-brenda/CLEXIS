package com.example.clexis.models.entity;

import java.time.LocalDateTime;

public class DownloadedFile {
    private String id;
    private String fileName;
    private String filePath;
    private String fileType;
    private String fileSize;
    private LocalDateTime downloadDate;
    private String sourceUrl;
    private String category; // "document", "image", "audio", "video", "other"

    public DownloadedFile() {
        this.downloadDate = LocalDateTime.now();
    }

    public DownloadedFile(String id, String fileName, String filePath, String fileType, String fileSize, LocalDateTime downloadDate) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.downloadDate = downloadDate;
        this.category = determineCategory(fileType);
    }

    private String determineCategory(String fileType) {
        if (fileType == null) return "other";
        
        String type = fileType.toLowerCase();
        if (type.matches("pdf|doc|docx|txt|rtf|odt")) {
            return "document";
        } else if (type.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return "image";
        } else if (type.matches("mp3|wav|m4a|aac|flac")) {
            return "audio";
        } else if (type.matches("mp4|avi|mkv|mov|wmv")) {
            return "video";
        } else {
            return "other";
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { 
        this.fileType = fileType; 
        this.category = determineCategory(fileType);
    }

    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getDownloadDate() { return downloadDate; }
    public void setDownloadDate(LocalDateTime downloadDate) { this.downloadDate = downloadDate; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}