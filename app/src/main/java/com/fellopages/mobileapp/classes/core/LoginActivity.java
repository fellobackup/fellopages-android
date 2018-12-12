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
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.startscreens.HomeScreen;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout usernameWrapper, passwordWrapper;
    private Button loginButton;
    private String emailValue = "";
    private String passwordValue = "";
    private boolean isValidatingData = false, isError = false,
            isNormalLogin = false;
    private Context mContext;
    private AppConstant mAppConst;
    private Toolbar mToolbar;
    private TextView mForgotPassword;
    private Bundle bundle;
    private String intentAction, intentType;
    private TextView errorView;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setBackgroundDrawableResource(R.drawable.first);
        mContext = this;
        mAppConst = new AppConstant(this);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            intentAction = getIntent().getAction();
            intentType = getIntent().getType();
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

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setPadding(0, (int) getResources().getDimension(R.dimen.login_button_top_bottom_padding),
                0, (int) getResources().getDimension(R.dimen.login_button_top_bottom_padding));
        loginButton.setOnClickListener(this);
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        mForgotPassword.setOnClickListener(this);
        usernameWrapper.setHint(getResources().getString(R.string.lbl_enter_email));
        passwordWrapper.setHint(getResources().getString(R.string.lbl_enter_password));
        errorView = (TextView) findViewById(R.id.error_view);

        if( PreferencesUtils.getOtpEnabledOption(mContext) != null &&
                !PreferencesUtils.getOtpEnabledOption(mContext).isEmpty()) {
            if (PreferencesUtils.getOtpEnabledOption(mContext).equals("both")) {
                passwordWrapper.setVisibility(View.GONE);
                mForgotPassword.setVisibility(View.GONE);
                setViewHideShow();

            } else {
                passwordWrapper.setVisibility(View.VISIBLE);
                mForgotPassword.setVisibility(View.VISIBLE);
            }
            usernameWrapper.setHint(getResources().getString(R.string.lbl_enter_email_phone));
        }
        LogUtils.LOGD(LoginActivity.class.getSimpleName(), "OtpEnabledOption->" + PreferencesUtils.getOtpEnabledOption(mContext));
    }

    public void setViewHideShow() {
        usernameWrapper.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().matches("-?\\d+(.\\d+)?")) {
                    isNormalLogin = false;
                    mForgotPassword.setVisibility(View.GONE);
                    passwordWrapper.setVisibility(View.GONE);
                    loginButton.setText(getResources().getString(R.string.otp_login_btn_name));

                } else {
                    isNormalLogin = true;
                    loginButton.setText(getResources().getString(R.string.login_btn_name));
                    mForgotPassword.setVisibility(View.VISIBLE);
                    if (charSequence.length() != 0) {
                        passwordWrapper.setVisibility(View.VISIBLE);
                        mForgotPassword.setVisibility(View.VISIBLE);
                    } else {
                        passwordWrapper.setVisibility(View.GONE);
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

        emailValue = usernameWrapper.getEditText().getText().toString();

        passwordValue = passwordWrapper.getEditText().getText().toString();

        if (emailValue.isEmpty()) {
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));
            usernameWrapper.setError(getResources().getString(R.string.email_address_message));

        } else if (!passwordWrapper.isShown()) {
            passwordWrapper.setVisibility(View.VISIBLE);
            usernameWrapper.setErrorEnabled(false);
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));

        } else if (passwordValue.isEmpty()) {
            usernameWrapper.setErrorEnabled(false);
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));
            passwordWrapper.setError(getResources().getString(R.string.password_message));

        } else {
            mAppConst.showProgressDialog();
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);

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
        emailValue = usernameWrapper.getEditText().getText().toString();
        passwordValue = passwordWrapper.getEditText().getText().toString();

        if (emailValue.isEmpty()){
            isValidatingData = false;
            usernameWrapper.setError(getResources().getString(R.string.email_address_phoneno_msg));

        } else if (passwordValue.isEmpty() && PreferencesUtils.getOtpEnabledOption(mContext).equals("otp")) {
            usernameWrapper.setErrorEnabled(false);
            isValidatingData = false;
            loginButton.setText(getResources().getString(R.string.login_btn_name));
            passwordWrapper.setError(getResources().getString(R.string.password_message));

        } else {
            mAppConst.showProgressDialog();
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);

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
                    usernameWrapper.setError(emailError);
                    passwordWrapper.setError(passError);

                } else if (emailError != null && !emailError.isEmpty()) {
                    usernameWrapper.setError(emailError);

                } else if (passError != null && !passError.isEmpty()) {
                    passwordWrapper.setError(passError);
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
        Intent loginActivity = new Intent(LoginActivity.this, HomeScreen.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivity);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.login_main),
                                getResources().getString(R.string.network_connectivity_error));
                    }
                }
                break;

            case R.id.forgot_password:
                forgotPassword();
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
                mAppConst.hideKeyboard();
                mAppConst.showProgressDialog();

                final String emailAddress = input.getText().toString();

                if (emailAddress.length() > 0 && !emailAddress.trim().isEmpty()) {

                    if (PreferencesUtils.isOTPPluginEnabled(mContext)) {

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
                                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.login_main),
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
                                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.login_main), message);
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

                                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.login_main),
                                        getResources().getString(R.string.forgot_password_success_message));
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                mAppConst.hideProgressDialog();
                                /* Show Message */
                                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.login_main), message);
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
}
