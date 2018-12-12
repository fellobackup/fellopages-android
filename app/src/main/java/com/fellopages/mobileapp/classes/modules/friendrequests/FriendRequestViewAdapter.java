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

package com.fellopages.mobileapp.classes.modules.friendrequests;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.viewholder.ProgressViewHolder;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;

public class FriendRequestViewAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROGRESSBAR = 0;
    private List<BrowseListItems> mBrowseItemList;
    private BrowseListItems mListItem;
    private Context mContext;

    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;


    public interface OnItemClickListener {
        void onAcceptButtonClick(View view, int position);
        void onIgnoreButtonClick(View view,int position);
        void onProfilePictureClicked(View view,int position);
    }

    public FriendRequestViewAdapter(Context context,List<BrowseListItems> listItem,
                                    OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
        this.mContext=context;
        this.mBrowseItemList=listItem;
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return mBrowseItemList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESSBAR;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_friend_requests, parent, false);
            viewHolder = new FRequestViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FRequestViewHolder) {
            mListItem = mBrowseItemList.get(position);
            ((FRequestViewHolder) holder).listItem = mListItem;

            mImageLoader.setImageForUserProfile(mListItem.getmBrowseImgUrl(), ((FRequestViewHolder) holder).userImage);
            ((FRequestViewHolder) holder).mOwnerName.setText(mListItem.getmBrowseListOwnerTitle());

            ((FRequestViewHolder) holder).acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onAcceptButtonClick(v, position);
                }
            });
            ((FRequestViewHolder) holder).ignoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onIgnoreButtonClick(v, position);
                }
            });

            ((FRequestViewHolder) holder).userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onProfilePictureClicked(v, position);
                }
            });

            ((FRequestViewHolder) holder).mOwnerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onProfilePictureClicked(v, position);
                }
            });


        }else {
            ProgressViewHolder.inflateProgressBar(((ProgressViewHolder) holder).progressView);
        }
    }

    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }

    public static class FRequestViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public BrowseListItems listItem;
        public TextView mOwnerName;
        public Button acceptButton,ignoreButton;
        public View container;

        public FRequestViewHolder(View view) {

            super(view);
            container = view;
            userImage = (ImageView) view.findViewById(R.id.userImage);
            mOwnerName = (TextView) view.findViewById(R.id.userName);
            acceptButton = (Button) view.findViewById(R.id.acceptRequest);
            ignoreButton = (Button) view.findViewById(R.id.ignoreRequest);

        }
    }

}
