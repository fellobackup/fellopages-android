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

package com.fellopages.mobileapp.classes.modules.user.signup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.PhotoUploadingActivity;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SignupPhotoActivity extends AppCompatActivity implements OnUploadResponseListener {

    private Toolbar mToobar;
    private Button mUploadImageButton;
    private ImageView mSelectedImage;
    private Context mContext;
    private String loginType;
    private HashMap<String, String> mFieldsParams, mAccountFormParams;
    private Map<String, String> mPostParams;
    private Bundle mFbTwitterBundle;
    public static final int REQUEST_IMAGE = 100;
    private ArrayList<String> mSelectPath;
    private AppConstant mAppConst;
    private String mPostUrl, mPackageId, picture;
    private int width;
    private String emailAddress, password;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;
    private boolean isPermissionForFacebookImage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_photo);

        mContext = this;
        mAppConst = new AppConstant(this);
        width = AppConstant.getDisplayMetricsWidth(SignupPhotoActivity.this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(mContext);

        mPostUrl = UrlUtil.SIGNUP_URL + "&subscriptionForm=1";
        mToobar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToobar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToobar);

        /* Get details from intents, Check signUp type, facebook,twitter
           * and add required parameters to  Post SignUp Url */

        Intent loginIntent = getIntent();
        mPackageId = loginIntent.getStringExtra("package_id");

        mFbTwitterBundle = loginIntent.getBundleExtra("fb_twitter_info");
        if (mFbTwitterBundle != null) {
            loginType = mFbTwitterBundle.getString("loginType");
            picture = mFbTwitterBundle.getString("picture");
            mPostUrl = mAppConst.buildQueryString(mPostUrl, SocialLoginUtil.getFacebookTwitterParams());
            if (loginType.equals("facebook")) {
                picture = mFbTwitterBundle.getString("picture");
            }
        }

        mAccountFormParams = (HashMap<String, String>) getIntent().getSerializableExtra("account_form_values");
        mFieldsParams = (HashMap<String, String>) getIntent().getSerializableExtra("field_form_values");

        emailAddress = mAccountFormParams.get("email");
        password = mAccountFormParams.get("password");

        mUploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        mSelectedImage = (ImageView) findViewById(R.id.selectedImage);

        mImageLoader.setPersonImageUrl(picture, mSelectedImage);

        mUploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPermissionForFacebookImage = false;

                /* Check if permission is granted or not */
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                } else {
                    startImageUploading();
                }
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        switch (requestCode) {
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                    // clearing picture which is coming from facebook when the user select another image from media.
                    picture = null;
                    try {
                        // Getting Bitmap from its real path.
                        Bitmap selectedImageBitmap = BitmapUtils.decodeSampledBitmapFromFile(mContext, mSelectPath.get(0), width,
                                (int) getResources().getDimension(R.dimen.sing_up_image_width_height), false);
                        mSelectedImage.setImageBitmap(selectedImageBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
                    SnackbarUtils.displaySnackbar(findViewById(R.id.main_frame),
                            mContext.getResources().getString(R.string.image_capture_failed));
                }
                break;
            case ConstantVariables.SIGN_UP_WEBVIEW_CODE:

                /**
                 * Clear Twitter and Facebook instances if subscription
                 * payment is not completed
                 */
                SocialLoginUtil.clearFbTwitterInstances(this, loginType);

                mAlertDialogWithAction.showAlertDialogForSignUpError("payment_error");

                break;
            default:
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.submit).setTitle(mContext.getResources().getString(R.string.edit_title_dialogue_button)).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
                break;

            case R.id.submit:
                postSignupForm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void postSignupForm() {

        mPostParams = new HashMap<>();

        if (mPackageId != null) {
            mPostParams.put("package_id", mPackageId);
        }

        if (mAccountFormParams != null) {
            Set<String> keySet = mAccountFormParams.keySet();

            for (String key : keySet) {
                String value = mAccountFormParams.get(key);
                mPostParams.put(key, value);
            }
        }
        if (mFieldsParams != null) {

            Set<String> keySet = mFieldsParams.keySet();

            for (String key : keySet) {
                String value = mFieldsParams.get(key);
                mPostParams.put(key, value);
            }
        }
        mPostParams.put("ip", GlobalFunctions.getLocalIpAddress());

        if (mSelectPath != null && mSelectPath.size() != 0 || (picture != null && !picture.isEmpty())) {
            if (picture != null && !picture.isEmpty() && !mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                isPermissionForFacebookImage = true;

                mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        ConstantVariables.WRITE_EXTERNAL_STORAGE);
            } else {
                new UploadFileToServerUtils(mContext, mPostUrl, mSelectPath, mPostParams, picture).execute();
            }

        } else {
            mAppConst.showProgressDialog();
            mAppConst.postLoginSignUpRequest(mPostUrl, mPostParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    /**
                     * Check If there user has chosen a paid subscription
                     * redirect to the web view activity on the url which is coming with body string in response.
                     * else user will be logged-in
                     */
                    mAppConst.proceedToUserSignup(mContext, mFbTwitterBundle, emailAddress,
                            password, jsonObject.optString("body"), jsonObject);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    showSignUpError(message, message);
                }
            });
        }
    }

    /**
     * Method to show sign up error alert dialog according to error code.
     *
     * @param errorCode ErrorCode
     * @param message   Error Message.
     */
    public void showSignUpError(String errorCode, String message) {
        switch (errorCode) {
            case "email_not_verified":
            case "not_approved":
                SocialLoginUtil.clearFbTwitterInstances(this, loginType);
                mAlertDialogWithAction.showAlertDialogForSignUpError(errorCode);
                break;
            default:
                if (GlobalFunctions.isValidJson(message)) {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        if (jsonObject.has("photo")) {
                            message = mContext.getResources().getString(R.string.sign_up_photo_error);
                        } else {
                            message = jsonObject.optString(jsonObject.keys() != null && jsonObject.keys().next() != null
                                    && !jsonObject.keys().next().isEmpty() ? jsonObject.keys().next() : message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                SnackbarUtils.displaySnackbar(findViewById(R.id.main_frame), message);
                break;
        }
    }

    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        String errorCode = jsonObject.optString("error_code");
        if (isRequestSuccessful) {
            // When sign up using fb user picture then deleting from local after successful login.
            if (picture != null && !picture.isEmpty()) {
                BitmapUtils.deleteImageFolder();
            }
            mAppConst.proceedToUserSignup(mContext, mFbTwitterBundle, emailAddress,
                    password, jsonObject.optJSONObject("body") == null ? jsonObject.optString("body") : null,
                    jsonObject.optJSONObject("body"));

        } else if (errorCode != null) {
            showSignUpError(errorCode, jsonObject.optString("message"));
        } else {
            SnackbarUtils.displaySnackbar(findViewById(R.id.main_frame),
                    jsonObject.optString("message"));
        }
    }

    private void startImageUploading() {
        Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
        intent.putExtra("selection_mode", true);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, proceed to the normal flow
                    if (isPermissionForFacebookImage) {
                        new UploadFileToServerUtils(mContext, mPostUrl, mSelectPath, mPostParams, picture).execute();
                    } else {
                        startImageUploading();
                    }
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.
                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.
                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext,
                                findViewById(R.id.main_frame), ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            Drawable drawable = submit.getIcon();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
