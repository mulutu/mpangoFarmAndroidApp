package com.vogella.android.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Transaction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder>{

    private List<Transaction> transactionsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionAmount, transactionDesc, transactionDate, transactionType, transactionID;

        public MyViewHolder(View view) {
            super(view);
            transactionAmount = (TextView) view.findViewById(R.id.transactionAmount);
            transactionDesc = (TextView) view.findViewById(R.id.transactionDescription);
            transactionDate = (TextView) view.findViewById(R.id.transactionDate);
        }
    }


    public TransactionAdapter(List<Transaction> trxList) {
        this.transactionsList = trxList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Transaction transaction = transactionsList.get(position);

        holder.transactionDesc.setText(transaction.getDescription());
        holder.transactionAmount.setText("KES " + currencyFormat(transaction.getAmount().toString()));

        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String trxDate = sdf.format(transaction.getTransactionDate());

        holder.transactionDate.setText(trxDate);
        if(transaction.getTransactionType().equalsIgnoreCase("EXPENSE")) {
            holder.transactionAmount.setTextColor(Color.parseColor("#FF0000"));
        }else{
            holder.transactionAmount.setTextColor(Color.parseColor("#008000"));
        }
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    private String currencyFormat(String amount){
        double harga = Double.parseDouble(amount);
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator('.');
        dfs.setGroupingSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        String k = df.format(harga);

        return k;
    }
}
