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
import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;


import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<ImageViewList> mPhotoList;
    private List<PhotoListDetails> mPhotoListDetail;
    private int mImageWidth;
    private LayoutInflater inflater;
    private boolean mIsBitMap = false, isPhotoFragment, isStickers;
    private IScrollup iscrollup;
    private ImageLoader mImageLoader;


    public GridViewAdapter(Activity activity,int imageWidth, boolean isStickers, List<ImageViewList> photoList){

        this(activity, imageWidth, photoList);
        this.isStickers = isStickers;
    }

    public GridViewAdapter(Activity activity,int imageWidth,List<ImageViewList> photoList){

        this.mActivity = activity;
        this.mPhotoList = photoList;
        this.mImageWidth = imageWidth;
        mImageLoader = new ImageLoader(mActivity);
    }

    public GridViewAdapter(Activity activity,int imageWidth,List<ImageViewList> photoList,
                           boolean isPhotoFragment, boolean isBitMap){

        this.mActivity = activity;
        this.mPhotoList = photoList;
        this.mImageWidth = imageWidth;
        this.mIsBitMap = isBitMap;
        mImageLoader = new ImageLoader(mActivity);
    }

    public GridViewAdapter(Activity activity,int imageWidth,List<PhotoListDetails> photoList, boolean isPhotoFragment){

        this.mActivity = activity;
        this.mPhotoListDetail = photoList;
        this.mImageWidth = imageWidth;
        this.isPhotoFragment = isPhotoFragment;
        mImageLoader = new ImageLoader(mActivity);
    }


    @Override
    public int getCount() {
        if(isPhotoFragment) {
            return mPhotoListDetail.size();
        }else {
            return mPhotoList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(isPhotoFragment) {
            return mPhotoListDetail.get(position);
        }else {
            return mPhotoList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        View view = convertView;

        if (convertView == null){
            inflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.pager_photo_view,null);
            holder = new ViewHolder();
            assert view != null;
            holder.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            holder.ivGifIcon = (ImageView) view.findViewById(R.id.gif_icon);
            holder.imageView.setLayoutParams(CustomViews.getCustomWidthHeightRelativeLayoutParams(mImageWidth, mImageWidth));
            holder.likeCount = (TextView) view.findViewById(R.id.photoLikeCount);
            holder.commentCount = (TextView) view.findViewById(R.id.photoCommentCount);
            holder.likeIcon = (TextView) view.findViewById(R.id.photoLikeIcon);
            holder.commentIcon = (TextView) view.findViewById(R.id.photoCommentIcon);
            view.setTag(holder);

        }else{
            holder = (ViewHolder) view.getTag();
        }

        if(isPhotoFragment){
            view.findViewById(R.id.photoBottomView).setVisibility(View.VISIBLE);
            PhotoListDetails listDetails = mPhotoListDetail.get(position);
            holder.commentIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mActivity));
            holder.likeIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mActivity));
            holder.likeIcon.setText("\uf164");
            holder.commentIcon.setText("\uf075");
            holder.likeCount.setText(String.valueOf(listDetails.getImageLikeCount()));
            holder.commentCount.setText(String.valueOf(listDetails.getImageCommentCount()));
            mImageLoader.setFeedImage(listDetails.getImageUrl(), holder.imageView);
            if (listDetails.getImageUrl() != null && !listDetails.getImageUrl().isEmpty()
                    && listDetails.getImageUrl().contains(".gif")) {
                holder.ivGifIcon.setVisibility(View.VISIBLE);
            } else {
                holder.ivGifIcon.setVisibility(View.GONE);
            }

        }else if(mIsBitMap ) {
            ImageViewList img = mPhotoList.get(position);
            holder.imageView.setImageBitmap(img.getmGridPhotoUrl());
            if (position == mPhotoList.size() - 1) {
                if (iscrollup != null) {
                    iscrollup.setScrollUpPosition(position);
                }
            }
        }else {
            ImageViewList img = mPhotoList.get(position);
            if (img.getmGridViewImageUrl() != null && !img.getmGridViewImageUrl().isEmpty()) {
                if(isStickers){
                    mImageLoader.setImageWithStickerBackground(img.getmGridViewImageUrl(), holder.imageView);
                } else{
                    mImageLoader.setAlbumPhoto(img.getmGridViewImageUrl(), holder.imageView);
                }
            }
            if (position == mPhotoList.size() - 1) {
                if (iscrollup != null) {
                    iscrollup.setScrollUpPosition(position);
                }
            }
        }

        return view;
    }


    public interface IScrollup {
        void setScrollUpPosition(int pos);
    }

    public void setIScrolup(IScrollup iscrolup) {
        this.iscrollup = iscrolup;
    }

    public IScrollup getIScrolup() {
        return iscrollup;
    }

    static class ViewHolder {
        ImageView imageView, ivGifIcon;
        TextView likeCount,commentCount,likeIcon,commentIcon;
    }
}

