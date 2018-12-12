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

package com.fellopages.mobileapp.classes.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status;
import com.fellopages.mobileapp.classes.modules.user.signup.SubscriptionActivity;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class NewLoginActivity extends AppCompatActivity implements View.OnClickListener,
        SocialLoginUtil.OnSocialLoginSuccessListener {


    private RelativeLayout mMainView;
    private Button loginButton;
    private String emailValue = "", passwordValue = "";
    private boolean isValidatingData = false, isError = false, isShowFacebookButton,
            isShowTwitterButton, isNormalLogin = false;;
    private Context mContext;
    private AppConstant mAppConst;
    private Bundle bundle;
    private String intentAction, intentType;
    private TextView errorView, mBrowseAsGuest, mChooseLanguage, showPassword, mForgotPassword;
    private LoginButton facebookLoginButton;
    private TwitterLoginButton twitterLoginButton;
    private EditText eEmailField, ePasswordField;
    private boolean isPasswordShow = false;
    private CallbackManager callbackManager;
    private TextView fbButton, twButton;
    private TextInputLayout usernameWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Initialize Facebook SDK, we need to initialize before using it ---- */
        SocialLoginUtil.initializeFacebookSDK(NewLoginActivity.this);

        SocialLoginUtil.setSocialLoginListener(this);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        setContentView(R.layout.activity_login_new);

        mContext = this;
        mAppConst = new AppConstant(this);

        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            intentAction = getIntent().getAction();
            intentType = getIntent().getType();
        }

        mMainView = findViewById(R.id.scrollView);
        ImageView icon = findViewById(R.id.app_icon);
        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        eEmailField = findViewById(R.id.email_field);
        ePasswordField = findViewById(R.id.password_field);

        mForgotPassword = findViewById(R.id.forgot_password);
        mForgotPassword.setOnClickListener(this);

        usernameWrapper.setHint(getResources().getString(R.string.lbl_enter_email));

        errorView = findViewById(R.id.error_view);

        facebookLoginButton = findViewById(R.id.facebook_login_button);
        fbButton = findViewById(R.id.fb_button);
        twitterLoginButton= findViewById(R.id.twitter_login_button);
        twButton = findViewById(R.id.tw_button);

        showPassword = findViewById(R.id.show_password_icon);
        showPassword.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        showPassword.setText("\uf06e");
        showPassword.setOnClickListener(this);

        TextView registerText = findViewById(R.id.register_text);
        registerText.setOnClickListener(this);

        mBrowseAsGuest = findViewById(R.id.browse_as_guest);
        mBrowseAsGuest.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
        mBrowseAsGuest.setOnClickListener(this);

        mChooseLanguage = (TextView) findViewById(R.id.choose_language);
        mChooseLanguage.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
        mChooseLanguage.setOnClickListener(this);

        Drawable ivEmailIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_email_white_24dp);
        ivEmailIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_light), PorterDuff.Mode.SRC_IN);
        eEmailField.setCompoundDrawablesWithIntrinsicBounds(ivEmailIcon, null, null, null);
        int drawablePadding = mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp);
        eEmailField.setCompoundDrawablePadding(drawablePadding);


        Drawable ivPasswordIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_lock_white_24dp);
        ivPasswordIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_light), PorterDuff.Mode.SRC_IN);
        ePasswordField.setCompoundDrawablesWithIntrinsicBounds(ivPasswordIcon, null, null, null);
        ePasswordField.setCompoundDrawablePadding(drawablePadding);

        if( PreferencesUtils.getOtpEnabledOption(mContext) != null &&
                !PreferencesUtils.getOtpEnabledOption(mContext).isEmpty()) {
            if (PreferencesUtils.getOtpEnabledOption(mContext).equals("both")) {
                ePasswordField.setVisibility(View.GONE);
                showPassword.setVisibility(View.GONE);
                mForgotPassword.setVisibility(View.GONE);
                setViewHideShow();

            } else {
                ePasswordField.setVisibility(View.VISIBLE);
                mForgotPassword.setVisibility(View.VISIBLE);
            }
            usernameWrapper.setHint(getResources().getString(R.string.lbl_enter_email_phone));
        }
        LogUtils.LOGD(NewLoginActivity.class.getSimpleName(), "OtpEnabledOption->" + PreferencesUtils.getOtpEnabledOption(mContext));


        /* Set ui configuration of facebook and twitter login button */

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Hide Facebook button when facebook_app_id is null or Empty.
        if (!getResources().getString(R.string.facebook_app_id).isEmpty()) {
            fbButton.setVisibility(View.VISIBLE);
            isShowFacebookButton = true;
        } else {
            fbButton.setVisibility(View.GONE);
            isShowFacebookButton = false;
        }

        // Hide twitter button when twitter_key or twitter_secret is null or Empty.
        if (!getResources().getString(R.string.twitter_key).isEmpty() &&
                !getResources().getString(R.string.twitter_secret).isEmpty()) {

            isShowTwitterButton = true;
            String twitterLoginText;

            if (isShowFacebookButton) {
                twitterLoginText = mContext.getResources().getString(R.string.twitter);
            } else {
                twitterLoginText = mContext.getResources().getString(R.string.twitter_login_text);
                twButton.setLayoutParams(layoutParams);
            }

            twButton.setVisibility(View.VISIBLE);
            twButton.setText(twitterLoginText);
        } else {
            twButton.setVisibility(View.GONE);
            isShowTwitterButton = false;
        }

        if (!isShowTwitterButton && isShowFacebookButton) {
            fbButton.setLayoutParams(layoutParams);
            fbButton.setText(mContext.getResources().getString(R.string.com_facebook_loginview_log_in_button_long));
        }

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginButton.performClick();
            }
        });

        twButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterLoginButton.performClick();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));

        //Facebook login authentication process
        SocialLoginUtil.registerFacebookLoginCallback(NewLoginActivity.this, mMainView, callbackManager, false);

        //Twitter login authentication process
        SocialLoginUtil.registerTwitterLoginCallback(NewLoginActivity.this, mMainView, twitterLoginButton, false);

        setBrowseGuestAndLanguageOptions();
    }

    public void setViewHideShow() {
        eEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().matches("-?\\d+(.\\d+)?")) {
                    isNormalLogin = false;
                    mForgotPassword.setVisibility(View.GONE);
                    ePasswordField.setVisibility(View.GONE);
                    showPassword.setVisibility(View.GONE);
                    loginButton.setText(getResources().getString(R.string.otp_login_btn_name));

                } else {
                    isNormalLogin = true;
                    loginButton.setText(getResources().getString(R.string.login_btn_name));
                    mForgotPassword.setVisibility(View.VISIBLE);
                    if (charSequence.length() != 0) {
                        ePasswordField.setVisibility(View.VISIBLE);
                        showPassword.setVisibility(View.VISIBLE);
                        mForgotPassword.setVisibility(View.VISIBLE);
                    } else {
                        ePasswordField.setVisibility(View.GONE);
                        showPassword.setVisibility(View.GONE);
                        mForgotPassword.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void LoginClicked() {

        emailValue = eEmailField.getText().toString();

        passwordValue = ePasswordField.getText().toString();

        if (emailValue.isEmpty()) {
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));
            eEmailField.setError(getResources().getString(R.string.email_address_message));

        } else if (!ePasswordField.isShown()) {
            ePasswordField.setVisibility(View.VISIBLE);
            showPassword.setVisibility(View.VISIBLE);
            eEmailField.setError(null);
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));

        } else if (passwordValue.isEmpty()) {
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));
            ePasswordField.setError(getResources().getString(R.string.password_message));
            ePasswordField.requestFocus();

        } else {
            mAppConst.showProgressDialog();

            Map<String, String> params = new HashMap<>();
            params.put("email", emailValue);
            params.put("password", passwordValue);
            params.put("ip", GlobalFunctions.getLocalIpAddress());

            mAppConst.postLoginSignUpRequest(UrlUtil.LOGIN_URL, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();

                    mAppConst.proceedToUserLogin(mContext, bundle, intentAction, intentType,
                            emailValue, passwordValue, jsonObject);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    displayError(message);
                    mAppConst.hideProgressDialog();
                }
            });
        }
    }

    public void otpLoginClicked(){

        emailValue = eEmailField.getText().toString();
        passwordValue = ePasswordField.getText().toString();

        if (emailValue.isEmpty()){
            isValidatingData = false;
            eEmailField.setError(getResources().getString(R.string.email_address_phoneno_msg));

        } else if (passwordValue.isEmpty() && PreferencesUtils.getOtpEnabledOption(mContext).equals("otp")) {
            eEmailField.setError(null);
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));
            ePasswordField.setError(getResources().getString(R.string.password_message));

        } else {
            mAppConst.showProgressDialog();
            eEmailField.setError(null);
            ePasswordField.setError(null);

            final Map<String, String> params = new HashMap<>();
            params.put("email", emailValue);
            params.put("password", passwordValue);
            params.put("ip", GlobalFunctions.getLocalIpAddress());

            LogUtils.LOGD(LoginActivity.class.getSimpleName(),"otp_login_params->" +params);
            mAppConst.postLoginSignUpRequest(UrlUtil.LOGIN_OTP_URL, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    mAppConst.hideProgressDialog();
                    sendToOtp(jsonObject);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    displayError(message);
                    mAppConst.hideProgressDialog();
                }
            });
        }
    }

    public void sendToOtp(JSONObject jsonObject){

        if (jsonObject != null) {
            if (!jsonObject.has("oauth_token") || jsonObject.has("phoneno")) {

                LogUtils.LOGD(LoginActivity.class.getSimpleName(), "otp_jsonObject->" + jsonObject);
                String country_code = jsonObject.optString("country_code");
                String phoneno = jsonObject.optString("phoneno");
                int otpDuration = jsonObject.optInt("duration");

                Intent otpIntent = new Intent(mContext, OTPActivity.class);
                otpIntent.putExtra("user_phoneno",  phoneno);
                otpIntent.putExtra("country_code",  country_code);
                otpIntent.putExtra("otp_duration", otpDuration);
                otpIntent.putExtra("user_login_email", emailValue);
                otpIntent.putExtra("user_login_pass", passwordValue);
                otpIntent.putExtra("mBundle", bundle);
                startActivity(otpIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            } else {
                mAppConst.proceedToUserLogin(mContext, bundle, intentAction, intentType,
                        emailValue, passwordValue, jsonObject);
            }
        }
    }

    /**
     * Method to set the browse as a guest text view and choose language option text view.
     */
    private void setBrowseGuestAndLanguageOptions() {

        // Checking browse as a guest option.
        if(!PreferencesUtils.isGuestUserEnabled(this)){
            mBrowseAsGuest.setVisibility(View.GONE);
        } else {
            mBrowseAsGuest.setVisibility(View.VISIBLE);
        }

        // Checking multiple language option.
        String languages = PreferencesUtils.getLanguages(this);
        if (languages != null) {
            try {
                JSONObject languageObject = new JSONObject(languages);
                if (languageObject.length() > 1) {
                    mChooseLanguage.setVisibility(View.VISIBLE);
                } else {
                    mChooseLanguage.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mChooseLanguage.setVisibility(View.GONE);
        }
    }

    /**
     * Method to show login errors
     *
     * @param message Message which needs to show on email or password field.
     */
    public void displayError(String message) {
        if (GlobalFunctions.isValidJson(message)) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                String emailError = jsonObject.optString("email");
                String passError = jsonObject.optString("password");

                if (emailError != null && !emailError.isEmpty() && passError != null && !passError.isEmpty()) {
                    eEmailField.setError(emailError);
                    eEmailField.requestFocus();
                    ePasswordField.setError(passError);

                } else if (emailError != null && !emailError.isEmpty()) {
                    eEmailField.setError(emailError);
                    eEmailField.requestFocus();

                } else if (passError != null && !passError.isEmpty()) {
                    ePasswordField.setError(passError);
                    ePasswordField.requestFocus();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (message != null) {
                switch (message) {
                    case "email_not_verified":
                        message = mContext.getResources().getString(R.string.email_not_verified);
                        break;

                    case "not_approved":
                        message = mContext.getResources().getString(R.string.signup_admin_approval);
                        break;

                    case "subscription_fail":
                        mContext.getResources().getString(R.string.subscription_unsuccessful_message);
                        break;
                }
            }
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
            isError = true;
        }

        isValidatingData = false;
        loginButton.setText(getResources().getString(R.string.login_btn_name));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (!isValidatingData) {
                    if (GlobalFunctions.isNetworkAvailable(mContext)) {
                        if (isError) {
                            errorView.setVisibility(View.GONE);
                        }
                        mAppConst.hideKeyboard();
                        isValidatingData = true;

                        if (PreferencesUtils.getOtpEnabledOption(mContext) != null
                                && (PreferencesUtils.getOtpEnabledOption(mContext).equals("otp")
                                || PreferencesUtils.getOtpEnabledOption(mContext).equals("both"))) {
                            if (isNormalLogin)
                                LoginClicked();
                            else
                                otpLoginClicked();

                        } else {
                            loginButton.setText(getResources().getString(R.string.login_validation_msg) + "……");
                            LoginClicked();
                        }
                    } else {
                        SnackbarUtils.displaySnackbarLongTime(mMainView,
                                getResources().getString(R.string.network_connectivity_error));
                    }
                }
                break;

            case R.id.forgot_password:
                forgotPassword();
                break;

            case R.id.register_text:
                Intent signUpIntent = new Intent(NewLoginActivity.this, SubscriptionActivity.class);
                startActivity(signUpIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.browse_as_guest:

                mBrowseAsGuest.setText("\uf110");
                // Updating default language as current language.
                PreferencesUtils.updateDashBoardData(NewLoginActivity.this,
                        PreferencesUtils.CURRENT_LANGUAGE,
                        PreferencesUtils.getDefaultLanguage(NewLoginActivity.this));
                Intent intent = new Intent(NewLoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.show_password_icon:
                if (isPasswordShow) {
                    showPassword.setText("\uf06e");
                    ePasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ePasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setText("\uf070");
                }
                isPasswordShow = !isPasswordShow;

                // Set cursor to end of password
                if (ePasswordField.getText() != null && !ePasswordField.getText().toString().isEmpty()) {
                    ePasswordField.setSelection(ePasswordField.length());
                }

                break;

            case R.id.choose_language:
                mAppConst.changeLanguage(this, "home");
                break;
        }

    }

    //Forgot Password
    private void forgotPassword() {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setTitle(getResources().getString(R.string.forgot_password_popup_title));

        final LinearLayout inputLayout = new LinearLayout(mContext);
        final EditText input = new EditText(this);
        inputLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        inputLayout.setLayoutParams(layoutParams);

        inputLayout.setFocusable(true);
        inputLayout.setFocusableInTouchMode(true);
        int padding20 = (int) mContext.getResources().getDimension(R.dimen.padding_20dp);
        inputLayout.setPadding(padding20, padding20/2, padding20, padding20/2);

        if (PreferencesUtils.isOTPPluginEnabled(mContext)) {
            alertBuilder.setMessage(getResources().getString(R.string.forgot_password_popup_message_otp));
            input.setHint(getResources().getString(R.string.lbl_enter_email_phone));

        } else {
            alertBuilder.setMessage(getResources().getString(R.string.forgot_password_popup_message));
            input.setHint(getResources().getString(R.string.lbl_enter_email));
        }
        input.setFocusableInTouchMode(true);

        inputLayout.addView(input);
        alertBuilder.setView(inputLayout);

        alertBuilder.setPositiveButton(getResources().getString(R.string.forgot_password_popup_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mAppConst.hideKeyboardInDialog(input);
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        // used to prevent the dialog from closing when ok button is clicked (For email condition)
        Button alertDialogPositiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (PreferencesUtils.isOTPPluginEnabled(mContext)) {
            alertDialogPositiveButton.setText(getResources().getString(R.string.forgot_password_popup_button_otp));
        }
        alertDialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppConst.showProgressDialog();

                final String emailAddress = input.getText().toString();

                if (emailAddress.length() > 0 && !emailAddress.trim().isEmpty()) {
                    mAppConst.hideKeyboardInDialog(v);

                    if (PreferencesUtils.isOTPPluginEnabled(mContext)){

                        HashMap<String, String> params = new HashMap<>();
                        params.put("email", emailAddress);

                        mAppConst.showProgressDialog();
                        alertDialog.dismiss();
                        mAppConst.postJsonResponseForUrl(UrlUtil.FORGOT_PASSWORD_OTP_URL, params, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                mAppConst.hideProgressDialog();

                                if (jsonObject != null) {
                                    JSONObject response = jsonObject.optJSONObject("response");
                                    if (response.optInt("isEmail") == 1) {
                                        SnackbarUtils.displaySnackbarLongTime(mMainView,
                                                getResources().getString(R.string.forgot_password_success_message));

                                    } else {
                                        Intent otpIntent = new Intent(mContext, OTPActivity.class);
                                        otpIntent.putExtra("user_phoneno", response.optString("phoneno"));
                                        otpIntent.putExtra("country_code", response.optString("country_code"));
                                        otpIntent.putExtra("otp_duration", response.optInt("duration"));
                                        otpIntent.putExtra("isForgotPassword", true);
                                        startActivity(otpIntent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                }
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                mAppConst.hideProgressDialog();
                                SnackbarUtils.displaySnackbarLongTime(mMainView, message);
                            }
                        });

                        mAppConst.hideProgressDialog();

                    } else {
                        alertDialog.dismiss();
                        HashMap<String, String> emailParams = new HashMap<>();
                        emailParams.put("email", emailAddress);

                        mAppConst.postJsonResponseForUrl(UrlUtil.FORGOT_PASSWORD_URL, emailParams, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {
                                mAppConst.hideProgressDialog();
                                SnackbarUtils.displaySnackbarLongTime(mMainView,
                                        getResources().getString(R.string.forgot_password_success_message));
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                mAppConst.hideProgressDialog();
                                /* Show Message */
                                SnackbarUtils.displaySnackbarLongTime(mMainView, message);
                            }
                        });
                    }

                } else {
                    mAppConst.hideProgressDialog();
                        if (PreferencesUtils.isOTPPluginEnabled(mContext)){
                            input.setError(getResources().getString(R.string.forgot_password_empty_email_phone_error_message));
                        } else {
                            input.setError(getResources().getString(R.string.forgot_password_empty_email_error_message));
                        }
                }

            }
        });

    }

    @Override
    public void onSuccess(String loginType) {

    }

    @Override
    public void onError(String loginType, String errorMessage) {
        SocialLoginUtil.clearFbTwitterInstances(this, loginType);
        switch (errorMessage) {
            case "email_not_verified":
                SnackbarUtils.displaySnackbar(mMainView, getResources().getString(R.string.email_not_verified));
                break;
            case "not_approved":
                SnackbarUtils.displaySnackbar(mMainView, getResources().getString(R.string.signup_admin_approval));
                break;
            default:
                SnackbarUtils.displaySnackbar(mMainView, errorMessage);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
