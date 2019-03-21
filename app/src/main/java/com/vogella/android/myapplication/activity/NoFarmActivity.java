package com.vogella.android.myapplication.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoFarmActivity extends AppCompatActivity {

    // Declaring the Toolbar Object
    private Toolbar toolbar;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    @BindView(R.id.btnCreateFarm) Button _btnCreateFarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_farm);

        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Add Farm");

        session = new SessionManager(getApplicationContext());

        _btnCreateFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFarm();
            }
        });
    }

    private void createFarm(){
        Bundle bundle = new Bundle();
        bundle.putString("Process", "INITIAL_ADD_FARM");
        bundle.putBoolean("HasFarms", false);

        Intent i = new Intent(getApplicationContext(), AddFarmActivity.class);
        i.putExtras(bundle);
        startActivity(i);
        finish();
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
            Toast.makeText(NoFarmActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
