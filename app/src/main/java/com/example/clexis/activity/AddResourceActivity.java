package com.example.clexis.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.SessionManager;
import com.example.clexis.models.Alert;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.enums.ResourceType;
import com.example.clexis.models.request.LoginRequest;
import com.example.clexis.models.request.RegisterRequest;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddResourceActivity extends AppCompatActivity {

    private static final int FILE_PICK_CODE = 101;

    private EditText etName, etDescription, etPhone, etPrice;
    private SwitchMaterial chkIsSelling;
    private MaterialAutoCompleteTextView spinnerType;
    private LinearLayout layoutSellingDetails;

    private Button btnUploadFile, btnSubmit;
    private TextView tvFileName, tvFeeNotice;

    private Uri selectedFileUri;

    private ApiService api;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modal_add_resource);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }


        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        // Bind views
        etName = findViewById(R.id.etResourceName);
        etDescription = findViewById(R.id.etResourceDescription);
        etPhone = findViewById(R.id.etPhoneNumber);
        etPrice = findViewById(R.id.etPrice);

        chkIsSelling = findViewById(R.id.chkIsSelling);
        spinnerType = findViewById(R.id.spinnerResourceType);
        layoutSellingDetails = findViewById(R.id.layoutSellingDetails);

        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSubmit = findViewById(R.id.btnSubmitResource);
        tvFileName = findViewById(R.id.tvFileName);
        tvFeeNotice = findViewById(R.id.tvFeeNotice);

        // Setup dropdown for resource types
        String[] types = new String[]{"Book", "Thesis", "Paper", "User Upload"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, types);
        spinnerType.setAdapter(adapter);

        // Show/hide selling details when switch toggled
        chkIsSelling.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutSellingDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // File upload button
        btnUploadFile.setOnClickListener(v -> pickFile());

        // Submit button
        btnSubmit.setOnClickListener(v -> submitResource());
    }

    // Launch file picker
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                String fileName = getFileName(selectedFileUri);
                tvFileName.setText(fileName);

                // Make the file info layout visible
                LinearLayout layoutFileInfo = findViewById(R.id.layoutFileInfo);
                layoutFileInfo.setVisibility(View.VISIBLE);
            }
        }
    }


    // Get the file name from URI
    private String getFileName(Uri uri) {
        String result = "Unknown";
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = new File(uri.getPath()).getName();
        }
        return result;
    }

    // Build and submit the Resource
    private void submitResource() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String typeStr = spinnerType.getText().toString().trim();

        boolean isSelling = chkIsSelling.isChecked();
        String phone = isSelling ? etPhone.getText().toString().trim() : null;
        int price = 0;
        if (isSelling) {
            try {
                price = Integer.parseInt(etPrice.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validate inputs
        if (name.isEmpty() || desc.isEmpty() || typeStr.isEmpty() ||
                (isSelling && (phone.isEmpty() || price <= 0)) || selectedFileUri == null) {
            Toast.makeText(this, "Please fill all fields and upload a file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert string to ResourceType enum
        ResourceType typeEnum;
        try {
            typeStr = typeStr.replace(" ", "_");
            typeEnum = ResourceType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid resource type "+typeStr, Toast.LENGTH_SHORT).show();
            return;
        }

        //logic to upload to cloudinary

        // Build Resource object
        Resource resource = Resource.builder()
                .id("res_" + System.currentTimeMillis())
                .name(name)
                .description(desc)
                .type(typeEnum)
                .free(!isSelling)
                .price(price)
                .merchantNumber(phone)
                .fileUrl(selectedFileUri.toString()) // upload logic to backend later
                .verified(false)
                .build();

        // TODO: send resource to backend API
        addResource(resource);

    }

    private void addResource(Resource resource) {
        Call<ResponseDto<Resource>> call = api.addResource(resource);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        call.enqueue(new Callback<ResponseDto<Resource>>() {
            @Override
            public void onResponse(Call<ResponseDto<Resource>> call, Response<ResponseDto<Resource>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<Resource> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        Alert.showAlert(AddResourceActivity.this,"\uD83C\uDF89 Resource Created!","Your resource is now part of the community. Thanks for sharing!",true,()->{
                           finish();
                        });


                    }else{
                        Alert.showAlert(AddResourceActivity.this,"\uD83D\uDE22 Oops!","We couldn’t create your resource. Let’s give it another shot.\n"+dto.getErrors(),false,null);

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
                            Alert.showAlert(AddResourceActivity.this,"Resource Addition Failed",errorResponse.getErrors(),false,null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseDto<Resource>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddResourceActivity.this, "Error Occured while adding resource .Check your network connectivity", Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: " + t.getMessage());
            }


        });
    }
}
