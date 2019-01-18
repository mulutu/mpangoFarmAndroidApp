package com.vogella.android.myapplication.activity;

import android.app.DatePickerDialog;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    Map<Integer, Integer> accountsMap =  new HashMap<>();
    Map<Integer, Integer> suppliersMap =  new HashMap<>();
    Map<Integer, Integer> projectsMap =  new HashMap<>();

    @BindView(R.id.incomeAmount) EditText _incomeAmount;
    @BindView(R.id.incomeDate) EditText _incomeDate;
    @BindView(R.id.CustomerID) Spinner _customerID;
    @BindView(R.id.txtProjectID) Spinner _projectID;
    @BindView(R.id.txtAccountID) Spinner _accountID;
    @BindView(R.id.btnSubmitIncome) Button _btnSubmitIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        ButterKnife.bind(this);

        _incomeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });

        getListOfProjects(userID);
        getListOfCustomers(userID);
    }

    public void getListOfCustomers(int userID){
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
        _customerID.setAdapter(dataAdapter);
        _customerID.setOnItemSelectedListener(this);
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
        _projectID.setAdapter(dataAdapter);
        _projectID.setOnItemSelectedListener(this);
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
                        _incomeDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
