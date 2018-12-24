package com.vogella.android.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenseActivity extends AppCompatActivity {

    private static final String TAG = "ExpenseActivity";

    @BindView(R.id.expenseAmount)  EditText _expenseAmount;
    @BindView(R.id.expenseDate) EditText _expenseDate;
    @BindView(R.id.btnSubmitExpense) Button _btnSubmitExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        ButterKnife.bind(this);

        _btnSubmitExpense.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitExpense();
            }
        });

    }

    public void submitExpense() {
        Log.d(TAG, "submitExpense");

        _btnSubmitExpense.setEnabled(false);

        String expenseAmount = _expenseAmount.getText().toString();
        String expenseDate =  _expenseDate.getText().toString();

        Log.d(TAG, "expenseAmount");
        Log.d(TAG, "expenseDate");
    }
}
