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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.activity.IncomeViewActivity;
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
    private static final int REQUEST_CALENDAR = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getExpenseList();
        //getIncomeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =   inflater.inflate(R.layout.fragment_transactions, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        //getExpenseList();
        getTransactionList();
        return rootView;
    }

    private void getTransactionList(){
        String URL_EXPENSES = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/transactions/user/1";

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
                                    Transaction obj =  new Transaction(); //farmName
                                    String dateStr = transactionObj.getString("transactionDate"); // "expenseDate": "30-07-2018",

                                    try {
                                        Date transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                        obj.setTransactionDate(transDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    obj.setTransactionID(transactionObj.getInt("transactionID"));
                                    obj.setTransactionAmount(new BigDecimal(transactionObj.getString("transactionAmount")));
                                    obj.setTransactionDescription(transactionObj.getString("transactionDescription"));
                                    obj.setTransactionType(transactionObj.getString("transactionType"));

                                    transactionListArray.add(obj);
                                }
                                Log.d(_TAG, "getExpenseList expenseListArray SIZE: " + transactionListArray.size());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(_TAG, e.getMessage());
                            }
                            buildTransList(transactionListArray);
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

    private void getExpenseList(){
        String URL_EXPENSES = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/expense/user/1";

        JsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest(
                Request.Method.GET,
                URL_EXPENSES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Transaction> expenseListArray = new ArrayList<>();
                        if (response != null ) {
                            try {
                                for(int i=0;i<response.length();i++){
                                    JSONObject expenseObj = response.getJSONObject(i);
                                    Transaction obj =  new Transaction(); //farmName
                                    String dateStr = expenseObj.getString("expenseDate"); // "expenseDate": "30-07-2018",

                                    try {
                                        Date transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                        obj.setTransactionDate(transDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    obj.setTransactionAmount(new BigDecimal(expenseObj.getString("amount")));
                                    obj.setTransactionDescription(expenseObj.getString("projectName") + " " + expenseObj.getString("account") + " " + expenseObj.getString("supplier"));
                                    obj.setTransactionType("EXPENSE");

                                    expenseListArray.add(obj);
                                }
                                Log.d(_TAG, "getExpenseList expenseListArray SIZE: " + expenseListArray.size());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(_TAG, e.getMessage());
                            }
                            buildTransList(expenseListArray);
                        }else{
                            //Toast.makeText(getApplicationContext(), "El usuario no existe en el Sistema", Toast.LENGTH_LONG).show();
                            Log.d(_TAG, "getIncomeList incomeListArray SIZE: " + expenseListArray.size() + " EXPENSE LIST IS NULL");
                            //return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                        }
                        //buildTransList(expenseListArray);
                        getIncomeList();
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


    private void getIncomeList(){
        String URL_EXPENSES = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/income/user/1";
        //JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
        JsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest(

                Request.Method.GET,
                URL_EXPENSES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Transaction> incomeListArray = new ArrayList<>();
                        if (response != null ) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject expenseObj = response.getJSONObject(i);
                                    Transaction obj = new Transaction(); //farmName

                                    String dateStr = expenseObj.getString("incomeDate"); // "expenseDate": "30-07-2018",

                                    try {
                                        Date transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                        obj.setTransactionDate(transDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    obj.setTransactionAmount(new BigDecimal(expenseObj.getString("amount")));
                                    obj.setTransactionDescription(expenseObj.getString("projectName") + " " + expenseObj.getString("account") + " " + expenseObj.getString("customer"));
                                    obj.setTransactionType("INCOME");

                                    incomeListArray.add(obj);
                                    // return Response.success(obj);
                                }
                                Log.d(_TAG, "getIncomeList incomeListArray SIZE: " + incomeListArray.size());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(_TAG, e.getMessage());
                            }
                            buildTransList(incomeListArray);
                        }else{
                            //Toast.makeText(getApplicationContext(), "El usuario no existe en el Sistema", Toast.LENGTH_LONG).show();
                            Log.d(_TAG, "getIncomeList incomeListArray SIZE: " + incomeListArray.size() + " INCOME LIST IS NULL");
                            //return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                        }
                        displayTransactionList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
                return;
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest,TAG);
    }

    public void buildTransList(List<Transaction> trxListArray){
        if(trxListArray.size() > 0 ){
            transactionList.addAll(trxListArray);
        }
        Log.d(_TAG, "buildTransList: trxListArray " + trxListArray.size());
        Log.d(_TAG, "buildTransList: transactionList " + transactionList.size());
    }

    public void displayTransactionList(){
        //transactionList = expenseListArray;
        if(transactionList.size()>0) {
            Collections.sort(transactionList);

            transactionAdapter = new TransactionAdapter(transactionList);
            transactionAdapter.notifyDataSetChanged();

            //Log.d(_TAG, "incomeList: 4 " + expenseList.size());
            //Log.d(_TAG, "incomeList: 4 " + incomeList.size());
            Log.d(_TAG, "transactionList: 4 " + transactionList.size());

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
            recyclerView.setAdapter(transactionAdapter); // asdhf sdf kasdhf hsdjkfh

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Transaction transaction = transactionList.get(position);

                    String trxType = transaction.getTransactionType();
                    if(trxType.equalsIgnoreCase("INCOME")){
                        Intent intent = new Intent(getActivity().getApplicationContext(), IncomeViewActivity.class);
                        intent.putExtra("transactionID",transaction.getTransactionID());

                        Log.d(_TAG, "transactionID: " + transaction.getTransactionID());

                        //Toast.makeText(getActivity().getApplicationContext(), transaction.getTransactionID() + " price is selected!", Toast.LENGTH_SHORT).show();

                        startActivityForResult(intent, REQUEST_CALENDAR);
                        //getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }else if(trxType.equalsIgnoreCase("EXPENSE")){
                        Toast.makeText(getActivity().getApplicationContext(), transaction.getTransactionAmount().toString() + " is selected!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
        Log.d("dispExpenseList", "displayTransactionList() METHOD:  LIST SIZE" + transactionList.size());
    }


}
