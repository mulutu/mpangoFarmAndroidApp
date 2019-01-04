package com.vogella.android.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class TransactionsFragment extends Fragment {

    private List<Expense> expenseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final String  _TAG = "EXPENSE ACCOUNTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_EXPENSES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Expense> expenseListArray = new ArrayList<>();
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject expenseObj = response.getJSONObject(i);

                                Expense obj =  new Expense(); //farmName
                                obj.setFarmName(expenseObj.getString("farmName"));
                                obj.setAmount(new BigDecimal(expenseObj.getString("amount")));


                                Log.d(_TAG, "expenseObj: " + expenseObj);
                                Log.d(_TAG, "obj: " + obj);
                                Log.d(_TAG, "obj:  getFarmName-->" + obj.getFarmName());
                                expenseListArray.add(obj);
                            }

                            //Log.d(_TAG, "expenseList FRAGMENT ARRAY LIST: " + expenseList.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }

                        dispExpenseList(expenseListArray);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }

    public void dispExpenseList(List<Expense> expenseListArray){
        expenseList = expenseListArray;
        expenseAdapter = new ExpenseAdapter(expenseList);
        expenseAdapter.notifyDataSetChanged();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(expenseAdapter); // asdhf sdf kasdhf hsdjkfh

        Log.d("dispExpenseList", "dispExpenseList METHOD:  LIST SIZE" + expenseListArray.size());
    }


}
