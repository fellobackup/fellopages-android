package com.socialengineaddons.messenger.user;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.ui.BaseButton;
import com.socialengineaddons.messenger.ui.CircularImageView;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class userProfile extends AppCompatActivity implements View.OnClickListener {

    private String userId;
    private int profileColor;
    private DatabaseReference userDb;
    private String userName, userImage, userProfileUrl;
    private ProgressBar progressBar;
    private CircularImageView userImageView;
    private TextView userNameView;
    private Context mContext;
    private BaseButton viewProfileBtn;
    private MessengerDatabaseUtils messengerDatabaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_layout);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mContext = this;
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userImageView = (CircularImageView) findViewById(R.id.user_image_view);
        userNameView = (TextView) findViewById(R.id.user_name_view);
        viewProfileBtn = (BaseButton) findViewById(R.id.view_profile_btn) ;
        viewProfileBtn.setOnClickListener(this);

        userId = getIntent().getStringExtra("user_id");
        profileColor = getIntent().getIntExtra(Constants.PROFILE_COLOR, 0);
        if (profileColor == 0) {
            Random random = new Random();
            this.profileColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }

        userDb = messengerDatabaseUtils.getUsersDatabase().child(userId);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = dataSnapshot.child("name").getValue(String.class);
                userImage = dataSnapshot.child("profileImageUrl").getValue(String.class);
                userProfileUrl = dataSnapshot.child("profileUrl").getValue(String.class);

                progressBar.setVisibility(View.GONE);

                userImageView.setVisibility(View.VISIBLE);
                if (userImage != null && !userImage.isEmpty()) {
                    userImageView.hideText();
                    Picasso.with(mContext)
                            .load(userImage)
                            .placeholder(R.drawable.person_image_empty)
                            .into(userImageView);
                } else {
                    userImageView.showText(userName, profileColor);
                }

                userNameView.setVisibility(View.VISIBLE);
                // todo in next release
//                viewProfileBtn.setVisibility(View.VISIBLE);
                userNameView.setText(userName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        userId = "1";

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.view_profile_btn){

            // Open SocialEngine Mobile application's user profile page.
        }
    }
}
