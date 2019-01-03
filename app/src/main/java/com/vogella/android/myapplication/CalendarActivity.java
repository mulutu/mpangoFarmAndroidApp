package com.vogella.android.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity  {
    CalendarView calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //initializes the calendarview
        initializeCalendar();

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
                Intent i = new Intent(getApplicationContext(), ExpenseActivity.class);
                i.putExtra("dateStr",dateStr);
                startActivity(i);
            }
        });
    }
}
