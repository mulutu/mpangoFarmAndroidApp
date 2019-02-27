package com.vogella.android.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Project;

public class AddFarmActivity extends AppCompatActivity {

    private EditText _farmName, _farmDesc;
    private Button _btnSubmitFarm;
    Farm project = new Farm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_farm);
    }
}
