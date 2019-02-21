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
import com.vogella.android.myapplication.model.Project;
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
    private EditText _projectID;
    private EditText _account;
    private EditText _notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_view);

        _amount = (EditText) findViewById(R.id.amount);
        _date = (EditText) findViewById(R.id.date);
        _customer = (EditText) findViewById(R.id.customer);

        _project = (EditText) findViewById(R.id.project);
        _projectID = (EditText) findViewById(R.id.income_view_projectID);
        
        _account = (EditText) findViewById(R.id.account);
        _notes = (EditText) findViewById(R.id.notes);

        Intent intent = getIntent();

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle extras = intent.getExtras();
            if(extras.getInt("transactionID") != 0){
                transactionID= extras.getInt("transactionID");
                getTransactionDetails(transactionID);
            }

            if ( getIntent().getSerializableExtra("income") != null ) {
                income = (Income) getIntent().getSerializableExtra("income");
                populateData(income);
            }
        }
    }

    private Date stringToDate(String dateStr){
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Income getIncome(){
        Income tempIncome = new Income();

        tempIncome.setId(income.getId());
        tempIncome.setUserId(income.getUserId());

        Date incomeDate = stringToDate(_date.getText().toString());
        tempIncome.setIncomeDate(incomeDate);

        tempIncome.setCustomerId(income.getCustomerId());
        tempIncome.setCustomer(income.getCustomer());

        BigDecimal amt_ = new BigDecimal(_amount.getText().toString());
        tempIncome.setAmount(amt_);

        tempIncome.setPaymentMethodId(income.getPaymentMethodId());
        tempIncome.setPaymentMethod(income.getPaymentMethod());

        tempIncome.setAccountId(income.getAccountId());
        tempIncome.setAccount(_account.getText().toString());

        int projid = Integer.parseInt(_projectID.getText().toString());
        tempIncome.setAccountId(projid);
        tempIncome.setProjectName(_project.getText().toString());

        tempIncome.setNotes(_notes.getText().toString());

        tempIncome.setFarmName(income.getFarmName());
        /*
            private int id;
            private int UserId;
            private Date IncomeDate;
            private int CustomerId;
            private BigDecimal Amount;
            private int PaymentMethodId;
            private int AccountId;
            private int ProjectId;
            private String Notes;
            private String Customer;
            private String Account;
            private String ProjectName;
            private String PaymentMethod;
            private String FarmName;
        */
        return tempIncome;
    }

    private void populateData(Income income){
        _amount.setText( income.getAmount().toString());

        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        String dateStr = format2.format(income.getIncomeDate());
        _date.setText(dateStr);
        _date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });

        _projectID.setText(String.valueOf(income.getProjectId()));
        _project.setText(income.getProjectName());
        _project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProject();
            }
        });

        _customer.setText(income.getCustomer());

        _account.setText(income.getAccount());
        _notes.setText(income.getNotes());

        Log.d(_TAG, "income.getProjectName(): " + income.getProjectName());
    }

    public void selectProject(){
        Bundle extras = new Bundle();
        extras.putSerializable("income", getIncome());

        Intent intent = new Intent(getApplicationContext(), ProjectsViewActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }



    public void displayDatePicker(){
        Bundle extras = new Bundle();
        extras.putSerializable("income", getIncome());

        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        intent.putExtras(extras);
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

                            populateData(income);

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
