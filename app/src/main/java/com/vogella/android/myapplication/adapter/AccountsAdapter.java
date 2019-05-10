package com.vogella.android.myapplication.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.util.SectionOrRow;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SectionOrRow> mData;

    public class RowViewHolder  extends RecyclerView.ViewHolder {
        public TextView listAccountDescription, listAccountName;
        public RowViewHolder(View view) {
            super(view);
            listAccountName = (TextView) view.findViewById(R.id.listAccountName);
            listAccountDescription = (TextView) view.findViewById(R.id.listAccountDescription);
        }
    }
    public class SectionViewHolder  extends RecyclerView.ViewHolder {
        public TextView listSectionName;
        public SectionViewHolder (View view) {
            super(view);
            listSectionName = (TextView) view.findViewById(R.id.listSectionName);
            //listAccountDescription = (TextView) view.findViewById(R.id.listAccountDescription);
        }
    }

    public AccountsAdapter(List<SectionOrRow> data) {
        mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounts_list_section, parent, false);
            return new AccountsAdapter.SectionViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounts_list_row, parent, false);
            return new AccountsAdapter.RowViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        SectionOrRow item = mData.get(position);
        if(!item.isRow()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SectionOrRow item = mData.get(position);
        if(item.isRow()) {
            RowViewHolder h = (RowViewHolder) holder;
            h.listAccountName.setText(item.getRow());
            h.listAccountDescription.setText(item.getRow2());
        } else {
            SectionViewHolder k = (SectionViewHolder) holder;
            k.listSectionName.setText(item.getSection());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
