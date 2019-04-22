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

package com.fellopages.mobileapp.classes.modules.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnVideoSourceLoadCompleteListener;
import com.fellopages.mobileapp.classes.common.ui.ActionIconThemedTextView;
import com.fellopages.mobileapp.classes.common.utils.GetVideoDataSourceUtils;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoView extends AppCompatActivity implements  View.OnClickListener,
        OnOptionItemClickResponseListener, View.OnLongClickListener {

    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private String mItemViewUrl, mVideoViewUrl, tags = null;
    private int mVideoType, mViewId, listingTypeId;
    private Context mContext;
    private BrowseListItems mBrowseList;
    private String title, body, category_title, image_icon, owner_title, creation_date, convertedDate, mContentUrl;
    private String mLikeUnlikeUrl, mLikeCommentsUrl, mModuleName;
    private int view_count, mRating, mCommentCount, mLikeCount, mVideoId, orientation, ownerId;
    private boolean isLike, isAlreadyRated = false, isContentEdited = false, isContentDeleted = false;

    private JSONArray mDataResponseArray, mGutterMenus, mTagNamesArray;
    private JSONObject mDataResponse, mBody, mTagObject;
    private TextView tvVideoMode;
    private SelectableTextView mViewTitle, mViewDetails, mTagView, mViewCount, mViewOwnerName, mViewDescription;
    private BezelImageView mOwnerProfilePic;
    private MediaController mController;
    private WebView mVideoWebView;
    private ProgressBar loadingProgress;
    private RatingBar mRatingBar;

    private TextView mLikeCountTextView, mCommentCountTextView;
    private ActionIconThemedTextView mLikeButton, mCommentButton;
    private LinearLayout mLikeBlock, mCountsBlock;
    private ImageView mReactionIcon;

    private ScrollView mScrollView;
    private RelativeLayout.LayoutParams layoutParams;
    SocialShareUtil socialShareUtil;
    private String mSubjectType;
    private JSONObject mReactionsObject, myReaction, mAllReactionObject, mContentReactions;
    private int mReactionsEnabled;
    private List<ImageViewList> reactionsImages;
    private boolean isSiteVideoEnabled;
    private ArrayList<JSONObject> mReactionsArray;
    private android.widget.VideoView videoView;
    private ImageLoader mImageLoader;
    private String sendLikeNotificationUrl;
    private int stopPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        orientation = getResources().getConfiguration().orientation;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        backArrow.setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        layoutParams = (RelativeLayout.LayoutParams) findViewById(R.id.main_content).getLayoutParams();

        mContext = this;
        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        mImageLoader = new ImageLoader(getApplicationContext());
        socialShareUtil = new SocialShareUtil(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);

        mModuleName = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mViewId = getIntent().getIntExtra(ConstantVariables.VIEW_ID, 0);

        mItemViewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        listingTypeId = getIntent().getIntExtra(ConstantVariables.LISTING_TYPE_ID, 0);
        String videoSubjectType = getIntent().getStringExtra(ConstantVariables.VIDEO_SUBJECT_TYPE);
        isSiteVideoEnabled = getIntent().getIntExtra(ConstantVariables.ADV_VIDEO_INTEGRATED, 0) == 1;
        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));


        if(isSiteVideoEnabled || mItemViewUrl == null || mItemViewUrl.isEmpty()) {
            mItemViewUrl = UrlUtil.VIDEO_VIEW_URL + mViewId + "?gutter_menu=" + 1;
        }

        if (videoSubjectType != null && !videoSubjectType.isEmpty() && !isSiteVideoEnabled) {
            mSubjectType = videoSubjectType;
        } else {
            mSubjectType = "video";
        }

        // Getting intent values.
        mVideoType = getIntent().getIntExtra(ConstantVariables.VIDEO_TYPE, 0);
        mVideoViewUrl = getIntent().getStringExtra(ConstantVariables.VIDEO_URL);

        mScrollView = findViewById(R.id.bottomAreaScroller);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
        }

        /**
         * Portrait and Landscape mode.
         */
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            assert getSupportActionBar() != null;
            getSupportActionBar().show();
            mScrollView.setVisibility(View.VISIBLE);
            findViewById(R.id.main_content).setLayoutParams(layoutParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            assert getSupportActionBar() != null;
            getSupportActionBar().hide();
            mScrollView.setVisibility(View.GONE);
            findViewById(R.id.main_content).setLayoutParams(CustomViews.getFullWidthHeightLayoutParams());
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        /*
        Like and Unlike Fields...
         */
        mLikeBlock = findViewById(R.id.likeBlock);
        mReactionIcon = findViewById(R.id.reactionIcon);
        mLikeButton = findViewById(R.id.like_button);
        mCommentButton  = findViewById(R.id.comment_button);
        mCountsBlock = findViewById(R.id.countsBlock);
        mLikeCountTextView = findViewById(R.id.likeCount);
        mCommentCountTextView = findViewById(R.id.commentCount);
        mLikeBlock.setOnClickListener(this);
        mLikeBlock.setOnLongClickListener(this);
        mCommentButton.setOnClickListener(this);
        mCountsBlock.setOnClickListener(this);

        mViewTitle = findViewById(R.id.video_title);
        mViewDetails = findViewById(R.id.video_detail);
        mViewCount = findViewById(R.id.video_view_count);
        mViewOwnerName = findViewById(R.id.owner_title);
        mViewDescription = findViewById(R.id.video_description);
        mOwnerProfilePic = findViewById(R.id.owner_image);
        loadingProgress = findViewById(R.id.loadingProgress);
        mRatingBar = findViewById(R.id.smallRatingBar);
        mTagView = findViewById(R.id.tagView);

        //Setting up local video player
        tvVideoMode = findViewById(R.id.video_mode);
        videoView = findViewById(R.id.video_player);
        mController = new MediaController(this);
        mController.setAnchorView(videoView);
        videoView.setMediaController(mController);
        videoView.requestFocus();

        //Setting up webview for playing youtube and vimeo videos
        mVideoWebView = findViewById(R.id.webView);
        loadVideoUrl();

        // Hide Rating Bar in case of Logged-out user

        if(mAppConst.isLoggedOutUser()){
            mRatingBar.setVisibility(View.GONE);
        }

        mReactionsEnabled = PreferencesUtils.getReactionsEnabled(this);

        /*
            Check if Reactions and nested comment plugin is enabled on the site
            send request to get the reactions on a particular content
            send this request only if the reactions Enabled is not saved yet in Preferences
             or if it is set to 1
         */
        if(mReactionsEnabled == 1 || mReactionsEnabled == -1){
            String getContentReactionsUrl = UrlUtil.CONTENT_REACTIONS_URL + "&subject_type=" + mSubjectType +
                    "&subject_id=" + mViewId;
            mAppConst.getJsonResponseFromUrl(getContentReactionsUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
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
                    if (mBody != null) {
                        isContentEdited = true;
                        setDataInView();
                    } else {
                        makeRequest();

                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    // Send Request to load View page data after fetching Reactions on the content.
                    if (mBody != null) {
                        isContentEdited = true;
                        setDataInView();
                    } else {
                        makeRequest();

                    }
                }
            });
        } else{
            makeRequest();
        }

    }

    /*
        Method to load video url when activity started.
     */
    public void loadVideoUrl() {

        loadingProgress.setVisibility(View.VISIBLE);
        if (mVideoType == 3) {
            if (mVideoViewUrl != null) {
                try {
                    videoView.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.VISIBLE);
                    new GetVideoDataSourceUtils(mVideoViewUrl, new OnVideoSourceLoadCompleteListener() {
                        @Override
                        public void onSuccess(String videoPath) {
                            if (videoPath != null && !videoPath.isEmpty()) {
                                videoView.setVideoPath(videoPath);
                                videoView.start();
                                videoView.requestFocus();
                                loadingProgress.setVisibility(View.GONE);
                                tvVideoMode.setVisibility(View.VISIBLE);
                                tvVideoMode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.
                                        getDrawable(mContext, R.drawable.ic_zoom_out_24dp), null, null, null);
                                tvVideoMode.setTag("original");
                                tvVideoMode.setOnClickListener(VideoView.this);
                            }
                        }
                    }).execute();


                } catch (Exception e) {
                    if (videoView != null) {
                        videoView.stopPlayback();
                    }
                    e.printStackTrace();
                }
            }
        } else if (mVideoType != 0) {

            //Auto playing videos in webview
            mVideoWebView.setClickable(true);
            mVideoWebView.setFocusableInTouchMode(true);
            mVideoWebView.getSettings().setJavaScriptEnabled(true);

            mVideoWebView.getSettings().setAppCacheEnabled(true);
            mVideoWebView.getSettings().setDomStorageEnabled(true);

            if (mVideoViewUrl != null && !mVideoViewUrl.contains("http://") &&
                    !mVideoViewUrl.contains("https://")) {
                mVideoViewUrl = "http://" + mVideoViewUrl;
            }

            if (mVideoViewUrl != null && mVideoViewUrl.contains("youtube")) {
                Map<String, String> extraHeaders = new HashMap<>();
                extraHeaders.put("Referer", "http://www.youtube.com");
                mVideoWebView.loadUrl(mVideoViewUrl, extraHeaders);
            } else {
                mVideoWebView.loadUrl(mVideoViewUrl);
            }

            mVideoWebView.setWebChromeClient(new WebChromeClient());
            mVideoWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    String javascript = "javascript:" +
                            "   document.getElementsByClassName('ytp-large-play-button')[0].click();"+
                            "   setTimeout(function() {" +
                            "   var endScreen = document.getElementsByClassName('videowall-endscreen')[0];"+
                            "   endScreen.style.opacity = 0;" +
                            "   endScreen.style.display = 'none';"+
                            "   var replayButton = document.getElementsByClassName('ytp-icon-replay')[0];"+
                            "   replayButton.style.marginBottom = '90px';"+
                            "   var headerTitle = document.getElementsByClassName('ytp-chrome-top')[0];" +
                            "   headerTitle.style.display = 'none';"+
                            "   }, 2000);"
                            ;
                    view.loadUrl(javascript);
                    super.onPageFinished(view, url);
                    loadingProgress.setVisibility(View.GONE);
                    mVideoWebView.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            assert getSupportActionBar() != null;
            getSupportActionBar().hide();
            mScrollView.setVisibility(View.GONE);
            findViewById(R.id.main_content).setLayoutParams(CustomViews.getFullWidthHeightRelativeLayoutParams());
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            assert getSupportActionBar() != null;
            getSupportActionBar().show();
            mScrollView.setVisibility(View.VISIBLE);
            findViewById(R.id.main_content).setLayoutParams(layoutParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void makeRequest() {

        mAppConst.getJsonResponseFromUrl(mItemViewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                setDataInView();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(findViewById(R.id.video_view_page),
                        message, new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }
        });
    }

    public void setDataInView(){

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.bottomVideoView).setVisibility(View.VISIBLE);
        try {
           // Parsing json object response
           // response will be a json object
            mDataResponse = mBody.optJSONObject("response");
            mGutterMenus = mBody.optJSONArray("gutterMenu");

            if (mGutterMenus != null) {
                invalidateOptionsMenu();
            }

            if (mDataResponse == null) {
                mDataResponseArray = mBody.optJSONArray("response");
                mDataResponse = mAppConst.convertToJsonObject(mDataResponseArray);
            }


            //Fetch Data from Response

            mVideoId = mDataResponse.optInt("video_id");
            view_count = mDataResponse.optInt("view_count");
            title = mDataResponse.optString("title");
            image_icon = mDataResponse.optString("owner_image_icon");
            String image_profile = mDataResponse.optString("image_profile");
            ownerId = mDataResponse.optInt("owner_id");
            owner_title = mDataResponse.optString("owner_title");
            creation_date = mDataResponse.optString("creation_date");
            mContentUrl = mDataResponse.optString("content_url");
            mRating = mDataResponse.optInt("rating");
            isAlreadyRated = mDataResponse.optBoolean("rated");
            convertedDate = AppConstant.convertDateFormat(getResources(), creation_date);
            mTagObject = mDataResponse.optJSONObject("tags");
            category_title = mDataResponse.optString("category");
            body = mDataResponse.getString("description");
            mCommentCount = mDataResponse.optInt("comment_count");
            mLikeCount = mDataResponse.optInt("like_count");
            isLike = mDataResponse.optBoolean("is_like");
            mBrowseList = new BrowseListItems(mVideoId, title, image_profile, mContentUrl);

            // When video url is not found when activity started then again load video url.
            if (mVideoViewUrl == null || mVideoType == 0) {
                mVideoViewUrl = mDataResponse.optString("video_url");
                mVideoType = mDataResponse.optInt("type");
                loadVideoUrl();
            }


            if (mTagObject != null) {
                mTagNamesArray = mTagObject.names();
                for (int i = 0; i < mTagNamesArray.length(); i++) {
                    String key = mTagNamesArray.getString(i);
                    if (tags == null) {
                        tags = "#"+mTagObject.getString(key);
                    } else {
                        tags += ",#" + mTagObject.getString(key);
                    }
                }
            }

            mViewTitle.setText(title);
            mViewOwnerName.setText(owner_title);

            mImageLoader.setImageForUserProfile(image_icon, mOwnerProfilePic);

            mViewOwnerName.setOnClickListener(this);
            mOwnerProfilePic.setOnClickListener(this);

            /*
            Set Body in TextView
             */
            mViewDescription.setText(Html.fromHtml(body));

            //Setting Ratings for video

            LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                    PorterDuff.Mode.SRC_ATOP);

            if (isAlreadyRated) {
                mRatingBar.setIsIndicator(true);
            }else {
                mRatingBar.setIsIndicator(false);
            }

            mRatingBar.setRating(mRating);
            mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    Map<String, String> params = new HashMap<>();
                    params.put("rating", String.valueOf(mRatingBar.getRating()));
                    postRating(params);
                    ratingBar.setIsIndicator(true);
                }
            });
            mRatingBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mRatingBar.isIndicator()) {
                        SnackbarUtils.displaySnackbar(findViewById(R.id.video_view_page),
                                getResources().getString(R.string.already_rated_video));
                    }
                    return false;
                }
            });


            if (tags != null) {
                mTagView.setText(Html.fromHtml(tags));
            }

            String detailFormat = mContext.getResources().getString(R.string.video_view_category_date_format);
            String detail = String.format(detailFormat,
                    mContext.getResources().getString(R.string.category_salutation),
                    category_title, convertedDate);
            if(category_title != null && category_title.length() > 0) {
                mViewDetails.setText(Html.fromHtml(detail));
            }else {
                mViewDetails.setText(convertedDate);
            }

            mViewCount.setText(getResources().getQuantityString(R.plurals.view_count,view_count,view_count));

            // Setting up Like and Comment Count
            setLikeAndCommentCount();


        } catch (JSONException e) {
            e.printStackTrace();
            SnackbarUtils.displaySnackbar(findViewById(R.id.video_view_page),
                    getResources().getString(R.string.no_data_available));
        }

    }

    public void setLikeAndCommentCount() {

        if(mAppConst.isLoggedOutUser()){
            findViewById(R.id.likeCommentContent).setVisibility(View.GONE);
        }else{
            findViewById(R.id.likeCommentContent).setVisibility(View.VISIBLE);

            mLikeButton.setActivated(isLike);
            if(!isLike){
                mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            }else{
                mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }

            /*
             Set Like and Comment Count
            */

            // Check if Reactions is enabled, show that content reaction and it's icon here.
            if(mReactionsEnabled == 1 && mReactionsObject != null && mReactionsObject.length() != 0){

                myReaction = mReactionsObject.optJSONObject("my_feed_reaction");

                if(myReaction != null && myReaction.length() != 0){
                    mLikeButton.setText(myReaction.optString("caption"));
                    mLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    String reactionImage = myReaction.optString("reaction_image_icon");
                    mImageLoader.setImageUrl(reactionImage, mReactionIcon);
                    mReactionIcon.setVisibility(View.VISIBLE);
                } else {
                    mReactionIcon.setVisibility(View.GONE);
                    mLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp),
                            null, null, null);
                    mLikeButton.setText(mContext.getResources().getString(R.string.like_text));
                }

            } else{
                mLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                mLikeButton.setText(mContext.getResources().getString(R.string.like_text));
            }

            mLikeCountTextView.setText(String.valueOf(mLikeCount));
            mCommentCountTextView.setText(String.valueOf(mCommentCount));
        }
    }

    public void postRating(Map<String, String> params) {
        String postRatingUrl = AppConstant.DEFAULT_URL + "videos/rate?video_id=" +
                mDataResponse.optInt("video_id");
        mAppConst.postJsonResponseForUrl(postRatingUrl, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(findViewById(R.id.video_view_page), message);
            }
        });
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
        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        } else {

            if (mGutterMenus != null) {

                mGutterMenuUtils.onMenuOptionItemSelected(findViewById(R.id.main_content),
                        findViewById(item.getItemId()), id, mGutterMenus, listingTypeId);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        if (mGutterMenus != null) {
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.VIDEO_MENU_TITLE,
                    mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            /*
            Set Result to Manage page to refresh the page if any changes made in the content.
             */
            if (isContentEdited || isContentDeleted) {
                Intent intent = new Intent();
                setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                if(resultCode == ConstantVariables.VIEW_PAGE_EDIT_CODE){
                    isContentEdited = true;
                    tags = null;
                    makeRequest();
                }
                break;

            case ConstantVariables.USER_PROFILE_CODE:
                PreferencesUtils.updateCurrentModule(mContext, mModuleName);
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (resultCode == ConstantVariables.VIEW_COMMENT_PAGE_CODE && data != null) {
                    mCommentCount = data.getIntExtra(ConstantVariables.PHOTO_COMMENT_COUNT, mCommentCount);
                    mCommentCountTextView.setText(String.valueOf(mCommentCount));
                }
                break;

            default:
                break;

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

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

            case R.id.comment_button:
            case R.id.countsBlock:

                mLikeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=" + mSubjectType
                        + "&subject_id=" + mVideoId + "&viewAllComments=1&page=1&limit=" + AppConstant.LIMIT;
                Intent commentIntent = new Intent(this, Comment.class);
                commentIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                commentIntent.putExtra(ConstantVariables.SUBJECT_ID, mVideoId);
                commentIntent.putExtra("commentCount", mCommentCount);
                commentIntent.putExtra("reactionsEnabled", mReactionsEnabled);
                if(mContentReactions != null){
                    commentIntent.putExtra("popularReactions", mContentReactions.toString());
                }
                startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

            case R.id.owner_image:
            case R.id.ownerTitle:
                Intent intent = new Intent(mContext, userProfile.class);
                intent.putExtra(ConstantVariables.USER_ID, ownerId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((AppCompatActivity)mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.video_mode:
                if (view.getTag().equals("fit_screen")) {
                    view.setTag("original");
                    tvVideoMode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.
                            getDrawable(mContext, R.drawable.ic_zoom_out_24dp), null, null, null);
                    setVideoLayoutParams(0);
                } else {
                    view.setTag("fit_screen");
                    tvVideoMode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.
                            getDrawable(mContext, R.drawable.ic_fullscreen_exit_white), null, null, null);
                    setVideoLayoutParams(1);
                }
                break;

        }

    }

    /**
     * Method to set rule on video view.
     * @param value Setting align true if its 1.
     */
    public void setVideoLayoutParams(int value) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, value);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, value);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, value);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, value);
        videoView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoWebView != null){
            mVideoWebView.onResume();
        }
        if (videoView != null) {
            stopPosition = (videoView.getDuration() < videoView.getCurrentPosition() + 500) ? 100 : stopPosition;
            videoView.seekTo(stopPosition);
            videoView.start();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mVideoWebView != null){
            mVideoWebView.onPause();
        }
        if (videoView != null) {
            stopPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int[] location = new int[2];
        mLikeBlock.getLocationOnScreen(location);
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

        if (mAllReactionObject != null && mReactionsArray != null) {

            reactionsImages = new ArrayList<>();
            for (int i = 0; i < mReactionsArray.size(); i++) {
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
                            if (myReaction != null) {
                                if (myReaction.optInt("reactionicon_id") != reactionId) {
                                    doLikeUnlike(reaction, true, reactionId, reactionIcon, caption);
                                }
                            } else {
                                doLikeUnlike(reaction, false, reactionId, reactionIcon, caption);
                            }
                        }
                    });

            reactionsRecyclerView.setAdapter(reactionsAdapter);
        }
        return true;
    }

    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.video_view_page),
                successMessage, new SnackbarUtils.OnSnackbarDismissListener() {
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
    }

    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption){

        mLikeButton.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mLikeButton.setText("\uf110");
        mLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        if (PreferencesUtils.isNestedCommentEnabled(mContext)) {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        } else {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "send-notification";
        }

        mReactionIcon.setVisibility(View.GONE);
        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, mSubjectType);
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mVideoId));

        if(reaction != null){
            likeParams.put("reaction", reaction);
        }

        if(!isLike || isReactionChanged){
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
                mLikeButton.setTypeface(null);

                // Set My FeedReaction Changed
                if( mReactionsEnabled == 1){
                    /* If a Reaction is posted or a reaction is changed on content
                       put the updated reactions in my feed reactions array
                     */
                    updateContentReactions(reactionId, reactionIcon, caption, isLike, isReactionChanged);

                    /* Calling to send notifications after like action */
                    if (!isLike) {
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
                mLikeButton.setTypeface(null);
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

}
