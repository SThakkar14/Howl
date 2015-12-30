package boomer.com.howl.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
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

import boomer.com.howl.HowlApiClient;
import boomer.com.howl.HTTPCodes;
import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.UserProfile;
import boomer.com.howl.R;
import boomer.howl.Constants;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SplashScreen extends AppCompatActivity {
    CallbackManager callbackManager;

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
                    Log.e("facebook_error" , "cancelled");
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
            contactServer();
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
