package com.fellopages.mobileapp.classes.core;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status;
import com.fellopages.mobileapp.classes.modules.user.settings.MemberSettingsActivity;
import com.fellopages.mobileapp.classes.modules.user.signup.SignupPhotoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private Context mContext;
    private AppConstant mAppConst;
    private TextInputLayout otpWrapper;
    private String otpCode = "";
    private Button otpVerifyButton;
    private TextView resendOtp, mobileNo;
    private String userPhoneNo, countryCode;
    private Bundle bundle;
    private String intentAction, intentType;
    private String userLoginEmail, userLoginPass;
    private int loginWithOtp = 0, otpDuration = 0;
    private String mPackageId, signupOtp, emailAddress, password, sentTime,
            loginType;
    private Bundle mFbTwitterBundle;
    private AlertDialogWithAction mAlertDialogWithAction;
    private Map<String, String> mPostParams;
    private HashMap<String, String> mAccountFormValues, mSignupParams;
    private boolean isPhotoStep = false, mHasProfileFields = true, isForgotPassword = false,
            isEnableOtp = false, isEditPhoneno = false, isEnableTwoFactor = false;
    private TextView otpTimer;
    CountDownTimer countTimer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mContext = this;
        mAppConst = new AppConstant(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mContext.getResources().getString(R.string.otp_toolbar_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bundle = getIntent().getExtras().getBundle("mBundle");

        if (bundle != null) {
            intentAction = getIntent().getAction();
            intentType = getIntent().getType();
        }

        userPhoneNo = getIntent().getStringExtra("user_phoneno");
        countryCode =  getIntent().getStringExtra("country_code");
        userLoginEmail = getIntent().getStringExtra("user_login_email");
        userLoginPass = getIntent().getStringExtra("user_login_pass");

        if (PreferencesUtils.getOtpEnabledOption(mContext) != null
                && PreferencesUtils.getOtpEnabledOption(mContext).equals("both")) {
            loginWithOtp = 1;
        }

        //for signup otp process
        mPackageId = getIntent().getStringExtra("package_id");
        isPhotoStep = getIntent().getBooleanExtra("isPhotoStep",false);
        isEnableOtp = getIntent().getBooleanExtra("isEnableOtp",false);
        mHasProfileFields = getIntent().getBooleanExtra("mHasProfileFields",true);
        mFbTwitterBundle = getIntent().getBundleExtra("fb_twitter_info");
        signupOtp = getIntent().getStringExtra("otp_code");
        otpDuration = getIntent().getIntExtra("otp_duration", 0);
        sentTime = getIntent().getStringExtra("sent_time");

        mAccountFormValues = (HashMap<String, String>) getIntent().getSerializableExtra("account_form_values");
        mSignupParams = (HashMap<String, String>) getIntent().getSerializableExtra("field_form_values");

        isEditPhoneno = getIntent().getBooleanExtra("isEditPhoneno", false);
        isEnableTwoFactor = getIntent().getBooleanExtra("isEnableTwoFactor", false);
        isForgotPassword = getIntent().getBooleanExtra("isForgotPassword", false);

        if (mAccountFormValues != null) {
            emailAddress = mAccountFormValues.get("email");
            password = mAccountFormValues.get("password");
        }
        if (mFbTwitterBundle != null) {
            loginType = mFbTwitterBundle.getString("loginType");
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
            }
        });

        otpWrapper = findViewById(R.id.otpWrapper);
        otpVerifyButton = findViewById(R.id.otp_verify_button);
        mobileNo = findViewById(R.id.mobile_no);
        resendOtp = findViewById(R.id.resend_otp);
        otpTimer = findViewById(R.id.otp_timer);

        otpWrapper.setHint(getResources().getString(R.string.otp_enter_otp));

        if (countryCode != null && !countryCode.isEmpty() &&
                userPhoneNo != null && !userPhoneNo.isEmpty()) {
            mobileNo.setText("+" + countryCode + "" + userPhoneNo);
        }
        otpVerifyButton.setOnClickListener(this);
        resendOtp.setOnClickListener(this);

        showOtpTimer(otpDuration);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void showOtpTimer(int otpDuration) {
        if (otpDuration != 0) {
            countTimer = new CountDownTimer(otpDuration * 1000, 1000) {

                public void onTick(long millisUntilFinished) {

                    long minDifference = millisUntilFinished / 1000 / 60;
                    long secDifference = (millisUntilFinished - minDifference * 60000) / 1000;
                    otpTimer.setText(minDifference + " : " + secDifference);
                }

                public void onFinish() {
                    otpTimer.setText(getResources().getString(R.string.otp_timer_done_msg));
                }

            }.start();
        }

    }

    public void verifyClicked(){

        otpCode = otpWrapper.getEditText().getText().toString();

        if(otpCode.isEmpty()){
            otpWrapper.setError(getResources().getString(R.string.otp_validation_error));
        }else {
            mAppConst.showProgressDialog();
            otpWrapper.setErrorEnabled(false);

            if (isEnableOtp) {
                verifySignupOTP(otpCode);

            } else if (isEnableTwoFactor){
                verifyTwoFactorOTP(otpCode);

            } else if (isForgotPassword){
                verifyForgotPasswordOTP(otpCode);

            } else {

                Map<String, String> params = new HashMap<>();
                params.put("code", otpCode);
                params.put("email", userLoginEmail);
                params.put("password", userLoginPass);
                params.put("loginWithOtp", String.valueOf(loginWithOtp));
                params.put("ip", GlobalFunctions.getLocalIpAddress());

                mAppConst.postLoginSignUpRequest(UrlUtil.LOGIN_URL, params, new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        mAppConst.hideProgressDialog();

                        mAppConst.proceedToUserLogin(mContext, bundle, intentAction, intentType,
                                userLoginEmail, userLoginPass, jsonObject);
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main), message);
                        mAppConst.hideProgressDialog();
                    }
                });
            }
        }
    }

    public void verifySignupOTP( String otpCode){

        String currentTime = getCurrentDateTime();
        int otpEnteredDuration = GlobalFunctions.secondsDifferenceFromEndDate(sentTime, currentTime);

        if (otpCode.equals(signupOtp)) {

            if (otpEnteredDuration <= otpDuration ) {

                if (isPhotoStep) {
                    Intent photoIntent = new Intent(mContext, SignupPhotoActivity.class);

                    /* facebook and send details to SignUpPhotoActivity */
                    if (mFbTwitterBundle != null && !mFbTwitterBundle.isEmpty()) {
                        photoIntent.putExtra("fb_twitter_info", mFbTwitterBundle);
                    }
                    photoIntent.putExtra("package_id", mPackageId);
                    photoIntent.putExtra("account_form_values", mAccountFormValues);
                    photoIntent.putExtra("field_form_values", mSignupParams);
                    finish();
                    if (mHasProfileFields) {
                        startActivity(photoIntent);
                    } else {
                        mAppConst.hideProgressDialog();
                        startActivityForResult(photoIntent, ConstantVariables.SIGN_UP_CODE);
                    }
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    postSignupForm();
                }

            } else {
                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main),
                        getResources().getString(R.string.otp_expires_msg));
                mAppConst.hideProgressDialog();
            }
        } else {
            otpWrapper.setError(getResources().getString(R.string.otp_invalid_error_msg));
            mAppConst.hideProgressDialog();
        }

    }

    public void verifyTwoFactorOTP( String otpCode){

        String type = null;
        if (isEditPhoneno){
            type = "edit";
        } else {
            type = "add";
        }
        mAppConst.showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put("code", otpCode);
        params.put("type", type);

        mAppConst.postJsonResponseForUrl(UrlUtil.TWO_FACTOR_OTP_VERIFICATION_URL, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mAppConst.hideProgressDialog();

                if (jsonObject != null){
                    setResult(ConstantVariables.TWO_FACTOR_VIEW_PAGE);
                    finish();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main), message);
            }
        });

    }

    public void verifyForgotPasswordOTP(final String otpCode) {

        Map<String, String> params = new HashMap<>();
        params.put("email", userPhoneNo);
        params.put("code", otpCode);

        mAppConst.postJsonResponseForUrl(UrlUtil.FORGOT_PASSWORD_OTP_VERIFY_URL, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mAppConst.hideProgressDialog();

                if (jsonObject != null) {

                    Intent resetPassword = new Intent(mContext, MemberSettingsActivity.class);
                    resetPassword.putExtra("selected_option", "reset_password");
                    resetPassword.putExtra("title", getResources().getString(R.string.reset_password_title));
                    resetPassword.putExtra("url", UrlUtil.FORGOT_PASSWORD_RESET_URL);
                    resetPassword.putExtra("emailValue", jsonObject.optJSONObject("response").optString("email"));
                    resetPassword.putExtra("code", otpCode);
                    finish();
                    startActivity(resetPassword);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main), message);
            }
        });

    }

    public void resendOTP(){

        mAppConst.showProgressDialog();
        final Map<String, String> params = new HashMap<>();

        if (isEnableOtp) {
            params.put("country_code", countryCode);
            params.put("phoneno", userPhoneNo);

            mAppConst.postLoginSignUpRequest(UrlUtil.SIGNUP_OTP_SEND_URL, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();

                    if (jsonObject != null) {
                        signupOtp = jsonObject.optJSONObject("response").optString("code");
                        sentTime = getCurrentDateTime();
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main),
                                getResources().getString(R.string.otp_resend_success_msg));
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main), message);
                }
            });

        } else if (isEnableTwoFactor) {
            params.put("country_code", countryCode);
            params.put("mobileno", userPhoneNo);

            mAppConst.postJsonResponseForUrl(UrlUtil.TWO_FACTOR_EDIT_MOBILE_URL, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();

                    if (jsonObject != null) {
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main),
                                getResources().getString(R.string.otp_resend_success_msg));
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main), message);
                }
            });

        } else if (isForgotPassword) {
            params.put("email", userPhoneNo);

            mAppConst.postJsonResponseForUrl(UrlUtil.FORGOT_PASSWORD_OTP_URL, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();

                    if (jsonObject != null) {
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main),
                                getResources().getString(R.string.otp_resend_success_msg));
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main), message);
                }
            });

        } else {
            params.put("email", userLoginEmail);
            params.put("password", userLoginPass);
            params.put("ip", GlobalFunctions.getLocalIpAddress());

            mAppConst.postLoginSignUpRequest(UrlUtil.LOGIN_OTP_URL, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();
                    if (jsonObject != null) {
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main),
                                getResources().getString(R.string.otp_resend_success_msg));
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main), message);
                }
            });
        }

        countTimer.cancel();
        showOtpTimer(otpDuration);
    }

    public void postSignupForm() {
        mAppConst.showProgressDialog();
        mPostParams = new HashMap<>();

        if (mPackageId != null) {
            mPostParams.put("package_id", mPackageId);
        }
        if (mAccountFormValues != null) {
            Set<String> keySet = mAccountFormValues.keySet();

            for (String key : keySet) {
                String value = mAccountFormValues.get(key);
                mPostParams.put(key, value);
            }
        }
        if (mSignupParams != null) {

            Set<String> keySet = mSignupParams.keySet();

            for (String key : keySet) {
                String value = mSignupParams.get(key);
                mPostParams.put(key, value);
            }
        }
        mPostParams.put("ip", GlobalFunctions.getLocalIpAddress());
        String postSignupUrl = AppConstant.DEFAULT_URL + "signup?subscriptionForm=1";

        if (loginType != null && !loginType.isEmpty()) {
            postSignupUrl = mAppConst.buildQueryString(postSignupUrl,
                    SocialLoginUtil.getFacebookTwitterParams());
        }

        mAppConst.postLoginSignUpRequest(postSignupUrl, mPostParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mAppConst.proceedToUserSignup(mContext, mFbTwitterBundle, emailAddress,
                        password, jsonObject.optString("body"), jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();

                switch (message) {
                    case "email_not_verified":
                    case "not_approved":
                        SocialLoginUtil.clearFbTwitterInstances(mContext, loginType);
                        mAlertDialogWithAction.showAlertDialogForSignUpError(message);
                        break;

                    default:
                        SnackbarUtils.displaySnackbar(findViewById(R.id.otp_main), message);
                        break;
                }
            }
        });
    }

    public static String getCurrentDateTime(){
        //for getting current date and time
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.otp_verify_button:
                if (GlobalFunctions.isNetworkAvailable(mContext)) {
                    mAppConst.hideKeyboard();
                    verifyClicked();
                } else {
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main),
                            getResources().getString(R.string.network_connectivity_error));
                }
                break;

            case R.id.resend_otp:
                if (GlobalFunctions.isNetworkAvailable(mContext)) {
                    mAppConst.hideKeyboard();
                    resendOTP();
                } else {
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.otp_main),
                            getResources().getString(R.string.network_connectivity_error));
                }
                break;
        }
    }
}
