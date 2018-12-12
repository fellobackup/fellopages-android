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

package com.fellopages.mobileapp.classes.modules.advancedVideos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SplitToolbar;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
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


public class ChannelProfilePage extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        View.OnLongClickListener, OnUploadResponseListener, OnOptionItemClickResponseListener, View.OnClickListener {

    // Member variables.
    private Context mContext;
    private View mRootView;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ViewPager viewPager;
    private TabLayout mSlidingTabs;
    private ImageView ivCoverImage, ivProfileImage, ivReactionIcon;
    private TextView tvContentTitle, tvToolBarTitle, tvLikeCount, tvCommentCount, tvLikeUnlike, tvSubscribe,
            tvCoverMenuIcon, tvProfileMenuIcon;
    private LinearLayout llLikeSubscribeBlock;
    private SplitToolbar mBottomToolBar;
    private ProgressBar mProgressBar;
    private String mChannelProfileUrl, mLikeUnlikeUrl, mTitle, mSuccessMessage;
    private int mContentId, mReactionsEnabled, defaultCover, mCoverPhoto = 0, mProfilePhoto = 0,
            mProfileTabSize, mLikeCount, mCommentCount;
    private float initialY;
    private boolean isLike, isSubscribe, isLoadingFromCreate = false, isAdapterSet = false,
            isContentEdited = false, isCoverRequest;
    private JSONObject mBody, mReactionsObject, myReaction, mAllReactionObject, mContentReactions;
    private JSONArray mGutterMenus, mProfileTabs, mCoverPhotoMenus, mProfilePhotoMenus;
    private Bundle bundle;
    private Map<String, String> mPostParams;
    private List<ImageViewList> reactionsImages;
    private ArrayList<PhotoListDetails> mCoverPhotoListDetails, mProfilePhotoListDetails;
    private ArrayList<JSONObject> mReactionsArray;
    private BrowseListItems mBrowseList;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ViewPageFragmentAdapter mViewPageFragmentAdapter;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pages);

        // Initialize member variables.
        mContext = ChannelProfilePage.this;
        mPostParams = new HashMap<>();
        mCoverPhotoListDetails = new ArrayList<>();
        mProfilePhotoListDetails = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        mAppConst = new AppConstant(mContext);
        mGutterMenuUtils = new GutterMenuUtils(mContext);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(getApplicationContext());

        //Get Views.
        getViews();

        /** Getting Intent Key's. **/
        mContentId = getIntent().getIntExtra(ConstantVariables.VIEW_ID, 0);
        mChannelProfileUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        // Checking for the reaction is enabled or not.
        mReactionsEnabled = PreferencesUtils.getReactionsEnabled(mContext);

        // Setting up the listeners.
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);

        /*
            Check if Reactions and nested comment plugin is enabled on the site
            send request to get the reactions on a particular content
            send this request only if the reactions Enabled is not saved yet in Preferences
             or if it is set to 1
         */
        if (mReactionsEnabled == 1 || mReactionsEnabled == -1) {
            String getContentReactionsUrl = UrlUtil.CONTENT_REACTIONS_URL + "&subject_type=sitevideo_channel"
                    + "&subject_id=" + mContentId;
            mAppConst.getJsonResponseFromUrl(getContentReactionsUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    mReactionsObject = jsonObject;
                    JSONObject reactionsData = mReactionsObject.optJSONObject("reactions");
                    if (reactionsData != null) {
                        mReactionsEnabled = reactionsData.optInt("reactionsEnabled");
                        mContentReactions = mReactionsObject.optJSONObject("feed_reactions");
                        PreferencesUtils.updateReactionsEnabledPref(mContext, mReactionsEnabled);
                        mAllReactionObject = reactionsData.optJSONObject("reactions");
                        if (mAllReactionObject != null) {
                            mReactionsArray = GlobalFunctions.sortReactionsObjectWithOrder(mAllReactionObject);
                        }
                    }
                    // Send Request to load View page data after fetching Reactions on the content.
                    makeServerRequestIfNeed();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    // Send Request to load View page data after fetching Reactions on the content.
                    makeServerRequestIfNeed();
                }
            });
        } else {
            makeServerRequestIfNeed();
        }
    }

    /**
     * Method to get the views.
     */
    private void getViews() {
        //Header view
        /* Create Back Button On Action Bar **/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mRootView = findViewById(R.id.main_content);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Setup the Tabs
        viewPager = (ViewPager) findViewById(R.id.pager);
        mSlidingTabs = (TabLayout) findViewById(R.id.slidingTabs);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

         // Like and Unlike Fields...
        llLikeSubscribeBlock = (LinearLayout) findViewById(R.id.likeCommentContent);
        mBottomToolBar = (SplitToolbar) findViewById(R.id.toolbarBottom);
        tvLikeCount = (TextView) findViewById(R.id.likeCount);
        tvCommentCount = (TextView) findViewById(R.id.commentCount);
        ivReactionIcon = (ImageView) findViewById(R.id.reactionIcon);
        tvLikeUnlike = (TextView) findViewById(R.id.likeUnlikeText);
        tvSubscribe = (TextView) findViewById(R.id.commentText);
        LinearLayout llLikeBlock = (LinearLayout) findViewById(R.id.likeBlock);
        LinearLayout llSubscribeBlock = (LinearLayout) findViewById(R.id.commentBlock);
        LinearLayout llCountContainer = (LinearLayout) findViewById(R.id.likeCommentBlock);
        llLikeBlock.setOnClickListener(this);
        llLikeBlock.setOnLongClickListener(this);
        llSubscribeBlock.setOnClickListener(this);
        llCountContainer.setOnClickListener(this);

        // Getting the cover image and other views.
        ivCoverImage = (ImageView) findViewById(R.id.coverImage);
        ivProfileImage = (ImageView) findViewById(R.id.profile_image);
        tvContentTitle = (TextView) findViewById(R.id.content_title);
        tvCoverMenuIcon = (TextView) findViewById(R.id.cover_image_menus);
        tvProfileMenuIcon = (TextView) findViewById(R.id.profile_image_menus);
        tvToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        tvToolBarTitle.setSelected(true);
        tvCoverMenuIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvProfileMenuIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
    }

    /**
     * Method to call server if needed.
     */
    private void makeServerRequestIfNeed() {
        // Calling to server.
        // Load Data Directly if Coming from Create Page.
        if (mBody != null && mBody.length() != 0) {
            isLoadingFromCreate = true;
            isContentEdited = true;
            loadViewPageData(mBody);
        } else {
            makeRequest();
        }
    }

    /**
     * Method to get response of profile page.
     */
    public void makeRequest(){

        // Do not send request if coming from create page
        if(!isLoadingFromCreate){
            mAppConst.getJsonResponseFromUrl(mChannelProfileUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBody = jsonObject;
                    loadViewPageData(mBody);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(mRootView, message,
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

    /**
     * Method to get view page data.
     * @param bodyJsonObject jsonObject, which contains view page data.
     */
    private void loadViewPageData(JSONObject bodyJsonObject) {
        mProgressBar.setVisibility(View.GONE);

        if (bodyJsonObject != null && bodyJsonObject.length() != 0) {
            try {
                mGutterMenus = bodyJsonObject.optJSONArray("gutterMenu");
                JSONObject responseObject = bodyJsonObject.optJSONObject("response");
                mTitle = responseObject.optString("title");
                mContentId = responseObject.optInt("channel_id");
                String coverImage = responseObject.optString("image");
                String contentUrl = responseObject.optString("content_url");
                final String profileImageUrl = responseObject.getString("image");
                String coverImageUrl = responseObject.optString("cover_image");
                isSubscribe = responseObject.optInt("is_subscribe") == 1;
                isLike = responseObject.optBoolean("is_like");
                mLikeCount = responseObject.optInt("like_count");
                mCommentCount = responseObject.optInt("comment_count");
                int isFavouriteOption = responseObject.optInt("is_favourite_option") == 1 ? 0: 1;
                mProfileTabs = bodyJsonObject.optJSONArray("profile_tabs");

                mBrowseList = new BrowseListItems(mContentId, isFavouriteOption, mTitle, coverImage, contentUrl);
                if (mGutterMenus != null) {
                    invalidateOptionsMenu();
                }

                bundle = new Bundle();
                bundle.putString(ConstantVariables.RESPONSE_OBJECT, bodyJsonObject.toString());
                bundle.putInt(ConstantVariables.CONTENT_ID, mContentId);
                bundle.putInt(ConstantVariables.CAN_UPLOAD, responseObject.optInt("canUpload"));
                bundle.putInt(ConstantVariables.CAN_UPLOAD_VIDEO, responseObject.optInt("canUploadVideo"));
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE);
                bundle.putString(ConstantVariables.SUBJECT_TYPE, "sitevideo_channel");
                bundle.putInt(ConstantVariables.SUBJECT_ID, mContentId);
                bundle.putInt(ConstantVariables.VIEW_PAGE_ID, mContentId);
                bundle.putString(ConstantVariables.CONTENT_TITLE, mTitle);
                bundle.putBoolean(ConstantVariables.IS_WAITING, false);

                if (!isAdapterSet) {
                    // Setup the viewPager
                    mProfileTabSize = mProfileTabs.length();
                    mViewPageFragmentAdapter = new ViewPageFragmentAdapter(mContext,
                            getSupportFragmentManager(), mProfileTabs, bundle);
                    viewPager.setAdapter(mViewPageFragmentAdapter);
                    viewPager.setOffscreenPageLimit(mViewPageFragmentAdapter.getCount() + 1);
                    // This method ensures that tab selection events update the ViewPager
                    // and page changes update the selected tab.
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

                if (viewPager != null) {
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            if (mBottomToolBar != null && mAppConst != null) {
                                if (viewPager.getCurrentItem() == 1 && !mAppConst.isLoggedOutUser()) {
                                    mBottomToolBar.setVisibility(View.VISIBLE);
                                } else {
                                    mBottomToolBar.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    });
                }

                //Setting data in views
                tvContentTitle.setText(mTitle);
                collapsingToolbar.setTitle(mTitle);
                tvToolBarTitle.setText(mTitle);
                CustomViews.setCollapsingToolBarTitle(collapsingToolbar);

                // If default_cover image is coming then showing user cover image plugin views.
                if (responseObject.has("default_cover")) {
                    ivProfileImage.setVisibility(View.VISIBLE);
                    //Showing content title
                    tvContentTitle.setText(mTitle);

                    defaultCover = responseObject.optInt("default_cover");

                    //Showing profile image.
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        ivProfileImage.setVisibility(View.VISIBLE);
                        mImageLoader.setCoverImageUrl(profileImageUrl, ivProfileImage);
                    }

                    //Showing Cover image.
                    if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                        mCoverPhoto = defaultCover == 1 ? 0 : 1;
                        mImageLoader.setCoverImageUrl(coverImageUrl, ivCoverImage);
                    }
                    mProfilePhoto = responseObject.has("photo_id") ? 1 : 0;
                    getCoverAndProfileMenuRequest();

                    mProfilePhotoListDetails.clear();
                    mCoverPhotoListDetails.clear();

                    mProfilePhotoListDetails.add(new PhotoListDetails(profileImageUrl));
                    mCoverPhotoListDetails.add(new PhotoListDetails(coverImageUrl));

                } else {
                    mImageLoader.setImageUrl(profileImageUrl, ivCoverImage);
                    mCoverPhotoListDetails.add(new PhotoListDetails(profileImageUrl));
                }

                ivCoverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PreferencesUtils.getSiteContentCoverPhotoEnabled(mContext) == 1 &&
                                mCoverPhotoMenus != null && mCoverPhotoMenus.length() > 0) {
                            isCoverRequest = true;
                            mGutterMenuUtils.showPopup(tvCoverMenuIcon, mCoverPhotoMenus,
                                    mBrowseList, ConstantVariables.ADVANCED_GROUPS_MENU_TITLE);
                        } else {
                            openLightBox(true);
                        }
                    }
                });

                ivProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (profileImageUrl != null && !profileImageUrl.isEmpty() &&
                                mProfilePhotoMenus != null && mProfilePhotoMenus.length() > 0) {
                            isCoverRequest = false;
                            mGutterMenuUtils.showPopup(tvProfileMenuIcon, mProfilePhotoMenus,
                                    mBrowseList, ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE);
                        } else {
                            openLightBox(false);
                        }
                    }
                });

                // Showing like/comment count.
                setLikeAndCommentCount();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to send server request to get the cover and profile menu array.
     */
    private void getCoverAndProfileMenuRequest() {
        String menuUrl = UrlUtil.GET_COVER_MENU_URL +"subject_id=" + mContentId
                + "&subject_type=sitevideo_channel&special=both&cover_photo=" + mCoverPhoto
                + "&profile_photo=" + mProfilePhoto;

        mAppConst.getJsonResponseFromUrl(menuUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                JSONObject responseObject = jsonObject.optJSONObject("response");
                if (responseObject != null && responseObject.length() > 0) {
                    setCoverAndProfileMenu(responseObject);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });

    }

    /**
     * Method to set cover and profile menus according to response.
     * @param responseObject Response Object Which contains the cover and profile menus array.
     */
    private void setCoverAndProfileMenu(JSONObject responseObject) {
        mCoverPhotoMenus = responseObject.optJSONArray("coverPhotoMenu");
        mProfilePhotoMenus = responseObject.optJSONArray("profilePhotoMenu");

        // Showing the cover photo menu icon if the cover photo menu array is coming in response.
        if (mCoverPhotoMenus != null && mCoverPhotoMenus.length() > 0) {
            tvCoverMenuIcon.setVisibility(View.VISIBLE);
            tvCoverMenuIcon.setText("\uf030");
        }

        // Showing the profile photo menu icon if the profile photo menu array is coming in response.
        if (mProfilePhotoMenus != null && mProfilePhotoMenus.length() > 0) {
            tvProfileMenuIcon.setVisibility(View.VISIBLE);
            tvProfileMenuIcon.setText("\uf030");
        }
    }

    /**
     * Method to open PhotoLightBox when user click on view image.
     * @param isCoverRequest parameter to decide whether it is user profile or user cover.
     */
    public void openLightBox(boolean isCoverRequest) {

        Bundle bundle = new Bundle();
        if (isCoverRequest) {
            bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mCoverPhotoListDetails);
        } else {
            bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mProfilePhotoListDetails);
        }
        Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
        i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
        i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE);
        i.putExtras(bundle);
        startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
    }

    /**
     * Method to launch PhotoUploadingActivity to upload the photo from the storage device.
     */
    private void startImageUploading() {
        Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
        intent.putExtra("selection_mode", true);
        intent.putExtra(ConstantVariables.IS_PHOTO_UPLOADED, true);
        startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
    }

    /**
     * Method to set like and comment count.
     */
    public void setLikeAndCommentCount() {

        if (mAppConst.isLoggedOutUser()) {
            mBottomToolBar.setVisibility(View.GONE);
        } else {
            if (viewPager != null && viewPager.getCurrentItem() == 1) {
                mBottomToolBar.setVisibility(View.VISIBLE);
            }
            // setting up the values in views
            llLikeSubscribeBlock.setVisibility(View.VISIBLE);

            tvLikeUnlike.setActivated(isLike);
            if (!isLike) {
                tvLikeUnlike.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            } else {
                tvLikeUnlike.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }

            // Method to set subscribe view.
            setSubscribeView();

            /*
            Set Like and Comment Count
             */
            // Check if Reactions is enabled, show that content reaction and it's icon here.
            if (mReactionsEnabled == 1 && mReactionsObject != null && mReactionsObject.length() != 0) {


                myReaction = mReactionsObject.optJSONObject("my_feed_reaction");

                if (myReaction != null && myReaction.length() != 0) {
                    tvLikeUnlike.setText(myReaction.optString("caption"));
                    tvLikeUnlike.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    String reactionImage = myReaction.optString("reaction_image_icon");
                    mImageLoader.setImageUrl(reactionImage, ivReactionIcon);
                    ivReactionIcon.setVisibility(View.VISIBLE);
                } else {
                    tvLikeUnlike.setText(getString(R.string.like_text));
                    tvLikeUnlike.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                            null, null, null);
                    ivReactionIcon.setVisibility(View.GONE);
                }

            } else {
                tvLikeUnlike.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                tvLikeUnlike.setText(getString(R.string.like_text));
            }

            tvLikeCount.setText(String.valueOf(mLikeCount));
            tvCommentCount.setText(String.valueOf(mCommentCount));
        }
    }

    /**
     * Method to set Subscribe/Unsubscribe in view.
     */
    private void setSubscribeView() {
        tvSubscribe.setTypeface(null);

        // Showing drawable for subscribe.
        int color;
        if (isSubscribe) {
            color = ContextCompat.getColor(mContext, R.color.themeButtonColor);
            tvSubscribe.setText(mContext.getResources().getString(R.string.subscribed_menu_label));
        } else {
            color = ContextCompat.getColor(mContext, R.color.grey_dark);
            tvSubscribe.setText(mContext.getResources().getString(R.string.subscribe_menu_label));
        }
        tvSubscribe.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext,
                R.drawable.ic_subscriptions_24dp), null, null, null);
        tvSubscribe.setTextColor(color);
        tvSubscribe.setActivated(isSubscribe);
    }

    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption){

        tvLikeUnlike.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvLikeUnlike.setText("\uf110");
        tvLikeUnlike.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null);
        final String sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        ivReactionIcon.setVisibility(View.GONE);
        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, "sitevideo_channel");
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mContentId));

        if(reaction != null){
            likeParams.put("reaction", reaction);
        }

        if(!isLike || isReactionChanged){
            if(mReactionsEnabled ==  1){
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

                tvLikeUnlike.setTypeface(null);
                // Set My FeedReaction Changed
                if( mReactionsEnabled == 1){
                    /* If a Reaction is posted or a reaction is changed on content
                       put the updated reactions in my feed reactions array
                     */
                    updateContentReactions(reactionId, reactionIcon, caption, isLike, isReactionChanged);

                    /* Calling to send notifications after like action */
                    mAppConst.postJsonRequest(sendLikeNotificationUrl, likeParams);
                }

                /*
                 Increase Like Count if content is liked else
                 decrease like count if the content is unliked
                 Do not need to increase/decrease the like count when it is already liked and only reaction is changed.
                  */
                if(!isLike){
                    mLikeCount += 1;
                } else if( !isReactionChanged){
                    mLikeCount -= 1;
                }

                // Toggle isLike Variable if reaction is not changed
                if( !isReactionChanged)
                    isLike = !isLike;

                setLikeAndCommentCount();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                tvLikeUnlike.setTypeface(null);
                setLikeAndCommentCount();
                SnackbarUtils.displaySnackbar(findViewById(R.id.main_content), message);
            }

        });
    }

    private void updateContentReactions(int reactionId, String reactionIcon, String caption,
                                        boolean isLiked, boolean isReactionChanged) {

        try {

            // Update the count of previous reaction in reactions object and remove the my_feed_reactions
            if (isLiked) {
                if (myReaction != null && mContentReactions != null) {
                    int myReactionId = myReaction.optInt("reactionicon_id");
                    if (mContentReactions.optJSONObject(String.valueOf(myReactionId)) != null) {
                        int myReactionCount = mContentReactions.optJSONObject(String.valueOf(myReactionId)).
                                optInt("reaction_count");
                        if ((myReactionCount - 1) <= 0) {
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
            if (!isLiked || isReactionChanged) {
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

    private void toolbarAnimateShow() {
        mBottomToolBar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    private void toolbarAnimateHide() {
        mBottomToolBar.animate()
                .translationY(mBottomToolBar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
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
    public void onClick(View view) {

        // Sending request only when the network is available. Else show the error.
        if (GlobalFunctions.isNetworkAvailable(mContext)) {
            switch (view.getId()) {
                case R.id.likeBlock:
                    int reactionId = 0;
                    String reactionIcon = null, caption = null;

                    if (mReactionsEnabled == 1 && mAllReactionObject != null) {
                        reactionId = mAllReactionObject.optJSONObject("like").optInt("reactionicon_id");
                        reactionIcon = mAllReactionObject.optJSONObject("like").optJSONObject("icon").
                                optString("reaction_image_icon");
                        caption = mContext.getResources().getString(R.string.like_text);
                    }
                    doLikeUnlike(null, false, reactionId, reactionIcon, caption);
                    break;

                //Click listener on Subscribe Button.
                case R.id.commentBlock:
                    mPostParams.clear();
                    final String subscribeUrl = AppConstant.DEFAULT_URL + "advancedvideo/channel/channel-subscribe/" + mContentId;
                    mPostParams.put("value", String.valueOf(isSubscribe ? 0 : 1));
                    if (isSubscribe) {
                        mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().
                                getString(R.string.unsubscribe_from)
                                + " <b>" + mTitle + "</b>.",
                                mContext.getResources().getString(R.string.unsubscribe_listing_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isSubscribe = !isSubscribe;
                                        setSubscribeView();
                                        mAppConst.postJsonRequest(subscribeUrl, mPostParams);
                                    }
                                });
                    } else {
                        isSubscribe = !isSubscribe;
                        setSubscribeView();
                        mAppConst.postJsonRequest(subscribeUrl, mPostParams);
                    }
                    break;

                //Click listener on like/comment count container.
                case R.id.likeCommentBlock:
                    String mLikeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=sitevideo_channel" +
                            "&subject_id=" + mContentId + "&viewAllComments=1&page=1&limit=20";
                    Intent intent = new Intent(mContext, Comment.class);
                    intent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                    intent.putExtra(ConstantVariables.SUBJECT_TYPE, "sitevideo_channel");
                    intent.putExtra(ConstantVariables.SUBJECT_ID, mContentId);
                    intent.putExtra("commentCount", mCommentCount);
                    intent.putExtra("reactionsEnabled", mReactionsEnabled);
                    if (mContentReactions != null) {
                        intent.putExtra("popularReactions", mContentReactions.toString());
                    }
                    startActivityForResult(intent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
            }
        } else {
            SnackbarUtils.displaySnackbar(view,
                    mContext.getResources().getString(R.string.network_connectivity_error));
        }
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
                mGutterMenuUtils.onMenuOptionItemSelected(mRootView, findViewById(item.getItemId()), id,
                        mGutterMenus);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE,
                    mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){

            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                isContentEdited = true;
                makeRequest();
                break;

            case ConstantVariables.PAGE_EDIT_CODE:
            case ConstantVariables.CREATE_REQUEST_CODE:
                if (mViewPageFragmentAdapter != null) {
                    mViewPageFragmentAdapter.updateData(bundle, mProfileTabs, false, true);
                }
                if (requestCode == ConstantVariables.PAGE_EDIT_CODE
                        && resultCode == ConstantVariables.PAGE_EDIT_CODE && data != null) {
                    ArrayList<String> resultList = data.getStringArrayListExtra(ConstantVariables.PHOTO_LIST);
                    String postUrl = UrlUtil.UPLOAD_COVER_PHOTO_URL + "subject_type=sitevideo_channel" +
                            "&subject_id=" + mContentId;
                    if (isCoverRequest) {
                        mSuccessMessage = mContext.getResources().getString(R.string.cover_photo_updated);
                    } else {
                        postUrl = postUrl + "&special=profile";
                        mSuccessMessage = mContext.getResources().getString(R.string.profile_photo_updated);
                    }
                    new UploadFileToServerUtils(mContext, postUrl, resultList, this).execute();

                }
                break;

            case ConstantVariables.VIEW_PAGE_CODE:
                if (requestCode == ConstantVariables.CREATE_REQUEST_CODE) {
                    isContentEdited = true;
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.bringToFront();
                    makeRequest();
                }
                break;

            case ConstantVariables.FEED_REQUEST_CODE:
                /* Reset the view pager adapter if any status is posted using AAF */
                if(viewPager != null) {
                    viewPager.setAdapter(mViewPageFragmentAdapter);
                }
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (data != null) {
                    mCommentCount = data.getIntExtra(ConstantVariables.PHOTO_COMMENT_COUNT, mCommentCount);
                    tvCommentCount.setText(String.valueOf(mCommentCount));
                }
        }
        isLoadingFromCreate = false;

        if(requestCode == ConstantVariables.FRAGMENT_LOAD_CODE){
            PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.ADV_GROUPS_MENU_TITLE);
        }
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
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        if (isRequestSuccessful) {
            SnackbarUtils.displaySnackbarLongTime(mRootView, mSuccessMessage);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
            mAppConst.refreshUserData();
            makeRequest();

        } else {
            SnackbarUtils.displaySnackbarLongTime(mRootView, jsonObject.optString("message"));
        }
    }

    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mRootView, successMessage,
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
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "photos");
                bundle.putString(ConstantVariables.CONTENT_TITLE, mBrowseList.getmBrowseListTitle());
                bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                bundle.putBoolean("isCoverRequest", isCoverRequest);
                bundle.putBoolean("isCoverChange", true);
                bundle.putString(ConstantVariables.URL_STRING, AppConstant.DEFAULT_URL + "advancedvideo/channel/photo/" + mContentId);
                bundle.putString(ConstantVariables.SUBJECT_TYPE, ConstantVariables.SITE_VIDEO_CHANNEL_MENU_TITLE);
                bundle.putInt(ConstantVariables.SUBJECT_ID, mContentId);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE);
                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, ConstantVariables.PAGE_EDIT_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        CustomViews.showMarqueeTitle(verticalOffset, collapsingToolbar, mToolbar, tvToolBarTitle, mTitle);
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
                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mRootView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;

        }
    }

}
