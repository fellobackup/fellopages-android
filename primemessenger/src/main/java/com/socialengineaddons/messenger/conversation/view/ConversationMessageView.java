package com.socialengineaddons.messenger.conversation.view;


import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.Utils;
import com.socialengineaddons.messenger.conversation.ConversationActivity;
import com.socialengineaddons.messenger.conversation.adapter.ConversationMessageAdapter;
import com.socialengineaddons.messenger.conversation.data_model.Message;
import com.socialengineaddons.messenger.interfaces.OnRetryClicked;
import com.socialengineaddons.messenger.interfaces.OnSongPlay;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class ConversationMessageView extends LinearLayout {

    private TextView dateTextView, userNameTextView;
    private TextView messageTextView;
    private TextView timestampTextView;
    private ImageView imageView, videoThumbnail;
    private RelativeLayout mVideoLayout, mImageLayout;
    private RelativeLayout audioLayout, conversationMessageLayout;
    private TextView playButton, musicIcon, textDuration, textBufferDuration;
    private SeekBar mSeekBar;
    private ImageView statusImageView;
    private ProgressBar progressBar;
    private TextView retryTextView;

    private int layoutResId;
    private Context mContext;
    private long mediaFileLengthInMilliseconds;

    private final Handler handler = new Handler();
    private MediaPlayer mPlayer;

    public ConversationMessageView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.layout
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            layoutResId = array.getResourceId(0, R.layout.merge_conversation_message_item_destination);
            array.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), layoutResId, this);
        this.conversationMessageLayout = (RelativeLayout) this.findViewById(R.id.conversationMessageLayout);
        this.dateTextView = (TextView) this.findViewById(R.id.dateTextView);
        this.messageTextView = (TextView) this.findViewById(R.id.message);
        this.timestampTextView = (TextView) this.findViewById(R.id.time);
        this.userNameTextView = (TextView) this.findViewById(R.id.user_name);

        this.imageView = (ImageView) this.findViewById(R.id.image);
        this.mImageLayout = (RelativeLayout) this.findViewById(R.id.imageLayout);
        this.videoThumbnail = (ImageView) this.findViewById(R.id.videoThumbnail);
        this.mVideoLayout = (RelativeLayout) this.findViewById(R.id.videoLayout);

        this.audioLayout = (RelativeLayout) this.findViewById(R.id.audioLayout);
        this.playButton = (TextView) this.findViewById(R.id.playButtonIcon);
        this.musicIcon = (TextView) this.findViewById(R.id.musicIcon);
        this.textBufferDuration = (TextView) this.findViewById(R.id.textBufferDuration);
        this.textDuration = (TextView) this.findViewById(R.id.textDuration);
        this.mSeekBar = (SeekBar) this.findViewById(R.id.playback_view_seekbar);
        this.progressBar = (ProgressBar) this.findViewById(R.id.progress);
        this.retryTextView = (TextView) findViewById(R.id.retryText);

        if(playButton != null){
            playButton.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
                    "fontIcons/fontawesome-webfont.ttf"));
            playButton.setText("\uf04b");
        }

        if(musicIcon != null){
            musicIcon.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
                    "fontIcons/fontawesome-webfont.ttf"));
            musicIcon.setText("\uf001");
        }

        statusImageView = (ImageView) findViewById(R.id.status);

    }

    /**
     * Function to set the data in view
     * @param position
     * @param message
     * @param onSongPlay
     * @param onRetryClicked
     * @param selfUid
     */
    public void display(final int position, final Message message, final OnSongPlay onSongPlay,
                        final OnRetryClicked onRetryClicked, String selfUid) {

        if(ConversationMessageAdapter.selectedMessages.contains(message)){
            conversationMessageLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryLight));
        } else{
            conversationMessageLayout.setBackgroundColor(0);
        }

        final long timestamp = message.getCreatedAt();
        if (dateTextView != null) {
            dateTextView.setText(Utils.getDateOfMessage(mContext, timestamp));
        }

        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

        switch (message.getAttachmentType()){


            case "text":

                mImageLayout.setVisibility(GONE);
                audioLayout.setVisibility(GONE);
                mVideoLayout.setVisibility(GONE);
                messageTextView.setVisibility(VISIBLE);
//                messageTextView.setTextIsSelectable(true);
                String messageBody = message.getBody().replace("\n", "<br>");
                messageTextView.setText(Html.fromHtml(Html.fromHtml(Html.fromHtml(messageBody).toString()).toString()));

                break;

            case "image":

                mImageLayout.setVisibility(VISIBLE);
                audioLayout.setVisibility(GONE);
                mVideoLayout.setVisibility(GONE);
                messageTextView.setVisibility(GONE);
                if(message.getTemporaryPath() != null && !message.getTemporaryPath().isEmpty()){
                    if(retryTextView != null){
                        retryTextView.setVisibility(VISIBLE);
                        retryTextView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                progressBar.setVisibility(VISIBLE);
                                retryTextView.setVisibility(GONE);
                                onRetryClicked.onRetryViewClicked(message);
                            }
                        });
                    }
                } else if(message.getFileUrl() != null && !message.getFileUrl().isEmpty()){
                    progressBar.setVisibility(VISIBLE);
                    int width = mContext.getResources().getDimensionPixelOffset(R.dimen.conversation_message_image_width);
                    int height = mContext.getResources().getDimensionPixelOffset(R.dimen.conversation_message_image_height);
                    Picasso.with(mContext)
                            .load(message.getFileUrl())
                            .placeholder(R.color.grey)
                            .resize(width, height)
                            .centerCrop()
                            .into(imageView, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(GONE);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                            });
                    // Open PhotoPreviewActivity to View the full Photo.
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!ConversationActivity.isMultiSelect) {
                                Intent intent = new Intent(mContext, PhotoPreviewActivity.class);
                                intent.putExtra("imageUrl", message.getFileUrl());
                                mContext.startActivity(intent);
                            }
                        }
                    });
                    if(retryTextView != null){
                        retryTextView.setVisibility(GONE);
                    }
                } else {
                    imageView.setImageResource(R.color.grey);
                    imageView.setBackgroundResource(R.color.grey);
                    progressBar.setVisibility(VISIBLE);
                }

                break;

            case "video":

                mImageLayout.setVisibility(GONE);
                audioLayout.setVisibility(GONE);
                messageTextView.setVisibility(GONE);

                if(message.getFileUrl() != null){

                    mVideoLayout.setVisibility(VISIBLE);
                    if(message.getVideoThumbnail() != null && !message.getVideoThumbnail().isEmpty()){
                        int width = mContext.getResources().getDimensionPixelOffset(R.dimen.conversation_message_image_width);
                        int height = mContext.getResources().getDimensionPixelOffset(R.dimen.conversation_message_image_height);
                        Picasso.with(mContext)
                                .load(message.getVideoThumbnail())
                                .placeholder(R.color.grey)
                                .resize(width, height)
                                .centerCrop()
                                .into(videoThumbnail);
                    } else {
                        videoThumbnail.setImageResource(R.color.grey);
                    }

                    videoThumbnail.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!ConversationActivity.isMultiSelect) {
                                Intent intent = new Intent(mContext, PhotoPreviewActivity.class);
                                intent.putExtra("imageUrl", message.getVideoThumbnail());
                                intent.putExtra("videoUrl", message.getFileUrl());
                                mContext.startActivity(intent);
                            }
                        }
                    });
                } else {
                    mVideoLayout.setVisibility(GONE);
                }
                break;

            case "audio":
                mImageLayout.setVisibility(GONE);
                mVideoLayout.setVisibility(GONE);
                messageTextView.setVisibility(GONE);

                if (message.getFileUrl() != null) {
                    audioLayout.setVisibility(VISIBLE);

                    if (message.getSeekBarProgress() != 0) {
                        this.mSeekBar.setProgress(message.getSeekBarProgress());
                        mSeekBar.setEnabled(true);
                    }

                    try {
                        mPlayer = new MediaPlayer();
                        ConversationMessageAdapter.medialPlayers.add(mPlayer);
                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mPlayer.setDataSource(message.getFileUrl());
                        mPlayer.prepareAsync();
                    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }

                    mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                        }
                    });

                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            playButton.setText("\uF04B");

                        }
                    });



                    playButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onSongPlay != null) {
                                if (!mPlayer.isPlaying()) {

                                    playButton.setText("\uf04c");
                                    mPlayer.start();
                                    mediaFileLengthInMilliseconds = (long) (message.getAudioDuration() != 0 ?
                                            message.getAudioDuration() * 1000 : 0);
                                    primarySeekBarProgressUpdater();
                                } else {

                                    mPlayer.pause();
                                    playButton.setText("\uF04B");
                                }
                            }
                        }
                    });
                } else {
                    audioLayout.setVisibility(GONE);
                }
                break;

        }

        if(userNameTextView != null){
            userNameTextView.setText(message.getUserName() != null && !message.getUserName().isEmpty()
                    ? message.getUserName() : mContext.getResources().getString(R.string.deleted_user));
        }

        timestampTextView.setText(Utils.getTimestamp(timestamp, mContext));

        /**
         * Set status of the message
         */
        if(statusImageView != null){

            switch (message.getStatus()){

                case 1:
                    statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_sent_18dp));
                    statusImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.secondary_text_color));
                    break;

                case 2:
                    statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_delivered_15dp));
                    statusImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.secondary_text_color));
                    break;

                case 3:
                    statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_delivered_15dp));
                    statusImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    break;
            }
        }
    }

    /**
     * Function to update the seek bar progress when a audio file is played
     */
    private void primarySeekBarProgressUpdater() {
        if(mSeekBar != null){
            textBufferDuration.setVisibility(VISIBLE);
            textBufferDuration.setText(Utils.getDuration(mPlayer.getCurrentPosition()));

            textDuration.setVisibility(VISIBLE);
            textDuration.setText(Utils.getDuration(mediaFileLengthInMilliseconds));
            mSeekBar.setEnabled(true);
            mSeekBar.setProgress((int)(((float)mPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100)); // This math construction give a percentage of "was playing"/"song length"
            if (mPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                ConversationMessageAdapter.medialPlayerHandlers.put(handler, notification);
                handler.postDelayed(notification, 1000);
            }
        }
    }

}
