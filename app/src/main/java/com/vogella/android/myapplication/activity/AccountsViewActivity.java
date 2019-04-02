package com.vogella.android.myapplication.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.adapter.accountsAdapter;
import com.vogella.android.myapplication.model.Account;
import com.vogella.android.myapplication.model.ChartOfAccounts;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.util.RecyclerTouchListener;
import com.vogella.android.myapplication.util.SectionOrRow;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AccountsViewActivity extends AppCompatActivity {

    private List<Account> accountList = new ArrayList<>();
    private List<SectionOrRow> mData = new ArrayList<>();
    private RecyclerView recyclerView;
    private accountsAdapter mAdapter;

    private Transaction transaction;
    private String process =  "";

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_view);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getSerializable("Transaction").equals(null)){
                transaction = (Transaction)bundle.getSerializable("Transaction");
            }
            if(bundle.get("Process").equals("NEW_TRANSACTION")){
                process = "NEW_TRANSACTION";
            }else if(bundle.get("Process").equals("EDIT_TRANSACTION")){
                process = "EDIT_TRANSACTION";
            }
        }

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();
        userId = user.getId();

        getListOfAccounts(userId);
    }

    public void getListOfAccounts(int userID){
        String URL_PROJECTS = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userID + "/coa";
        final String  _TAG = "LIST OF ACCOUNTS: ";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){

                                JSONObject coaObj = response.getJSONObject(i);

                                String accountGroupTypeCode  = coaObj.getString("accountGroupTypeCode");
                                int accountGroupTypeId  = coaObj.getInt("accountGroupTypeId");
                                String accountGroupTypeName  = coaObj.getString("accountGroupTypeName");
                                JSONArray listOfAccounts = coaObj.getJSONArray("listOfAccounts");

                                List<Account> listofaccts = new ArrayList<>();

                                mData.add(SectionOrRow.createSection(accountGroupTypeName));

                                for(int k=0;k<listOfAccounts.length();k++){
                                    Account acc = new Account();

                                    JSONObject acctObj = listOfAccounts.getJSONObject(k);

                                    int id = acctObj.getInt("id");
                                    String accountName = acctObj.getString("accountName");
                                    String description = acctObj.getString("description");

                                    acc.setId(id);
                                    acc.setDescription(description);

                                    listofaccts.add(acc);

                                    mData.add(SectionOrRow.createRow(id,accountName,description));
                                }

                                ChartOfAccounts coa = new ChartOfAccounts();

                                coa.setAccountGroupTypeCode(accountGroupTypeCode);
                                coa.setAccountGroupTypeId(accountGroupTypeId);
                                coa.setAccountGroupTypeName(accountGroupTypeName);
                                coa.setListOfAccounts(listofaccts);

                                /*
                                "id": 97,
                                "expectedOutput": 1,
                                "actualOutput": 1,
                                "unitId": 1,
                                "unitDescription": null,
                                "transactions": null,
                                "totalExpeses": null,
                                "totalIncomes": null,
                                "description": "fg hdfg hdfg hdf hdfg ",
                                "userId": 1,
                                "projectName": "fghfghf hdfgh dfg hdfgh",
                                "farmId": 1,
                                "dateCreated": "29-08-2018"
                                */
                                //projectList.add(project);

                                Log.d(_TAG, "COA :>>>> " + coa.toString());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }

                       prepareAccountsData();

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

    public void prepareAccountsData(){
        recyclerView = (RecyclerView) findViewById(R.id.accounts_recycler_view);

        mAdapter = new accountsAdapter(mData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                SectionOrRow row = mData.get(position);

                if(row.isRow()) {
                    int accountId = row.getId();
                    String accountName = row.getRow();

                    transaction.setAccountId(accountId);
                    transaction.setAccountName(accountName);

                    Bundle extras = new Bundle();
                    extras.putSerializable("Transaction", transaction);

                    if (process.equalsIgnoreCase("NEW_TRANSACTION")) {
                        Intent intent = new Intent(getApplicationContext(), AddTransactionActivity.class);
                        extras.putString("Process", "NEW_TRANSACTION");
                        intent.putExtras(extras);
                        finish();
                        startActivity(intent);
                    } else if (process.equalsIgnoreCase("EDIT_TRANSACTION")) {
                        Intent intent = new Intent(getApplicationContext(), TransactionViewActivity.class);
                        extras.putString("Process", "EDIT_TRANSACTION");
                        intent.putExtras(extras);
                        finish();
                        startActivity(intent);
                    }
                    //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
