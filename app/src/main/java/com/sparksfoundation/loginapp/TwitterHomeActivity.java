package com.sparksfoundation.loginapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;

public class TwitterHomeActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, usernameTextView;
    private ImageView profilePicImageView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_home);

        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        usernameTextView = findViewById(R.id.username_text_view);
        profilePicImageView = findViewById(R.id.profile_pic_image_view);
        logoutButton = findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterLogout();
                Toast.makeText(getApplicationContext(), R.string.logout_message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");

        nameTextView.setText(name);
        emailTextView.setText(email);
        usernameTextView.setText(username);
        String profilePicUrl = "https://twitter.com/" + username + "/profile_image?size=original";

        Picasso.with(getApplicationContext()).load(profilePicUrl).into(profilePicImageView);
    }

    public void twitterLogout() {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }
}
