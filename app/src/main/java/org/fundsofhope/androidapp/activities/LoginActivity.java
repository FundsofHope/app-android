package org.fundsofhope.androidapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener  {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _userName;
    EditText _passWord;
    int user_flag;

    Button _loginButton;
    Button _signupButton;
    ProgressDialog progressDialog;
    String email;
    String password;
    JSONObject jobj=null;
    RadioButton user;
    RadioButton ngo;
    SharedPreferences.Editor editor;
    String URL;
    CallbackManager callbackManager;
    SharedPreferences prefs;
    Person person;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 0;
    int requestCode = 0;
    int loginIntent = 0;
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog dialog;
    String postAction = "";
    ViewPager pager;
    RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


            setContentView(R.layout.activity_login);
           // FacebookSdk.sdkInitialize(getApplicationContext());
           // callbackManager = CallbackManager.Factory.create();
            //LoginButton loginButton = (LoginButton) view.findViewById(R.id.usersettings_fragment_login_button);
            //loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //});
            FacebookSdk.sdkInitialize(getApplicationContext());
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(new Scope(Scopes.PLUS_LOGIN))
                    .addScope(new Scope("email"))
                    .build();
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            Log.i("hell", "entering profile");
                            final AccessToken accessToken = AccessToken.getCurrentAccessToken();

                            final Profile profile = Profile.getCurrentProfile();
                            if (profile != null) {
                                makeGraphRequest(profile, accessToken);
                                Log.i("hell", "entering profile");
                                Toast.makeText(LoginActivity.this, "name" + profile.getName()+profile.getFirstName()+profile.getLastName()+profile.getId(), Toast.LENGTH_LONG).show();


                            } else {
                                Log.i("hell", "entering else");
                                ProfileTracker profileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                        if (currentProfile != null) {
                                            makeGraphRequest(currentProfile, accessToken);
                                        }
                                        stopTracking();
                                    }
                                };
                                profileTracker.startTracking();
                            }
                            Intent intent = getIntent();
                            requestCode = intent.getIntExtra("requestCode", 0);
                            loginIntent = intent.getIntExtra("intent", 0);
                            postAction = intent.getStringExtra("postAction");
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            Toast.makeText(LoginActivity.this, "Could not sign in. Please try again.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            Toast.makeText(LoginActivity.this, "Could not sign in. Please try again.", Toast.LENGTH_SHORT).show();
                            exception.printStackTrace();
                        }
                    });
            editor = pref.edit();
            editor.putInt("flag", 0);
            _userName = (EditText) findViewById(R.id.input_user);
            _passWord = (EditText) findViewById(R.id.input_password);

            _loginButton = (Button) findViewById(R.id.btn_login);
            _signupButton = (Button) findViewById(R.id.btn_signup);
            user=(RadioButton)findViewById(R.id.user);
            ngo=(RadioButton)findViewById(R.id.ngo);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ngo.setEnabled(false);
                    user_flag = 1;
                }
            });
            ngo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setEnabled(false);
                    user_flag = 2;
                }
            });

            _loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = _userName.getText().toString();
                    password = _passWord.getText().toString();
                    SharedPreferences mpref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = mpref.edit();
                    editor.putInt("user",user_flag);
                    editor.commit();
                    new LoginTask().execute("");
                }
            });

            _signupButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            });
        }
       /* else {
            setContentView(R.layout.activity_back);

            Button back = (Button) findViewById(R.id.back);
            TextView txt=(TextView)findViewById(R.id.link_login);
            SharedPreferences mpref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String ema=mpref.getString("email","");
            txt.setText("Hi "+ema);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //sdemail = pref.getString("email", "");
                    //password = pref.getString("pass", "");
                    //token=pref.getString("token","");
                    //Log.i(TAG,"email"+email+""+password);

                    Intent inte=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(inte);
                    finish();
                    //new LoginTask().execute("");
                }
            });
        }
        */




    private InputStream is = null;
    private String page_output = "";

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Login", "onConnected:" + bundle);


        mShouldResolve = false;

        // Show the signed-in UI
        //showSignedInUI();
        Log.i("hell", "reached google login");
        googleLogin();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Login", "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {

                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    private class LoginTask extends AsyncTask<String, Integer, JSONObject> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this,
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
            try{
                Log.i(TAG,"entered try()");

                //Toast.makeText(getApplicationContext(), "Please wait,connecting to server",Toast.LENGTH_LONG).show();
                Log.i(TAG,"entered toast()");
                Log.i(TAG,email);
                Log.i(TAG,password);
                if(user_flag==1)
                URL="http://fundsofhope.herokuapp.com/user/login";
                else if(user_flag==2)
                    URL="http://fundsofhope.herokuapp.com/ngo/login";
                HttpClient Client=new DefaultHttpClient();
                Log.i(TAG,"created client");
//			try{
//				String Response="";
                HttpGet httpget=new HttpGet(URL);
                Log.i(TAG, "hhtp get");
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                Log.i(TAG, "in response handler");

                List<NameValuePair> data = new ArrayList<NameValuePair>();
                if(user_flag==1)data.add(new BasicNameValuePair("username", email));
                else if(user_flag==2)data.add(new BasicNameValuePair("ngoid", email));
                data.add(new BasicNameValuePair("password", password));

                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(data));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.i(TAG,"executed");
                is = httpEntity.getContent();
                Log.i(TAG, "in strict mode");

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    page_output = sb.toString();
                    jobj=new JSONObject(page_output);
                    Log.i("LOG", "page_output --> " + page_output);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                Log.i(TAG,"request executed");

            } catch(UnsupportedEncodingException e){
            } catch (IOException e) {
            }
            Log.i(TAG, "returning response");
            return jobj;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(JSONObject result){
            Log.i(TAG, "Entered on post execute");
            progressDialog.dismiss();


            //Toast.makeText(LoginActivity.this,"length="+result.length()+result, Toast.LENGTH_LONG).show();


            try {
                if(result.getBoolean("login")) {
                    SharedPreferences.Editor editor;

                    SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = pref.edit();

                    String token=result.getString("token");

                    String message=result.getString("message");
                    editor.putInt("flag", 1);
                    editor.putInt("user",user_flag);


                    editor.putString("token", token);
                    editor.putString("email",email);
                    editor.putString("pass",password);
                    editor.commit();

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    System.out.println("token is"+token);
                    Log.i(TAG, "Entered if");
                    //onLoginSuccess();
                    successlog();
                }
                else if (!result.getBoolean("login")) {
                    onLoginFailed();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Can't Connect", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        protected void successlog(){


            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            } else {

//                dialog.setTitle("Signing In");
                //              dialog.setCancelable(false);
                //            dialog.show();
            }

            mIsResolving = false;
            mGoogleApiClient.connect();

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String password = _passWord.getText().toString();

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passWord.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passWord.setError(null);
        }

        return valid;
    }
    public void facebookLogin(View v) {
        Log.i("hell","reached on facebook login");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        Log.i("hell", "exiting facebook login");
    }
    private void makeGraphRequest(Profile profile, final AccessToken accessToken) {

        final String name = profile.getName();
        final String fbId = profile.getId();
        final Uri fbPic = profile.getProfilePictureUri(150, 150);

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                final String email = user.optString("email");
                final String gender = user.optString("gender");
                String ageRangeMin = "";
                String ageRangeMax = "";
                try {
                    final JSONObject ageRange = user.getJSONObject("age_range");
                    ageRangeMin = ageRange.optString("min");
                    ageRangeMax = ageRange.optString("max");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String firstName = user.optString("first_name");
                final String lastName = user.optString("last_name");
                final String birthday = user.optString("birthday");
                Toast.makeText(LoginActivity.this,firstName+lastName+birthday+email+gender+name+fbId+fbPic,Toast.LENGTH_LONG).show();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "name, id, email, gender, age_range, birthday, bio, first_name, last_name");
        request.setParameters(bundle);
        request.executeAsync();
    }
    public void googleLogin() {
        Log.i("hell", "enterd google login");
        person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        if (person != null) {
            Log.i("hell", "fetching details");
            final String googleId = person.getId();
            final String name = person.getDisplayName();
            final String pic = person.getImage().getUrl();
            final String birthday = person.getBirthday() != null ? person.getBirthday() : "";
            Person.AgeRange ageRange = person.getAgeRange();
            final int ageRangeMin = ageRange != null ? ageRange.getMin() : 0;
            final int ageRangeMax = ageRange != null ? ageRange.getMax() : 0;
            final String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            final String gender = (person.getGender() == Person.Gender.MALE) ? "male" : "female";
            final String firstName = person.getName().getGivenName();
            final String lastName = person.getName().getFamilyName();
            prefs = getSharedPreferences("application_settings", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("googleId", googleId);
            editor.putString("name", name);
            editor.putString("pic", pic + "&sz=200");
            editor.putString("email", email);
            editor.putString("birthday", birthday);
            editor.putString("ageRangeMin", String.valueOf(ageRangeMin));
            editor.putString("ageRangeMax", String.valueOf(ageRangeMax));
            editor.putString("gender", gender);
            editor.commit();
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            //Toast.makeText(LoginActivity.this,name+pic+birthday+email+firstName+lastName,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(LoginActivity.this,"Error login",Toast.LENGTH_LONG).show();
    }
    public void googleLogin(View v) {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        //mStatusTextView.setText(R.string.signing_in);
    }
    private void showErrorDialog(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();

        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Show the default Google Play services error dialog which may still start an intent
            // on our behalf if the user can resolve the issue.
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mShouldResolve = false;
                            //updateUI(false);
                        }
                    }).show();
        } else {
            // No default Google Play Services error, display a message to the user.
            //String errorString = getString(R.string.play_services_error_fmt, errorCode);
            //Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

            mShouldResolve = false;
            //updateUI(false);
        }
    }
}
