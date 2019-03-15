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

package com.fellopages.mobileapp.classes.modules.advancedActivityFeeds;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;


public class FeelingActivityAdapter extends RecyclerView.Adapter<FeelingActivityAdapter.ItemViewHolder> {

    // Member variables.
    private Context mContext;
    private List<BrowseListItems> mBrowseItemList;
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;


    public FeelingActivityAdapter(Context context, List<BrowseListItems> browseItemList,
                                  OnItemClickListener onItemClickListener) {

        this.mContext = context;
        mBrowseItemList = browseItemList;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.feeling_activity_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        BrowseListItems browseListItems = mBrowseItemList.get(position);
        mImageLoader.setAlbumPhoto(browseListItems.getmBrowseImgUrl(), holder.ivMainImage);

        holder.tvTitle.setText(browseListItems.getmBrowseListTitle());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    public void setUpdatedList(List<BrowseListItems> updatedList) {
        mBrowseItemList = updatedList;
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ImageView ivMainImage;
        public TextView tvTitle;

        public ItemViewHolder(View view) {
            super(view);
            container = view;

            ivMainImage = view.findViewById(R.id.main_image);
            tvTitle = view.findViewById(R.id.title);
        }
    }

}
