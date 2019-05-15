package com.vogella.android.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.activity.AddTransactionActivity;
import com.vogella.android.myapplication.activity.tasks.TaskActivity;
import com.vogella.android.myapplication.activity.tasks.TaskNoteActivity;
import com.vogella.android.myapplication.activity.user.LoginActivity;
import com.vogella.android.myapplication.adapter.TaskAdapter;
import com.vogella.android.myapplication.model.MyUser;
import com.vogella.android.myapplication.model.Task;
import com.vogella.android.myapplication.model.Transaction;
import com.vogella.android.myapplication.util.AlertDialogManager;
import com.vogella.android.myapplication.util.AppSingleton;
import com.vogella.android.myapplication.util.CustomJsonArrayRequest;
import com.vogella.android.myapplication.util.MyDividerItemDecoration;
import com.vogella.android.myapplication.util.RecyclerTouchListener;
import com.vogella.android.myapplication.util.SessionManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Task> taskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Transaction> expenseList = new ArrayList<>();
    private List<Transaction> incomeList = new ArrayList<>();
    final String _TAG = "TRANSACTIONS FRAGMENT: ";
    final String TAG = "REQUEST_QUEUE";
    private static final int REQUEST_TRANSACTION = 0;
    private com.vogella.android.myapplication.activity.TextView_Lato btnAddIncome, btnAddExpense;
    private Transaction transaction = new Transaction();
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private MyUser user;
    private int userId;
    private Toolbar toolbar;
    FloatingActionButton floatingActionButton, fab1, fab2, fab3;
    private boolean isFABOpen = false;

    private LinearLayout layoutFab1, layoutFab2, layoutFab3;

    public TasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance(String param1, String param2) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        setHasOptionsMenu(true);

        populateTitleBar(rootView);

        btnAddIncome = (com.vogella.android.myapplication.activity.TextView_Lato) rootView.findViewById(R.id.addIncomeTransactions);
        btnAddExpense = (com.vogella.android.myapplication.activity.TextView_Lato) rootView.findViewById(R.id.addExpenseTransactions);

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUser();
        userId = user.getId();

        transaction.setUserId(userId);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_tasks);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        getTasksList(userId);

        manageFloatingButton(rootView);

        return rootView;
    }

    private void manageFloatingButton(View rootView){
        floatingActionButton = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) rootView.findViewById(R.id.fab3);

        layoutFab1 = (LinearLayout) rootView.findViewById(R.id.fab1lay);
        layoutFab2 = (LinearLayout) rootView.findViewById(R.id.fab2lay);
        layoutFab3 = (LinearLayout) rootView.findViewById(R.id.fab3lay);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }

                /*Bundle extras = new Bundle();
                extras.putInt("projectId", 1 );
                Intent intent = new Intent(getActivity().getApplicationContext(), TaskNoteActivity.class);
                intent.putExtras(extras);
                startActivityForResult(intent, REQUEST_TRANSACTION);*/

            }
        });
    }

    private void showFABMenu(){
        isFABOpen=true;
        layoutFab1.setVisibility(View.VISIBLE);
        layoutFab2.setVisibility(View.VISIBLE);
        layoutFab3.setVisibility(View.VISIBLE);
        //fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        //fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        //fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        layoutFab1.setVisibility(View.INVISIBLE);
        layoutFab2.setVisibility(View.INVISIBLE);
        layoutFab3.setVisibility(View.INVISIBLE);
        //fab1.animate().translationY(0);
        //fab2.animate().translationY(0);
        //fab3.animate().translationY(0);
    }


    private void populateTitleBar(View rootView) {
        toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle("Tasks");
        }
    }

    private void getTasksList(int userId) {
        String URL_ = "http://45.56.73.81:8084/Mpango/api/v1/users/" + userId + "/tasks";
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.GET, URL_, null, (Response.Listener<JSONArray>) getActivity(), (Response.ErrorListener) getActivity(), "getTaskListFragment");
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req, TAG);
    }

    public void displayTasksList(final List<Task> tasklist2) {

        if (tasklist2.size() > 0) {
            taskAdapter = new TaskAdapter(tasklist2);
            taskAdapter.notifyDataSetChanged();

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
            recyclerView.setAdapter(taskAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Task task = tasklist2.get(position);

                    Bundle extras = new Bundle();
                    extras.putSerializable("Task", task );
                    extras.putInt("id", task.getTaskId() );
                    extras.putSerializable("Process", "EDIT_TASK" );

                    Intent intent = new Intent(getActivity().getApplicationContext(), TaskNoteActivity.class);
                    intent.putExtras(extras);

                    startActivityForResult(intent, REQUEST_TRANSACTION);
                    //getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
        Log.d("displayTasksList", "displayTasksList() METHOD:  LIST SIZE" + tasklist2.size());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            case R.id.action_logout:
                session.logoutUser();
                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
                return true;

            case R.id.action_favorite:
                Toast.makeText(getActivity().getApplicationContext(), "Action clicked", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
