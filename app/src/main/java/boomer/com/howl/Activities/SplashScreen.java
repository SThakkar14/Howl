package boomer.com.howl.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import boomer.com.howl.Constants;
import boomer.com.howl.HTTPCodes;
import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.UserProfile;
import boomer.com.howl.R;
import boomer.com.howl.Services.QuickstartPreferences;
import boomer.com.howl.Services.RegistrationIntentService;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SplashScreen extends AppCompatActivity {
    CallbackManager callbackManager;
    private BroadcastReceiver deviceRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SplashScreen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_splash_screen);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_about_me", "email", "user_friends");

        if (AccessToken.getCurrentAccessToken() == null) {
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    contactServer();
                }

                @Override
                public void onCancel() {
                    Log.e("facebook_error", "cancelled");
                }

                @Override
                public void onError(FacebookException error) {
                }
            });

            LoginManager.getInstance().logOut();

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);

            AnimationSet animation = new AnimationSet(true);
            animation.addAnimation(fadeIn);
            loginButton.startAnimation(animation);
            loginButton.setVisibility(View.VISIBLE);
        } else {
            //TODO: invoke the regintentService here
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
            deviceRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    String device_token = intent.getStringExtra("device_token");
                    Log.i(TAG , device_token);
//                    if (sentToken) {
//                        mInformationTextView.setText(getString(R.string.gcm_send_message));
//                    } else {
//                        mInformationTextView.setText(getString(R.string.token_error_message));
//                    }
                }
            };



            contactServer();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void contactServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HowlApiClient api = retrofit.create(HowlApiClient.class);
        String accessToken = AccessToken.getCurrentAccessToken().getToken();
        api.login(accessToken).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Response<UserProfile> response, Retrofit retrofit) {
                if (response.code() == HTTPCodes.OK) {
                    loadLandingPage(response.body());
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = response.errorBody().toString();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void loadLandingPage(UserProfile userProfile) {
        Intent intent = new Intent(SplashScreen.this, LandingPage.class);
        intent.putExtra("user_profile", userProfile);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(deviceRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceRegistrationBroadcastReceiver);
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
