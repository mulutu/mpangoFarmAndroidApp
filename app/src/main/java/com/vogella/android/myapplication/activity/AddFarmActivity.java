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
import com.vogella.android.myapplication.model.Farm;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFarmActivity extends AppCompatActivity {

    private EditText _farmName, _farmDesc;
    private Button _btnSubmitFarm;
    Farm farm = new Farm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_farm);

        _farmName = (EditText) findViewById(R.id.txtFarmName);
        _farmDesc = (EditText) findViewById(R.id.farmNotes);
        _btnSubmitFarm = (Button) findViewById(R.id.btnSubmitFarm);
        _btnSubmitFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFarm();
            }
        });
    }

    private Farm getFarm(){

        Farm _farm = new Farm();

        int userID = 1;
        int size = 1;
        String location = "TBA";

        String Name =  _farmName.getText().toString();
        String Desc =  _farmDesc.getText().toString();

        _farm.setUserId(userID);
        _farm.setFarmName(Name);
        _farm.setSize(size);
        _farm.setLocation(location);
        _farm.setDescription(Desc);

        return _farm;
    }

    private void submitFarm(){

        String URL = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/farm/create/";

        farm = getFarm();

        // int userId, String farmName, int size, String location, String description
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

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

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
