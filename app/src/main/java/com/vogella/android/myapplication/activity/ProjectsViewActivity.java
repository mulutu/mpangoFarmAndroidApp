package com.vogella.android.myapplication.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.adapter.projectsAdapter;
import com.vogella.android.myapplication.model.Income;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class ProjectsViewActivity extends AppCompatActivity {

    private List<Project> projectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private projectsAdapter mAdapter;

    private Income _income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_view);

        recyclerView = (RecyclerView) findViewById(R.id.projects_recycler_view);

        mAdapter = new projectsAdapter(projectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getSerializable("income").equals(null)){
                _income = (Income)bundle.getSerializable("income");
                showSnackbar( findViewById(android.R.id.content),  "INCOME: " + _income.toString() , Snackbar.LENGTH_INDEFINITE );
            }
        }
        prepareProjectsData();
    }

    public void showSnackbar(View view, String message, int duration){
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void prepareProjectsData() {
        // public Project(int userId, int farmId, String projectName, String description)

        Project project = new Project(1,1,1, "test project 1", "test project 1");
        projectList.add(project);

        project = new Project(2,1,1, "test project 2", "test project 2");
        projectList.add(project);


        mAdapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Project project = projectList.get(position);

                _income.setProjectId(project.getId());
                _income.setProjectName(project.getProjectName());

                Intent intent = new Intent(getApplicationContext(), IncomeViewActivity.class);
                Log.d("PROJ-LIST", "PROJ-LIST : " );

                Bundle extras = new Bundle();
                //extras.putSerializable("project", project);
                extras.putSerializable("income", _income);
                intent.putExtras(extras);
                finish();
                startActivity(intent);
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
