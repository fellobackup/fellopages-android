package com.bigsteptech.realtimechat.user;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.ui.BaseButton;
import com.bigsteptech.realtimechat.ui.CircularImageView;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class userProfile extends AppCompatActivity implements View.OnClickListener {

    private String userId;
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

        userDb = messengerDatabaseUtils.getUsersDatabase().child(userId);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = dataSnapshot.child("name").getValue(String.class);
                userImage = dataSnapshot.child("profileImageUrl").getValue(String.class);
                userProfileUrl = dataSnapshot.child("profileUrl").getValue(String.class);

                progressBar.setVisibility(View.GONE);

                userImageView.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(userImage)
                        .placeholder(R.drawable.person_image_empty)
                        .into(userImageView);

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
