package com.vogella.android.myapplication.activity.tasks;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.AddTransactionActivity;
import com.vogella.android.myapplication.activity.CalendarActivity;
import com.vogella.android.myapplication.activity.EditTransactionActivity;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.adapter.projectsAdapter;
import com.vogella.android.myapplication.adapter.taskAdapter;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Task;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.util.RecyclerTouchListener;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements taskAdapter.ClickListener {

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;
    private Toolbar toolbar;
    private Project project;
    private String process ="";
    private int projectId;

    private ArrayList<Task> taskList = new ArrayList<>();

    private RecyclerView recyclerView;
    private taskAdapter mAdapter;

    public static final int NEW_TODO_REQUEST_CODE = 200;
    public static final int UPDATE_TODO_REQUEST_CODE = 300;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        populateTitleBar();

        userSession();

        prepareView();

        manageVariables();

        getListOfTasks(projectId);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("projectId", projectId );

                Intent intent = new Intent(TaskActivity.this, TaskNoteActivity.class);
                intent.putExtras(extras);

                startActivityForResult(intent, NEW_TODO_REQUEST_CODE);
            }
        });
    }

    private void manageVariables(){
        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getSerializable("Project").equals(null)){
                project = (Project)bundle.getSerializable("Project");
                projectId = project.getId();
            }
            if(bundle.get("Process").equals("NEW_PROJECT")){
                process = "NEW_PROJECT";
            }else if(bundle.get("Process").equals("EDIT_PROJECT")){
                process = "EDIT_PROJECT";
            }
        }
    }

    private void userSession(){
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();
        userId = user.getId();
    }

    private void populateTitleBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Project Planner");
        }
    }

    private void prepareView(){
        recyclerView = (RecyclerView) findViewById(R.id.projects_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new taskAdapter( taskList, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void prepareTasksData() {
        mAdapter.notifyDataSetChanged();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Task task = taskList.get(position);

                Bundle extras = new Bundle();
                extras.putSerializable("Task", task );
                extras.putInt("id", task.getTaskId() );
                extras.putSerializable("Process", "EDIT_TASK" );

                Intent intent = new Intent(TaskActivity.this, TaskNoteActivity.class);
                intent.putExtras(extras);

                startActivityForResult( intent, UPDATE_TODO_REQUEST_CODE);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    public void getListOfTasks(int projectId){
        String URL_PROJECTS = "http://45.56.73.81:8084/Mpango/api/v1/projects/" + projectId + "/tasks";
        final String  _TAG = "LIST OF TASKS: ";
        CustomJsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject projObj = response.getJSONObject(i);

                                int taskId = projObj.getInt("taskId");
                                int projectId = projObj.getInt("projectId");
                                String taskName = projObj.getString("taskName");
                                String description = projObj.getString("description");
                                int priority = projObj.getInt("priority");
                                boolean active = projObj.getBoolean("active");

                                String dateStr = projObj.getString("taskDate"); // "expenseDate": "30-07-2018",
                                Date taskDate = null;
                                try {
                                    taskDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Task task = new Task();
                                task.setTaskId(taskId);
                                task.setProjectId(projectId);
                                task.setTaskName(taskName);
                                task.setTaskDate(taskDate);
                                task.setDescription(description);
                                task.setPriority(priority);
                                task.setActive(active);

                                /*
                                {
        "taskId": 1,
        "projectId": 2,
        "taskName": "test task 11 update",
        "description": "test task 1 desc",
        "taskDate": "30-09-2018",
        "priority": 1,
        "active": true
    }
                                */

                                taskList.add(task);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }
                        prepareTasksData();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_logout:
                session.logoutUser();

                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;

            case R.id.action_favorite:
                Toast.makeText(this, "Action clicked", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void launchIntent(int id) {
        /*Task task = taskList.get(id);

        Bundle extras = new Bundle();
        extras.putSerializable("Task", task );
        extras.putInt("id", task.getTaskId() );
        extras.putSerializable("Process", "EDIT_TASK" );

        Intent intent = new Intent(TaskActivity.this, TaskNoteActivity.class);
        intent.putExtras(extras);

        startActivityForResult( intent, UPDATE_TODO_REQUEST_CODE);*/
        //startActivityForResult(new Intent(TaskActivity.this, TaskNoteActivity.class).putExtra("id", id), UPDATE_TODO_REQUEST_CODE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
