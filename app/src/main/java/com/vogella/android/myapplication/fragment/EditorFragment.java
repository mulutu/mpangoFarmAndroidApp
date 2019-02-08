package com.vogella.android.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vogella.android.myapplication.R;
import com.vogella.android.myapplication.model.Project;
import com.vogella.android.myapplication.util.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class EditorFragment extends Fragment {
    private static final String KEY_POSITION="position";

    //private static String title = "";

    public static EditorFragment newInstance(int position, Project proj) {
        EditorFragment frag=new EditorFragment();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        args.putSerializable("proj", proj);

        frag.setArguments(args);

        return(frag);
    }

    public static String getTitle(Context ctxt, int position, String title) {
        //Toast.makeText(null,"PROJECTS:->" + projects.size(), Toast.LENGTH_LONG).show();
        //return(String.format(ctxt.getString(R.string.hint), position + 1));
        return title;
    }

    public static String getText(Context ctxt, int position) {
        return(" jsfjkhs kjfhsdjk fhsdjh fsdjk fjsd hfsdhjkf ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.editor, container, false);

        TextView tv = (TextView)result.findViewById(R.id.editor);
        int position = getArguments().getInt(KEY_POSITION, -1);

        TextView tvOne = (TextView)result.findViewById(R.id.txtOne);
        TextView tvTwo = (TextView)result.findViewById(R.id.txtTwo);
        TextView tvProfit = (TextView)result.findViewById(R.id.txtProfit);


        Project project = (Project) getArguments().getSerializable("proj");
        String projDesc = project.getDescription();

        int expected = project.getExpectedOutput();
        int actual = project.getActualOutput();
        BigDecimal totalExpenses = project.getTotalExpenses();
        BigDecimal totalIncomes = project.getTotalIncomes();

        MathContext mc = new MathContext(2); // 2 precision

        BigDecimal profit = totalIncomes.subtract(totalExpenses, MathContext.DECIMAL64);

        //if (gp.compareTo(BigDecimal.ZERO) > 0){ }

        tvOne.setText( currencyFormat(totalIncomes.toString()));
        tvTwo.setText( currencyFormat(totalExpenses.toString()));
        tvProfit.setText( currencyFormat(profit.toString()));

        //tv.setText(projDesc);

        //tv.setText(getText(getActivity(), position));
        //EditText editor=(EditText)result.findViewById(R.id.editor);
        //int position=getArguments().getInt(KEY_POSITION, -1);
        //editor.setHint(getTitle(getActivity(), position));

        return(result);
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
