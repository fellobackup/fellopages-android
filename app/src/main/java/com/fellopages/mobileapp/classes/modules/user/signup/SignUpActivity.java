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

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;

import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignUpActivity extends FormActivity {

    private AppConstant mAppConst;
    private RelativeLayout mAccountFormView;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    public static String TAG = "SignUp Activity";
    private Map<String, String> postParams;
    private HashMap<String, String> accountParams;
    private Bundle mFbTwitterBundle;
    private boolean isError = false;
    private Context mContext;
    private JSONObject mFieldJsonObject, mSelectedFieldsObject, loginFromObject;
    private JSONObject emailJsonObject, mResponseObject;
    private boolean isPhotoStep = false;
    private String loginType, email, jsonResponse, first_name, last_name, picture, timezone;
    private String selectedProfileType, mSelectedPackageId, profileAddress;
    private boolean isEnableOtp = false, isDiffrentField = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);

        mContext = this;
        mAppConst = new AppConstant(mContext);

        mAccountFormView = findViewById(R.id.form_view);
        mProgressBar = findViewById(R.id.progressBar);

        mProgressBar.setVisibility(View.GONE);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CustomViews.createMarqueeTitle(this, mToolbar);

        Intent loginIntent = getIntent();

        mSelectedPackageId = loginIntent.getStringExtra("package_id");
        String signUpResponse = loginIntent.getStringExtra("signup_response");
        if (signUpResponse != null && !signUpResponse.isEmpty()) {
            try {
                mResponseObject = new JSONObject(signUpResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mResponseObject.optInt("isEnableotp") == 1){
            isEnableOtp = true;
        }

        if (mResponseObject.optInt("isDiffrentField") == 1){
            isDiffrentField = true;
        }

        mFbTwitterBundle = loginIntent.getBundleExtra("fb_twitter_info");
        if(mFbTwitterBundle != null) {
            jsonResponse = mFbTwitterBundle.getString("response");
            loginType = mFbTwitterBundle.getString("loginType");
            email = mFbTwitterBundle.getString("email");
            if (loginType.equals("facebook")) {
                first_name = mFbTwitterBundle.getString("firstName");
                last_name = mFbTwitterBundle.getString("lastName");
                picture = mFbTwitterBundle.getString("picture");
                timezone = mFbTwitterBundle.getString("timezone");

                profileAddress = first_name + last_name;

            } else if (loginType.equals("twitter")) {
                profileAddress = mFbTwitterBundle.getString("username");
            }

            try {
                loginFromObject = new JSONObject(jsonResponse);
                isPhotoStep = loginFromObject.has("photo");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            if (loginType != null && !loginType.isEmpty()) {

                profileAddress += new Random().nextInt(10);

                emailJsonObject = new JSONObject();
                emailJsonObject.put("email", email);
                emailJsonObject.put("username", profileAddress);
                loginFromObject.put("formValues", emailJsonObject);

                mFieldJsonObject = loginFromObject.optJSONObject("fields");
                mAccountFormView.addView(populate(loginFromObject, "signup_account"));

            } else {
                mFieldJsonObject = mResponseObject.optJSONObject("fields");
                isPhotoStep = mResponseObject.has("photo");
                mAccountFormView.addView(generateForm(mResponseObject, false, "signup_account"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.submit).setIcon(null);
        menu.findItem(R.id.submit).setTitle(mContext.getResources().getString(R.string.signup_next_step));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){

            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
                break;
            case R.id.submit:
                validateAccountForm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void validateAccountForm(){

        isError = false;
        String accountValidationUrl = AppConstant.DEFAULT_URL + "signup/validations?fields_validation=0";

         /* Check signUp type, facebook and add required parameters to Validation Url*/
        if (loginType != null && !loginType.isEmpty()) {
            accountValidationUrl = mAppConst.buildQueryString(accountValidationUrl,
                    SocialLoginUtil.getFacebookTwitterParams());
        }

        accountValidationUrl = mAppConst.buildQueryString(accountValidationUrl,
                mAppConst.getAuthenticationParams());


        postParams = new HashMap<>();
        postParams = save();

        accountParams = new HashMap<>();
        accountParams = (HashMap<String, String> ) postParams;


        if(postParams != null){
            mAppConst.hideKeyboard();
            if(postParams.containsKey("profile_type")){
                selectedProfileType = postParams.get("profile_type");
                if (mFieldJsonObject != null) {
                    mSelectedFieldsObject = mFieldJsonObject.optJSONObject(selectedProfileType);
                }
            }else{
                mSelectedFieldsObject = mFieldJsonObject;
            }
            mProgressBar.bringToFront();
            mProgressBar.setVisibility(View.VISIBLE);

            // Checking if the field object is null.
            if (mSelectedFieldsObject == null || mSelectedFieldsObject.length() == 0) {
                mSelectedFieldsObject = new JSONObject();
            }

            mAppConst.postJsonResponseForUrl(accountValidationUrl, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProgressBar.setVisibility(View.GONE);

                    if(!isError){
                        Intent fieldsIntent = new Intent(mContext, FieldsFormActivity.class);

                        if (getIntent().getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false)) {
                            fieldsIntent.putExtra(ConstantVariables.KEY_USER_CREATE_SESSION, true);
                        }

                        /* Check signUp type, facebook and send details to FieldFormActivity */

                        if (mFbTwitterBundle != null && !mFbTwitterBundle.isEmpty()) {
                            fieldsIntent.putExtra("fb_twitter_info", mFbTwitterBundle);
                        }

                        fieldsIntent.putExtra("fields", mSelectedFieldsObject.toString());
                        fieldsIntent.putExtra("isPhotoStep", isPhotoStep);
                        fieldsIntent.putExtra("account_fields", accountParams);
                        fieldsIntent.putExtra("selectedProfileType", selectedProfileType);
                        fieldsIntent.putExtra("package_id", mSelectedPackageId);
                        fieldsIntent.putExtra("isEnableotp", isEnableOtp);
                        fieldsIntent.putExtra("isDiffrentField", isDiffrentField);
                        startActivityForResult(fieldsIntent, ConstantVariables.SIGN_UP_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    try {
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject validationMessagesObject = new JSONObject(message);
                        isError = showValidations(validationMessagesObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false)) {
            setResult(ConstantVariables.CODE_USER_CREATE_SESSION_CANCELLED);
            finish();
        }

        SocialLoginUtil.clearFbTwitterInstances(this, loginType);
        if(mSelectedPackageId != null && !mSelectedPackageId.isEmpty()){
            Intent intent = new Intent();
            intent.putExtra("package_id", Integer.parseInt(mSelectedPackageId));
            setResult(ConstantVariables.SIGN_UP_CODE, intent);
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Remove the existing form and generate the form again on back pressed from fields activity.
         * As the FormActivity now have the layout of fields form activity
         * so, on this activity, the fields are blank and it do not proceed further.
         */

        if(requestCode == ConstantVariables.SIGN_UP_CODE && data != null){
            HashMap<String, String> accountFormValues = (HashMap<String, String>)data.
                    getSerializableExtra("accountFormValues");

            mAccountFormView.removeView(mAccountFormView.findViewById(R.id.form_layout));

            try{
                if(accountFormValues != null){
                    JSONObject formValues = new JSONObject();
                    for (Map.Entry<String,String> entry : accountFormValues.entrySet()) {
                        String key = entry.getKey();
                        formValues.put(key, entry.getValue());
                    }

                    if(loginType != null){
                        loginFromObject.put("formValues", formValues);
                        mAccountFormView.addView(populate(loginFromObject, "signup_account"));
                    }else{
                        mResponseObject.put("formValues", formValues);
                        mAccountFormView.addView(populate(mResponseObject, "signup_account"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (resultCode == ConstantVariables.CODE_USER_CREATE_SESSION) {
            setResult(ConstantVariables.CODE_USER_CREATE_SESSION);
            finish();
        }
    }
}
