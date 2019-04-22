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

package com.fellopages.mobileapp.classes.modules.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.modules.music.ui.TrackView;

import java.util.ArrayList;


/**
 * Simple adapter used to display artist tracks in a list with an optional header.
 */
public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.Holder> {

    /**
     * View types.
     */
    private static final int VIEW_TYPE_TRACK = 1;
    private static final int VIEW_TYPE_HEADER = 2;

    /**
     * Current played track playlist position used to display an indicator.
     */
    private int mPlayedTrackPosition;

    /**
     * Adapted tracks.
     */
    private ArrayList<PlaylistDetail> mTracks;

    /**
     * view header
     */
    private View mHeaderView;

    /**
     * listener used to catch event on the raw view.
     */
    private TrackView.Listener mListener;

    /**
     * Listener used to catch event performed on the list.
     */
    private Listener mAdapterListener;

    /**
     * Simple adapter used to display tracks in a list.
     *
     * @param listener listener used to catch event on the raw view.
     * @param tracks   tracks.
     */
    public TracksAdapter(TrackView.Listener listener, ArrayList<PlaylistDetail> tracks) {
        super();
        mTracks = tracks;
        mPlayedTrackPosition = -1;
        mListener = listener;
    }



    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Holder holder;
        switch (i) {
            case VIEW_TYPE_TRACK:
                TrackView v = new TrackView(viewGroup.getContext());
                v.setListener(mListener);
                v.setLayoutParams(
                        new RecyclerView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                );
                holder = new TrackHolder(v);
                break;
            case VIEW_TYPE_HEADER:
                holder = new HeaderHolder(mHeaderView);
                break;
            default:
                throw new IllegalStateException("View type not handled : " + i);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int i) {
        switch (holder.viewType) {
            case VIEW_TYPE_TRACK:
                int offset = mHeaderView != null ? 1 : 0;
                ((TrackHolder) holder).trackView.setModel(mTracks.get(i - offset),(i - offset));
                if (i == mPlayedTrackPosition) {
                    ((TrackHolder) holder).trackView
                            .setBackgroundResource(R.drawable.selectable_background_colored_light);
                    ((TrackHolder) holder).trackView.setSelected(true);
                } else {
                    ((TrackHolder) holder).trackView
                            .setBackgroundResource(R.drawable.selectable_background_white);
                    ((TrackHolder) holder).trackView.setSelected(false);
                }
                break;
            case VIEW_TYPE_HEADER:

                // do nothing
                break;
            default:
                throw new IllegalStateException("Unhandled view type : " + holder.viewType);
        }
    }

    @Override
    public int getItemCount() {
        int header = mHeaderView == null ? 0 : 1;
        return header + mTracks.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_TRACK;
        }
    }




    /**
     * Set the header view.
     *
     * @param v header view.
     */
    public void setHeaderView(View v) {
        mHeaderView = v;
    }

    /**
     * Set a listener used to catch events performed on the list.
     *
     * @param listener listener which will be notified of events performed on the list.
     */
    public void setAdapterListener(Listener listener) {
        mAdapterListener = listener;
    }

    /**
     * View holder pattern.
     */
    public static abstract class Holder extends RecyclerView.ViewHolder {
        private int viewType;

        public Holder(View v, int viewType) {
            super(v);
            this.viewType = viewType;
        }
    }

    /**
     * View holder for a track view.
     */
    public static class TrackHolder extends Holder {
        private TrackView trackView;

        public TrackHolder(TrackView v) {
            super(v, VIEW_TYPE_TRACK);
            this.trackView = v;
        }
    }

    /**
     * View holder for the view header.
     */
    public static class HeaderHolder extends Holder {

        public HeaderHolder(View v) {
            super(v, VIEW_TYPE_HEADER);
        }
    }

    /**
     * Interface used to catch event performed on the list.
     */
    public interface Listener {
        /**
         * Called when the user performed a swipe to dismiss gesture on the list.
         *
         * @param i adapter position of the item which should be removed
         */
        void onTrackDismissed(int i);
    }
}
