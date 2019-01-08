package com.vogella.android.myapplication;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class TransactionsFragment extends Fragment {

    private List<Transaction> transactionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;

    private List<Transaction> expenseList = new ArrayList<>();
    private List<Transaction> incomeList = new ArrayList<>();

    final String  _TAG = "TRANSACTIONS FRAGMENT: ";


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

        getExpenseList();



        return rootView;
    }

    private void getExpenseList(){
        String URL_EXPENSES = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/expense/user/1";
        final String  TAG = "EXPENSE ACCOUNTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_EXPENSES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Transaction> expenseListArray = new ArrayList<>();
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
                                obj.setTransactionDescription(expenseObj.getString("account") + " " + expenseObj.getString("supplier"));
                                obj.setTransactionType("EXPENSE");

                                expenseListArray.add(obj);
                            }

                            Log.d(_TAG, "getExpenseList expenseListArray SIZE: " + expenseListArray.size());

                            //expenseList = expenseListArray;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }

                        buildTransList(expenseListArray);

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
        final String  TAG = "INCOME ACCOUNTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_EXPENSES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Transaction> incomeListArray = new ArrayList<>();
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject expenseObj = response.getJSONObject(i);
                                Transaction obj =  new Transaction(); //farmName

                                String dateStr = expenseObj.getString("incomeDate"); // "expenseDate": "30-07-2018",

                                try {
                                    Date transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                    obj.setTransactionDate(transDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                obj.setTransactionAmount(new BigDecimal(expenseObj.getString("amount")));
                                obj.setTransactionDescription(expenseObj.getString("account") + " " + expenseObj.getString("customer"));
                                obj.setTransactionType("INCOME");

                                incomeListArray.add(obj);
                            }

                            Log.d(_TAG, "getIncomeList incomeListArray SIZE: " + incomeListArray.size());

                            //incomeList = incomeListArray;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }

                        buildTransList(incomeListArray);

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

    public void buildTransList(List<Transaction> trxListArray){
        transactionList.addAll(trxListArray);

        Log.d(_TAG, "buildTransList: trxListArray " + trxListArray.size());
        Log.d(_TAG, "buildTransList: transactionList " + transactionList.size());
    }

    public void displayTransactionList(){
        //transactionList = expenseListArray;

        Collections.sort(transactionList);

        transactionAdapter = new TransactionAdapter(transactionList);
        transactionAdapter.notifyDataSetChanged();

        Log.d(_TAG, "incomeList: 4 " + expenseList.size());
        Log.d(_TAG, "incomeList: 4 " + incomeList.size());
        Log.d(_TAG, "transactionList: 4 " + transactionList.size());

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(transactionAdapter); // asdhf sdf kasdhf hsdjkfh

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Transaction transaction = transactionList.get(position);
                Toast.makeText(getActivity().getApplicationContext(), transaction.getTransactionAmount().toString() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Log.d("dispExpenseList", "displayTransactionList() METHOD:  LIST SIZE" + transactionList.size());
    }


}
