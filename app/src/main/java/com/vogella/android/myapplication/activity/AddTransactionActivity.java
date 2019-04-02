package com.vogella.android.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddTransactionActivity extends AppCompatActivity{

    private static final String TAG = "TransactionActivity";

    // Declaring the Toolbar Object
    private Toolbar toolbar;

    private int userID = 1;
    private static final int REQUEST_CALENDAR = 0;

    private Transaction transaction;

    private EditText _transactionAmount;
    private EditText _transactionDate;
    private EditText _projectName;
    private EditText _accountName;
    private EditText _description;
    private Button _btnSubmitTransaction;

    private String process =  "";
    private String transactionType =  "";

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        populateTitleBar();

        session = new SessionManager(getApplicationContext());

        user = session.getUser();
        userId = user.getId();

        _transactionAmount = (EditText) findViewById(R.id.trxAmount);
        _transactionDate = (EditText) findViewById(R.id.trxDate);
        _projectName = (EditText) findViewById(R.id.trxProjectName);
        _accountName = (EditText) findViewById(R.id.trxAccountName);
        _description = (EditText) findViewById(R.id.trxDescription);
        _btnSubmitTransaction = (Button) findViewById(R.id.btnSubmitTransaction);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if ( getIntent().getSerializableExtra("Transaction") != null ) {
                    transaction = (Transaction) getIntent().getSerializableExtra("Transaction");
                    populateData(transaction);
                }
                if(extras.get("Process").equals("NEW_TRANSACTION")){
                    process = "NEW_TRANSACTION";
                }else if(extras.get("Process").equals("EDIT_TRANSACTION")){
                    process = "EDIT_TRANSACTION";
                }
            }
        }
        _transactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });
        _btnSubmitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTransaction();
            }
        });
    }

    private void populateTitleBar(){
        // Attaching the layout to the toolbar object
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        // Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Add Transaction");
        }
    }

    private void populateData(Transaction transaction){
        if(transaction.getAmount()!=null) {
            _transactionAmount.setText(transaction.getAmount().toString());
        }
        if(transaction.getTransactionDate()!=null) {
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
            String dateStr = format2.format(transaction.getTransactionDate());
            _transactionDate.setText(dateStr);
        }
        _transactionDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayDatePicker();
            }
        });

        if(transaction.getProjectName()!=""){
            _projectName.setText(transaction.getProjectName());
        }
        _projectName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                selectProject();
            }
        });

        if(transaction.getAccountName()!=""){
            _accountName.setText(transaction.getProjectName());
        }
        _accountName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                selectAccount();
            }
        });

        if(transaction.getAccountName()!="") {
            _accountName.setText(transaction.getAccountName());
        }
        if(transaction.getDescription()!="") {
            _description.setText(transaction.getDescription());
        }
    }

    public Transaction getTransaction() {
        if (_transactionAmount.getText() != null){
            BigDecimal amt_;
            MathContext mc = MathContext.DECIMAL32;
            try {
                amt_ = new BigDecimal(_transactionAmount.getText().toString(), mc);
                transaction.setAmount(amt_);
            } catch (NumberFormatException e) {
                //amt_ = null;
            }
        }
        if( _description.getText() != null) {
            transaction.setDescription(_description.getText().toString());
        }
        return transaction;
    }

    public void selectAccount(){
        Bundle extras = new Bundle();
        extras.putSerializable("Transaction", getTransaction());
        extras.putString("Process","NEW_TRANSACTION");

        Intent intent = new Intent(getApplicationContext(), AccountsViewActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void selectProject(){
        Bundle extras = new Bundle();
        extras.putSerializable("Transaction", getTransaction());
        extras.putString("Process","NEW_TRANSACTION");

        Intent intent = new Intent(getApplicationContext(), ProjectsViewActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void displayDatePicker(){
        Bundle extras = new Bundle();
        extras.putSerializable("Transaction", getTransaction());
        extras.putString("Process", "NEW_TRANSACTION");

        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private String dateToString(Date date){
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
        return format2.format(date);
    }

    public void submitTransaction() {
        Log.d(TAG, "submitTransaction");

        String URL_ADD_TRANSACTION= "http://45.56.73.81:8084/Mpango/api/v1/transactions";
        //_btnSubmitExpense.setEnabled(false);

        String  REQUEST_TAG = "com.vogella.android.volleyJsonObjectRequest";

        Transaction trx =  getTransaction();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("transactionDate", dateToString(trx.getTransactionDate()));
            postparams.put("amount", trx.getAmount());
            postparams.put("accountId", trx.getAccountId());
            postparams.put("projectId", trx.getProjectId());
            postparams.put("description", trx.getDescription());
            postparams.put("userId", trx.getUserId());
            postparams.put("transactionTypeId", trx.getTransactionTypeId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("submitTransaction::: ", "userId: " + trx.getUserId());

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(
                Request.Method.POST,
                URL_ADD_TRANSACTION,
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
                Log.d("TRX_PUT_RESPONSE", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_logout:
                session.logoutUser();

                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;

            case R.id.action_favorite:
                Toast.makeText(this, "Action clicked", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //public boolean onCreateOptionsMenu(Menu menu) {
    //    return true;
   // }
}