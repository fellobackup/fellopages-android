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

package com.fellopages.mobileapp.classes.modules.user.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.PhotoUploadingActivity;
import com.fellopages.mobileapp.classes.common.adapters.ViewPageFragmentAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.FragmentDrawer;
import com.fellopages.mobileapp.classes.core.LoginActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;
import com.fellopages.mobileapp.classes.modules.user.signup.SubscriptionActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.dimorinny.showcasecard.position.TopLeftToolbar;
import ru.dimorinny.showcasecard.position.TopRightToolbar;
import ru.dimorinny.showcasecard.position.ViewPosition;
import ru.dimorinny.showcasecard.step.ShowCaseStep;
import ru.dimorinny.showcasecard.step.ShowCaseStepDisplayer;


public class userProfile extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        OnUploadResponseListener, OnOptionItemClickResponseListener, View.OnClickListener, ShowCaseStepDisplayer.DismissListener {

    static int mUserId, index = 0;
    private String userProfileUrl, displayName, mPreviousSelectedModule, mCoverImageUrl;
    private int mPreviousSelectedModuleListingTypeId;
    private int ONE_WAY = 1;
    private int TWO_WAY = 2;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private Toolbar mToolBar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private ProgressBar mProgressBar;
    private JSONObject mBody, mResponseObject;
    private JSONArray mGutterMenus, mOptionArray, mProfileTabs, mUserProfileTabs, coverPhotoMenuArray,
            profilePhotoMenuArray;
    private ImageView mCoverImage, mProfileImage;
    private TextView mProfileImageMenus, mCoverImageMenus, mContentTitle;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Context mContext;
    private ViewPageFragmentAdapter mViewPageFragmentAdapter;
    private String successMessage;
    private CoordinatorLayout mMainContent;
    private ArrayList<PhotoListDetails> mPhotoDetails, mProfilePhotoDetail;
    private Boolean isOrganizerProfile = false, isCoverRequest = false, isShowFriends = false;
    private TextView mToolBarTitle, tvAddFriend, tvMessage, tvMore, tvFollow;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;
    private int mFriendshipType = ONE_WAY;
    private boolean isFriendsOption = false;

    private JSONObject menuObject;
    private static Activity activity;
    private ShowCaseStepDisplayer.Builder showCaseStepDisplayer;
    private boolean isShowCaseView;
    private int callingTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Log.d("ThisIsTheUserAcct ", "true");
        mAppConst = new AppConstant(this);
        mContext = this;

        /* Initialize show case view step builder */
        showCaseStepDisplayer = new ShowCaseStepDisplayer.Builder(this, R.color.colorAccent);

        // Todo enable it for testing
//        PreferencesUtils.setProfileShowCaseViewPref(mContext, false, false,
//                false, false, false, false, false);

        mGutterMenuUtils = new GutterMenuUtils(this, true);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(getApplicationContext());
        mProfileTabs = mUserProfileTabs = new JSONArray();
        mUserId = getIntent().getExtras().getInt(ConstantVariables.USER_ID);
        isShowFriends = getIntent().getBooleanExtra("isShowFriends", false);

        String profileType = getIntent().getExtras().getString(ConstantVariables.PROFILE_TYPE);
        if (profileType != null && !profileType.isEmpty() && profileType.equals("organizer_profile")) {
            isOrganizerProfile = true;
        }

        mPreviousSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if (mPreviousSelectedModule != null && mPreviousSelectedModule.equals("sitereview_listing")) {
            mPreviousSelectedModuleListingTypeId = PreferencesUtils.getCurrentSelectedListingId(mContext);
        }

        mToolBar = findViewById(R.id.toolbar);
        mToolBarTitle = findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPhotoDetails = new ArrayList<>();
        mProfilePhotoDetail = new ArrayList<>();

        mProgressBar = findViewById(R.id.progressBar);
        mContentTitle = findViewById(R.id.content_title);
        mCoverImage = findViewById(R.id.coverImage);
        mProfileImage = findViewById(R.id.profile_image);
        mCoverImageMenus = findViewById(R.id.cover_image_menus);
        mProfileImageMenus = findViewById(R.id.profile_image_menus);
        mCoverImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mProfileImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.slidingTabs);
        mMainContent = findViewById(R.id.main_content);
        appBar = findViewById(R.id.appbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        tvAddFriend = findViewById(R.id.add_friend);
        tvMessage = findViewById(R.id.message);
        tvMore = findViewById(R.id.more);
        tvFollow = findViewById(R.id.follow);

        tvAddFriend.setOnClickListener(this);
        tvMessage.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvFollow.setOnClickListener(this);

        if (isOrganizerProfile) {
            userProfileUrl = AppConstant.DEFAULT_URL + "advancedevents/organizer/" + mUserId;
        } else {
            userProfileUrl = AppConstant.DEFAULT_URL + "user/profile/" + mUserId;
        }

        // IF profile photo is uploaded via album view page.
        if (getIntent().getBooleanExtra(ConstantVariables.IS_PHOTO_UPLOADED, false)) {
            mAppConst.refreshUserData();
        }
        try {
            if (getIntent().hasExtra(ConstantVariables.PROFILE_QUICK_INFO) && getIntent().getStringExtra(ConstantVariables.PROFILE_QUICK_INFO) != null) {
                JSONObject userDetails = new JSONObject(getIntent().getStringExtra(ConstantVariables.PROFILE_QUICK_INFO));
                mBody = userDetails;
            }
            if (!mAppConst.isLoggedOutUser()) {
                JSONObject userDetails = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                if (mUserId == userDetails.optInt("user_id", 0)) {
                    mBody = userDetails;
                }
            }
            if (mBody != null) {
//                setUserProfileDetails(mBody, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeRequest();
    }

    public void makeRequest() {

        try {
            mAppConst.getJsonResponseFromUrl(userProfileUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBody = jsonObject;
                    mProgressBar.setVisibility(View.GONE);

                    if (mBody != null) {
                        setUserProfileDetails(jsonObject, false);
                        // Adding verification gutter menu.
                        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "memberverify/allow-verify?user_id=" + mUserId,
                                new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) {
                                        mBrowseList.setIsAdminApprovalRequired(jsonObject.optInt("is_admin_approval_required") == 1);
                                        mBrowseList.setIsAdminApproved(jsonObject.optInt("admin_approve") == 1);

                                        JSONArray menuArray = jsonObject.optJSONArray("menu");
                                        if (menuArray != null && menuArray.length() > 0) {
                                            if (mOptionArray == null) {
                                                mOptionArray = menuArray;
                                            } else {
                                                for (int i = 0; i < menuArray.length(); i++) {
                                                    mOptionArray.put(menuArray.optJSONObject(i));
                                                }
                                            }

                                            if (mOptionArray.length() > 0) {
                                                tvMore.setVisibility(View.VISIBLE);
                                                setEditProfileOptions(tvMore, mContext.getResources().getString(R.string.more),
                                                        -1, R.drawable.ic_more);
                                            }
                                            isShowCaseView = false;
                                            invalidateOptionsMenu();
                                        }
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                                    }
                                });


                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                    // Show Privacy Message to user if not authorized to view this
                    mProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(mMainContent, message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    if(!isFinishing()){
                                        finish();
                                    }
                                }
                            });
                }
            });
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setUserProfileDetails(JSONObject jsonObject, boolean isCache) {
        mGutterMenus = mBody.optJSONArray("gutterMenu");
        Log.d("mGutterMenus ", String.valueOf(mGutterMenus));
        mUserProfileTabs = mBody.optJSONArray("profile_tabs");
        if (mUserProfileTabs == null) {
            mUserProfileTabs = mBody.optJSONArray("profileTabs");
        }
        mResponseObject = mBody.optJSONObject("response");
        if (mResponseObject == null) {
            mResponseObject = mBody;

        }

        if (mResponseObject != null) {
            mUserId = mResponseObject.optInt("user_id");
            String userImageProfile = null;
            if (isOrganizerProfile) {
                mUserId = mResponseObject.optInt("organizer_id");
                displayName = jsonObject.optString("title");
                userImageProfile = jsonObject.optString("owner_image");
                if (userImageProfile == null || userImageProfile.isEmpty() )
                    userImageProfile = jsonObject.optString("image");

                mImageLoader.setImageUrl(userImageProfile, mCoverImage);

            } else {
                displayName = mResponseObject.optString("displayname");
                userImageProfile = mResponseObject.optString("image");
                // When the user name or profile image is changed then Updating the user data.
                if (!mAppConst.isLoggedOutUser()
                        && PreferencesUtils.getUserDetail(mContext) != null) {
                    try {
                        JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                        String userName = userDetail.optString("displayname");
                        String coverImageUrl = userDetail.optString("image");
                        if (userDetail.optInt("user_id") == mUserId && (!displayName.equals(userName) ||
                                !userImageProfile.equals(coverImageUrl))) {
//                            mResponseObject.optString("image_profile").equals(mCoverImageUrl)
                            Log.d("TestHerePls ", "true");
                            userDetail.put("displayname", displayName);
                            userDetail.put("image", userImageProfile);
                            userDetail.put("image_profile", mResponseObject.optString("image_profile"));
                            PreferencesUtils.updateUserPreferences(mContext, userDetail.toString(),
                                    PreferencesUtils.getUserPreferences(mContext).getString("oauth_secret", null),
                                    PreferencesUtils.getUserPreferences(mContext).getString("oauth_token", null));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            mBrowseList = new BrowseListItems("", mUserId, displayName);

            // Used for message.
            mBrowseList.setUserId(mUserId);
            mBrowseList.setUserDisplayName(displayName);
            mBrowseList.setUserProfileImageUrl(mResponseObject.optString("image_normal"));

            mPhotoDetails.clear();
            mCoverImageUrl = mResponseObject.optString("cover");

            //Showing content title
            mContentTitle.setText(displayName);
            collapsingToolbar.setTitle(displayName);
            mToolBarTitle.setText(displayName);
            CustomViews.setCollapsingToolBarTitle(collapsingToolbar);

            if (mResponseObject.optInt("showVerifyIcon") == 1) {
                mContentTitle.setMaxLines(1);
                mContentTitle.setSingleLine(true);
                mContentTitle.setEllipsize(TextUtils.TruncateAt.END);//
                Drawable verifyDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_verification);
                verifyDrawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_15dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.margin_15dp));
                mContentTitle.setCompoundDrawables(null, null, verifyDrawable, null);
            }

            // If not null then showing user cover image plugin views.
            if (mCoverImageUrl != null && !mCoverImageUrl.isEmpty()) {
                mProfileImage.setVisibility(View.VISIBLE);
                coverPhotoMenuArray = mResponseObject.optJSONArray("coverPhotoMenu");
                profilePhotoMenuArray = mResponseObject.optJSONArray("mainPhotoMenu");
                if (coverPhotoMenuArray != null && coverPhotoMenuArray.length() > 0) {
                    mCoverImageMenus.setVisibility(View.VISIBLE);
                    mCoverImageMenus.setText("\uf030");
                }
                if (profilePhotoMenuArray != null && profilePhotoMenuArray.length() > 0) {
                    mProfileImageMenus.setVisibility(View.VISIBLE);
                    mProfileImageMenus.setText("\uf030");
                }
                //Showing profile image.
                mImageLoader.setImageForUserProfile(userImageProfile, mProfileImage);
                if (isCache && (userImageProfile == null || userImageProfile.contains("nophoto_user_thumb_profile.png")|| userImageProfile.isEmpty())) {
                    showUploadImageDialog();
                }
                //Showing Cover image.
                if (coverPhotoMenuArray != null){
                    if(coverPhotoMenuArray.length() < 4){
                        if (PreferencesUtils.getUserDetail(mContext) != null) {
                            try {
                                JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                                Log.d("TestLogHere ", "true");
                                userDetail.put("cover", mCoverImage);
                                if (mGutterMenus.length() < 3){
                                    PreferencesUtils.updateUserDetails(mContext, userDetail.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
//
//
//                if (!mResponseObject.optString("image_profile").equals(mCoverImageUrl) || mResponseObject.optString("image_profile").equals("image")) {
//                    Log.d("ThisCoverisSimilar ", "true");
//
//                }
                Log.d("ThisShouldExecute ", mCoverImageUrl);
//                mImageLoader.setImageUrl(mCoverImageUrl, mCoverImage);
                Picasso.with(userProfile.this).load(mCoverImageUrl).fit().centerCrop().into(mCoverImage);

                mProfilePhotoDetail.clear();
                mProfilePhotoDetail.add(new PhotoListDetails(userImageProfile));
                mPhotoDetails.add(new PhotoListDetails(mCoverImageUrl));

//                userDetail.put("cover", mCoverImage);
                if (mGutterMenus.length() < 3){
                    PreferencesUtils.updateUserDetails(mContext, mResponseObject.toString());
                }

            } else {
                mImageLoader.setImageUrl(userImageProfile, mCoverImage);
                mPhotoDetails.add(new PhotoListDetails(userImageProfile));
            }

            mCoverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCoverImageUrl != null && !mCoverImageUrl.isEmpty() &&
                            coverPhotoMenuArray != null && coverPhotoMenuArray.length() > 0) {
                        isCoverRequest = true;
                        mGutterMenuUtils.showPopup(mCoverImageMenus, coverPhotoMenuArray,
                                mBrowseList, ConstantVariables.USER_MENU_TITLE);
                    } else {
                        openLightBox(true);
                    }

                }
            });

            mProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCoverImageUrl != null && !mCoverImageUrl.isEmpty() &&
                            profilePhotoMenuArray != null && profilePhotoMenuArray.length() > 0) {
                        isCoverRequest = false;
                        mGutterMenuUtils.showPopup(mProfileImageMenus, profilePhotoMenuArray,
                                mBrowseList, ConstantVariables.USER_MENU_TITLE);

                    } else {
                        openLightBox(false);
                    }
                }
            });

            if (mUserProfileTabs != null && !isCache) {
                if(mProfileTabs != null && mProfileTabs.length() != 0){
                    mProfileTabs = new JSONArray();
                }
                for (int i = 0; i < mUserProfileTabs.length(); i++) {
                    JSONObject singleJsonObject = mUserProfileTabs.optJSONObject(i);
                    String tabName = singleJsonObject.optString("name");
                    int totalItemCount = singleJsonObject.optInt("totalItemCount");

                    if (!((!(tabName.equals("update") || tabName.equals("info") ||
                            tabName.equals("organizer_info") || tabName.equals("organizer_events")) &&
                            (totalItemCount == 0)) || (tabName.equals("forum_topics")))) {
                        mProfileTabs.put(singleJsonObject);
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.SUBJECT_TYPE, "user");
                bundle.putInt(ConstantVariables.SUBJECT_ID, mUserId);
                bundle.putString(ConstantVariables.MODULE_NAME, "userProfile");
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, "userProfile");
                bundle.putInt(ConstantVariables.USER_ID, mUserId);
                bundle.putString(ConstantVariables.RESPONSE_OBJECT, mResponseObject.toString());

                // Setup the viewPager
                mViewPageFragmentAdapter = new ViewPageFragmentAdapter(mContext,
                        getSupportFragmentManager(), mProfileTabs, bundle);
                mViewPager.setAdapter(mViewPageFragmentAdapter);
                mViewPager.setOffscreenPageLimit(mViewPageFragmentAdapter.getCount() + 1);
                // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
                mTabLayout.setupWithViewPager(mViewPager);
                if (isShowFriends) {
                    mViewPageFragmentAdapter.loadFriendTab();
                    isShowFriends = false;
                }

                mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        try {
                            JSONObject jsonObject = mProfileTabs.getJSONObject(tab.getPosition());
                            PreferencesUtils.setCurrentSelectedListingId(mContext, jsonObject.optInt(ConstantVariables.LISTING_TYPE_ID));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        }
        // Method to check profile options.
        if (!isCache)
            checkForOptions();
    }

    private void showUploadImageDialog(){
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
        dlgBuilder.setTitle(null);
        dlgBuilder.setMessage(getResources().getString(R.string.alert_upload_profile_pic_desc));
        dlgBuilder.setPositiveButton(getResources().getString(R.string.upload_profile_pic), (dialog, which) -> {
            dialog.dismiss();
            startImageUploading();

        });
        dlgBuilder.setNegativeButton(getResources().getString(R.string.browse_dashboard_dialog_negative_button), (dialog, which) -> {
            dialog.dismiss();

        });
        dlgBuilder.create().show();
    }

    private void checkForOptions() {
        findViewById(R.id.profile_options).setVisibility(View.VISIBLE);
        if (mGutterMenus != null && mGutterMenus.length() > 0) {
            findViewById(R.id.cover_layout).setBackgroundResource(R.color.white);
            mOptionArray = new JSONArray();
            JSONObject profileObject = null;
            for (int i = 0; i < mGutterMenus.length(); i++) {
                JSONObject jsonObject = mGutterMenus.optJSONObject(i);
                String menuName = jsonObject.optString("name");

                switch (menuName) {
                    case "add_friend":
                    case "member_follow":
                    case "accept_request":
                        setAddFriendOption(i, jsonObject, R.drawable.ic_friend_request, R.color.gray_text_color);
                        if (!PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.USER_PROFILE_FRIENDS_SHOW_CASE_VIEW)) {
                            isShowCaseView = true;

                            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.USER_PROFILE_FRIENDS_SHOW_CASE_VIEW);
                            showCaseStepDisplayer.addStep(new ShowCaseStep(new ViewPosition(tvAddFriend),
                                    mContext.getResources().getString(R.string.user_profile_send_friend_request_show_case_text),
                                    mContext.getResources().getDimension(R.dimen.radius_20)));
                        }
                        break;

                    case "remove_friend":
                    case "member_unfollow":
                        setAddFriendOption(i, jsonObject, R.drawable.ic_user_remove, R.color.themeButtonColor);
                        break;

                    case "cancel_request":
                    case "cancel_follow":
                        setAddFriendOption(i, jsonObject, R.drawable.ic_user_cancel, R.color.themeButtonColor);
                        break;

                    case "follow":
                        setFollowMemberOption(i, jsonObject, R.drawable.ic_follow_action, R.color.gray_text_color);
                        if (!PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.USER_PROFILE_FOLLOW_SHOW_CASE_VIEW)) {
                            isShowCaseView = true;

                            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.USER_PROFILE_FOLLOW_SHOW_CASE_VIEW);
                            showCaseStepDisplayer.addStep(new ShowCaseStep(new ViewPosition(tvFollow),
                                    mContext.getResources().getString(R.string.user_profile_follow_show_case_text),
                                    mContext.getResources().getDimension(R.dimen.radius_20)));
                        }
                        break;

                    case "following":
                        setFollowMemberOption(i, jsonObject, R.drawable.ic_follow_action, R.color.themeButtonColor);
                        break;

                    case "user_profile_send_message":
                        tvMessage.setEnabled(true);
                        tvMessage.setClickable(true);
                        tvMessage.setAlpha(1);
                        setEditProfileOptions(tvMessage,
                                mContext.getResources().getString(R.string.contact_us_message),
                                i, R.drawable.ic_message);
                        if (!PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.USER_PROFILE_MESSAGE_SHOW_CASE_VIEW)) {
                            isShowCaseView = true;
                            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.USER_PROFILE_MESSAGE_SHOW_CASE_VIEW);
                            showCaseStepDisplayer.addStep(new ShowCaseStep(new ViewPosition(tvMessage),
                                    mContext.getResources().getString(R.string.user_profile_send_message_show_case_text),
                                    mContext.getResources().getDimension(R.dimen.radius_20)));
                        }

                        if (mViewPageFragmentAdapter != null) {
                            mViewPageFragmentAdapter.checkForMessageOption(mBrowseList);
                        }
                        break;

                    case "user_home_edit":
                        try {
                            profileObject = new JSONObject(jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setEditProfileOptions(tvAddFriend, mContext.getResources().getString(R.string.edit_profile),
                                i, R.drawable.ic_status);
                        // Add edit profile showcase view
                        if (!PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.USER_PROFILE_EDIT_SHOW_CASE_VIEW)) {
                            isShowCaseView = true;
                            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.USER_PROFILE_EDIT_SHOW_CASE_VIEW);
                            showCaseStepDisplayer.addStep(new ShowCaseStep(new ViewPosition(tvAddFriend),
                                    mContext.getResources().getString(R.string.user_profile_edit_profile_show_case_text),
                                    mContext.getResources().getDimension(R.dimen.radius_20)));
                        }
                        isFriendsOption = true;
                        break;

                    default:
                        mOptionArray.put(jsonObject);
                        break;
                }
            }

            if (mOptionArray.length() > 0) {
                tvMore.setVisibility(View.VISIBLE);
                setEditProfileOptions(tvMore, mContext.getResources().getString(R.string.more),
                        -1, R.drawable.ic_more);
            } else {
                tvMore.setVisibility(View.GONE);
            }

            if (profileObject != null && profileObject.length() > 0) {
                try {
                    tvMessage.setEnabled(true);
                    tvMessage.setClickable(true);
                    tvMessage.setAlpha(1);
                    setEditProfileOptions(tvMessage, mContext.getResources().getString(R.string.edit_photo),
                            mGutterMenus.length(), R.drawable.ic_photo_camera_white_24dp);
                    profileObject.put("is_photo_tab", true);

                    // Add edit profile photo showcase view
                    if (!PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.USER_PROFILE_EDIT_PHOTO_SHOW_CASE_VIEW)) {
                        isShowCaseView = true;
                        PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.USER_PROFILE_EDIT_PHOTO_SHOW_CASE_VIEW);
                        showCaseStepDisplayer.addStep(new ShowCaseStep(new ViewPosition(tvMessage),
                                mContext.getResources().getString(R.string.user_profile_edit_photo_show_case_text),
                                mContext.getResources().getDimension(R.dimen.radius_20)));
                    }

                    mGutterMenus.put(mGutterMenus.length(), profileObject);

                    if (mViewPageFragmentAdapter != null) {
                        tvFollow.setEnabled(mViewPageFragmentAdapter.isFriendTabExist());
                        if (mViewPageFragmentAdapter.isFriendTabExist()) {
                            tvFollow.setAlpha(1);
                        } else {
                            tvFollow.setAlpha(0.5f);
                        }
                        tvFollow.setClickable(mViewPageFragmentAdapter.isFriendTabExist());
                    }
                    setEditProfileOptions(tvFollow, mContext.getResources().getString(R.string.action_bar_title_friend),
                            mGutterMenus.length() + 1, R.drawable.ic_user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                tvFollow.setVisibility(View.VISIBLE);
                setEditProfileOptions(tvMessage,
                        mContext.getResources().getString(R.string.contact_us_message),
                        -1, R.drawable.ic_message);
            }

        } else {

            findViewById(R.id.cover_layout).setBackgroundResource(R.color.white);
            setEditProfileOptions(tvAddFriend,
                    mContext.getResources().getString(R.string.add_friend_title),
                    -1, R.drawable.ic_friend_request);
            setEditProfileOptions(tvMessage,
                    mContext.getResources().getString(R.string.contact_us_message),
                    -1, R.drawable.ic_message);

            setEditProfileOptions(tvMore,
                    mContext.getResources().getString(R.string.more),
                    -1, R.drawable.ic_more);

            setEditProfileOptions(tvFollow,
                    mContext.getResources().getString(R.string.follow),
                    -1, R.drawable.ic_follow_action);

            disableOptions(tvAddFriend);
            disableOptions(tvMessage);
            disableOptions(tvMore);
            disableOptions(tvFollow);
        }
        invalidateOptionsMenu();

    }

    private void disableOptions(TextView tvOption) {
        tvOption.setClickable(false);
        tvOption.setEnabled(false);
        tvOption.setAlpha(0.5f);
    }

    private void setAddFriendOption(int position, JSONObject jsonObject, int drawableResId, int colorResId) {
        tvAddFriend.setVisibility(View.VISIBLE);
        tvAddFriend.setText(jsonObject.optString("label"));
        tvAddFriend.setTag(position);
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableResId);
        tvAddFriend.setTextColor(ContextCompat.getColor(mContext, colorResId));
        drawable.setColorFilter(ContextCompat.getColor(mContext, colorResId), PorterDuff.Mode.SRC_ATOP);
        tvAddFriend.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    private void setFollowMemberOption(int position, JSONObject jsonObject, int drawableResId, int colorResId) {
        mFriendshipType = TWO_WAY;
        tvFollow.setVisibility(View.VISIBLE);
        tvFollow.setText(jsonObject.optString("label"));
        tvFollow.setTag(position);
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableResId);
        tvFollow.setTextColor(ContextCompat.getColor(mContext, colorResId));
        drawable.setColorFilter(ContextCompat.getColor(mContext, colorResId), PorterDuff.Mode.SRC_ATOP);
        tvFollow.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    private void setEditProfileOptions(TextView tvOption, String label, int tag, int resId) {
        tvOption.setText(label);
        tvOption.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(mContext, resId);
        drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color),
                PorterDuff.Mode.SRC_ATOP);
        tvOption.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        if (tag >= 0) {
            tvOption.setTag(tag);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mFriendshipType == TWO_WAY && mGutterMenus != null) {
            menu.clear();
            mGutterMenuUtils.showOptionMenus(menu, mOptionArray, ConstantVariables.USER_MENU_TITLE, mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        ShowCaseStep showCaseStep = null;

        if (mFriendshipType == ONE_WAY) {
            menu.findItem(R.id.action_settings).setVisible(false);
            if (mOptionArray != null && mOptionArray.length() > 0) {
                tvMore.setVisibility(View.VISIBLE);
                // Add more option showcase view
                showCaseStep = new ShowCaseStep(new ViewPosition(tvMore),
                        mContext.getResources().getString(R.string.user_profile_more_icon_show_case_text),
                        mContext.getResources().getDimension(R.dimen.radius_25));
            }
            if (isFriendsOption){
                tvFollow.setVisibility(View.VISIBLE);
            } else {
                tvFollow.setVisibility(View.GONE);
            }
        } else {
            menu.findItem(R.id.action_settings).setVisible(true);
            tvMore.setVisibility(View.GONE);
            tvFollow.setVisibility(View.VISIBLE);

            showCaseStep = new ShowCaseStep(new TopRightToolbar(),
            mContext.getResources().getString(R.string.user_profile_more_icon_show_case_text),
                    mContext.getResources().getDimension(R.dimen.radius_20));
        }

        // Add more option showcase view
        if (showCaseStep != null && !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.USER_PROFILE_MORE_SHOW_CASE_VIEW)) {
            isShowCaseView = true;
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.USER_PROFILE_MORE_SHOW_CASE_VIEW);
            showCaseStepDisplayer.addStep(showCaseStep);
        }
        callingTime++;
        if (callingTime >= 2 && isShowCaseView && PreferencesUtils.getAppTourEnabled(mContext) == 1) {
            showCaseStepDisplayer.build().start();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        } else {
            if(mOptionArray != null) {
                mGutterMenuUtils.onMenuOptionItemSelected(mMainContent, findViewById(item.getItemId()), id, mOptionArray);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to open photoLightBox when user click on view image
     *
     * @param isCoverRequest parameter to decide whether it is user profile or user cover.
     */
    public void openLightBox(boolean isCoverRequest) {

        Bundle bundle = new Bundle();
        if (isCoverRequest) {
            bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mPhotoDetails);
        } else {
            bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mProfilePhotoDetail);
        }
        Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
        i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
        i.putExtras(bundle);
        startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);

    }

    private void startImageUploading() {
        if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
        }else {
            Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
            intent.putExtra("selection_mode", true);
            intent.putExtra(ConstantVariables.IS_PHOTO_UPLOADED, true);
            startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.follow:
                int position = (int) view.getTag();
                if (position == mGutterMenus.length() + 1 && mViewPageFragmentAdapter != null) {
                    mViewPageFragmentAdapter.loadFriendTab();
                } else {
                    mGutterMenuUtils.onMenuItemSelected(view, position, mGutterMenus,
                            ConstantVariables.USER_MENU_TITLE, mBrowseList);
                }
                break;

            case R.id.more:
                if (view.getTag() != null) {
                    if (mViewPageFragmentAdapter != null) {
                        mViewPageFragmentAdapter.loadFriendTab();
                    }
                } else if (mOptionArray != null) {
                    mGutterMenuUtils.showPopup(view, mOptionArray, mBrowseList, ConstantVariables.USER_MENU_TITLE);
                }
                break;

            default:
                int pos = (int) view.getTag();
                mGutterMenuUtils.onMenuItemSelected(view, pos, mGutterMenus,
                        ConstantVariables.USER_MENU_TITLE, mBrowseList);
                break;

        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        index = i;
        CustomViews.showMarqueeTitle(i, collapsingToolbar, mToolBar, mToolBarTitle, displayName);
    }

    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {

        if (isRequestSuccessful) {
            SnackbarUtils.displaySnackbarLongTime(mMainContent, successMessage);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
            mAppConst.refreshUserData();
            makeRequest();

        } else {
            SnackbarUtils.displaySnackbarLongTime(mMainContent, jsonObject.optString("message"));
        }

    }

    @Override
    public void onItemDelete(String successMessage) {

    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseList = (BrowseListItems) itemList;

        switch (menuName) {
            case "view_profile_photo":
            case "view_cover_photo":
                openLightBox(isCoverRequest);
                break;

            case "upload_cover_photo":
            case "upload_photo":
                startImageUploading();
                break;

            case "choose_from_album":
                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "album");
                bundle.putString(ConstantVariables.CONTENT_TITLE, mBrowseList.getmBrowseListTitle());
                bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                bundle.putInt("user_id", mUserId);
                bundle.putBoolean("isMemberAlbums", true);
                bundle.putBoolean("isCoverRequest", isCoverRequest);
                if (isOrganizerProfile) {
                    bundle.putString(ConstantVariables.PROFILE_TYPE, "organizer_profile");
                }

                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, ConstantVariables.PAGE_EDIT_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            default:
                finish();
                startActivity(getIntent());
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        appBar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        appBar.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mPreviousSelectedModule != null) {
            PreferencesUtils.updateCurrentModule(mContext, mPreviousSelectedModule);
            if (mPreviousSelectedModule.equals("sitereview_listing") && mPreviousSelectedModuleListingTypeId != 0) {
                PreferencesUtils.setCurrentSelectedListingId(mContext, mPreviousSelectedModuleListingTypeId);
            } else if (mPreviousSelectedModule.equals(ConstantVariables.USER_MENU_TITLE)) {
                setResult(RESULT_OK);
                finish();
            }
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case ConstantVariables.VIEW_PAGE_CODE:
                makeRequest();
                break;

            case ConstantVariables.PAGE_EDIT_CODE:
                if (resultCode == ConstantVariables.PAGE_EDIT_CODE && data != null) {
                    ArrayList<String> resultList = data.getStringArrayListExtra(ConstantVariables.PHOTO_LIST);
                    String postUrl;
                    if (isCoverRequest) {
                        postUrl = AppConstant.DEFAULT_URL + "user/profilepage/upload-cover-photo/user_id/" +
                                mUserId + "/special/cover";
                        successMessage = mContext.getResources().getString(R.string.cover_photo_updated);
                    } else {
                        postUrl = AppConstant.DEFAULT_URL + "user/profilepage/upload-cover-photo/user_id/" +
                                mUserId + "/special/profile";
                        successMessage = mContext.getResources().getString(R.string.profile_photo_updated);
                    }
                    new UploadFileToServerUtils(userProfile.this, postUrl, resultList, this).execute();
                }
                break;

            case ConstantVariables.USER_PROFILE_CODE:
                if (resultCode == ConstantVariables.USER_PROFILE_CODE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.bringToFront();
                    mAppConst.refreshUserData();
                    makeRequest();
                }
                break;

            case ConstantVariables.FEED_REQUEST_CODE:
                /* Reset the view pager adapter if any status is posted using AAF */
                if (resultCode == ConstantVariables.FEED_REQUEST_CODE && mViewPager != null) {
                    mViewPager.setAdapter(mViewPageFragmentAdapter);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    startImageUploading();
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mMainContent,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

        }
    }

    @Override
    public void onDismiss() {

    }
}
