package org.fundsofhope.androidapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.fundsofhope.androidapp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anip on 12/3/2015.
 */
public class ProjectsActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    String TAG=null;
    String token;
    JSONObject jobj=null;
    SharedPreferences.Editor editor;
    RecyclerView recList;
    String[] ttitle;
    String[] ddesc;
    String [] image;
    String [] about;
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
        String[] image = new String [jsonArray.length()];
        String[] about =new String  [jsonArray.length()];
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
                about[i] = obj.getString("description");
                Log.i(TAG, ttitle[i]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {


                ddesc[i] = String.valueOf(obj.getInt("cost"));
                Log.i(TAG, ddesc[i]);
                if(obj.getString("imageURL")!=null)
                    image[i]= obj.getString("imageURL");
                    else
                    image[i]= "https://upload.wikimedia.org/wikipedia/commons/c/c9/Chennai_india.jpg";

            } catch (JSONException e) {
                e.printStackTrace();
            }


            RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
           ;
            //Bitmap bi;
            //       new LoginTask().execute("");
            ContactAdapter ca = new ContactAdapter(ttitle, ddesc,about,image,this);
            recList.setAdapter(ca);

            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);


            recList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent int1 = new Intent(ProjectsActivity.this, ProjectAddActivity.class);
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
