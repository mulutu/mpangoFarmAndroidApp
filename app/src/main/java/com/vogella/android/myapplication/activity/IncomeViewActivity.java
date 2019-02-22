package com.vogella.android.myapplication.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private EditText _account;
    private EditText _notes;
    private Button _btnSubmitIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_view);

        _amount = (EditText) findViewById(R.id.amount);
        _date = (EditText) findViewById(R.id.date);
        _customer = (EditText) findViewById(R.id.customer);
        _project = (EditText) findViewById(R.id.project);        
        _account = (EditText) findViewById(R.id.account);
        _notes = (EditText) findViewById(R.id.notes);
        _btnSubmitIncome = (Button) findViewById(R.id.btnSubmitIncome_edit);

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

        _btnSubmitIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitIncome();
            }
        });
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

    private String dateToString(Date date){
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        return format2.format(date);
    }



    public Income getIncome(){

        BigDecimal amt_ = new BigDecimal(_amount.getText().toString());
        income.setAmount(amt_);

        income.setNotes(_notes.getText().toString());

        /*
        Income tempIncome = new Income();
        tempIncome.setId(income.getId());
        tempIncome.setUserId(income.getUserId());
        //Date incomeDate = stringToDate(_date.getText().toString());
        tempIncome.setIncomeDate(income.getIncomeDate());
        tempIncome.setCustomerId(income.getCustomerId());
        tempIncome.setCustomer(income.getCustomer());
        tempIncome.setPaymentMethodId(income.getPaymentMethodId());
        tempIncome.setPaymentMethod(income.getPaymentMethod());
        tempIncome.setAccountId(income.getAccountId());
        tempIncome.setAccount(_account.getText().toString());
        tempIncome.setProjectId(income.getProjectId());
        tempIncome.setProjectName(income.getProjectName());
        tempIncome.setFarmName(income.getFarmName());
        */

        return income;
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
        extras.putString("transactionType", "INCOME");

        Intent intent = new Intent(getApplicationContext(), ProjectsViewActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }



    public void displayDatePicker(){
        Bundle extras = new Bundle();
        extras.putSerializable("income", getIncome());
        extras.putString("transactionType", "INCOME");

        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void getTransactionDetails( int trxID){
        String URL_ = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/income/" + trxID;

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

                                //income.setCustomerId(response.getInt("customerId"));
                                //income.setCustomer(response.getString("customer"));

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


    public void submitIncome() {
        String URL_ADD_EXPENSE = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/income/";
        //_btnSubmitExpense.setEnabled(false);

        Income incomeObjPosting =  getIncome();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("incomeDate", dateToString(incomeObjPosting.getIncomeDate())); //sdjf hsdjksdfhjhsdfj
            postparams.put("amount", incomeObjPosting.getAmount());
            postparams.put("customerId", incomeObjPosting.getCustomerId());
            postparams.put("paymentMethodId", incomeObjPosting.getPaymentMethodId());
            postparams.put("accountId", incomeObjPosting.getAccountId());
            postparams.put("projectId", incomeObjPosting.getProjectId());
            postparams.put("notes", incomeObjPosting.getNotes());
            postparams.put("userId", incomeObjPosting.getUserId());
            postparams.put("id", incomeObjPosting.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(IncomeViewActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(
                Request.Method.PUT,
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
