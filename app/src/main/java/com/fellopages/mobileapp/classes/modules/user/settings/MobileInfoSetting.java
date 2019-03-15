package com.fellopages.mobileapp.classes.modules.user.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.OTPActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MobileInfoSetting extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private Context mContext;
    private AppConstant mAppConst;
    private String actionBarTitle;
    private LinearLayout mobileInfoLayoutContainer, enablePhonenoLayout, addPhonenoLayout;
    private EditText userPhoneno, countryCode, phoneno;
    private TextView tvPhoneno, twoFactorTitle;
    private Switch statusSwitch;
    private Button btnVerifyInfo, btnEditPhoneno, btnDeletePhoneno;
    private AlertDialogWithAction alertDialogWithAction;
    private boolean isEditPhoneno = false, isFormVisible = false;
    private String userCountryCode, userPhoneNumber;
    private int enableVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_info_setting);

        mContext = this;
        mAppConst = new AppConstant(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mAppConst.showProgressDialog();
        alertDialogWithAction = new AlertDialogWithAction(mContext);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null){
            actionBarTitle = getIntent().getStringExtra("title");
        }

        if(actionBarTitle != null && getSupportActionBar()!= null){
            getSupportActionBar().setTitle(actionBarTitle);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
            }
        });

        mobileInfoLayoutContainer = findViewById(R.id.mobile_info_layout_container);
        enablePhonenoLayout = findViewById(R.id.enable_phoneno_layout);
        addPhonenoLayout = findViewById(R.id.add_phoneno_layout);

        userPhoneno = findViewById(R.id.user_phone_no);
        countryCode = findViewById(R.id.country_code);
        phoneno = findViewById(R.id.phoneno);
        tvPhoneno = findViewById(R.id.tv_phoneno);
        twoFactorTitle = findViewById(R.id.two_factor_title);
        statusSwitch = findViewById(R.id.status_switch);
        btnVerifyInfo = findViewById(R.id.btn_verify_info);
        btnEditPhoneno = findViewById(R.id.btn_edit_phoneno);
        btnDeletePhoneno = findViewById(R.id.btn_delete_phoneno);
        btnVerifyInfo.setOnClickListener(this);
        btnEditPhoneno.setOnClickListener(this);
        btnDeletePhoneno.setOnClickListener(this);
        statusSwitch.setOnClickListener(this);

        Drawable icLockIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_lock_outline);
        icLockIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.black), PorterDuff.Mode.SRC_IN);
        twoFactorTitle.setCompoundDrawablesWithIntrinsicBounds(icLockIcon, null, null, null);
        int drawablePadding = mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp);
        twoFactorTitle.setCompoundDrawablePadding(drawablePadding);

        Drawable icPhone = ContextCompat.getDrawable(mContext, R.drawable.ic_phone_android);
        icPhone.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor), PorterDuff.Mode.SRC_IN);
        userPhoneno.setCompoundDrawablesWithIntrinsicBounds(icPhone, null, null, null);
        userPhoneno.setCompoundDrawablePadding(drawablePadding);

        makeRequest();

    }

    private void makeRequest() {

        mAppConst.getJsonResponseFromUrl(UrlUtil.TWO_FACTOR_GET_VERIFICATION_URL, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mAppConst.hideProgressDialog();

                if (jsonObject != null) {

                    if (jsonObject.has("form")) {

                        isFormVisible = true;
                        mobileInfoLayoutContainer.setVisibility(View.VISIBLE);
                        enablePhonenoLayout.setVisibility(View.GONE);
                        addPhonenoLayout.setVisibility(View.VISIBLE);
                        btnVerifyInfo.setVisibility(View.VISIBLE);
                        countryCode.setText(null);
                        phoneno.setText(null);
                        countryCode.setHint(getResources().getString(R.string.two_factor_country_code_hint));
                        phoneno.setHint(getResources().getString(R.string.two_factor_phoneno_hint));

                    } else if (jsonObject.has("response")){

                        JSONObject resultObject = jsonObject.optJSONObject("response");
                        String userPhone = resultObject.optString("phoneno");
                        String uCountryCode = resultObject.optString("country_code");
                        int enableTwoFactor = resultObject.optInt("enable_verification");

                        if (uCountryCode != null && !uCountryCode.isEmpty() &&
                                userPhone != null && !userPhone.isEmpty()) {
                            userPhoneno.setText("+" + uCountryCode + " " + userPhone);
                            tvPhoneno.setText("+" + uCountryCode + " " + userPhone);
                            countryCode.setText(uCountryCode);
                            phoneno.setText(userPhone);
                        }

                        if (userPhoneno != null) {
                            userPhoneno.setEnabled(false);
                        }

                        if (enableTwoFactor == 1) {
                            statusSwitch.setChecked(true);
                        }

                        mobileInfoLayoutContainer.setVisibility(View.VISIBLE);
                        enablePhonenoLayout.setVisibility(View.VISIBLE);
                        addPhonenoLayout.setVisibility(View.GONE);
                        btnVerifyInfo.setVisibility(View.GONE);
                        LogUtils.LOGD(MobileInfoSetting.class.getSimpleName(), "uCountryCode-userPhone-enableTwoFactor-> "
                                + uCountryCode + "-" + userPhone + "-" + enableTwoFactor);
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.mobile_info_layout_container), message);
            }
        });
    }

    public void verifyClicked(){

        userCountryCode = countryCode.getText().toString();
        userPhoneNumber = phoneno.getText().toString();
        String url =null;

        if (userCountryCode.isEmpty()){
            countryCode.setError(getResources().getString(R.string.two_factor_country_code_msg));

        } else if (userPhoneNumber.isEmpty()){
            phoneno.setError(getResources().getString(R.string.two_factor_phoneno_msg));

        } else {
            mAppConst.showProgressDialog();
            countryCode.setError(null);
            phoneno.setError(null);

            Map<String, String> params = new HashMap<>();
            params.put("country_code", userCountryCode);
            params.put("mobileno", userPhoneNumber);

            if (isEditPhoneno){
                url = UrlUtil.TWO_FACTOR_EDIT_MOBILE_URL;
            } else {
                url = UrlUtil.TWO_FACTOR_ADD_MOBILE_URL;
            }

            mAppConst.postJsonResponseForUrl(url, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();
                    LogUtils.LOGD(MobileInfoSetting.class.getSimpleName(), "jsonObject-> " + jsonObject);

                    if (jsonObject != null) {
                        JSONObject resultJson = jsonObject.optJSONObject("response");
                        String country_code = resultJson.optString("country_code");
                        String phoneno = resultJson.optString("phoneno");
                        int otpDuration = resultJson.optInt("duration");

                        Intent otpIntent = new Intent(mContext, OTPActivity.class);
                        otpIntent.putExtra("user_phoneno", phoneno);
                        otpIntent.putExtra("country_code", country_code);
                        otpIntent.putExtra("otp_duration", otpDuration);
                        otpIntent.putExtra("isEnableTwoFactor", true);
                        otpIntent.putExtra("isEditPhoneno", isEditPhoneno);
                        startActivityForResult(otpIntent, ConstantVariables.TWO_FACTOR_ACTIVITY_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.mobile_info_layout_container), message);
                }
            });
        }
    }


    @Override
    public void onBackPressed() {

        if (!(enablePhonenoLayout.getVisibility() == View.VISIBLE) && !isFormVisible) {
            enablePhonenoLayout.setVisibility(View.VISIBLE);
            addPhonenoLayout.setVisibility(View.GONE);
            btnVerifyInfo.setVisibility(View.GONE);

        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_edit_phoneno:
                enablePhonenoLayout.setVisibility(View.GONE);
                addPhonenoLayout.setVisibility(View.VISIBLE);
                btnVerifyInfo.setVisibility(View.VISIBLE);
                isEditPhoneno = true;
                break;

            case R.id.btn_delete_phoneno:
                showAlertPopup();
                break;

            case R.id.status_switch:
                if (statusSwitch.isChecked()){
                    enableVerification = 1;
                } else {
                    enableVerification = 0;
                }
                enableDisableTwoFactor(enableVerification);
                break;

            case R.id.btn_verify_info:
                if (GlobalFunctions.isNetworkAvailable(mContext)) {
                    mAppConst.hideKeyboard();
                    verifyClicked();
                } else {
                    SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.mobile_info_layout_container),
                            getResources().getString(R.string.network_connectivity_error));
                }
                break;
        }
    }

    public void showAlertPopup(){
        alertDialogWithAction.showAlertDialogWithAction(mContext.getResources().getString(R.string.two_factor_delete_popup_title),
                mContext.getResources().getString(R.string.two_factor_delete_popup_msg),
                mContext.getResources().getString(R.string.two_factor_popup_delete_btn),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAppConst.showProgressDialog();

                        mAppConst.deleteResponseForUrl(UrlUtil.TWO_FACTOR_DELETE_MOBILE_URL, null, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {

                                if (jsonObject != null) {
                                    makeRequest();
                                }
                                mAppConst.hideProgressDialog();
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.mobile_info_layout_container), message);
                                mAppConst.hideProgressDialog();
                            }
                        });
                    }
                });
    }

    public void enableDisableTwoFactor(int enableVerification){

        mAppConst.showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put("enable_verification", String.valueOf(enableVerification));

        final int finalSuccessMsg = enableVerification;
        mAppConst.postJsonResponseForUrl(UrlUtil.TWO_FACTOR_ENABLE_DISABLE_URL, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mAppConst.hideProgressDialog();
                if (jsonObject != null) {
                    if (finalSuccessMsg == 1) {
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.mobile_info_layout_container),
                                getResources().getString(R.string.two_factor_verification_enabled_msg));

                    } else if (finalSuccessMsg == 0) {
                        SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.mobile_info_layout_container),
                                getResources().getString(R.string.two_factor_verification_disabled_msg));
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.mobile_info_layout_container), message);
                mAppConst.hideProgressDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantVariables.TWO_FACTOR_ACTIVITY_CODE &&
                resultCode == ConstantVariables.TWO_FACTOR_VIEW_PAGE){
            mAppConst.showProgressDialog();
            makeRequest();
        }
    }
}
