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

        /*
        if (myContext.getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            myContext.getSupportFragmentManager().beginTransaction().add(android.R.id.content, new PagerFragment(), "pagerfragment").commit();
        }*/
        /*if (myContext.getSupportFragmentManager().findFragmentById(R.id.fragmentSection) == null) {
            myContext.getSupportFragmentManager().beginTransaction().add(R.id.fragmentSection, new PagerFragment(), "pagerfragment").commit();
        }*/
        // fragmentSection
        //myContext.getSupportFragmentManager().getFragments();
    }

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
        }*/
        return allFragments;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        btn1= (Button)rootView.findViewById(R.id.expense);
        btn2= (Button)rootView.findViewById(R.id.income);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.add_farm_recycler_view);

        transaction.setUserId(userId);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    private void prepareProjectsData() {



        mAdapter = new farmsAdapter(farmsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Bundle extras = new Bundle();

                Farm farm = farmsList.get(position);

                _project.setFarmId(farm.getId());
                _project.setFarmName(farm.getFarmName());

                Intent intent = new Intent(getActivity().getApplicationContext(), AddProjectActivity.class);

                extras.putSerializable("Project", _project);
                extras.putString("Process", "NEW_PROJECT");
                intent.putExtras(extras);
                //finish();
                startActivity(intent);

                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }



    public void getListOfFarms(int userID){
        String URL_PROJECTS = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userID + "/farms";
        final String  _TAG = "LIST OF FARMS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject farmObj = response.getJSONObject(i);

                                int id = farmObj.getInt("id");
                                String description = farmObj.getString("description");
                                String location = farmObj.getString("location");
                                String dateCreated = farmObj.getString("dateCreated");
                                String farmName = farmObj.getString("farmName");
                                int userId = farmObj.getInt("userId");
                                int size = farmObj.getInt("size");

                                Farm farm = new Farm();

                                farm.setId(id);
                                farm.setLocation(location);
                                farm.setSize(size);
                                farm.setFarmName(farmName);
                                farm.setDescription(description);
                                farm.setUserId(userId);

                                try {
                                    Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dateCreated);
                                    farm.setDateCreated(date1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                farmsList.add(farm);
                                /*[
                                {
                                        "id": 1,
                                        "description": "Gachuriri Farm",
                                        "location": "Embu South, Gachuriri",
                                        "dateCreated": "2018-07-16T00:00:00.000+0000",
                                        "farmName": "Gachuriri",
                                        "userId": 1,
                                        "size": 10
                                }
                                ]*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }
                        prepareProjectsData();
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

}
