package com.vogella.android.myapplication.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.fragment.EditorFragment;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SampleAdapter extends FragmentPagerAdapter {
    Context ctxt=null;

    private List<Fragment> myFragments;
    //private ArrayList<String> categories;
    private ArrayList<Project> projects;

    public SampleAdapter(Context ctxt, FragmentManager mgr, List<Fragment> myFrags, ArrayList<Project> projects) {
        super(mgr);
        this.ctxt=ctxt;

        myFragments = myFrags;
        //this.categories = cats;
        this.projects = projects;
    }

    @Override
    public int getCount() {
        return myFragments.size();
        //return(10);
    }

    @Override
    public Fragment getItem(int position) {
        Project proj = projects.get(position);
        return( EditorFragment.newInstance( position, proj ) );
    }

    @Override
    public String getPageTitle(int position) {
        Toast.makeText(ctxt,"SampleAdapter :-> getPageTitle() ARRAY SIZE->" + projects.size(), Toast.LENGTH_LONG).show(); // jhsd afasd fhasdkfhasd hfjkasd fsd hkk
        Project proj = projects.get(position);
        String title = proj.getProjectName();
        return(EditorFragment.getTitle(ctxt, position, title ));
    }


}