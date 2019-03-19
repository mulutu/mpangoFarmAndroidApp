package com.vogella.android.myapplication.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.fragment.HomeFragment;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.fragment.ProjectsSettingsFragment;
import com.vogella.android.myapplication.fragment.TransactionsFragment;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.SessionManager;

import java.util.List;


public class MainActivity extends AppCompatActivity  implements ProjectsSettingsFragment.OnFragmentInteractionListener {

    // Declaring the Toolbar Object
    private Toolbar toolbar;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attaching the layout to the toolbar object
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();
        userId = user.getId();

        setupNavigationView();
    }
  /*
        btnLogout.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // Clear the session data
            // This will clear all session data and
            // redirect user to LoginActivity
            session.logoutUser();
        }
    });
*/
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
                    if(fragment!=null){
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        //trx.hide(fragment);
                        trx.remove(fragment);
                        trx.commit();
                    }
                }
                pushFragment(new HomeFragment(), "homefragment");

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

        /*FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }*/
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rootLayout, fragment, tag);
        transaction.show(fragment);
        transaction.commit();
        //return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
