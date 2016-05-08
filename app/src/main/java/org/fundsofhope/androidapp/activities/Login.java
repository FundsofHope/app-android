package org.fundsofhope.androidapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
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
 * Created by Anip on 2/2/2016.
 */
public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    CallbackManager callbackManager;
    SharedPreferences prefs;
    Person person;
    int[] mResources = {
            R.drawable.images1,
            R.drawable.images2,
            R.drawable.logo
    };
    String[] mTitle = {
            "CONNECT WITH CLIENTS",
            "CONSULT ONLINE",
            "EASY & FAST MESSAGING"
    };
    String[] mContent= {
            " Now get connected to users, who are into fitness and need your help to improve","Increase your reach by going online. No more dropping of leads because you are far way from the client","Reply to user queries in seconds as per your availability. Attach files or photos easily"
    };
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 0;
    int requestCode = 0;
    int loginIntent = 0;
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog dialog;
    String postAction = "";
    ViewPager pager;
    CustomPagerAdapter adapter;
    RequestQueue queue;
    View view1;
    View view2;
    View view3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        view1=(View)findViewById(R.id.view1);
        view2=(View)findViewById(R.id.view2);
        view3=(View)findViewById(R.id.view3);
        pager = (ViewPager)findViewById(R.id.pager);
        adapter = new CustomPagerAdapter(this);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    view1.setBackgroundResource(R.drawable.oval2);
                    view2.setBackgroundResource(R.drawable.oval);
                    view3.setBackgroundResource(R.drawable.oval);

                } else if (position == 1) {
                    view2.setBackgroundResource(R.drawable.oval2);
                    view1.setBackgroundResource(R.drawable.oval);
                    view3.setBackgroundResource(R.drawable.oval);

                } else if (position == 2) {
                    view3.setBackgroundResource(R.drawable.oval2);
                    view1.setBackgroundResource(R.drawable.oval);
                    view2.setBackgroundResource(R.drawable.oval);

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                            Toast.makeText(Login.this, "name" + profile.getName() + profile.getFirstName() + profile.getLastName() + profile.getId(), Toast.LENGTH_LONG).show();


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
                        Toast.makeText(Login.this, "Could not sign in. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(Login.this, "Could not sign in. Please try again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Login.this,firstName+lastName+birthday+email+gender+name+fbId+fbPic,Toast.LENGTH_LONG).show();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "name, id, email, gender, age_range, birthday, bio, first_name, last_name");
        request.setParameters(bundle);
        request.executeAsync();
    }

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
    public void mylogin(View v){
        Intent intent=new Intent(Login.this,LoginActivity.class);
        startActivity(intent);
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
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
          //  Toast.makeText(Login.this,name+pic+birthday+email+firstName+lastName,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(Login.this,"Error login",Toast.LENGTH_LONG).show();
    }
    public void googleLogin(View v) {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        //mStatusTextView.setText(R.string.signing_in);
    }
    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;


        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);
            /*TextView title=(TextView)itemView.findViewById(R.id.title);
            TextView content=(TextView)itemView.findViewById(R.id.content);
            title.setText(mTitle[position]);
            content.setText(mContent[position]);
            */
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }

    }

}
