package com.example.clexis.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.SessionManager;
import com.example.clexis.activity.AchievementActivity;
import com.example.clexis.activity.DownloadActivity;
import com.example.clexis.activity.NotificationActivity;
import com.example.clexis.activity.ScheduleActivity;
import com.example.clexis.activity.ViewLearningPlanActivity;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.dto.ChangePasswordRequest;
import com.example.clexis.models.entity.Student;
import com.example.clexis.models.entity.User;
import com.example.clexis.models.enums.AcademicLevel;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Student student;
    private User user;
    private ApiService api;

    // UI references
    private TextView tvFullName, tvAcademic, tvBio, tvEmail, tvStudName ,tvProfession, tvPhone;
    private MaterialButton tvChangePassword;
    private ImageView tvEditProfile;


    // Flags to track network completion
    private boolean studentLoaded = false;
    private boolean userLoaded = false;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        api = ApiClient.getRetrofitInstance(requireContext()).create(ApiService.class);

        // navigation buttons
        view.findViewById(R.id.lpath).setOnClickListener(v -> startActivity(new Intent(getContext(), ViewLearningPlanActivity.class)));
        view.findViewById(R.id.portfolio).setOnClickListener(v -> startActivity(new Intent(getContext(), AchievementActivity.class)));
        view.findViewById(R.id.notification).setOnClickListener(v -> startActivity(new Intent(getContext(), NotificationActivity.class)));
        view.findViewById(R.id.downloads).setOnClickListener(v -> startActivity(new Intent(getContext(), DownloadActivity.class)));
        view.findViewById(R.id.schedule).setOnClickListener(v -> startActivity(new Intent(getContext(), ScheduleActivity.class)));

        // bind profile fields
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvStudName = view.findViewById(R.id.tv_student_name);
        tvAcademic = view.findViewById(R.id.tv_academic_level);
        tvBio = view.findViewById(R.id.tv_bio_interests);
        tvEmail = view.findViewById(R.id.tv_email);
        tvProfession = view.findViewById(R.id.tv_profession);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvEditProfile = view.findViewById(R.id.tv_edit_profile);
        tvChangePassword = view.findViewById(R.id.signupBtn);

        // Show local defaults immediately
        getLocalStudent();
        getLocalUser();
        setupActivity(); // populate UI quickly with defaults

        // Fetch network data
        getUserAndStudentData();

        // Edit profile click listener
        tvEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Change password click listener
        tvChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        return view;
    }

    private void setupActivity() {
        if (student == null || user == null) return;

        tvFullName.setText(student.getFullName());
        tvAcademic.setText(student.getAcademicLevel() != null ? student.getAcademicLevel().toString() : "N/A");
        tvBio.setText(student.getBioOrInterest());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(student.getPhoneNumber());
        tvProfession.setText(student.getProfession());
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);

        TextInputEditText etFullName = dialogView.findViewById(R.id.et_fullname);
        TextInputEditText etEmail= dialogView.findViewById(R.id.et_email);
        TextInputEditText etProfession = dialogView.findViewById(R.id.et_profession);
        AutoCompleteTextView dropdownRoles = dialogView.findViewById(R.id.dropdown_roles);
        TextInputEditText etPhone = dialogView.findViewById(R.id.input_phone);
        TextInputEditText etBio = dialogView.findViewById(R.id.bioInput);

        // Pre-fill with existing data (if available)
        if (student != null) {
            etFullName.setText(student.getFullName());
            etProfession.setText(student.getProfession());
            etPhone.setText(student.getPhoneNumber());
            etBio.setText(student.getBioOrInterest());
            dropdownRoles.setText(student.getAcademicLevel().name());
        }
        if (user != null) {
            // Example: use email in "academic" just for placeholder
            etEmail.setText(user.getEmail());
        }

        String[] roles = {"UNDERGRADUATE", "GRADUATE", "HIGHSCHOOL", "PROFESSIONAL"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, roles);
        dropdownRoles.setAdapter(adapter);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String fullName = etFullName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String profession = etProfession.getText().toString().trim();
                    String role = dropdownRoles.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String bio = etBio.getText().toString().trim();

                    // Save back into objects
                    if (student != null) {
                        student.setFullName(fullName);
                        student.setProfession(profession);
                        student.setBioOrInterest(bio);
                        student.setPhoneNumber(phone);
                        if (!role.isEmpty()) {
                            try {
                                student.setAcademicLevel(AcademicLevel.valueOf(role.toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                student.setAcademicLevel(null); // fallback
                            }
                        }
                    }
                    if (user != null) {
                        user.setEmail(email); // or use proper field depending on your model
                    }

                    // Call update API
                    updateStudent(new ApiResultCallback<Student>() {
                        @Override
                        public void onSuccess(Student data) {
                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                            setupActivity(); // refresh UI
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etOldPassword = dialogView.findViewById(R.id.et_old_password);
        TextInputEditText etNewPassword= dialogView.findViewById(R.id.et_new_password);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String oldPass = etOldPassword.getText().toString().trim();
                    String newPass = etNewPassword.getText().toString().trim();

                    if (oldPass.isEmpty() || newPass.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill both fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ChangePasswordRequest request = new ChangePasswordRequest(student.getId(), oldPass, newPass);
                    changePassword(request, new ApiResultCallback<Student>() {
                        @Override
                        public void onSuccess(Student data) { Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show(); }
                        @Override
                        public void onError(String errorMessage) { Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show(); }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void getUserAndStudentData() {
        SessionManager session = new SessionManager(requireContext());
        String id = session.getId();

        api.getStudent(id).enqueue(new Callback<ResponseDto<Student>>() {
            @Override
            public void onResponse(Call<ResponseDto<Student>> call, Response<ResponseDto<Student>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta().getStatusCode() == 200) {
                    student = response.body().getData();
                    studentLoaded = true;
                    trySetupActivity();
                } else { logError(response); }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDto<Student>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }
        });

        api.getUser(id).enqueue(new Callback<ResponseDto<User>>() {
            @Override
            public void onResponse(Call<ResponseDto<User>> call, Response<ResponseDto<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta().getStatusCode() == 200) {
                    user = response.body().getData();
                    userLoaded = true;
                    trySetupActivity();
                } else { logError(response); }
            }

            @Override
            public void onFailure(Call<ResponseDto<User>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }
        });
    }

    private void trySetupActivity() { if (studentLoaded && userLoaded) setupActivity(); }

    public interface ApiResultCallback<T> { void onSuccess(T data); void onError(String errorMessage); }

    public void changePassword(ChangePasswordRequest request, ApiResultCallback<Student> callback) {
        api.changePassword(request).enqueue(new Callback<ResponseDto<Student>>() {
            @Override
            public void onResponse(Call<ResponseDto<Student>> call, Response<ResponseDto<Student>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta().getStatusCode() == 200)
                    callback.onSuccess(response.body().getData());
                else callback.onError("Password change failed");
            }

            @Override
            public void onFailure(Call<ResponseDto<Student>> call, Throwable t) {
                callback.onError("Failed to reach the server: " + t.getMessage());
            }
        });
    }

    private void logError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorString = response.errorBody().string();
                Log.d("API response", "Error String: " + errorString);

                Gson gson = new Gson();
                ResponseDto<Object> errorResponse = gson.fromJson(
                        errorString,
                        new TypeToken<ResponseDto<Object>>() {}.getType()
                );

                Log.d("API response", "Error message: " + errorResponse.getMeta().getMessage());
                Log.d("API response", "Error code: " + errorResponse.getMeta().getStatusCode());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void getLocalUser() { user = User.defaultUser(); }
    private void getLocalStudent() { student = Student.defaultStudent(); }

    public void updateStudent(ApiResultCallback<Student> callback) {
        api.updateStudent(student).enqueue(new Callback<ResponseDto<Student>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDto<Student>> call, Response<ResponseDto<Student>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta().getStatusCode() == 200)
                    callback.onSuccess(response.body().getData());
                else callback.onError("Failed to update student");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDto<Student>> call, Throwable t) {
                callback.onError("Failed to reach the server: " + t.getMessage());
            }
        });
    }

    public void updateUser(ApiResultCallback<User> callback) {
        api.updateUser(user).enqueue(new Callback<ResponseDto<User>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDto<User>> call, Response<ResponseDto<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta().getStatusCode() == 200)
                    callback.onSuccess(response.body().getData());
                else callback.onError("Failed to update user");
            }

            @Override
            public void onFailure(Call<ResponseDto<User>> call, Throwable t) {
                callback.onError("Failed to reach the server: " + t.getMessage());
            }
        });
    }
}
