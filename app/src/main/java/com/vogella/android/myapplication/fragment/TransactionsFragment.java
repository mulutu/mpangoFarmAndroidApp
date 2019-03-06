package com.vogella.android.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.activity.TransactionActivity;
import com.vogella.android.myapplication.activity.TransactionViewActivity;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.util.RecyclerTouchListener;
import com.vogella.android.myapplication.adapter.TransactionAdapter;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class TransactionsFragment extends Fragment {

    private List<Transaction> transactionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;

    private List<Transaction> expenseList = new ArrayList<>();
    private List<Transaction> incomeList = new ArrayList<>();

    final String  _TAG = "TRANSACTIONS FRAGMENT: ";
    final String  TAG = "REQUEST_QUEUE";
    private static final int REQUEST_TRANSACTION = 0;

    private Button btnAddIncome, btnAddExpense;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =   inflater.inflate(R.layout.fragment_transactions, container, false);

        btnAddIncome = (Button)rootView.findViewById(R.id.addIncomeTransactions);
        btnAddExpense = (Button)rootView.findViewById(R.id.addExpenseTransactions);

        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TransactionActivity.class);
                startActivity(i);
            }
        });

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TransactionActivity.class);
                startActivity(i);
            }
        });

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        int userId = 1;
        getTransactionList(userId);
        return rootView;
    }

    private void getTransactionList( int userId ){
        String URL_EXPENSES = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userId + "/transactions";

        JsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest(
                Request.Method.GET,
                URL_EXPENSES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Transaction> transactionListArray = new ArrayList<>();
                        if (response != null ) {
                            try {
                                for(int i=0;i<response.length();i++){
                                    JSONObject transactionObj = response.getJSONObject(i);

                                    Transaction obj =  new Transaction();

                                    int id = transactionObj.getInt("id");
                                    int accountId = transactionObj.getInt("accountId");

                                    MathContext mc = MathContext.DECIMAL32;
                                    BigDecimal amount = new BigDecimal( transactionObj.getString("amount"), mc);

                                    String dateStr = transactionObj.getString("transactionDate"); // "expenseDate": "30-07-2018",
                                    Date transDate = null;
                                    try {
                                        transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                        obj.setTransactionDate(transDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    int transactionTypeId = transactionObj.getInt("transactionTypeId");
                                    String transactionType = transactionObj.getString("transactionType");
                                    String description = transactionObj.getString("description");
                                    int projectId = transactionObj.getInt("projectId");
                                    String projectName = transactionObj.getString("projectName");
                                    int userId = transactionObj.getInt("userId");
                                    int farmId = transactionObj.getInt("farmId");
                                    String farmName = transactionObj.getString("farmName");
                                    String accountName = transactionObj.getString("accountName");

                                    obj.setId(id);
                                    obj.setAccountId(accountId);
                                    obj.setAmount(amount);
                                    obj.setTransactionDate(transDate);
                                    obj.setTransactionTypeId(transactionTypeId);
                                    obj.setTransactionType(transactionType);
                                    obj.setDescription(description);
                                    obj.setProjectId(projectId);
                                    obj.setProjectName(projectName);
                                    obj.setUserId(userId);
                                    obj.setFarmId(farmId);
                                    obj.setFarmName(farmName);
                                    obj.setAccountName(accountName);

                                    transactionList.add(obj);

                                    /*{
                                        "id": 1,
                                            "accountId": 20,
                                            "amount": 333,
                                            "transactionDate": "30-07-2018",
                                            "transactionTypeId": 0,
                                            "transactionType": "INCOME",
                                            "description": "333",
                                            "projectId": 1,
                                            "projectName": "Onions",
                                            "userId": 1,
                                            "farmId": 0,
                                            "farmName": "Gachuriri",
                                            "accountName": "Online Sales"
                                    },*/
                                }
                                Log.d(_TAG, "getExpenseList expenseListArray SIZE: " + transactionListArray.size());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(_TAG, e.getMessage());
                            }
                            //buildTransList(transactionListArray);
                        }else{
                            //Toast.makeText(getApplicationContext(), "El usuario no existe en el Sistema", Toast.LENGTH_LONG).show();
                            Log.d(_TAG, "getIncomeList incomeListArray SIZE: " + transactionListArray.size() + " EXPENSE LIST IS NULL");
                            //return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                        }
                        //buildTransList(expenseListArray);
                        //getIncomeList();
                        displayTransactionList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest,TAG);
    }

    public void displayTransactionList(){

        if(transactionList.size()>0) {
            Collections.sort(transactionList);

            transactionAdapter = new TransactionAdapter(transactionList);
            transactionAdapter.notifyDataSetChanged();

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
            recyclerView.setAdapter(transactionAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Transaction transaction = transactionList.get(position);

                    Bundle extras = new Bundle();
                    extras.putSerializable("Transaction", transaction );

                    Intent intent = new Intent(getActivity().getApplicationContext(), TransactionViewActivity.class);
                    intent.putExtras(extras);

                    startActivityForResult(intent, REQUEST_TRANSACTION);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
        Log.d("dispExpenseList", "displayTransactionList() METHOD:  LIST SIZE" + transactionList.size());
    }


}
