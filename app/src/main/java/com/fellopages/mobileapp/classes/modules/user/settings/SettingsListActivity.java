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

package com.fellopages.mobileapp.classes.modules.user.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsListActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mGeneralNextIcon, mPrivacyNextIcon, mNetworksNextIcon, mNotificationsNextIcon;
    private TextView mPasswordNextIcon,mDeleteNextIcon, mSubscriptionNextIcon, mMobileInfoNextIcon;
    private TextView mGeneralSettingLabel, mPrivacySettingLabel, mNetworkSettingLabel, mSoundSettingLabel, mMobileInfoSettingLabel;
    private TextView mPasswordLabel, mDeleteLabel,mNotificationSettingLabel, mSubscriptionSettingLabel;
    private RelativeLayout mGeneralSettings, mPrivacySettings, mNotificationSettings, mSubscriptionSettings;
    private RelativeLayout mNetworksSettings, mPasswordSettings, mDeleteAccount, mSoundSetting, mMobileInfoSettings;
    private SwitchCompat mSoundEffectSwitch;
    private View mDeleteAccountBottomLine;
    private Typeface fontIcon;
    private Context mContext;
    private Toolbar mToolBar;
    AppConstant mAppConst;
    View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_list);

        fontIcon = GlobalFunctions.getFontIconTypeFace(this);
        mContext = this;

        mAppConst = new AppConstant(mContext);

        mToolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolBar);

        getUserSettingsFields();

        makeRequest();

    }

    private void getUserSettingsFields(){

        mRootView = findViewById(R.id.rootView);

        mGeneralNextIcon = findViewById(R.id.general_nextIcon);
        mPrivacyNextIcon = findViewById(R.id.privacy_nextIcon);
        mNetworksNextIcon = findViewById(R.id.networks_nextIcon);
        mNotificationsNextIcon = findViewById(R.id.notifications_nextIcon);
        mPasswordNextIcon = findViewById(R.id.password_nextIcon);
        mDeleteNextIcon = findViewById(R.id.delete_nextIcon);
        mSubscriptionNextIcon = findViewById(R.id.subscription_nextIcon);
        mMobileInfoNextIcon = findViewById(R.id.mobile_info_nextIcon);

        mGeneralSettingLabel = findViewById(R.id.general_setting_label);
        mPrivacySettingLabel = findViewById(R.id.privacy_setting_label);
        mNetworkSettingLabel = findViewById(R.id.network_setting_label);
        mNotificationSettingLabel = findViewById(R.id.notification_setting_label);
        mPasswordLabel = findViewById(R.id.password_setting_label);
        mDeleteLabel = findViewById(R.id.delete_account_label);
        mSoundSettingLabel = findViewById(R.id.sound_setting_label);
        mSubscriptionSettingLabel = findViewById(R.id.subscription_label);
        mMobileInfoSettingLabel = findViewById(R.id.mobile_info_setting_label);

        mGeneralSettings = findViewById(R.id.settings_general);
        mPrivacySettings = findViewById(R.id.settings_privacy);
        mNotificationSettings = findViewById(R.id.settings_notifications);
        mNetworksSettings = findViewById(R.id.settings_networks);
        mPasswordSettings = findViewById(R.id.settings_password);
        mDeleteAccount = findViewById(R.id.settings_delete_account);
        mSoundSetting = findViewById(R.id.sound_settings);
        mSubscriptionSettings = findViewById(R.id.settings_subscription);
        mSoundEffectSwitch = findViewById(R.id.sound_setting_switch);
        mSoundEffectSwitch.setChecked(PreferencesUtils.isSoundEffectEnabled(mContext));
        mDeleteAccountBottomLine = findViewById(R.id.delete_accound_bottom_line);
        mMobileInfoSettings = findViewById(R.id.settings_mobile_info);

        mGeneralSettings.setOnClickListener(this);
        mPrivacySettings.setOnClickListener(this);
        mNotificationSettings.setOnClickListener(this);
        mNetworksSettings.setOnClickListener(this);
        mPasswordSettings.setOnClickListener(this);
        mDeleteAccount.setOnClickListener(this);
        mSubscriptionSettings.setOnClickListener(this);
        mMobileInfoSettings.setOnClickListener(this);


        mGeneralNextIcon.setTypeface(fontIcon);
        mPrivacyNextIcon.setTypeface(fontIcon);
        mNetworksNextIcon.setTypeface(fontIcon);
        mNotificationsNextIcon.setTypeface(fontIcon);
        mPasswordNextIcon.setTypeface(fontIcon);
        mDeleteNextIcon.setTypeface(fontIcon);
        mSubscriptionNextIcon.setTypeface(fontIcon);
        mMobileInfoNextIcon.setTypeface(fontIcon);

        mGeneralNextIcon.setText("\uf054");
        mPrivacyNextIcon.setText("\uf054");
        mNetworksNextIcon.setText("\uf054");
        mNotificationsNextIcon.setText("\uf054");
        mPasswordNextIcon.setText("\uf054");
        mDeleteNextIcon.setText("\uf054");
        mSubscriptionNextIcon.setText("\uf054");
        mMobileInfoNextIcon.setText("\uf054");
    }

    private void makeRequest(){

        mAppConst.getJsonResponseFromUrl(UrlUtil.ACCOUNT_SETTINGS, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(jsonObject != null){
                    JSONArray responseArray = jsonObject.optJSONArray("response");
                    if(responseArray != null && responseArray.length() != 0){
                        for(int i = 0; i < responseArray.length(); i++){
                            JSONObject settingObject = responseArray.optJSONObject(i);
                            String setting_name = settingObject.optString("name");
                            String settingLabel = settingObject.optString("label").trim();
                            switch(setting_name){

                                case "general":
                                    mGeneralSettings.setVisibility(View.VISIBLE);
                                    mGeneralSettingLabel.setText(settingLabel);
                                    break;

                                case "privacy":
                                    mPrivacySettings.setVisibility(View.VISIBLE);
                                    mPrivacySettingLabel.setText(settingLabel);
                                    break;

                                case "network":
                                    mNetworksSettings.setVisibility(View.VISIBLE);
                                    mNetworkSettingLabel.setText(settingLabel);
                                    break;

                                case "notification":
                                    mNotificationSettings.setVisibility(View.VISIBLE);
                                    mNotificationSettingLabel.setText(settingLabel);
                                    break;

                                case "password":
                                    mPasswordSettings.setVisibility(View.VISIBLE);
                                    mPasswordLabel.setText(settingLabel);
                                    break;

                                case "delete":
                                    mDeleteAccount.setVisibility(View.VISIBLE);
                                    mDeleteLabel.setText(settingLabel);
                                    break;

                                case "sound":
                                    mSoundSetting.setVisibility(View.VISIBLE);
                                    findViewById(R.id.mobileInfo_bottom_line).setVisibility(View.VISIBLE);
                                    mSoundSettingLabel.setText(settingLabel);
                                    mSoundEffectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            PreferencesUtils.setSoundEffectValue(mContext, isChecked);

                                        }
                                    });
                                    break;

                                case "subscription":
                                    mSubscriptionSettings.setVisibility(View.VISIBLE);
                                    mSubscriptionSettingLabel.setText(settingLabel);
                                    mDeleteAccountBottomLine.setVisibility(View.VISIBLE);
                                    break;

                                case "mobileinfo":
                                    mMobileInfoSettings.setVisibility(View.VISIBLE);
                                    findViewById(R.id.subscription_bottom_line).setVisibility(View.VISIBLE);
                                    mMobileInfoSettingLabel.setText(settingLabel);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (isRetryOption) {
                    SnackbarUtils.displaySnackbarWithAction(mContext, mRootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(mRootView, message);
                }

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        Intent settingsIntent = null;
        String url = null;
        if (id != R.id.settings_mobile_info) {
            settingsIntent = new Intent(mContext, MemberSettingsActivity.class);
        }
        switch (id){

            case R.id.settings_general:
                url = AppConstant.DEFAULT_URL + "members/settings/general?facebookTwitterIntegrate=1";
                settingsIntent.putExtra("selected_option", "settings_general");
                settingsIntent.putExtra("title", getResources().getString(R.string.general_settings));
                break;

            case R.id.settings_privacy:
                url = AppConstant.DEFAULT_URL + "members/settings/privacy?getBlockedUsers=1";
                settingsIntent.putExtra("selected_option", "settings_privacy");
                settingsIntent.putExtra("title", getResources().getString(R.string.privacy_settings));
                break;

            case R.id.settings_notifications:
                url = AppConstant.DEFAULT_URL + "members/settings/notifications";
                settingsIntent.putExtra("selected_option", "settings_notifications");
                settingsIntent.putExtra("title", getResources().getString(R.string.notification_settings));
                break;

            case R.id.settings_networks:
                url = AppConstant.DEFAULT_URL + "members/settings/network";
                settingsIntent.putExtra("selected_option", "settings_networks");
                settingsIntent.putExtra("title", getResources().getString(R.string.network_settings));
                break;

            case R.id.settings_password:
                url = AppConstant.DEFAULT_URL + "members/settings/password";
                settingsIntent.putExtra("selected_option", "settings_password");
                settingsIntent.putExtra("title", getResources().getString(R.string.change_password_settings));
                break;

            case R.id.settings_delete_account:
                url = AppConstant.DEFAULT_URL + "members/settings/delete";
                settingsIntent.putExtra("selected_option", "settings_delete_account");
                settingsIntent.putExtra("title", getResources().getString(R.string.delete_account_settings));
                break;

            case R.id.settings_subscription:
                url = AppConstant.DEFAULT_URL + "/members/settings/subscriptions";
                settingsIntent.putExtra("selected_option", "settings_subscription");
                settingsIntent.putExtra("title", getResources().getString(R.string.subscription_settings));
                break;

            case R.id.settings_mobile_info:
                settingsIntent = new Intent(mContext, MobileInfoSetting.class);
                settingsIntent.putExtra("title",getResources().getString(R.string.mobile_info_settings));
                break;

        }
        settingsIntent.putExtra("url", url);
        startActivity(settingsIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
