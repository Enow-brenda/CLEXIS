package com.example.clexis.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.clexis.R;
import com.example.clexis.models.entity.DownloadedFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private List<DownloadedFile> allDownloads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_downloads_activity);
        allDownloads = getDownloads();

        setupStatusBar();
        setupActivity();
    }

    private void setupActivity() {


        LinearLayout container = findViewById(R.id.container);

        if (allDownloads == null || allDownloads.isEmpty()) {
            View empty = getLayoutInflater().inflate(R.layout.empty_state, container, false);
            TextView heading = empty.findViewById(R.id.heading);
            TextView desc = empty.findViewById(R.id.description);
            heading.setText("No Downloads Yet");
            desc.setText("You haven’t downloaded anything. Your downloaded files will appear here for quick access.");
            container.addView(empty);
        } else {
            container.removeAllViews();
            for (DownloadedFile file : allDownloads) {
                View downloadBox = getLayoutInflater().inflate(R.layout.doenload_item_layout, container, false);

                // Bind data
                TextView fileName = downloadBox.findViewById(R.id.file_name);
                TextView fileSize = downloadBox.findViewById(R.id.file_size);
                TextView fileDate = downloadBox.findViewById(R.id.download_date);
                TextView fileIcon = downloadBox.findViewById(R.id.file_icon);
                ImageButton menuBtn = downloadBox.findViewById(R.id.btn_file_menu);

                fileName.setText(file.getFileName());
                fileSize.setText(file.getFileSize());
                fileDate.setText(file.getDownloadDate());
                String[] parts = file.getFileName().split("\\.");
                String type = parts[parts.length - 1];
                fileIcon.setText(type.toUpperCase());

                // Handle 3-dot menu
                menuBtn.setOnClickListener(v -> showFileMenu(v, file, downloadBox, container));

                container.addView(downloadBox);
            }
        }
    }

    private void showFileMenu(View anchor, DownloadedFile file, View fileView, LinearLayout container) {
        PopupMenu popup = new PopupMenu(this, anchor);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.download_item_menu, popup.getMenu()); // <-- we’ll create this menu

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_view) {
                viewFile(file);
                return true;
            } else if (itemId == R.id.action_share) {
                shareFile(file);
                return true;
            } else if (itemId == R.id.action_remove) {
                showRemoveConfirmation(file, fileView, container);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showRemoveConfirmation(DownloadedFile file, View fileView, LinearLayout container) {
        new AlertDialog.Builder(this)
                .setTitle("Remove File")
                .setMessage("Are you sure you want to remove this file?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    container.removeView(fileView);
                    allDownloads.remove(file);
                    setupActivity();
                    // Optional: Delete the actual file from storage
                    deleteFileFromStorage(file);
//                    showToast("File removed successfully");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteFileFromStorage(DownloadedFile file) {
        File physicalFile = new File(file.getFilePath());
        if (physicalFile.exists()) {
            physicalFile.delete();
        }
    }

    private void viewFile(DownloadedFile file) {
        try {
            File fileToView = new File(file.getFilePath());

            if (!fileToView.exists()) {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get MIME type of the file
            String mimeType = getMimeType(file.getFilePath());

            if (mimeType == null) {
                mimeType = "*/*"; // Fallback to generic type
            }

            // Create intent to view the file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    fileToView);

            intent.setDataAndType(fileUri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verify there's an app to handle this intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error opening file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Helper method to get MIME type
    private String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "pdf": return "application/pdf";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "txt": return "text/plain";
            case "mp4": return "video/mp4";
            case "mp3": return "audio/mpeg";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt": return "application/vnd.ms-powerpoint";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "zip": return "application/zip";
            case "rar": return "application/x-rar-compressed";
            default: return null;
        }
    }
    private void shareFile(DownloadedFile file) {
        try {
            File fileToShare = new File(file.getFilePath());

            if (!fileToShare.exists()) {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get MIME type for sharing
            String mimeType = getMimeType(file.getFilePath());
            if (mimeType == null) {
                mimeType = "*/*"; // Generic type
            }

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri fileUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    fileToShare);

            shareIntent.setType(mimeType);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Optional: Add text description
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing file: " + file.getFileName());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing " + file.getFileName());

            // Start share activity
            startActivity(Intent.createChooser(shareIntent, "Share via"));

        } catch (Exception e) {
            Toast.makeText(this, "Error sharing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private List<DownloadedFile> getDownloads() {
        // TODO: Load actual downloads from DB or storage
        List<DownloadedFile> files = new ArrayList<>();
        files.add(new DownloadedFile(
                "1",
                "Machine Learning Notes.pdf",
                "/storage/emulated/0/Download/ml.pdf",
                "2.3 MB",
                "2 hours ago"
        ));

        files.add(new DownloadedFile(
                "2",
                "Resume.docx",
                "/storage/emulated/0/Download/resume.docx",
                "1.1 MB",
                "Yesterday"
        ));

        return files;

    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
    }
}
