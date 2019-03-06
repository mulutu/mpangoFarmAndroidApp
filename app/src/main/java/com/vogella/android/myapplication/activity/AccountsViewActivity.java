package com.vogella.android.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.adapter.accountsAdapter;
import com.vogella.android.myapplication.adapter.projectsAdapter;
import com.vogella.android.myapplication.model.Account;
import com.vogella.android.myapplication.model.ChartOfAccounts;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountsViewActivity extends AppCompatActivity {

    private List<Account> accountList = new ArrayList<>();
    private RecyclerView recyclerView;
    private accountsAdapter mAdapter;

    private Transaction transaction;
    private String process =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_view);

        recyclerView = (RecyclerView) findViewById(R.id.accounts_recycler_view);

        mAdapter = new accountsAdapter(accountList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        /*if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getSerializable("Transaction").equals(null)){
                transaction = (Transaction)bundle.getSerializable("Transaction");
            }
            if(bundle.get("Process").equals("NEW_TRANSACTION")){
                process = "NEW_TRANSACTION";
            }else if(bundle.get("Process").equals("EDIT_TRANSACTION")){
                process = "EDIT_TRANSACTION";
            }
        }*/

        int userId = 1;
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

                                for(int k=0;k<listOfAccounts.length();k++){
                                    Account acc = new Account();

                                    JSONObject acctObj = listOfAccounts.getJSONObject(k);
                                    int id = acctObj.getInt("id");
                                    String description = acctObj.getString("description");

                                    acc.setId(id);
                                    acc.setDescription(description);

                                    listofaccts.add(acc);
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

                       // prepareProjectsData();

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
}
