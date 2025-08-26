package com.example.clexis.models;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class Alert {

    public static void showAlert(Context context, String title, String message, boolean isSuccess,DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set title and message
        builder.setTitle(title);
        builder.setMessage(message);

        // Optional: change icon based on success or error
        if (isSuccess) {
            builder.setIcon(android.R.drawable.checkbox_on_background); // success icon
        } else {
            builder.setIcon(android.R.drawable.ic_delete); // error icon
        }
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onOkClicked(); // trigger the action
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void progress(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();
    }
    public interface DialogCallback {
        void onOkClicked();
    }


}

