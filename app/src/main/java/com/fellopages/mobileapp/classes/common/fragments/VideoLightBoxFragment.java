package com.fellopages.mobileapp.classes.common.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.listeners.OnSlideDownDismissTouchListener;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.wordpress.android.util.ToastUtils;

import java.util.HashMap;
import java.util.Map;

public class VideoLightBoxFragment extends Activity {

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_PLAY_BACK_POSITION = "resumePosition";
    PlayerView playerView;
    SimpleExoPlayer exoPlayer;
    long playbackPosition;
    int currentWindow;
    boolean playWhenReady;
    private View rootView;
    private Context mContext;
    private String mVideoViewUrl;
    private WebView mVideoWebView;
    private ProgressBar pbVideoLoading;
    private int mVideoType, resizeMode = 1;
    private boolean canPlay;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private View mainFrame;
    private int mResumeWindow;
    private long mPlayBackPosition;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_light_box_bottomsheet);
        rootView = findViewById(R.id.bottom_sheet);
        setSwipeListener(rootView, true);
        mContext = this;
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mPlayBackPosition = savedInstanceState.getLong(STATE_PLAY_BACK_POSITION);
        }
        _init();
        try {
            if (mVideoType == 3 && mVideoViewUrl != null) {
                pbVideoLoading.setVisibility(View.VISIBLE);
                initializePlayer(mVideoViewUrl);
            } else {
                renderVideoLightBox(mVideoType, mVideoViewUrl);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoWebView.onResume();
        canPlay = true;
        startPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        doOnPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void setSwipeListener(View view, final boolean canDismiss) {
        view.setOnTouchListener(new OnSlideDownDismissTouchListener(view, null, new OnSlideDownDismissTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(Object token) {
                return canDismiss;
            }

            @Override
            public void onDismiss(View view, Object token) {
                if (canDismiss) {
                    onBackPressed();
                }
            }
        }));
    }

    public void _init() {

        mVideoWebView = rootView.findViewById(R.id.webView);
        pbVideoLoading = rootView.findViewById(R.id.loadingProgress);
        playerView = rootView.findViewById(R.id.exo_video_player);
        mainFrame = rootView.findViewById(R.id.main_media_frame);
        setSwipeListener(playerView, true);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mVideoType = args.getInt(ConstantVariables.VIDEO_TYPE, 0);
            mVideoViewUrl = args.getString(ConstantVariables.VIDEO_URL);
        }

    }

    private void initializePlayer(String videoURL) {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(mContext),
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
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
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
            mResumeWindow = exoPlayer.getCurrentWindowIndex();
            mPlayBackPosition = Math.max(0, exoPlayer.getContentPosition());
        }
    }

    private void startPlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.getPlaybackState();
        }
    }

    public void loadVideoUrl() {

        pbVideoLoading.setVisibility(View.VISIBLE);

        if (mVideoType != 0 && mVideoType != 3) {

            //Auto playing videos in webview
            mVideoWebView.setClickable(true);
            mVideoWebView.setFocusableInTouchMode(true);
            mVideoWebView.getSettings().setJavaScriptEnabled(true);
            mVideoWebView.getSettings().setAppCacheEnabled(true);
            mVideoWebView.getSettings().setDomStorageEnabled(true);

            mVideoWebView.setVisibility(View.GONE);
            mVideoWebView.onResume();
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
                    if (canPlay) {
                        view.loadUrl(javascript);
                    }
                    super.onPageFinished(view, url);
                    pbVideoLoading.setVisibility(View.GONE);
                    mVideoWebView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            finish();
            ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.video_under_process));
        }

    }

    public void renderVideoLightBox(int videoType, String videoViewUrl) {
        mVideoType = videoType;
        mVideoViewUrl = videoViewUrl;
        mainFrame.setVisibility(View.GONE);
        loadVideoUrl();
    }

    private void doOnPause() {
        mVideoWebView.onPause();
        canPlay = false;
        pausePlayer();
    }

    private void _initMediaAction() {

        PlaybackControlView controlView = playerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_fit_x));
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_PLAY_BACK_POSITION, mPlayBackPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ((Activity) mContext).overridePendingTransition(R.anim.slide_up_in, R.anim.push_down_out);
    }
}
