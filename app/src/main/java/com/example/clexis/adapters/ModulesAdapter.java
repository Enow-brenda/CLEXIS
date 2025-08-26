package com.example.clexis.adapters;

import static android.content.ContentValues.TAG;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clexis.R;
import com.example.clexis.models.dto.Module;
import com.example.clexis.models.dto.Task;

import java.util.List;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ModuleViewHolder> {

    private List<Module> moduleList;

    public ModulesAdapter(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_card, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.moduleTitle.setText(module.getTitle());
        holder.moduleDesc.setText(module.getObjective());


        // Load tasks into nested layout
        holder.tasksLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        for (Task task : module.getTasks()) {
            View taskView = inflater.inflate(R.layout.task_box_lp, holder.tasksLayout, false);

            // Access and update views inside item_task.xml
            TextView titleText = taskView.findViewById(R.id.task_title);   // Update ID in XML
            TextView scheduleText = taskView.findViewById(R.id.date); // Update ID in XML

            // Update text dynamically
            titleText.setText(task.getTitle());
            if (task.getDate() != null) {
                scheduleText.setText("Date: " + task.getDate());
            } else {
                scheduleText.setText(TextUtils.join(", ", task.getSchedule()));
            }

            // Optional: Set listeners for edit/delete icons
            ImageView editBtn = taskView.findViewById(R.id.task_edit);   // Update ID in XML
            ImageView deleteBtn = taskView.findViewById(R.id.task_delete); // Update ID in XML

            // Example click listener
            editBtn.setOnClickListener(v -> {
                // Handle edit
            });

            deleteBtn.setOnClickListener(v -> {
                // Handle delete
            });

            // Add the custom task view to the layout
            holder.tasksLayout.addView(taskView);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Debug info here"+moduleList.size() );
        return moduleList.size();
    }

    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView moduleTitle, moduleDesc;
        LinearLayout tasksLayout;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleTitle = itemView.findViewById(R.id.moduleTitle);
            moduleDesc = itemView.findViewById(R.id.moduleDesc);
            tasksLayout = itemView.findViewById(R.id.tasks);
        }
    }
}


