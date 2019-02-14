package com.vogella.android.myapplication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Income;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeViewActivity extends AppCompatActivity {

    final String  _TAG = "INCOMEVEVIEW: ";
    final String  TAG = "REQUEST_QUEUE";

    int transactionID = 0;

    Income income  = new Income();

    DatePickerDialog datePickerDialog;

    private EditText _amount;
    private EditText _date;
    private EditText _customer;
    private EditText _project;
    private EditText _account;
    private EditText _notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_view);
        //ButterKnife.bind(this);


        transactionID = this.getIntent().getExtras().getInt("transactionID");


        _amount = (EditText) findViewById(R.id.amount);
        _date = (EditText) findViewById(R.id.date);
        _customer = (EditText) findViewById(R.id.customer);
        _project = (EditText) findViewById(R.id.project);
        _account = (EditText) findViewById(R.id.account);
        _notes = (EditText) findViewById(R.id.notes);



        Bundle extras = getIntent().getExtras();

        if ( getIntent().getSerializableExtra("income") != null ) {
            String datePickedStr = extras.getString("dateStr");
            _date.setText(datePickedStr);

            income = (Income) getIntent().getSerializableExtra("income");

            //transactionID = income.getId();

        }else{
            getTransactionDetails(transactionID);
        }

        populateData(income);
    }

    private void populateData(Income income){
        _amount.setText( income.getAmount().toString());

        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        String dateStr = format2.format(income.getIncomeDate());
        _date.setText( dateStr);

        // Expense date DatePicker
        _date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });

        _customer.setText(income.getCustomer());
        _project.setText(income.getProjectName());
        _account.setText(income.getAccount());
        _notes.setText(income.getNotes());

        Log.d(_TAG, "income.getProjectName(): " + income.getProjectName());
    }

    public void displayDatePicker(){
        // calender class's instance and get current date , month and year from calender
        //final Calendar c = Calendar.getInstance();
        //int mYear = c.get(Calendar.YEAR); // current year
        //int mMonth = c.get(Calendar.MONTH); // current month
        //int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        //// date picker dialog
        //datePickerDialog = new DatePickerDialog(IncomeViewActivity.this,
               // new DatePickerDialog.OnDateSetListener() {
                   // @Override
                   // public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                       // // set day of month , month and year value in the edit text
                       // _date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                  //  }
               // }, mYear, mMonth, mDay);
       // datePickerDialog.show();
        // create a view with calendar
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        intent.putExtra("income", income);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void getTransactionDetails( int trxID){
        String URL_ = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/income/" + trxID;

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {
                        if (response != null ) {
                            try {
                                String dateStr = response.getString("incomeDate"); // "expenseDate": "30-07-2018",
                                try {
                                    Date transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                    income.setIncomeDate(transDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                income.setId(response.getInt("id"));

                                BigDecimal amount_ = new BigDecimal(response.getString("amount"));
                                income.setAmount(amount_);

                                income.setCustomerId(response.getInt("customerId"));
                                income.setCustomer(response.getString("customer"));

                                income.setFarmName(response.getString("farmName"));

                                income.setPaymentMethodId(response.getInt("paymentMethodId"));
                                income.setPaymentMethod(response.getString("paymentMethod"));

                                income.setProjectId(response.getInt("projectId"));
                                income.setProjectName(response.getString("projectName"));

                                income.setNotes(response.getString("notes"));

                                income.setAccountId(response.getInt("accountId"));
                                income.setAccount(response.getString("account"));

                                income.setCustomerId(response.getInt("customerId"));
                                income.setCustomer(response.getString("customer"));

                                income.setUserId(response.getInt("userId"));


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(_TAG, e.getMessage());
                            }

                        }else{}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,TAG);
    }

}
