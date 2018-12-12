package com.fellopages.mobileapp.classes.common.multimediaselector;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.core.AppConstant;


import java.io.File;
import java.util.ArrayList;

public class MultiMediaSelectorActivity extends FragmentActivity implements MultiMediaSelectorFragment.Callback{

    /** The maximum picture selection times , int type, default 9*/
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** Picture Select mode, the default multiple choice */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";

    public static final String EXTRA_URL = "redirect url";
    /** Whether to display the camera , the default display */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** Choose a result, the return for the ArrayList <String> </> image path set  */
    public static final String EXTRA_RESULT = "select_result";
    /** Choose a result, the return for the Video path  */
    public static final String VIDEO_RESULT = "video_result";
    /** The default selection set */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** Radio*/
    public static final int MODE_SINGLE = 0;
    /** Multiple choice */
    public static final int MODE_MULTI = 1;

    public static final String EXTRA_SELECTION_TYPE = "selection_type";
    public static final String SELECTION_PHOTO = "photo";
    public static final String SELECTION_VIDEO = "video";
    public static final String SELECTION_PHOTO_VIDEO = "photo_video";


    public static final String IS_STORY_POST = "story_post";

    private static final int REQUEST_IMAGE = 300;

    public static final String OPEN_PHOTO_BLOCK = "isOpenPhoto";


    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;
    private String mRedirectUrl;
    private boolean isOpenPhotoBlock;
    private AppConstant mAppConst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_uploading);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 20);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);

        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if(mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        if(intent.hasExtra(EXTRA_URL)){
            mRedirectUrl = intent.getStringExtra(EXTRA_URL);
        }

        if(intent.hasExtra(OPEN_PHOTO_BLOCK)){
            isOpenPhotoBlock = intent.getBooleanExtra(OPEN_PHOTO_BLOCK, false);
        }

        boolean isStoryPost = intent.getBooleanExtra(IS_STORY_POST, false);
        String selectionType = intent.getStringExtra(EXTRA_SELECTION_TYPE);

        if (selectionType == null || selectionType.isEmpty()) {
            selectionType = SELECTION_PHOTO;
        }

        mAppConst = new AppConstant(this);

        // Check If Permission is revoked from App Info Section and activity is already opened
        // We will get exception in this case
        // So need to finish this activity, we can request again for permission.
        if(!mAppConst.checkManifestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            finish();
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiMediaSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiMediaSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiMediaSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putString(MultiMediaSelectorFragment.EXTRA_SELECTION_TYPE, selectionType);
        bundle.putBoolean(MultiMediaSelectorFragment.IS_STORY_POST, isStoryPost);
        bundle.putStringArrayList(MultiMediaSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiMediaSelectorFragment.class.getName(), bundle))
                .commit();

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonPressed();
            }
        });

        // Finish button
        mSubmitButton = (Button) findViewById(R.id.commit);
        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title));
            mSubmitButton.setEnabled(false);
        }else{
            mSubmitButton.setText(String.format("%s(%d/%d)",
                    getResources().getString(R.string.action_bar_button_title), resultList.size(),
                    mDefaultCount));
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultList != null && resultList.size() >0){

                    // Returns the selected image data
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    if(mRedirectUrl!=null) {
                        data.putExtra(EXTRA_URL, mRedirectUrl);
                        setResult(REQUEST_IMAGE, data);
                    }else {
                        setResult(RESULT_OK, data);
                    }
                    finish();
                }
            }
        });

        if (mode == MODE_SINGLE) {
            mSubmitButton.setVisibility(View.GONE);
        } else {
            mSubmitButton.setVisibility(View.VISIBLE);
        }

        // Set media picture screen title as per selection type
        String title;
        switch (selectionType) {
            case SELECTION_VIDEO:
                title = getResources().getString(R.string.video);
                break;
            case SELECTION_PHOTO_VIDEO:
                title = getResources().getString(R.string.gallery);
                break;
            default:
                title = getResources().getString(R.string.action_bar_title);
                break;
        }

        TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText(title);
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        if(mRedirectUrl!=null) {
            data.putExtra(EXTRA_URL, mRedirectUrl);
            setResult(REQUEST_IMAGE, data);
        }else {
            setResult(RESULT_OK, data);
        }
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        // After a picture , change button states
        if(resultList.size() > 0){
            mSubmitButton.setText(String.format("%s(%d/%d)",
                    getResources().getString(R.string.action_bar_button_title), resultList.size(),
                    mDefaultCount));
            if(!mSubmitButton.isEnabled()){
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
            mSubmitButton.setText(String.format("%s(%d/%d)",
                    getResources().getString(R.string.action_bar_button_title), resultList.size(),
                    mDefaultCount));
        }else{
            mSubmitButton.setText(String.format("%s(%d/%d)",
                    getResources().getString(R.string.action_bar_button_title), resultList.size(),
                    mDefaultCount));
        }
        // When the state is to select the picture when
        if(resultList.size() == 0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title));
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile, boolean isImageFile) {

        if(imageFile != null) {

            // Send Broadcast intent to show image
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(GlobalFunctions.getFileUri(this, imageFile));
                this.sendBroadcast(mediaScanIntent);
            } else {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("content://"
                                    + Environment.getExternalStorageDirectory())));
            }

            Intent data = new Intent();

            // Adding file path when its video recording result.
            if (isImageFile) {
                resultList.add(imageFile.getAbsolutePath());
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            } else {
                data.putExtra(VIDEO_RESULT, imageFile.getAbsolutePath());
            }

            if(mRedirectUrl != null) {
                Bundle bundle = new Bundle();
                data.putExtra(EXTRA_URL, mRedirectUrl);
                data.putExtras(bundle);
                setResult(REQUEST_IMAGE, data);
            } else {
                setResult(RESULT_OK, data);
            }
            finish();
        }
    }

    @Override
    public void onVideoSelected(String path) {
        Intent data = new Intent();
        data.putExtra(VIDEO_RESULT, path);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onBackButtonPressed(){
        Intent intent = new Intent();
        if(isOpenPhotoBlock){
            intent.putExtra(OPEN_PHOTO_BLOCK, isOpenPhotoBlock);
        }
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackButtonPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

}
