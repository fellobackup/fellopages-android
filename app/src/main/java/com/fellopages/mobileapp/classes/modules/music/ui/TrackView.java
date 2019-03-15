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

package com.fellopages.mobileapp.classes.modules.music.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.modules.music.adapter.PlaylistDetail;


/**
 * Simple View used to render a track.
 */
public class TrackView extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {

    private TextView mTitle;
    private TextView mPlayCount;

    private PlaylistDetail mModel;
    private Listener mListener;

    private int mTrackColor;
    private int mDurationColor;
    private int mTrackColorSelected;
    private int mDurationColorSelected;
    private int trackPosition;

    /**
     * Simple View used to render a track.
     *
     * @param context calling context.
     */
    public TrackView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    /**
     * Simple View used to render a track.
     *
     * @param context calling context.
     * @param attrs   attr from xml.
     */
    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    /**
     * Simple View used to render a track.
     *
     * @param context      calling context.
     * @param attrs        attr from xml.
     * @param defStyleAttr style from xml.
     */
    public TrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            mPlayCount.setTextColor(mDurationColorSelected);
            mTitle.setTextColor(mTrackColorSelected);
        } else {
            mPlayCount.setTextColor(mDurationColor);
            mTitle.setTextColor(mTrackColor);
        }
    }

    /**
     * Set the track which must be displayed.
     *
     * @param track view model.
     */
    public void setModel(PlaylistDetail track,int position) {
        mModel = track;
        trackPosition = position;
        if (mModel != null) {
            mTitle.setText(track.getTrackTitle());
            mPlayCount.setText(String.format(getResources().getString(R.string.play_count_format),
                    track.getPlayCount(), getResources().getString(R.string.music_play_count)));
        }
    }

    /**
     * Set a listener to catch the view events.
     *
     * @param listener listener to register.
     */
    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.track_view, this);
        mTitle = findViewById(R.id.track_view_title);
        mPlayCount = findViewById(R.id.track_view_play_count);

        setBackgroundResource(R.drawable.selectable_background_white);
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding_8dp);
        setPadding(padding, padding, padding, padding);

        this.setOnClickListener(this);
        this.setOnLongClickListener(this);

        mTrackColor = ContextCompat.getColor(context, R.color.grey_dark);
        mDurationColor =  ContextCompat.getColor(context, R.color.grey_dark);
        mTrackColorSelected =  ContextCompat.getColor(context, R.color.white);
        mDurationColorSelected =  ContextCompat.getColor(context, R.color.white);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onTrackClicked(mModel,trackPosition);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mListener != null) {
            mListener.onTrackLongClick(mTitle, mModel, trackPosition);
        }
        return true;
    }


    /**
     * Interface used to catch view events.
     */
    public interface Listener {

        /**
         * Called when the user clicked on the track view.
         *
         * @param track model of the view.
         */
        void onTrackClicked(PlaylistDetail track,int position);
        void onTrackLongClick(View view, PlaylistDetail track,int position);
    }
}
