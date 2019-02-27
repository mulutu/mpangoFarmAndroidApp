package com.vogella.android.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class AddProjectActivity extends AppCompatActivity {

    private EditText _projectName, _farmName, _projectDesc;
    private Button _btnSubmitProject;
    Project project = new Project();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        _projectName = (EditText) findViewById(R.id.projectName_add_project);
        _farmName = (EditText) findViewById(R.id.txtFarm_add_project);

        _farmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFarm();
            }
        });
        _projectDesc = (EditText) findViewById(R.id.projectDesc_add_project);

        _btnSubmitProject = (Button) findViewById(R.id.btnSubmitProject);
        _btnSubmitProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProject();
            }
        });
    }

    public void selectFarm(){
        Bundle extras = new Bundle();
        extras.putSerializable("project", getProject());
        extras.putString("transactionType", "ADD_PROJECT");

        Intent intent = new Intent(getApplicationContext(), FarmsViewActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private Project getProject(){

        Project prj = new Project();

        int userID = 1;
        int farmID =  1;
        int unitID =  1;

        String projName =  _projectName.getText().toString();
        String projDesc =  _projectDesc.getText().toString();

        prj.setUserId(userID);
        //prj.setFarmId(farmID);
        prj.setProjectName(projName);
        prj.setDescription(projDesc);
        prj.setFarmId(project.getFarmId());
        prj.setUnitId(unitID);

        return prj;
    }

    private void submitProject(){

        String URL = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/project/create/";

        project = getProject();

        // int userId, int farmId, String projectName, String description
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
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);

    }
}
