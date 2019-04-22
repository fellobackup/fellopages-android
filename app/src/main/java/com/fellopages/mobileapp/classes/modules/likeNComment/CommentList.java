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

package com.fellopages.mobileapp.classes.modules.likeNComment;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.util.HashMap;

public class CommentList {

    private int isFriendshipVerified;
    private int mCommentId, mLikeCount, mIsLike, mTotalCommmentCount, mUserId;
    private String mAuthorPhoto, mAuthorTitle, mCommentBody, mCommentDate, mFriendshipType, mReactionIcon;
    private JSONObject mDeleteJsonObject, mLikeJsonObject, mImageSize;
    private boolean showPosting, isFullCommentShowing = true;
    private String mStickerImage;
    private Bitmap mImageBitmap;
    HashMap<String, String> mClickableStringsList;

    public CommentList(){

    }

    public CommentList(int user_id, int mCommentId, int mLikeCount, int mIsLike, String mAuthorPhoto,
                       String mAuthorTitle, String mCommentBody, HashMap<String, String> clickableStrings, String mCommentDate, JSONObject mDeleteJsonObject,
                       JSONObject mLikeJsonObject, String stickerImage, JSONObject imageSize) {

        mUserId = user_id;
        this.mCommentId = mCommentId;
        this.mLikeCount = mLikeCount;
        this.mIsLike = mIsLike;
        this.mAuthorPhoto = mAuthorPhoto;
        this.mAuthorTitle = mAuthorTitle;
        this.mCommentBody = mCommentBody;
        this.mClickableStringsList = clickableStrings;
        this.mCommentDate = mCommentDate;
        this.mDeleteJsonObject = mDeleteJsonObject;
        this.mLikeJsonObject = mLikeJsonObject;
        this.mStickerImage = stickerImage;
        this.mImageSize = imageSize;
    }

    public CommentList(int user_id, String mAuthorTitle, String mAuthorPhoto, String friendshipType, int isFriendshipVerified) {
        mUserId = user_id;
        this.mAuthorPhoto = mAuthorPhoto;
        this.mAuthorTitle = mAuthorTitle;
        mFriendshipType = friendshipType;
        this.isFriendshipVerified = isFriendshipVerified;
    }

    public CommentList(int user_id, String mAuthorTitle, String mAuthorPhoto, String friendshipType,
                       String reactionIcon, int isFriendshipVerified) {
        mUserId = user_id;
        this.mAuthorPhoto = mAuthorPhoto;
        this.mAuthorTitle = mAuthorTitle;
        mFriendshipType = friendshipType;
        mReactionIcon = reactionIcon;
        this.isFriendshipVerified = isFriendshipVerified;
    }

    public CommentList(int user_id, String mAuthorTitle, String mAuthorPhoto, String mCommentBody, String stickerImage,
                       boolean showPosting) {
        mUserId = user_id;
        this.mAuthorPhoto = mAuthorPhoto;
        this.mAuthorTitle = mAuthorTitle;
        this.mCommentBody = mCommentBody;
        this.showPosting = showPosting;
        this.mStickerImage = stickerImage;
    }

    public CommentList(int user_id, String mAuthorTitle, String mAuthorPhoto, String mCommentBody, String stickerImage,
                       boolean showPosting, Bitmap imageBitmap) {
        mUserId = user_id;
        this.mAuthorPhoto = mAuthorPhoto;
        this.mAuthorTitle = mAuthorTitle;
        this.mCommentBody = mCommentBody;
        this.showPosting = showPosting;
        this.mStickerImage = stickerImage;
        this.mImageBitmap = imageBitmap;
    }

    public int getmUserId() {
        return mUserId;
    }

    public int getmCommentId() {
        return mCommentId;
    }

    public int getmLikeCount() {
        return mLikeCount;
    }

    public void setmLikeCount(int mLikeCount) {
        this.mLikeCount = mLikeCount;
    }

    public int getmIsLike() {
        return mIsLike;
    }

    public void setmIsLike(int mIsLike) {
        this.mIsLike = mIsLike;
    }

    public String getmAuthorPhoto() {
        return mAuthorPhoto;
    }

    public String getmAuthorTitle() {
        return mAuthorTitle;
    }

    public String getmCommentBody() {
        return mCommentBody;
    }

    public String getmCommentDate() {
        return mCommentDate;
    }


    public JSONObject getmDeleteJsonObject() {
        return mDeleteJsonObject;
    }

    public JSONObject getmLikeJsonObject() {
        return mLikeJsonObject;
    }

    public void setmLikeJsonObject(JSONObject mLikeJsonObject) {
        this.mLikeJsonObject = mLikeJsonObject;
    }

    public int getmTotalCommmentCount() {
        return mTotalCommmentCount;
    }

    public void setmTotalCommmentCount(int mTotalCommmentCount) {
        this.mTotalCommmentCount = mTotalCommmentCount;
    }

    public boolean isShowPosting() {
        return showPosting;
    }

    public String getmFriendshipType() {
        return mFriendshipType;
    }

    public void setmFriendshipType(String mFriendshipType) {
        this.mFriendshipType = mFriendshipType;
    }

    public String getmReactionIcon() {
        return mReactionIcon;
    }

    public String getmStickerImage() {
        return mStickerImage;
    }

    public Bitmap getmImageBitmap() {
        return mImageBitmap;
    }

    public int getIsFriendshipVerified() {
        return isFriendshipVerified;
    }
    public HashMap<String, String> getmClickableStringsList() {
        return mClickableStringsList;
    }

    public void showFullComment(boolean isFullCommentShowing) {
        this.isFullCommentShowing = isFullCommentShowing;
    }

    public boolean isFullCommentShowing() {
        return isFullCommentShowing;
    }

    public JSONObject getmImageSize() {
        return mImageSize;
    }
}
