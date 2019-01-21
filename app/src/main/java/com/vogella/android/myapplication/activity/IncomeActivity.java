package com.vogella.android.myapplication.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatePicker datePicker;
    DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int year, month, day;

    private int userID = 1;
    private int _customerID, POS,  _projectId, _accountId;
    private String _customerName, _projectName, _accountName;

    Map<Integer, Integer> accountsMap =  new HashMap<>();
    Map<Integer, Integer> customersMap =  new HashMap<>();
    Map<Integer, Integer> projectsMap =  new HashMap<>();

    @BindView(R.id.txtIncomeAmount) EditText _txtIncomeAmount;
    @BindView(R.id.txtIncomeDate) EditText _txtIncomeDate;
    @BindView(R.id.spCustomerID) Spinner _spCustomerID;
    @BindView(R.id.spProjectID) Spinner _spProjectID;
    @BindView(R.id.spAccountID) Spinner _spAccountID;
    @BindView(R.id.btnSubmitIncome) Button _btnSubmitIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        ButterKnife.bind(this);

        _txtIncomeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });

        getListOfProjects(userID);
        getListOfCustomers(userID);
        getListOfAccounts(userID);

        _btnSubmitIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitIncome();
            }
        });
    }
    public void getListOfAccounts(int userID){
        String URL_ACCOUNTS = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/coa/types/";
        final String  _TAG = "EXPENSE ACCOUNTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_ACCOUNTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String accountName = "";
                        int accountId;
                        List<String> objectsx =  new ArrayList<String>();
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                accountName = supplier.getString("accountTypeName");
                                accountId = supplier.getInt("id");
                                objectsx.add(accountName);
                                accountsMap.put(i,accountId);
                            }
                            spinnerAccountsArray(objectsx);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }

    public void spinnerAccountsArray(List<String> objects){
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_text2, objects);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown2);
        _spAccountID.setAdapter(dataAdapter);
        _spAccountID.setOnItemSelectedListener(this);
    }

    public void getListOfCustomers(int userID){
        String URL_INCOMES = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/customers/user/" + userID;
        final String  _TAG = "INCOME: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_INCOMES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String customerName = "";
                        int supplierId;
                        List<String> objectsx =  new ArrayList<String>();
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                customerName = supplier.getString("customerNames");
                                supplierId = supplier.getInt("id");
                                objectsx.add(customerName);
                                customersMap.put(i,supplierId);
                            }
                            spinnerCustomersArray(objectsx);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }

    public void spinnerCustomersArray(List<String> objects){
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_text2, objects);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown2);
        _spCustomerID.setAdapter(dataAdapter);
        _spCustomerID.setOnItemSelectedListener(this);
    }

    public void getListOfProjects(int userID){
        String URL_PROJECTS = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/projects/user/" + userID;
        final String  _TAG = "PROJECTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String projectName = "";
                        int projectId;
                        List<String> objectsx =  new ArrayList<String>();
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                projectName = supplier.getString("projectName");
                                projectId = supplier.getInt("id");
                                objectsx.add(projectName);
                                projectsMap.put(i,projectId);
                            }
                            spinnerProjectsArray(objectsx);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }

    public void spinnerProjectsArray(List<String> objects){
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_text2, objects);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown2);
        _spProjectID.setAdapter(dataAdapter);
        _spProjectID.setOnItemSelectedListener(this);
    }

    public void displayDatePicker(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(IncomeActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        _txtIncomeDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spCustomerID) {
            POS = position;
            _customerID = customersMap.get(position);
            _customerName = _spCustomerID.getItemAtPosition(position).toString();
        }else if(parent.getId() == R.id.spProjectID){
            POS = position;
            _projectId = projectsMap.get(position);
            _projectName = _spProjectID.getItemAtPosition(position).toString();
        }else if(parent.getId() == R.id.spAccountID){
            POS = position;
            _accountId = accountsMap.get(position);
            _accountName = _spAccountID.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void submitIncome() {
        String URL_ADD_EXPENSE = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/income/";
        //_btnSubmitExpense.setEnabled(false);
        BigDecimal incomeAmount = new BigDecimal(_txtIncomeAmount.getText().toString());
        String incomeDateStr = _txtIncomeDate.getText().toString();
        //int customerId =  _customerID;
        String incomeNotes = " test income desc ";

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("incomeDate", incomeDateStr); //sdjf hsdjksdfhjhsdfj
            postparams.put("amount", incomeAmount);
            postparams.put("customerId", _customerID);
            postparams.put("paymentMethodId", 1);
            postparams.put("accountId", _accountId);
            postparams.put("projectId", _projectId);
            postparams.put("notes", incomeNotes);
            postparams.put("userId", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(IncomeActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(
                Request.Method.POST,
                URL_ADD_EXPENSE,
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
