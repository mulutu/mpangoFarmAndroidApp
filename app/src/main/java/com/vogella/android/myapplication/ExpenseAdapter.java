package com.vogella.android.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder>{

    private List<Expense> transactionsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amount;

        public MyViewHolder(View view) {
            super(view);
            amount = (TextView) view.findViewById(R.id.amount);
            //genre = (TextView) view.findViewById(R.id.genre);
            //year = (TextView) view.findViewById(R.id.year);
        }
    }


    public ExpenseAdapter(List<Expense> trxList) {
        this.transactionsList = trxList;
        Log.d("ADAPTER:: ", "transactionsList LIST: " + transactionsList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Log.d("ADAPTER:: ", " POSITION : " + position);
        Log.d("ADAPTER:: ", " transactionsList LEN : " + transactionsList.size());
        Log.d("ADAPTER:: ", " transactionsList item 0 : " + transactionsList.get(0));



        Expense expensex = transactionsList.get(0);

        Log.d("ADAPTER:: ", " EXP NUMBER 0  : getFarmName " + expensex.getFarmName());

        Expense expense = transactionsList.get(position);
        //holder.title.setText(expense.getId());
        holder.amount.setText(expense.getAmount().toString());
        //holder.year.setText(expense.getExpenseDate().toString());

        Log.d("ADAPTER:: ", "transactionsList POSITION : " + position);
        Log.d("ADAPTER:: ", "transactionsList getProjectName : " + expense.getProjectName());
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }
}
