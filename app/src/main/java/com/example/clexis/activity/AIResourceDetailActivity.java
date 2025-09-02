package com.example.clexis.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.models.application.Flashcard;
import com.example.clexis.models.application.Mcq;
import com.example.clexis.models.application.Quiz;
import com.example.clexis.models.entity.LearningAssets;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AIResourceDetailActivity extends AppCompatActivity {

    // Common UI
    private TextView resourceTitle, uploaderName, fileName, fileSize;

    // Flashcard Section
    private CardView flashcardSection ,quizSection,summarySection , transcriptionSection;
    private TextView flashcardCount, flashcardContent;

    private TextView extension;

    private Button prevFlashcardBtn, nextFlashcardBtn, flipCardBtn;
    private int flashcardIndex = 0;
    private boolean isShowingQuestion = true;
    private List<Flashcard> flashcards;

    // Quiz Section
    private LinearLayout questionsSection;
    private TextView quizDetails;
    private Button startQuizBtn;
    private Button endQuizBtn;

    private TextView summaryPreview;
    private Button downloadSummaryBtn;

    private TextView transcribedText;
    private Button downloadTranscriptionBtn;

    private String type;

    private int questionIndex;

    private long startTime;


    private int score;
    private boolean activeQuiz;

    private LearningAssets asset;

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

        initViews();
        setupBackButton();

        // Get data from intent
        type = getIntent().getStringExtra("type"); // "flashcard", "quiz", "summary", "transcribe"
        String json = getIntent().getStringExtra("asset");

        if (json != null) {
            asset = new Gson().fromJson(json, LearningAssets.class);
        } else {
            Toast.makeText(this, "Error loading resource", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateCommonData();
        updateUIByType(type);
    }

    private void initViews() {
        // Header views
        resourceTitle = findViewById(R.id.resource_title); // You'll need to add this ID to your XML

        // Common views
        fileName = findViewById(R.id.originalFileName);
        fileSize = findViewById(R.id.originalFileSize);

        // Flashcard section
        flashcardSection = findViewById(R.id.flashcard_section);
        flashcardCount = findViewById(R.id.tv_flashcard_count);
        flashcardContent = findViewById(R.id.tv_flashcard_content);
        prevFlashcardBtn = findViewById(R.id.btn_prev_flashcard);
        nextFlashcardBtn = findViewById(R.id.btn_next_flashcard);
        flipCardBtn = findViewById(R.id.btn_flip_card);

        questionsSection = findViewById(R.id.questions);

        // Quiz section
        quizSection = findViewById(R.id.quiz_section);

        startQuizBtn = findViewById(R.id.btn_start_quiz);
        endQuizBtn = findViewById(R.id.btn_end_quiz);

        extension = findViewById(R.id.extension);
        // Summary section
        summarySection = findViewById(R.id.summary_section);
        summaryPreview = findViewById(R.id.tv_summary_preview);
        downloadSummaryBtn = findViewById(R.id.btn_download_summary);

        // Transcription section
        transcriptionSection = findViewById(R.id.transcription_section);
        transcribedText = findViewById(R.id.tv_transcribed_text);
    }

    private void setupBackButton() {
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
    }

    private void populateCommonData() {
        if (asset != null) {
            // Update header title (you'll need to add this TextView to your XML)
            if (resourceTitle != null) {
                TextView rType = findViewById(R.id.type);
                rType.setText(type + " Resource");
                resourceTitle.setText(asset.getTitle());
            }
            String filename = asset.getOriginalFilename();
            int dotIndex = filename.lastIndexOf(".");
            String ext = (dotIndex != -1) ? filename.substring(dotIndex + 1) : "";
            extension.setText(ext.toUpperCase());


            fileName.setText(asset.getOriginalFilename() != null ? asset.getOriginalFilename() : "Unknown file");
            fileSize.setVisibility(View.GONE); // Hide file size as it's not in LearningAssets
        }
    }

    private void updateUIByType(String type) {
        hideAllSections();

        if (asset == null) {
            Toast.makeText(this, "No asset data available", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type != null ? type.toLowerCase() : "") {
            case "flashcard":
                setupFlashcardSection();
                break;

            case "quiz":

                setupQuizSection();
                break;

            case "summary":
                setupSummarySection();
                break;

            case "transcribe":
                setupTranscriptionSection();
                break;

            default:
                Toast.makeText(this, "Unknown resource type: " + type, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setupFlashcardSection() {
        flashcardSection.setVisibility(View.VISIBLE);


        flashcards = asset.getFlashcards();

        if (flashcards == null || flashcards.isEmpty()) {
            flashcardContent.setText("No flashcards available for this resource.");
            flashcardCount.setText("0 of 0");
            prevFlashcardBtn.setEnabled(false);
            nextFlashcardBtn.setEnabled(false);
            flipCardBtn.setEnabled(false);
            return;
        }

        flashcardIndex = 0;
        isShowingQuestion = true;
        updateFlashcard();

        prevFlashcardBtn.setOnClickListener(v -> {
            if (flashcardIndex > 0) {
                flashcardIndex--;
                isShowingQuestion = true;
                updateFlashcard();
            }
        });

        nextFlashcardBtn.setOnClickListener(v -> {
            if (flashcardIndex < flashcards.size() - 1) {
                flashcardIndex++;
                isShowingQuestion = true;
                updateFlashcard();
            }
        });

        flipCardBtn.setOnClickListener(v -> {
            isShowingQuestion = !isShowingQuestion;
            updateFlashcard();
        });
    }

    private void setupQuizSection() {
        quizSection.setVisibility(View.VISIBLE);
        questionsSection.setVisibility(View.GONE);

        Quiz quiz = asset.getQuiz();
        TextView best = findViewById(R.id.quizBest);
        TextView bestP = findViewById(R.id.bestPercentage);
        TextView questionsC = findViewById(R.id.questions_count);
        TextView attempt = findViewById(R.id.attempts);

        best.setText(quiz.getDifficulty());
        attempt.setText(String.valueOf(quiz.getAttempts()));
        questionsC.setText(String.valueOf(quiz.getQuestions().size()));


        bestP.setText(quiz.getHighestScore()+"/"+quiz.getQuestions().size());

        if (quiz == null) {
            Toast.makeText(this, "No quiz available for this resource", Toast.LENGTH_SHORT).show();
            return;
        }

        startQuizBtn.setOnClickListener(v -> {
            // Hide quiz overview and show questions
            quizSection.setVisibility(View.GONE);
            questionsSection.setVisibility(View.VISIBLE);
            score = 0;
            activeQuiz = true;
            questionIndex = 0;



            renderQuestion(quiz.getQuestions());


            // TODO: Implement quiz logic here
            // You'll need to populate the questions section with actual quiz data
            Toast.makeText(this, "Starting quiz...", Toast.LENGTH_SHORT).show();
        });
        endQuizBtn.setOnClickListener(v -> {
            activeQuiz = false;

            // TODO: Implement quiz logic here
            // You'll need to populate the questions section with actual quiz data
            Toast.makeText(this, "stopping quiz...", Toast.LENGTH_SHORT).show();
            // Hide quiz overview and show questions
            quizSection.setVisibility(View.VISIBLE);
            questionsSection.setVisibility(View.GONE);


        });
    }

    private void renderQuestion(List<Mcq> questions) {
        if(questionIndex==0){
            startTime= System.currentTimeMillis();
        }
        Button nextButton = findViewById(R.id.btn_next_question);
        nextButton.setVisibility(View.GONE);
        nextButton.setOnClickListener(v->{
            questionIndex++;
            renderQuestion(questions);
        });
        if(questionIndex==(questions.size())){
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            long seconds = (durationMillis / 1000) % 60;
            long minutes = (durationMillis / (1000 * 60)) % 60;
            long hours = (durationMillis / (1000 * 60 * 60)) % 24;
            String timeTaken = "";
            if(hours>0){
                timeTaken = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }else{
                timeTaken = String.format("%02d:%02d", minutes, seconds);
            }



            if(asset.getQuiz().getHighestScore() < score){
                asset.getQuiz().setHighestScore(score);
            }
            int attempts = asset.getQuiz().getAttempts();
            asset.getQuiz().setAttempts(attempts+1);
            showResultDialog(timeTaken,questions);



        }else{
            Mcq question = questions.get(questionIndex);
            TextView questionCount = findViewById(R.id.questions_c);
            questionCount.setText((questionIndex+1)+" of "+questions.size());

            TextView questionValue = findViewById(R.id.question);
            questionValue.setText(question.getQuestion());

            LinearLayout answers = findViewById(R.id.answers);
            String[] alphabets = {"A","B","C","D"};
            List<String> answersValue = new ArrayList<>();
            answersValue.add(question.getOption1());
            answersValue.add(question.getOption2());
            answersValue.add(question.getOption3());
            answersValue.add(question.getOption4());
            int count = 0;
            final boolean[] choosen = {false};


            answers.removeAllViews();
            for(String value: answersValue){
                View answerBox = getLayoutInflater().inflate(R.layout.mcq_questions,answers,false);
                TextView alpha = answerBox.findViewById(R.id.tv_profile_initials);
                TextView questionS = answerBox.findViewById(R.id.answer1);
                 alpha.setText(alphabets[count]);
                 questionS.setText(value);

                 answerBox.setOnClickListener(v -> {
                     //stop the others from been clicked

                     if(!choosen[0]){
                         LinearLayout box = v.findViewById(R.id.box);
                         int selected = answersValue.indexOf(value) + 1;
                         if(question.getCorrectOption() == selected){
                             box.setBackgroundResource(R.drawable.mcq_correct);
                             score++;
                         }else{
                             box.setBackgroundResource(R.drawable.mcq_active);
                         }
                         nextButton.setVisibility(View.VISIBLE);
                         choosen[0] = true;
                     }


                 });

                 answers.addView(answerBox);

                 count++;

            }

        }

    }

    private void showResultDialog(String timeTaken, List<Mcq> questions) {
        int totalQuestions = questions.size();
        int correct = score;
        int incorrect = totalQuestions - score;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_score ,null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Bind views
        TextView scoreTitle = dialogView.findViewById(R.id.scoreTitle);
        TextView congratsMessage = dialogView.findViewById(R.id.congratsMessage);
        TextView scoreValue = dialogView.findViewById(R.id.scoreValue);
        TextView percentageScore = dialogView.findViewById(R.id.percentageScore);
        TextView correctAnswers = dialogView.findViewById(R.id.correctAnswers);
        TextView incorrectAnswers = dialogView.findViewById(R.id.incorrectAnswers);
        TextView timeTakenView = dialogView.findViewById(R.id.timeTaken);

        MaterialButton retakeBtn = dialogView.findViewById(R.id.retakeBtn);
        MaterialButton closeBtn = dialogView.findViewById(R.id.closeBtn);

        // Set data
        scoreValue.setText(score + "/" + totalQuestions);
        int percent = (score * 100) / totalQuestions;
        percentageScore.setText(percent + "% Correct");
        correctAnswers.setText(String.valueOf(correct));
        incorrectAnswers.setText(String.valueOf(incorrect));
        timeTakenView.setText(timeTaken);

        questionIndex = 0;
        score = 0;

        // Actions
        retakeBtn.setOnClickListener(v -> {
            dialog.dismiss();
            // Restart quiz logic here
            renderQuestion(questions);
        });

        closeBtn.setOnClickListener(v -> {
            setupQuizSection();
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }



    private void setupSummarySection() {
        summarySection.setVisibility(View.VISIBLE);

        String summary = asset.getSummary();

        if (summary != null && !summary.trim().isEmpty()) {
            summaryPreview.setText(summary);
        } else {
            summaryPreview.setText("No summary available for this resource.");
        }

        downloadSummaryBtn.setOnClickListener(v -> {
            // TODO: Implement download functionality
            Toast.makeText(this, "Downloading summary...", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupTranscriptionSection() {
        transcriptionSection.setVisibility(View.VISIBLE);

        String transcription = asset.getTranscription();

        if (transcription != null && !transcription.trim().isEmpty()) {
            transcribedText.setText(transcription);
        } else {
            transcribedText.setText("No transcription available for this resource.");
        }

        // Note: No download button click listener needed as it's not in the XML
        // If you want to add download functionality, add the button and listener
    }

    private void hideAllSections() {
        flashcardSection.setVisibility(View.GONE);
        quizSection.setVisibility(View.GONE);
        questionsSection.setVisibility(View.GONE);
        summarySection.setVisibility(View.GONE);
        transcriptionSection.setVisibility(View.GONE);
    }

    private void updateFlashcard() {
        if (flashcards == null || flashcards.isEmpty() || flashcardIndex >= flashcards.size()) {
            return;
        }

        Flashcard currentFlashcard = flashcards.get(flashcardIndex);

        if (isShowingQuestion) {
            flashcardContent.setText(currentFlashcard.getQuestion());
            flipCardBtn.setText("Show Answer");
        } else {
            flashcardContent.setText(currentFlashcard.getAnswer());
            flipCardBtn.setText("Show Question");
        }

        flashcardCount.setText("Flashcard " + (flashcardIndex + 1) + " of " + flashcards.size());

        // Update button states
        prevFlashcardBtn.setEnabled(flashcardIndex > 0);
        nextFlashcardBtn.setEnabled(flashcardIndex < flashcards.size() - 1);
    }

    @Override
    public void onBackPressed() {
        // If we're in quiz questions view, go back to quiz overview
//        if (questionsSection.getVisibility() == View.VISIBLE) {
//            questionsSection.setVisibility(View.GONE);
//            quizSection.setVisibility(View.VISIBLE);}


        if(activeQuiz){
            Toast.makeText(this, "You cannot go back during the quiz! Just Give up ", Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }
}