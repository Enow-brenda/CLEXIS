package com.example.clexis.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.clexis.R;
import com.example.clexis.models.entity.Resource;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

public class ResourceDescriptionActivity extends AppCompatActivity {
    private ImageView resourceCover;
    private TextView resourceTitle, resourceUploader, resourceDescription;
    private Button btnView, btnDownloadOrBuy;

    private Resource resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resource_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        resourceCover = findViewById(R.id.resource_cover);
        resourceTitle = findViewById(R.id.resource_title);
        resourceUploader = findViewById(R.id.resource_uploader);
        resourceDescription = findViewById(R.id.resource_description);
        btnView = findViewById(R.id.btn_view);
        btnDownloadOrBuy = findViewById(R.id.btn_download_or_buy);

        // Toolbar back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Get Resource Object ---
        Intent intent = getIntent();
        String json = intent.getStringExtra("resource"); // If you used Gson
        resource = new Gson().fromJson(json, Resource.class);

        if (resource != null) {
            bindResource(resource);
        }
    }

    private void bindResource(Resource res) {
        // Set title, uploader, and description
        resourceTitle.setText(res.getName());
        resourceUploader.setText("Uploaded by: @" + res.getUsername());
        resourceDescription.setText(res.getDescription());

        // Load cover image (if exists)
        if (res.getImageUrl() != null && !res.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(res.getImageUrl())
                    .placeholder(R.color.gray)
                    .into(resourceCover);
        } else {
            resourceCover.setImageResource(R.color.gray);
        }

        // Button text depends on free/paid
        if (res.isFree()) {
            btnDownloadOrBuy.setText("Download");
        } else {
            btnDownloadOrBuy.setText("Buy (" + res.getPrice() + ")");
        }

        // --- Button Actions ---
        btnView.setOnClickListener(v -> {
            if (res.getFileUrl() != null) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(res.getFileUrl()));
                startActivity(viewIntent);
            }
        });

        btnDownloadOrBuy.setOnClickListener(v -> {
            if (res.isFree() && res.getFileUrl() != null) {
                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(res.getFileUrl()));
                startActivity(downloadIntent);
            } else {
                // Open payment/merchant logic (MVP just show number for now)
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + res.getMerchantNumber()));
                startActivity(dialIntent);
            }
        });
    }
}
