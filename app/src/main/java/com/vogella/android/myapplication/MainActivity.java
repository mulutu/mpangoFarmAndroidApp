package com.vogella.android.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity {

    private Button btn1, btn2;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);


        btn1= (Button)findViewById(R.id.expense);
        btn2= (Button)findViewById(R.id.income);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ExpenseActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, IncomeActivity.class);
                startActivity(i);
                finish();
            }
        });


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
            Toast.makeText(this, "SETTINGS::: ", LENGTH_LONG).show();
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
}
