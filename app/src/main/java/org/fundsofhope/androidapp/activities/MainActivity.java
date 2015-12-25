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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.FacebookSdk;

import com.pushbots.push.Pushbots;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fundsofhope.androidapp.R;
import org.fundsofhope.androidapp.slidingtabs.fragments.SlidingTabsBasicFragment;
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
        if(pref.getInt("user",-1)==2)
            add.setClickable(true);
        else
        add.setEnabled(false);
        //Bitmap bi;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte=new Intent(MainActivity.this,AddProject.class);
                startActivity(inte);
            }
        });



            Pushbots.sharedInstance().init(this);
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
    private InputStream isi = null;
    private String page_outputo = "";

    private class LoginTask extends AsyncTask<String, Integer, JSONObject> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
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
                String URL = "http://fundsofhope.herokuapp.com/user/";
                HttpClient Client = new DefaultHttpClient();
                Log.i(TAG, "created client");
//			try{
//				String Response="";
                HttpGet httpget = new HttpGet(URL);
                Log.i(TAG, "hhtp get" + token);
                httpget.addHeader("x-access-token",token);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.i(TAG, "in response handler");

                List<NameValuePair> data = new ArrayList<NameValuePair>();

                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpResponse httpResponse = Client.execute(httpget);
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
                if (result.getString("message").contains("Home")) {
                    //onLoginSuccess();
                    new LooginTask().execute("");
                    // successlog();
                } else if (!result.getBoolean("success")) {
                    // onLoginFailed();
                    SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = pref.edit();
                    editor.putInt("flag",0);
                    editor.commit();

                    Intent inte = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(inte);
                } else {
                    Toast.makeText(MainActivity.this, "Can't Connect", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }}
    private InputStream is = null;
    private String page_output = "";

    private class LooginTask extends AsyncTask<String, Integer, JSONObject> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
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
                String URL = "http://fundsofhope.org/project/";
                HttpClient Client = new DefaultHttpClient();
                Log.i(TAG, "created client");
//			try{
//				String Response="";
                HttpGet httpget = new HttpGet(URL);
                Log.i(TAG, "hhtp get" + token);
                httpget.addHeader("x-access-token", "");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.i(TAG, "in response handler");

                List<NameValuePair> data = new ArrayList<NameValuePair>();

                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpResponse httpResponse = Client.execute(httpget);
                Log.i(TAG, "entered execute");
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
                    SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = mypref.edit();
                    editor.putString("projects", page_output);
                    editor.commit();
                    //onSuccess();
                    //jobj = new JSONObject(page_output);
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




            //          System.out.println(ttitle[0]);
//           System.out.println(ddesc[0]);


        }
    }


    private void configureToolbar() {
        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("FundsofHope");

//        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
//                    mDrawerLayout.closeDrawer(Gravity.START);
//
//                } else {
//                    mDrawerLayout.openDrawer(Gravity.START);
//                }
//            }
//        });
    }

    private void configureDrawer() {
        // Configure drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navView = (NavigationView) findViewById(R.id.navView);

        setupDrawerContent(navView);


//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                R.string.drawer_open,
//                R.string.drawer_closed) {
//
//            public void onDrawerClosed(View view) {
//                supportInvalidateOptionsMenu();
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
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
//  In case of Fragments
//        Fragment fragment = null;
//
//        Class fragmentClass;
//        switch(menuItem.getItemId()) {
//            case R.id.nav_first_fragment:
//                fragmentClass = MainActivity.class;
//                break;
//            case R.id.nav_second_fragment:
//                fragmentClass = TransitionFirstActivity.class;
//                break;
//            case R.id.nav_third_fragment:
//                fragmentClass = TransitionSecondActivity.class;
//                break;
//            default:
//                fragmentClass = MainActivity.class;
//        }
//
//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.sample_content_fragment, fragment).commit();

        Intent intent = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.nav_second_fragment:
                intent = new Intent(this, TransitionFirstActivity.class);

                break;
            case R.id.nav_third_fragment:
                intent = new Intent(this, Recycler.class);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView parent, View view, int position, long id) {
//            selectItem(position);
//            drawerLayout.closeDrawer(drawerListView);
//
//        }
//    }
}
