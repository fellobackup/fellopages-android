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

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnImageLoadingListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnPopUpDismissListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnVideoSourceLoadCompleteListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnViewTouchListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GetVideoDataSourceUtils;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.messages.CreateNewMessage;
import com.fellopages.mobileapp.classes.modules.story.photofilter.PhotoEditActivity;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoryView extends AppCompatActivity implements StoriesProgressView.StoriesListener,
        View.OnClickListener, OnPopUpDismissListener, OnMenuClickResponseListener {

    // Member variables.
    private Context mContext;
    private View mRootView;
    private StoriesProgressView storiesProgressView;
    private TextView tvUserName, tvStoryTime, tvViewCount, tvWriteMessage, tvAddToStory, tvStoryCaption;
    private ImageView ivMain, ivUserProfile, ivOptionMenu, ivCloseIcon;
    private VideoView videoPlayer;
    private ProgressBar pbLoading;
    private int storyId, mUserId, counter = 0, videoPausePosition = 0;
    private boolean isStoryDeleted = false, mIsMyStory = false, mIsVideoLoading = false, mIsPhotoLoading = false,
            mIsActivityRunning = true, isPopUpShowing = false, canSendMessage = false;
    private String mStoryResponse, mUserName;
    private JSONArray mStoryArray, mGutterMenuArray;
    private AppConstant mAppConst;
    private GetVideoDataSourceUtils mGetVideoDataSourceUtils;
    private GutterMenuUtils mGutterMenuUtils;
    private AlertDialogWithAction mAlertDialogWithAction;
    private Intent storyIntent;
    private ImageLoader mImageLoader;
    private boolean isTextExpanded = false;
    private String fullCaption, shortCaption;
    private RelativeLayout mBottomView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story_view);

        // Getting references of member variables.
        mContext = StoryView.this;
        mAppConst = new AppConstant(mContext);
        mGutterMenuUtils = new GutterMenuUtils(mContext);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(mContext);
        mGutterMenuUtils.setOnMenuClickResponseListener(this);
        mGutterMenuUtils.setOnPopUpDismissListener(this);
        mStoryResponse = getIntent().getStringExtra("story_response");
        storyId = getIntent().getIntExtra(ConstantVariables.STORY_ID, 0);
        mIsMyStory = getIntent().getBooleanExtra("is_my_story", false);

        //getting views.
        getViews();

        // Set details.
        storiesProgressView.setStoriesListener(this);
        makeRequest();
    }

    /**
     * Method to get all views.
     */
    private void getViews() {
        mRootView = findViewById(R.id.main_view);
        storiesProgressView = findViewById(R.id.stories);
        tvUserName = findViewById(R.id.user_name);
        tvStoryTime = findViewById(R.id.story_time);
        mBottomView = findViewById(R.id.bottom_view);
        tvViewCount = findViewById(R.id.view_count);
        tvStoryCaption = findViewById(R.id.story_caption);
        tvWriteMessage = findViewById(R.id.write_message);
        tvAddToStory = findViewById(R.id.add_to_story);
        ivMain = findViewById(R.id.image);
        ivUserProfile = findViewById(R.id.user_image);
        ivOptionMenu = findViewById(R.id.optionMenu);
        videoPlayer = findViewById(R.id.video_player);
        pbLoading = findViewById(R.id.loadingProgress);
        ivOptionMenu.setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_IN);
        ivCloseIcon = findViewById(R.id.closeIcon);
        ivCloseIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_IN);
    }

    private void makeRequest() {
        pbLoading.setVisibility(View.VISIBLE);
        String url;
        url = AppConstant.DEFAULT_URL + "advancedactivity/story/view/" + storyId;

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                try {
                    pbLoading.setVisibility(View.GONE);
                    if (jsonObject.optJSONArray("response") != null) {
                        mStoryResponse = jsonObject.optJSONArray("response").toString();
                        setDetails();
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                pbLoading.setVisibility(View.GONE);
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
     * Method to set details for each image view.
     */
    private void setDetails() {
        try {
            mStoryArray = new JSONArray(mStoryResponse);
            if (mStoryArray.length() > 0) {
                storiesProgressView.setStoriesCount(mStoryArray.length());
                showStory(counter);
                JSONObject storyObject = mStoryArray.optJSONObject(counter);
                if (storyObject.optString("videoUrl") == null || storyObject.optString("videoUrl").isEmpty()) {
                    storiesProgressView.startStories();
                } else {
                    storiesProgressView.startStoriesWithPause(counter);
                }

                mRootView.setOnTouchListener(new OnViewTouchListener(mContext) {

                    @Override
                    public void onLongPressed() {

                    }

                    @Override
                    public void onLongPressedCustom() {
                        if (!mIsVideoLoading && !mIsPhotoLoading) {
                            playPauseVideo(counter, false);
                        }
                        checkForVisibility(false);
                    }

                    @Override
                    public void onTouchRelease() {

                        // If video/image is currently loading then not doing any action
                        // otherwise performing action accordingly.
                        if (!mIsVideoLoading && !mIsPhotoLoading) {
                            playPauseVideo(counter, true);
                        }

                        if (isTextExpanded){
                            isTextExpanded = false;
                            tvStoryCaption.setText(Html.fromHtml(shortCaption));
                        } else {
                            checkForVisibility(true);
                        }

                    }

                    @Override
                    public void onLeftClick() {
                        if (isTextExpanded){
                            isTextExpanded = false;
                            tvStoryCaption.setText(Html.fromHtml(shortCaption));
                            if (!mIsVideoLoading && !mIsPhotoLoading) {
                                playPauseVideo(counter, true);
                            }
                        } else {
                            storiesProgressView.reverse();
                            pauseAnimationForVideo(counter);
                        }
                    }

                    @Override
                    public void onRightClick() {
                        if (isTextExpanded){
                            isTextExpanded = false;
                            tvStoryCaption.setText(Html.fromHtml(shortCaption));
                            if (!mIsVideoLoading && !mIsPhotoLoading) {
                                playPauseVideo(counter, true);
                            }
                        } else {
                            storiesProgressView.skip();
                            pauseAnimationForVideo(counter);
                        }
                    }

                    @Override
                    public void onTopToBottomSwipe() {
                        finish();
                    }
                });

                ivUserProfile.setOnClickListener(this);
                ivCloseIcon.setOnClickListener(this);
                tvUserName.setOnClickListener(this);
                ivOptionMenu.setOnClickListener(this);
                tvViewCount.setOnClickListener(this);
                tvWriteMessage.setOnClickListener(this);
                tvAddToStory.setOnClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to show story media for each position.
     *
     * @param position Position of story.
     */
    private void showStory(final int position) {

        JSONObject storyObject = mStoryArray.optJSONObject(position);
        String image = storyObject.optString("image");
        String userImage = storyObject.optString("owner_image_profile");
        mUserId = storyObject.optInt("owner_id");
        storyId = storyObject.optInt("story_id");
        isStoryDeleted = storyObject.optBoolean("isDeleted");
        mUserName = storyObject.optString("owner_title");
        canSendMessage = storyObject.optInt("isSendMessage") == 1;
        String storyCaption = storyObject.optString("description");

        // Showing story owner name and story creation time.
        tvUserName.setText(mUserName);
        tvStoryTime.setText(AppConstant.convertDateFormat(mContext.getResources(), storyObject.optString("create_date")));

        // Display story owner image.
        mImageLoader.setImageForUserProfile(userImage, ivUserProfile);

        // Setting up the story progress view time according to media type.
        storiesProgressView.setStoryDuration(storyObject.optInt("duration") == 0 ? 3400L : storyObject.optInt("duration") * 1000L);

        // Checking if the story is video or photo.
        if (isVideoStory(position)) {
            // Making videoLoading true.
            mIsVideoLoading = true;
            mIsPhotoLoading = false;

            // If any previous Async task is executing then finishing it.
            if (mGetVideoDataSourceUtils != null) {
                mGetVideoDataSourceUtils.cancel(true);
            }
            pbLoading.setVisibility(View.VISIBLE);
            pbLoading.bringToFront();

            videoPlayer.stopPlayback();
            videoPlayer.setVisibility(View.GONE);
            ivMain.setVisibility(View.VISIBLE);
            if (storiesProgressView.getAnimatorSize() != 0) {
                storiesProgressView.stopAnimation(counter);
            }

            // Getting path from videoUrl.
            mGetVideoDataSourceUtils = new GetVideoDataSourceUtils(storyObject.optString("videoUrl"), new OnVideoSourceLoadCompleteListener() {
                @Override
                public void onSuccess(String videoPath) {
                    if (videoPath != null && !videoPath.isEmpty()) {
                        mIsVideoLoading = false;
                        ivMain.setVisibility(View.GONE);
                        pbLoading.setVisibility(View.GONE);
                        videoPlayer.setVisibility(View.VISIBLE);
                        videoPlayer.setVideoPath(videoPath);
                        videoPlayer.requestFocus();

                        // If activity is not currently active then not playing the video as well as animation.
                        if (mIsActivityRunning && !isPopUpShowing) {
                            videoPlayer.start();
                            storiesProgressView.playAnimation(position);
                        }
                    }
                }
            });
            mGetVideoDataSourceUtils.execute();

        } else {
            // If the Story is photo type then making videoLoading false.
            mIsVideoLoading = false;
            mIsPhotoLoading = true;
            // If any previous Async task is executing then finishing it.
            if (mGetVideoDataSourceUtils != null) {
                mGetVideoDataSourceUtils.cancel(true);
            }
            // If any video is playing then stopping it.
            videoPlayer.stopPlayback();
            videoPlayer.setVisibility(View.GONE);
            ivMain.setVisibility(View.VISIBLE);
            pbLoading.bringToFront();
            pbLoading.setVisibility(View.VISIBLE);
            if (storiesProgressView.getAnimatorSize() != 0) {
                storiesProgressView.stopAnimation(counter);
            }
        }

        tvWriteMessage.setTag(String.valueOf(storyId));

        // Showing the story view count.
        tvViewCount.setText(String.valueOf(storyObject.optInt("view_count")));
        mGutterMenuArray = storyObject.optJSONArray("gutterMenu");

        setCaptionInView(storyCaption);

        checkForVisibility(true);

        // Making server call to mark the story as viewed.
        if (storyObject.optInt("isViewed") == 0) {
            mAppConst.postJsonRequest(AppConstant.DEFAULT_URL + "advancedactivity/story/viewer-count/" + storyId);
        }

        // Showing story main image into Main ImageView.
        mImageLoader.setImageWithCallbackListener(image, ivMain, new OnImageLoadingListener() {
            @Override
            public void onLoadFailed() {
                mIsPhotoLoading = false;
                if (!isVideoStory(counter) && mIsActivityRunning && !isPopUpShowing) {
                    pbLoading.setVisibility(View.GONE);
                    storiesProgressView.playAnimation(counter);
                }

            }

            @Override
            public void onResourceReady() {
                mIsPhotoLoading = false;
                if (!isVideoStory(counter) && mIsActivityRunning && !isPopUpShowing) {
                    pbLoading.setVisibility(View.GONE);
                    storiesProgressView.playAnimation(counter);
                }
            }
        });
    }

    private void setCaptionInView(String description) {
        if (description != null && !description.isEmpty()) {
            fullCaption = description;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (description.length() < 100) {
                    tvStoryCaption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tvStoryCaption.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                }
            }

            if (description.length() > ConstantVariables.STORY_WORD_LIMIT) {
                shortCaption = description.substring(0, ConstantVariables.STORY_WORD_LIMIT) + "... <b>See more</b>";
                tvStoryCaption.setText(Html.fromHtml(shortCaption));
                tvStoryCaption.setClickable(true);
                tvStoryCaption.setOnClickListener(this);
            } else {
                tvStoryCaption.setClickable(false);
                tvStoryCaption.setText(fullCaption);
            }
            tvStoryCaption.setVisibility(View.VISIBLE);

        } else {
            tvStoryCaption.setVisibility(View.GONE);
            tvStoryCaption.setText("");
        }
    }

    /**
     * Method to check view's visibility when long pressed and released.
     *
     * @param isNeedToShow If true then showing view accordingly.
     */
    private void checkForVisibility(boolean isNeedToShow) {
        if (isNeedToShow) {
            findViewById(R.id.user_info_layout).setVisibility(View.VISIBLE);
            ivUserProfile.setVisibility(View.VISIBLE);
            ivOptionMenu.setVisibility(View.VISIBLE);
            ivCloseIcon.setVisibility(View.VISIBLE);
            if (mGutterMenuArray != null && mGutterMenuArray.length() > 0 && !isStoryDeleted) {
                ivOptionMenu.setVisibility(View.VISIBLE);
            } else {
                ivOptionMenu.setVisibility(View.GONE);
            }
            tvAddToStory.setVisibility(mIsMyStory ? View.VISIBLE : View.GONE);
            tvViewCount.setVisibility(!isStoryDeleted && mIsMyStory ? View.VISIBLE : View.GONE);
            tvWriteMessage.setVisibility(!isStoryDeleted && !mIsMyStory && canSendMessage ? View.VISIBLE : View.GONE);
            mBottomView.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.user_info_layout).setVisibility(View.GONE);
            ivUserProfile.setVisibility(View.GONE);
            ivCloseIcon.setVisibility(View.GONE);
            ivOptionMenu.setVisibility(View.GONE);
            tvViewCount.setVisibility(View.GONE);
            tvWriteMessage.setVisibility(View.GONE);
            tvAddToStory.setVisibility(View.GONE);
            mBottomView.setVisibility(View.GONE);
        }
    }

    /**
     * Method to check if the current visible story is video type and start/pause videoplayer accordingly.
     *
     * @param position      Position of currently visible story.
     * @param isPlayRequest True if need to play video.
     */
    public void checkForVideoOption(int position, boolean isPlayRequest) {
        if (isVideoStory(position)) {
            if (isPlayRequest) {
                videoPlayer.seekTo(videoPausePosition + 1000);
                videoPlayer.start();
            } else {
                videoPlayer.pause();
                videoPausePosition = videoPlayer.getCurrentPosition();
            }
        }

        if (isPlayRequest) {
            storiesProgressView.playAnimation(counter);
        } else {
            storiesProgressView.stopAnimation(counter);
        }
    }

    /**
     * Method to check if the current visible story is video type and start/pause videoplayer accordingly.
     *
     * @param position      Position of currently visible story.
     * @param isPlayRequest True if need to play video.
     */
    public void playPauseVideo(int position, boolean isPlayRequest) {
        if (isVideoStory(position)) {
            if (isPlayRequest) {
                videoPlayer.start();
            } else {
                videoPlayer.pause();
            }
        }
        if (isPlayRequest) {
            storiesProgressView.playAnimation(counter);
        } else {
            storiesProgressView.stopAnimation(counter);
        }
    }

    /**
     * Method to pause video player when new story changed from 1 video/photo type to another.
     *
     * @param position Position of currently visible story.
     */
    public void pauseAnimationForVideo(int position) {
        if (isVideoStory(position)) {
            if (position == 0 && !mIsVideoLoading) {
                videoPlayer.seekTo(0);
                videoPlayer.start();
            } else {
                videoPlayer.pause();
                storiesProgressView.stopAnimation(counter);
            }
        } else if (mIsPhotoLoading) {
            storiesProgressView.stopAnimation(counter);
        }
    }

    /**
     * Method to get currently visible story is belongs to video or image.
     *
     * @param position Position of currently visible story.
     * @return Returns true if the current story is video type.
     */
    public boolean isVideoStory(int position) {
        JSONObject storyObject = mStoryArray.optJSONObject(position);
        return (storyObject.optString("videoUrl") != null && !storyObject.optString("videoUrl").isEmpty());
    }

    /**
     * Method to get video url from story.
     *
     * @param position Position of current story.
     * @return Returns the story video url.
     */
    public String getVideoUrl(int position) {
        JSONObject storyObject = mStoryArray.optJSONObject(position);
        return storyObject.optString("videoUrl");
    }

    /**
     * Method to get Image url from story.
     *
     * @param position Position of current story.
     * @return Returns the story Image url.
     */
    public String getImageUrl(int position) {
        JSONObject storyObject = mStoryArray.optJSONObject(position);
        return storyObject.optString("image");
    }

    /**
     * Method to start media picker activity for story uploading.
     */
    public void startStoryMediaPickerActivity() {
        Intent intent = new Intent(mContext, PhotoEditActivity.class);
        startActivityForResult(intent, ConstantVariables.REQUEST_STORY_POST);
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onNext() {
        showStory(++counter);
    }

    @Override
    public void onNextStarted() {
        pauseAnimationForVideo(counter);
    }

    @Override
    public void onPrev() {
        if (counter - 1 < 0) return;
        showStory(--counter);
    }

    @Override
    public void onComplete() {
        StoryUtils.sCurrentStory = StoryUtils.sCurrentStory + 1;
        if (StoryUtils.sCurrentStory < StoryUtils.STORY.size()
                && StoryUtils.isMuteStory.get(StoryUtils.sCurrentStory) != 1 && StoryUtils.STORY.get(StoryUtils.sCurrentStory) != 0) {
            Intent intent = getIntent();
            intent.putExtra("is_my_story", false);
            intent.putExtra(ConstantVariables.STORY_ID, StoryUtils.STORY.get(StoryUtils.sCurrentStory));
            finish();
            startActivity(intent);
        } else {
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (storiesProgressView != null && mStoryResponse != null) {
            mIsActivityRunning = false;
            checkForVideoOption(counter, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsActivityRunning = true;
        if (storiesProgressView != null && mStoryResponse != null && !mIsVideoLoading && !isPopUpShowing
                && !mIsPhotoLoading) {
            checkForVideoOption(counter, true);
        }
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (storyIntent != null) {
            setResult(ConstantVariables.REQUEST_STORY_POST, storyIntent);
        }  else {
            Intent data = new Intent();
            setResult(ConstantVariables.STORY_VIEW_PAGE_CODE, data);
        }
        super.finish();
    }

    @Override
    public void onClick(View view) {

        Intent intent;
        switch (view.getId()) {

            case R.id.user_name:
            case R.id.user_image:
                intent = new Intent(mContext, userProfile.class);
                intent.putExtra(ConstantVariables.USER_ID, mUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.optionMenu:
                mGutterMenuUtils.showPopup(view, counter, mGutterMenuArray,
                        new BrowseListItems(), ConstantVariables.SITE_PAGE_MENU_TITLE);
                isPopUpShowing = true;
                if (!mIsVideoLoading && !mIsPhotoLoading) {
                    playPauseVideo(counter, false);
                }
                break;

            case R.id.view_count:
                intent = new Intent(mContext, FragmentLoadActivity.class);
                intent.putExtra("story_id", storyId);
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mContext.getResources().getString(R.string.story_viewers));
                intent.putExtra(ConstantVariables.FRAGMENT_NAME, "story_viewer");
                intent.putExtra("isStoryViewer", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.write_message:
                intent = new Intent(mContext, CreateNewMessage.class);
                intent.putExtra("isStoryReply", true);
                intent.putExtra(ConstantVariables.USER_ID, mUserId);
                intent.putExtra(ConstantVariables.STORY_ID, (String) view.getTag());
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mUserName);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.add_to_story:
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                } else {
                    startStoryMediaPickerActivity();
                }
                break;

            case R.id.closeIcon:
                onBackPressed();
                break;

            case R.id.story_caption:
                if (!isTextExpanded) {
                    tvStoryCaption.setText(fullCaption);
                    if (!mIsVideoLoading && !mIsPhotoLoading) {
                        playPauseVideo(counter, false);
                    }
                } else {
                    tvStoryCaption.setText(Html.fromHtml(shortCaption));
                    if (!mIsVideoLoading && !mIsPhotoLoading) {
                        playPauseVideo(counter, true);
                    }
                }
                isTextExpanded = !isTextExpanded;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (data != null && requestCode == ConstantVariables.REQUEST_STORY_IMAGE_VIDEO) {
                    redirectToImageFilter(data);
                }
                break;

            case ConstantVariables.REQUEST_STORY_POST:
                if (data != null) {
                    storyIntent = data;
                    setResult(ConstantVariables.REQUEST_STORY_POST, storyIntent);
                    finish();
                }
                break;

            case ConstantVariables.PAGE_EDIT_CODE:
                if (requestCode == ConstantVariables.PAGE_EDIT_CODE && data != null) {
                    String jsonObject = data.getStringExtra("response");
                    try {
                        JSONObject responseObject = new JSONObject(jsonObject);
                        String description = responseObject.optJSONObject("body").optString("description");
                        mStoryArray.optJSONObject(counter).put("description", description);
                        setCaptionInView(description);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void redirectToImageFilter(Intent data) {
        ArrayList<String> selectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
        String selectedVideoPath = data.getStringExtra(MultiMediaSelectorActivity.VIDEO_RESULT);

        Intent intent = new Intent(mContext, PhotoEditActivity.class);
        intent.putStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT, selectPath);
        intent.putExtra(MultiMediaSelectorActivity.VIDEO_RESULT, selectedVideoPath);
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, ConstantVariables.STORY_POST_COUNT_LIMIT);
        startActivityForResult(intent, ConstantVariables.REQUEST_STORY_POST);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted, proceed to the normal flow
                    startStoryMediaPickerActivity();
                } else {
                    // If user press deny in the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, findViewById(R.id.rootView),
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;
        }
    }

    @Override
    public void onPopUpDismiss(boolean isDismissed) {
        isPopUpShowing = false;
        if (!mIsVideoLoading && !mIsPhotoLoading && !isTextExpanded) {
            playPauseVideo(counter, true);
        }
    }

    @Override
    public void onItemDelete(int position) {
        JSONObject storyObject = mStoryArray.optJSONObject(position);
        try {
            storyObject.put("isDeleted", true);
            mStoryArray.put(position, storyObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isPopUpShowing = false;
        ivOptionMenu.setVisibility(View.GONE);
        tvViewCount.setVisibility(View.GONE);
        if (!mIsVideoLoading && !mIsPhotoLoading) {
            checkForVideoOption(counter, true);
        }
        storiesProgressView.skip();
        pauseAnimationForVideo(counter);
    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {

        isPopUpShowing = false;
        switch (menuName) {
            case "save":
                downloadMedia(getImageUrl(position), "Image");
                break;
            case "save_video":
                downloadMedia(getVideoUrl(position), "Video");
                break;
            case "mute":
                JSONObject storyObject = mStoryArray.optJSONObject(position);
                try {
                    storyObject.put("isDeleted", true);
                    mStoryArray.put(position, storyObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isPopUpShowing = false;
                ivOptionMenu.setVisibility(View.GONE);
                tvViewCount.setVisibility(View.GONE);
                if (!mIsVideoLoading && !mIsPhotoLoading) {
                    checkForVideoOption(counter, true);
                }
                storiesProgressView.skip();
                pauseAnimationForVideo(counter);
                break;
        }
    }

    /**
     * Method to download media.
     *
     * @param mediaUrl Media(Image/Video) url.
     * @param fileType Type of media.
     */
    private void downloadMedia(String mediaUrl, String fileType) {

        String message;
        if (mediaUrl != null && !mediaUrl.isEmpty() && !mediaUrl.equals("null")) {

            // Getting the DownloadManager Request.
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mediaUrl));
            request.setTitle(mContext.getResources().getString(R.string.app_name) + "_" + fileType);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    mContext.getResources().getString(R.string.app_name) + "_" + fileType);

            // Get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            if (fileType.equals("Image")) {
                message = mContext.getResources().getString(R.string.photo_saved_success_message);
            } else {
                message = mContext.getResources().getString(R.string.video_saved_success_message);
            }
        } else {
            message = mContext.getResources().getString(R.string.url_not_valid);
        }
        SnackbarUtils.displaySnackbar(mRootView, message);
    }
}
