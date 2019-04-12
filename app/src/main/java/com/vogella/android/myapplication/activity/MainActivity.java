package com.vogella.android.myapplication.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.fragment.HomeFragment;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.fragment.ProjectsSettingsFragment;
import com.vogella.android.myapplication.fragment.TransactionsFragment;
import com.vogella.android.myapplication.model.Farm;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
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

//public class MainActivity extends AppCompatActivity  implements ProjectsSettingsFragment.OnFragmentInteractionListener, Response.Listener<JSONObject>, Response.ErrorListener {

public class MainActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener, ProjectsSettingsFragment.OnFragmentInteractionListener {

    // Declaring the Toolbar Object
    private Toolbar toolbar;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    private Boolean hasFarms = false;
    private Boolean hasProjects = false;

    private String process = "";

    private ArrayList<Farm> farmsList = new ArrayList<>();
    private ArrayList<Farm> farmsListHomeFragment = new ArrayList<>();
    private List<Transaction> transactionList = new ArrayList<>();
    private List<Project> projectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attaching the layout to the toolbar object
        //toolbar = (Toolbar) findViewById(R.id.tool_bar);

        // Setting toolbar as the ActionBar with setSupportActionBar() call
        //setSupportActionBar(toolbar);

        // Remove default title text
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view
        //TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            //session.checkLogin();
            //user = session.getUser();
            //userId = user.getId();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else {
            /*if(getIntent()!=null && getIntent().getExtras()!=null){
                Bundle bundle = getIntent().getExtras();
                if(!bundle.getSerializable("HasFarms").equals(null)){
                    hasFarms = bundle.getBoolean("HasFarms");
                }
                if(bundle.get("Process").equals("LOGIN")){
                    process = "LOGIN";
                }
            }*/
            user = session.getUser();
            userId = user.getId();
            getListOfFarms(userId);
        }
    }


    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));
            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    protected void selectFragment(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {

            case R.id.navigation_home:

                List<Fragment> allFragmentsHome = getSupportFragmentManager().getFragments();
                for (Fragment fragment : allFragmentsHome) {
                    if (fragment != null) {
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        //trx.hide(fragment);
                        trx.remove(fragment);
                        trx.commit();
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("farmsArray", farmsList);

                HomeFragment hf = new HomeFragment();
                hf.setArguments(bundle);

                pushFragment(hf, "homefragment");


                /*Fragment fr = getSupportFragmentManager().findFragmentByTag("homefragment");
                if(fr != null) {
                    getSupportFragmentManager().beginTransaction().show(fr).commit();
                }else{
                    pushFragment(new HomeFragment(), "homefragment");
                }

                List<Fragment> allFrags = getSupportFragmentManager().getFragments();
                for (Fragment fragment : allFrags) {
                    if(fragment!=null){
                        //Log.d("HOME:-> FRAGMENTS TAG: ", fragment.getTag());
                        if(fragment.getTag()!=null) {
                            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                            if (fragment.getTag().equalsIgnoreCase("homefragment")) {
                                trx.show(fragment);
                                //pushFragment(new HomeFragment(), "homefragment");
                            }else{
                                trx.show(fragment);
                            }
                            trx.commit();
                            //List<Fragment> childFr = fragment.getChildFragmentManager().getFragments();
                            //Log.d("HOME CHILD LIST SIZE: ", String.valueOf(childFr.size()));
                        }
                    }
                }*/

                break;

            case R.id.navigation_transactions:

                List<Fragment> allFragmentsNav = getSupportFragmentManager().getFragments();
                for (Fragment fragment : allFragmentsNav) {
                    if (fragment != null) {
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        //trx.hide(fragment);
                        trx.remove(fragment);
                        trx.commit();
                    }
                }
                pushFragment(new TransactionsFragment(), "txnsFragment");

                break;

            case R.id.navigation_projects:

                List<Fragment> allFragmentsProj = getSupportFragmentManager().getFragments();
                for (Fragment fragment : allFragmentsProj) {
                    if (fragment != null) {
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        //trx.hide(fragment);
                        trx.remove(fragment);
                        trx.commit();
                    }
                }
                pushFragment(new ProjectsSettingsFragment(), "txnsProjectsSettings");
                break;
        }
    }

    protected void pushFragment(Fragment fragment, String tag) {
        if (fragment == null)
            return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rootLayout, fragment, tag);
        transaction.show(fragment);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_logout){

            session.logoutUser();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }else if (id == R.id.action_favorite) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }*/


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void getListOfFarms(int userId) {

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String URL_PROJECTS = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userId + "/farms";
        final String _TAG = "LIST OF FARMSY: ";
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.GET, URL_PROJECTS, null, (Response.Listener<JSONArray>) this, (Response.ErrorListener) this, "getListOfFarmsMainActivity");
        progressDialog.hide();
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(req, _TAG);
    }

    private void verifyData() {
        Log.d("MY FARMS MAIN :  ", "verifyData");
        if (hasFarms) {
            Log.d("MY FARMS MAIN : ", String.valueOf(farmsList.size()));
            setupNavigationView();
        } else {
            Log.d("MY FARMS MAIN: ", " NO FARMSmobee");
            //pushFragment(new HomeFragmentNoFarm(), "homefragmentNoFarm");

            Intent i = new Intent(getApplicationContext(), NoFarmActivity.class);
            startActivity(i);
            finish();
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(MainActivity.this, error + " ::: ERRROR RESPONSE", Toast.LENGTH_SHORT).show();
        Log.d("ERROR:: ", error + " ::: ERRROR RESPONSE");
    }

    @Override
    public void onResponse(Object response) {
        if (response != null) {
            Log.d("ERROR:: ", "RESPONSE-> NOT NULL");
            if (response instanceof JSONArray) {

                Log.d("ERROR:: ", "RESPONSE-> IS ARRAY");

                try {

                    JSONObject farmObjx = ((JSONArray) response).getJSONObject(0);
                    String type = farmObjx.getString("Type");

                    JSONArray respArray = ((JSONArray) response).getJSONArray(1);

                    Log.d("ERROR:: ", "RESPONSE-> " + response.toString());
                    Log.d("ERROR:: ", "RESPONSE -> respArray -> " + respArray.toString());

                    if (type.equalsIgnoreCase("getTransactionList")) {
                        displayTransactionsList(respArray);
                    } else if (type.equalsIgnoreCase("getListOfProjects")) {
                        displayProjectsList(respArray);
                    } else if (type.equalsIgnoreCase("getListOfFarmsMainActivity")) {
                        displayFarmsList(respArray);
                    } else if (type.equalsIgnoreCase("getListOfFarmsHomeFragment")) {
                        displayFarmsListHomeFragment(respArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayFarmsListHomeFragment(JSONArray respArray) {
        farmsListHomeFragment.clear();
        for (int i = 0; i < respArray.length(); i++) {
            JSONObject farmObj = null;
            try {
                farmObj = respArray.getJSONObject(i);
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

                farmsListHomeFragment.add(farm);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // call fragment::: to display
        HomeFragment fragment = new HomeFragment();
        fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("homefragment");
        if (fragment != null) {
            fragment.prepareFarmsData(farmsListHomeFragment);
        }
    }

    private void displayTransactionsList(JSONArray respArray) {
        // initialise the TransactionsArray
        transactionList.clear();

        for (int i = 0; i < respArray.length(); i++) {

            JSONObject transactionObj = null;
            try {
                transactionObj = respArray.getJSONObject(i);

                Transaction obj = new Transaction();

                int id = transactionObj.getInt("id");
                int accountId = transactionObj.getInt("accountId");

                MathContext mc = MathContext.DECIMAL32;
                BigDecimal amount = new BigDecimal(transactionObj.getString("amount"), mc);

                String dateStr = transactionObj.getString("transactionDate"); // "expenseDate": "30-07-2018",
                Date transDate = null;
                try {
                    transDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
                    obj.setTransactionDate(transDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int transactionTypeId = transactionObj.getInt("transactionTypeId");
                String transactionType = transactionObj.getString("transactionType");
                String description = transactionObj.getString("description");
                int projectId = transactionObj.getInt("projectId");
                String projectName = transactionObj.getString("projectName");
                int userId = transactionObj.getInt("userId");
                int farmId = transactionObj.getInt("farmId");
                String farmName = transactionObj.getString("farmName");
                String accountName = transactionObj.getString("accountName");

                obj.setId(id);
                obj.setAccountId(accountId);
                obj.setAmount(amount);
                obj.setTransactionDate(transDate);
                obj.setTransactionTypeId(transactionTypeId);
                obj.setTransactionType(transactionType);
                obj.setDescription(description);
                obj.setProjectId(projectId);
                obj.setProjectName(projectName);
                obj.setUserId(userId);
                obj.setFarmId(farmId);
                obj.setFarmName(farmName);
                obj.setAccountName(accountName);

                transactionList.add(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // call fragment::: to display
        TransactionsFragment fragment = new TransactionsFragment();
        fragment = (TransactionsFragment) getSupportFragmentManager().findFragmentByTag("txnsFragment");
        if (fragment != null) {
            fragment.displayTransactionList(transactionList);
        }
    }

    private void displayProjectsList(JSONArray respArray) {

        projectList.clear();

        for (int i = 0; i < respArray.length(); i++) {

            JSONObject projObj = null;
            try {
                projObj = respArray.getJSONObject(i);
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

                projectList.add(project);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // call fragment::: to display
        ProjectsSettingsFragment fragment = new ProjectsSettingsFragment();
        fragment = (ProjectsSettingsFragment) getSupportFragmentManager().findFragmentByTag("txnsProjectsSettings");
        if (fragment != null) {
            fragment.prepareProjectsData(projectList);
        }
    }

    private void displayFarmsList(JSONArray respArray) {

        farmsList.clear();

        for (int i = 0; i < respArray.length(); i++) {
            hasFarms = true;
            JSONObject farmObj = null;
            try {
                farmObj = respArray.getJSONObject(i);
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        verifyData();
    }
}
