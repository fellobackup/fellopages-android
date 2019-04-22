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

package com.fellopages.mobileapp.classes.common.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Invite extends FormActivity {

    private String inviteUrl,mToolbarTitle,mDefaultMessage;
    private AppConstant mAppConst;
    private Map<String, String> postParams;
    private JSONArray userIdsArray;
    private Toolbar mToolbar;
    private RelativeLayout inviteFormView;
    private SelectableTextView mNoFriendsMessageView;
    private JSONArray mResponseArray;
    private Context mContext;
    private String mCurrentSelectedModule, mSnackBarMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);

        //Setting up the action bar
        mToolbar = findViewById(R.id.toolbar);
        mContext = this;
        mNoFriendsMessageView = findViewById(R.id.noFriendsMessage);

        mCurrentSelectedModule = getIntent().getExtras().getString(ConstantVariables.EXTRA_MODULE_TYPE);
        if (mCurrentSelectedModule == null || mCurrentSelectedModule.isEmpty()) {
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        switch (mCurrentSelectedModule){
            case "core_main_group":
                mToolbarTitle = getResources().getString(R.string.title_activity_invite);
                mDefaultMessage = getResources().getString(R.string.invite_no_friend_message);
                mSnackBarMessage = getResources().getString(R.string.invitation_successful);
                break;
            case "core_main_event":
                mDefaultMessage = getResources().getString(R.string.invite_no_friend_message_event);
                mToolbarTitle = getResources().getString(R.string.title_activity_invite_event);
                mSnackBarMessage = getResources().getString(R.string.invitation_successful);
                break;
            case "core_main_sitegroup":
                mToolbarTitle = getResources().getString(R.string.join_group_dialogue_title);
                mSnackBarMessage = getResources().getString(R.string.join_group_dialogue_success_message);
                break;
        }

        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mToolbarTitle);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        inviteFormView = findViewById(R.id.form_view);
        mAppConst = new AppConstant(this);

        inviteUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);

        mAppConst.getJsonResponseFromUrl(inviteUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
               findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(jsonObject != null){
                    mResponseArray = jsonObject.getJSONArray("response");

                    if(mResponseArray != null) {
                        if (mResponseArray.length() == 0) {
                            mNoFriendsMessageView.setText(mDefaultMessage);
                            mNoFriendsMessageView.setVisibility(View.VISIBLE);
                        } else {
                            try {
                                // Get UserIds of members
                                if(!mCurrentSelectedModule.equals("core_main_sitegroup")){
                                    for (int i = 0; i < mResponseArray.length(); i++) {
                                        JSONObject fieldsJsonObject = mResponseArray.getJSONObject(i);
                                        String type = fieldsJsonObject.getString("type");
                                        if (type.equals("Multicheckbox")) {
                                            JSONObject multiOptions = fieldsJsonObject.getJSONObject("multiOptions");
                                            userIdsArray = multiOptions.names();
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            inviteFormView.addView(generateForm(jsonObject, true,
                                    PreferencesUtils.getCurrentSelectedModule(Invite.this)));
                            invalidateOptionsMenu();
                        }
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(inviteFormView, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
        menu.findItem(R.id.submit).setTitle(mContext.getResources().getString(R.string.contact_form_post_text)).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(Invite.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(Invite.this);
                }
                return true;
            case R.id.submit:
                sendInvites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sendInvite = menu.findItem(R.id.submit);
        if(mCurrentSelectedModule.equals("core_main_sitegroup")){
            sendInvite.setTitle(getResources().getString(R.string.join));
        }
        if(mResponseArray != null && mResponseArray.length() != 0)
            sendInvite.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    public void sendInvites() {

        postParams = new HashMap<>();
        postParams = save();

        String userIds = "";

        if (postParams != null) {

            String selectAllValue = null, users = null;

            /*
            IF SELECT ALL IS CHECKED THEN ADD ALL THE USER IDS IN THE USERS FIELDS.
             */
            if(!mCurrentSelectedModule.equals("core_main_sitegroup")){
                selectAllValue = postParams.get("selectall");
                users = postParams.get("users");
                if (selectAllValue.equals("1")) {
                    for (int j = 0; j < userIdsArray.length(); j++) {
                        if (j < userIdsArray.length() - 1)
                            userIds += userIdsArray.optString(j) + ",";
                        else
                            userIds += userIdsArray.optString(j);
                    }
                    postParams.put("users", userIds);
                }
            }

            if (selectAllValue != null && selectAllValue.equals("0") && users != null && users.equals("")) {
                SnackbarUtils.displaySnackbarShortWithListener(inviteFormView,
                        getResources().getString(R.string.invite_error_message),
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                            }
                        });
            }else{
                mAppConst.showProgressDialog();
                mAppConst.postJsonResponseForUrl(inviteUrl, postParams, new OnResponseListener() {

                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                         mAppConst.hideProgressDialog();
                    SnackbarUtils.displaySnackbarShortWithListener(inviteFormView, mSnackBarMessage,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    if(mCurrentSelectedModule.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)) {
                                        Intent intent = new Intent();
                                        setResult(ConstantVariables.ADV_GROUPS_INVITE_REQUEST, intent);
                                    }
                                    finish();
                                }
                            });
                    }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                    SnackbarUtils.displaySnackbarShortWithListener(inviteFormView, message,
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
