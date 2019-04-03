package com.vogella.android.myapplication.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity  {
    CalendarView calendar;
    private String process =  "";
    private Transaction transaction;
    private Toolbar toolbar;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        manageSession();

        populateTitleBar();

        manageVariables();

        initializeCalendar();
    }

    private void manageSession(){
        session = new SessionManager(getApplicationContext());
        user = session.getUser();
        userId = user.getId();
    }

    private void manageVariables(){
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
    }

    private void populateTitleBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Select date");
        }
    }

    private Date stringToDate(String dateStr){
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public void initializeCalendar() {
        calendar = (CalendarView) findViewById(R.id.calendarView);
        // sets whether to show the week number.
        calendar.setShowWeekNumber(false);
        // sets the first day of week according to Calendar.
        // here we set Monday as the first day of the Calendar
        //calendar.setFirstDayOfWeek(2);
        //calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
        //calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
        //calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
        //calendar.setSelectedDateVerticalBar(R.color.darkgreen);
        //calendar.setFocusedMonthDateColor(Color.RED); // set the red color for the dates of  focused month
        //calendar.setUnfocusedMonthDateColor(Color.BLUE); // set the yellow color for the dates of an unfocused month
        //calendar.setSelectedWeekBackgroundColor(Color.RED); // red color for the selected week's background
        //calendar.setWeekSeparatorLineColor(Color.GREEN); // green color for the week separator line
        //sets the listener to be notified upon selected date change.
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                String dateStr = day + "-" + (month+1) + "-" + year;
                Bundle extras = new Bundle();
                Date strTrxDate = stringToDate(dateStr);
                transaction.setTransactionDate(strTrxDate);
                extras.putSerializable("Transaction", transaction);

                if( process.equalsIgnoreCase("NEW_TRANSACTION") ){
                    Intent intent = new Intent(getApplicationContext(), AddTransactionActivity.class);
                    extras.putString("Process", "NEW_TRANSACTION");
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }else if( process.equalsIgnoreCase("EDIT_TRANSACTION") ){
                    Intent intent = new Intent(getApplicationContext(), EditTransactionActivity.class);
                    extras.putString("Process", "EDIT_TRANSACTION");
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_logout:
                session.logoutUser();

                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;

            case R.id.action_favorite:
                Toast.makeText(this, "Action clicked", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
