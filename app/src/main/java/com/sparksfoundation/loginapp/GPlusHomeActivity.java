package com.sparksfoundation.loginapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

public class GPlusHomeActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView;
    private ImageView profilePicImageView;
    private Button logoutButton;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gplus_home);

        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        profilePicImageView = findViewById(R.id.profile_pic_image_view);
        logoutButton = findViewById(R.id.logout_button);

        final GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignOut();
                Toast.makeText(getApplicationContext(), R.string.logout_message, Toast.LENGTH_SHORT).show();
            }
        });


        String name = googleSignInAccount.getDisplayName();
        String email = googleSignInAccount.getEmail();
        nameTextView.setText(name);
        emailTextView.setText(email);
        if(googleSignInAccount.getPhotoUrl() != null) {
            String profilePicUrl = googleSignInAccount.getPhotoUrl().toString();
            Picasso.with(getApplicationContext()).load(profilePicUrl).into(profilePicImageView);
        }
        Toast.makeText(getApplicationContext(), R.string.login_successful_message, Toast.LENGTH_SHORT).show();
    }

    private void googleSignOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                finish();
            }
        });
    }
}
