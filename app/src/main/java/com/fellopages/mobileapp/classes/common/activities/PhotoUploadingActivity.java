/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.common.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.GridViewAdapter;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorFragment;
import com.fellopages.mobileapp.classes.common.utils.AttachmentDialogUtil;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;




public class PhotoUploadingActivity extends FragmentActivity implements MultiMediaSelectorFragment.Callback,
        View.OnClickListener{

    private ArrayList<String> resultList = new ArrayList<>();
    private boolean isPhotoUploadingRequest = false;
    private Button mSubmitButton, mUploadButton;
    private ImageView mBackButton;
    private int mDefaultCount;
    public static final String EXTRA_RESULT = "select_result";
    private List<ImageViewList> mPhotoUrls;
    private GridView mGridView;
    private GridViewAdapter adapter;
    private int columnWidth, width;
    private AppConstant mAppConst;
    private AttachmentDialogUtil mAttachmentDialogUtil;
    public static final int NUM_OF_COLUMNS = 2;

    // GridView image padding
    public static final int GRID_PADDING = 4;

    private PhotoUploadingActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_uploading);

        mAppConst = new AppConstant(this);
        mAttachmentDialogUtil = new AttachmentDialogUtil(PhotoUploadingActivity.this);

        // Check If Permission is revoked from App Info Section and activity is already opened
        // We will get exception in this case
        // So need to finish this activity, we can request again for permission.
        if(!mAppConst.checkManifestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            finish();
        }

        mSubmitButton = findViewById(R.id.commit);
        mUploadButton = findViewById(R.id.upload);

        Bundle bundle = new Bundle();

        Bundle arguments = getIntent().getExtras();
        if(arguments != null){
            isPhotoUploadingRequest = arguments.getBoolean(ConstantVariables.IS_PHOTO_UPLOADED);
            if (arguments.getBoolean("selection_mode", false)) {
                bundle.putInt(MultiMediaSelectorFragment.EXTRA_SELECT_MODE, MultiMediaSelectorFragment.MODE_SINGLE);
            }
        }else{
            bundle.putInt(MultiMediaSelectorFragment.EXTRA_SELECT_MODE, MultiMediaSelectorFragment.MODE_MULTI);
            mSubmitButton.setVisibility(View.VISIBLE);
        }

        bundle.putString(MultiMediaSelectorFragment.EXTRA_SELECTION_TYPE, MultiMediaSelectorFragment.SELECTION_PHOTO);

        mAppConst = new AppConstant(PhotoUploadingActivity.this);
        width = AppConstant.getDisplayMetricsWidth(PhotoUploadingActivity.this);
        mDefaultCount = 10;

        mPhotoUrls = new ArrayList<>();

        mBackButton = findViewById(R.id.btn_back);
        mBackButton.setOnClickListener(this);
        mGridView = findViewById(R.id.gridView);
        mGridView.setVisibility(View.VISIBLE);
        initilizeGridLayout();

        mActivity = this;

        // customer logic here...
        bundle.putInt(MultiMediaSelectorFragment.EXTRA_SELECT_COUNT, 10);
        bundle.putBoolean(MultiMediaSelectorFragment.EXTRA_SHOW_CAMERA, true);
        // Add fragment to your Activity


        Fragment multiSelectorFragment = new MultiMediaSelectorFragment();

        multiSelectorFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.image_grid, multiSelectorFragment).commit();


        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title));
            mSubmitButton.setEnabled(false);
        }else{
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
            mSubmitButton.setEnabled(true);
        }

        mSubmitButton.setOnClickListener(view -> {
            if(resultList != null && resultList.size() >0){
                findViewById(R.id.image_grid).setVisibility(View.GONE);
                for (String p : resultList) {
                    try {
                        Bitmap selectedImage = BitmapUtils.decodeSampledBitmapFromFile(PhotoUploadingActivity.this,
                                p, width, (int) getResources().getDimension(R.dimen.feed_attachment_image_height), false);
                        mPhotoUrls.add(new ImageViewList(selectedImage));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                adapter = new GridViewAdapter(mActivity, columnWidth, mPhotoUrls,false, true);
                mGridView.setAdapter(adapter);

                mSubmitButton.setVisibility(View.GONE);
                mUploadButton.setVisibility(View.VISIBLE);
            }
        });

        mUploadButton.setOnClickListener(this);
    }

    @Override
    public void onSingleImageSelected(String path) {
        // When select mode set to MODE_SINGLE, this method will received result from fragment

        if (isPhotoUploadingRequest) {
            showSelectedImage(path);

        } else {
            Intent data = new Intent();
            resultList.add(path);
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onImageSelected(String path) {

        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        if(resultList.size() > 0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
            if(!mSubmitButton.isEnabled()){
                mSubmitButton.setEnabled(true);
            }
        }
        // You can specify your ActionBar behavior here
    }

    @Override
    public void onImageUnselected(String path) {
        // You can specify your ActionBar behavior here
        if(resultList.contains(path)){
            resultList.remove(path);
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
        }else{
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
        }
        if(resultList.size() == 0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title));
            mSubmitButton.setEnabled(false);
        }

    }

    @Override
    public void onCameraShot(File imageFile, boolean isImageFile) {
        if(imageFile != null) {
            if (isPhotoUploadingRequest) {
                showSelectedImage(imageFile.getAbsolutePath());

            } else {
                Intent data = new Intent();
                resultList.add(imageFile.getAbsolutePath());
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void onVideoSelected(String path) {

    }

    /**
     * Method to show selected image or captured image.
     * @param absolutePath path of image.
     */
    public void showSelectedImage(String absolutePath) {
        resultList.clear();
        resultList.add(absolutePath);
        final AlertDialog alertDialog = mAttachmentDialogUtil.showAlertDialogWithPhoto(resultList);
        Button alertDialogPositiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        alertDialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent();
                photoIntent.putStringArrayListExtra(ConstantVariables.PHOTO_LIST, resultList);
                setResult(ConstantVariables.PAGE_EDIT_CODE, photoIntent);
                finish();
                alertDialog.dismiss();
            }
        });
    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     * */
    public void initilizeGridLayout() {

        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((getScreenWidth() - ((NUM_OF_COLUMNS + 1) * padding)) /
                NUM_OF_COLUMNS);

        // Setting number of grid columns
        mGridView.setNumColumns(NUM_OF_COLUMNS);
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        mGridView.setColumnWidth(columnWidth);

        // Setting horizontal and vertical padding
        mGridView.setHorizontalSpacing((int) padding);
        mGridView.setVerticalSpacing((int) padding);
    }

    /*
     * getting screen width
     */
    @SuppressWarnings("deprecation")
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) {
            // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.upload:
                Intent data = new Intent();
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.btn_back:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(PhotoUploadingActivity.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(PhotoUploadingActivity.this);
                }
                break;
            default:
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
