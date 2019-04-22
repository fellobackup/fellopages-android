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

package com.fellopages.mobileapp.classes.modules.messages;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.ui.viewholder.ProgressViewHolder;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;


public class MessageViewAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Object> mBrowseItemList;
    private BrowseListItems mListItem;
    private Context mContext;


    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public MessageViewAdapter(Context context,List<Object> listItem,
                               OnItemClickListener onItemClickListener){

        mOnItemClickListener = onItemClickListener;
        this.mContext = context;
        this.mBrowseItemList = listItem;
        mImageLoader = new ImageLoader(mContext);

    }

    @Override
    public int getItemViewType(int position) {
        return mBrowseItemList.get(position) instanceof String ? VIEW_PROG : VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.message_view, parent, false);
            viewHolder = new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {

            mListItem = (BrowseListItems) mBrowseItemList.get(position);

            ((ItemViewHolder) holder).listItem = mListItem;

            String date = AppConstant.convertDateFormat(mContext.getResources(),
                    mListItem.getmBrowseListCreationDate());
            ((ItemViewHolder) holder).mDateView.setText(date);
            ((ItemViewHolder) holder).mOwnerTitle.setText(Html.fromHtml(mListItem.getmBrowseListOwnerTitle()));

            mImageLoader.setImageForUserProfile(mListItem.getmBrowseImgUrl(), ((ItemViewHolder) holder).mOwnerImage);

            if(mListItem.getMessageTitle().isEmpty()){
                ((ItemViewHolder) holder).mMessageDescription.setText(Html.fromHtml(Html.fromHtml(
                        mListItem.getMessageBody()).toString()));
            }else {
                ((ItemViewHolder) holder).mMessageDescription.setText(Html.fromHtml(Html.fromHtml(
                        mListItem.getMessageTitle()).toString()));
            }

            if(mListItem.getInboxRead() == 0){
                ((ItemViewHolder) holder).container.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.themeButtonColor));
                ((ItemViewHolder) holder).container.getBackground().setAlpha(30);
            }else {
                ((ItemViewHolder) holder).container.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.white));
            }

            ((ItemViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            });

        } else {
            ProgressViewHolder.inflateFooterView(mContext, ((ProgressViewHolder) holder).progressView,
                    mBrowseItemList.get(position), ConstantVariables.ACTION_VIEW_ALL_MESSAGES);
        }

    }

    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public BezelImageView mOwnerImage;
        public BrowseListItems listItem;
        public TextView mOwnerTitle;
        public SelectableTextView mDateView, mMessageDescription;
        public View container;

        public ItemViewHolder(View view) {

            super(view);
            view.setClickable(true);
            container = view;
            mOwnerImage = view.findViewById(R.id.senderImage);
            mMessageDescription = view.findViewById(R.id.messageDescription);
            mOwnerTitle = view.findViewById(R.id.ownerTitle);
            mDateView = view.findViewById(R.id.messageDate);
        }
    }

}
