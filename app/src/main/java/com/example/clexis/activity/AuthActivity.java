package com.example.clexis.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clexis.R;
import com.example.clexis.SessionManager;
import com.example.clexis.models.Alert;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.entity.Student;
import com.example.clexis.models.entity.Student;
import com.example.clexis.models.request.LoginRequest;
import com.example.clexis.models.request.RegisterRequest;
import com.example.clexis.models.response.LoginResponse;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {
    TextInputEditText fullnameInput, emailInput, passwordInput, emailInput2, passwordInput2,confirmPasswordInput,
            professionInput, inputPhone, bioInput;
    AutoCompleteTextView dropdownRoles; //
    private ApiService api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        LinearLayout loginContent = findViewById(R.id.loginFragment);
        ScrollView signupContent = findViewById(R.id.signupFragment);

        signupContent.findViewById(R.id.signupBtn).setOnClickListener(v->{

            registerStudent();

        });

        loginContent.findViewById(R.id.loginBtn).setOnClickListener(v->{

            loginStudent();
        });

        fullnameInput = findViewById(R.id.fullnameInput);
        emailInput = findViewById(R.id.emailInput);
        emailInput2 = signupContent.findViewById(R.id.emailInput);
        passwordInput2 = signupContent.findViewById(R.id.passwordInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        professionInput = findViewById(R.id.academicLevelInput);
        dropdownRoles = findViewById(R.id.dropdown_roles);
        inputPhone = findViewById(R.id.input_phone);
        bioInput = findViewById(R.id.bioInput);

        String[] levels = {"HIGHSCHOOL", "UNDERGRADUATE", "GRADUATE","OTHER"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, levels);
        dropdownRoles.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Login tab
                        loginContent.setVisibility(View.VISIBLE);
                        signupContent.setVisibility(View.GONE);
                        break;
                    case 1: // Signup tab
                        loginContent.setVisibility(View.GONE);
                        signupContent.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void registerStudent(){
        String fullName = fullnameInput.getText().toString().trim();
        String email = emailInput2.getText().toString().trim();
        String password = passwordInput2.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String profession = professionInput.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();
        String level = dropdownRoles.getText().toString().trim();

        String language = getSavedLangCode().equals("en") ? "english" : "french";
        RegisterRequest request = new RegisterRequest(fullName,level,password,email,phone,bio,profession,language);
        Log.d("TEST API", "Request object: " + new Gson().toJson(request));
        // ✅ Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || profession.isEmpty() || phone.isEmpty() || bio.isEmpty() || level.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 9) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }


        register(request);


    }

    public void loginStudent(){

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();


        // ✅ Validation
        if (email.isEmpty() || password.isEmpty() ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }

       LoginRequest request = new LoginRequest(email,password);

        login(request);

    }

    private String getSavedLangCode() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        return prefs.getString("My_Lang", "en");
    }

    private void login(LoginRequest request) {
        login(request, false); // calls the main method with default
    }
    private void login(LoginRequest request,boolean registered) {
        Call<ResponseDto<LoginResponse>> call = api.loginUser(request);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing Login...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        call.enqueue(new Callback<ResponseDto<LoginResponse>>() {
            @Override
            public void onResponse(Call<ResponseDto<LoginResponse>> call, Response<ResponseDto<LoginResponse>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<LoginResponse> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic
                        SessionManager sessionManager = new SessionManager(AuthActivity.this);
                        sessionManager.saveSession(dto.data.getToken(),dto.data.getUserId());

                        Log.d("API", "Login Success! Token: " + dto.data.getToken());
                        getStudentName(dto.data.getUserId());
                        if(registered){
                            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                            finish();
                        }else{
                            Alert.showAlert(AuthActivity.this,"Login Successfully","Welcome Back learner. Get ready to succeed",true,()->{
                                startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                                finish();
                            });
                        }

                    }else{
                        Alert.showAlert(AuthActivity.this,"Login Failed",dto.getErrors(),false,null);
                    }


                }
                else{
                    String errorString = null;
                    try {
                        if (response.errorBody() != null) {
                            errorString = response.errorBody().string();
                            Log.d("API response", "Error String: " + errorString);

                            // Parse the JSON string into ResponseDto
                            Gson gson = new Gson();
                            ResponseDto<Object> errorResponse = gson.fromJson(
                                    errorString,
                                    new TypeToken<ResponseDto<Object>>(){}.getType()
                            );

                            // Now you can access fields
                            Log.d("API response", "Error message: " + errorResponse.getMeta().getMessage());
                            Log.d("API response", "Error code: " + errorResponse.getMeta().getStatusCode());

                            // Show a meaningful message to the user
                            Alert.showAlert(AuthActivity.this,"Login Failed",errorResponse.getErrors(),false,null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onFailure(Call<ResponseDto<LoginResponse>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AuthActivity.this, "Error Occured during login . Try again later", Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void getStudentName(String userId) {
        Call<ResponseDto<Student>> call = api.getStudent(userId);
        call.enqueue(new Callback<ResponseDto<Student>>() {
            @Override
            public void onResponse(Call<ResponseDto<Student>> call, Response<ResponseDto<Student>> response) {
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<Student> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("studentName", dto.getData().getFullName());
                        editor.putInt("streak", 0);
                        editor.apply();

                    }
                }
                else{
                    String errorString = null;
                    try {
                        if (response.errorBody() != null) {
                            errorString = response.errorBody().string();
                            Log.d("API response", "Error String: " + errorString);

                            // Parse the JSON string into ResponseDto
                            Gson gson = new Gson();
                            ResponseDto<Object> errorResponse = gson.fromJson(
                                    errorString,
                                    new TypeToken<ResponseDto<Object>>(){}.getType()
                            );

                            // Now you can access fields
                            Log.d("API response", "Error message: " + errorResponse.getMeta().getMessage());
                            Log.d("API response", "Error code: " + errorResponse.getMeta().getStatusCode());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseDto<Student>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });

    }

    private void register(RegisterRequest request) {
        Call<ResponseDto<Object>> call = api.registerUser(request);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing Registration...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        call.enqueue(new Callback<ResponseDto<Object>>() {
            @Override
            public void onResponse(Call<ResponseDto<Object>> call, Response<ResponseDto<Object>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<Object> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        Alert.showAlert(AuthActivity.this,"Account Created","Your Account has been created successfully. Welcome Aboard",true,()->{
                            LoginRequest request2 = new LoginRequest(request.getEmail(),request.getPassword());
                            login(request2,true);
                        });
                        Log.d("API", "Registraion Success!" );

                    }else{
                        Alert.showAlert(AuthActivity.this,"Registration Failed",dto.getErrors(),false,null);
                        Log.d("API", "Registraion failed! "+dto.getErrors() );
                    }

                } else{
                    String errorString = null;
                    try {
                        if (response.errorBody() != null) {
                            errorString = response.errorBody().string();
                            Log.d("API response", "Error String: " + errorString);

                            // Parse the JSON string into ResponseDto
                            Gson gson = new Gson();
                            ResponseDto<Object> errorResponse = gson.fromJson(
                                    errorString,
                                    new TypeToken<ResponseDto<Object>>(){}.getType()
                            );

                            // Now you can access fields
                            Log.d("API response", "Error message: " + errorResponse.getMeta().getMessage());
                            Log.d("API response", "Error code: " + errorResponse.getMeta().getStatusCode());

                            // Show a meaningful message to the user
                            Alert.showAlert(AuthActivity.this,"Registration Failed",errorResponse.getErrors(),false,null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseDto<Object>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AuthActivity.this, "Error Occured during registration . Try again later", Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: " + t.getMessage());
            }


        });
    }





}