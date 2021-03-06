package com.vogella.android.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class AddProjectActivity extends AppCompatActivity {
    private EditText _projectName, _farmName, _projectDesc;
    private Button _btnSubmitProject;
    Project project = new Project();
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        populateTitleBar();

        userSession();

        prepareView();

        manageVariables();
    }

    private void prepareView(){
        _projectName = (EditText) findViewById(R.id.projectName);
        _farmName = (EditText) findViewById(R.id.projectFarm);
        _farmName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                selectFarm();
            }
        });
        _projectDesc = (EditText) findViewById(R.id.projectDesc);
        _btnSubmitProject = (Button) findViewById(R.id.btnSubmitProject);
        _btnSubmitProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProject();
            }
        });
    }

    private void userSession(){
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();
        userId = user.getId();
    }

    private void manageVariables(){
        Intent intent = getIntent();
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras.get("Process").equals("NEW_PROJECT")) {
                if (getIntent().getSerializableExtra("Project") != null) {
                    project = (Project) getIntent().getSerializableExtra("Project");
                    populateData(project);
                }
            }
        }
    }

    private void populateTitleBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Add Project");
        }
    }

    private void populateData(Project project){
        if(project.getProjectName() != "") {
            _projectName.setText(project.getProjectName());
        }
        if(project.getFarmName() != "") {
            _farmName.setText(project.getFarmName());
        }
        _farmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFarm();
            }
        });
        if(project.getDescription() != "") {
            _projectDesc.setText(project.getDescription());
        }
    }

    public void selectFarm(){
        Bundle extras = new Bundle();
        extras.putSerializable("Project", getProject());
        extras.putString("Process", "NEW_PROJECT");

        Intent intent = new Intent(getApplicationContext(), SelectFarmActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private Project getProject(){
        int unitID =  1;
        if( _projectName.getText() != null) {
            String projName = _projectName.getText().toString();
            project.setProjectName(projName);
        }
        if( _projectDesc.getText() != null) {
            String projDesc = _projectDesc.getText().toString();
            project.setDescription(projDesc);
        }
        project.setUserId(userId);
        project.setUnitId(unitID);
        return project;
    }

    private boolean validate(){
        boolean valid = true;
        String name = _projectName.getText().toString();
        String desc = _projectDesc.getText().toString();

        if (name.isEmpty()) {
            _projectName.setError("enter name of project");
            valid = false;
        } else {
            _projectName.setError(null);
        }
        if (desc.isEmpty()) {
            _projectDesc.setError("enter description");
            valid = false;
        } else {
            _projectDesc.setError(null);
        }
        return valid;
    }

    private void onValidateFailed(){
        _btnSubmitProject.setEnabled(true);
    }

    private void submitProject(){
        _btnSubmitProject.setEnabled(false);
        if (!validate()) {
            onValidateFailed();
            return;
        }
        String URL = "http://45.56.73.81:8084/Mpango/api/v1/projects";
        project = getProject();
        JSONObject postparams = new JSONObject();
        try {
            postparams.put("userId", project.getUserId()); //sdjf hsdjksdfhjhsdfj
            postparams.put("farmId", project.getFarmId());
            postparams.put("projectName", project.getProjectName());
            postparams.put("description", project.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(AddProjectActivity.this, R.style.AppTheme_Dark_Dialog);
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
