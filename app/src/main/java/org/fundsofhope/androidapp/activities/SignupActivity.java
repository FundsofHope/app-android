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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    Button _signupButton;
    TextView _gotoLogin;

    EditText _nameText;
    EditText _userName;
    EditText _emailText;
    EditText _passwordText;
    EditText _phoneText;
    String name;
    String email;
    String password;
    String phone;
    SharedPreferences.Editor editor;
    JSONObject jobj=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        _signupButton = (Button) findViewById(R.id.btn_sign);
        TextView _gotoLogin = (TextView) findViewById(R.id.link_login);

        _nameText = (EditText) findViewById(R.id.input_name);
        _userName = (EditText) findViewById(R.id.input_user);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _phoneText = (EditText) findViewById(R.id.input_phone);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        //_signupButton.setEnabled(false);
        new LoginTask().execute("");

        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        phone=_phoneText.getText().toString();
        new LoginTask().execute("");

    }
    ProgressDialog progressDialog;
    private InputStream is = null;
    private String page_output = "";
    private class LoginTask extends AsyncTask<String, Integer, JSONObject> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub


//
            try {
                Log.i(TAG, "entered try()");
                String URL = "http://fundsofhope.herokuapp.com/user/signup";

                HttpClient Client = new DefaultHttpClient();
                Log.i(TAG, "created client");
//
                HttpGet httpget = new HttpGet(URL);
                Log.i(TAG, "hhtp get");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.i(TAG, "in response handler");

                List<NameValuePair> data = new ArrayList<NameValuePair>();
                Log.i(TAG, email);
                Log.i(TAG, password);
                Log.i(TAG, name);
                data.add(new BasicNameValuePair("username", email));
                data.add(new BasicNameValuePair("password", password));
                data.add(new BasicNameValuePair("name", name));
                Log.i(TAG, "creating name value");

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(data));
                Log.i(TAG, "Encodding");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.i(TAG, "Executing");
                HttpEntity httpEntity = httpResponse.getEntity();
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

            } catch (UnsupportedEncodingException ex) {
                Toast.makeText(SignupActivity.this, "Incorrect Password or username", Toast.LENGTH_LONG).show();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            Log.i(TAG, "returning response");

            return jobj;
        }


        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


        protected void onPostExecute(JSONObject result){
            Log.i(TAG, "Entered on post execute");

            // Log.i(TAG,result);
            progressDialog.dismiss();
            String ref= result.toString();
            //Toast.makeText(SignupActivity.this,"length="+result.length()+result, Toast.LENGTH_LONG).show();
            Pattern pattern= Pattern.compile(".*Valid.*");

            Matcher matcher=pattern.matcher(ref);
            try {
                if(result.getInt("code")==2000) {
                    Log.i(TAG, "Entered if");
                    //successlog();
                    Toast.makeText(SignupActivity.this,"Registered Succesfully", Toast.LENGTH_LONG).show();
                    local_data();
                }
                else if(result.getInt("code")==11000) {
                    Toast.makeText(SignupActivity.this, "User Already exist", Toast.LENGTH_LONG).show();

                }
                else
                    Toast.makeText(SignupActivity.this,"Error connecting internet", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void local_data(){
        Log.i(TAG,"entered local data");
        editor.putString("name", name);
        editor.putString("email",email);
        editor.putString("pass", password);
        editor.putInt("flag", 1);
        editor.commit();
        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
        intent.putExtra("email",email);
        startActivity(intent);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String username = _userName.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Enter at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (username.isEmpty() || username.length() < 4) {
            _userName.setError("Enter at least 4 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}