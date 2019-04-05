package com.vogella.android.myapplication.activity.tasks;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.AddProjectActivity;
import com.vogella.android.myapplication.activity.CalendarActivity;
import com.vogella.android.myapplication.activity.MainActivity;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Task;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskNoteActivity extends AppCompatActivity {

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;
    private Toolbar toolbar;


    private EditText _taskName;
    private EditText _taskDate;
    private EditText _taskDescription;
    private Button _btnSubmitTask, _btnDeleteTask;

    private Task task =  new Task();

    boolean isNewTask = false;

    private String process = "";

    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_note);

        populateTitleBar();

        userSession();

        manageView();

        manageVariables();
    }

    private void manageVariables(){
        /*int taskId = getIntent().getIntExtra("id", -100);
        if (taskId == -100) {
            isNewTask = true;
        }*/
        if (!isNewTask) {
            task = (Task) getIntent().getSerializableExtra("Task");
            populateData();
            _btnDeleteTask.setVisibility(View.VISIBLE);
        }else{
            int projectId = getIntent().getIntExtra("projectId", 0);
            task.setProjectId(projectId);
        }
    }

    private void populateData(){
        if(task.getTaskName()!=null) {
            _taskName.setText(task.getTaskName().toString());
        }
        if(task.getTaskDate()!=null) {
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
            String dateStr = format2.format(task.getTaskDate());
            _taskDate.setText(dateStr);
        }
        _taskDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayDatePicker();
            }
        });
        if(task.getDescription()!=""){
            _taskDescription.setText(task.getDescription());
        }
    }

    private void manageView(){
        _taskName = (EditText) findViewById(R.id.inTitle);
        _taskDate = (EditText) findViewById(R.id.inDate);
        _taskDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayDatePicker();
            }
        });
        _taskDescription = (EditText) findViewById(R.id.inDescription);
        _btnDeleteTask = (Button) findViewById(R.id.btnDelete);
        _btnDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });
        _btnSubmitTask = (Button) findViewById(R.id.btnDone);
        _btnSubmitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNewTask) {
                    updateTask();
                }else{
                    submitTask();
                }
            }
        });
    }

    private void deleteTask(){
        _btnDeleteTask.setEnabled(false);
        task = getTask();
        String URL = "http://45.56.73.81:8084/Mpango/api/v1/tasks/" + task.getTaskId();
        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(TaskNoteActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(
                Request.Method.DELETE,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int status = response.getInt("status");
                            String message = response.getString("message");
                            if(status == 0 && message.equalsIgnoreCase("CREATED")){
                                //progressDialog.hide();
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LOGIN RESPONSE", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    private boolean validate(){
        boolean valid = true;
        String name = _taskName.getText().toString();
        String date = _taskDate.getText().toString();
        String desc = _taskDescription.getText().toString();

        if (name.isEmpty()) {
            _taskName.setError("enter Task name");
            valid = false;
        } else {
            _taskName.setError(null);
        }
        if (date.isEmpty()) {
            _taskDate.setError("enter date");
            valid = false;
        } else {
            _taskDate.setError(null);
        }
        if (desc.isEmpty()) {
            _taskDescription.setError("enter description");
            valid = false;
        } else {
            _taskDescription.setError(null);
        }
        return valid;
    }

    private Date stringToDate(String dateStr){
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Task getTask() {
        if( _taskName.getText() != null) {
            task.setTaskName(_taskName.getText().toString());
        }
        if( _taskDate.getText() != null) {
            String dateStr = _taskDate.getText().toString();
            Date strTrxDate = stringToDate(dateStr);
            task.setTaskDate(strTrxDate);
        }
        if( _taskDescription.getText() != null) {
            task.setDescription(_taskDescription.getText().toString());
        }
        return task;
    }

    private void onValidateFailed(){
        _btnSubmitTask.setEnabled(true);
    }

    private String dateToString(Date date){
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        return format2.format(date);
    }

    private void updateTask(){
        _btnSubmitTask.setEnabled(false);
        if (!validate()) {
            onValidateFailed();
            return;
        }
        String URL = "http://45.56.73.81:8084/Mpango/api/v1/tasks";
        task = getTask();
        JSONObject postparams = new JSONObject();
        try {
            postparams.put("taskId", task.getTaskId()); //sdjf hsdjksdfhjhsdfj
            postparams.put("projectId", task.getProjectId()); //sdjf hsdjksdfhjhsdfj
            postparams.put("taskName", task.getTaskName());
            postparams.put("description", task.getDescription());
            postparams.put("taskDate", dateToString(task.getTaskDate()));
            postparams.put("priority", 1);
            postparams.put("active", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(TaskNoteActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int status = response.getInt("status");
                            String message = response.getString("message");
                            if(status == 0 && message.equalsIgnoreCase("CREATED")){
                                //progressDialog.hide();
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LOGIN RESPONSE", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    private void submitTask(){
        _btnSubmitTask.setEnabled(false);
        if (!validate()) {
            onValidateFailed();
            return;
        }
        String URL = "http://45.56.73.81:8084/Mpango/api/v1/tasks";
        task = getTask();
        JSONObject postparams = new JSONObject();
        try {
            postparams.put("projectId", task.getProjectId()); //sdjf hsdjksdfhjhsdfj
            postparams.put("taskName", task.getTaskName());
            postparams.put("description", task.getDescription());
            postparams.put("taskDate", dateToString(task.getTaskDate()));
            postparams.put("priority", 1);
            postparams.put("active", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(TaskNoteActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int status = response.getInt("status");
                            String message = response.getString("message");
                            if(status == 0 && message.equalsIgnoreCase("CREATED")){
                                //progressDialog.hide();
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LOGIN RESPONSE", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void displayDatePicker(){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(TaskNoteActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        _taskDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, year, month, day);
        picker.show();
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

            int taskId = getIntent().getIntExtra("id", -100);
            if (taskId == -100) {
                isNewTask = true;
            }

            if (!isNewTask) {
                actionBar.setTitle("Edit Task");
            }else{
                actionBar.setTitle("New Task");
            }
        }
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
}
