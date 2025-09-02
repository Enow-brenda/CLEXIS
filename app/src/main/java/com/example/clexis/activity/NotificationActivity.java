package com.example.clexis.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.entity.Notification;
import com.example.clexis.models.enums.NotificationType;
import com.example.clexis.models.response.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private List<Notification> notificationList = new ArrayList<>();



    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        setupActivity();
    }

    private void setupActivity() {
        List<Notification> online = getOnlineNotification();
        if (online == null) online = new ArrayList<>();

        List<Notification> local = getLocalNotification();
        if (local == null) local = new ArrayList<>();

        for(Notification notification:local){
            notification.setRead(true);
        }
        if(online!=null && online.isEmpty()){
            notificationList.addAll(local);
        }else{
            for (Notification notif : online) {
                // Check if this notification is already in local
                boolean isRead = false;
                for (Notification localNotif : local) {
                    if (notif.getId().equals(localNotif.getId())) {
                        isRead = true; // already seen locally
                        break;
                    }
                }
                notif.setRead(isRead); // mark as read/unread
                notificationList.add(notif);
            }
        }

        renderNotifications();
    }

    private void renderNotifications() {
        LinearLayout container = findViewById(R.id.notification_container);
        container.removeAllViews();

        // Sort notifications by timestamp descending
        notificationList.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));

        // Use a map to group notifications by date string
        Map<String, List<Notification>> grouped = new LinkedHashMap<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault());
        for (Notification notif : notificationList) {
            String date = notif.getTimestamp().format(dateFormatter);
            if (!grouped.containsKey(date)) {
                grouped.put(date, new ArrayList<>());
            }
            grouped.get(date).add(notif);
        }

        // Loop through each date group
        for (Map.Entry<String, List<Notification>> entry : grouped.entrySet()) {
            String date = entry.getKey();
            List<Notification> notifs = entry.getValue();

            // Date header
            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(Gravity.CENTER_VERTICAL);
            headerLayout.setPadding(24, 24, 24, 12);

            // Left divider line
            View leftLine = new View(this);
            LinearLayout.LayoutParams leftLineParams = new LinearLayout.LayoutParams(0, 2);
            leftLineParams.weight = 1;
            leftLineParams.setMargins(0, 0, 16, 0);
            leftLine.setLayoutParams(leftLineParams);
            leftLine.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));

            // Date text
            TextView dateHeader = new TextView(this);
            dateHeader.setText(date);
            dateHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            dateHeader.setTextColor(ContextCompat.getColor(this, R.color.white));
            dateHeader.setTypeface(null, Typeface.BOLD);
            dateHeader.setPaddingRelative(12, 6, 12, 6);
            dateHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.date_header_bg));

            // Right divider line
            View rightLine = new View(this);
            LinearLayout.LayoutParams rightLineParams = new LinearLayout.LayoutParams(0, 2);
            rightLineParams.weight = 1;
            rightLineParams.setMargins(16, 0, 0, 0);
            rightLine.setLayoutParams(rightLineParams);
            rightLine.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));

            // Add views to header layout
            headerLayout.addView(leftLine);
            headerLayout.addView(dateHeader);
            headerLayout.addView(rightLine);

            container.addView(headerLayout);

            // Notifications for that date
            for (Notification notif : notifs) {
                View view = getLayoutInflater().inflate(R.layout.notification_item, container, false);

                TextView title = view.findViewById(R.id.text_notification_title);
                TextView message = view.findViewById(R.id.text_notification_message);
                TextView timestamp = view.findViewById(R.id.text_notification_time);

                title.setText(notif.getTitle());
                message.setText(notif.getMessage());

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
                timestamp.setText(notif.getTimestamp().format(timeFormatter));

                if (!notif.isRead()) {
                    view.findViewById(R.id.unread).setVisibility(View.VISIBLE);
                }

                container.addView(view);
            }
        }
    }



    private List<Notification> getLocalNotification() {
        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification(
                "n1",
                "New Task Assigned",
                "You have a new task due today.",
                NotificationType.REMINDER,
                "user@example.com",
                LocalDateTime.of(2025, 9, 1, 9, 0),
                "user123"
        ));


        notifications.add(new Notification(
                "n2",
                "Task Reminder",
                "Don't forget to complete your task.",
                NotificationType.REMINDER,
                "user@example.com",
                LocalDateTime.of(2025, 9, 1, 14, 30),
                "user123"
        ));

        notifications.add(new Notification(
                "n3",
                "Welcome!",
                "You successfully registered.",
                NotificationType.REGISTERED,
                "user@example.com",
                LocalDateTime.of(2025, 9, 2, 10, 15),
                "user123"
        ));

        notifications.add(new Notification(
                "n4",
                "Weekly Update",
                "Here’s what’s new this week.",
                NotificationType.NEW_UPDATE,
                "user@example.com",
                LocalDateTime.of(2025, 9, 3, 8, 0),
                "user123"
        ));

        notifications.add(new Notification(
                "n5",
                "Maintenance Notice",
                "Scheduled maintenance at 6 PM.",
                NotificationType.OTHER,
                "user@example.com",
                LocalDateTime.of(2025, 9, 1, 18, 45),
                "user123"
        ));
        return notifications;
    }

    private List<Notification> getOnlineNotification() {
        final List<Notification>[] notifs = new List[]{new ArrayList<>()};
        Call<ResponseDto<List<Notification>>> call = api.getNotifications();
        call.enqueue(new Callback<ResponseDto<List<Notification>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<Notification>>> call, Response<ResponseDto<List<Notification>>> response) {
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<List<Notification>> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic
                        notifs[0] = dto.getData();

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
            public void onFailure(Call<ResponseDto<List<Notification>>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });

        notifs[0].add(new Notification(
                "n89",
                "New Task Assigned",
                "You have a new task due today.",
                NotificationType.REMINDER,
                "user@example.com",
                LocalDateTime.of(2025, 9, 8, 9, 0),
                "user123"
        ));
        notifs[0].add(new Notification(
                "n1",
                "New Task Assigned",
                "You have a new task due today.",
                NotificationType.REMINDER,
                "user@example.com",
                LocalDateTime.of(2025, 9, 1, 9, 0),
                "user123"
        ));


        notifs[0].add(new Notification(
                "n2",
                "Task Reminder",
                "Don't forget to complete your task.",
                NotificationType.REMINDER,
                "user@example.com",
                LocalDateTime.of(2025, 9, 1, 14, 30),
                "user123"
        ));

        notifs[0].add(new Notification(
                "n3",
                "Welcome!",
                "You successfully registered.",
                NotificationType.REGISTERED,
                "user@example.com",
                LocalDateTime.of(2025, 9, 2, 10, 15),
                "user123"
        ));

        notifs[0].add(new Notification(
                "n4",
                "Weekly Update",
                "Here’s what’s new this week.",
                NotificationType.NEW_UPDATE,
                "user@example.com",
                LocalDateTime.of(2025, 9, 3, 8, 0),
                "user123"
        ));

        notifs[0].add(new Notification(
                "n5",
                "Maintenance Notice",
                "Scheduled maintenance at 6 PM.",
                NotificationType.OTHER,
                "user@example.com",
                LocalDateTime.of(2025, 9, 1, 18, 45),
                "user123"
        ));

        return notifs[0];
    }
}
