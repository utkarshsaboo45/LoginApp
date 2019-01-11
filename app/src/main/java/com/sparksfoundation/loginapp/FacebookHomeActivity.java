package com.sparksfoundation.loginapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookHomeActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView;
    private ImageView profilePicImageView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_home);

        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        profilePicImageView = findViewById(R.id.profile_pic_image_view);
        logoutButton = findViewById(R.id.logout_button);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        setFacebookData(accessToken);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookSignout();
                Toast.makeText(getApplicationContext(), R.string.logout_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFacebookData(final AccessToken accessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try
                        {
                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");

                            nameTextView.setText(firstName + "" + lastName);
                            emailTextView.setText(email);

                            if (Profile.getCurrentProfile()!=null)
                            {
                                Picasso.with(getApplicationContext()).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(profilePicImageView);
                            }
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(getApplicationContext(), R.string.data_not_fetched_message, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void facebookSignout()
    {
        LoginManager.getInstance().logOut();
        finish();
    }
}
