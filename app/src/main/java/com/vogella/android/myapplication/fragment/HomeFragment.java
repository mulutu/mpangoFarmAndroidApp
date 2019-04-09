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
import com.vogella.android.myapplication.adapter.farmsListAdapter;
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
    private farmsListAdapter mAdapter;

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

        getListOfFarms(userId);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    private void prepareProjectsData() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.farm_list_recycler_view);

        mAdapter = new farmsListAdapter(farmsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 3));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
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
