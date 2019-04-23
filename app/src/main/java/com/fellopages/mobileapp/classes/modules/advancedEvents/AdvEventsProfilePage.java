/*
 *   Copyright (c) 2015 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.modules.advancedEvents;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ViewPageFragmentAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.fragments.PhotoFragment;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.PhotoUploadingActivity;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.MainActivity;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.advancedEvents.ticketsSelling.AdvEventsBuyTicketsInfo;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.likeNComment.EventLikesActivity;
import com.fellopages.mobileapp.classes.modules.likeNComment.Likes;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AdvEventsProfilePage extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        OnOptionItemClickResponseListener, OnUploadResponseListener, SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private String mModuleName, mPhotoMenuTitle;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private JSONObject mBody, mDataResponse;
    private JSONArray mGutterMenus, mProfileTabs;
    private String title, profileImageUrl, mItemViewUrl, coverImageUrl;
    private Integer likeCount = 0;
    private String successMessage;
    private String mContentUrl, mInviteGuestUrl, location, startTime, endTime;
    private int mContentId, mEventId, mOccurrenceId, mProfileTabSize;
    // private CharSequence[] items = new CharSequence[3]; // TODO: Just enable this back if rsvp will applied
    private int isClosed;
    private boolean isLoadingFromCreate = false, isContentEdited = false, isContentDeleted = false;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView mCoverImage, mProfileImage, mLikeIcon;
    private CoordinatorLayout mMainContent;
    private Toolbar mToolbar;
    public static AppBarLayout appBar;
    private TextView mProfileImageMenus, mCoverImageMenus, mContentTitle, mLikeCount, mLikeLabel;
    private RelativeLayout mLikeButton;
    private Bundle bundle;
    private boolean isAdapterSet,  isCoverRequest = false;
    private ViewPager viewPager;
    private TabLayout mSlidingTabs;
    private ViewPageFragmentAdapter mViewPageFragmentAdapter;
    private TextView mToolBarTitle;
    private int siteVideoPluginEnabled, mAdvVideosCount;
    public static String sAttending, sNotAttending, sMayBeAttending;
    public static int sMembershipRequestCode = 0, sSelectedRsvpValue;
    private JSONArray coverPhotoMenuArray, profilePhotoMenuArray;
    private ArrayList<PhotoListDetails> mPhotoDetails, mProfilePhotoDetail;
    private ProgressBar mProgressBar;
    private int defaultCover, cover_photo = 0, profile_photo = 0;
    private AlertDialogWithAction mAlertDialogWithAction;
    private boolean isRedirectedFromEventProfile = false;
    private ImageLoader mImageLoader;
    private boolean isCoverProfilePictureRequest = false;
    private Integer likeID = 0;
    private boolean isFromWebPayment = false;
    private SwipeRefreshLayout swipeRefresh;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        //CHECK IF OPENED VIA URL
        checkIfOpenedViaURL();

        mContext = this;
        mPhotoDetails = new ArrayList<>();
        mProfilePhotoDetail = new ArrayList<>();
        mImageLoader = new ImageLoader(getApplicationContext());

        setContentView(R.layout.activity_profile_pages);
        /* Create Back Button On Action Bar **/
        mToolbar = findViewById(R.id.toolbar);
        mToolBarTitle = findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);

        isRedirectedFromEventProfile = intent.getBooleanExtra("isRedirectedFromEventProfile", false);
        LogUtils.LOGD(AdvEventsProfilePage.class.getSimpleName(), "isRedirectedFromEventProfile->" +isRedirectedFromEventProfile );

        mModuleName = intent.getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mContentId = intent.getExtras().getInt(ConstantVariables.VIEW_PAGE_ID);
        isFromWebPayment = intent.getBooleanExtra("isFromWebPayment", false);

        if (isFromWebPayment){
            View parentLayout = findViewById(android.R.id.content);
            SnackbarUtils.displaySnackbarLongTime(parentLayout, "You can set up your payment methods anytime by going to your event dashboard");
        }


        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(intent.getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

         /* Create Back Button On Action Bar **/
        mToolbar = findViewById(R.id.toolbar);
        mToolBarTitle = findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // TODO: Enable this again if rsvp will be applied
        // items[0] = sAttending =  mContext.getResources().getString(R.string.rsvp_filter_attending);
        // items[1] = sMayBeAttending = mContext.getResources().getString(R.string.rsvp_filter_may_be_attending);
        // items[2] = sNotAttending = mContext.getResources().getString(R.string.rsvp_filter_not_attending);

        if (intent.getExtras().containsKey(ConstantVariables.VIEW_PAGE_URL)) {
            mItemViewUrl = intent.getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        } else {
            mItemViewUrl = AppConstant.DEFAULT_URL + "advancedevents/view/"+ mContentId +"?gutter_menu=" + 1;
        }
        mItemViewUrl += "&rsvp_form=1&announcement=1";


        /*
        Getting Fields to show content
         */

        /* Create Back Button On Action Bar **/
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Header view
        mMainContent = findViewById(R.id.main_content);
        appBar = findViewById(R.id.appbar);
        appBar.addOnOffsetChangedListener(this);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        // Setup the Tabs
        viewPager = findViewById(R.id.pager);
        mSlidingTabs = findViewById(R.id.slidingTabs);
        mProgressBar = findViewById(R.id.progressBar);
        mContentTitle = findViewById(R.id.content_title);
        mCoverImage = findViewById(R.id.coverImage);
        mProfileImage = findViewById(R.id.profile_image);
        mCoverImageMenus = findViewById(R.id.cover_image_menus);
        mProfileImageMenus = findViewById(R.id.profile_image_menus);
        mLikeCount = findViewById(R.id.like_count);
        mLikeLabel = findViewById(R.id.like_label);
        mLikeButton = findViewById(R.id.like_button);
        mLikeIcon = findViewById(R.id.like_icon);
        mCoverImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mProfileImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));


        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mProfileTabs = new JSONArray();
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        //Restart App if newly logged in
        if (intent.getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false))
            mAppConst.restartApp();

        /*
        Load Data Directly if Coming from Create Page.
         */
        if (mBody != null && mBody.length() != 0) {
            isLoadingFromCreate = true;
            isContentEdited = true;
            checkSiteVideoPluginEnabled();
        }

    }

    private void checkIfOpenedViaURL() {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals("android.intent.action.VIEW")) {
                Uri uri = intent.getData();
                Integer id = Integer.valueOf(uri.getLastPathSegment());
                String url = AppConstant.buildUrl(id);
                intent.putExtra(ConstantVariables.VIEW_PAGE_URL, url);
                intent.putExtra(ConstantVariables.VIEW_PAGE_ID, id);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
            }
        }
    }

    public void makeRequest(){
        // Do not send request if coming from create page
        Log.d("ThisWasCalledAfter ", "true");
//        if(!isLoadingFromCreate){
//
//
//        }

        mAppConst.getJsonResponseFromUrl(mItemViewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                checkSiteVideoPluginEnabled();
                checkLikeStatus();
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                SnackbarUtils.displaySnackbarLongWithListener(mMainContent, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }
        });
    }

    private void checkLikeStatus() {
        if (!mAppConst.isLoggedOutUser()) {
            Log.d("ThisWasrequested ", "true");
            mLikeButton.setEnabled(false);
            String url = AppConstant.DEFAULT_URL + "advancedactivity/event-has-like?sendNotification=0";
            Map<String, String> params = new HashMap<>();
            params.put("resource_id", mContentId + "");
            params.put("resource_type", "siteevent_event");
            mAppConst.postJsonResponseForUrl(url, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    Boolean status = jsonObject.getBoolean("status");
                    likeCount = jsonObject.getInt("count_likes");
                    String likeCountString = likeCount > 1 || likeCount == 0 ? likeCount + " likes" : likeCount + " like";
                    mLikeCount.setText(likeCountString);
                    likeID = jsonObject.getInt("like_id");

                    if (status){
                        mLikeLabel.setText(R.string.unlike_label);
                        mLikeIcon.setImageDrawable(getDrawable(R.drawable.ic_thumb_down_white_24dp));
                    }else{
                        mLikeLabel.setText(R.string.like_label);
                        mLikeIcon.setImageDrawable(getDrawable(R.drawable.ic_thumb_up_white_24dp));
                    }

                    mLikeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mLikeButton.setEnabled(false);
                            String url = AppConstant.DEFAULT_URL + "advancedactivity/event-like?sendNotification=0";
                            Map<String,String> postParams = new HashMap<>();
                            postParams.put("resource_id",String.valueOf(mEventId));
                            postParams.put("resource_type","siteevent_event");
                            postParams.put("like_id",likeID+"");

                            if (likeID != 0){
                                mLikeLabel.setText(R.string.like_label);
                                mLikeIcon.setImageDrawable(getDrawable(R.drawable.ic_thumb_up_white_24dp));
                                likeCount--;
                            }else{
                                mLikeLabel.setText(R.string.unlike_label);
                                mLikeIcon.setImageDrawable(getDrawable(R.drawable.ic_thumb_down_white_24dp));
                                likeCount++;
                            }

                            String likeCountString = likeCount > 1 || likeCount == 0 ? likeCount + " likes" : likeCount + " like";
                            mLikeCount.setText(likeCountString);

                            mAppConst.postJsonResponseForUrl(url, postParams, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                    Boolean status = jsonObject.getBoolean("status");
                                    if (status) {
                                        likeID = jsonObject.getInt("like_id");
                                    }else{
                                        likeID = 0;
                                    }
                                    mLikeButton.setEnabled(true);
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mLikeButton.setEnabled(true);
                                    if (likeID != 0){
                                        mLikeLabel.setText(R.string.unlike_label);
                                        mLikeIcon.setImageDrawable(getDrawable(R.drawable.ic_thumb_down_white_24dp));
                                        likeCount++;
                                    }else{
                                        mLikeIcon.setImageDrawable(getDrawable(R.drawable.ic_thumb_up_white_24dp));
                                        mLikeLabel.setText(R.string.like_label);
                                        likeCount--;
                                    }

                                    String likeCountString = likeCount > 1 || likeCount == 0 ? likeCount + " likes" : likeCount + " like";
                                    mLikeCount.setText(likeCountString);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mLikeButton.setEnabled(true);
                }
            });
        }
    }

    private void refreshLabel(){

    }

    /**
     *  This calling will return sitevideoPluginEnabled to 1 if
     *  1. Adv Video plugin is integrated with AdvEvent plugin
     *  2. And if there is any video uploaded in this event using Avd video
     *  else it will return sitevideoPluginEnabled to 0
     */
    public void checkSiteVideoPluginEnabled() {

        String url = UrlUtil.IS_SITEVIDEO_ENABLED + "?subject_id=" + mContentId + "&subject_type=siteevent_event";
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                siteVideoPluginEnabled = jsonObject.optInt("sitevideoPluginEnabled");
                mAdvVideosCount = jsonObject.optInt("totalItemCount");
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                loadViewPageData(mBody);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                loadViewPageData(mBody);
            }
        });
    }

    private void getCoverMenuRequest() {
        String menuUrl = UrlUtil.GET_COVER_MENU_URL +"subject_id=" + mEventId +
                "&subject_type=siteevent_event&special=both&cover_photo=" + cover_photo + "&profile_photo=" + profile_photo;

        mAppConst.getJsonResponseFromUrl(menuUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                JSONObject mResponseObject = jsonObject.optJSONObject("response");

                setCoverMenu(mResponseObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
            }
        });

    }

    private void setCoverMenu(JSONObject mResponseObject) {

        if (mResponseObject != null) {

            coverPhotoMenuArray = mResponseObject.optJSONArray("coverPhotoMenu");
            profilePhotoMenuArray = mResponseObject.optJSONArray("profilePhotoMenu");

            if (coverPhotoMenuArray != null && coverPhotoMenuArray.length() > 0) {
                mCoverImageMenus.setVisibility(View.VISIBLE);
                mCoverImageMenus.setText("\uf030");
            }

            if (profilePhotoMenuArray != null && profilePhotoMenuArray.length() > 0) {
                mProfileImageMenus.setVisibility(View.VISIBLE);
                mProfileImageMenus.setText("\uf030");
            }
        }

    }

    private void loadViewPageData(JSONObject bodyJsonObject){

        isLoadingFromCreate = false;
        mProgressBar.setVisibility(View.GONE);
        appBar.setVisibility(View.VISIBLE);

        if(bodyJsonObject != null && bodyJsonObject.length() != 0) {
            try {

                mDataResponse = bodyJsonObject.getJSONObject("response");
                mGutterMenus = bodyJsonObject.getJSONArray("gutterMenu");

                /*
                Check Invite Option exist in GutterMenus or not to add + icon on Members Page
                 */
                String messageOwnerUrl = null, messageOwnerTitle = null;
                if (mGutterMenus != null) {
                    for (int i = 0; i < mGutterMenus.length(); i++) {
                        JSONObject menuJsonObject = mGutterMenus.getJSONObject(i);
                        String menuUrl = menuJsonObject.optString("url");

                        if (menuUrl.contains("advancedevents/member/invite")) {
                            mInviteGuestUrl = AppConstant.DEFAULT_URL + menuUrl;
                        }

                        if (menuUrl.contains("messageowner")) {
                            messageOwnerUrl = AppConstant.DEFAULT_URL + menuUrl;
                            messageOwnerTitle = menuJsonObject.optString("label");
                        }

                        if (menuUrl.contains("advancedevents/member/request/")) {
                            sMembershipRequestCode = ConstantVariables.REQUEST_INVITE;

                        } else if (menuUrl.contains("advancedevents/member/cancel")) {
                            sMembershipRequestCode = ConstantVariables.CANCEL_INVITE;

                        } else if (menuUrl.contains("advancedevents/member/join")) {
                            sMembershipRequestCode = ConstantVariables.JOIN_EVENT;

                        } else if (menuUrl.contains("advancedevents/member/leave")) {
                            sMembershipRequestCode = ConstantVariables.LEAVE_EVENT;

                        } else if (menuUrl.contains("advancedevents/waitlist/join")) {
                            sMembershipRequestCode = ConstantVariables.EVENT_WAIT_LIST;

                        } else if (menuUrl.contains("advancedevents/member/accept")) {
                            sMembershipRequestCode = 0;

                        } else if (menuUrl.contains("advancedevents/member/reject")) {
                            sMembershipRequestCode = 0;
                        } else if (menuUrl.contains("advancedeventtickets/tickets/tickets-buy")) {
                            sMembershipRequestCode = ConstantVariables.BOOK_EVENT_TICKETS;
                        }
                    }
                    invalidateOptionsMenu();
                }

                /*
                Fetch Data from Response
                 */

                mEventId = mDataResponse.getInt("event_id");
                title = mDataResponse.getString("title");
                profileImageUrl = mDataResponse.getString("image");
                coverImageUrl = mDataResponse.optString("cover_image");
                mContentUrl = mDataResponse.optString("content_url");
                mOccurrenceId = mDataResponse.optInt("occurrence_id");
                location = mDataResponse.getString("location");
                startTime = mDataResponse.getString("starttime");
                endTime = mDataResponse.getString("endtime");
                isClosed = mDataResponse.optInt("closed");
                sSelectedRsvpValue = mDataResponse.optInt("rsvp");
                likeCount = mDataResponse.optInt("like_count");
                String likeCountString = likeCount > 1 || likeCount == 0 ? likeCount + " likes" : likeCount + " like";
                if (mAppConst.isLoggedOutUser()) {
                    mLikeCount.setText(likeCountString);
                }else{
                    mLikeButton.setVisibility(View.VISIBLE);
                    mLikeButton.setEnabled(true);
                }
                mLikeCount.setVisibility(View.VISIBLE);

                mBrowseList = new BrowseListItems(mEventId, mOccurrenceId, title, profileImageUrl, mContentUrl, isClosed);

                // Used for message.
                mBrowseList.setUserId(mDataResponse.optInt("owner_id"));
                mBrowseList.setUserDisplayName(mDataResponse.optString("owner_title"));
                mBrowseList.setUserProfileImageUrl(mDataResponse.optString("owner_image_normal"));
                mBrowseList.setMessageOwnerUrl(messageOwnerUrl);
                mBrowseList.setMessageOwnerTitle(messageOwnerTitle);

                JSONArray profileTabs = bodyJsonObject.getJSONArray("profile_tabs");

                // Add Info Tab in array on 1st position
                if (profileTabs != null) {
                    JSONObject InfoTabJsonObject = new JSONObject();
                    InfoTabJsonObject.put("totalItemCount", 0);
                    InfoTabJsonObject.put("label", getResources().getString(R.string.action_bar_title_info));
                    InfoTabJsonObject.put("name", "info");
                    mProfileTabs.put(1, InfoTabJsonObject);

                    // Add All the tabs in mProfileTabs
                    for(int i = 0; i < profileTabs.length(); i++){
                        JSONObject tabObject = profileTabs.getJSONObject(i);
                        if(i == 0){
                            mProfileTabs.put(i, tabObject);
                        }else{
                            mProfileTabs.put(i + 1, tabObject);
                        }
                    }

                    bundle = new Bundle();
                    bundle.putString(ConstantVariables.RESPONSE_OBJECT, bodyJsonObject.toString());
                    bundle.putInt(ConstantVariables.CONTENT_ID, mContentId);
                    bundle.putString(ConstantVariables.CONTENT_TITLE, title);
                    bundle.putInt(ConstantVariables.CAN_UPLOAD, mDataResponse.optInt("canUpload"));
                    bundle.putString(ConstantVariables.SUBJECT_TYPE, "siteevent_event");
                    bundle.putInt(ConstantVariables.SUBJECT_ID, mEventId);
                    bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
                    bundle.putString(ConstantVariables.VIEW_PAGE_URL, mItemViewUrl);
                    bundle.putInt(ConstantVariables.VIEW_PAGE_ID, mContentId);
                    bundle.putString(ConstantVariables.INVITE_GUEST, mInviteGuestUrl);
                    bundle.putInt(ConstantVariables.ADV_VIDEO_INTEGRATED, siteVideoPluginEnabled);
                    bundle.putInt(ConstantVariables.ADV_VIDEOS_COUNT, mAdvVideosCount);

                    if (!isAdapterSet) {

                        mProfileTabSize = mProfileTabs.length();
                        // Setup the viewPager
                        mViewPageFragmentAdapter = new ViewPageFragmentAdapter(mContext,
                                getSupportFragmentManager(), mProfileTabs, bundle);
                        viewPager.setAdapter(mViewPageFragmentAdapter);
                        viewPager.setOffscreenPageLimit(mViewPageFragmentAdapter.getCount() + 1);
                        // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
                        mSlidingTabs.setupWithViewPager(viewPager);

                        isAdapterSet = true;
                    } else {
                        mViewPageFragmentAdapter.updateData(bundle, mProfileTabs,
                                (mProfileTabSize == mProfileTabs.length()), false);
                        // If any tab is added/removed then again set the adapter.
                        if (mProfileTabSize != mProfileTabs.length()) {
                            mProfileTabSize = mProfileTabs.length();
                            viewPager.setAdapter(mViewPageFragmentAdapter);
                        }
                    }
                }

                if (viewPager != null){
                    viewPager.setCurrentItem(1);
                }

                mViewPageFragmentAdapter.checkForMessageOwnerOption(mBrowseList);

                // Set values in views


                // If default_cover image is coming then showing user cover image plugin views.
                if (mDataResponse.has("default_cover")) {
                    mProfileImage.setVisibility(View.VISIBLE);
                    defaultCover = mDataResponse.optInt("default_cover");

                    //Showing profile image.
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        mProfileImage.setVisibility(View.VISIBLE);
                        mImageLoader.setCoverImageUrl(profileImageUrl, mProfileImage);
                    }

                    //Showing Cover image.
                    if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                        cover_photo = defaultCover == 1 ? 0 : 1;
                        mImageLoader.setCoverImageUrl(coverImageUrl, mCoverImage);
                    }
                    profile_photo = mDataResponse.has("photo_id") ? 1 : 0;
                    getCoverMenuRequest();

                    mProfilePhotoDetail.clear();
                    mPhotoDetails.clear();

                    mProfilePhotoDetail.add(new PhotoListDetails(profileImageUrl));
                    mPhotoDetails.add(new PhotoListDetails(coverImageUrl));

                } else {
                    mImageLoader.setImageUrl(profileImageUrl, mCoverImage);
                    mPhotoDetails.clear();
                    mPhotoDetails.add(new PhotoListDetails(profileImageUrl));
                }

                mContentTitle.setText(title);
                collapsingToolbar.setTitle(title);
                mToolBarTitle.setText(title);
                CustomViews.setCollapsingToolBarTitle(collapsingToolbar);

                mCoverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PreferencesUtils.getSiteContentCoverPhotoEnabled(mContext) == 1 &&
                                coverPhotoMenuArray != null && coverPhotoMenuArray.length() > 0) {
                            isCoverRequest = true;
                            mPhotoMenuTitle = getResources().getString(R.string.choose_cover_photo);
                            mGutterMenuUtils.showPopup(mCoverImageMenus, coverPhotoMenuArray,
                                    mBrowseList, ConstantVariables.ADV_EVENT_MENU_TITLE);
                        } else {
                            openLightBox(true);
                        }

                    }
                });

                mProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (profileImageUrl != null && !profileImageUrl.isEmpty() &&
                                profilePhotoMenuArray != null && profilePhotoMenuArray.length() > 0) {
                            isCoverRequest = false;
                            mPhotoMenuTitle = getResources().getString(R.string.choose_profile_photo);
                            mGutterMenuUtils.showPopup(mProfileImageMenus, profilePhotoMenuArray,
                                    mBrowseList, ConstantVariables.ADV_EVENT_MENU_TITLE);
                        } else {
                            openLightBox(false);
                        }
                    }
                });

                mLikeCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(AdvEventsProfilePage.this,EventLikesActivity.class);
                        i.putExtra(ConstantVariables.VIEW_PAGE_URL,mItemViewUrl);
                        startActivity(i);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        }else{
            if(mGutterMenus != null) {
                mGutterMenuUtils.onMenuOptionItemSelected(mMainContent, findViewById(item.getItemId()),
                        id, mGutterMenus);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Method to open photolightbox when user click on view image
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
        i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mModuleName);
        i.putExtras(bundle);
        startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);

    }

    private void startImageUploading() {
        isCoverProfilePictureRequest = false;
        Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
        intent.putExtra("selection_mode", true);
        intent.putExtra(ConstantVariables.IS_PHOTO_UPLOADED, true);
        startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.setEventId(mEventId);
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, mModuleName, mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantVariables.PACKAGE_WEBVIEW_CODE:
                isLoadingFromCreate = false;
                Log.d("makeRequest 1 ", "true");
                makeRequest();
                break;

            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                if (resultCode == ConstantVariables.VIEW_PAGE_EDIT_CODE) {
                    isLoadingFromCreate = false;
                    isContentEdited = true;
                    Log.d("makeRequest 2 ", "true");
                    makeRequest();
                }
                break;

            case ConstantVariables.CREATE_REQUEST_CODE:
            case ConstantVariables.PAGE_EDIT_CODE:
                if (mViewPageFragmentAdapter != null && (resultCode == ConstantVariables.CREATE_REQUEST_CODE
                        || resultCode == ConstantVariables.PAGE_EDIT_CODE)) {
                    mViewPageFragmentAdapter.updateData(bundle, mProfileTabs, false, true);
                }
                if (requestCode == ConstantVariables.PAGE_EDIT_CODE
                        && resultCode == ConstantVariables.PAGE_EDIT_CODE && data != null) {
                    ArrayList<String> resultList = data.getStringArrayListExtra(ConstantVariables.PHOTO_LIST);
                    String postUrl = UrlUtil.UPLOAD_COVER_PHOTO_URL + "subject_type=siteevent_event" +
                            "&subject_id=" + mEventId;
                    if (isCoverRequest) {
                        successMessage = mContext.getResources().getString(R.string.cover_photo_updated);
                    } else {
                        postUrl = postUrl + "&special=profile";
                        successMessage = mContext.getResources().getString(R.string.profile_photo_updated);
                    }
                    new UploadFileToServerUtils(AdvEventsProfilePage.this, postUrl, resultList, this).execute();
                }
                break;


            case ConstantVariables.FEED_REQUEST_CODE:
                /* Reset the view pager adapter if any status is posted using AAF */
                if (resultCode == ConstantVariables.FEED_REQUEST_CODE && viewPager != null) {
                    viewPager.setAdapter(mViewPageFragmentAdapter);
                }
                break;

            case ConstantVariables.VIEW_PAGE_CODE:
                if (resultCode == ConstantVariables.ADD_VIDEO_CODE) {
                    isAdapterSet = false;
                    Log.d("makeRequest 3 ", "true");
                    makeRequest();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            if(isContentDeleted && isRedirectedFromEventProfile){
                sendToEventBrowsePage();
            }else if(isContentEdited || isContentDeleted){
                Intent intent = new Intent();
                setResult(ConstantVariables.VIEW_PAGE_CODE, intent);

            }else if(isRedirectedFromEventProfile){
                sendToEventBrowsePage();
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        if (intent.getData() != null){
            Intent intent = new Intent(AdvEventsProfilePage.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void sendToEventBrowsePage(){
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("isRedirectedFromEventProfile", isRedirectedFromEventProfile);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("makeRequest 4 ", "true");
        makeRequest();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        CustomViews.showMarqueeTitle(verticalOffset, collapsingToolbar, mToolbar, mToolBarTitle, title);
    }

    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mMainContent, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        isContentDeleted = true;
                        onBackPressed();
                    }
                });
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseList = (BrowseListItems) itemList;

        switch (menuName) {

            case "request_invite":
            case "cancel_invite":
                sMembershipRequestCode = (sMembershipRequestCode == 1 ? 2 : 1);
                break;

            case "leave":
            case "join":
                sMembershipRequestCode = (sMembershipRequestCode == 3 ? 4 : 3);
                break;

            case "close":
                break;

            case "view_profile_photo":
            case "view_cover_photo":
                openLightBox(isCoverRequest);
                break;

            case "upload_cover_photo":
            case "upload_photo":
                isCoverProfilePictureRequest = true;
                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    startImageUploading();
                }
                break;

            case "choose_from_album":
                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "photos");
                bundle.putString(ConstantVariables.CONTENT_TITLE, mPhotoMenuTitle);
                bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                bundle.putInt("subject_id", mEventId);
                bundle.putBoolean("isCoverRequest", isCoverRequest);
                bundle.putBoolean("isCoverChange", true);
                bundle.putString(ConstantVariables.URL_STRING, AppConstant.DEFAULT_URL + "advancedevents/photo/list/" + mEventId);
                bundle.putString(ConstantVariables.SUBJECT_TYPE, "siteevent_event");
                bundle.putInt(ConstantVariables.SUBJECT_ID, mEventId);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_EVENT_MENU_TITLE);
                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, ConstantVariables.PAGE_EDIT_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "remove_cover_photo":
            case "remove_photo":
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.bringToFront();
                Log.d("makeRequest 5 ", "true");
                makeRequest();
                break;

            case "book_now":
                String mRequestUrl = AppConstant.DEFAULT_URL + "advancedeventtickets/tickets/tickets-buy?event_id=" + String.valueOf(mEventId);

                Intent buyIntent = new Intent(mContext, AdvEventsBuyTicketsInfo.class);
                buyIntent.putExtra(ConstantVariables.URL_STRING, mRequestUrl);
                buyIntent.putExtra(ConstantVariables.TITLE, title);
                buyIntent.putExtra("occurrence_id", String.valueOf(mOccurrenceId));
                buyIntent.putExtra("location", location);
                buyIntent.putExtra("starttime", startTime);
                buyIntent.putExtra("endtime", endTime);
                ((Activity) mContext).startActivityForResult(buyIntent, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            default:
                sMembershipRequestCode = 0;
                finish();
                startActivity(intent);
                break;
        }

        //Update the data inside fragment.
        if (mViewPageFragmentAdapter != null) {
            mViewPageFragmentAdapter.updateData(bundle, mProfileTabs, true, false);
        }
    }

    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        if (isRequestSuccessful) {
            SnackbarUtils.displaySnackbarLongTime(mMainContent, successMessage);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
            Log.d("makeRequest 6 ", "true");
            makeRequest();

        } else {
            SnackbarUtils.displaySnackbarLongTime(mMainContent, jsonObject.optString("message"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    if (isCoverProfilePictureRequest) {
                        startImageUploading();
                    } else {
                        PhotoFragment fragment = (PhotoFragment) mViewPageFragmentAdapter.getFragment(mViewPageFragmentAdapter.mPhotoTabPosition);
                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }else{
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
    public void onRefresh() {



        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
//                isAdLoaded = false;
                makeRequest();
            }
        });
    }
}
