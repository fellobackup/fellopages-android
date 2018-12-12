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

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.SearchActivity;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesRecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.ui.hashtag.OnTagClickListener;
import com.fellopages.mobileapp.classes.common.ui.hashtag.TagSelectingTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO Major Code Optimization Required
public class AdvVideoView extends AppCompatActivity implements View.OnClickListener,
        OnOptionItemClickResponseListener, View.OnLongClickListener, OnTagClickListener {

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_PLAY_BACK_POSITION = "playBackPosition";
    private PlayerView playerView;
    private SimpleExoPlayer exoPlayer;
    private boolean playWhenReady;
    // Member variables.
    private Context mContext;
    private View mMainView, mScrollView;
    private TextView tvViewOwnerName, tvLike, tvComment, tvSubscribe, tvAddToPlaylist, tvWatchLater,
            tvFavourite, tvTags, tvVideoMode;
    private SelectableTextView tvTitle, tvDetails, tvViewCount, tvDescription;
    private ImageView ivReactionIcon;
    private RatingBar rbVideo;
    private ProgressBar pbVideoLoading;
    private WebView webViewVideo;
    private BezelImageView ivOwnerProfile;
    private VideoView videoView;
    private LinearLayout llLikeBlock;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseAdapter;
    private CollapsingToolbarLayout.LayoutParams layoutParams;
    private MediaController mController;
    private String mItemViewUrl, mVideoViewUrl, tags = null, mModuleName, mSubjectType, mChannelTitle;
    private int mVideoType, mViewId, mReactionsEnabled, listingTypeId, mChannelId, mCommentCount,
            mLikeCount, mVideoId, ownerId, mAddToWatchLater, mAddedToFavourite, mVideoSubscribed;
    private boolean isLike, isContentEdited = false, isContentDeleted = false, isChannel = false,
            isPasswordProtected = false, isPasswordVerified = false, isSiteVideoEnabled;
    private ArrayList<JSONObject> mReactionsArray;
    private List<ImageViewList> reactionsImages;
    private List<Object> mBrowseItemList;
    private Map<String, String> mPostParams, mActionUrlParams, mTagList;
    private JSONArray mGutterMenus;
    private JSONObject mDataResponse, mBody, mReactionsObject, myReaction, mAllReactionObject, mContentReactions;
    private BrowseListItems mBrowseList;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private AlertDialogWithAction mAlertDialogWithAction;
    private TagSelectingTextView mTagSelectingTextView;
    private ImageLoader mImageLoader;
    private String sendLikeNotificationUrl;
    private int mVideoHeight, resizeMode = 1;
    private TextView mToolBarTitle;
    private Toolbar mToolbar;
    private AppBarLayout appBarLayout;
    private int mResumeWindow;
    private long mPlayBackPosition;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view_page);

        // Getting screen orientation.
        int orientation = getResources().getConfiguration().orientation;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        backArrow.setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mPlayBackPosition = savedInstanceState.getLong(STATE_PLAY_BACK_POSITION);
        }
        // Getting layout params of the main view.
        layoutParams = (CollapsingToolbarLayout.LayoutParams) findViewById(R.id.main_content).getLayoutParams();

        // Initializing member variables.
        mContext = this;
        mTagList = new HashMap<>();
        mPostParams = new HashMap<>();
        mActionUrlParams = new HashMap<>();
        mBrowseItemList = new ArrayList<>();
        mTagSelectingTextView = new TagSelectingTextView();
        mImageLoader = new ImageLoader(getApplicationContext());
        mVideoHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_250dp);

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        // Getting intent values.
        mModuleName = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mViewId = getIntent().getIntExtra(ConstantVariables.VIEW_ID, 0);

        mItemViewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        listingTypeId = getIntent().getIntExtra(ConstantVariables.LISTING_TYPE_ID, 0);
        mVideoType = getIntent().getIntExtra(ConstantVariables.VIDEO_TYPE, 0);
        String videoSubjectType = getIntent().getStringExtra(ConstantVariables.VIDEO_SUBJECT_TYPE);
        isSiteVideoEnabled = getIntent().getIntExtra(ConstantVariables.ADV_VIDEO_INTEGRATED, 0) == 1;
        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        if (mItemViewUrl == null || mItemViewUrl.isEmpty()) {
            mItemViewUrl = UrlUtil.ADV_VIDEO_VIEW_URL + mViewId + "?gutter_menu=" + 1;
        }

        if (videoSubjectType != null && !videoSubjectType.isEmpty() && !isSiteVideoEnabled) {
            mSubjectType = videoSubjectType;
        } else if (isSiteVideoEnabled){
            mSubjectType = "sitevideo_video";
        } else {
            mSubjectType = "video";
        }

        // Getting all the views.
        getViews();
        pbVideoLoading.setVisibility(View.VISIBLE);

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

        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            assert getSupportActionBar() != null;
            getSupportActionBar().hide();
            mScrollView.setVisibility(View.GONE);
            findViewById(R.id.main_content).setLayoutParams(CustomViews.getFullWidthHeightLayoutParams());
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        // Hide Rating Bar in case of Logged-out user
        if (mAppConst.isLoggedOutUser()) {
            rbVideo.setVisibility(View.GONE);
        }

        mReactionsEnabled = PreferencesUtils.getReactionsEnabled(this);

        /*
            Check if Reactions and nested comment plugin is enabled on the site
            send request to get the reactions on a particular content
            send this request only if the reactions Enabled is not saved yet in Preferences
             or if it is set to 1
         */
        if (mReactionsEnabled == 1 || mReactionsEnabled == -1) {
            String getContentReactionsUrl = UrlUtil.CONTENT_REACTIONS_URL + "&subject_type=" + mSubjectType +
                    "&subject_id=" + mViewId;
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
                    loadViewPageData();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    // Send Request to load View page data after fetching Reactions on the content.
                    loadViewPageData();
                }
            });
        } else {
            loadViewPageData();
        }

        //Getting the reference of adapter.
        mBrowseAdapter = new AdvModulesRecyclerViewAdapter(mContext, mBrowseItemList, "videos",
                ConstantVariables.ADV_VIDEO_MENU_TITLE, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.LOGD(AdvVideoView.class.getSimpleName(), "onItemClick: " + position);
                BrowseListItems browseListItems = (BrowseListItems) mBrowseItemList.get(position);
                Intent viewIntent = AdvVideoUtil.getViewPageIntent(mContext, browseListItems.getmListItemId(),
                        browseListItems.getmVideoUrl(), new Bundle());
                viewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_MENU_TITLE);
                startActivity(viewIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        mRecyclerView.setAdapter(mBrowseAdapter);

    }

    /**
     * Check if there is reponse coming from creation page then load data directly.
     */
    private void loadViewPageData() {
        if (mBody != null) {
            isContentEdited = true;
            isPasswordProtected = mBody.optInt("is_password") == 1;
            if (isPasswordProtected && !isPasswordVerified) {
                showPasswordWindow();
            } else {
                setDataInView();
            }
        } else {
            makeRequest();
        }
    }

    /**
     * Method to get all the view page views.
     */
    private void getViews() {

        findViewById(R.id.ownerDetailView).setVisibility(View.GONE);
        mMainView = findViewById(R.id.video_view_page);
        mScrollView = findViewById(R.id.bottomAreaScroller);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        /*
        Like and Unlike Fields...
         */
        llLikeBlock = (LinearLayout) findViewById(R.id.like_block);
        ivReactionIcon = (ImageView) findViewById(R.id.reaction_icon);
        tvLike = (TextView) findViewById(R.id.like_view);
        tvLike.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvComment = (TextView) findViewById(R.id.comment_view);
        llLikeBlock.setOnClickListener(this);
        llLikeBlock.setOnLongClickListener(this);
        tvComment.setOnClickListener(this);

        // Video title and description fields.
        tvTitle = (SelectableTextView) findViewById(R.id.video_title);
        tvDetails = (SelectableTextView) findViewById(R.id.video_detail);
        tvViewCount = (SelectableTextView) findViewById(R.id.video_view_count);
        tvViewOwnerName = (TextView) findViewById(R.id.owner_name);
        tvDescription = (SelectableTextView) findViewById(R.id.video_description);
        ivOwnerProfile = (BezelImageView) findViewById(R.id.owner_icon);
        pbVideoLoading = (ProgressBar) findViewById(R.id.loadingProgress);
        rbVideo = (RatingBar) findViewById(R.id.smallRatingBar);
        tvTags = (TextView) findViewById(R.id.tagView);
        tvVideoMode = (TextView) findViewById(R.id.video_mode);

        //Setting up local video player
        videoView = (VideoView) findViewById(R.id.video_player);
        mController = new MediaController(this);
        mController.setAnchorView(videoView);
        videoView.setMediaController(mController);
        videoView.requestFocus();

        //Setting up WebView for playing YouTube, Vimeo, DailyMotion and EmbedCode videos.
        webViewVideo = (WebView) findViewById(R.id.webView);

        // Advanced video options.
        tvSubscribe = (TextView) findViewById(R.id.subscribe);
        tvAddToPlaylist = (TextView) findViewById(R.id.add_to_list);
        tvWatchLater = (TextView) findViewById(R.id.watch_later);
        tvFavourite = (TextView) findViewById(R.id.favourite);
        tvSubscribe.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvSubscribe.setOnClickListener(this);
        tvAddToPlaylist.setOnClickListener(this);
        tvWatchLater.setOnClickListener(this);
        tvFavourite.setOnClickListener(this);

        playerView = (PlayerView) findViewById(R.id.exo_video_player);
        appBarLayout = findViewById(R.id.appbar);
    }

    /**
     * Method to load video url in VideoView/WebView when the activity is started.
     *
     * @param isLoadingFromResponse True if the method is being called from the server resopnse's video url.
     */
    public void loadVideoUrl(boolean isLoadingFromResponse) {
        pbVideoLoading.setVisibility(View.VISIBLE);
        if (mVideoViewUrl != null && !mVideoViewUrl.isEmpty() && !mVideoViewUrl.equals("null")) {
            if (mVideoType == 3) {
                pbVideoLoading.setVisibility(View.VISIBLE);
                playWhenReady = true;
                initializePlayer(mVideoViewUrl);

            } else {

                //Auto playing videos in webview
                webViewVideo.setClickable(true);
                webViewVideo.setFocusableInTouchMode(true);
                webViewVideo.getSettings().setJavaScriptEnabled(true);
                findViewById(R.id.main_content).setVisibility(View.VISIBLE);
                webViewVideo.getSettings().setAppCacheEnabled(true);
                webViewVideo.getSettings().setDomStorageEnabled(true);

                if (!mVideoViewUrl.contains("http://") &&
                        !mVideoViewUrl.contains("https://")) {
                    mVideoViewUrl = "http://" + mVideoViewUrl;
                }

                if (mVideoViewUrl.contains("youtube")) {
                    Map<String, String> extraHeaders = new HashMap<>();
                    extraHeaders.put("Referer", "http://www.youtube.com");
                    webViewVideo.loadUrl(mVideoViewUrl, extraHeaders);
                } else {
                    webViewVideo.loadUrl(mVideoViewUrl);
                }

                webViewVideo.setWebChromeClient(new WebChromeClient());
                webViewVideo.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        String javascript = "javascript:" +
                                "   document.getElementsByClassName('ytp-large-play-button')[0].click();" +
                                "   setTimeout(function() {" +
                                "   var endScreen = document.getElementsByClassName('videowall-endscreen')[0];" +
                                "   endScreen.style.opacity = 0;" +
                                "   endScreen.style.display = 'none';" +
                                "   var replayButton = document.getElementsByClassName('ytp-icon-replay')[0];" +
                                "   replayButton.style.marginBottom = '90px';" +
                                "   var headerTitle = document.getElementsByClassName('ytp-chrome-top')[0];" +
                                "   headerTitle.style.display = 'none';" +
                                "   }, 2000);";
                        view.loadUrl(javascript);
                        super.onPageFinished(view, url);
                        pbVideoLoading.setVisibility(View.GONE);
                        webViewVideo.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else if (isLoadingFromResponse) {
            //Hiding the loading progress bar when there is not video url is coming either from intent or in response.
            pbVideoLoading.setVisibility(View.GONE);
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
            playerView.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
            appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layoutParams.height = mAppConst.getScreenHeight();
            findViewById(R.id.main_content).setLayoutParams(layoutParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            assert getSupportActionBar() != null;
            getSupportActionBar().show();
            mScrollView.setVisibility(View.VISIBLE);
            playerView.setPadding(0, 0, 0, 0);
            appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.height = mVideoHeight;
            findViewById(R.id.main_content).setLayoutParams(layoutParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * Method to send request on the server to get the view page data.
     */
    public void makeRequest() {
        mAppConst.getJsonResponseFromUrl(mItemViewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                isPasswordProtected = mBody.optInt("is_password") == 1;
                if (isPasswordProtected && !isPasswordVerified) {
                    showPasswordWindow();
                } else {
                    setDataInView();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(mMainView, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }
        });
    }

    /**
     * Method to show video protection dialog.
     */
    private void showPasswordWindow() {
        pbVideoLoading.setVisibility(View.GONE);
        mAlertDialogWithAction.showPasswordDialog(mContext,
                new AlertDialogWithAction.OnPositionButtonClickListener() {
                    @Override
                    public void onButtonClick(final TextInputLayout inputLayout, final AlertDialog alertDialog) {
                        inputLayout.setError(null);
                        String password = inputLayout.getEditText().getText().toString();
                        if (password.length() > 0 && !password.trim().isEmpty()) {
                            mAppConst.hideKeyboardInDialog(inputLayout);
                            mAppConst.showProgressDialog();
                            mPostParams.clear();
                            mPostParams.put("password", password);
                            mAppConst.postJsonResponseForUrl(AppConstant.DEFAULT_URL + "advancedvideo/password-protection/"
                                    + mViewId, mPostParams, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                                    mAppConst.hideProgressDialog();
                                    alertDialog.dismiss();
                                    isPasswordVerified = true;
                                    setDataInView();
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mAppConst.hideProgressDialog();
                                    inputLayout.setError(message);
                                }
                            });
                        } else {
                            inputLayout.setError(mContext.getResources().getString(R.string.widget_error_msg));
                        }
                    }
                });
    }

    /**
     * Method to set view page data into the respective views.
     */
    public void setDataInView() {

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
                JSONArray mDataResponseArray = mBody.optJSONArray("response");
                mDataResponse = mAppConst.convertToJsonObject(mDataResponseArray);
            }

            //Fetch Data from Response
            mVideoId = mDataResponse.optInt("video_id");
            ownerId = mDataResponse.optInt("owner_id");
            mCommentCount = mDataResponse.optInt("comment_count");
            mLikeCount = mDataResponse.optInt("like_count");
            isLike = mDataResponse.optBoolean("is_like");

            int viewCount = mDataResponse.optInt("view_count");
            int mRating = mDataResponse.optInt("rating");
            boolean isAlreadyRated = mDataResponse.optBoolean("rated");
            String title = mDataResponse.optString("title");
            String ownerImageIcon = mDataResponse.optString("owner_image_icon");
            String owner_title = mDataResponse.optString("owner_title");
            String creation_date = mDataResponse.optString("creation_date");
            String contentUrl = mDataResponse.optString("content_url");
            String convertedDate = AppConstant.convertDateFormat(getResources(), creation_date);
            JSONObject jsonObjectTag = mDataResponse.optJSONObject("tags");
            String categoryTitle = mDataResponse.optString("category");
            String body = mDataResponse.getString("description");
            mChannelId = mDataResponse.optInt("main_channel_id");
            mChannelTitle = mDataResponse.optString("channel_title");
            String channelIcon = mDataResponse.optString("channel_image");

            // Adding data into model.
            mBrowseList = new BrowseListItems(mVideoId, title, mDataResponse.optString("image"), contentUrl);

            // loading video url.
            if (mVideoViewUrl == null || mVideoViewUrl.isEmpty()) {
                mVideoViewUrl = mDataResponse.optString("video_url");
                mVideoType = mDataResponse.optInt("type");
                loadVideoUrl(true);
            }

            // Getting tags.
            if (jsonObjectTag != null) {
                JSONArray tagNamesArray = jsonObjectTag.names();
                for (int i = 0; i < tagNamesArray.length(); i++) {
                    String key = tagNamesArray.getString(i);
                    String value = jsonObjectTag.getString(key).replaceAll("\\s+", "").replaceAll("#", "");
                    mTagList.put(value, key);
                    if (tags == null) {
                        tags = "#" + value;
                    } else {
                        tags += ",#" + value;
                    }
                }
            }

            // Showing the info into views.
            tvTitle.setText(title);
            tvViewOwnerName.setText(owner_title);
            tvDescription.setText(Html.fromHtml(body));

            // Showing the owner profile image, and setting up the click listener on user icon and name.
            if (mChannelId != 0 && channelIcon != null && !channelIcon.isEmpty() && mChannelTitle != null
                    && !mChannelTitle.isEmpty()) {
                isChannel = true;
                tvViewOwnerName.setText(mChannelTitle);
                mImageLoader.setImageForUserProfile(channelIcon, ivOwnerProfile);

            } else if (ownerImageIcon != null && !ownerImageIcon.isEmpty()) {
                mImageLoader.setImageForUserProfile(ownerImageIcon, ivOwnerProfile);
            }
            findViewById(R.id.owner_layout).setOnClickListener(this);

            //Setting Ratings for video.
            LayerDrawable stars = (LayerDrawable) rbVideo.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                    PorterDuff.Mode.SRC_ATOP);
            if (isAlreadyRated) {
                rbVideo.setIsIndicator(true);
            } else {
                rbVideo.setIsIndicator(false);
            }

            rbVideo.setRating(mRating);
            rbVideo.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    Map<String, String> params = new HashMap<>();
                    params.put("rating", String.valueOf(rbVideo.getRating()));
                    params.put("video_id", mDataResponse.optString("video_id"));
                    postRating(params);
                    ratingBar.setIsIndicator(true);
                }
            });
            rbVideo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (rbVideo.isIndicator()) {
                        SnackbarUtils.displaySnackbar(mMainView,
                                getResources().getString(R.string.already_rated_video));
                    }
                    return false;
                }
            });

            // Showing the tag into tag view.
            if (tags != null) {
                tvTags.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.body_default_font_size));
                tvTags.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_3dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_3dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_3dp));
                tvTags.setMovementMethod(LinkMovementMethod.getInstance());
                tvTags.setText(mTagSelectingTextView.addClickablePart(tags, this, 0,
                        ConstantVariables.DEFAULT_HASHTAG_COLOR), TextView.BufferType.SPANNABLE);
            }

            // Showing the category and date details.
            String detailFormat = mContext.getResources().getString(R.string.video_view_category_date_format);
            String detail = String.format(detailFormat,
                    mContext.getResources().getString(R.string.category_salutation),
                    categoryTitle, convertedDate);
            if (categoryTitle != null && !categoryTitle.isEmpty() && !categoryTitle.equals("null")) {
                tvDetails.setText(Html.fromHtml(detail));
            } else {
                tvDetails.setText(convertedDate);
            }

            tvViewCount.setText(getResources().getQuantityString(R.plurals.view_count, viewCount, viewCount));

            // Setting up Like and Comment Count
            setLikeAndCommentCount();

            // Setting up the advanced video options.
            setAdvancedVideoOptions();

            JSONArray relatedVideoArray = mBody.optJSONArray("relatedVideo");
            if (relatedVideoArray != null && relatedVideoArray.length() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));
                loadVideosResponse(relatedVideoArray);
            } else {
                mRecyclerView.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            SnackbarUtils.displaySnackbar(mMainView, getResources().getString(R.string.no_data_available));
        }
    }

    /**
     * Method to load videos into recycler view.
     *
     * @param videosArray JsonArray of videos.
     */
    private void loadVideosResponse(JSONArray videosArray) {

        for (int i = 0; i < videosArray.length(); i++) {
            JSONObject jsonDataObject = videosArray.optJSONObject(i);
            int videoId = jsonDataObject.optInt("video_id");
            int duration = jsonDataObject.optInt("duration");
            String title = jsonDataObject.optString("title");
            String ownerTitle = jsonDataObject.optString("owner_title");
            if (jsonDataObject.optInt("owner_id") == 0) {
                ownerTitle = mContext.getResources().getString(R.string.deleted_member_text);
            }
            String image = jsonDataObject.optString("image");
            int allowToView = jsonDataObject.optInt("allow_to_view");
            String videoUrl = jsonDataObject.optString("video_url");
            int type = jsonDataObject.optInt("type");
            mBrowseItemList.add(new BrowseListItems(videoId, title, ownerTitle, image, duration, videoUrl,
                    type, allowToView == 1, false));

        }
        mBrowseAdapter.notifyDataSetChanged();
    }

    /**
     * Method to check advanced video options(Watch Later, Subscribe, Favorite, Add to Playlist)
     */
    private void setAdvancedVideoOptions() {
        findViewById(R.id.adv_video_layout).setVisibility(View.VISIBLE);
        JSONArray menuArray = mBody.optJSONArray("menus");
        if (menuArray != null && menuArray.length() > 0) {
            for (int i = 0; i < menuArray.length(); i++) {
                JSONObject menuObject = menuArray.optJSONObject(i);
                JSONObject urlParams = menuObject.optJSONObject("urlParams");
                if (urlParams == null) {
                    urlParams = new JSONObject();
                }
                mActionUrlParams.put(menuObject.optString("name"), AppConstant.DEFAULT_URL + menuObject.optString("url"));
                switch (menuObject.optString("name")) {
                    case "watch_later":
                        tvWatchLater.setVisibility(View.VISIBLE);
                        mAddToWatchLater = urlParams.optInt("value");
                        setViewTextColor(mAddToWatchLater, tvWatchLater, R.drawable.ic_schedule_24dp);
                        break;

                    case "playlist":
                        tvAddToPlaylist.setVisibility(View.VISIBLE);
                        setViewTextColor(1, tvAddToPlaylist, R.drawable.ic_playlist_add_24dp);
                        break;

                    case "favourite":
                        tvFavourite.setVisibility(View.VISIBLE);
                        mAddedToFavourite = urlParams.optInt("value");
                        setViewTextColor(mAddedToFavourite, tvFavourite, R.drawable.ic_favorite_filled_24);
                        break;

                    case "subscribe":
                        tvSubscribe.setVisibility(View.VISIBLE);
                        mVideoSubscribed = urlParams.optInt("value");
                        setSubscribeTextView();
                        break;
                }
            }

        }
    }

    /**
     * Method to set text color on the text view according to value.
     *
     * @param value      Value of the respective field.
     * @param tvOption   TextView on which color is to be applied.
     * @param drawableId Id of the drawable which will set onto the text view.
     */
    private void setViewTextColor(int value, TextView tvOption, int drawableId) {
        int color;
        if (value == 0) {
            color = ContextCompat.getColor(mContext, R.color.themeButtonColor);
        } else {
            color = ContextCompat.getColor(mContext, R.color.gray_text_color);
        }
        tvOption.setTextColor(color);
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        tvOption.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    /**
     * Method to set text in Subscribe Text view according to subscribe value.
     */
    private void setSubscribeTextView() {
        if (mVideoSubscribed == 0) {
            tvSubscribe.setText(mContext.getResources().getString(R.string.subscribed_menu_label));
        } else {
            tvSubscribe.setText(mContext.getResources().getString(R.string.subscribe_dialogue_button));
        }
    }

    /**
     * Method to set like and comment count.
     */
    public void setLikeAndCommentCount() {

        if (mAppConst.isLoggedOutUser()) {
            findViewById(R.id.options_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.options_layout).setVisibility(View.VISIBLE);
            ivReactionIcon.setVisibility(View.VISIBLE);
            if (!isLike) {
                tvLike.setTextColor(ContextCompat.getColor(mContext, R.color.gray_text_color));
            } else {
                tvLike.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }

            /*
             Set Like and Comment Count
            */

            // Check if Reactions is enabled, show that content reaction and it's icon here.
            if (mReactionsEnabled == 1 && mReactionsObject != null && mReactionsObject.length() != 0) {

                myReaction = mReactionsObject.optJSONObject("my_feed_reaction");

                if (myReaction != null && myReaction.length() != 0) {
                    String reactionImage = myReaction.optString("reaction_image_icon");
                    mImageLoader.setImageUrl(reactionImage, ivReactionIcon);
                } else {
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp);
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color),
                            PorterDuff.Mode.SRC_ATOP);
                    ivReactionIcon.setImageDrawable(drawable);
                }

            } else {
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp);
                if (isLike) {
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor),
                            PorterDuff.Mode.SRC_ATOP);
                } else {
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color),
                            PorterDuff.Mode.SRC_ATOP);
                }
                ivReactionIcon.setImageDrawable(drawable);
            }
            tvLike.setText(String.valueOf(mLikeCount));
            tvComment.setText(String.valueOf(mCommentCount));
            setViewTextColor(1, tvComment, R.drawable.ic_chat_bubble_white_24dp);
        }
    }

    /**
     * Method to post user applied rating.
     *
     * @param params PostParams which contains the User applied rating.
     */
    public void postRating(Map<String, String> params) {
        String postRatingUrl;
        if (mSubjectType.equals("video")) {
             postRatingUrl = AppConstant.DEFAULT_URL + "videos/rate?video_id=" +
                    mDataResponse.optInt("video_id");
        } else {
             postRatingUrl = AppConstant.DEFAULT_URL + "advancedvideo/rate/"
                    + mDataResponse.optInt("video_id") + "?video_id=" + mDataResponse.optInt("video_id");
        }

        mAppConst.postJsonResponseForUrl(postRatingUrl, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mMainView, message);
            }
        });
    }

    /**
     * Method to perform action according to menu name.
     *
     * @param value    Value of the field.
     * @param menuName MenuName for which action is to be performed.
     */
    private void performAction(int value, String menuName) {
        mPostParams.clear();
        mPostParams.put("value", String.valueOf(value));
        mAppConst.postJsonRequest(mActionUrlParams.get(menuName), mPostParams);
        switch (menuName) {
            case "watch_later":
                mAddToWatchLater = (value == 1) ? 0 : 1;
                setViewTextColor(mAddToWatchLater, tvWatchLater, R.drawable.ic_schedule_24dp);
                break;

            case "favourite":
                mAddedToFavourite = (value == 1) ? 0 : 1;
                setViewTextColor(mAddedToFavourite, tvFavourite, R.drawable.ic_favorite_filled_24);
                break;

            case "subscribe":
                mVideoSubscribed = (value == 1) ? 0 : 1;
                setSubscribeTextView();
                break;
        }
    }

    @Override
    public void clickedTag(CharSequence tag) {
        String tagName = tag.toString().replaceAll("#", "");
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra("tag", tagName);
        intent.putExtra("tag_id", mTagList.get(tagName));
        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_MENU_TITLE);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        } else if (mGutterMenus != null) {
            mGutterMenuUtils.onMenuOptionItemSelected(findViewById(R.id.main_content),
                    findViewById(item.getItemId()), id, mGutterMenus, listingTypeId);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        if (mGutterMenus != null) {
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.ADV_VIDEO_MENU_TITLE,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                if (resultCode == ConstantVariables.VIEW_PAGE_EDIT_CODE) {
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
                    tvComment.setText(String.valueOf(mCommentCount));
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;

        // Sending request only when the network is available. Else show the error.
        if (GlobalFunctions.isNetworkAvailable(mContext)) {
            switch (id) {
                case R.id.like_block:
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

                case R.id.comment_view:
                case R.id.countsBlock:

                    String mLikeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=" + mSubjectType
                            + "&subject_id=" + mVideoId + "&viewAllComments=1&page=1&limit=" + AppConstant.LIMIT;
                    Intent commentIntent = new Intent(this, Comment.class);
                    commentIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                    commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                    commentIntent.putExtra(ConstantVariables.SUBJECT_ID, mVideoId);
                    commentIntent.putExtra("commentCount", mCommentCount);
                    commentIntent.putExtra("reactionsEnabled", mReactionsEnabled);
                    if (mContentReactions != null) {
                        commentIntent.putExtra("popularReactions", mContentReactions.toString());
                    }
                    startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    break;

                case R.id.owner_layout:
                    if (isChannel) {
                        intent = AdvVideoUtil.getChannelViewPageIntent(mContext, mChannelId,
                                AppConstant.DEFAULT_URL, new Bundle());
                        startActivityForResult(intent, ConstantVariables.VIEW_PAGE_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        intent = new Intent(mContext, userProfile.class);
                        intent.putExtra(ConstantVariables.USER_ID, ownerId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ((AppCompatActivity) mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    break;

                case R.id.subscribe:
                    if (mVideoSubscribed == 0) {
                        mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().
                                        getString(R.string.unsubscribe_from)
                                        + " <b>" + mChannelTitle + "</b>.",
                                mContext.getResources().getString(R.string.unsubscribe_listing_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        performAction(mVideoSubscribed, "subscribe");
                                    }
                                });
                    } else {
                        performAction(mVideoSubscribed, "subscribe");
                    }
                    break;

                case R.id.add_to_list:
                    intent = new Intent(mContext, CreateNewEntry.class);
                    intent.putExtra(ConstantVariables.CREATE_URL, mActionUrlParams.get("playlist"));
                    intent.putExtra(ConstantVariables.FORM_TYPE, "add_to_playlist");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_MENU_TITLE);
                    startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case R.id.watch_later:
                    performAction(mAddToWatchLater, "watch_later");
                    break;

                case R.id.favourite:
                    performAction(mAddedToFavourite, "favourite");
                    break;

                case R.id.video_mode:
                    if (view.getTag().equals("fit_screen")) {
                        view.setTag("original");
                        tvVideoMode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.
                                getDrawable(mContext, R.drawable.ic_zoom_out_24dp), null, null, null);

                    } else {
                        view.setTag("fit_screen");
                        tvVideoMode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.
                                getDrawable(mContext, R.drawable.ic_fullscreen_exit_white), null, null, null);

                    }
                    break;
            }
        } else {
            SnackbarUtils.displaySnackbar(mMainView,
                    mContext.getResources().getString(R.string.network_connectivity_error));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (webViewVideo != null) {
            webViewVideo.onResume();
        }
        if (mVideoType == 3) {
            loadVideoUrl(false);
            startPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webViewVideo != null) {
            webViewVideo.onPause();
        }
        pausePlayer();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public boolean onLongClick(View v) {
        int[] location = new int[2];
        llLikeBlock.getLocationOnScreen(location);
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

    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mMainView, successMessage,
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
        if (menuName.equals("download")) {
            if (mVideoViewUrl != null && !mVideoViewUrl.isEmpty() && !mVideoViewUrl.equals("null")) {

                // Getting the DownloadManager Request.
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mVideoViewUrl));
                request.setTitle(mVideoViewUrl.substring(mVideoViewUrl.lastIndexOf("/") + 1));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        mVideoViewUrl.substring(mVideoViewUrl.lastIndexOf("/")));

                // Get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            } else {
                SnackbarUtils.displaySnackbar(mMainView,
                        mContext.getResources().getString(R.string.video_not_valid));
            }
        }
    }

    /**
     * Method to enable/disable like block click event.
     *
     * @param makeClickable True if its enable request.
     */
    private void setLikeViewClickable(boolean makeClickable) {
        tvLike.setClickable(makeClickable);
        llLikeBlock.setClickable(makeClickable);
        llLikeBlock.setLongClickable(makeClickable);
    }

    /**
     * Method to perform like and unlike on video.
     */
    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption) {

        tvLike.setText("\uf110");
        setLikeViewClickable(false);
        ivReactionIcon.setImageResource(R.color.white);

        if (PreferencesUtils.isNestedCommentEnabled(mContext)) {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        } else {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "send-notification";
        }

        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, mSubjectType);
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mVideoId));

        if (reaction != null) {
            likeParams.put("reaction", reaction);
        }

        String mLikeUnlikeUrl;
        if (!isLike || isReactionChanged) {
            if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "advancedcomments/like?sendNotification=0";
            } else {
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "like";
            }
        } else {
            mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "unlike";
        }

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                setLikeViewClickable(true);
                // Set My FeedReaction Changed
                if (mReactionsEnabled == 1) {
                    /* If a Reaction is posted or a reaction is changed on content
                       put the updated reactions in my feed reactions array
                     */
                    updateContentReactions(reactionId, reactionIcon, caption, isLike, isReactionChanged);

                    /* Calling to send notifications after like action */
                    if (!isLike) {
                        mAppConst.postJsonRequest(sendLikeNotificationUrl, likeParams);
                    }
                }

                /*
                 Increase Like Count if content is liked else
                 decrease like count if the content is unliked
                 Do not need to increase/decrease the like count when it is already liked and only reaction is changed.
                  */
                if (!isLike) {
                    mLikeCount += 1;
                } else if (!isReactionChanged) {
                    mLikeCount -= 1;
                }

                // Toggle isLike Variable if reaction is not changed
                if (!isReactionChanged)
                    isLike = !isLike;

                setLikeAndCommentCount();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                setLikeViewClickable(true);
                setLikeAndCommentCount();
                SnackbarUtils.displaySnackbar(findViewById(R.id.main_content), message);
            }

        });
    }

    /**
     * Method to update content reaction on like/unlike.
     */
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

    private void initializePlayer(String videoURL) {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(exoPlayer);

        exoPlayer.setPlayWhenReady(playWhenReady);
        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.prepare(mediaSource, true, false);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                        pbVideoLoading.setVisibility(View.GONE);
                        if (mPlayBackPosition > 0) {
                            exoPlayer.seekTo(mResumeWindow, mPlayBackPosition);
                            mPlayBackPosition = 0;
                        }
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        pbVideoLoading.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        playerView.setFastForwardIncrementMs(ConstantVariables.VALUE_FAST_FORWARD_INCREMENT);
        playerView.setRewindIncrementMs(ConstantVariables.VALUE_REWIND_INCREMENT);
        _initMediaAction();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            mPlayBackPosition = exoPlayer.getCurrentPosition();
            mResumeWindow = exoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    private void pausePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.getPlaybackState();
        }
    }

    private void startPlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.getPlaybackState();
        }
    }

    private void _initMediaAction() {

        PlaybackControlView controlView = playerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_fit_x));
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerView.setResizeMode(resizeMode);

                switch (resizeMode) {
                    case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_fit_x));
                        break;
                    case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_fit_y));
                        break;
                    case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_view_carousel));
                        break;
                    case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_crop));
                        break;
                    case AspectRatioFrameLayout.RESIZE_MODE_ZOOM:
                        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_fullscreen));
                        break;
                }
                if (resizeMode == 4) {
                    resizeMode = 0;
                } else {
                    resizeMode++;
                }
            }
        });
        View rotateIcon = controlView.findViewById(R.id.exo_rotate_icon);
        rotateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOrientation(getRequestedOrientation());
            }
        });
    }

    private void toggleOrientation(int orientation) {
        try {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_PLAY_BACK_POSITION, mPlayBackPosition);
        super.onSaveInstanceState(outState);
    }


}
