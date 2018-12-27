package com.vogella.android.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_LONG;


public class ExpenseActivity extends AppCompatActivity implements OnItemSelectedListener{

    private static final String TAG = "ExpenseActivity";

    private DatePicker datePicker;
    DatePickerDialog datePickerDialog;
    private Calendar calendar;
    //private TextView dateView;
    private int year, month, day;
    //private String selectedItem;

    String[] suppliers;

    //Spinner Supplier;

    @BindView(R.id.expenseAmount)  EditText _expenseAmount;
    @BindView(R.id.expenseDate) EditText _expenseDate;
    @BindView(R.id.Supplier) Spinner _supplier;

    @BindView(R.id.btnSubmitExpense) Button _btnSubmitExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        ButterKnife.bind(this);

        //Supplier = (Spinner)findViewById(R.id.Supplier);
        String[] objects = { "January", "Feburary", "March", "April", "May",
                "June", "July", "August", "September", "October", "November","December" };



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
                                _expenseDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // supplier dropdown
        //String[] items = new  String[]{"1", "2", "three"};
        // Spinner click listener
       // _supplier.setOnItemSelectedListener(this);

        int userID = 1;
        //List<String> list = getListOfSuppliers(userID);
        //String[] objectsx = getListOfSuppliers(userID);
        // Creating adapter for spinner
        ArrayAdapter dataAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, objects);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        //_supplier.setAdapter(dataAdapter);
        _supplier.setAdapter(dataAdapter);
        Toast.makeText(this, "Selected-subb: " + suppliers.length, LENGTH_LONG).show();

        _supplier.setOnItemSelectedListener(this);

        _btnSubmitExpense.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitExpense();
            }
        });

        //_supplier.setOnItemSelectedListener(new CustomOnItemSelectedListener());


    }

    public String[] getListOfSuppliers(int userID){

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
                        //String[] suppliers;
                        //suppliers = new String[response.length()];
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);
                                supplierName = supplier.getString("supplierNames");
                                supplierId = supplier.getInt("id");
                                Log.d(_TAG, supplierId + " " + supplierName);
                                suppliers[i] = supplierName;
                            }
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

        return suppliers;
    }


    public void submitExpense() {
        Log.d(TAG, "submitExpense");

        _btnSubmitExpense.setEnabled(false);

        String expenseAmount = _expenseAmount.getText().toString();
        String expenseDate =  _expenseDate.getText().toString();
       // String supplierName = selectedItem;

        //Toast.makeText(this, "Selected-subb: " + supplierName, LENGTH_LONG).show();

        Log.d(TAG, "expenseAmount=" +  expenseAmount);
        Log.d(TAG, "expenseDate=" +  expenseDate);
        //Log.d(TAG, "supplierName=" +  selectedItem);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "Selected-subb: " + _supplier.getItemAtPosition(position).toString(), LENGTH_LONG).show();
        //selectedItem = Supplier.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, LENGTH_LONG).show();
        Log.d(TAG, "SELECT=" +  "Selected: " + item );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), "Selected: NOTHING ", LENGTH_LONG).show();
        Log.d(TAG, "SELECT=" +  "Selected: NOTHING");
    }*/
}
