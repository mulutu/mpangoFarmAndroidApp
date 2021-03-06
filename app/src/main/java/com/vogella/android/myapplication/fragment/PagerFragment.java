package com.vogella.android.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.adapter.SampleAdapter;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.util.AlertDialogManager;
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

public class PagerFragment extends Fragment {
    ViewPager pager;
    View result;
    private ImageButton leftNav, rightNav;
    ArrayList<Project> projects = new ArrayList<>();
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        result = inflater.inflate(R.layout.pager, container, false);
        pager=(ViewPager)result.findViewById(R.id.pager);
        setHasOptionsMenu(true);

        populateTitleBar(result);

        manageSession();

        getListOfProjects(userId);

        manageView();

        return(result);
    }

    private void manageView(){
        //viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        leftNav = (ImageButton) result.findViewById(R.id.left_nav);
        rightNav = (ImageButton) result.findViewById(R.id.right_nav);
        // Images left navigation
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = pager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    pager.setCurrentItem(tab);
                } else if (tab == 0) {
                    pager.setCurrentItem(tab);
                }
            }
        });
        // Images right navigatin
        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = pager.getCurrentItem();
                tab++;
                pager.setCurrentItem(tab);
            }
        });
    }

    private void manageSession(){
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUser();
        userId = user.getId();
    }

    private void populateTitleBar(View rootView){
        toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle("Farm Summary");
        }
    }

    private PagerAdapter buildAdapter() {

        List<Fragment> fragments = buildFragments();
        //ArrayList<String> categories = new ArrayList<>();
        //categories.add("one");
        //categories.add("two");
        //categories.add("three");
        //categories.add("four");
        //categories.add("five");
        //mPager = (ViewPager) v.findViewById(R.id.pager);
        //mPageAdapter = new MyFragmentPageAdapter(this,getSupportFragmentManager(), fragments, categories);
        //mPager.setAdapter(mPageAdapter);

        return(new SampleAdapter(getContext(), getChildFragmentManager(), fragments,  projects ));
    }

    private List<Fragment> buildFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        for(int i = 0; i<projects.size(); i++) {
            Bundle b = new Bundle();
            b.putInt("position", i);
            fragments.add(Fragment.instantiate(getContext(),PagerFragment.class.getName(),b));
        }

        return fragments;
    }


    public void getListOfProjects(int userID){
        String URL_ACCOUNTS = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userID + "/projects/summary";
        final String  _TAG = "PROJECTS-LIST: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_ACCOUNTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject projObj = response.getJSONObject(i);

                                int id = projObj.getInt("id");
                                int expectedOutput = projObj.getInt("expectedOutput");
                                int actualOutput = projObj.getInt("actualOutput");
                                int unitId = projObj.getInt("unitId");
                                String unitDescription = projObj.getString("unitDescription");
                                String description = projObj.getString("description");
                                int userId = projObj.getInt("userId");
                                String projectName = projObj.getString("projectName");
                                int farmId = projObj.getInt("farmId");

                                String dateStr = projObj.getString("dateCreated"); // "expenseDate": "30-07-2018",
                                Date dateCreated = null;
                                try {
                                    dateCreated = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                MathContext mc = MathContext.DECIMAL32;
                                BigDecimal totalExpenses = new BigDecimal( projObj.getString("totalExpeses"), mc);
                                BigDecimal totalIncomes = new BigDecimal( projObj.getString("totalIncomes"), mc);
                                Project project = new Project();
                                project.setId(id);
                                project.setExpectedOutput(expectedOutput);
                                project.setActualOutput(actualOutput);
                                project.setUnitId(unitId);

                                project.setTotalExpenses(totalExpenses);
                                project.setTotalIncomes(totalIncomes);

                                project.setUnitDescription(unitDescription);
                                project.setDescription(description);
                                project.setUserId(userId);
                                project.setProjectName(projectName);
                                project.setFarmId(farmId);
                                project.setDateCreated(dateCreated);

                                /*
                                "id": 97,
                                "expectedOutput": 1,
                                "actualOutput": 1,
                                "unitId": 1,
                                "unitDescription": null,
                                "transactions": null,
                                "totalExpeses": null,
                                "totalIncomes": null,
                                "description": "fg hdfg hdfg hdf hdfg ",
                                "userId": 1,
                                "projectName": "fghfghf hdfgh dfg hdfgh",
                                "farmId": 1,
                                "dateCreated": "29-08-2018"
                                */

                                projects.add(project);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(_TAG, e.getMessage());
                        }
                        pager.setAdapter(buildAdapter());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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