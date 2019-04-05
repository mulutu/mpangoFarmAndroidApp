package com.vogella.android.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.AddFarmActivity;
import com.vogella.android.myapplication.activity.AddProjectActivity;
import com.vogella.android.myapplication.activity.EditTransactionActivity;
import com.vogella.android.myapplication.activity.tasks.TaskActivity;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.adapter.projectsAdapter;
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectsSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectsSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectsSettingsFragment extends Fragment {

    // ------------------------------------------------------------------
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    //--------------------------------------------------------------------

    private com.vogella.android.myapplication.activity.TextView_Lato btnAddProject, btnAddFarm;

    private List<Project> projectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private projectsAdapter mAdapter;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    // Declaring the Toolbar Object
    private Toolbar toolbar;

    public ProjectsSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProjectsSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProjectsSettingsFragment newInstance(String param1, String param2) {
        ProjectsSettingsFragment fragment = new ProjectsSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUser();
        userId = user.getId();

        View rootView =   inflater.inflate(R.layout.fragment_projects_settings, container, false);

        setHasOptionsMenu(true);

        populateTitleBar(rootView);

        btnAddProject = (com.vogella.android.myapplication.activity.TextView_Lato)rootView.findViewById(R.id.addproject);
        btnAddFarm = (com.vogella.android.myapplication.activity.TextView_Lato)rootView.findViewById(R.id.addfarm);

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddProjectActivity.class);
                startActivity(i);
            }
        });

        btnAddFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddFarmActivity.class);
                startActivity(i);
            }
        });

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_projects_setting_view);

        mAdapter = new projectsAdapter(projectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getListOfProjects(userId);

        return rootView;
        //return inflater.inflate(R.layout.fragment_projects_settings, container, false);
    }

    private void populateTitleBar(View rootView){
        toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle("Projects");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

                                //MathContext mc = MathContext.DECIMAL32;
                                //BigDecimal totalExpenses = new BigDecimal( projObj.getString("totalExpeses"), mc);
                               // BigDecimal totalIncomes = new BigDecimal( projObj.getString("totalIncomes"), mc);

                                Project project = new Project();
                                project.setId(id);
                                project.setExpectedOutput(expectedOutput);
                                project.setActualOutput(actualOutput);
                                project.setUnitId(unitId);

                                //project.setTotalExpenses(totalExpenses);
                                //project.setTotalIncomes(totalIncomes);

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
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }

    private void prepareProjectsData() {
        mAdapter.notifyDataSetChanged();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Project project = projectList.get(position);

                Bundle extras = new Bundle();
                extras.putSerializable("Project", project );
                extras.putSerializable("Process", "EDIT_PROJECT" );

                Intent intent = new Intent(getActivity().getApplicationContext(), TaskActivity.class);
                intent.putExtras(extras);

                startActivityForResult(intent, 0);
                //getActivity().finish();
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
