package com.example.clexis.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;

public class AIResourceDetailActivity extends AppCompatActivity {

    // Common UI
    private TextView resourceTitle, uploaderName, fileName, fileSize;

    // Flashcard Section
    private LinearLayout flashcardSection;
    private TextView flashcardCount, flashcardContent;
    private Button prevFlashcardBtn, nextFlashcardBtn;
    private int flashcardIndex = 0;
    private String[] flashcards = {
            "What is AI?",
            "Explain supervised learning.",
            "Define neural networks."
    };

    // Quiz Section
    private LinearLayout quizSection;
    private TextView quizDetails;
    private Button startQuizBtn;

    // Summary Section
    private LinearLayout summarySection;
    private TextView summaryPreview;
    private Button downloadSummaryBtn;

    // Transcription Section
    private LinearLayout transcriptionSection;
    private TextView transcribedText;
    private Button downloadTranscriptionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_resource);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }

//        initViews();

        // Simulate data from Intent or API
//        String type = getIntent().getStringExtra("type"); // "flashcard", "quiz", "summary", "transcribe"
//        String title = getIntent().getStringExtra("title");
//        String uploader = getIntent().getStringExtra("uploader");
//
//        resourceTitle.setText(title != null ? title : "Resource Title");
//        uploaderName.setText("Uploaded by: @" + (uploader != null ? uploader : "unknown"));
//        fileName.setText("document.pdf");
//        fileSize.setText("2.3 MB");
//
//        updateUIByType(type);
    }

//    private void initViews() {
//        resourceTitle = findViewById(R.id.resourceTitle);
//        uploaderName = findViewById(R.id.uploaderName);
//        fileName = findViewById(R.id.originalFileName);
//        fileSize = findViewById(R.id.originalFileSize);
//
//        flashcardSection = findViewById(R.id.flashcard_section);
//        flashcardCount = findViewById(R.id.tv_flashcard_count);
//        flashcardContent = findViewById(R.id.tv_flashcard_content);
//        prevFlashcardBtn = findViewById(R.id.btn_prev_flashcard);
//        nextFlashcardBtn = findViewById(R.id.btn_next_flashcard);
//
//        quizSection = findViewById(R.id.quiz_section);
//        quizDetails = findViewById(R.id.tv_quiz_details);
//        startQuizBtn = findViewById(R.id.btn_start_quiz);
//
//        summarySection = findViewById(R.id.summary_section);
//        summaryPreview = findViewById(R.id.tv_summary_preview);
//        downloadSummaryBtn = findViewById(R.id.btn_download_summary);
//
//        transcriptionSection = findViewById(R.id.transcription_section);
//        transcribedText = findViewById(R.id.tv_transcribed_text);
//        downloadTranscriptionBtn = findViewById(R.id.btn_download_transcription);
//    }

    private void updateUIByType(String type) {
        hideAllSections();

        switch (type != null ? type.toLowerCase() : "") {
            case "flashcard":
                flashcardSection.setVisibility(View.VISIBLE);
                updateFlashcard();

                prevFlashcardBtn.setOnClickListener(v -> {
                    if (flashcardIndex > 0) {
                        flashcardIndex--;
                        updateFlashcard();
                    }
                });

                nextFlashcardBtn.setOnClickListener(v -> {
                    if (flashcardIndex < flashcards.length - 1) {
                        flashcardIndex++;
                        updateFlashcard();
                    }
                });
                break;

            case "quiz":
                quizSection.setVisibility(View.VISIBLE);
                quizDetails.setText("Questions: 10 | Attempts: 3 | Highest Score: 8");

                startQuizBtn.setOnClickListener(v -> {
                    Toast.makeText(this, "Starting quiz...", Toast.LENGTH_SHORT).show();
                });
                break;

            case "summary":
                summarySection.setVisibility(View.VISIBLE);
                summaryPreview.setText("This is a brief preview of the generated summary...");

                downloadSummaryBtn.setOnClickListener(v -> {
                    Toast.makeText(this, "Downloading summary...", Toast.LENGTH_SHORT).show();
                });
                break;

            case "transcribe":
                transcriptionSection.setVisibility(View.VISIBLE);
                transcribedText.setText("This is the full transcription of the uploaded audio...");

                downloadTranscriptionBtn.setOnClickListener(v -> {
                    Toast.makeText(this, "Downloading transcript...", Toast.LENGTH_SHORT).show();
                });
                break;

            default:
                Toast.makeText(this, "Unknown resource type", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void hideAllSections() {
        flashcardSection.setVisibility(View.GONE);
        quizSection.setVisibility(View.GONE);
        summarySection.setVisibility(View.GONE);
        transcriptionSection.setVisibility(View.GONE);
    }

    private void updateFlashcard() {
        flashcardContent.setText(flashcards[flashcardIndex]);
        flashcardCount.setText("Flashcard " + (flashcardIndex + 1) + " of " + flashcards.length);
    }
}



