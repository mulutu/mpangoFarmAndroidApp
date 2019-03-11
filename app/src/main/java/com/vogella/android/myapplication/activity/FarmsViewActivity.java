package com.vogella.android.myapplication.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.adapter.farmsAdapter;
import com.vogella.android.myapplication.adapter.projectsAdapter;
import com.vogella.android.myapplication.model.Expense;
import com.vogella.android.myapplication.model.Farm;
import com.vogella.android.myapplication.model.Income;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
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
import java.util.Date;
import java.util.List;

public class FarmsViewActivity extends AppCompatActivity {

    private Project _project;

    private List<Farm> farmsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private farmsAdapter mAdapter;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farms_view);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();
        userId = user.getId();

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(bundle.get("Process").equals("NEW_PROJECT")){
                if(!bundle.getSerializable("Project").equals(null)){
                    _project = (Project)bundle.getSerializable("Project");
                }
            }
        }

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();
        userId = user.getId();

        getListOfFarms(userId);
    }

    private void prepareProjectsData() {

        recyclerView = (RecyclerView) findViewById(R.id.add_farm_recycler_view);

        mAdapter = new farmsAdapter(farmsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Bundle extras = new Bundle();

                Farm farm = farmsList.get(position);

                _project.setFarmId(farm.getId());
                _project.setFarmName(farm.getFarmName());

                Intent intent = new Intent(getApplicationContext(), AddProjectActivity.class);

                extras.putSerializable("Project", _project);
                extras.putString("Process", "NEW_PROJECT");
                intent.putExtras(extras);
                finish();
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
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }


}
