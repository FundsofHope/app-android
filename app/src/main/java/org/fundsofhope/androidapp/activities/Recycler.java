package org.fundsofhope.androidapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fundsofhope.androidapp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anip on 12/3/2015.
 */
public class Recycler extends AppCompatActivity {
    ProgressDialog progressDialog;
    String TAG=null;
    String token;
    JSONObject jobj=null;
    SharedPreferences.Editor editor;
    RecyclerView recList;
    String[] ttitle;
    String[] ddesc;
//    String[] my1 = { "One", "Two", "Three" };
  //  String[] my = { "One", "Two", "Three" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler);
        SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sc = mpref.getString("projects", "");
        Log.i(TAG, "projects is" + sc);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, String.valueOf(jsonArray.length()));
        String[] ttitle = new String[jsonArray.length()];
        String[] ddesc = new String[jsonArray.length()];
        //title = new String[jsonArray.length()];
        //desc = new String[jsonArray.length()];
        //String[] ddate = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                ttitle[i] = obj.getString("title");
                Log.i(TAG, ttitle[i]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {


                ddesc[i] = String.valueOf(obj.getInt("cost"));
                Log.i(TAG, ddesc[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            //Bitmap bi;
            //       new LoginTask().execute("");
            ContactAdapter ca = new ContactAdapter(ttitle, ddesc);
            recList.setAdapter(ca);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);


            recList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent int1 = new Intent(Recycler.this, TransitionFirstActivity.class);
                    startActivity(int1);
                }
            });
        }
    }




protected void onSuccess(){
    SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String sc = mpref.getString("projects", "");
    Log.i(TAG,"projects is"+sc);
    JSONArray jsonArray = null;
    try {
        jsonArray = new JSONArray(sc);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    Log.i(TAG, String.valueOf(jsonArray.length()));
   ttitle = new String[jsonArray.length()];
    ddesc = new String[jsonArray.length()];
    //title=new String[jsonArray.length()];
    //desc=new String[jsonArray.length()];
    //String[] ddate = new String[jsonArray.length()];
    for(int i=0;i<jsonArray.length();i++){
        JSONObject obj=null;
        try {
            obj=jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            ttitle[i]=obj.getString("title");
            Log.i(TAG,ttitle[i]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {


            ddesc[i]= String.valueOf(obj.getInt("cost"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
    //for(int i=0;i<ttitle.length;i++){




}
