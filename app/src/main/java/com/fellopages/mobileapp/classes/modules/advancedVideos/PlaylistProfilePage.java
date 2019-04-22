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

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesRecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnVideoSourceLoadCompleteListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GetVideoDataSourceUtils;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to show playlist of videos. And play all the videos in the cover layout of the view.
 */
public class PlaylistProfilePage extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        OnOptionItemClickResponseListener, View.OnClickListener {

    // Member variables.
    private Context mContext;
    private View mRootView;
    private ImageView ivCoverImage;
    private TextView tvTitle, tvToolBarTitle, tvVideoMode;
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView mRecyclerView;
    private RelativeLayout rlTitleBlock, rlVideoPlayer;
    private WebView webVideoPlayer;
    private android.widget.VideoView videoViewPlayer;
    private MediaController mController;
    private FloatingActionButton fabPlayAll;
    private ProgressBar mProgressBar, mVideoLoadingProgress;
    private Toolbar mToolbar;
    private AppBarLayout.LayoutParams layoutParams;
    private Drawable toolBarBackgroundDrawable;
    private String mPlaylistProfileUrl, mTitle;
    private int mContentId, mCurrentVideoPosition = 1;
    private boolean isContentEdited = false, isContentDeleted = false, isVideoPlaying = false;
    private JSONObject mBody;
    private JSONArray mGutterMenus;
    private List<Object> mBrowseItemList;
    private ArrayList<PhotoListDetails> mCoverImageDetails;
    private BrowseListItems mBrowseList;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private GetVideoDataSourceUtils mGetVideoDataSourceUtils;
    private RecyclerView.Adapter mBrowseAdapter;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_view);

        // Initializing member variables.
        mContext = PlaylistProfilePage.this;
        mAppConst = new AppConstant(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mBrowseItemList = new ArrayList<>();
        mCoverImageDetails = new ArrayList<>();
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mImageLoader = new ImageLoader(getApplicationContext());

        // Getting all the views.
        getViews();

        /** Getting Intent Key's. **/
        mContentId = getIntent().getIntExtra(ConstantVariables.VIEW_ID, 0);
        mPlaylistProfileUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        if (mPlaylistProfileUrl == null || mPlaylistProfileUrl.isEmpty()) {
            mPlaylistProfileUrl = AppConstant.DEFAULT_URL + "advancedvideo/playlist/view/" + mContentId
                    + "?gutter_menu=1";
        }
        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        //Getting the reference of adapter.
        mBrowseAdapter = new AdvModulesRecyclerViewAdapter(mContext, mBrowseItemList, "videos",
                ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // Making video playing option visible.
                isVideoPlaying = true;
                invalidateOptionsMenu();
                stopVideoPlayingOptions();
                BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                mCurrentVideoPosition = position;
                loadVideoUrl(listItems);
            }
        }, new OnItemDeleteResponseListener() {
            @Override
            public void onItemDelete(int position, boolean isNoVideoLeft) {
                // When the video is playing and the video is removed from the playlist
                // then closing the video playing window,
                if (isVideoPlaying && position == mCurrentVideoPosition) {
                    closeVideoPlayer();
                }

                // when all videos are deleted then removing the item decoration.
                if (isNoVideoLeft) {
                    fabPlayAll.setVisibility(View.GONE);
                    mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext, R.color.white));
                }
            }
        });
        mRecyclerView.setAdapter(mBrowseAdapter);

        // Calling to server.
        // Load Data Directly if Coming from Create Page.
        if (mBody != null && mBody.length() != 0) {
            isContentEdited = true;
            loadViewPageData(mBody);
        } else {
            makeRequest();
        }
    }

    /**
     * Method to get all the view page views.
     */
    private void getViews() {

        // Getting header views.
        mRootView = findViewById(R.id.main_layout);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        fabPlayAll = findViewById(R.id.play_all_btn);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
        tvToolBarTitle = findViewById(R.id.toolbar_title);
        tvToolBarTitle.setSelected(true);
        ivCoverImage = findViewById(R.id.cover_image);
        tvTitle = findViewById(R.id.content_title);
        mProgressBar = findViewById(R.id.progressBar);

        toolBarBackgroundDrawable = mToolbar.getBackground();
        layoutParams = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        rlTitleBlock = findViewById(R.id.title_block);
        rlVideoPlayer = findViewById(R.id.player_layout);
        webVideoPlayer = findViewById(R.id.webView);
        videoViewPlayer = findViewById(R.id.video_player);
        mController = new MediaController(this);
        mController.setAnchorView(videoViewPlayer);
        videoViewPlayer.setMediaController(mController);
        tvVideoMode = findViewById(R.id.video_mode);
        mVideoLoadingProgress = findViewById(R.id.loadingProgress);
        fabPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoPlaying = true;
                invalidateOptionsMenu();
                stopVideoPlayingOptions();
                mCurrentVideoPosition = 1;
                loadVideoUrl((BrowseListItems) mBrowseItemList.get(1));
            }
        });
    }

    /**
     * Sending request to server to get the view page data.
     */
    private void makeRequest() {
        mAppConst.getJsonResponseFromUrl(mPlaylistProfileUrl, new OnResponseListener() {
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

    /**
     * Method to get view page data.
     *
     * @param bodyJsonObject jsonObject, which contains view page data.
     */
    private void loadViewPageData(JSONObject bodyJsonObject) {
        try {
            if (bodyJsonObject != null && bodyJsonObject.length() != 0) {
                mProgressBar.setVisibility(View.GONE);
                mGutterMenus = bodyJsonObject.optJSONArray("gutterMenu");
                JSONObject responseObject = bodyJsonObject.optJSONObject("response");
                mTitle = responseObject.optString("title");
                mContentId = responseObject.optInt("playlist_id");
                int ownerId = responseObject.optInt("owner_id");
                String contentUrl = responseObject.optString("content_url");
                String coverImageUrl = responseObject.optString("image");
                String ownerImage = responseObject.optString("owner_image");
                String ownerTitle = responseObject.optString("owner_title");
                int likeCount = responseObject.optInt("like_count");
                int viewCount = responseObject.optInt("view_count");

                mBrowseList = new BrowseListItems(mContentId, mTitle, coverImageUrl, contentUrl);
                mBrowseItemList.clear();
                mBrowseItemList.add(0, new BrowseListItems(mContentId, ownerTitle, ownerImage, ownerId, likeCount, viewCount));
                if (mGutterMenus != null) {
                    invalidateOptionsMenu();
                }

                //Setting data in views
                tvTitle.setText(mTitle);
                collapsingToolbar.setTitle(mTitle);
                tvToolBarTitle.setText(mTitle);
                CustomViews.setCollapsingToolBarTitle(collapsingToolbar);
                // Showing cover image.
                mImageLoader.setImageUrl(coverImageUrl, ivCoverImage);
                // Adding cover image into list to display it in PhotoLightBox.
                mCoverImageDetails.clear();
                mCoverImageDetails.add(new PhotoListDetails(coverImageUrl));
                ivCoverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mCoverImageDetails);
                        Intent lightBoxIntent = new Intent(mContext, PhotoLightBoxActivity.class);
                        lightBoxIntent.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
                        lightBoxIntent.putExtra(ConstantVariables.SHOW_OPTIONS, false);
                        lightBoxIntent.putExtras(bundle);
                        startActivity(lightBoxIntent);
                    }
                });

                // Showing videos data.
                JSONArray videosArray = bodyJsonObject.optJSONArray("videos");
                if (videosArray != null && videosArray.length() > 0) {
                    fabPlayAll.setVisibility(View.VISIBLE);
                    mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));
                    loadVideosResponse(videosArray);
                } else {
                    fabPlayAll.setVisibility(View.GONE);
                    mBrowseAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            int isRemoveOption = jsonDataObject.optInt("is_remove");
            mBrowseItemList.add(new BrowseListItems(videoId, title, ownerTitle, image, duration, videoUrl,
                    type, allowToView == 1, isRemoveOption == 1));

        }
        mBrowseAdapter.notifyDataSetChanged();
    }

    /**
     * Method to load video url in VideoView/WebView when the any video clicked.
     *
     * @param listItems List item of the current clicked video item.
     */
    private void loadVideoUrl(BrowseListItems listItems) {

        // Showing video player and making the collapsing tool bar non-collapsible,
        // So that video player is visible on scrolling.
        mVideoLoadingProgress.setVisibility(View.VISIBLE);
        webVideoPlayer.setVisibility(View.GONE);
        videoViewPlayer.setVisibility(View.GONE);
        tvVideoMode.setVisibility(View.GONE);
        layoutParams.setScrollFlags(0);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        String videoUrl = listItems.getmVideoUrl();
        if (videoUrl != null && !videoUrl.isEmpty() && !videoUrl.equals("null")) {
            if (listItems.getmVideoType() == 3) {
                try {
                    mGetVideoDataSourceUtils = new GetVideoDataSourceUtils(videoUrl,
                            new OnVideoSourceLoadCompleteListener() {
                                @Override
                                public void onSuccess(String videoPath) {
                                    if (videoPath != null && !videoPath.isEmpty()) {
                                        videoViewPlayer.setVisibility(View.VISIBLE);
                                        videoViewPlayer.setVideoPath(videoPath);
                                        videoViewPlayer.start();
                                        videoViewPlayer.requestFocus();
                                        videoViewPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                if (mCurrentVideoPosition != mBrowseItemList.size() - 1
                                                        && mBrowseItemList.size() > 2) {
                                                    mCurrentVideoPosition = mCurrentVideoPosition + 1;
                                                    stopVideoPlayingOptions();
                                                    loadVideoUrl((BrowseListItems) mBrowseItemList.get(mCurrentVideoPosition));
                                                }
                                            }
                                        });

                                        tvVideoMode.setVisibility(View.VISIBLE);
                                        tvVideoMode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.
                                                getDrawable(mContext, R.drawable.ic_zoom_out_24dp), null, null, null);
                                        tvVideoMode.setTag("original");
                                        tvVideoMode.setOnClickListener(PlaylistProfilePage.this);

                                        if (Build.VERSION.SDK_INT >= 17) {
                                            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                                            metadataRetriever.setDataSource(videoPath);
                                            String rotation = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                                            if (rotation != null && rotation.equals("0")) {
                                                setVideoLayoutParams(1);
                                                tvVideoMode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.
                                                        getDrawable(mContext, R.drawable.ic_fullscreen_exit_white), null, null, null);
                                                tvVideoMode.setTag("fit_screen");
                                            }
                                        }
                                    }
                                    mVideoLoadingProgress.setVisibility(View.GONE);
                                }
                            });
                    mGetVideoDataSourceUtils.execute();
                } catch (Exception e) {
                    if (videoViewPlayer != null) {
                        videoViewPlayer.stopPlayback();
                    }
                    e.printStackTrace();
                }
            } else {

                //Auto playing videos in webview
                webVideoPlayer.setClickable(true);
                webVideoPlayer.setFocusableInTouchMode(true);
                webVideoPlayer.getSettings().setJavaScriptEnabled(true);

                webVideoPlayer.getSettings().setAppCacheEnabled(true);
                webVideoPlayer.getSettings().setDomStorageEnabled(true);

                if (!videoUrl.contains("http://") && !videoUrl.contains("https://")) {
                    videoUrl = "http://" + videoUrl;
                }

                if (videoUrl.contains("youtube")) {
                    Map<String, String> extraHeaders = new HashMap<>();
                    extraHeaders.put("Referer", "http://www.youtube.com");
                    webVideoPlayer.loadUrl(videoUrl, extraHeaders);
                } else {
                    webVideoPlayer.loadUrl(videoUrl);
                }

                webVideoPlayer.setWebChromeClient(new WebChromeClient());
                webVideoPlayer.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mVideoLoadingProgress.setVisibility(View.GONE);
                        webVideoPlayer.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            //Hiding the loading progress bar when there is not video url.
            mVideoLoadingProgress.setVisibility(View.GONE);
        }
    }

    /***
     * Method to stop all video playing options.
     */
    private void stopVideoPlayingOptions() {
        videoViewPlayer.setVisibility(View.GONE);
        tvVideoMode.setVisibility(View.GONE);
        webVideoPlayer.setVisibility(View.GONE);

        // Clearing cache and history of WebView.
        if (webVideoPlayer != null) {
            webVideoPlayer.clearHistory();
            webVideoPlayer.clearCache(true);
            webVideoPlayer.clearFormData();
        }
        // StopPlayBack video player.
        if (videoViewPlayer != null) {
            videoViewPlayer.stopPlayback();
        }

        // When there is any data source request is currently executing then cancel it.
        if (mGetVideoDataSourceUtils != null && !mGetVideoDataSourceUtils.isCancelled()) {
            mGetVideoDataSourceUtils.cancel(true);
        }

    }

    /**
     * Method to close the video player and hide the respective layouts.
     */
    private void closeVideoPlayer() {
        // If close video player options is clicked then hiding the video player layouts
        // And showing the cover image layout and stops the Web/VideoView player.
        // Also removed the close icon and making the collapsingToolBar collapsible again.
        rlVideoPlayer.setVisibility(View.GONE);
        ivCoverImage.setVisibility(View.VISIBLE);
        rlTitleBlock.setVisibility(View.VISIBLE);
        stopVideoPlayingOptions();
        isVideoPlaying = false;

        // Making the tool bar collapsible.
        mToolbar.setBackground(toolBarBackgroundDrawable);
        layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);

        // Invalidating option menu to remove the "close" icon.
        invalidateOptionsMenu();
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
        } else if (id == R.id.close_player) {
            closeVideoPlayer();

        } else if (mGutterMenus != null) {
            mGutterMenuUtils.onMenuOptionItemSelected(findViewById(R.id.main_content),
                    findViewById(item.getItemId()), id, mGutterMenus);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        if (mGutterMenus != null) {
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE,
                    mBrowseList);
        }

        // If video is playing then showing the video player layout and hide the cover image layout.
        // And add the close icon to stop the video player.
        if (isVideoPlaying) {
            rlVideoPlayer.setVisibility(View.VISIBLE);
            rlTitleBlock.setVisibility(View.GONE);
            ivCoverImage.setVisibility(View.GONE);
            menu.add(Menu.NONE, R.id.close_player, Menu.NONE,
                    mContext.getResources().getString(R.string.close_group_label)).
                    setIcon(R.drawable.ic_close_white_24dp).
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mRootView, successMessage,
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

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        CustomViews.showMarqueeTitle(verticalOffset, collapsingToolbar, mToolbar, tvToolBarTitle, mTitle);
    }

    @Override
    public void onClick(View view) {
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
    }


    /**
     * Method to set rule on video view.
     * @param value Setting align true if its 1.
     */
    public void setVideoLayoutParams(int value) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoViewPlayer.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, value);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, value);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, value);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, value);
        videoViewPlayer.setLayoutParams(layoutParams);
    }
}
