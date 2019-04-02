package com.vogella.android.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Farm;

import java.util.List;

public class farmsListAdapter extends RecyclerView.Adapter<farmsListAdapter.MyViewHolder> {

    private List<Farm> farmsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView listFarmDescription, listFarmName;

        public MyViewHolder(View view) {
            super(view);
            listFarmName = (TextView) view.findViewById(R.id.listFarmName);
            listFarmDescription = (TextView) view.findViewById(R.id.listFarmDescription);
        }
    }


    public farmsListAdapter(List<Farm> farmsList) {
        this.farmsList = farmsList;
    }

    @Override
    public farmsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.farms_list_row_home, parent, false);

        return new farmsListAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(farmsListAdapter.MyViewHolder holder, int position) {
        Farm farm = farmsList.get(position);
        holder.listFarmName.setText(farm.getFarmName());
        holder.listFarmDescription.setText(farm.getDescription());
    }

    @Override
    public int getItemCount() {
        return farmsList.size();
    }
}

