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

package com.fellopages.mobileapp.classes.modules.photoLightBox;

import org.json.JSONArray;

import java.io.Serializable;

public class PhotoListDetails implements Serializable {

    private String mImageUrl, mImageRequestUrl, mSubjectType;
    private int mTotalImageCount, mImageLikeCount,mImageCommentCount, mPhotoId , mPosition, mAlbumId;
    private boolean isLiked;
    private String mOwnerTitle, mAlbumTitle, mImageTitle, mImageDescription, mReactionsObject;
    private String mMenuArray;
    private String mUserTagArray;
    private String mAttachmentUri;

    //For Classifieds
    public PhotoListDetails(String albumTitle, String ownerTitle, String photoTitle, String photoDescription,
                            int photoId, String imgUrl, int likeCount,
                            boolean likeStatus, String menuArray){

        mAlbumTitle = albumTitle;
        mOwnerTitle = ownerTitle;
        mImageTitle = photoTitle;
        mImageDescription = photoDescription;
        mPhotoId = photoId;
        mImageUrl = imgUrl;
        mImageLikeCount = likeCount;
        isLiked = likeStatus;
        mMenuArray = menuArray;

    }


    //For Albums,Event and Groups
    public PhotoListDetails(String albumTitle,String ownerTitle,String photoTitle,String photoDescription,
                            int photoId,String imgUrl, int likeCount, int commentCount,
                            boolean likeStatus,String menuArray){

        mImageUrl = imgUrl;
        mImageCommentCount = commentCount;
        mImageLikeCount = likeCount;
        isLiked = likeStatus;
        mMenuArray = menuArray;
        mPhotoId = photoId;
        mAlbumTitle = albumTitle;
        mOwnerTitle = ownerTitle;
        mImageTitle = photoTitle;
        mImageDescription = photoDescription;
    }

    //For Albums,Event and Groups
    public PhotoListDetails(String albumTitle, String ownerTitle, String photoTitle, String photoDescription,
                            int photoId, String imgUrl, int likeCount, int commentCount,
                            boolean likeStatus, String menuArray, String reactionsObject, String userTagArray, String contentUrl){

        mImageUrl = imgUrl;
        mImageCommentCount = commentCount;
        mImageLikeCount = likeCount;
        isLiked = likeStatus;
        mMenuArray = menuArray;
        mPhotoId = photoId;
        mAlbumTitle = albumTitle;
        mOwnerTitle = ownerTitle;
        mImageTitle = photoTitle;
        mImageDescription = photoDescription;
        mReactionsObject = reactionsObject;
        mUserTagArray = userTagArray;
        mAttachmentUri = contentUrl;
    }

    //Activity Feeds
    public PhotoListDetails(int photoId, String imgUrl, int likeCount, int commentCount, String userTagArray,
                            boolean likeStatus, String reactions){

        mImageUrl = imgUrl;
        mImageCommentCount = commentCount;
        mImageLikeCount = likeCount;
        isLiked = likeStatus;
        mPhotoId = photoId;
        mReactionsObject = reactions;
        mUserTagArray = userTagArray;
    }

    //Activity Feeds
    public PhotoListDetails(String photoDescription, int photoId, String imgUrl, int likeCount,
                            int commentCount, boolean likeStatus, String reactions, String userTagArray,
                            String menuArray, String uri){
        mImageDescription = photoDescription;
        mPhotoId = photoId;
        mImageUrl = imgUrl;
        mImageLikeCount = likeCount;
        mImageCommentCount = commentCount;
        isLiked = likeStatus;
        mReactionsObject = reactions;
        mUserTagArray = userTagArray;
        mMenuArray = menuArray;
        mAttachmentUri = uri;
    }

    //Single Activity Feeds
    public PhotoListDetails(int photoId,String imgUrl, int likeCount, int commentCount,
                            boolean likeStatus, String subjectType, String reactions){

        mImageUrl = imgUrl;
        mImageCommentCount = commentCount;
        mImageLikeCount = likeCount;
        isLiked = likeStatus;
        mPhotoId = photoId;
        mSubjectType = subjectType;
        mReactionsObject = reactions;
    }

        //Single Activity Feeds
    public PhotoListDetails(int photoId, int albumId, String imgUrl, int likeCount, int commentCount,
                            boolean likeStatus, String subjectType, String reactions, String menuArray, String uri){

        mImageUrl = imgUrl;
        mImageCommentCount = commentCount;
        mImageLikeCount = likeCount;
        isLiked = likeStatus;
        mPhotoId = photoId;
        mAlbumId = albumId;
        mSubjectType = subjectType;
        mReactionsObject = reactions;
        mMenuArray = menuArray;
        mAttachmentUri = uri;
    }

    //Message view page.
    public PhotoListDetails(int photoId,String imgUrl,String imageRequestUrl,int totalImageCount,
                            int likeCount, int commentCount, boolean likeStatus, int position) {

        mImageUrl = imgUrl;
        mImageRequestUrl = imageRequestUrl;
        mTotalImageCount = totalImageCount;
        mImageCommentCount = commentCount;
        mImageLikeCount = likeCount;
        isLiked = likeStatus;
        mPhotoId = photoId;
        mPosition = position;
    }

    //Profile Page Cover Photos
    public PhotoListDetails(String imgUrl){
        mImageUrl = imgUrl;
    }

    public String getOwnerTitle() {
        return mOwnerTitle;
    }

    public void setOwnerTitle(String mOwnerTitle) {
        this.mOwnerTitle = mOwnerTitle;
    }

    public String getAlbumTitle() {
        return mAlbumTitle;
    }

    public String getImageTitle() {
        return mImageTitle;
    }

    public String getImageDescription() {
        return mImageDescription;
    }

    public int getPhotoId() {
        return mPhotoId;
    }

    public int geAlbumId() {
        return mAlbumId;
    }

    public String getMenuArray() {
        return mMenuArray;
    }

    public void setMenuArray(String mMenuArray) {
        this.mMenuArray = mMenuArray;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public int getImageCommentCount() {
        return mImageCommentCount;
    }

    public int getImageLikeCount() {
        return mImageLikeCount;
    }

    public void setImageLikeCount(int mImageLikeCount) {
        this.mImageLikeCount = mImageLikeCount;
    }

    public int getmPosition() {
        return mPosition;
    }

    public int getmTotalImageCount() {
        return mTotalImageCount;
    }

    public void setmImageCommentCount(int mImageCommentCount) {
        this.mImageCommentCount = mImageCommentCount;
    }

    public String getmImageRequestUrl() {
        return mImageRequestUrl;
    }

    public String getmReactionsObject() {
        return mReactionsObject;
    }

    public void setmReactionsObject(String reactionsObject) {
        this.mReactionsObject = reactionsObject;
    }

    public String getmSubjectType() {
        return mSubjectType;
    }

    public String getmUserTagArray() {
        return mUserTagArray;
    }

    public String getmAttachmentUri(){
        return mAttachmentUri;
    }
}
