package com.vogella.android.myapplication.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.adapter.projectsAdapter;
import com.vogella.android.myapplication.model.Expense;
import com.vogella.android.myapplication.model.Income;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.util.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectsViewActivity extends AppCompatActivity {

    private List<Project> projectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private projectsAdapter mAdapter;

    private Income _income;

    private Expense _expense;

    private String trxType =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_view);

        recyclerView = (RecyclerView) findViewById(R.id.projects_recycler_view);

        mAdapter = new projectsAdapter(projectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(bundle.get("transactionType").equals("EXPENSE")){
                if(!bundle.getSerializable("expense").equals(null)){
                    _expense = (Expense)bundle.getSerializable("expense");
                    trxType = "EXPENSE";
                }
            }else if(bundle.get("transactionType").equals("INCOME")){
                if(!bundle.getSerializable("income").equals(null)){
                    _income = (Income)bundle.getSerializable("income");
                    trxType = "INCOME";
                }
            }


        }

        int userId = 1;
        getListOfProjects(userId);
    }

    public void showSnackbar(View view, String message, int duration){
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void prepareProjectsData() {

        mAdapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Bundle extras = new Bundle();

                Project project = projectList.get(position);

                if( trxType.equalsIgnoreCase("INCOME") ){
                    _income.setProjectId(project.getId());
                    _income.setProjectName(project.getProjectName());

                    Intent intent = new Intent(getApplicationContext(), IncomeViewActivity.class);

                    extras.putSerializable("income", _income);
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }else if( trxType.equalsIgnoreCase("EXPENSE") ){
                    _expense.setProjectId(project.getId());
                    _expense.setProjectName(project.getProjectName());

                    Intent intent = new Intent(getApplicationContext(), ExpenseViewActivity.class);

                    extras.putSerializable("expense", _expense);
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void getListOfProjects(int userID){
        String URL_PROJECTS = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/financials/projects/user/" + userID;
        final String  _TAG = "LIST OF PROJECTS: ";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject projObj = response.getJSONObject(i);

                                int projectId = projObj.getInt("id");
                                int userid = projObj.getInt("userId");
                                int farmId = projObj.getInt("farmId");
                                String projectName = projObj.getString("projectName");
                                String description = projObj.getString("description");

                                Project project = new Project(projectId,userid,farmId, projectName, description);

                                projectList.add(project);

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
