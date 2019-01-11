package com.sparksfoundation.loginapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class LinkedinHomeActivity extends AppCompatActivity {

    private static final String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";
    private TextView nameTextView, emailTextView;
    private ImageView profilePicImageView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_home);

        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        profilePicImageView = findViewById(R.id.profile_pic_image_view);
        logoutButton = findViewById(R.id.logout_button);

        linkedinHelperApi();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linkedinSignOut();
                Toast.makeText(getApplicationContext(), R.string.logout_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void linkedinHelperApi()
    {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(LinkedinHomeActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                try
                {
                    getFinalResult(apiResponse.getResponseDataAsJson());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void onApiError(LIApiError LIApiError) {

            }
        });
    }

    public void getFinalResult(JSONObject jsonObject)
    {
        try
        {
            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String pictureUrl = jsonObject.getString("pictureUrl");
            String emailAddress = jsonObject.getString("emailAddress");

            Picasso.with(getApplicationContext()).load(pictureUrl).into(profilePicImageView);

            nameTextView.setText("Name : " + firstName + " " + lastName);
            emailTextView.setText("Email Address : " + emailAddress);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), R.string.data_not_fetched_message, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void linkedinSignOut()
    {
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        finish();
    }

}
