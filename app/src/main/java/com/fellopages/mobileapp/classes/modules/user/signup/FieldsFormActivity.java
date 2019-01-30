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
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.MainActivity;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.core.OTPActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class FieldsFormActivity extends FormActivity {

    private Toolbar mToolBar;
    private ProgressBar mProgressBar;
    private JSONObject mFieldsJsonObject;
    private RelativeLayout mAccountFormView;
    private boolean isPhotoStep, mHasProfileFields = true;
    private AppConstant mAppConst;
    private Map<String, String> singupFieldsParams, postParams;
    private HashMap<String, String> mAccountFormValues, mSignupParams;
    private Bundle mFbTwitterBundle;
    private Context mContext;
    private AlertDialogWithAction mAlertDialogWithAction;
    private String loginType, emailAddress, password, mProfileType, mPackageId, firstName,
            lastName, picture;
    private boolean isEnableOtp = false, isDiffrentField = false;
    private String countryCode, emailaddress, phoneno;
    private OTPActivity otpActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mContext = this;
        mAppConst = new AppConstant(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        otpActivity = new OTPActivity();

        Intent loginIntent = getIntent();
        mFbTwitterBundle = loginIntent.getBundleExtra("fb_twitter_info");
        isEnableOtp = loginIntent.getBooleanExtra("isEnableotp", false);
        isDiffrentField = loginIntent.getBooleanExtra("isDiffrentField", false);

        if (mFbTwitterBundle != null) {
            loginType = mFbTwitterBundle.getString("loginType");
            if (loginType != null && loginType.equals("facebook")) {
                firstName = mFbTwitterBundle.getString("firstName");
                lastName = mFbTwitterBundle.getString("lastName");
                picture = mFbTwitterBundle.getString("picture");
            }
        }
        mProfileType = loginIntent.getStringExtra("selectedProfileType");
        mPackageId = loginIntent.getStringExtra("package_id");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolBar);

        mAccountFormView = (RelativeLayout) findViewById(R.id.form_view);

        try {
            mFieldsJsonObject = new JSONObject(getIntent().getStringExtra("fields"));
            mAccountFormValues = (HashMap<String, String>) getIntent().getSerializableExtra("account_fields");
            emailAddress = mAccountFormValues.get("email");
            password = mAccountFormValues.get("password");
            isPhotoStep = getIntent().getBooleanExtra("isPhotoStep", false);
            countryCode = mAccountFormValues.get("country_code");
            if (isDiffrentField) {
                phoneno = mAccountFormValues.get("phoneno");
            } else {
                emailaddress = mAccountFormValues.get("emailaddress");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Checking if there is any profile fields present or not. If present then load in view,
        // otherwise sent the sign up post request.
        if(mFieldsJsonObject != null && mFieldsJsonObject.length() > 0) {
            mProgressBar.setVisibility(View.GONE);
            mAccountFormView.addView(generateForm(mFieldsJsonObject, false, ConstantVariables.SIGN_UP_FIELDS, firstName, lastName));
        } else {
            mHasProfileFields = false;
            mAppConst.showProgressDialog();
            validateFieldsForm();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
                break;

            case R.id.submit:
                validateFieldsForm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem addItem = menu.findItem(R.id.submit);

        if(isPhotoStep || isEnableOtp){
            addItem.setIcon(null);
            addItem.setTitle(mContext.getResources().getString(R.string.signup_next_step));
        }else {
            Drawable drawable = addItem.getIcon();
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary),
                    PorterDuff.Mode.SRC_ATOP));
            addItem.setTitle(mContext.getResources().getString(R.string.edit_title_dialogue_button));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("accountFormValues", mAccountFormValues);
        setResult(ConstantVariables.SIGN_UP_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void validateFieldsForm(){

        String accountValidationUrl = AppConstant.DEFAULT_URL + "signup/validations?account_validation=0";

         /* Check signUp type, facebook and add required parameters to Validation Url*/
        if (loginType != null && !loginType.isEmpty()) {
            accountValidationUrl = mAppConst.buildQueryString(accountValidationUrl,
                    SocialLoginUtil.getFacebookTwitterParams());
        }

        singupFieldsParams = save();

        mSignupParams = new HashMap<>();
        mSignupParams = (HashMap<String, String>) singupFieldsParams;

        if(singupFieldsParams != null){
            if (mProfileType != null) {
                singupFieldsParams.put("profile_type", mProfileType);
            }

            mAppConst.hideKeyboard();
            mProgressBar.bringToFront();
            mProgressBar.setVisibility(View.VISIBLE);
            mAppConst.postJsonResponseForUrl(accountValidationUrl, singupFieldsParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProgressBar.setVisibility(View.GONE);

                    checkOtpSteps();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    try {
                        JSONObject validationMessagesObject = new JSONObject(message);
                        showValidations(validationMessagesObject);
                        mProgressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void checkOtpSteps() {
        if (isEnableOtp){
            Map<String, String> params = new HashMap<>();
            params.put("country_code", countryCode);
            if(emailaddress != null){
                params.put("emailaddress", emailaddress);
            } else if (phoneno != null){
                params.put("phoneno", phoneno);
            }

            mAppConst.postLoginSignUpRequest(UrlUtil.SIGNUP_OTP_SEND_URL, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    mProgressBar.setVisibility(View.GONE);

                    if (jsonObject != null) {

                        JSONObject otpJsonObject = jsonObject.optJSONObject("response");
                        String userPhoneno = otpJsonObject.optString("phoneno");
                        String userCountryCode = otpJsonObject.optString("country_code");
                        boolean isOtpSend = otpJsonObject.optBoolean("isOtpSend");
                        String otpCode = otpJsonObject.optString("code");
                        int otpDuration = otpJsonObject.optInt("duration");

                        if (isOtpSend) {
                            String sentTime = otpActivity.getCurrentDateTime();

                            Intent otpIntent = new Intent(mContext, OTPActivity.class);
                            otpIntent.putExtra("isPhotoStep", isPhotoStep);
                            otpIntent.putExtra("country_code", userCountryCode);
                            otpIntent.putExtra("user_phoneno", userPhoneno);
                            otpIntent.putExtra("otp_duration", otpDuration);
                            otpIntent.putExtra("mHasProfileFields", mHasProfileFields);
                            otpIntent.putExtra("isEnableOtp", isEnableOtp);
                            otpIntent.putExtra("otp_code", otpCode);
                            otpIntent.putExtra("sent_time", sentTime);          //sent current Date and Time
                            if (mFbTwitterBundle != null && !mFbTwitterBundle.isEmpty()) {
                                otpIntent.putExtra("fb_twitter_info", mFbTwitterBundle);
                            }

                            otpIntent.putExtra("package_id", mPackageId);
                            otpIntent.putExtra("account_form_values", mAccountFormValues);
                            otpIntent.putExtra("field_form_values", mSignupParams);
                            startActivity(otpIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        } else if (isPhotoStep) {
                            signupPhotoActivity();
                        } else {
                            postSignupForm();
                        }
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.form_view), message);
                }
            });

        } else if (isPhotoStep){
            signupPhotoActivity();
        }else{
            postSignupForm();
        }
    }

    public void signupPhotoActivity(){
        Intent photoIntent = new Intent(mContext, SignupPhotoActivity.class);

        /* facebook and send details to SignUpPhotoActivity */

        if (mFbTwitterBundle != null && !mFbTwitterBundle.isEmpty()) {
            photoIntent.putExtra("fb_twitter_info", mFbTwitterBundle);
        }

        photoIntent.putExtra("package_id", mPackageId);
        photoIntent.putExtra("account_form_values", mAccountFormValues);
        photoIntent.putExtra("field_form_values", mSignupParams);
        if (mHasProfileFields) {
            if (getIntent().getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false)) {
                photoIntent.putExtra(ConstantVariables.KEY_USER_CREATE_SESSION, true);
                startActivityForResult(photoIntent, ConstantVariables.CODE_USER_CREATE_SESSION);
            } else {
                startActivity(photoIntent);
            }
        } else {
            mAppConst.hideProgressDialog();
            startActivityForResult(photoIntent, ConstantVariables.SIGN_UP_CODE);
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void postSignupForm(){

        mAppConst.showProgressDialog();
        postParams = new HashMap<>();

        if(mPackageId != null){
            postParams.put("package_id", mPackageId);
        }

        if(mAccountFormValues != null){
            Set<String> keySet = mAccountFormValues.keySet();

            for (String key : keySet) {
                String value =  mAccountFormValues.get(key);
                postParams.put(key, value);
            }
        }

        if(singupFieldsParams != null){

            Set<String> keySet = singupFieldsParams.keySet();

            for (String key : keySet) {
                String value =  singupFieldsParams.get(key);
                postParams.put(key, value);
            }
        }

        postParams.put("ip", GlobalFunctions.getLocalIpAddress());

        String postSignupUrl = AppConstant.DEFAULT_URL + "signup?subscriptionForm=1";

        if (loginType != null && !loginType.isEmpty()) {
            postSignupUrl = mAppConst.buildQueryString(postSignupUrl,
                    SocialLoginUtil.getFacebookTwitterParams());
        }

        mAppConst.postLoginSignUpRequest(postSignupUrl, postParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mAppConst.proceedToUserSignup(mContext, mFbTwitterBundle, emailAddress,
                                            password, jsonObject.optString("body"), jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                mProgressBar.setVisibility(View.GONE);

                switch (message) {
                    case "email_not_verified":
                    case "not_approved":
                        SocialLoginUtil.clearFbTwitterInstances(mContext, loginType);
                        mAlertDialogWithAction.showAlertDialogForSignUpError(message);
                        break;

                    default:
                        SnackbarUtils.displaySnackbar(mAccountFormView, message);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ConstantVariables.SIGN_UP_WEBVIEW_CODE){

            /**
             * Clear Twitter and Facebook instances if subscription
             * payment is not completed
             */
            SocialLoginUtil.clearFbTwitterInstances(this, loginType);

            mAlertDialogWithAction.showAlertDialogForSignUpError("payment_error");
        } else if (requestCode == ConstantVariables.SIGN_UP_CODE) {
            onBackPressed();
        } else if (resultCode == ConstantVariables.CODE_USER_CREATE_SESSION) {
            setResult(ConstantVariables.CODE_USER_CREATE_SESSION);
            finish();
        }
    }
}
