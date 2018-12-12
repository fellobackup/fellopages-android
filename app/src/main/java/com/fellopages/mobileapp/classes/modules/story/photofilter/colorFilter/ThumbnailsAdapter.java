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
 *
 */

package com.fellopages.mobileapp.classes.modules.story.photofilter.colorFilter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CircularImageView;

import java.util.List;


public class ThumbnailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int lastPosition = -1;
    private ThumbnailCallback thumbnailCallback;
    private List<ThumbnailItem> dataSet;
    private Context mContext;

    public ThumbnailsAdapter(List<ThumbnailItem> dataSet, Context context) {
        this.dataSet = dataSet;
        this.mContext = context;
        this.thumbnailCallback = (ThumbnailCallback) mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.list_thumbnail_item, viewGroup, false);
        return new ThumbnailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = dataSet.get(position);
        ThumbnailsViewHolder thumbnailsViewHolder = (ThumbnailsViewHolder) holder;
        thumbnailsViewHolder.thumbnail.setImageBitmap(thumbnailItem.image);

        String name;
        switch (position) {
            case 1:
                name = mContext.getResources().getString(R.string.filter_name_star);
                break;

            case 2:
                name =  mContext.getResources().getString(R.string.filter_name_blue);
                break;

            case 3:
                name =  mContext.getResources().getString(R.string.filter_name_awesome);
                break;

            case 4:
                name =  mContext.getResources().getString(R.string.filter_name_lime);
                break;

            case 5:
                name = mContext.getResources().getString(R.string.filter_name_night);
                break;

            default:
                name = mContext.getResources().getString(R.string.filter_name_original);
        }

        thumbnailsViewHolder.filterName.setText(name);
        thumbnailsViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPosition != position) {
                    thumbnailCallback.onThumbnailClick(thumbnailItem.filter);
                    lastPosition = position;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ThumbnailsViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView thumbnail;
        public TextView filterName;

        public ThumbnailsViewHolder(View v) {
            super(v);
            this.thumbnail = (CircularImageView) v.findViewById(R.id.thumbnail);
            this.filterName = (TextView) v.findViewById(R.id.filter_name);
        }
    }
}
