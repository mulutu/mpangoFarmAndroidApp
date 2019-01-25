package com.vogella.android.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.adapter.SampleAdapter;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class PagerFragment extends Fragment {

    ViewPager pager;

    ArrayList<Project> projects = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.pager, container, false);
        pager=(ViewPager)result.findViewById(R.id.pager);

        getListOfProjects(1);

        //Button button = (Button) result.findViewById(R.id.first);

        //button.setOnClickListener(new View.OnClickListener() {
            //public void onClick(View v) {
               // pager.setCurrentItem(0);
           // }
       // });

       // button = (Button) result.findViewById(R.id.last);
       // button.setOnClickListener(new View.OnClickListener() {
            //public void onClick(View v) {
               // pager.setCurrentItem(buildAdapter().getCount() - 1);
           // }
       // });

        return(result);
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
        String URL_ACCOUNTS = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/projects/user/" + userID;
        final String  _TAG = "PROJECTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_ACCOUNTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int projectId;
                        String projectName = "";
                        int farmId;
                        String projectDesc = "";

                        int expectedOutput = 0;
                        int actualOutput = 0;

                        //BigDecimal totalExpenses = new BigDecimal("0");
                        //BigDecimal totalIncomes = new BigDecimal("0");

                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject supplier = response.getJSONObject(i);

                                projectId = supplier.getInt("id");
                                projectName = supplier.getString("projectName");
                                farmId = supplier.getInt("farmId");
                                projectDesc = supplier.getString("description");

                                expectedOutput = supplier.getInt("expectedOutput");
                                actualOutput = supplier.getInt("actualOutput");

                                String totalExpenses =  supplier.getString("totalExpeses");
                                String totalIncomes = supplier.getString("totalIncomes");

                                Project project = new Project();
                                project.setId(projectId);
                                project.setProjectName(projectName);
                                project.setFarmId(farmId);
                                project.setDescription(projectDesc);
                                project.setExpectedOutput(expectedOutput);
                                project.setExpectedOutput(actualOutput);

                                MathContext mc = new MathContext(2); // 2 precision

                                project.setTotalExpenses(new BigDecimal(totalExpenses, MathContext.DECIMAL64));
                                project.setTotalIncomes(new BigDecimal(totalIncomes, MathContext.DECIMAL64));

                                // Toast
                                //Toast.makeText(getContext(),"PageFragment:-> totalExpenses" + totalExpenses.toString(), Toast.LENGTH_LONG).show();
                                //Toast.makeText(getContext(),"PageFragment:-> totalIncomes" + totalIncomes.toString(), Toast.LENGTH_LONG).show();

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
}