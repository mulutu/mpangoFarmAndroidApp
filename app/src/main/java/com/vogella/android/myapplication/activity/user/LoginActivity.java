package com.vogella.android.myapplication.activity.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.activity.MainActivity;
import com.vogella.android.myapplication.activity.NoFarmActivity;
import com.vogella.android.myapplication.model.Farm;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity  {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private static final String SHARED_PREFERENCES_KEY_NAME = "vidslogin"; //  <--- Add this
    private static final String SHARED_PREFERENCES_KEY_USER_ID = "userid";  //  <--- To save username
    private static final String SHARED_PREFERENCES_KEY_USERNAME = "username";  //  <--- To save username
    private static final String SHARED_PREFERENCES_KEY_PASSWORD = "password";  //  <--- To save password
    private static final String SHARED_PREFERENCES_KEY_REMEMBER_ACCOUNT = "rememberAccount";

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    private Boolean hasFarms =  false;
    private Boolean hasProjects =  false;

    private ArrayList<Farm> farmsList = new ArrayList<>();

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    String email = "";
    String password = "";
    String firstName = "";
    String lastName = "";
    int userId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        final Context context = getApplicationContext();

        // Session Manager
        session = new SessionManager(getApplicationContext());

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        //String email = _emailText.getText().toString();
        //String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        volleyJsonObjectRequest(email, password);

   }

    public void volleyJsonObjectRequest(final String email, final String password){

        String url = "http://45.56.73.81:8084/Mpango/api/v1/users/login/";

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("email", email);
            postparams.put("username", email);
            postparams.put("password", password);
            postparams.put("firstname", "");
            postparams.put("lastname", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";
        //progressDialog.setMessage("Loading...");
        //progressDialog.show();

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LOGIN RESPONSE", response.toString());

                        String userType = "";
                        Boolean userEnabled = false;

                        try {
                            userType = response.getString("userType");
                            userEnabled = response.getBoolean("enabled");
                            userId = response.getInt("id");
                            firstName = response.getString("firstName");
                            lastName = response.getString("lastName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("LOGIN JSON E", e.getMessage());
                        }

                        if(userEnabled){
                            Log.d("LOGIN ENABLED ?", userType);
                            //boolean rememberAccount = true;
                            //writeToSharedPreferences(getApplicationContext(), userId, email, password, rememberAccount);
                            onLoginSuccess();
                            progressDialog.dismiss();
                        }else{
                            Log.d("LOGIN ENABLED ?", "NOT ENABLED:::: ");
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("LOGIN ERROR", "Error: " + error.getMessage());
                Log.d("LOGIN RESPONSE", "Error: " + error.getMessage());
                //progressDialog.hide();
                progressDialog.dismiss();
                onLoginFailed();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically

                Log.d("LOGIN RESPONSE", "..after login success!!");
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        session.createLoginSession(firstName, lastName, email, userId);

        getListOfFarms(userId);


    }

    private void verifyData(){
        Log.d("MY FARMS :  ", "verifyData" );
        if(hasFarms){
            Log.d("MY FARMS : ", String.valueOf(farmsList.size()) );

            Bundle bundle = new Bundle();
            bundle.putString("Process", "LOGIN");
            bundle.putBoolean("HasFarms", true);

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtras(bundle);
            startActivity(i);
            finish();

        }else{
            Log.d("MY FARMS : ", " NO FARMSmobee" );
            //pushFragment(new HomeFragmentNoFarm(), "homefragmentNoFarm");

            Intent i = new Intent(getApplicationContext(), NoFarmActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }


    public void getListOfFarms(int userId){

        String URL_PROJECTS = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userId + "/farms";
        final String  _TAG = "LIST OF FARMS: ";
        CustomJsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response == null){
                            hasFarms = false;
                        }else{
                            hasFarms = true;
                            try {
                                for(int i=0;i<response.length();i++){
                                    JSONObject farmObj = response.getJSONObject(i);

                                    int id = farmObj.getInt("id");
                                    String description = farmObj.getString("description");
                                    String location = farmObj.getString("location");
                                    String dateCreated = farmObj.getString("dateCreated");
                                    String farmName = farmObj.getString("farmName");
                                    int userId = farmObj.getInt("userId");
                                    int size = farmObj.getInt("size");

                                    Farm farm = new Farm();

                                    farm.setId(id);
                                    farm.setLocation(location);
                                    farm.setSize(size);
                                    farm.setFarmName(farmName);
                                    farm.setDescription(description);
                                    farm.setUserId(userId);

                                    try {
                                        Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dateCreated);
                                        farm.setDateCreated(date1);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    farmsList.add(farm);
                                    /*[
                                    {
                                            "id": 1,
                                            "description": "Gachuriri Farm",
                                            "location": "Embu South, Gachuriri",
                                            "dateCreated": "2018-07-16T00:00:00.000+0000",
                                            "farmName": "Gachuriri",
                                            "userId": 1,
                                            "size": 10
                                    }
                                    ]*/
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(_TAG, "TRY ERROR" + e.getMessage());
                            }
                        }

                        verifyData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(_TAG, "Error: " + error.getMessage());
                verifyData();
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }
}
