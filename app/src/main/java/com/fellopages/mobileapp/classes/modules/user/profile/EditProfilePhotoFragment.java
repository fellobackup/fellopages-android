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

package com.fellopages.mobileapp.classes.modules.user.profile;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.activities.PhotoUploadingActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfilePhotoFragment extends Fragment implements View.OnClickListener,
        OnUploadResponseListener {

    private View mRootView;
    private AppConstant mAppConst;
    private Context mContext;
    private String mEditPhotoUrl;
    private boolean isVisibleToUser = false;
    private ProgressBar mProgressBar;
    private ImageView mUserProfileImage;
    private Button mUploadPhotoButton, mSavePhotoButton, mCancelButton;
    private ArrayList<String> mSelectPath;
    private LinearLayout mLinearLayout;
    private String mProfileImageUrl;
    private int width;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;

    public EditProfilePhotoFragment() {
        // Required empty public constructor
    }

    public static EditProfilePhotoFragment newInstance(Bundle bundle){
        EditProfilePhotoFragment fragment = new EditProfilePhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_edit_profile_photo, container, false);
        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(mContext);

        mEditPhotoUrl = AppConstant.DEFAULT_URL + "members/edit/photo";

        mProgressBar = mRootView.findViewById(R.id.progressBar);
        mLinearLayout = mRootView.findViewById(R.id.profileImageBlock);
        mUserProfileImage = mRootView.findViewById(R.id.userProfileImage);
        mUploadPhotoButton = mRootView.findViewById(R.id.uploadImageButton);
        mSavePhotoButton = mRootView.findViewById(R.id.savePhotoButton);
        mCancelButton = mRootView.findViewById(R.id.cancelButton);

        mUploadPhotoButton.setOnClickListener(this);
        mSavePhotoButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        width = AppConstant.getDisplayMetricsWidth(mContext);

        // When photo tab is called directly.
        if (getArguments() != null && getArguments().getBoolean("is_photo_tab")) {
            makeRequest();
        }

        return mRootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            makeRequest();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Method to send request to server to get edit photo page data.
     */
    public void makeRequest(){

        if (mEditPhotoUrl != null) {

            // Hiding the linear layout to show the progress bar when the user submit request.
            mLinearLayout.setVisibility(View.GONE);
            mSavePhotoButton.setVisibility(View.GONE);
            mCancelButton.setVisibility(View.GONE);
            mUploadPhotoButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            mAppConst.getJsonResponseFromUrl(mEditPhotoUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    isVisibleToUser = true;
                    mProgressBar.setVisibility(View.GONE);
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mProfileImageUrl = jsonObject.optString("image");

                    mImageLoader.setFeedImage(mProfileImageUrl, mUserProfileImage);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbar(mRootView, message);
                }
            });
        }
    }


    @Override
    public void onClick(View view) {

        int id  = view.getId();

        switch (id){

            case R.id.uploadImageButton:

                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    startImageUploading();
                }

                break;

            case R.id.savePhotoButton:
                new UploadFileToServerUtils(mContext, mEditPhotoUrl, mSelectPath, this).execute();
                break;

            case R.id.cancelButton:

                mSavePhotoButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.GONE);

                mUploadPhotoButton.setVisibility(View.VISIBLE);
                mImageLoader.setImageUrl(mProfileImageUrl, mUserProfileImage);
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        // if the result is capturing Image
        switch (requestCode){
            case ConstantVariables.REQUEST_IMAGE:
                if(resultCode == AppCompatActivity.RESULT_OK){
                    mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);

                    // Getting Bitmap from its real path.
                    Bitmap selectedImageBitmap = BitmapUtils.decodeSampledBitmapFromFile(mContext, mSelectPath.get(0), width,
                            (int) getResources().getDimension(R.dimen.sing_up_image_width_height), false);

                    mUserProfileImage.setImageBitmap(selectedImageBitmap);
                    mSavePhotoButton.setVisibility(View.VISIBLE);
                    mCancelButton.setVisibility(View.VISIBLE);
                    mUploadPhotoButton.setVisibility(View.GONE);
                }else if (resultCode != AppCompatActivity.RESULT_CANCELED) {
                    // failed to capture image
                    Toast.makeText(mContext,
                            getResources().getString(R.string.image_capturing_failed), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {

        String message;
        if (isRequestSuccessful) {
            EditProfileActivity.isProfileUpdated = true;
            message = mContext.getResources().getString(R.string.profile_photo_updated);
            // Sending the user photo page request again,
            // when user changed the profile photo.
            makeRequest();

        } else {
            message = jsonObject.optString("message");
        }
        SnackbarUtils.displaySnackbar(mRootView, message);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem saveMenuItem = menu.findItem(R.id.submit);
        saveMenuItem.setVisible(false);

        MenuItem deleteMenuItem = menu.findItem(R.id.delete);
        deleteMenuItem.setVisible(true);

        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.submit:
                return false;
            case R.id.delete:
                deletePhoto();
                return true;
            default:
                break;
        }

        return false;
    }

    public void deletePhoto(){

        mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().getString(R.string.remove_user_profile_photo_title),
                mContext.getResources().getString(R.string.remove_user_profile_photo_message),
                mContext.getResources().getString(R.string.remove_user_profile_photo_title),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String mDeletePhotoUrl = AppConstant.DEFAULT_URL +  "members/edit/remove-photo";
                        mAppConst.deleteResponseForUrl(mDeletePhotoUrl, null, new OnResponseListener() {

                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {
                                EditProfileActivity.isProfileUpdated = true;
                                SnackbarUtils.displaySnackbar(mRootView,
                                        mContext.getResources().getString(R.string.profile_photo_deleted));

                                // Sending the user photo page request again,
                                // when user deleted the profile photo.
                                makeRequest();
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                SnackbarUtils.displaySnackbar(mRootView, message);
                            }
                        });
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void startImageUploading(){

        Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
        intent.putExtra("selection_mode", true);
        startActivityForResult(intent, ConstantVariables.REQUEST_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    startImageUploading();
                } else {
                    // If user deny the permission popup
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.
                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mRootView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

        }
    }
}
