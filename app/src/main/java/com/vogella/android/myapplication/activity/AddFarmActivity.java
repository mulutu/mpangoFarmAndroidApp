package com.vogella.android.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.vogella.android.myapplication.model.Farm;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFarmActivity extends AppCompatActivity {

    private EditText _farmName, _farmDesc, _farmLocation, _farmSize;
    private Button _btnSubmitFarm;
    Farm farm;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;
    private Toolbar toolbar;

    private String process = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_farm);

        pupolateToolBar();

        userSession();

        manageVariables();

        prepareView();
    }

    private void userSession(){
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();
        userId = user.getId();
    }

    private void manageVariables(){
        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(bundle.get("Process").equals("INITIAL_ADD_FARM")){
                process = "INITIAL_ADD_FARM";
            }else if(bundle.get("Process").equals("ADD_FARM")){
                process = "ADD_FARM";
            }
        }
    }

    private void prepareView(){
        _farmName = (EditText) findViewById(R.id.farmName);
        _farmLocation = (EditText) findViewById(R.id.farmLocation);
        _farmSize = (EditText) findViewById(R.id.farmSize);
        _farmDesc = (EditText) findViewById(R.id.farmNotes);
        _btnSubmitFarm = (Button) findViewById(R.id.btnSubmitFarm);
        _btnSubmitFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFarm();
            }
        });
    }

    private void pupolateToolBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Add Farm");
        }
    }

    private void processResponseData(){
        if(process.equalsIgnoreCase("INITIAL_ADD_FARM")){
            Bundle bundle = new Bundle();
            bundle.putString("Process", "INITIAL_ADD_FARM");
            bundle.putBoolean("HasFarms", true);

            Intent i = new Intent(getApplicationContext(), AddProjectActivity.class);
            i.putExtras(bundle);
            startActivity(i);
            finish();
        }else{
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Farm getFarm(){
        Farm _farm = new Farm();
        String Name =  _farmName.getText().toString();
        String location =  _farmLocation.getText().toString();
        String sizeStr =  _farmSize.getText().toString();
        int size = Integer.parseInt(sizeStr);
        String Desc =  _farmDesc.getText().toString();

        _farm.setUserId(userId);
        _farm.setFarmName(Name);
        _farm.setSize(size);
        _farm.setLocation(location);
        _farm.setDescription(Desc);

        return _farm;
    }

    private boolean validate(){
        boolean valid = true;
        String name = _farmName.getText().toString();
        String location = _farmLocation.getText().toString();
        String size = _farmSize.getText().toString();
        String desc = _farmDesc.getText().toString();
        if (name.isEmpty()) {
            _farmName.setError("enter name of farm");
            valid = false;
        } else {
            _farmName.setError(null);
        }
        if (location.isEmpty()) {
            _farmLocation.setError("enter location");
            valid = false;
        } else {
            _farmLocation.setError(null);
        }
        if (size.isEmpty()) {
            _farmSize.setError("enter size of farm");
            valid = false;
        } else {
            _farmSize.setError(null);
        }
        if (desc.isEmpty()) {
            _farmDesc.setError("enter description");
            valid = false;
        } else {
            _farmDesc.setError(null);
        }
        return valid;
    }

    private void onValidateFailed(){
        _btnSubmitFarm.setEnabled(true);
    }

    private void submitFarm(){
        _btnSubmitFarm.setEnabled(false);
        if (!validate()) {
            onValidateFailed();
            return;
        }
        String URL = "http://45.56.73.81:8084/Mpango/api/v1/farms";
        farm = getFarm();
        JSONObject postparams = new JSONObject();
        try {
            postparams.put("userId", farm.getUserId());
            postparams.put("farmName", farm.getFarmName());
            postparams.put("size", farm.getSize());
            postparams.put("location", farm.getLocation());
            postparams.put("description", farm.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String  REQUEST_TAG = "com.mpango.android.volleyJsonObjectRequest";
        final ProgressDialog progressDialog = new ProgressDialog(AddFarmActivity.this, R.style.AppTheme_Dark_Dialog);
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
                                processResponseData();
                                //progressDialog.hide();
                                progressDialog.dismiss();
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
