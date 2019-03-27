package com.vogella.android.myapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.AddProjectActivity;
import com.vogella.android.myapplication.activity.TransactionActivity;
import com.vogella.android.myapplication.adapter.farmsAdapter;
import com.vogella.android.myapplication.model.Farm;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.util.RecyclerTouchListener;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment  {

    View rootView;

    private Button btn1, btn2;

    private FragmentActivity myContext;

    private Transaction transaction = new Transaction();

    private Project _project;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    private List<Farm> farmsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private farmsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUser();
        userId = user.getId();

        if (myContext.getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            myContext.getSupportFragmentManager().beginTransaction().add(android.R.id.content, new PagerFragment(), "pagerfragment").commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_home, container, false);


        ArrayList<Farm> farmsList = (ArrayList<Farm>) this.getArguments().getSerializable("farmsArray");

        //btn1= (Button)rootView.findViewById(R.id.expense);
        //btn2= (Button)rootView.findViewById(R.id.income);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.add_farm_recycler_view);

        transaction.setUserId(userId);

        /*btn1.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("Process", "NEW_TRANSACTION" );

                transaction.setTransactionTypeId(1);
                extras.putSerializable("Transaction", transaction );
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("Process", "NEW_TRANSACTION" );

                transaction.setTransactionTypeId(0);
                extras.putSerializable("Transaction", transaction );
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });*/
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    /*
    public List<Fragment> getVisibleFragments() {
        List<Fragment> allFragments = myContext.getSupportFragmentManager().getFragments();
        if (allFragments == null || allFragments.isEmpty()) {
            return Collections.emptyList();
        }
        List<Fragment> visibleFragments = new ArrayList<Fragment>();
        /*for (Fragment fragment : allFragments) {
            if (fragment.isVisible()) {
                visibleFragments.add(fragment);
            }
        }
        return allFragments;
    }*/

}
