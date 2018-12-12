package com.uniprogy.outquiz.activities.signin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.MainActivity;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.helpers.API;
import com.uniprogy.outquiz.helpers.APIListener;
import com.uniprogy.outquiz.helpers.Misc;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;
import com.uniprogy.outquiz.helpers.RoundedCornersStrokeListener;
import com.uniprogy.outquiz.models.Player;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ProfileActivity extends BaseActivity {

    private static final int RESULT_CAMERA = 1;
    private static final int RESULT_GALLERY = 2;
    private static final int PERMISSION_CAMERA = 1;
    private static final int PERMISSION_GALLERY = 2;

    EditText usernameField;
    Button nextButton;
    ImageView avatarImageView;
    ProgressBar spinner;

    //region View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        configureView();
    }

    public void configureView()
    {
        usernameField = findViewById(R.id.usernameField);
        usernameField.addOnLayoutChangeListener(new RoundedCornersStrokeListener(getResources().getColor(R.color.system_button_background)));
        usernameField.addTextChangedListener(textWatcher);
        nextButton = findViewById(R.id.nextButton);
        nextButton.addOnLayoutChangeListener(new RoundedCornersListener());
        nextButton.setEnabled(false);
        nextButton.setAlpha(0.5f);
        avatarImageView = findViewById(R.id.avatarImageView);
        spinner = findViewById(R.id.progressBar);

        // load avatar
        Misc.getCurrentPlayer().setAvatar(avatarImageView);

        // setup taps
        nextButtonTap();
        avatarTap();
    }

    //endregion

    //region Profile (username)

    private void nextButtonTap()
    {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                API.profile(usernameField.getText().toString(), profileListener);
            }
        });
    }

    private APIListener profileListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            spinner.setVisibility(View.INVISIBLE);
            Player p = Misc.getCurrentPlayer();
            p.username = Misc.jsonNull(response, "username");
            p.token = Misc.jsonNull(response, "token");
            Misc.setCurrentPlayer(p);
            Misc.toMain();
        }

        @Override
        public void failure(int code, String[] errors) {
            spinner.setVisibility(View.INVISIBLE);
            showErrors(errors);
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            nextButton.setEnabled(s.length() >= 3);
            nextButton.setAlpha(s.length() >= 3 ? 1.0f : 0.5f);
        }
    };

    //endregion

    //region Avatar

    private void avatarTap()
    {
        final PopupMenu popup = new PopupMenu(this, avatarImageView);
        popup.getMenu().add(0,1,1,R.string.tr_new_photo);
        popup.getMenu().add(0,2,2,R.string.tr_camera_roll);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId())
                {
                    // new photo
                    case 1:
                        imageFromCamera();
                        return true;
                    // photo from gallery
                    case 2:
                        imageFromGallery();
                        return true;
                    default:
                        return false;
                }
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
    }

    private void imageFromCamera()
    {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA);
        }
        else {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, RESULT_CAMERA);
            }

        }
    }

    private void imageFromGallery()
    {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_GALLERY);
        }
        else {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, RESULT_GALLERY);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromCamera();
                }
                return;
            case PERMISSION_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromGallery();
                }
                return;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;

        if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            // get image bitmap
            imageBitmap = (Bitmap) extras.get("data");
        }
        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                // get image bitmap
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(imageBitmap != null)
        {
            // show spinner
            findViewById(R.id.avatarProgressBar).setVisibility(View.VISIBLE);
            // generate file and upload
            File imgFile = getImage(imageBitmap);
            if(imgFile != null) {
                API.avatar(imgFile, avatarListener);
            }
        }
    }

    private File getImage(Bitmap image) {
        try {
            File imgFile = new File(getCacheDir(), "image_");
            imgFile.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(imgFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return imgFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private APIListener avatarListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            // hide spinner
            findViewById(R.id.avatarProgressBar).setVisibility(View.GONE);
            // update player info
            Player p = Misc.getCurrentPlayer();
            p.token = Misc.jsonNull(response, "token");
            p.avatar = Misc.jsonNull(response, "avatar");
            Misc.setCurrentPlayer(p);
            // update image view
            p.setAvatar(avatarImageView, true);
        }

        @Override
        public void failure(int code, String[] errors) {
            // hide spinner
            findViewById(R.id.avatarProgressBar).setVisibility(View.GONE);
            // show errors
            showErrors(errors);
        }
    };

    //endregion
}
