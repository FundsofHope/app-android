package org.fundsofhope.androidapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.fundsofhope.androidapp.R;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Anip on 1/19/2016.
 */
public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.facebook);

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
                            Toast.makeText(GoogleLoginActivity.this, "name" + profile.getName()+profile.getFirstName()+profile.getLastName()+profile.getId(), Toast.LENGTH_LONG).show();


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
                        Toast.makeText(GoogleLoginActivity.this, "Could not sign in. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(GoogleLoginActivity.this, "Could not sign in. Please try again.", Toast.LENGTH_SHORT).show();
                        exception.printStackTrace();
                    }
                });

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
                Toast.makeText(GoogleLoginActivity.this,firstName+lastName+birthday+email+gender+name+fbId+fbPic,Toast.LENGTH_LONG).show();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "name, id, email, gender, age_range, birthday, bio, first_name, last_name");
        request.setParameters(bundle);
        request.executeAsync();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("SplashActivity", "onConnected:" + bundle);


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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("SplashActivity", "onConnectionFailed:" + connectionResult);

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

            Toast.makeText(GoogleLoginActivity.this,name+pic+birthday+email+firstName+lastName,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(GoogleLoginActivity.this,"Error login",Toast.LENGTH_LONG).show();
    }
    public void googleLogin(View v) {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        //mStatusTextView.setText(R.string.signing_in);
    }
}