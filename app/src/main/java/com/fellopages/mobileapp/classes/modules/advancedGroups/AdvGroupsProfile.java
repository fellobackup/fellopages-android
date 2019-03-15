/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.modules.advancedGroups;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.PhotoUploadingActivity;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.adapters.ViewPageFragmentAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.fragments.FragmentUtils;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUserReviewDeleteListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SplitToolbar;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvGroupsProfile extends AppCompatActivity implements View.OnClickListener,
        AppBarLayout.OnOffsetChangedListener, OnOptionItemClickResponseListener, OnUserReviewDeleteListener,
        OnUploadResponseListener, View.OnLongClickListener {

    private Context mContext;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private CollapsingToolbarLayout collapsingToolbar;
    private CoordinatorLayout mMainContent;
    private ViewPager viewPager;
    private TabLayout mSlidingTabs;
    private ViewPageFragmentAdapter mViewPageFragmentAdapter;
    private ImageView mCoverImage, mProfileImage;
    private String groupProfileUrl, mContentUrl;
    private String successMessage, title, coverImage;
    private int mContentId, isGroupClosed = 0, likeCount, commentCount, group_id, mProfileTabSize;
    private boolean isLoadingFromCreate = false, isAdapterSet = false, isLiked = false;
    private JSONObject mBody;
    private JSONArray mGutterMenus, mProfileTabs;
    public static AppBarLayout appBar;
    Bundle bundle;
    private TextView mLikeCountTextView, mCommentCountTextView, mLikeUnlikeText;
    private LinearLayout mLikeCommentContent;
    private SplitToolbar mBottomToolBar;
    private Typeface fontIcon;
    private float initialY;
    private ArrayList<PhotoListDetails> mCoverImageDetails;
    private int mMembershipRequestCode = 0, isGroupJoined = 0;
    private boolean showAddPeople = true, isGroupPublished = false;
    private String mInviteGuestUrl, profileImageUrl, coverImageUrl;
    private boolean isContentEdited = false, isCoverRequest;
    private Toolbar mToolbar;
    private TextView mToolBarTitle;
    private JSONObject mReactionsObject, myReaction, mAllReactionObject, mContentReactions;
    private int mReactionsEnabled;
    private ImageView mReactionIcon;
    private List<ImageViewList> reactionsImages;
    private String mLikeUnlikeUrl;
    private int siteVideoPluginEnabled, mAdvVideosCount;
    public static int sIsGroupFollowed = 0;
    private TextView mProfileImageMenus, mCoverImageMenus, mContentTitle;
    private JSONArray coverPhotoMenuArray, profilePhotoMenuArray;
    private ArrayList<PhotoListDetails> mPhotoDetails, mProfilePhotoDetail;
    private ProgressBar mProgressBar;
    private int defaultCover, cover_photo = 0, profile_photo = 0;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ArrayList<JSONObject> mReactionsArray;
    private ImageLoader mImageLoader;
    private String sendLikeNotificationUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pages);

        mContext = this;
        mAppConst = new AppConstant(mContext);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mCoverImageDetails = new ArrayList<>();
        FragmentUtils.setOnUserReviewDeleteListener(this);
        mPhotoDetails = new ArrayList<>();
        mProfilePhotoDetail = new ArrayList<>();
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(getApplicationContext());

        /* Create Back Button On Action Bar **/
        mToolbar = findViewById(R.id.toolbar);
        mToolBarTitle = findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
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
        mCoverImage = findViewById(R.id.coverImage);
        mContentTitle = findViewById(R.id.content_title);
        mProfileImage = findViewById(R.id.profile_image);
        mCoverImageMenus = findViewById(R.id.cover_image_menus);
        mProfileImageMenus = findViewById(R.id.profile_image_menus);
        mCoverImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mProfileImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));


        //Footer view.
            /*
                Like and Unlike Fields...
            */

        mLikeCountTextView = findViewById(R.id.likeCount);
        mCommentCountTextView = findViewById(R.id.commentCount);
        mReactionIcon = findViewById(R.id.reactionIcon);
        mLikeUnlikeText = findViewById(R.id.likeUnlikeText);

        LinearLayout mLikeBlock = findViewById(R.id.likeBlock);
        LinearLayout mCommentBlock = findViewById(R.id.commentBlock);
        LinearLayout mLikeCommentBlock = findViewById(R.id.likeCommentBlock);

        mLikeBlock.setOnClickListener(this);
        mLikeBlock.setOnLongClickListener(this);
        mCommentBlock.setOnClickListener(this);
        mLikeCommentBlock.setOnClickListener(this);

        mLikeCommentContent = findViewById(R.id.likeCommentContent);
        mBottomToolBar = findViewById(R.id.toolbarBottom);


        /** Getting Intent Key's. **/
        mContentId = getIntent().getIntExtra(ConstantVariables.VIEW_PAGE_ID, 0);
        // View Page Url
        groupProfileUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        mReactionsEnabled = PreferencesUtils.getReactionsEnabled(mContext);

        /*
            Check if Reactions and nested comment plugin is enabled on the site
            send request to get the reactions on a particular content
            send this request only if the reactions Enabled is not saved yet in Preferences
             or if it is set to 1
         */
        if (mReactionsEnabled == 1 || mReactionsEnabled == -1) {
            String getContentReactionsUrl = UrlUtil.CONTENT_REACTIONS_URL + "&subject_type=sitegroup_group" +
                    "&subject_id=" + mContentId;
            mAppConst.getJsonResponseFromUrl(getContentReactionsUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mReactionsObject = jsonObject;
                    JSONObject reactionsData = mReactionsObject.optJSONObject("reactions");
                    mContentReactions = mReactionsObject.optJSONObject("feed_reactions");
                    if (reactionsData != null) {
                        mReactionsEnabled = reactionsData.optInt("reactionsEnabled");
                        PreferencesUtils.updateReactionsEnabledPref(mContext, mReactionsEnabled);
                        mAllReactionObject = reactionsData.optJSONObject("reactions");
                        if(mAllReactionObject != null){
                            mReactionsArray = GlobalFunctions.sortReactionsObjectWithOrder(mAllReactionObject);
                        }
                    }

                    // Load Data Directly if Coming from Create Page.
                    if (mBody != null && mBody.length() != 0) {
                        isLoadingFromCreate = true;
                        isContentEdited = true;
                        checkSiteVideoPluginEnabled();
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    // Load Data Directly if Coming from Create Page.
                    if (mBody != null && mBody.length() != 0) {
                        isLoadingFromCreate = true;
                        isContentEdited = true;
                        checkSiteVideoPluginEnabled();
                    }
                }
            });
        } else {
            // Load Data Directly if Coming from Create Page.
            if (mBody != null && mBody.length() != 0) {
                isLoadingFromCreate = true;
                isContentEdited = true;
                checkSiteVideoPluginEnabled();
            }
        }
    }

    /**
     * Method to get response of profile page.
     */
    public void makeRequest(){

        // Do not send request if coming from create page
        if(!isLoadingFromCreate){
            mAppConst.getJsonResponseFromUrl(groupProfileUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBody = jsonObject;
                    checkSiteVideoPluginEnabled();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
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
    }

    private void getCoverMenuRequest() {
        String menuUrl = UrlUtil.GET_COVER_MENU_URL +"subject_id=" + group_id +
                "&subject_type=sitegroup_group&special=both&cover_photo=" + cover_photo + "&profile_photo=" + profile_photo;

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


    /**
     *  This calling will return sitevideoPluginEnabled to 1 if
     *  1. Adv Video plugin is integrated with Directory/Pages plugin
     *  2. And if there is any video uploaded in this page using Avd video
     *  else it will return sitevideoPluginEnabled to 0
     */
    public void checkSiteVideoPluginEnabled(){

        String url = UrlUtil.IS_SITEVIDEO_ENABLED + "?subject_id=" + mContentId + "&subject_type=sitegroup_group";
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                siteVideoPluginEnabled = jsonObject.optInt("sitevideoPluginEnabled");
                mAdvVideosCount = jsonObject.optInt("totalItemCount");
                loadViewPageData(mBody);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                loadViewPageData(mBody);
            }
        });

    }

    /**
     * Method to get view page data.
     * @param bodyJsonObject jsonObject, which contains view page data.
     */
    private void loadViewPageData(JSONObject bodyJsonObject) {
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        if (bodyJsonObject != null && bodyJsonObject.length() != 0) {
            try {
                mGutterMenus = bodyJsonObject.optJSONArray("gutterMenu");
                JSONObject responseObject = bodyJsonObject.optJSONObject("response");
                title = responseObject.optString("title");
                group_id = responseObject.optInt("group_id");
                coverImage = responseObject.optString("image");
                mContentUrl = responseObject.optString("content_url");
                isGroupClosed = responseObject.optInt("closed");
                isLiked = responseObject.optBoolean("is_like");
                profileImageUrl = responseObject.getString("image");
                coverImageUrl = responseObject.optString("cover_image");


                likeCount = responseObject.optInt("like_count");
                commentCount = responseObject.optInt("comment_count");
                sIsGroupFollowed = responseObject.optInt("isGroupFollowed");

                if (mGutterMenus != null) {

                    for (int i = 0; i < mGutterMenus.length(); i++) {
                        JSONObject menuJsonObject = mGutterMenus.getJSONObject(i);
                        String menuUrl = menuJsonObject.optString("url");

                        if (menuUrl.contains("advancedgroups/member/invite-members/"))
                            mInviteGuestUrl = AppConstant.DEFAULT_URL + menuUrl;

                        if (menuUrl.contains("advancedgroups/member/request/")) {
                            mMembershipRequestCode = 0;
                        } else if (menuUrl.contains("advancedgroups/member/cancel/")) {
                            mMembershipRequestCode = 1;
                        }

                        if(menuUrl.contains("advancedgroups/member/leave")){
                            isGroupJoined = 1;
                        }else if(menuUrl.contains("advancedgroups/member/join")){
                            isGroupJoined = 0;
                        }
                    }
                    invalidateOptionsMenu();
                }
                mProfileTabs = bodyJsonObject.optJSONArray("profile_tabs");

                mBrowseList = new BrowseListItems(group_id, title, coverImage, mContentUrl,
                        isGroupClosed, isGroupJoined, mMembershipRequestCode,
                        showAddPeople, isGroupPublished);

                bundle = new Bundle();
                bundle.putString(ConstantVariables.RESPONSE_OBJECT, bodyJsonObject.toString());
                bundle.putInt(ConstantVariables.CONTENT_ID, mContentId);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_GROUPS_MENU_TITLE);
                bundle.putString(ConstantVariables.INVITE_GUEST, mInviteGuestUrl);
                bundle.putInt(ConstantVariables.ADV_VIDEO_INTEGRATED, siteVideoPluginEnabled);
                bundle.putInt(ConstantVariables.ADV_VIDEOS_COUNT, mAdvVideosCount);
                bundle.putString(ConstantVariables.SUBJECT_TYPE, "sitegroup_group");
                bundle.putInt(ConstantVariables.SUBJECT_ID, mContentId);
                bundle.putInt(ConstantVariables.VIEW_PAGE_ID, mContentId);
                bundle.putString(ConstantVariables.CONTENT_TITLE, title);
                bundle.putBoolean(ConstantVariables.IS_WAITING, false);

                if (!isAdapterSet) {
                    // Setup the viewPager
                    mProfileTabSize = mProfileTabs.length();
                    mViewPageFragmentAdapter = new ViewPageFragmentAdapter(mContext,
                            getSupportFragmentManager(), mProfileTabs, bundle);
                    viewPager.setAdapter(mViewPageFragmentAdapter);
                    viewPager.setOffscreenPageLimit(mViewPageFragmentAdapter.getCount() + 1);
                    // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
                    mSlidingTabs.setupWithViewPager(viewPager);

                    // Showing the alert dialog once when page is closed.
                    if (isGroupClosed == 1) {
                        mAlertDialogWithAction.showDialogForClosedContent(title,
                                mContext.getResources().getString(R.string.group_closed_msg));
                    }
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

                if (viewPager != null) {
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(int position) {
                            if (mBottomToolBar != null && mAppConst != null) {
                                if (viewPager.getCurrentItem() == 0 && !mAppConst.isLoggedOutUser()) {
                                    mBottomToolBar.setVisibility(View.VISIBLE);
                                } else {
                                    mBottomToolBar.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    });
                }

                //Setting data in views
                mContentTitle.setText(title);
                collapsingToolbar.setTitle(title);
                mToolBarTitle.setText(title);
                CustomViews.setCollapsingToolBarTitle(collapsingToolbar);

                // If default_cover image is coming then showing user cover image plugin views.
                if (responseObject.has("default_cover")) {
                    mProfileImage.setVisibility(View.VISIBLE);
                    //Showing content title
                    mContentTitle.setText(title);

                    defaultCover = responseObject.optInt("default_cover");

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
                    profile_photo = responseObject.has("photo_id") ? 1 : 0;
                    getCoverMenuRequest();

                    mProfilePhotoDetail.clear();
                    mPhotoDetails.clear();

                    mProfilePhotoDetail.add(new PhotoListDetails(profileImageUrl));
                    mPhotoDetails.add(new PhotoListDetails(coverImageUrl));

                } else {
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        mImageLoader.setImageUrl(profileImageUrl, mCoverImage);
                    }
                    mPhotoDetails.add(new PhotoListDetails(profileImageUrl));
                }

                mCoverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PreferencesUtils.getSiteContentCoverPhotoEnabled(mContext) == 1 &&
                                coverPhotoMenuArray != null && coverPhotoMenuArray.length() > 0) {
                            isCoverRequest = true;
                            mGutterMenuUtils.showPopup(mCoverImageMenus, coverPhotoMenuArray,
                                    mBrowseList, ConstantVariables.ADVANCED_GROUPS_MENU_TITLE);
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
                            mGutterMenuUtils.showPopup(mProfileImageMenus, profilePhotoMenuArray,
                                    mBrowseList, ConstantVariables.ADVANCED_GROUPS_MENU_TITLE);
                        } else {
                            openLightBox(false);
                        }
                    }
                });

                setLikeAndCommentCount();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_GROUPS_MENU_TITLE);
        i.putExtras(bundle);
        startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);

    }

    private void startImageUploading() {
        Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
        intent.putExtra("selection_mode", true);
        intent.putExtra(ConstantVariables.IS_PHOTO_UPLOADED, true);
        startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
    }

    /**
     * Method to set like and comment count in bottom bar.
     */
    public void setLikeAndCommentCount() {

        if(mAppConst.isLoggedOutUser() || (viewPager != null && viewPager.getCurrentItem() != 0)){
            mBottomToolBar.setVisibility(View.GONE);
        }else{
            mBottomToolBar.setVisibility(View.VISIBLE);
            // setting up the values in views
            mLikeCommentContent.setVisibility(View.VISIBLE);

            mLikeUnlikeText.setActivated(isLiked);
            if(!isLiked){
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            }else{
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }

            /*
            Set Like and Comment Count
             */
            // Check if Reactions is enabled, show that content reaction and it's icon here.
            if(mReactionsEnabled == 1 && mReactionsObject != null && mReactionsObject.length() != 0){


                myReaction = mReactionsObject.optJSONObject("my_feed_reaction");

                if(myReaction != null && myReaction.length() != 0){
                    mLikeUnlikeText.setText(myReaction.optString("caption"));
                    String reactionImage = myReaction.optString("reaction_image_icon");
                    mImageLoader.setImageUrl(reactionImage, mReactionIcon);
                    mReactionIcon.setVisibility(View.VISIBLE);
                    mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null);
                } else {
                    mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                            null, null, null);
                    mReactionIcon.setVisibility(View.GONE);
                    mLikeUnlikeText.setText(getString(R.string.like_text));
                }

            } else{
                mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                mLikeUnlikeText.setText(getString(R.string.like_text));
            }

            mLikeCountTextView.setText(String.valueOf(likeCount));
            mCommentCountTextView.setText(String.valueOf(commentCount));
        }
    }

    private void toolbarAnimateShow() {
        mBottomToolBar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                });
    }

    private void toolbarAnimateHide() {
        mBottomToolBar.animate()
                .translationY(mBottomToolBar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                float finalY = event.getY();
                // When both position equal then don't take any action.
                if (initialY != finalY) {
                    if (initialY < finalY) {
                        toolbarAnimateShow();
                    } else if ((initialY - finalY) > 80){
                        // Hide only when user scroll.
                        toolbarAnimateHide();
                    }
                }
                break;
        }

        return super.dispatchTouchEvent(event);
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
        } else {
            if (mGutterMenus != null) {
                mGutterMenuUtils.onMenuOptionItemSelected(mMainContent, findViewById(item.getItemId()), id,
                        mGutterMenus);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.ADV_GROUPS_MENU_TITLE,
                    mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            if (isContentEdited) {
                Intent intent = new Intent();
                setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case ConstantVariables.ADV_GROUPS_INVITE_REQUEST:
                showAddPeople = true;
                break;

            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                isContentEdited = true;
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (data != null) {
                    commentCount = data.getIntExtra(ConstantVariables.PHOTO_COMMENT_COUNT, commentCount);
                    mCommentCountTextView.setText(String.valueOf(commentCount));
                }
                break;

            case ConstantVariables.PAGE_EDIT_CODE:
            case ConstantVariables.CREATE_REQUEST_CODE:
                if (mViewPageFragmentAdapter != null) {
                    mViewPageFragmentAdapter.updateData(bundle, mProfileTabs, false, true);
                }
                if (requestCode == ConstantVariables.PAGE_EDIT_CODE
                        && resultCode == ConstantVariables.PAGE_EDIT_CODE && data != null) {
                    ArrayList<String> resultList = data.getStringArrayListExtra(ConstantVariables.PHOTO_LIST);
                    String postUrl = UrlUtil.UPLOAD_COVER_PHOTO_URL + "subject_type=sitegroup_group" +
                            "&subject_id=" + group_id;
                    if (isCoverRequest) {
                        successMessage = mContext.getResources().getString(R.string.cover_photo_updated);
                    } else {
                        postUrl = postUrl + "&special=profile";
                        successMessage = mContext.getResources().getString(R.string.profile_photo_updated);
                    }
                    new UploadFileToServerUtils(AdvGroupsProfile.this, postUrl, resultList, this).execute();

                }
                break;

            case ConstantVariables.FEED_REQUEST_CODE:
                /* Reset the view pager adapter if any status is posted using AAF */
                if(viewPager != null) {
                    viewPager.setAdapter(mViewPageFragmentAdapter);
                }
                break;

            case ConstantVariables.VIEW_PAGE_CODE:
                if (requestCode == ConstantVariables.ADD_VIDEO_CODE) {
                    isAdapterSet = false;
                    makeRequest();
                }
                break;
        }
        isLoadingFromCreate = false;

        if(requestCode == ConstantVariables.FRAGMENT_LOAD_CODE){
            PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.ADV_GROUPS_MENU_TITLE);
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.likeBlock:
                int reactionId = 0;
                String reactionIcon = null, caption = null;

                if(mReactionsEnabled ==  1 && mAllReactionObject != null ){
                    reactionId = mAllReactionObject.optJSONObject("like").optInt("reactionicon_id");
                    reactionIcon = mAllReactionObject.optJSONObject("like").optJSONObject("icon").
                            optString("reaction_image_icon");
                    caption = mContext.getResources().getString(R.string.like_text);
                }
                doLikeUnlike(null, false, reactionId, reactionIcon, caption);
                break;

            case R.id.commentBlock:
            case R.id.likeCommentBlock:
                String mLikeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=sitegroup_group" +
                        "&subject_id=" + group_id + "&viewAllComments=1&page=1&limit=20";
                intent = new Intent(mContext, Comment.class);
                intent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                intent.putExtra(ConstantVariables.SUBJECT_TYPE, "sitegroup_group");
                intent.putExtra(ConstantVariables.SUBJECT_ID, group_id);
                intent.putExtra("commentCount", commentCount);
                intent.putExtra("reactionsEnabled", mReactionsEnabled);
                if(mContentReactions != null){
                    intent.putExtra("popularReactions", mContentReactions.toString());
                }
                startActivityForResult(intent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.coverImage:
                Bundle bundle = new Bundle();
                bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mCoverImageDetails);
                Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
                i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
                i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
                i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_GROUPS_MENU_TITLE);
                i.putExtras(bundle);
                startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);

                break;

        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        CustomViews.showMarqueeTitle(verticalOffset, collapsingToolbar, mToolbar, mToolBarTitle, title);
    }

    @Override
    public boolean onLongClick(View v) {
        int[] location = new int[2];
        mBottomToolBar.getLocationOnScreen(location);
        RecyclerView reactionsRecyclerView = new RecyclerView(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reactionsRecyclerView.setHasFixedSize(true);
        reactionsRecyclerView.setLayoutManager(linearLayoutManager);
        reactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final PopupWindow popUp = new PopupWindow(reactionsRecyclerView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape));
        popUp.setTouchable(true);
        popUp.setFocusable(true);
        popUp.setOutsideTouchable(true);
        popUp.setAnimationStyle(R.style.customDialogAnimation);

        // Playing popup effect when user long presses on like button of a feed.
        if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
            SoundUtil.playSoundEffectOnReactionsPopup(mContext);
        }
        popUp.showAtLocation(reactionsRecyclerView, Gravity.TOP, location[0], location[1] - 80);

        if(mAllReactionObject != null && mReactionsArray != null) {

            reactionsImages = new ArrayList<>();
            for(int i = 0; i< mReactionsArray.size(); i++){
                JSONObject reactionObject = mReactionsArray.get(i);
                String reaction_image_url = reactionObject.optJSONObject("icon").
                        optString("reaction_image_icon");
                String caption = reactionObject.optString("caption");
                String reaction = reactionObject.optString("reaction");
                int reactionId = reactionObject.optInt("reactionicon_id");
                String reactionIconUrl = reactionObject.optJSONObject("icon").
                        optString("reaction_image_icon");
                reactionsImages.add(new ImageViewList(reaction_image_url, caption,
                        reaction, reactionId, reactionIconUrl));
            }

            ImageAdapter reactionsAdapter = new ImageAdapter((Activity) mContext, reactionsImages, true,
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {


                            ImageViewList imageViewList = reactionsImages.get(position);
                            String reaction = imageViewList.getmReaction();
                            String caption = imageViewList.getmCaption();
                            String reactionIcon = imageViewList.getmReactionIcon();
                            int reactionId = imageViewList.getmReactionId();
                            popUp.dismiss();

                            /**
                             * If the user Presses the same reaction again then don't do anything
                             */
                            if(myReaction != null){
                                if(myReaction.optInt("reactionicon_id") != reactionId){
                                    doLikeUnlike(reaction, true, reactionId, reactionIcon, caption);
                                }
                            } else{
                                doLikeUnlike(reaction, false, reactionId, reactionIcon, caption);
                            }
                        }
                    });

            reactionsRecyclerView.setAdapter(reactionsAdapter);
        }
        return true;
    }

    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption){

        mLikeUnlikeText.setTypeface(fontIcon);
        mLikeUnlikeText.setText("\uf110");
        mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null);

        if (PreferencesUtils.isNestedCommentEnabled(mContext)) {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        } else {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "send-notification";
        }

        mReactionIcon.setVisibility(View.GONE);
        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, "sitegroup_group");
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mContentId));

        if(reaction != null){
            likeParams.put("reaction", reaction);
        }

        if(!isLiked || isReactionChanged){
            if(mReactionsEnabled ==  1 && PreferencesUtils.isNestedCommentEnabled(mContext)){
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "advancedcomments/like?sendNotification=0";
            } else{
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "like";
            }
        } else{
            mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "unlike";
        }

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mLikeUnlikeText.setTypeface(null);
                // Set My FeedReaction Changed
                if( mReactionsEnabled == 1){
                    /* If a Reaction is posted or a reaction is changed on content
                       put the updated reactions in my feed reactions array
                     */
                    updateContentReactions(reactionId, reactionIcon, caption, isLiked, isReactionChanged);

                     /* Calling to send notifications after like action */
                    if (!isLiked) {
                        mAppConst.postJsonResponseForUrl(sendLikeNotificationUrl, likeParams, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {

                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                            }
                        });
                    }
                }

                /*
                 Increase Like Count if content is liked else
                 decrease like count if the content is unliked
                 Do not need to increase/decrease the like count when it is already liked and only reaction is changed.
                  */
                if(!isLiked){
                    likeCount += 1;
                } else if( !isReactionChanged){
                    likeCount -= 1;
                }

                // Toggle isLike Variable if reaction is not changed
                if( !isReactionChanged)
                    isLiked = !isLiked;

                setLikeAndCommentCount();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mLikeUnlikeText.setTypeface(null);
                setLikeAndCommentCount();
                SnackbarUtils.displaySnackbar(findViewById(R.id.main_content), message);
            }

        });
    }
    private void updateContentReactions(int reactionId, String reactionIcon, String caption,
                                        boolean isLiked, boolean isReactionChanged){

        try{

            // Update the count of previous reaction in reactions object and remove the my_feed_reactions
            if(isLiked){
                if(myReaction != null && mContentReactions != null){
                    int myReactionId = myReaction.optInt("reactionicon_id");
                    if(mContentReactions.optJSONObject(String.valueOf(myReactionId)) != null){
                        int myReactionCount = mContentReactions.optJSONObject(String.valueOf(myReactionId)).
                                optInt("reaction_count");
                        if((myReactionCount - 1) <= 0){
                            mContentReactions.remove(String.valueOf(myReactionId));
                        } else {
                            mContentReactions.optJSONObject(String.valueOf(myReactionId)).put("reaction_count",
                                    myReactionCount - 1);
                        }
                        mReactionsObject.put("feed_reactions", mContentReactions);
                    }
                }
                mReactionsObject.put("my_feed_reaction", null);
            }

            // Update the count of current reaction in reactions object and set new my_feed_reactions object.
            if(!isLiked || isReactionChanged){
                // Set the updated my Reactions

                JSONObject jsonObject = new JSONObject();
                jsonObject.putOpt("reactionicon_id", reactionId);
                jsonObject.putOpt("reaction_image_icon", reactionIcon);
                jsonObject.putOpt("caption", caption);
                mReactionsObject.put("my_feed_reaction", jsonObject);

                if (mContentReactions != null) {
                    if (mContentReactions.optJSONObject(String.valueOf(reactionId)) != null) {
                        int reactionCount = mContentReactions.optJSONObject(String.valueOf(reactionId)).optInt("reaction_count");
                        mContentReactions.optJSONObject(String.valueOf(reactionId)).putOpt("reaction_count", reactionCount + 1);
                    } else {
                        jsonObject.put("reaction_count", 1);
                        mContentReactions.put(String.valueOf(reactionId), jsonObject);
                    }
                } else {
                    mContentReactions = new JSONObject();
                    jsonObject.put("reaction_count", 1);
                    mContentReactions.put(String.valueOf(reactionId), jsonObject);
                }
                mReactionsObject.put("feed_reactions", mContentReactions);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mMainContent, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        Intent intent = new Intent();
                        setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
                        finish();
                    }
                });
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseList = (BrowseListItems) itemList;

        switch (menuName) {

            case "upload_cover_photo":
            case "upload_photo":
                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    startImageUploading();
                }
                break;

            case "choose_from_album":
                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "album");
                bundle.putString(ConstantVariables.CONTENT_TITLE, mBrowseList.getmBrowseListTitle());
                bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                bundle.putBoolean("isCoverRequest", isCoverRequest);
                bundle.putBoolean("isSiteGroupAlbums", true);
                bundle.putString(ConstantVariables.SUBJECT_TYPE, "sitegroup_group");
                bundle.putInt(ConstantVariables.SUBJECT_ID, group_id);
                bundle.putInt(ConstantVariables.VIEW_PAGE_ID, group_id);
                bundle.putString(ConstantVariables.URL_STRING, AppConstant.DEFAULT_URL + "advancedgroups/photos/index/" + group_id);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_GROUPS_MENU_TITLE);
                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, ConstantVariables.PAGE_EDIT_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "view_profile_photo":
            case "view_cover_photo":
                openLightBox(isCoverRequest);
                break;

            case "remove_cover_photo":
            case "remove_photo":
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.bringToFront();
                makeRequest();
                break;

            case "follow":
            case "unfollow":
                sIsGroupFollowed = (sIsGroupFollowed == 1) ? 0 : 1 ;
                //Update the data inside fragment.

                if (mViewPageFragmentAdapter != null) {
                    mViewPageFragmentAdapter.updateData(this.bundle, mProfileTabs, true, false);
                }
                break;
        }
    }

    @Override
    public void onUserReviewDelete() {
        makeRequest();
    }

    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        if (isRequestSuccessful) {
            SnackbarUtils.displaySnackbarLongTime(mMainContent, successMessage);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
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
                    startImageUploading();
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

}
