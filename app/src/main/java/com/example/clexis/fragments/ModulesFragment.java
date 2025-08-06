package com.example.clexis.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clexis.R;
import com.example.clexis.adapters.ModulesAdapter;
import com.example.clexis.models.Module;
import com.example.clexis.models.ModuleItem;
import com.example.clexis.models.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModulesFragment extends Fragment {

    private RecyclerView modulesRecyclerView;
    private ModulesAdapter moduleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modules_fragment, container, false);

        modulesRecyclerView = view.findViewById(R.id.recycler_modules);
        modulesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Module> modules = generateSampleModules();
        moduleAdapter = new ModulesAdapter(modules);
        modulesRecyclerView.setAdapter(moduleAdapter);

        return view;
    }

    private List<Module> generateSampleModules() {
        List<Module> modules = new ArrayList<>();


        List<Task> tasks = Arrays.asList(
                new Task("Code Challenge Practice", Arrays.asList("Tuesday","Friday"), null, false),
                new Task("Build Personal Portfolio Page", null, "Mar 10, 2024", false)
        );

        modules.add(new Module("Module 1: Front-End Fundamentals", "Covering HTML5, CSS3, and JavaScript concepts.", tasks));
        return modules;
    }
}
