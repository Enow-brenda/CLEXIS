package com.example.clexis;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.clexis.models.entity.DownloadedFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DownloadHelper {
    private static final String TAG = "DownloadHelper";
    private Context context;
    private DownloadManager downloadManager;
    private DownloadCallback callback;

    public interface DownloadCallback {
        void onDownloadComplete(DownloadedFile downloadedFile);
        void onDownloadFailed(String errorMessage);
        void onDownloadProgress(int progress, long downloadedBytes, long totalBytes);
    }

    public DownloadHelper(Context context) {
        this.context = context;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /**
     * Download file using Android's DownloadManager (Recommended)
     */
    public long downloadFile(String fileUrl, String fileName, String category, DownloadCallback callback) {
        this.callback = callback;

        try {
            // Generate unique ID for the download
            String downloadId = UUID.randomUUID().toString();

            // Create downloads directory if it doesn't exist
            File downloadsDir = getDownloadsDirectory(category);
            if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                throw new Exception("Failed to create downloads directory");
            }

            // Create DownloadManager request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));

            // Set destination
            request.setDestinationUri(Uri.fromFile(new File(downloadsDir, fileName)));

            // Set notification settings
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(fileName);
            request.setDescription("Downloading file...");

            // Set network constraints
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);

            // Set MIME type if available
            String mimeType = getMimeTypeFromFileName(fileName);
            if (mimeType != null) {
                request.setMimeType(mimeType);
            }

            // Enqueue download
            long systemDownloadId = downloadManager.enqueue(request);

            // Store download info for tracking
            storeDownloadInfo(systemDownloadId, downloadId, fileName, fileUrl, category);

            // Register receiver to track download completion
            registerDownloadReceiver(systemDownloadId, downloadId);

            return systemDownloadId;

        } catch (Exception e) {
            Log.e(TAG, "Download failed to start", e);
            if (callback != null) {
                callback.onDownloadFailed("Download failed to start: " + e.getMessage());
            }
            return -1;
        }
    }

    /**
     * Manual HTTP download for more control and progress tracking
     */
    public void downloadFileManual(String fileUrl, String fileName, String category, DownloadCallback callback) {
        this.callback = callback;

        new Thread(() -> {
            String downloadId = UUID.randomUUID().toString();
            File outputFile = null;

            try {
                java.net.URL url = new java.net.URL(fileUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();

                if (connection.getResponseCode() != java.net.HttpURLConnection.HTTP_OK) {
                    throw new Exception("Server returned error: " + connection.getResponseCode());
                }

                long fileLength = connection.getContentLengthLong();

                // Create category directory
                File downloadsDir = getDownloadsDirectory(category);
                if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                    throw new Exception("Failed to create downloads directory");
                }

                outputFile = new File(downloadsDir, fileName);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile);
                java.io.InputStream input = connection.getInputStream();

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalDownloaded = 0;

                while ((bytesRead = input.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalDownloaded += bytesRead;

                    // Update progress
                    if (callback != null) {
                        final int progress = fileLength > 0 ? (int) (totalDownloaded * 100 / fileLength) : 0;
                        callback.onDownloadProgress(progress, totalDownloaded, fileLength);
                    }
                }

                fos.close();
                input.close();

                // Create DownloadedFile object
                DownloadedFile downloadedFile = createDownloadedFile(
                        downloadId, outputFile, fileName, fileUrl, category
                );

                if (callback != null) {
                    callback.onDownloadComplete(downloadedFile);
                }

            } catch (Exception e) {
                Log.e(TAG, "Manual download failed", e);

                // Clean up partially downloaded file
                if (outputFile != null && outputFile.exists()) {
                    outputFile.delete();
                }

                if (callback != null) {
                    callback.onDownloadFailed("Download failed: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Create DownloadedFile object from downloaded file
     */
    private DownloadedFile createDownloadedFile(String id, File file, String fileName,
                                                String sourceUrl, String category) {
        String fileSize = formatFileSize(file.length());
        String downloadDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date(file.lastModified()));
        String mimeType = getMimeTypeFromFileName(fileName);

        return new DownloadedFile(
                id,
                fileName,
                file.getAbsolutePath(),
                fileSize,
                downloadDate,
                sourceUrl,
                category,
                mimeType
        );
    }

    /**
     * Get MIME type from file name
     */
    private String getMimeTypeFromFileName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "application/octet-stream";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "pdf": return "application/pdf";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "txt": return "text/plain";
            case "mp4": return "video/mp4";
            case "mp3": return "audio/mpeg";
            case "wav": return "audio/wav";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt": return "application/vnd.ms-powerpoint";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "zip": return "application/zip";
            case "rar": return "application/x-rar-compressed";
            case "apk": return "application/vnd.android.package-archive";
            default: return "application/octet-stream";
        }
    }

    /**
     * Format file size to human readable string
     */
    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format(Locale.getDefault(), "%.1f %s",
                size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * Get downloads directory for specific category
     */
    private File getDownloadsDirectory(String category) {
        File baseDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "MyAppDownloads");

        if (category != null && !category.equals("other")) {
            return new File(baseDir, category);
        }
        return baseDir;
    }

    /**
     * Store download info for tracking
     */
    private void storeDownloadInfo(long systemDownloadId, String downloadId,
                                   String fileName, String fileUrl, String category) {
        // Implement your storage logic here (SharedPreferences, Room, etc.)
        Log.d(TAG, "Storing download info: " + downloadId);
    }

    private void registerDownloadReceiver(long systemDownloadId, String downloadId) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedId == systemDownloadId) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(systemDownloadId);

                    try (Cursor cursor = downloadManager.query(query)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            int localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            int sizeIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                            int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                            if (statusIndex == -1) {
                                if (callback != null) {
                                    callback.onDownloadFailed("Unable to get download status");
                                }
                                return;
                            }

                            int status = cursor.getInt(statusIndex);

                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                if (localUriIndex == -1) {
                                    if (callback != null) {
                                        callback.onDownloadFailed("Unable to get file location");
                                    }
                                    return;
                                }

                                String localUri = cursor.getString(localUriIndex);
                                long size = sizeIndex != -1 ? cursor.getLong(sizeIndex) : 0;

                                if (localUri != null) {
                                    File file = new File(Uri.parse(localUri).getPath());

                                    if (file.exists()) {
                                        // Extract category from path or use stored info
                                        String category = extractCategoryFromPath(file.getParent());
                                        String fileName = file.getName();

                                        DownloadedFile downloadedFile = createDownloadedFile(
                                                downloadId, file, fileName,
                                                getSourceUrlFromStoredInfo(systemDownloadId),
                                                category
                                        );

                                        if (callback != null) {
                                            callback.onDownloadComplete(downloadedFile);
                                        }
                                    } else {
                                        if (callback != null) {
                                            callback.onDownloadFailed("Downloaded file not found");
                                        }
                                    }
                                }

                            } else if (status == DownloadManager.STATUS_FAILED) {
                                String errorMessage = "Download failed";
                                if (reasonIndex != -1) {
                                    int reason = cursor.getInt(reasonIndex);
                                    errorMessage += ": " + getErrorReason(reason);
                                }

                                if (callback != null) {
                                    callback.onDownloadFailed(errorMessage);
                                }
                            }
                        } else {
                            if (callback != null) {
                                callback.onDownloadFailed("Download information not available");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing download completion", e);
                        if (callback != null) {
                            callback.onDownloadFailed("Error processing download: " + e.getMessage());
                        }
                    } finally {
                        context.unregisterReceiver(this);
                    }
                }
            }
        };

//        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        // Register receiver with proper flags for Android 14+

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            ContextCompat.registerReceiver(context, receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED);
        }


    }

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != -1) {
                // Handle completed download
                Toast.makeText(context, "Download completed: " + id, Toast.LENGTH_SHORT).show();
            }
        }
    };


    private String getErrorReason(int reason) {
        switch (reason) {
            case DownloadManager.ERROR_CANNOT_RESUME: return "Cannot resume";
            case DownloadManager.ERROR_DEVICE_NOT_FOUND: return "Device not found";
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS: return "File already exists";
            case DownloadManager.ERROR_FILE_ERROR: return "File error";
            case DownloadManager.ERROR_HTTP_DATA_ERROR: return "HTTP data error";
            case DownloadManager.ERROR_INSUFFICIENT_SPACE: return "Insufficient space";
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS: return "Too many redirects";
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE: return "Unhandled HTTP code";
            default: return "Unknown error (" + reason + ")";
        }
    }

    private String extractCategoryFromPath(String path) {
        if (path == null) return "other";
        if (path.contains("/document/")) return "document";
        if (path.contains("/image/")) return "image";
        if (path.contains("/audio/")) return "audio";
        if (path.contains("/video/")) return "video";
        return "other";
    }

    private String getSourceUrlFromStoredInfo(long systemDownloadId) {
        // Retrieve from your storage (SharedPreferences, database, etc.)
        return "unknown";
    }

    /**
     * Get all downloaded files from all categories
     */
    public java.util.List<DownloadedFile> getAllDownloadedFiles() {
        java.util.List<DownloadedFile> downloadedFiles = new java.util.ArrayList<>();
        File baseDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "MyAppDownloads");

        if (baseDir.exists() && baseDir.isDirectory()) {
            // Scan all category directories
            String[] categories = {"document", "image", "audio", "video", "other"};

            for (String category : categories) {
                File categoryDir = new File(baseDir, category);
                if (categoryDir.exists() && categoryDir.isDirectory()) {
                    addFilesFromDirectory(downloadedFiles, categoryDir, category);
                }
            }

            // Also check base directory for files without category
            addFilesFromDirectory(downloadedFiles, baseDir, "other");
        }

        return downloadedFiles;
    }

    private void addFilesFromDirectory(java.util.List<DownloadedFile> files, File directory, String category) {
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isFile()) {
                    String id = UUID.nameUUIDFromBytes(file.getAbsolutePath().getBytes()).toString();
                    DownloadedFile downloadedFile = createDownloadedFile(
                            id, file, file.getName(), "unknown", category
                    );
                    files.add(downloadedFile);
                }
            }
        }
    }

    /**
     * Cancel a download
     */
    public boolean cancelDownload(long downloadId) {
        try {
            int result = downloadManager.remove(downloadId);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error canceling download", e);
            return false;
        }
    }

    /**
     * Check if a file already exists to avoid duplicates
     */
    public boolean fileExists(String fileName, String category) {
        File downloadsDir = getDownloadsDirectory(category);
        File file = new File(downloadsDir, fileName);
        return file.exists();
    }
}