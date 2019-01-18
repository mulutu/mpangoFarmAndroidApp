package com.vogella.android.myapplication.activity;

import android.app.DatePickerDialog;
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
import android.widget.Toast;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.R;

import android.widget.AdapterView.OnItemSelectedListener;

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
    private int _accountId, POS;
    private String _accountName;
    private int userID = 1;
    private static final int REQUEST_CALENDAR = 0;

    Map<Integer, Integer> accountsMap =  new HashMap<>();
    Map<Integer, Integer> suppliersMap =  new HashMap<>();
    Map<Integer, Integer> projectsMap =  new HashMap<>();

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String datePickedStr = extras.getString("dateStr");
            _expenseDate.setText(datePickedStr);
        }

        // Expense date DatePicker
        _expenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.Supplier) {
            POS = position;
            _supplierId = suppliersMap.get(position);
            _supplierName = _supplier.getItemAtPosition(position).toString();
        }else if(parent.getId() == R.id.txtProjectID){
            POS = position;
            _projectId = projectsMap.get(position);
            _projectName = _projects.getItemAtPosition(position).toString();
        }else if(parent.getId() == R.id.txtAccountID){
            POS = position;
            _accountId = accountsMap.get(position);
            //_accountId = position;
            _accountName = _accounts.getItemAtPosition(position).toString();
        }
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
                        List<String> objectsx =  new ArrayList<String>();
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                supplierName = supplier.getString("supplierNames");
                                supplierId = supplier.getInt("id");
                                objectsx.add(supplierName);
                                suppliersMap.put(i,supplierId);
                            }
                            spinnerSuppliersArray(objectsx);
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

    public void spinnerSuppliersArray(List<String> objects){
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_text2, objects);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown2);
        _supplier.setAdapter(dataAdapter);
        _supplier.setOnItemSelectedListener(this); // hsd fsdk fjsdh jkf hsdhf sdj fhsdjhkf sdjfh sdjk fhsdhfjsd sdfh fhsdjkf sdjkf sdjhdfjk
    }

    public void submitExpense() {
        Log.d(TAG, "submitExpense");
        String URL_ADD_EXPENSE = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/expense/";
        //_btnSubmitExpense.setEnabled(false);
        BigDecimal expenseAmount = new BigDecimal(_expenseAmount.getText().toString());
        String expenseDateStr = _expenseDate.getText().toString();
        int supplierId =  _supplierId;
        String expNotes = " test desc " + _supplierName;

        // Toast
        Toast.makeText(getApplicationContext(),"ACCOUNTS:  _accountName->" + _accountName + " _accountId->>" + _accountId, Toast.LENGTH_LONG).show();

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



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void displayDatePicker(){
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
                        _expenseDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        // create a view with calendar
        // Start the Signup activity
        //Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        //startActivityForResult(intent, REQUEST_CALENDAR);
        //finish();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
