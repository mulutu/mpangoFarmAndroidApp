package com.vogella.android.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity  {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private static final String SPF_NAME = "vidslogin"; //  <--- Add this
    private static final String USERNAME = "username";  //  <--- To save username
    private static final String PASSWORD = "password";  //  <--- To save password
    private String SHARED_PREFERENCES_KEY_REMEMBER_ACCOUNT = "rememberAccount";

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // check remember me feature:
        if(checkIfActiveUser()){
            finish();
        }else {

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
    }

    public Boolean checkIfActiveUser(){
        Boolean activeStatus =  false;
        //  ADD THIS  TO  READ  SAVED  username & password  NEXT-TIME OPENING Application
        SharedPreferences loginPreferences = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        _emailText.setText(loginPreferences.getString(USERNAME, ""));
        _passwordText.setText(loginPreferences.getString(PASSWORD, ""));

        return activeStatus;
    }

    // Write data to SharedPreferences object.
    private void writeToSharedPreferences(Context context, String userName, String password, boolean rememberAccount){
        // Get SharedPreferences object, the shared preferences file name is this activity class name.
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPF_NAME, MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, userName);
        editor.putString(PASSWORD, password);
        editor.putBoolean(SHARED_PREFERENCES_KEY_REMEMBER_ACCOUNT, rememberAccount);

        editor.apply();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        volleyJsonObjectRequest(email, password);

   }

    public void volleyJsonObjectRequest(String email, String password){

        String url = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/login/";

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
                        Long userId;

                        try {
                            userType = response.getString("userType");
                            userEnabled = response.getBoolean("enabled");
                            userId = response.getLong("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("LOGIN JSON E", e.getMessage());
                        }

                        if(userEnabled){
                            Log.d("LOGIN ENABLED ?", userType);
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
                progressDialog.hide();
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
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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
}
