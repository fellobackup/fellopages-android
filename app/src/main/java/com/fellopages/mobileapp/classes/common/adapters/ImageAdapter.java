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

package com.fellopages.mobileapp.classes.common.adapters;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private List<ImageViewList> mPhotoList;
    private int mImageWidth, mImageHeight, mImageCount;
    LayoutInflater inflater;
    boolean mIsReactionsAdapter = false;

    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;

    public ImageAdapter(Activity activity, List<ImageViewList> photoList,
                        OnItemClickListener onItemClickListener){
        this.mActivity = activity;
        this.mPhotoList = photoList;
        inflater = LayoutInflater.from(activity);
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mActivity);
    }

    public ImageAdapter(Activity activity, List<ImageViewList> photoList,
                        boolean isReactionsAdapter, OnItemClickListener onItemClickListener){
        this.mActivity = activity;
        this.mPhotoList = photoList;
        inflater = LayoutInflater.from(activity);
        mIsReactionsAdapter = isReactionsAdapter;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mActivity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(inflater.inflate(R.layout.feeds_image, parent, false));
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ImageViewList img = mPhotoList.get(position);
        ImageHolder imageHolder = (ImageHolder) holder;

        mImageWidth  = (int) img.getmImageWidth();
        mImageHeight = (int) img.getmImageHeight();
        mImageCount = img.getRemainingPhotoCount();

        if(!mIsReactionsAdapter){

            mImageLoader.setFeedImage(img.getmGridViewImageUrl(), imageHolder.imageView);

            if(mPhotoList.size() == 1) {
                imageHolder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            imageHolder.imageView.setLayoutParams(CustomViews.getCustomWidthHeightRelativeLayoutParams(mImageWidth,
                    mImageHeight));

            if(mImageCount != 0 ){
                imageHolder.photoCount.setText("+" + mImageCount);
                imageHolder.photoCount.setVisibility(View.VISIBLE);
            }
            if (img.getmGridViewImageUrl() != null && !img.getmGridViewImageUrl().isEmpty()
                    && img.getmGridViewImageUrl().contains(".gif")) {
                imageHolder.ivGifIcon.setVisibility(View.VISIBLE);
            } else {
                imageHolder.ivGifIcon.setVisibility(View.GONE);
            }
        } else {

            imageHolder.reactionIcon.setVisibility(View.VISIBLE);
            imageHolder.imageView.setVisibility(View.GONE);
            mImageLoader.setReactionImageUrl(img.getmGridViewImageUrl(), imageHolder.reactionIcon);
        }

        /**
         * Set OnItemClick on Images
         */
        ((ImageHolder) holder).container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, holder.getAdapterPosition());
            }
        });

        // Todo Zoom effect on reaction icons on touch
        /**
         * Set OnLongClick listener on reaction icons
         */
//        ((ImageHolder) holder).container.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return mOnItemClickListener.onItemTouch(v, position);
//            }
//        });



    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    /**
     * Holder for Image View
     */
    public static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView, reactionIcon, ivGifIcon;
        TextView photoCount;
        public View container;

        public ImageHolder(View itemView) {
            super(itemView);
            container = itemView;
            reactionIcon = (ImageView) itemView.findViewById(R.id.reactionIcon);
            imageView = (ImageView) itemView.findViewById(R.id.thumbnail);
            ivGifIcon = (ImageView) itemView.findViewById(R.id.gif_icon);
            photoCount = (TextView) itemView.findViewById(R.id.photoCount);
        }
    }
}

