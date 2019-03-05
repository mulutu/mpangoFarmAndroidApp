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

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectsViewActivity extends AppCompatActivity {

    private List<Project> projectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private projectsAdapter mAdapter;

    private Transaction transaction;

    private String process =  "";
    private String transactionType =  "";

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
            if(!bundle.getSerializable("Transaction").equals(null)){
                transaction = (Transaction)bundle.getSerializable("Transaction");
            }
            if(bundle.get("Process").equals("NEW_TRANSACTION")){
                process = "NEW_TRANSACTION";
            }else if(bundle.get("Process").equals("EDIT_TRANSACTION")){
                process = "EDIT_TRANSACTION";
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

                Project project = projectList.get(position);

                transaction.setProjectId(project.getId());
                transaction.setProjectName(project.getProjectName());

                Bundle extras = new Bundle();
                if( process.equalsIgnoreCase("NEW_TRANSACTION") ){
                    Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }else if( process.equalsIgnoreCase("EDIT_TRANSACTION") ){
                    Intent intent = new Intent(getApplicationContext(), TransactionViewActivity.class);
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
        String URL_PROJECTS = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userID + "/projects";
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
