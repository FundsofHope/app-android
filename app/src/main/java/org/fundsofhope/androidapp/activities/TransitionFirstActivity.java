package org.fundsofhope.androidapp.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
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
import org.fundsofhope.androidapp.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TransitionFirstActivity extends Activity {

    private View fabButton;
    ProgressDialog progressDialog;
    JSONObject jobj = null;
    String TAG = null;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_first);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = pref.getString("token", "");
        // Set explode animation when enter and exit the activity
        Utils.configureWindowEnterExitTransition(getWindow());

        // Fab Button
        fabButton = findViewById(R.id.fab_button);
        fabButton.setOnClickListener(fabClickListener);
        Utils.configureFab(fabButton);
    }


    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent i = new Intent(TransitionFirstActivity.this, TransitionSecondActivity.class);

            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(TransitionFirstActivity.this,
                    Pair.create(fabButton, "fab"));

            startActivity(i, transitionActivityOptions.toBundle());
        }
    };
    private InputStream is = null;
    private String page_output = "";

    private class LoginTask extends AsyncTask<String, Integer, JSONObject> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TransitionFirstActivity.this,
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
                String URL = "http://fundsofhope.herokuapp.com/user/?" + token;
                HttpClient Client = new DefaultHttpClient();
                Log.i(TAG, "created client");
//			try{
//				String Response="";
                HttpGet httpget = new HttpGet(URL);
                Log.i(TAG, "hhtp get");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.i(TAG, "in response handler");

                List<NameValuePair> data = new ArrayList<NameValuePair>();

                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpResponse httpResponse = httpClient.execute(httpget);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.i(TAG, "executed");
                is = httpEntity.getContent();
                Log.i(TAG, "in strict mode");

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    page_output = sb.toString();
                    jobj = new JSONObject(page_output);
                    Log.i("LOG", "page_output --> " + page_output);
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
                if (result.getBoolean("success")) {
                    //onLoginSuccess();
                    // successlog();
                } else if (!result.getBoolean("success")) {
                    // onLoginFailed();
                    Intent inte=new Intent(TransitionFirstActivity.this,LoginActivity.class);
                    startActivity(inte);
                } else {
                    Toast.makeText(TransitionFirstActivity.this, "Can't Connect", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

