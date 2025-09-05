package com.example.clexis;

import android.content.Context;

import com.example.clexis.models.entity.BuddyProgram;
import com.example.clexis.models.entity.LearningPath;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.ObjectRepository;

import java.io.File;


public class DatabaseManager {

    private static Nitrite database;

    public static void initDatabase(String username, String password) {
        if (database == null) {
            database = Nitrite.builder()
                    .openOrCreate(username, password);
        }

    }

    public static Nitrite getDatabase(Context context) {
        if (database == null) {
            File dbFile = new File(context.getFilesDir(), "clexis.db"); // local storage

            database = Nitrite.builder()
                    .openOrCreate("username", "password"); // optional credentials
        }
        return database;
    }

    public static void closeDatabase() {
        if (database != null && !database.isClosed()) {
            database.close();
        }
    }
}
