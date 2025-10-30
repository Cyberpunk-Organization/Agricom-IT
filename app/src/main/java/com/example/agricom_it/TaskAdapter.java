package com.example.agricom_it;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Use updated getter names
        holder.tvTaskDesc.setText(task.getDescription());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isDone());

        // Update appearance if already done
        updateTaskAppearance(holder, task.isDone());

        holder.checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            task.setDone(isChecked); // update the task model
            updateTaskAppearance(holder, isChecked);
        });

        holder.buttonDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                taskList.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private void updateTaskAppearance(@NonNull TaskViewHolder holder, boolean isDone) {
        if (isDone) {
            holder.tvTaskDesc.setPaintFlags(holder.tvTaskDesc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTaskDesc.setTextColor(0xFF888888); // gray text
        } else {
            holder.tvTaskDesc.setPaintFlags(holder.tvTaskDesc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvTaskDesc.setTextColor(0xFF000000); // black text
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskDesc;
        CheckBox checkBox;
        ImageButton buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskDesc = itemView.findViewById(R.id.tvDescription);
            checkBox = itemView.findViewById(R.id.checkBox);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
