package com.bigsteptech.realtimechat.conversation.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.conversation.data_model.Message;
import com.bigsteptech.realtimechat.multiimageselector.bean.Image;
import com.bigsteptech.realtimechat.utils.BitmapUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhotoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView videoPlayButton, navigationBack;
    private TouchImageView image;
    TextView mCancelButton, mChooseButton;
    private ArrayList<String> mSelectPath = new ArrayList<>();
    private String imageUrl, videoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        image = (TouchImageView) findViewById(R.id.image);
        videoPlayButton = (ImageView) findViewById(R.id.play_button);
        mCancelButton = (TextView) findViewById(R.id.cancel_action);
        mChooseButton = (TextView) findViewById(R.id.choose_button);
        navigationBack = (ImageView) findViewById(R.id.navigation_back);

        mCancelButton.setOnClickListener(this);
        mChooseButton.setOnClickListener(this);
        navigationBack.setOnClickListener(this);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent() != null){
            if(getIntent().hasExtra("image")){
                mSelectPath = getIntent().getStringArrayListExtra("image");
            } else {
                imageUrl = getIntent().hasExtra("imageUrl") ? getIntent().getStringExtra("imageUrl") : null;
                videoUrl = getIntent().hasExtra("videoUrl") ? getIntent().getStringExtra("videoUrl") : null;
            }
        }

        if(mSelectPath != null && mSelectPath.size() != 0){
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(this, mSelectPath.get(0));
            image.setImageBitmap(bitmap);
        } else if (imageUrl != null){
            Picasso.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_error)
                    .into(image);
            findViewById(R.id.buttonsLayout).setVisibility(View.GONE);
        }

        if(videoUrl != null && !videoUrl.isEmpty()){
            videoPlayButton.setVisibility(View.VISIBLE);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            intent.setDataAndType(Uri.parse(videoUrl), "video/mp4");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {


        Log.d(PhotoPreviewActivity.class.getSimpleName(), " onClick called..");

        if(view.getId() == R.id.cancel_action){
            finish();
        } else if (view.getId() == R.id.choose_button) {
            // Upload Image to Firebase Storage
            Intent intent = new Intent();
            intent.putExtra("imageUri", mSelectPath.get(0));
            setResult(Constants.UPLOAD_IMAGE, intent);
            finish();
        } else if(view.getId() == R.id.navigation_back){
            Log.d(PhotoPreviewActivity.class.getSimpleName(), " inside navigation_back ");
            finish();
        }
    }
}
