package com.vogella.android.myapplication.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> taskList;
    private TaskAdapter.ClickListener clickListener;

    /*public TaskAdapter(ClickListener clickListener) {
        this.clickListener = clickListener;
        taskList = new ArrayList<>();
    }*/

    public TaskAdapter(List<Task> taskList, ClickListener clickListener) {
        this.taskList = taskList;
        this.clickListener = clickListener;
    }

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_row, parent, false);
        TaskAdapter.ViewHolder viewHolder = new TaskAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.txtName.setText(task.getTaskName());
        //holder.txtNo.setText("#" + String.valueOf(task.getTaskId()));
        holder.txtNo.setText("#" + String.valueOf(position+1));
        holder.txtDesc.setText(task.getDescription());
        holder.txtCategory.setText(dateToString(task.getTaskDate()));
    }

    private String dateToString(Date date){
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        return format2.format(date);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


    public void updateTodoList(List<Task> data) {
        taskList.clear();
        taskList.addAll(data);
        notifyDataSetChanged();
    }

    public void addRow(Task data) {
        taskList.add(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public TextView txtNo;
        public TextView txtDesc;
        public TextView txtCategory;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);

            txtNo = view.findViewById(R.id.txtNo);
            txtName = view.findViewById(R.id.txtName);
            txtDesc = view.findViewById(R.id.txtDesc);
            txtCategory = view.findViewById(R.id.txtCategory);
            cardView = view.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.launchIntent(taskList.get(getAdapterPosition()).getTaskId());
                }
            });
        }
    }

    public interface ClickListener {
        void launchIntent(int id);
    }
}
