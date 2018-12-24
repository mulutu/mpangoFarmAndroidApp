package com.vogella.android.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomerLogin extends AsyncTask<String, String, Void> {
    private Context context;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(String... arg0) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String response = "";


        String URL_LOGIN = "http://45.56.73.81:8084/MpangoFarmEngineApplication/api/login/";

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(URL_LOGIN);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setReadTimeout(15000);
            //urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email", arg0[0]);
            jsonParam.put("username", arg0[0]);
            jsonParam.put("password", arg0[1]);
            jsonParam.put("firstname", "");
            jsonParam.put("lastname", "");

            Log.i("USER: ", jsonParam.toString());

            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int ch;
            StringBuilder sb = new StringBuilder();
            while((ch = in.read()) != -1)
                sb.append((char)ch);


            Log.i("msg: ", sb.toString());

            Log.i("STATUS", String.valueOf(urlConnection.getResponseCode()));
            Log.i("MSG" , urlConnection.getResponseMessage());


            urlConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
