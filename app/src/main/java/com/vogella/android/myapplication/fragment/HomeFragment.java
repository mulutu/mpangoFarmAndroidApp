package com.vogella.android.myapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.ExpenseActivity;
import com.vogella.android.myapplication.activity.IncomeActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment  {

    private Button btn1, btn2;

    private FragmentActivity myContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (myContext.getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            myContext.getSupportFragmentManager().beginTransaction().add(android.R.id.content, new PagerFragment(), "pagerfragment").commit();
        }

        myContext.getSupportFragmentManager().getFragments();
    }

    public List<Fragment> getVisibleFragments() {
        List<Fragment> allFragments = myContext.getSupportFragmentManager().getFragments();
        if (allFragments == null || allFragments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Fragment> visibleFragments = new ArrayList<Fragment>();
        /*for (Fragment fragment : allFragments) {
            if (fragment.isVisible()) {
                visibleFragments.add(fragment);
            }
        }*/
        return allFragments;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        btn1= (Button)rootView.findViewById(R.id.expense);
        btn2= (Button)rootView.findViewById(R.id.income);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ExpenseActivity.class);
                startActivity(i);

                //FragmentManager fm = myContext.getSupportFragmentManager();
                //for(int k = 0; k < fm.getBackStackEntryCount(); ++k) {
                    //fm.popBackStack();
                //}


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), IncomeActivity.class);
                startActivity(i);

                //FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
                //transaction.remove(new PagerFragment());
                //transaction.commit();


            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

}
