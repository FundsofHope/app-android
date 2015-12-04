package org.fundsofhope.androidapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.fundsofhope.androidapp.R;
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
 * Created by Anip on 12/4/2015.
 */
public class AddProject extends AppCompatActivity {
    EditText title;
    EditText desc;
    EditText cost;
    Button add;
    String tit;
    String des;
    String cos;
    ProgressDialog progressDialog;
    SharedPreferences.Editor editor;
    String TAG;
    JSONObject jobj=null;
    String token;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_project);
        title = (EditText) findViewById(R.id.title);
        desc = (EditText) findViewById(R.id.description);
        cost = (EditText) findViewById(R.id.cost);
        add = (Button) findViewById(R.id.add);
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token=pref.getString("token","");
        id=pref.getString("email","");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               tit= title.getText().toString();
                des=desc.getText().toString();
                cos=cost.getText().toString();
                new LoginTask().execute("");
            }
        });

    }

    private InputStream isi = null;
    private String page_outputo = "";

    private class LoginTask extends AsyncTask<String, Integer, JSONObject> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddProject.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            // progressDialog.dismiss();


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub


//            Log.i(TAG,uname.getText().toString()+"ksjdvnslkdvxnwadlk");
//			Log.i(TAG,pass.getText().toString()+"fsdxcjvnskjdn");
            try {
                Log.i(TAG, "entered try()");

                //Toast.makeText(getApplicationContext(), "Please wait,connecting to server",Toast.LENGTH_LONG).show();
                Log.i(TAG, "entered toast()");
                //Log.i(TAG,email);
                //Log.i(TAG,password);
                String URL = "http://fundsofhope.org/ngo/"+id+"/project/";
                HttpClient Client = new DefaultHttpClient();
                Log.i(TAG, "created client");
//			try{
//				String Response="";
                List<NameValuePair> data = new ArrayList<NameValuePair>();
                HttpGet httpget = new HttpGet(URL);
                Log.i(TAG, "hhtp get" + token);
                httpget.addHeader("x-access-token", token);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.i(TAG, "in response handler");

                data.add(new BasicNameValuePair("title", tit));
                data.add(new BasicNameValuePair("decription", des));
                data.add(new BasicNameValuePair("cost", cos));
                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(URL);
                httpPost.addHeader("x-access-token",token);
                httpPost.setEntity(new UrlEncodedFormEntity(data));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.i(TAG, "executed");
                isi = httpEntity.getContent();
                Log.i(TAG, "in strict mode");

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(isi, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    isi.close();
                    page_outputo = sb.toString();
                    jobj = new JSONObject(page_outputo);
                    Log.i("LOG", "page_output --> " + page_outputo);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                Log.i(TAG, "request executed");

            } catch (UnsupportedEncodingException e) {
            } catch (IOException e) {
            }
            Log.i(TAG, "returning response");
            return jobj;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(JSONObject result) {
            Log.i(TAG, "Entered on post execute");
            progressDialog.dismiss();


            //Toast.makeText(LoginActivity.this,"length="+result.length()+result, Toast.LENGTH_LONG).show();

            try {
                if (result.getString("message").contains("Created")) {
                    //onLoginSuccess();
                    // successlog()
                    //;
                    Toast.makeText(AddProject.this, "New Project Created", Toast.LENGTH_LONG).show();
                    Intent inte=new Intent(AddProject.this,MainActivity.class);
                    startActivity(inte);
                }else {
                    Toast.makeText(AddProject.this, "Can't Connect to the servers", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}