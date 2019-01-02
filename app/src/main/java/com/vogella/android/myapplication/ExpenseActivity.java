package com.vogella.android.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_LONG;


public class ExpenseActivity extends AppCompatActivity implements OnItemSelectedListener{
    private static final String TAG = "ExpenseActivity";
    private DatePicker datePicker;
    DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int year, month, day;

    private int _supplierId;
    private String _supplierName;

    private int _projectId;
    private String _projectName;

    private int _accountId;
    private String _accountName;

    private int userID = 1;

    @BindView(R.id.expenseAmount)  EditText _expenseAmount;
    @BindView(R.id.expenseDate) EditText _expenseDate;
    @BindView(R.id.Supplier) Spinner _supplier;
    @BindView(R.id.txtProjectID) Spinner _projects;
    @BindView(R.id.txtAccountID) Spinner _accounts;

    @BindView(R.id.btnSubmitExpense) Button _btnSubmitExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        ButterKnife.bind(this);

        // Expense date DatePicker
        _expenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(ExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                //_expenseDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                _expenseDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                //sdfsdfsdfsdsd
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        getListOfSuppliers(userID);
        getListOfProjects(userID);
        getListOfAccounts(userID);
        _btnSubmitExpense.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitExpense();
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
                        String[] objects = new String[response.length()];
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                accountName = supplier.getString("accountTypeName");
                                accountId = supplier.getInt("id");
                                Log.d(_TAG, accountId + " " + accountName);
                                objects[i] = accountName;
                            }
                            spinnerAccountsArray(objects);
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

    public void spinnerAccountsArray(String[] objects){
        Toast.makeText(getApplicationContext(), "LIST ARRAY SIZE:  " + objects.length, LENGTH_LONG).show();
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, objects);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _accounts.setAdapter(dataAdapter);
        _accounts.setOnItemSelectedListener(this);
    }

    public void getListOfProjects(int userID){
        String URL_PROJECTS = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/projects/user/" + userID;
        final String  _TAG = "EXPENSE PROJECTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String projectName = "";
                        int projectId;
                        String[] objects = new String[response.length()];
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                projectName = supplier.getString("projectName");
                                projectId = supplier.getInt("id");
                                Log.d(_TAG, projectId + " " + projectName);
                                objects[i] = projectName;
                            }
                            spinnerProjectsArray(objects);
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

    public void spinnerProjectsArray(String[] objects){
        Toast.makeText(getApplicationContext(), "LIST ARRAY SIZE:  " + objects.length, LENGTH_LONG).show();
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, objects);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _projects.setAdapter(dataAdapter);
        _projects.setOnItemSelectedListener(this);
    }

    public void getListOfSuppliers(int userID){
        String URL_SUPPLIERS = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/suppliers/user/" + userID;
        final String  _TAG = "EXPENSE: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_SUPPLIERS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String supplierName = "";
                        int supplierId;
                        String[] objects = new String[response.length()];
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                supplierName = supplier.getString("supplierNames");
                                supplierId = supplier.getInt("id");
                                Log.d(_TAG, supplierId + " " + supplierName);
                                objects[i] = supplierName;
                            }
                            spinnerSuppliersArray(objects);
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

    public void spinnerSuppliersArray(String[] objects){
        Toast.makeText(getApplicationContext(), "LIST ARRAY SIZE:  " + objects.length, LENGTH_LONG).show();
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, objects);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _supplier.setAdapter(dataAdapter);
        _supplier.setOnItemSelectedListener(this);
    }

    public void submitExpense() {
        Log.d(TAG, "submitExpense");
        String URL_ADD_EXPENSE = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/expense/";
        //_btnSubmitExpense.setEnabled(false);
        BigDecimal expenseAmount = new BigDecimal(_expenseAmount.getText().toString());
        String expenseDateStr = _expenseDate.getText().toString();
        int supplierId =  _supplierId;
        String expNotes = " test desc " + _supplierName;

        /*String formattedTime = "";
        try {
            //expenseDate_=new SimpleDateFormat("yyyy-MM-dd").parse(expenseDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date expenseDate_ = sdf.parse(expenseDateStr);
            formattedTime = output.format(expenseDate_);

        } catch (ParseException e) {
            e.printStackTrace();
        } */

        Toast.makeText(this, "supplier name: " + _supplierName + " \n ID: " + _supplierId + "\n AMT: " + expenseAmount + "\n DATE: " + expenseDateStr, LENGTH_LONG).show();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("expenseDate", expenseDateStr); //sdjf hsdjksdfhjhsdfj
            postparams.put("amount", expenseAmount);
            postparams.put("supplierId", supplierId);
            postparams.put("paymentMethodId", 1);
            postparams.put("accountId", _accountId);
            postparams.put("projectId", _projectId);
            postparams.put("notes", expNotes);
            postparams.put("userId", userID);
        } catch (JSONException e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "EXPENSE POST ERROR: " + e.getMessage(), LENGTH_LONG).show();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(ExpenseActivity.this, R.style.AppTheme_Dark_Dialog);
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
                            Toast.makeText(getApplicationContext(), "EXPENSE RESPONSE: " + status + " -> " +  message, LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "EXPENSE FAIL RESPONSE: " + error.getMessage(), LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getApplicationContext(), "Selected-subb: " + _supplier.getItemAtPosition(position).toString(), LENGTH_LONG).show();
        if(parent.getId() == R.id.Supplier) {
            _supplierId = position;
            _supplierName = _supplier.getItemAtPosition(position).toString();
        }
        else if(parent.getId() == R.id.txtProjectID){
            _projectId = position;
            _projectName = _supplier.getItemAtPosition(position).toString();
        }else if(parent.getId() == R.id.txtAccountID){
            _accountId = position;
            _accountName = _supplier.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
