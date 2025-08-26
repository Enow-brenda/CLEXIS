package com.example.clexis.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.clexis.SessionManager;
import com.example.clexis.activity.AuthActivity;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {

//    private static final String BASE_URL = "https://clexis-gateway.up.railway.app/";
    private static final String BASE_URL = "http://192.168.208.190:8000/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        SessionManager sessionManager = new SessionManager(context);
                        String token = sessionManager.getToken();

                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        if (token != null) {
                            builder.header("Authorization", "Bearer " + token);
                        }

                        Request request = builder.build();
                        Response response = chain.proceed(request);

                        if (response.code() == 401) { // Unauthorized
                            // Token expired: clear session
                            sessionManager.logout();

                            // Redirect to login (must run on UI thread)
                            // Use Handler to post on main thread
                            new android.os.Handler(context.getMainLooper()).post(() -> {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                builder2.setTitle("Session Expired");
                                builder2.setMessage("Your session has expired. Please log in again.");
                                builder2.setCancelable(false);
                                builder2.setPositiveButton("OK", (dialog, which) -> {
                                    // Redirect to login
                                    Intent intent = new Intent(context, AuthActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                });
                                builder2.show();
                            });
                        }

                        return response;
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
