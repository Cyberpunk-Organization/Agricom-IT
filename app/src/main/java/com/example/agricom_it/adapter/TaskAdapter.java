package com.example.agricom_it.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
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

import com.example.agricom_it.R;
import com.example.agricom_it.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<Task> taskList;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final OnTaskInteractionListener listener;

    //---------------------------------------------------------------------------------[OnTaskInteractionLister]
    public interface OnTaskInteractionListener {
        void onTaskDeleteClicked(Task task, int position);
    }

    //---------------------------------------------------------------------------------[TaskAdapter]
    public TaskAdapter(List<Task> taskList, Context context, OnTaskInteractionListener listener ) {
        this.taskList = taskList;
        this.context = context;
        this.listener = listener;
    }

    //--------------------------------------------------------------------------[onCreateViewHolder]
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    //----------------------------------------------------------------------------[onBindViewHolder]
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTaskDesc.setText(task.getDescription());

        // Checkbox logic
//        holder.checkBox.setOnCheckedChangeListener(null);
//        holder.checkBox.setChecked(task.isDone());
//        updateTaskAppearance(holder, task.isDone());
//
//        holder.checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
//            task.setDone(isChecked);
//            updateTaskAppearance(holder, isChecked);
//        });

        // Show due date or default text
        if (task.getDueDate() != null) {
            holder.tvDueDate.setText(dateFormat.format(task.getDueDate()));
        } else {
            holder.tvDueDate.setText("No date");
        }

        // 🔹 Click on due date to change it
        holder.tvDueDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            if (task.getDueDate() != null) {
                calendar.setTime(task.getDueDate());
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (datePickerView, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        task.setDueDate(calendar.getTime());
                        holder.tvDueDate.setText(dateFormat.format(calendar.getTime()));
                        notifyItemChanged(holder.getAdapterPosition());
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Delete button
        holder.buttonDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (listener != null && currentPosition != RecyclerView.NO_POSITION) {
                listener.onTaskDeleteClicked(taskList.get(currentPosition), currentPosition);
            }
        });
    }

    //--------------------------------------------------------------------------------[getItemCount]
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    //------------------------------------------------------------------------[updateTaskAppearance]
    private void updateTaskAppearance(@NonNull TaskViewHolder holder, boolean isDone) {
        if (isDone) {
            holder.tvTaskDesc.setPaintFlags(holder.tvTaskDesc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTaskDesc.setTextColor(0xFF888888); // gray text
        } else {
            holder.tvTaskDesc.setPaintFlags(holder.tvTaskDesc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    //------------------------------------------------------------------------------[TaskViewHolder]
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskDesc, tvDueDate;
        CheckBox checkBox;
        ImageButton buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskDesc = itemView.findViewById(R.id.tvDescription);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    //------------------------------------------------------------------------------[removeTask]
    public void removeItem(int position) {
        // Check for a valid position to prevent crashes
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            notifyItemRemoved(position);
            // This is important to update the positions of subsequent items
            notifyItemRangeChanged(position, taskList.size());
        }
    }

}
