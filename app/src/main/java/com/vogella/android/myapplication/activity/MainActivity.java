package com.vogella.android.myapplication.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.Toast;

import com.vogella.android.myapplication.fragment.EditorFragment;
import com.vogella.android.myapplication.fragment.HomeFragment;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.fragment.PagerFragment;
import com.vogella.android.myapplication.fragment.ProjectsSettingsFragment;
import com.vogella.android.myapplication.fragment.TransactionsFragment;

import java.util.List;

import butterknife.BindView;


public class MainActivity extends AppCompatActivity implements ProjectsSettingsFragment.OnFragmentInteractionListener {

    //private Button btn1, btn2;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        setupNavigationView();
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

                Fragment fr = getSupportFragmentManager().findFragmentByTag("homefragment");
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
                }

                break;

            case R.id.navigation_transactions:

                List<Fragment> allFragments = getSupportFragmentManager().getFragments();
                for (Fragment fragment : allFragments) {
                    if(fragment!=null){
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        trx.hide(fragment);
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
                        trx.hide(fragment);
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
            Context context = getApplicationContext();
            //SharedPreferences sharedPreferences = context.getSharedPreferences("vidslogin", Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().remove("rememberAccount").commit();
            sharedPreferences.edit().remove("userid").commit();
            sharedPreferences.edit().remove("username").commit();
            sharedPreferences.edit().remove("password").commit();
            sharedPreferences.edit().remove("vidslogin").commit();
            sharedPreferences.edit().clear().commit(); // jksd fsdh ghksdf gkhsdf ghdfsjk ghsdfjkhg jksdfhjk ghdjk

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish(); // jkdfhgjkdfhjkd sfjkd gjksdfh gjkhdf jghsdfjk gdfkhgjksdf
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
