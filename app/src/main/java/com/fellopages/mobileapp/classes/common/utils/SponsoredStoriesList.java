/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.common.utils;


import org.json.JSONArray;

public class SponsoredStoriesList {

    int mResourceId, isLike, mAdId, mLikeCount;
    String mResourceType, mResourceTitle, mContentUrl, image, mModuleTitle;
    JSONArray mLikesArray;
    private JSONArray removeAdsOptions;

    public SponsoredStoriesList(int adId, int resourceId, String resourceType, String resourceTitle, JSONArray likes,
                                String contentUrl, String image, int isLike, String moduleTitle, int likeCount){

        mAdId = adId;
        mResourceId = resourceId;
        mResourceType = resourceType;
        mResourceTitle = resourceTitle;
        mLikesArray = likes;
        mContentUrl = contentUrl;
        this.image = image;
        mModuleTitle = moduleTitle;
        mLikeCount = likeCount;
        this.isLike = isLike;
    }

    public int getmResourceId() {
        return mResourceId;
    }

    public String getmResourceTitle() {
        return mResourceTitle;
    }

    public String getmResourceType() {
        return mResourceType;
    }

    public String getmContentUrl() {
        return mContentUrl;
    }

    public String getImage() {
        return image;
    }

    public JSONArray getmLikesArray() {
        return mLikesArray;
    }

    public int getIsLike() {
        return isLike;
    }

    public String getmModuleTitle() {
        return mModuleTitle;
    }

    public int getmAdId() {
        return mAdId;
    }

    public void setRemoveAdsOptions(JSONArray jsonObject){
        removeAdsOptions = jsonObject;
    }

    public JSONArray getRemoveAdsOptions() {
        return removeAdsOptions;
    }

    public void setmResourceId(int mResourceId) {
        this.mResourceId = mResourceId;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public void setmAdId(int mAdId) {
        this.mAdId = mAdId;
    }

    public void setmResourceType(String mResourceType) {
        this.mResourceType = mResourceType;
    }

    public void setmResourceTitle(String mResourceTitle) {
        this.mResourceTitle = mResourceTitle;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setmContentUrl(String mContentUrl) {
        this.mContentUrl = mContentUrl;
    }

    public void setmModuleTitle(String mModuleTitle) {
        this.mModuleTitle = mModuleTitle;
    }

    public void setmLikesArray(JSONArray mLikesArray) {
        this.mLikesArray = mLikesArray;
    }

    public int getmLikeCount() {
        return mLikeCount;
    }
}
