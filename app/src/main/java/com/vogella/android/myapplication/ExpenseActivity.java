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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenseActivity extends AppCompatActivity {

    private static final String TAG = "ExpenseActivity";

    private DatePicker datePicker;
    DatePickerDialog datePickerDialog;
    private Calendar calendar;
    //private TextView dateView;
    private int year, month, day;

    private Spinner Supplier;

    @BindView(R.id.expenseAmount)  EditText _expenseAmount;
    @BindView(R.id.expenseDate) EditText _expenseDate;
    @BindView(R.id.Supplier) Spinner _supplier;
    @BindView(R.id.btnSubmitExpense) Button _btnSubmitExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        ButterKnife.bind(this);

        _btnSubmitExpense.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitExpense();
            }
        });

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
        String[] items = new String[]{"1", "2", "three"};
        int userID = 1;
        getListOfSuppliers(userID);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        _supplier.setAdapter(adapter);

    }

    public void getListOfSuppliers(int userID){
        String URL_SUPPLIERS = "http://localhost:8084/MpangoFarmEngineApplication/api/financials/suppliers/user/" + userID;

        final String  _TAG = "EXPENSE: ";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.GET, URL_SUPPLIERS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LOGIN RESPONSE", response.toString());

                        String supplierName = "";
                        int supplierId;
                        Long userId;

                        try {
                            supplierName = response.getString("supplierNames");
                            supplierId = response.getInt("id");
                            Log.d(_TAG, supplierId + " " + supplierName);
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
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,_TAG);
    }


    public void submitExpense() {
        Log.d(TAG, "submitExpense");

        _btnSubmitExpense.setEnabled(false);

        String expenseAmount = _expenseAmount.getText().toString();
        String expenseDate =  _expenseDate.getText().toString();

        Log.d(TAG, "expenseAmount=" +  expenseAmount);
        Log.d(TAG, "expenseDate=" +  expenseDate);
    }
}
