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

package com.fellopages.mobileapp.classes.common.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ImageViewList {
    private String mGridViewImageUrl;
    private Bitmap mGridPhotoBitmap;
    private Drawable drawableIcon;
    private int totalPhotoCount;
    private int remainingPhotoCount;
    float mImageWidth, mImageHeight;
    private String albumDescription,ownerTitle,ownerImageUrl;
    private String mCaption, mReaction;
    private String mReactionIcon;
    private int mReactionId, mStickerId, mSelectedItemPos;
    private String mStickerTitle, mStickerBackGroundColor, mStickerGuid, mStickerKey;

    public ImageViewList(){

    }

    //For Album View Page
    public ImageViewList (String imgUrl , String albumDescription, String ownerTitle,
                          String ownerImageUrl){
        mGridViewImageUrl=imgUrl;
        this.albumDescription = albumDescription;
        this.ownerImageUrl = ownerImageUrl;
        this.ownerTitle = ownerTitle;

    }

    public ImageViewList(Drawable drawable) {
        drawableIcon = drawable;
    }

    public ImageViewList(Drawable drawable, String description) {
        drawableIcon = drawable;
        this.albumDescription = description;
    }


    public void setOwnerImageUrl(String ownerImageUrl) {
        this.ownerImageUrl = ownerImageUrl;
    }

    public void setOwnerTitle(String ownerTitle) {
        this.ownerTitle = ownerTitle;
    }

    public void setAlbumDescription(String albumDescription) {
        this.albumDescription = albumDescription;
    }

    public String getAlbumDescription() {
        return albumDescription;
    }

    public String getOwnerTitle() {
        return ownerTitle;
    }

    public String getOwnerImageUrl() {
        return ownerImageUrl;
    }

    public ImageViewList(String imgurl){
        mGridViewImageUrl=imgurl;
    }

    public ImageViewList(String imgurl, float width, float height){
        mGridViewImageUrl = imgurl;
        mImageWidth = width;
        mImageHeight = height;
    }

    // For Reaction Icons
    public ImageViewList(String imgurl, String caption, String reaction, int reactionId, String reactionIcon){
        mGridViewImageUrl = imgurl;
        mCaption = caption;
        mReaction = reaction;
        mReactionId = reactionId;
        mReactionIcon = reactionIcon;
    }

    public ImageViewList(String imgurl, float width, float height, int photoCount){
        mGridViewImageUrl = imgurl;
        mImageWidth = width;
        mImageHeight = height;
        remainingPhotoCount = photoCount;
    }

    public ImageViewList(String imgurl, String stickerGuid){
        mGridViewImageUrl = imgurl;
        mStickerGuid = stickerGuid;
    }

    public ImageViewList(String imgurl, int stickerId, String stickerTitle, String stickersKey, String backGroundColor){
        mGridViewImageUrl = imgurl;
        mStickerId = stickerId;
        mStickerTitle = stickerTitle;
        mStickerKey = stickersKey;
        mStickerBackGroundColor = backGroundColor;
    }

    public ImageViewList(Bitmap imgurl){
        mGridPhotoBitmap = imgurl;
    }

    public ImageViewList(int selectedPosition, Bitmap imgurl){
        mSelectedItemPos = selectedPosition;
        mGridPhotoBitmap = imgurl;
    }

    public int getmSelectedItemPos() {
        return mSelectedItemPos;
    }

    public void setmSelectedItemPos(int mSelectedItemPos) {
        this.mSelectedItemPos = mSelectedItemPos;
    }

    public String getmGridViewImageUrl(){
        return mGridViewImageUrl;
    }

    public Bitmap getmGridPhotoUrl() {
        return mGridPhotoBitmap;
    }

    public int getTotalPhotoCount() {
        return totalPhotoCount;
    }

    public void setTotalPhotoCount(int totalPhotoCount) {
        this.totalPhotoCount = totalPhotoCount;
    }

    public float getmImageWidth() {
        return mImageWidth;
    }

    public float getmImageHeight() {
        return mImageHeight;
    }

    public int getRemainingPhotoCount() {
        return remainingPhotoCount;
    }

    public String getmCaption() {
        return mCaption;
    }

    public String getmReaction() {
        return mReaction;
    }

    public int getmReactionId() {
        return mReactionId;
    }

    public String getmReactionIcon() {
        return mReactionIcon;
    }

    public int getmStickerId() {
        return mStickerId;
    }

    public String getmStickerTitle() {
        return mStickerTitle;
    }

    public String getmStickerBackGroundColor() {
        return mStickerBackGroundColor;
    }

    public String getmStickerGuid() {
        return mStickerGuid;
    }

    public String getmStickerKey() {
        return mStickerKey;
    }

    public Drawable getDrawableIcon() {
        return drawableIcon;
    }
}
