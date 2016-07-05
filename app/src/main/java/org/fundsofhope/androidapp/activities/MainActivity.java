/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.fundsofhope.androidapp.activities;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fundsofhope.androidapp.R;
import org.fundsofhope.androidapp.slidingtabs.fragments.SlidingTabsBasicFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navView;
    private Toolbar mainToolbar;
    ProgressDialog progressDialog;
    JSONObject jobj = null;
    String TAG = null;
    String token;
    int temp=1;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sliding);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        Button add;
        add=(Button)findViewById(R.id.fab_button);
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.i(TAG, "loged in as"+String.valueOf(pref.getInt("user", -1)));

        add.setClickable(true);

        //Bitmap bi;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte=new Intent(MainActivity.this,GoogleLoginActivity.class);
                startActivity(inte);
                finish();
            }
        });
//            Pushbots.sharedInstance().init(this);
            temp = 2;

        final SharedPreferences ppref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = ppref.getString("token", "");
        new LoginTask().execute("");

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        configureToolbar();
        configureDrawer();
    }

    private class LoginTask extends AsyncTask<String, Integer, JSONObject> {

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Toast.makeText(getApplicationContext(), "Please wait,connecting to server",Toast.LENGTH_LONG).show();
                String URL = "http://fundsofhope.herokuapp.com/user/";
                HttpClient Client = new DefaultHttpClient();
                Log.i(TAG, "created client");

                HttpGet httpget = new HttpGet(URL);
                httpget.addHeader("x-access-token",token);

                HttpResponse httpResponse = Client.execute(httpget);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.i(TAG, "executed");
                InputStream isi = httpEntity.getContent();
                Log.i(TAG, "in strict mode");

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(isi, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    isi.close();
                    String page_outputo = sb.toString();
                    jobj = new JSONObject(page_outputo);
                    Log.i("LOG", "page_output --> " + page_outputo);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }
                Log.i(TAG, "request executed");

            } catch (IOException ignored) {}
            return jobj;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(JSONObject result) {
            Log.i(TAG, "Entered on post execute");
            progressDialog.dismiss();

            Toast.makeText(MainActivity.this,"length="+result.length()+result, Toast.LENGTH_LONG).show();
            try {
                if (result.getString("message").contains("Home")) {
                    new GetProjectTask().execute("");
                } else if (!result.getBoolean("success")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = pref.edit();
                    editor.putInt("flag", 0);
                    editor.apply();

                    Intent inte = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(inte);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetProjectTask extends AsyncTask<String, Integer, JSONObject> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Log.i(TAG, "entered try()");

                Toast.makeText(getApplicationContext(), "Please wait,connecting to server",Toast.LENGTH_LONG).show();
                String URL = "http://fundsofhope.org/project/";
                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                httpget.addHeader("x-access-token", "");
                Log.i(TAG, "in response handler");

                HttpResponse httpResponse = Client.execute(httpget);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    is.close();
                    String page_output = sb.toString();
                    SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = mypref.edit();
                    editor.putString("projects", page_output);
                    editor.apply();
                    jobj = new JSONObject(page_output);
                    Log.i("LOG", "page_output --> " + page_output);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                Log.i(TAG, "request executed");

            } catch (IOException ignored) {
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

        }
    }


    private void configureToolbar() {
        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("FundsofHope");
    }

    private void configureDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navView = (NavigationView) findViewById(R.id.navView);

        setupDrawerContent(navView);


        mDrawerToggle = setupDrawerToggle();

        mDrawer.setDrawerListener(mDrawerToggle);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mainToolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.nav_second_fragment:
                intent = new Intent(this, TransitionFirstActivity.class);

                break;
            case R.id.nav_third_fragment:
                intent = new Intent(this, ProjectsActivity.class);
                break;
            default :
                intent = new Intent(this, MainActivity.class);
                break;
        }

        startActivity(intent);
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
