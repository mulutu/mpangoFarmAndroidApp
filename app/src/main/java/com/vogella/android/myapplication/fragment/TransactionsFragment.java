package com.vogella.android.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.vogella.android.myapplication.activity.AddTransactionActivity;
import com.vogella.android.myapplication.activity.EditTransactionActivity;
import com.vogella.android.myapplication.activity.MainActivity;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.util.RecyclerTouchListener;
import com.vogella.android.myapplication.adapter.TransactionAdapter;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.SessionManager;

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


public class TransactionsFragment extends Fragment {

    private List<Transaction> transactionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;

    private List<Transaction> expenseList = new ArrayList<>();
    private List<Transaction> incomeList = new ArrayList<>();

    final String _TAG = "TRANSACTIONS FRAGMENT: ";
    final String TAG = "REQUEST_QUEUE";
    private static final int REQUEST_TRANSACTION = 0;

    private com.vogella.android.myapplication.activity.TextView_Lato btnAddIncome, btnAddExpense;

    private Transaction transaction = new Transaction();

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    // Declaring the Toolbar Object
    private Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
        setHasOptionsMenu(true);

        populateTitleBar(rootView);


        btnAddIncome = (com.vogella.android.myapplication.activity.TextView_Lato) rootView.findViewById(R.id.addIncomeTransactions);
        btnAddExpense = (com.vogella.android.myapplication.activity.TextView_Lato) rootView.findViewById(R.id.addExpenseTransactions);

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUser();
        userId = user.getId();

        transaction.setUserId(userId);

        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = new Bundle();
                extras.putString("Process", "NEW_TRANSACTION");

                transaction.setTransactionTypeId(0);
                extras.putSerializable("Transaction", transaction);

                Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = new Bundle();
                extras.putString("Process", "NEW_TRANSACTION");

                transaction.setTransactionTypeId(1);
                extras.putSerializable("Transaction", transaction);

                Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        getTransactionList(userId);
        return rootView;
    }

    private void populateTitleBar(View rootView) {
        toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle("Transactions");
        }
    }

    private void getTransactionList(int userId) {
        String URL_EXPENSES = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userId + "/transactions";
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.GET, URL_EXPENSES, null, (Response.Listener<JSONArray>) getActivity(), (Response.ErrorListener) getActivity(), "getTransactionList");
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req, TAG);
    }

    public void displayTransactionList(final List<Transaction> transactionList2) {

        if (transactionList2.size() > 0) {
            transactionAdapter = new TransactionAdapter(transactionList2);
            transactionAdapter.notifyDataSetChanged();

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
            recyclerView.setAdapter(transactionAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Transaction trx = transactionList2.get(position);

                    Bundle extras = new Bundle();
                    extras.putSerializable("Transaction", trx);
                    extras.putSerializable("Process", "EDIT_TRANSACTION");

                    Intent intent = new Intent(getActivity().getApplicationContext(), EditTransactionActivity.class);
                    intent.putExtras(extras);

                    startActivityForResult(intent, REQUEST_TRANSACTION);
                    //getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
        Log.d("dispExpenseList", "displayTransactionList() METHOD:  LIST SIZE" + transactionList2.size());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                return true;

            case R.id.action_logout:
                session.logoutUser();

                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
                //finish();
                return true;

            case R.id.action_favorite:
                Toast.makeText(getActivity().getApplicationContext(), "Action clicked", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
