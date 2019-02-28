package com.vogella.android.myapplication.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Expense;
import com.vogella.android.myapplication.model.Income;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseViewActivity extends AppCompatActivity {

    final String  _TAG = "INCOMEVEVIEW: ";
    final String  TAG = "REQUEST_QUEUE";

    int transactionID = 0;

    Transaction transaction  = new Transaction();

    DatePickerDialog datePickerDialog;

    private EditText _amount;
    private EditText _date;
    private EditText _project;
    private EditText _account;
    private EditText _notes;
    private Button _btnSubmitExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_view);

        _amount = (EditText) findViewById(R.id.amount_expense);
        _date = (EditText) findViewById(R.id.date_expense);
        _project = (EditText) findViewById(R.id.project_expense);
        _account = (EditText) findViewById(R.id.account_expense);
        _notes = (EditText) findViewById(R.id.notes_expense);
        _btnSubmitExpense = (Button) findViewById(R.id.btnSubmitExpense_edit);

        Intent intent = getIntent();

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle extras = intent.getExtras();
            if(extras.getInt("transactionID") != 0){
                transactionID= extras.getInt("transactionID");
                getTransactionDetails(transactionID);
            }

            if ( getIntent().getSerializableExtra("transaction") != null ) {
                transaction = (Transaction) getIntent().getSerializableExtra("transaction");
                populateData(transaction);
            }
        }

        _btnSubmitExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitExpense();
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



    public Transaction getTransaction(){
        BigDecimal amt_ = new BigDecimal(_amount.getText().toString());
        transaction.setAmount(amt_);
        transaction.setDescription(_notes.getText().toString());
        return transaction;
    }

    private void populateData(Transaction transaction){
        _amount.setText( transaction.getAmount().toString());
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        String dateStr = format2.format(transaction.getTransactionDate());
        _date.setText(dateStr);
        _date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });
        _project.setText(transaction.getProjectName());
        _project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProject();
            }
        });
        _account.setText(transaction.getAccountName());
        _notes.setText(transaction.getDescription());
        Log.d(_TAG, "income.getProjectName(): " + transaction.getProjectName());
    }

    public void selectProject(){
        Bundle extras = new Bundle();
        extras.putSerializable("transaction", getTransaction());
        extras.putString("transactionType", "EXPENSE");

        Intent intent = new Intent(getApplicationContext(), ProjectsViewActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }



    public void displayDatePicker(){
        Bundle extras = new Bundle();
        extras.putSerializable("transaction", getTransaction());
        extras.putString("transactionType", "EXPENSE");

        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void getTransactionDetails( int trxID){
        String URL_ = "http://45.56.73.81:8084/Mpango/api/v1/transactions/" + trxID;

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {
                        if (response != null ) {
                            try {
                                transaction.setId(response.getInt("id"));
                                transaction.setAccountId(response.getInt("accountId"));

                                BigDecimal amount_ = new BigDecimal(response.getString("amount"));
                                transaction.setAmount(amount_);

                                String dateStr = response.getString("transactionDate"); // "expenseDate": "30-07-2018",
                                try {
                                    Date transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                    transaction.setTransactionDate(transDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                transaction.setTransactionTypeId(response.getInt("transactionTypeId"));
                                transaction.setTransactionType(response.getString("transactionType"));
                                transaction.setDescription(response.getString("description"));
                                transaction.setProjectId(response.getInt("projectId"));
                                transaction.setProjectName(response.getString("projectName"));
                                transaction.setUserId(response.getInt("userId"));
                                transaction.setFarmName(response.getString("farmName"));
                                transaction.setAccountName(response.getString("accountName"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(_TAG, e.getMessage());
                            }

                            populateData(transaction);

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


    public void submitExpense() {
        String URL_ADD_EXPENSE = "http://45.56.73.81:8084/Mpango/api/v1/transaction";
        //_btnSubmitExpense.setEnabled(false);

        Transaction expenseObjPosting =  getTransaction();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("expenseDate", dateToString(expenseObjPosting.getTransactionDate())); //sdjf hsdjksdfhjhsdfj
            postparams.put("amount", expenseObjPosting.getAmount());
            postparams.put("accountId", expenseObjPosting.getAccountId());
            postparams.put("projectId", expenseObjPosting.getProjectId());
            postparams.put("notes", expenseObjPosting.getDescription());
            postparams.put("userId", expenseObjPosting.getUserId());
            postparams.put("id", expenseObjPosting.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        final ProgressDialog progressDialog = new ProgressDialog(ExpenseViewActivity.this, R.style.AppTheme_Dark_Dialog);
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
