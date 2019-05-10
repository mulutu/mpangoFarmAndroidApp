package com.vogella.android.myapplication.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.adapter.FarmsListAdapter;
import com.vogella.android.myapplication.model.Farm;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONArray;

import java.util.ArrayList;
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
    private FarmsListAdapter mAdapter;

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

    public void prepareFarmsData( List<Farm> farmsList2 ) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.farm_list_recycler_view);
        mAdapter = new FarmsListAdapter(farmsList2);
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
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.GET, URL_PROJECTS, null, (Response.Listener<JSONArray>) getActivity(), (Response.ErrorListener) getActivity(), "getListOfFarmsHomeFragment");
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req, _TAG);
    }
}