package com.vogella.android.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Project;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.MyViewHolder> {

    private List<Project> projectsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView listProjectDescription, listProjectName;

        public MyViewHolder(View view) {
            super(view);
            listProjectName = (TextView) view.findViewById(R.id.listProjectName);
            listProjectDescription = (TextView) view.findViewById(R.id.listProjectDescription);
        }
    }


    public ProjectsAdapter(List<Project> projectsList) {
        this.projectsList = projectsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.projects_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Project project = projectsList.get(position);
        holder.listProjectName.setText(project.getProjectName());
        holder.listProjectDescription.setText(project.getDescription());
    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }
}
