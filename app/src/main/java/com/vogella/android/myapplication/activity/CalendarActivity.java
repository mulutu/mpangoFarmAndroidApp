package com.vogella.android.myapplication.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.EditText;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Expense;
import com.vogella.android.myapplication.model.Income;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity  {
    CalendarView calendar;

    private Income _income;

    private Expense _expense;

    private String trxType =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(bundle.get("transactionType").equals("EXPENSE")){
                if(!bundle.getSerializable("expense").equals(null)){
                    _expense = (Expense)bundle.getSerializable("expense");
                    trxType = "EXPENSE";
                }
            }else if(bundle.get("transactionType").equals("INCOME")){
                if(!bundle.getSerializable("income").equals(null)){
                    _income = (Income)bundle.getSerializable("income");
                    trxType = "INCOME";
                }
            }
        }
        initializeCalendar();
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

                if( trxType.equalsIgnoreCase("INCOME") ){
                    _income.setIncomeDate(strTrxDate);

                    extras.putSerializable("income", _income);

                    Intent intent = new Intent(getApplicationContext(), IncomeViewActivity.class);
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }else if( trxType.equalsIgnoreCase("EXPENSE") ){
                    _expense.setExpenseDate(strTrxDate);

                    extras.putSerializable("expense", _expense);

                    Intent intent = new Intent(getApplicationContext(), ExpenseViewActivity.class);
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
}
