package com.vogella.android.myapplication.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
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
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity  implements ProjectsSettingsFragment.OnFragmentInteractionListener {

    // Declaring the Toolbar Object
    private Toolbar toolbar;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    private Boolean hasFarms =  false;
    private Boolean hasProjects =  false;

    private String process =  "";

    private ArrayList<Farm> farmsList = new ArrayList<>();

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

        if(!session.isLoggedIn()) {
            //session.checkLogin();
            //user = session.getUser();
            //userId = user.getId();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }else{
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

    private void verifyData(){
        Log.d("MY FARMS :  ", "verifyData" );
        if(hasFarms){
            Log.d("MY FARMS : ", String.valueOf(farmsList.size()) );
            setupNavigationView();
        }else{
            Log.d("MY FARMS : ", " NO FARMSmobee" );
            //pushFragment(new HomeFragmentNoFarm(), "homefragmentNoFarm");

            Intent i = new Intent(getApplicationContext(), NoFarmActivity.class);
            startActivity(i);
            finish();
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
                    if(fragment!=null){
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
                    if(fragment!=null){
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




    public void getListOfFarms(int userId){

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String URL_PROJECTS = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userId + "/farms";
        final String  _TAG = "LIST OF FARMS: ";
        CustomJsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest(
                Request.Method.GET,
                URL_PROJECTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response == null){
                            hasFarms = false;
                        }else{
                            try {
                                for(int i=0;i<response.length();i++){

                                    hasFarms = true;

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
                                Log.d(_TAG, "TRY ERROR" + e.getMessage());
                            }
                        }
                        verifyData();

                        progressDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(_TAG, "Error: " + error.getMessage());
                Log.d(_TAG, "Error: " + error.getMessage());
                verifyData();
            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest,_TAG);
    }
}
