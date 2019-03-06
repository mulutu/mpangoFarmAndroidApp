package com.vogella.android.myapplication.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Account;
import com.vogella.android.myapplication.model.Project;

import java.util.List;

public class accountsAdapter extends RecyclerView.Adapter<accountsAdapter.MyViewHolder> {

    private List<Account> accountsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView listAccountDescription, listAccountName;

        public MyViewHolder(View view) {
            super(view);
            listAccountName = (TextView) view.findViewById(R.id.listAccountName);
            listAccountDescription = (TextView) view.findViewById(R.id.listAccountDescription);
        }
    }

    public accountsAdapter(List<Account> accountsList) {
        this.accountsList = accountsList;
    }

    @NonNull
    @Override
    public accountsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounts_list_row, parent, false);
            return new accountsAdapter.MyViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounts_list_row, parent, false);
            return new accountsAdapter.MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(accountsAdapter.MyViewHolder holder, int position) {
        Account account = accountsList.get(position);
        holder.listAccountName.setText(account.getAccountName());
        holder.listAccountDescription.setText(account.getDescription());
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }

}
