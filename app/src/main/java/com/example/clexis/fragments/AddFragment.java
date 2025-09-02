package com.example.clexis.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.AIResourceDetailActivity;
import com.example.clexis.activity.ResourceDescriptionActivity;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.entity.LearningAssets;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFragment extends Fragment {

    // UI Components
    private MaterialButton uploadButton;
    private TextView currentDialogFileText;
    private CardView flashcardsCard, quizzesCard, summariesCard, transcriptionsCard;
    private TextView flashcardsCount, flashcardsLastUsed;
    private TextView quizzesCount, quizzesLastUsed;
    private TextView summariesCount, summariesLastUsed;
    private TextView transcriptionsCount, transcriptionsLastUsed;
    private LinearLayout learningAssetsContainer;

    // Data
    private List<LearningAssets> assetsList;
    private List<LearningAssets> filteredAssetsList;
    private ApiService api;
    private String selectedFileUri;
    private String currentFilter = "ALL"; // ALL, FLASHCARD, QUIZ, SUMMARY, TRANSCRIPTION

    // File picker launcher
    private ActivityResultLauncher<Intent> filePickerLauncher;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        api = ApiClient.getRetrofitInstance(this.getContext()).create(ApiService.class);

        initializeViews(view);
        setupFilePickerLauncher();
        setupClickListeners();
        setupActivity();

        return view;
    }

    private void initializeViews(View view) {
        // Upload button
        uploadButton = view.findViewById(R.id.loginBtn);

        // Card views for study tools
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        if (gridLayout != null) {
            flashcardsCard = (CardView) gridLayout.getChildAt(0);
            quizzesCard = (CardView) gridLayout.getChildAt(1);
            summariesCard = (CardView) gridLayout.getChildAt(2);
            transcriptionsCard = (CardView) gridLayout.getChildAt(3);

            // Get text views from each card
            setupCardTextViews();
        }

        learningAssetsContainer = view.findViewById(R.id.lAssets);
    }

    private void setupCardTextViews() {
        // Flashcards card text views
        LinearLayout flashcardsLayout = (LinearLayout) flashcardsCard.getChildAt(0);
        flashcardsCount = (TextView) flashcardsLayout.getChildAt(2);
        flashcardsLastUsed = (TextView) flashcardsLayout.getChildAt(3);

        // Quizzes card text views
        LinearLayout quizzesLayout = (LinearLayout) quizzesCard.getChildAt(0);
        quizzesCount = (TextView) quizzesLayout.getChildAt(2);
        quizzesLastUsed = (TextView) quizzesLayout.getChildAt(3);

        // Summaries card text views
        LinearLayout summariesLayout = (LinearLayout) summariesCard.getChildAt(0);
        summariesCount = (TextView) summariesLayout.getChildAt(2);
        summariesLastUsed = (TextView) summariesLayout.getChildAt(3);

        // Transcriptions card text views
        LinearLayout transcriptionsLayout = (LinearLayout) transcriptionsCard.getChildAt(0);
        transcriptionsCount = (TextView) transcriptionsLayout.getChildAt(2);
        transcriptionsLastUsed = (TextView) transcriptionsLayout.getChildAt(3);
    }

    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedFileUri = result.getData().getData();
                        if (selectedFileUri != null) {
                            this.selectedFileUri = selectedFileUri.toString();
                            String fileName = selectedFileUri.getLastPathSegment();
                            if (fileName == null) {
                                fileName = "Selected file";
                            }

                            // Update the dialog's file display
                            if (currentDialogFileText != null) {
                                currentDialogFileText.setText(fileName);
                            }

                            Toast.makeText(getContext(), "File selected: " + fileName,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupClickListeners() {
        // Upload button click listener
        uploadButton.setOnClickListener(v -> showUploadDialog());

        // Card click listeners for filtering
        flashcardsCard.setOnClickListener(v -> filterAssets("FLASHCARD"));
        quizzesCard.setOnClickListener(v -> filterAssets("QUIZ"));
        summariesCard.setOnClickListener(v -> filterAssets("SUMMARY"));
        transcriptionsCard.setOnClickListener(v -> filterAssets("TRANSCRIPTION"));
    }

    private void showUploadDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.upload_dialog_layout, null);

        // Initialize dialog views
        MaterialButton selectFileBtn = dialogView.findViewById(R.id.selectFileBtn);
        TextView selectedFileText = dialogView.findViewById(R.id.selectedFileText);
        AutoCompleteTextView learningTypeSpinner = dialogView.findViewById(R.id.learningTypeSpinner);
        TextInputEditText questionsCountEdit = dialogView.findViewById(R.id.questionsCountEdit);
        SeekBar difficultySeekBar = dialogView.findViewById(R.id.difficultySeekBar);
        TextView difficultyText = dialogView.findViewById(R.id.difficultyText);
        MaterialButton submitBtn = dialogView.findViewById(R.id.submitBtn);
        MaterialButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        LinearLayout dynamicOptionsContainer = dialogView.findViewById(R.id.dynamicOptionsContainer);

        // Set reference for file picker callback
        currentDialogFileText = selectedFileText;

        // Clear previous file selection
        selectedFileUri = null;

        // Setup dropdown adapter
        String[] learningTypes = {"Flashcards", "Quiz", "Summary", "Transcription"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, learningTypes);
        learningTypeSpinner.setAdapter(adapter);

        // Set default selection and show it
        learningTypeSpinner.setText(learningTypes[0], false);
        learningTypeSpinner.dismissDropDown(); // Ensure dropdown is dismissed

        // File picker button
        selectFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(Intent.createChooser(intent, "Select file"));
        });

        // Learning type selection listener - use both click and text change
        learningTypeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = learningTypes[position];
            showDynamicOptions(dialogView, selectedType);
        });

        // Also handle text changes in case user types
        learningTypeSpinner.setOnClickListener(v -> {
            learningTypeSpinner.showDropDown();
        });

        // Difficulty seekbar
        difficultySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String[] difficulties = {"Easy", "Medium", "Hard", "Expert"};
                difficultyText.setText(difficulties[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Submit button
        submitBtn.setOnClickListener(v -> {
            if (selectedFileUri == null || selectedFileUri.isEmpty()) {
                Toast.makeText(getContext(), "Please select a file first", Toast.LENGTH_SHORT).show();
                return;
            }

            String learningType = learningTypeSpinner.getText().toString();
            if (learningType.isEmpty()) {
                Toast.makeText(getContext(), "Please select a learning type", Toast.LENGTH_SHORT).show();
                return;
            }

            String questionsCount = questionsCountEdit.getText() != null ?
                    questionsCountEdit.getText().toString() : "";
            int difficulty = difficultySeekBar.getProgress();

            // Submit to endpoint
            submitLearningMaterial(selectedFileUri, learningType, questionsCount, difficulty);
            bottomSheetDialog.dismiss();
        });

        // Cancel button
        cancelBtn.setOnClickListener(v -> {
            currentDialogFileText = null; // Clear reference
            bottomSheetDialog.dismiss();
        });

        // Initialize file display
        selectedFileText.setText("No file selected");

        // Show default options for first learning type
        showDynamicOptions(dialogView, learningTypes[0]);

        // Clear reference when dialog is dismissed
        bottomSheetDialog.setOnDismissListener(dialog -> {
            currentDialogFileText = null;
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void showDynamicOptions(View dialogView, String selectedType) {
        TextView questionsLabel = dialogView.findViewById(R.id.questionsLabel);
        View questionsInputLayout = dialogView.findViewById(R.id.questionsInputLayout);
        TextView difficultyLabel = dialogView.findViewById(R.id.difficultyLabel);
        TextView difficultyText = dialogView.findViewById(R.id.difficultyText);
        SeekBar difficultySeekBar = dialogView.findViewById(R.id.difficultySeekBar);
        View difficultyLabelsContainer = dialogView.findViewById(R.id.difficultyLabelsContainer);

        // Hide all dynamic options first
        questionsLabel.setVisibility(View.GONE);
        questionsInputLayout.setVisibility(View.GONE);
        difficultyLabel.setVisibility(View.GONE);
        difficultyText.setVisibility(View.GONE);
        difficultySeekBar.setVisibility(View.GONE);
        difficultyLabelsContainer.setVisibility(View.GONE);

        // Show relevant options based on selected type
        switch (selectedType) {
            case "Flashcards":
                questionsLabel.setText("Number of Flashcards");
                questionsLabel.setVisibility(View.VISIBLE);
                questionsInputLayout.setVisibility(View.VISIBLE);
                break;
            case "Quiz":
                questionsLabel.setText("Number of Questions");
                questionsLabel.setVisibility(View.VISIBLE);
                questionsInputLayout.setVisibility(View.VISIBLE);
                difficultyLabel.setVisibility(View.VISIBLE);
                difficultyText.setVisibility(View.VISIBLE);
                difficultySeekBar.setVisibility(View.VISIBLE);
                difficultyLabelsContainer.setVisibility(View.VISIBLE);
                break;
            case "Summary":
                // No additional options for summary
                break;
            case "Transcription":
                // No additional options for transcription
                break;
        }
    }

    private void submitLearningMaterial(String fileUri, String type, String count, int difficulty) {
        // TODO: Implement API call to submit learning material
        // This is where you'd make the retrofit call to your endpoint

        Toast.makeText(getContext(), "Submitting: " + type + " with " + count + " items",
                Toast.LENGTH_SHORT).show();

        // After successful submission, refresh the learning assets
        getLearningAssets();
    }

    private void filterAssets(String filterType) {
        // Update card backgrounds
        resetCardBackgrounds();
        setActiveCardBackground(filterType);

        currentFilter = filterType;

        // Filter the assets list
        filteredAssetsList = new ArrayList<>();
        if (assetsList != null) {
            for (LearningAssets asset : assetsList) {
                if (asset.getType() != null && asset.getType().name().equalsIgnoreCase(filterType)) {
                    filteredAssetsList.add(asset);
                }
            }
        }

        displayLearningAssets();
    }

    private void resetCardBackgrounds() {
        flashcardsCard.setBackgroundDrawable(null);
        quizzesCard.setBackgroundDrawable(null);
        summariesCard.setBackgroundDrawable(null);
        transcriptionsCard.setBackgroundDrawable(null);
    }

    private void setActiveCardBackground(String filterType) {
        switch (filterType) {
            case "FLASHCARD":
                flashcardsCard.setBackground(getResources().getDrawable(R.drawable.mcq_active));
                break;
            case "QUIZ":
                quizzesCard.setBackground(getResources().getDrawable(R.drawable.mcq_active));
                break;
            case "SUMMARY":
                summariesCard.setBackground(getResources().getDrawable(R.drawable.mcq_active));
                break;
            case "TRANSCRIPTION":
                transcriptionsCard.setBackground(getResources().getDrawable(R.drawable.mcq_active));
                break;
        }
    }

    public void setupActivity() {
        getLearningAssets();
        updateCardStatistics();
    }

    private void updateCardStatistics() {
        if (assetsList == null || assetsList.isEmpty()) {
            // Set all counts to 0 and "Never used"
            flashcardsCount.setText("0 resource sets");
            flashcardsLastUsed.setText("Never used");

            quizzesCount.setText("0 resource sets");
            quizzesLastUsed.setText("Never used");

            summariesCount.setText("0 resource sets");
            summariesLastUsed.setText("Never used");

            transcriptionsCount.setText("0 resource sets");
            transcriptionsLastUsed.setText("Never used");
        } else {
            // Count assets by type and find last used dates
            int flashcardsCount = 0, quizzesCount = 0, summariesCount = 0, transcriptionsCount = 0;
            LocalDate flashcardsLastDate = null, quizzesLastDate = null,
                    summariesLastDate = null, transcriptionsLastDate = null;

            for (LearningAssets asset : assetsList) {
                if (asset.getType() == null) continue;

                switch (asset.getType().name().toUpperCase()) {
                    case "FLASHCARD":
                        flashcardsCount++;
                        if (asset.getCreatedDate() != null &&
                                (flashcardsLastDate == null || asset.getCreatedDate().isAfter(flashcardsLastDate))) {
                            flashcardsLastDate = asset.getCreatedDate();
                        }
                        break;
                    case "QUIZ":
                        quizzesCount++;
                        if (asset.getCreatedDate() != null &&
                                (quizzesLastDate == null || asset.getCreatedDate().isAfter(quizzesLastDate))) {
                            quizzesLastDate = asset.getCreatedDate();
                        }
                        break;
                    case "SUMMARY":
                        summariesCount++;
                        if (asset.getCreatedDate() != null &&
                                (summariesLastDate == null || asset.getCreatedDate().isAfter(summariesLastDate))) {
                            summariesLastDate = asset.getCreatedDate();
                        }
                        break;
                    case "TRANSCRIPTION":
                        transcriptionsCount++;
                        if (asset.getCreatedDate() != null &&
                                (transcriptionsLastDate == null || asset.getCreatedDate().isAfter(transcriptionsLastDate))) {
                            transcriptionsLastDate = asset.getCreatedDate();
                        }
                        break;
                }
            }

            // Update UI
            this.flashcardsCount.setText(flashcardsCount + " resource sets");
            this.flashcardsLastUsed.setText(formatLastUsedDate(flashcardsLastDate));

            this.quizzesCount.setText(quizzesCount + " resource sets");
            this.quizzesLastUsed.setText(formatLastUsedDate(quizzesLastDate));

            this.summariesCount.setText(summariesCount + " resource sets");
            this.summariesLastUsed.setText(formatLastUsedDate(summariesLastDate));

            this.transcriptionsCount.setText(transcriptionsCount + " resource sets");
            this.transcriptionsLastUsed.setText(formatLastUsedDate(transcriptionsLastDate));
        }
    }

    private String formatLastUsedDate(LocalDate date) {
        if (date == null) {
            return "Never used";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
        return "Last used: " + date.format(formatter);
    }

    public void getLearningAssets() {
        // TODO: Replace with actual API call
        LearningAssets asset = new LearningAssets();
        assetsList = asset.getDefault();

        // For now, show all assets
        filteredAssetsList = new ArrayList<>(assetsList != null ? assetsList : new ArrayList<>());
        displayLearningAssets();
        updateCardStatistics();
    }

    private void displayLearningAssets() {
        if (learningAssetsContainer == null) return;

        // Clear existing views
        learningAssetsContainer.removeAllViews();

        if (filteredAssetsList == null || filteredAssetsList.isEmpty()) {
            // Show empty state

            View empty = getLayoutInflater().inflate(R.layout.empty_state,learningAssetsContainer,false);
            TextView heading = empty.findViewById(R.id.heading);
            TextView desc =empty.findViewById(R.id.description);

            heading.setText("No Learning Assets Yet");
            desc.setText("You haven’t added any resources. Upload your first study material to get started!");
            learningAssetsContainer.addView(empty);
            return;
        }

        // Add each asset to the container
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (LearningAssets asset : filteredAssetsList) {
            View assetView = createAssetView(inflater, asset);
            learningAssetsContainer.addView(assetView);
        }
    }

    private View createAssetView(LayoutInflater inflater, LearningAssets asset) {
        // Inflate the custom layout
        View assetView = inflater.inflate(R.layout.learning_assets_item, learningAssetsContainer, false);

        // Get references to views
        ImageView assetTypeIcon = assetView.findViewById(R.id.assetTypeIcon);
        TextView assetTitle = assetView.findViewById(R.id.assetTitle);
        TextView assetDescription = assetView.findViewById(R.id.assetDescription);
        TextView assetDate = assetView.findViewById(R.id.assetDate);
        ImageView deleteButton = assetView.findViewById(R.id.deleteButton);

        // Set asset type icon based on type
        int iconResource = getAssetTypeIcon(asset.getType() != null ? asset.getType().name() : "DEFAULT");
        assetTypeIcon.setImageResource(iconResource);

        // Set text values
        assetTitle.setText(asset.getTitle() != null ? asset.getTitle() : "Untitled");
        assetDescription.setText(asset.getType() != null ?
                asset.getType().name() + " from "+asset.getOriginalFilename() : "Unknown type");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
        assetDate.setText(asset.getCreatedDate() != null ?
                asset.getCreatedDate().format(formatter) : "Unknown date");

        // Set click listeners
        assetView.setOnClickListener(v -> {
            // TODO: Handle asset click - open the learning asset
            Toast.makeText(getContext(), "Opening: " + asset.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this.getContext(), AIResourceDetailActivity.class);

            // Convert Resource object to JSON string
            Gson gson = new Gson();
            String assetJson = gson.toJson(asset);

            intent.putExtra("asset", assetJson);
            intent.putExtra("type", asset.getType().name().toLowerCase());
            startActivity(intent);

        });

        deleteButton.setOnClickListener(v -> {
            // TODO: Handle delete action
            showDeleteConfirmation(asset);
        });

        return assetView;
    }

    private int getAssetTypeIcon(String type) {
        switch (type.toUpperCase()) {
            case "FLASHCARD":
                return R.drawable.flashcard;
            case "QUIZ":
                return R.drawable.quiz;
            case "SUMMARY":
                return R.drawable.summary;
            case "TRANSCRIPTION":
                return R.drawable.transcribe;
            default:
                return R.drawable.book;
        }
    }

    private void showDeleteConfirmation(LearningAssets asset) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Delete Learning Asset")
                .setMessage("Are you sure you want to delete \"" + asset.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAsset(asset);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAsset(LearningAssets asset) {
        // TODO: Implement API call to delete asset
        if (assetsList != null) {
            assetsList.remove(asset);
        }
        if (filteredAssetsList != null) {
            filteredAssetsList.remove(asset);
        }
        displayLearningAssets();
        updateCardStatistics();
        Toast.makeText(getContext(), "Asset deleted", Toast.LENGTH_SHORT).show();
    }
}