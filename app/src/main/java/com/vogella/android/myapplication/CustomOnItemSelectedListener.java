package com.vogella.android.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

class CustomOnItemSelectedListener implements OnItemSelectedListener {
    Context ctx;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("SPINNER: ", parent.getItemAtPosition(position).toString());
        Toast.makeText(ctx, "Selected ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
