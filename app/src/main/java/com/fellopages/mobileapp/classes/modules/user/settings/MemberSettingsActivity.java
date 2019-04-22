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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.formgenerator.FormTextView;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseUpdateListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MemberSettingsActivity extends FormActivity implements
        SocialLoginUtil.OnSocialLoginSuccessListener, OnResponseUpdateListener {

    private Toolbar mToolBar;
    private String url, selectedSettingOption, actionBarTitle, emailValue, code;
    private RelativeLayout formContainer;
    private ProgressBar mProgressBar;
    private AppConstant mAppConst;
    private Context mContext;
    private Map<String, String> postParams;
    private boolean isError = false;
    public JSONObject mNetworkObject;
    private RelativeLayout mNetworkView;
    private ViewPager viewPager;
    public TabLayout mTabLayout;
    private static int currentNetwork = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);

        mContext = this;
        mAppConst = new AppConstant(this);

        mToolBar = findViewById(R.id.toolbar);
        formContainer = findViewById(R.id.form_view);
        mProgressBar = findViewById(R.id.progressBar);
        setSupportActionBar(mToolBar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolBar);

        final Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            url = bundle.getString("url");
            selectedSettingOption = bundle.getString("selected_option");
            actionBarTitle = bundle.getString("title");
            emailValue = bundle.getString("emailValue");
            code = bundle.getString("code");
        }

        if(actionBarTitle != null && getSupportActionBar()!= null)
            getSupportActionBar().setTitle(actionBarTitle);

        if(selectedSettingOption.equals("settings_networks")){
            initializeNetworkView();
        } else if(selectedSettingOption.equals("settings_delete_account")){
            mProgressBar.setVisibility(View.GONE);
            Fragment networksFragment = new DeleteAccountFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Bundle urlBundle = new Bundle();
            urlBundle.putString("url", url);
            networksFragment.setArguments(bundle);
            ft.replace(R.id.form_view, networksFragment);
            ft.commit();
        }

        makeRequest();
    }

    private void makeRequest() {
        if(url != null && !selectedSettingOption.equals("settings_delete_account")){

            if (selectedSettingOption.equals("reset_password")){
                url += "&email=" + emailValue + "&code=" + code;
            }
            // Code to Send Request for Create Form
            mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProgressBar.setVisibility(View.GONE);

                    if (selectedSettingOption.equals("settings_networks")) {
                        mNetworkObject = jsonObject;
                        setNetworkView();

                    } else if(selectedSettingOption.equals("settings_password") ||
                            selectedSettingOption.equals("settings_subscription") ||
                            selectedSettingOption.equals("reset_password"))
                        formContainer.addView(generateForm(jsonObject, true, selectedSettingOption));
                    else {
                        if (selectedSettingOption.equals("settings_general")) {
                            SocialLoginUtil.setSocialLoginListener(MemberSettingsActivity.this);
                        }
                        formContainer.addView(populate(jsonObject, selectedSettingOption));
                    }

                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(formContainer, message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    finish();
                                }
                            });
                }
            });

        }
    }

    private void initializeNetworkView() {
        mNetworkView = findViewById(R.id.network_view_container);
        viewPager = findViewById(R.id.viewpager);
        mTabLayout= findViewById(R.id.tabs);
        mNetworkView.setVisibility(View.VISIBLE);

    }

    private void setNetworkView() {
        formContainer.setVisibility(View.GONE);
        MyNetworkAdapter networkAdapter = new MyNetworkAdapter(getSupportFragmentManager());
        viewPager.setAdapter(networkAdapter);
        viewPager.setOffscreenPageLimit(2);
        mTabLayout.setTabTextColors(ContextCompat.getColor(mContext, R.color.grey),
                ContextCompat.getColor(mContext, R.color.themeButtonColor));
        mTabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(currentNetwork);

        setupTabIcons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
        menu.findItem(R.id.submit).setTitle(mContext.getResources().getString(R.string.save_menu_title));
        if (selectedSettingOption.equals("settings_subscription")) {
            menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.subscription_upgrade_title));

        } else if (selectedSettingOption.equals("reset_password")){
            menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.reset_password_submit_button));
        }
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
                putFormValues();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.submit);

        if(selectedSettingOption.equals("settings_networks") || selectedSettingOption.equals("settings_delete_account")){
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAppConst.hideKeyboard();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void putFormValues() {
        isError = false;
        postParams = save();

        if (selectedSettingOption.equals("settings_privacy") && postParams != null &&
                postParams.containsKey("publishTypes")) {
            ArrayList<String> ar = new ArrayList<>();
            String publishTypesOptions = postParams.get("publishTypes");
            String[] publishTypeArray = publishTypesOptions.split(",");
            if (publishTypeArray.length != 0) {
                for (String aPublishTypeArray : publishTypeArray) {
                    if (aPublishTypeArray != null && !aPublishTypeArray.isEmpty())
                        ar.add("\"" + aPublishTypeArray + "\"");
                }
            }
            postParams.put("publishTypes", ar.toString());
        }

        if (selectedSettingOption.equals("settings_password") || selectedSettingOption.equals("settings_privacy") ||
                selectedSettingOption.equals("reset_password")) {
            mProgressBar.bringToFront();
            mProgressBar.setVisibility(View.VISIBLE);
            mAppConst.postJsonResponseForUrl(url, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProgressBar.setVisibility(View.GONE);
                    if (!isError) {
                        switch (selectedSettingOption) {
                            case "settings_password":
                                Toast.makeText(mContext, getResources().getString(R.string.password_change_success_message),
                                        Toast.LENGTH_SHORT).show();
                                break;

                            case "reset_password":
                                Toast.makeText(mContext, getResources().getString(R.string.reset_password_success_msg),
                                        Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                Toast.makeText(mContext, getResources().getString(R.string.changes_saved),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                            if (!isFinishing()) {
                                finish();
                                if (!selectedSettingOption.equals("reset_password"))
                                    startActivity(getIntent());
                            }
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    isError = true;
                    mProgressBar.setVisibility(View.GONE);
                    if (selectedSettingOption.equals("settings_password")) {
                        if (!isFinishing() && message != null && !message.isEmpty()) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                            alertBuilder.setMessage(message);
                            alertBuilder.setTitle(getResources().getString(R.string.change_pass_alert_dialogue_title));
                            alertBuilder.setPositiveButton(getResources().getString(R.string.change_pass_alert_dialogue_button),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            alertBuilder.create().show();
                        }
                    } else {
                        try {
                            JSONObject errorMessagesObject = new JSONObject(message);
                            isError = showValidations(errorMessagesObject);
                            mProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            if (selectedSettingOption.equals("reset_password"))
                                SnackbarUtils.displaySnackbar(findViewById(R.id.form_view), message);
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else if (selectedSettingOption.equals("settings_subscription")) {

            String getUrl = mAppConst.buildQueryString(url, postParams);

            mAppConst.showProgressDialog();

            mAppConst.getJsonResponseFromUrl(getUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(final JSONObject jsonObject) {

                    mAppConst.hideProgressDialog();

                    if (!isFinishing() && jsonObject.optString("webViewRedirectURL") != null
                            && !jsonObject.optString("webViewRedirectURL").isEmpty()) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

                        alertBuilder.setMessage(jsonObject.optString("selected_plan"));
                        alertBuilder.setTitle(getResources().getString(R.string.subscribe_dialogue_title));

                        alertBuilder.setPositiveButton(getResources().getString(R.string.subscribe_dialouge_continue_button), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent webViewIntent = new Intent(mContext, WebViewActivity.class);
                                webViewIntent.putExtra("url", jsonObject.optString("webViewRedirectURL"));
                                webViewIntent.putExtra("isChangeSubscriptionPlan", true);
                                startActivityForResult(webViewIntent, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);

                            }
                        });

                        alertBuilder.setNegativeButton(getResources().getString(R.string.cancel_dialogue_message), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertBuilder.create().show();
                    } else {
                        SnackbarUtils.displaySnackbarLongWithListener(findViewById(R.id.form_view),
                                getResources().getString(R.string.subscription_change_success_message),
                                new SnackbarUtils.OnSnackbarDismissListener() {
                                    @Override
                                    public void onSnackbarDismissed() {
                                        finish();
                                    }
                                });

                    }

                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                }

            });
        } else if (postParams != null) {

            url = mAppConst.buildQueryString(url, postParams);
            mProgressBar.bringToFront();
            mProgressBar.setVisibility(View.VISIBLE);
            mAppConst.putResponseForUrl(url, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProgressBar.setVisibility(View.GONE);
                    if (!isError) {
                        Toast.makeText(mContext, getResources().getString(R.string.changes_saved),
                                Toast.LENGTH_SHORT).show();

                        if (!isFinishing()) {
                            finish();
                            startActivity(getIntent());
                        }
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    try {
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject errorMessagesObject = new JSONObject(message);
                        isError = showValidations(errorMessagesObject);
                    } catch (JSONException e) {
                        SnackbarUtils.displaySnackbar(findViewById(R.id.form_view), message);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        if (selectedSettingOption.equals("settings_general") && resultCode == RESULT_OK) {
            if (FormTextView.callbackManager != null) {
                FormTextView.callbackManager.onActivityResult(requestCode, resultCode, data);
            }
            if (FormTextView.twitterLoginButton != null) {
                FormTextView.twitterLoginButton.onActivityResult(requestCode, resultCode, data);
            }
        }
        String message = null;

        if (resultCode == ConstantVariables.PAYMENT_FAILED_ACTIVITY_CODE) {
            message = getResources().getString(R.string.subscription_change_failed_message);
        } else if (requestCode == ConstantVariables.WEB_VIEW_ACTIVITY_CODE) {
            message = getResources().getString(R.string.subscription_change_success_message);
        }

        if (message != null) {
            SnackbarUtils.displaySnackbarLongWithListener(findViewById(R.id.form_view), message,
                    new SnackbarUtils.OnSnackbarDismissListener() {
                        @Override
                        public void onSnackbarDismissed() {
                            finish();
                        }
                    });
        }
    }

    @Override
    public void onSuccess(String loginType) {
        SocialLoginUtil.clearFbTwitterInstances(mContext, loginType);
        if (!isFinishing()) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onError(String loginType, String errorMessage) {
        SocialLoginUtil.clearFbTwitterInstances(mContext, loginType);
        SnackbarUtils.displaySnackbar(findViewById(R.id.form_view), errorMessage);
    }

    @Override
    public void onResponseUpdate(JSONObject jsonObject) {
        mNetworkObject = jsonObject;
        currentNetwork = viewPager.getCurrentItem();
        setNetworkView();
    }

    /***
     * View Pager Adapter to set Tabs
     */
    class MyNetworkAdapter extends FragmentStatePagerAdapter {

        public MyNetworkAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String name;
            JSONArray networkArray;

            if (position == 0) {
                networkArray = mNetworkObject.optJSONArray("joinedNetworks");
                name = "joined";
            } else {
                networkArray = mNetworkObject.optJSONArray("availableNetworks");
                name= "available";
            }

            Bundle bundle = new Bundle();
            bundle.putString(ConstantVariables.NETWORK_NAME, name);
            bundle.putString(ConstantVariables.NETWORK_RESPONSE, networkArray.toString());
            bundle.putString(ConstantVariables.URL_STRING, url);
            Fragment returnFragment = new NetworkFragment();
            returnFragment.setArguments(bundle);
            return returnFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void setupTabIcons(){

        if(mNetworkObject != null){
            int myNetworkCount = 0, availableNetworkCount = 0;
            if (mNetworkObject.optJSONArray("joinedNetworks") != null ) {
                myNetworkCount = mNetworkObject.optJSONArray("joinedNetworks").length();
            }

            if (mNetworkObject.optJSONArray("availableNetworks") != null ) {
                availableNetworkCount = mNetworkObject.optJSONArray("availableNetworks").length();
            }

            mTabLayout.getTabAt(0).setText(mContext.getResources().getString(R.string.my_networks_text) + " (" + myNetworkCount + ")");
            mTabLayout.getTabAt(1).setText(mContext.getResources().getString(R.string.available_networks_text) + " (" + availableNetworkCount + ")");
        }
    }
}
