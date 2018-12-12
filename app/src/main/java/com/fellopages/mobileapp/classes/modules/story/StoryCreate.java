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

package com.fellopages.mobileapp.classes.modules.story;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.DrawableClickListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status.NETWORK_LIST_ARRAY;
import static com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status.USER_LIST_ARRAY;


public class StoryCreate extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    //Member variables.
    private Context mContext;
    private LinearLayout llStory, llPost;
    private AppCompatCheckBox cbStory, cbPost;
    private TextView tvStoryTitle, tvPostTitle, tvStoryDesc, tvPostDesc;
    private BezelImageView ivUser;
    private ProgressBar pbLoading;
    private ArrayList<String> mSelectPath, mStoryCaption;
    private String mStoryPrivacy = "everyone", mPostPrivacy = "everyone", mVideoPath, mVideoThumb;
    private JSONObject mFeedPostMenus, mUserPrivacyObject, userDetails, mStoryPrivacyObject;
    private Map<String, String> mMultiSelectUserPrivacy;
    private ArrayList<String> popupMenuList = new ArrayList<>();
    private AppConstant mAppConst;
    private ImageLoader mImageLoader;
    private int mDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_create);

          /* Create Back Button On Action Bar **/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CustomViews.createMarqueeTitle(this, toolbar);

        //getting views.
        getViews();

        // Getting references of member variables.
        mContext = StoryCreate.this;
        mAppConst = new AppConstant(mContext);
        mMultiSelectUserPrivacy = new HashMap<>();
        mImageLoader = new ImageLoader(mContext);

        //Getting Intent values.
        mVideoPath = getIntent().getStringExtra(MultiMediaSelectorActivity.VIDEO_RESULT);
        mVideoThumb = getIntent().getStringExtra(ConstantVariables.STORY_VIDEO_THUMB);
        mDuration = getIntent().getIntExtra(ConstantVariables.STORY_DURATION, 0);
        mSelectPath = getIntent().getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
        mPostPrivacy = PreferencesUtils.getStatusPrivacyKey(mContext);
        mStoryPrivacy = PreferencesUtils.getStoryPrivacyKey(mContext);
        mStoryCaption = getIntent().getStringArrayListExtra(ConstantVariables.STORY_DESCRIPTION);

        try {
            userDetails = new JSONObject(PreferencesUtils.getUserDetail(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (PreferencesUtils.getStatusPostPrivacyOptions(mContext) != null
                && PreferencesUtils.getStoryPrivacy(mContext) != null) {
            setPrivacy();
        } else {
            pbLoading.setVisibility(View.VISIBLE);
            showHideViews(false);
            getStoryAndPostPrivacy();
        }

    }

    /**
     * Method to get all views.
     */
    private void getViews() {
        tvStoryTitle = (TextView) findViewById(R.id.story_title);
        tvPostTitle = (TextView) findViewById(R.id.post_title);
        tvStoryDesc = (TextView) findViewById(R.id.story_desc);
        tvPostDesc = (TextView) findViewById(R.id.post_desc);
        ivUser = (BezelImageView) findViewById(R.id.owner_image);
        llStory = (LinearLayout) findViewById(R.id.story_layout);
        llPost = (LinearLayout) findViewById(R.id.post_layout);
        cbStory = (AppCompatCheckBox) findViewById(R.id.select_story);
        cbPost = (AppCompatCheckBox) findViewById(R.id.select_post);
        pbLoading = (ProgressBar) findViewById(R.id.loadingProgress);
        llStory.setOnClickListener(this);
        llPost.setOnClickListener(this);
        cbStory.setOnCheckedChangeListener(this);
        cbPost.setOnCheckedChangeListener(this);
    }

    /**
     * Method to show/hide views when the data is loaded.
     *
     * @param isNeedToShow True if need to show views.
     */
    private void showHideViews(boolean isNeedToShow) {
        ivUser.setVisibility(isNeedToShow ? View.VISIBLE : View.GONE);
        llStory.setVisibility(isNeedToShow ? View.VISIBLE : View.GONE);
        cbStory.setVisibility(isNeedToShow ? View.VISIBLE : View.GONE);
        findViewById(R.id.post_image).setVisibility(isNeedToShow ? View.VISIBLE : View.GONE);
        llPost.setVisibility(isNeedToShow ? View.VISIBLE : View.GONE);
        cbPost.setVisibility(isNeedToShow ? View.VISIBLE : View.GONE);
    }

    /**
     * Method to set privacy.
     */
    private void setPrivacy() {
        try {
            mFeedPostMenus = new JSONObject(PreferencesUtils.getStatusPostPrivacyOptions(mContext));
            mStoryPrivacyObject = new JSONObject(PreferencesUtils.getStoryPrivacy(mContext));
            mUserPrivacyObject = mFeedPostMenus.optJSONObject("userprivacy");
            USER_LIST_ARRAY = mFeedPostMenus.optJSONArray("userlist");
            NETWORK_LIST_ARRAY = mFeedPostMenus.optJSONArray("multiple_networklist");


            // When the user selected custom network or friend list then putting the all options into map.
            if ((mPostPrivacy.equals("network_list_custom")
                    || mPostPrivacy.equals("friend_list_custom"))
                    && PreferencesUtils.getStatusPrivacyMultiOptions(mContext) != null) {
                List<String> multiOptionList = Arrays.asList(PreferencesUtils.
                        getStatusPrivacyMultiOptions(mContext).split("\\s*,\\s*"));
                if (!multiOptionList.isEmpty()) {
                    for (int i = 0; i < multiOptionList.size(); i++) {
                        mMultiSelectUserPrivacy.put(multiOptionList.get(i), "1");
                    }
                }
            }

            setPrivacyOption(false);
            setStoryPrivacyOption(false);

            // once all data setup showing data in views.
            setDataInView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to set default privacy option when status page opened up.
     */
    private void setPrivacyOption(boolean isOptionChanged) {
        switch (mPostPrivacy) {
            case "network_list_custom":
            case "friend_list_custom":
                if (isOptionChanged) {
                    getPrivacyForm(mPostPrivacy.equals("friend_list_custom"), mPostPrivacy);
                    mPostPrivacy = !PreferencesUtils.getStatusPrivacyKey(mContext).equals("network_list_custom")
                            && !PreferencesUtils.getStatusPrivacyKey(mContext).equals("friend_list_custom")
                            ? PreferencesUtils.getStatusPrivacyKey(mContext) : null;
                } else {
                    setDescription(mUserPrivacyObject.optString(mPostPrivacy), false);
                    PreferencesUtils.setStatusPrivacyKey(mContext, mPostPrivacy);
                    mPostPrivacy = null;
                }
                break;

            default:
                setDescription(mUserPrivacyObject.optString(mPostPrivacy), false);
                break;
        }

        if (mPostPrivacy != null && !mPostPrivacy.equals("network_list_custom")
                && !mPostPrivacy.equals("friend_list_custom")) {
            PreferencesUtils.setStatusPrivacyKey(mContext, mPostPrivacy);
            if (mMultiSelectUserPrivacy != null) {
                mMultiSelectUserPrivacy.clear();
            }
        }
    }

    /**
     * Method to set default privacy key.
     */
    private void setDefaultPrivacyKey() {
        mPostPrivacy = !PreferencesUtils.getStatusPrivacyKey(mContext).equals("network_list_custom")
                && !PreferencesUtils.getStatusPrivacyKey(mContext).equals("friend_list_custom")
                ? PreferencesUtils.getStatusPrivacyKey(mContext) : "everyone";
        setDescription(mUserPrivacyObject.optString(mPostPrivacy), false);
    }

    /**
     * Method to set default story privacy when privacy change
     */
    private void setStoryPrivacyOption(boolean isOptionChanged) {
        if (mStoryPrivacy != null && !mStoryPrivacy.equals(PreferencesUtils.getStoryPrivacyKey(mContext))
                && isOptionChanged){
            PreferencesUtils.setStoryPrivacyKey(mContext, mStoryPrivacy);
        }
        setDescription(mStoryPrivacyObject.optString(mStoryPrivacy), true);
    }

    /**
     * Method to get privacy options and store them in preferences.
     */
    private void getStoryAndPostPrivacy() {
        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "advancedactivity/feeds/feed-post-menus",
                new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        if (jsonObject != null && jsonObject.optJSONObject("feed_post_menu") != null) {
                            PreferencesUtils.setStatusPrivacyOptions(mContext, jsonObject.optJSONObject("feed_post_menu"));
                        }
                        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "advancedactivity/stories/create",
                                new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) {
                                        if (jsonObject != null && jsonObject.optJSONArray("response") != null) {
                                            JSONObject privacy = jsonObject.optJSONArray("response").optJSONObject(0).optJSONObject("multiOptions");
                                            PreferencesUtils.setStoryPrivacy(mContext, privacy);
                                        }
                                        setPrivacy();
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                                    }
                                });
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                    }
                });
    }

    /**
     * Method to launch Form Creation activity for multiple friend/network list.
     *
     * @param isFriendList True if the form is to be load for friend list.
     * @param key          Key of the selected privacy option.
     */
    private void getPrivacyForm(boolean isFriendList, String key) {
        Intent intent = new Intent(mContext, CreateNewEntry.class);
        intent.putExtra("is_status_privacy", true);
        intent.putExtra("isFriendList", isFriendList);
        intent.putExtra("privacy_key", key);
        intent.putExtra("user_id", userDetails.optInt("user_id"));
        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.HOME_MENU_TITLE);
        intent.putExtra(ConstantVariables.CONTENT_TITLE, mUserPrivacyObject.optString(key));
        startActivityForResult(intent, ConstantVariables.USER_PRIVACY_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Method to set data in respective views.
     */
    private void setDataInView() {
        pbLoading.setVisibility(View.GONE);
        showHideViews(true);
        try {
            mImageLoader.setImageForUserProfile(userDetails.optString("image"), ivUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Getting drawable for setting
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_settings_white_24dp);
        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.gray_stroke_color),
                PorterDuff.Mode.SRC_ATOP));
        drawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_15dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.margin_15dp));
        tvStoryTitle.setCompoundDrawables(null, null, drawable, null);
        tvPostTitle.setCompoundDrawables(null, null, drawable, null);

        tvStoryTitle.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(tvStoryTitle) {
            @Override
            public boolean onDrawableClick() {
                showPopup(tvStoryTitle, true);
                return true;
            }
        });

        tvPostTitle.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(tvPostTitle) {
            @Override
            public boolean onDrawableClick() {
                showPopup(tvPostTitle, false);
                return true;
            }
        });

        setTitleTypeFace();
    }

    /**
     * Method to set title in bold format when the checkbox is selected.
     */
    private void setTitleTypeFace() {
        invalidateOptionsMenu();
        tvStoryTitle.setTypeface(cbStory.isChecked() ? Typeface.DEFAULT_BOLD : null);
        tvPostTitle.setTypeface(cbPost.isChecked() ? Typeface.DEFAULT_BOLD : null);
    }

    /**
     * Method to set description on story/post description view according to privacy.
     *
     * @param privacyTitle Privacy title of respected view.
     * @param isStory      True if it is for story.
     */
    private void setDescription(String privacyTitle, boolean isStory) {
        if (isStory) {
            tvStoryDesc.setText(mContext.getResources().getString(R.string.visible_in_story) + " "
                    + mContext.getResources().getString(R.string.to_text) + " " + privacyTitle + " "
                    + mContext.getResources().getQuantityString(R.plurals.for_days,
                    PreferencesUtils.getStoryDuration(mContext), PreferencesUtils.getStoryDuration(mContext)));
        } else {
            tvPostDesc.setText(mContext.getResources().getString(R.string.share_with) + " " + privacyTitle);
        }
    }

    /**
     * Method to show popup when the
     *
     * @param view    View on which popup need to be shown
     * @param isStory True if it is for story.
     */
    public void showPopup(View view, final boolean isStory) {

        PopupMenu popup = new PopupMenu(mContext, view);
        popupMenuList.clear();

        if (!isStory && mUserPrivacyObject != null && mUserPrivacyObject.length() != 0) {
            JSONArray mPrivacyKeys = mUserPrivacyObject.names();

            for (int i = 0; i < mUserPrivacyObject.length(); i++) {
                String key = mPrivacyKeys.optString(i);
                popupMenuList.add(key);
                String privacyLabel = mUserPrivacyObject.optString(key);
                if (mPostPrivacy != null && mPostPrivacy.equals(key)) {
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel).setCheckable(true).setChecked(true);
                } else if (mPostPrivacy == null && key.equals("everyone")
                        && (mMultiSelectUserPrivacy == null || mMultiSelectUserPrivacy.isEmpty())) {
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel).setCheckable(true).setChecked(true);
                } else {
                    boolean isSelected = (mMultiSelectUserPrivacy != null && mMultiSelectUserPrivacy.size() > 0)
                            && mMultiSelectUserPrivacy.get(key) != null && mMultiSelectUserPrivacy.get(key).equals("1");
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel).setCheckable(isSelected).setChecked(isSelected);
                }
            }
        }

        if (isStory && mStoryPrivacyObject != null && mStoryPrivacyObject.length() != 0) {
            JSONArray mPrivacyKeys = mStoryPrivacyObject.names();

            for (int i = 0; i < mStoryPrivacyObject.length(); i++) {
                String key = mPrivacyKeys.optString(i);
                popupMenuList.add(key);
                String privacyLabel = mStoryPrivacyObject.optString(key);
                if (mStoryPrivacy != null && mStoryPrivacy.equals(key)) {
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel).setCheckable(true).setChecked(true);
                } else {
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel);
                }
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (isStory) {
                    mStoryPrivacy = popupMenuList.get(id);
                    setStoryPrivacyOption(true);

                } else {
                    mPostPrivacy = popupMenuList.get(id);

                    // Clearing list when any other popup option(other than multiple friend/network list) is clicked.
                    if (!mPostPrivacy.equals("network_list_custom")
                            && !mPostPrivacy.equals("friend_list_custom")) {
                        mMultiSelectUserPrivacy.clear();
                    }
                    setPrivacyOption(true);
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * Method to get postparams for story/post uploading.
     *
     * @return Returns the postparams with required details.
     */
    private Intent getPostParams(Intent intent) {
        if (cbStory.isChecked()) {
            HashMap<String, String> storyParams = new HashMap<>();
            storyParams.put("privacy", mStoryPrivacy);
            for(int i = 0; i < mStoryCaption.size(); i++) {
                if (i == 0) {
                    storyParams.put("description", mStoryCaption.get(i));
                } else {
                    storyParams.put("description" + i, mStoryCaption.get(i));
                }
            }
            storyParams.put("duration", String.valueOf(mDuration));
            intent.putExtra("story_url", AppConstant.DEFAULT_URL + "advancedactivity/stories/create");
            intent.putExtra("story_param", storyParams);
        }

        if (cbPost.isChecked()) {
            String statusPostUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds/post";
            HashMap<String, String> postParams = new HashMap<>();

            if (mVideoPath != null) {
                List<String> enabledModuleList = null;
                if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
                    enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
                }
                if (enabledModuleList != null && enabledModuleList.contains("sitevideo")
                        && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains("sitevideo")) {
                    statusPostUrl = AppConstant.DEFAULT_URL + "advancedvideos/create";
                } else {
                    statusPostUrl = AppConstant.DEFAULT_URL + "videos/create";
                }
                postParams.put("is_storyPost", "1");

            } else {
                postParams.put("type", "photo");
                postParams.put("locationLibrary", "client");
                postParams.put("post_attach", "1");
                postParams.put("body", "");

                // Adding post privacy option.
                if (mMultiSelectUserPrivacy != null && !mMultiSelectUserPrivacy.isEmpty()) {
                    for (Map.Entry<String, String> entry : mMultiSelectUserPrivacy.entrySet()) {
                        if (entry.getValue().equals("1")) {
                            if (mPostPrivacy != null) {
                                mPostPrivacy += entry.getKey() + ",";
                            } else {
                                mPostPrivacy = entry.getKey() + ",";
                            }
                        }
                    }
                    mPostPrivacy = mPostPrivacy.substring(0, mPostPrivacy.lastIndexOf(","));

                } else if (mPostPrivacy == null) {
                    mPostPrivacy = "everyone";
                }
                postParams.put("auth_view", mPostPrivacy);

            }
            postParams.put("body", mStoryCaption.get(0));
            intent.putExtra("post_param", postParams);
            intent.putExtra("post_url", statusPostUrl);
        }

        if (mVideoPath != null && mVideoThumb != null) {
            intent.putExtra(MultiMediaSelectorActivity.VIDEO_RESULT, mVideoPath);
            intent.putExtra(ConstantVariables.STORY_VIDEO_THUMB, mVideoThumb);
        }
        intent.putStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT, mSelectPath);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = null;
        if (data != null) {
            bundle = data.getExtras();
        }

        if (bundle != null && requestCode == ConstantVariables.USER_PRIVACY_REQUEST_CODE
                && bundle.getSerializable("param") != null) {
            mMultiSelectUserPrivacy = (HashMap<String, String>) bundle.getSerializable("param");

            if (bundle.getString("feed_post_menu") != null
                    && bundle.getString("feed_post_menu").length() > 0
                    && bundle.getString("privacy_key").equals("friend_list_custom")) {
                try {
                    mFeedPostMenus = new JSONObject(bundle.getString("feed_post_menu"));
                    mUserPrivacyObject = mFeedPostMenus.optJSONObject("userprivacy");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (mMultiSelectUserPrivacy != null && mMultiSelectUserPrivacy.size() > 0) {
                boolean isAnyOptionSelected = false;
                for (Map.Entry<String, String> entry : mMultiSelectUserPrivacy.entrySet()) {
                    if (entry.getValue().equals("1")) {
                        isAnyOptionSelected = true;
                        break;
                    }
                }

                // When there is any option is selected then showing the name of multi select list.
                if (isAnyOptionSelected) {
                    mPostPrivacy = null;
                    setDescription(mUserPrivacyObject.optString(bundle.getString("privacy_key")), false);
                    String multiOptions = null;
                    for (Map.Entry<String, String> entry : mMultiSelectUserPrivacy.entrySet()) {
                        if (entry.getValue().equals("1")) {
                            if (multiOptions != null) {
                                multiOptions += entry.getKey() + ",";
                            } else {
                                multiOptions = entry.getKey() + ",";
                            }
                        }
                    }
                    if (multiOptions != null) {
                        multiOptions = multiOptions.substring(0, multiOptions.lastIndexOf(","));
                        PreferencesUtils.setStatusPrivacyKey(mContext, bundle.getString("privacy_key"));
                        PreferencesUtils.setStatusPrivacyMultiOptions(mContext, multiOptions);
                    }
                } else {
                    mPostPrivacy = !PreferencesUtils.getStatusPrivacyKey(mContext).equals("network_list_custom")
                            && !PreferencesUtils.getStatusPrivacyKey(mContext).equals("friend_list_custom")
                            ? PreferencesUtils.getStatusPrivacyKey(mContext) : "everyone";
                    mMultiSelectUserPrivacy.clear();
                    PreferencesUtils.setStatusPrivacyKey(mContext, mPostPrivacy);
                    setDescription(mUserPrivacyObject.optString(mPostPrivacy), false);
                }
            } else {
                setDefaultPrivacyKey();
            }
        } else {
            mMultiSelectUserPrivacy.clear();
            setDefaultPrivacyKey();
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

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        } else {
            Intent intent = new Intent();
            intent = getPostParams(intent);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            Drawable drawable = submit.getIcon();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        if (!cbStory.isChecked() && !cbPost.isChecked() && menu.findItem(R.id.submit) != null) {
            menu.findItem(R.id.submit).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.story_layout:
            case R.id.select_story:
                cbStory.setChecked(!cbStory.isChecked());
                setTitleTypeFace();
                break;

            case R.id.post_layout:
            case R.id.select_post:
                cbPost.setChecked(!cbPost.isChecked());
                setTitleTypeFace();
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setTitleTypeFace();
    }
}
