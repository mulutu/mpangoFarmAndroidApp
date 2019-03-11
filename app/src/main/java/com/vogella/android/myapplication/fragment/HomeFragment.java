package com.vogella.android.myapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.TransactionActivity;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment  {

    private Button btn1, btn2;

    private FragmentActivity myContext;

    private Transaction transaction = new Transaction();

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUser();
        userId = user.getId();

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

        transaction.setUserId(userId);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("Process", "NEW_TRANSACTION" );

                transaction.setTransactionTypeId(1);
                extras.putSerializable("Transaction", transaction );
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("Process", "NEW_TRANSACTION" );

                transaction.setTransactionTypeId(0);
                extras.putSerializable("Transaction", transaction );
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
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
