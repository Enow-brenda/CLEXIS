package com.example.clexis.repository;

import android.content.Context;

import com.example.clexis.models.entity.LearningPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class LearningPathRepository {
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type listType = new TypeToken<List<LearningPath>>() {}.getType();

    public LearningPathRepository(Context context) {
        file = new File(context.getFilesDir(), "learning_paths.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
                saveAll(new ArrayList<>()); // initialize empty array
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 🔹 Load all LearningPaths
    private List<LearningPath> loadAll() {
        try (FileReader reader = new FileReader(file)) {
            List<LearningPath> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 🔹 Save all LearningPaths
    private void saveAll(List<LearningPath> list) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ CREATE
    public void add(LearningPath lp) {
        List<LearningPath> list = loadAll();
        list.add(lp);
        saveAll(list);
    }

    // ✅ READ
    public List<LearningPath> getAll() {
        return loadAll();
    }

    public LearningPath getActive() {
        List<LearningPath> lpaths = loadAll();
        if(!lpaths.isEmpty()){
            List<LearningPath> actives = lpaths.stream()
                    .filter(LearningPath::isActive)
                    .collect(Collectors.toList());
            if(actives.isEmpty()){
                return null;
            }else{
                return actives.get(0);
            }
        }
        return null;

    }

    public List<LearningPath> getByUser(String userId) {
        return loadAll().stream()
                .filter(lp -> userId.equals(lp.getUserId()))
                .collect(Collectors.toList());
    }

    public Optional<LearningPath> getById(String id) {
        return loadAll().stream()
                .filter(lp -> id.equals(lp.getId()))
                .findFirst();
    }

    // ✅ UPDATE
    public boolean update(String id, LearningPath updated) {
        List<LearningPath> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.set(i, updated);
                saveAll(list);
                return true;
            }
        }
        return false; // not found
    }

    // ✅ DELETE
    public boolean delete(String id) {
        List<LearningPath> list = loadAll();
        boolean removed = list.removeIf(lp -> lp.getId().equals(id));
        if (removed) saveAll(list);
        return removed;
    }
}
