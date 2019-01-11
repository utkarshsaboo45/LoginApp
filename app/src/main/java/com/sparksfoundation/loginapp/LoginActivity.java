package com.sparksfoundation.loginapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button loginLinkedinButton;
    private LoginButton loginFacebookButton;
    private SignInButton loginGplusButton;
    private TwitterLoginButton loginTwitterButton;

    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        Twitter.initialize(this);
        setContentView(R.layout.activity_login);

        loginLinkedinButton = findViewById(R.id.login_linkedin_button);
        loginFacebookButton = findViewById(R.id.login_facebook_button);
        loginTwitterButton = findViewById(R.id.login_twitter_button);
        loginGplusButton = findViewById(R.id.login_gplus_button);

        //Twitter button Sign in
        loginTwitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                loginTwitter(session);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getApplicationContext(), R.string.authentication_failed_message, Toast.LENGTH_SHORT).show();
            }
        });

        //Linkedin Sign in
        loginLinkedinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LISessionManager.getInstance(getApplicationContext()).init(LoginActivity.this, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        Intent intent = new Intent(LoginActivity.this, LinkedinHomeActivity.class);
                        Toast.makeText(getApplicationContext(), R.string.login_successful_message, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Toast.makeText(getApplicationContext(), R.string.authentication_failed_message, Toast.LENGTH_SHORT).show();
                    }
                }, true);
            }
        });

        //Facebook Sign in
        callbackManager = CallbackManager.Factory.create();
        loginFacebookButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, FacebookHomeActivity.class);
                Toast.makeText(getApplicationContext(), R.string.login_successful_message, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.login_cancelled_message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.authentication_failed_message, Toast.LENGTH_SHORT).show();
            }
        });

        //Google Plus Sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        loginGplusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE)
        {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }
        loginTwitterButton.onActivityResult(requestCode, resultCode, data);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    private void googleSignIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void handleResult(GoogleSignInResult googleSignInResult)
    {
        if(googleSignInResult.isSuccess())
        {
            Intent intent = new Intent(LoginActivity.this, GPlusHomeActivity.class);
            startActivity(intent);
        }
    }

    public void loginTwitter(TwitterSession session)
    {
        TwitterCore.getInstance().getApiClient(session).getAccountService().verifyCredentials(false,true,false).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                try
                {
                    String profilePicUrl = userResult.data.profileImageUrl;
                    String email = userResult.data.email;
                    String name = userResult.data.name;
                    String username = userResult.data.screenName;

                    Intent intent = new Intent(LoginActivity.this, TwitterHomeActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    intent.putExtra("name", name);
                    intent.putExtra("profilePicUrl", profilePicUrl);
                    Toast.makeText(getApplicationContext(), R.string.login_successful_message, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), R.string.data_not_fetched_message, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(getApplicationContext(), R.string.authentication_failed_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}