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
import org.json.JSONObject;

public class CommunityAdsList {

    int mCommunityAdsId;
    String mCommunityAdType, mCommunityAdTitle, mCommunityAdBody, mCommunityAdUrl, mCommunityAdImage;
    private JSONArray removeAdsOptions;

    public CommunityAdsList (int userad_id, String ad_type, String cads_title, String cads_body,
                             String cads_url, String image){

        mCommunityAdsId = userad_id;
        mCommunityAdType = ad_type;
        mCommunityAdTitle = cads_title;
        mCommunityAdBody = cads_body;
        mCommunityAdUrl = cads_url;
        mCommunityAdImage = image;

    }

    public int getmCommunityAdsId() {
        return mCommunityAdsId;
    }

    public String getmCommunityAdBody() {
        return mCommunityAdBody;
    }

    public String getmCommunityAdImage() {
        return mCommunityAdImage;
    }

    public String getmCommunityAdUrl() {
        return mCommunityAdUrl;
    }

    public String getmCommunityAdTitle() {
        return mCommunityAdTitle;
    }

    public String getmCommunityAdType() {
        return mCommunityAdType;
    }

    public void setRemoveAdsOptions(JSONArray jsonObject){
        removeAdsOptions = jsonObject;
    }

    public JSONArray getRemoveAdsOptions() {
        return removeAdsOptions;
    }

    public void setmCommunityAdsId(int mCommunityAdsId) {
        this.mCommunityAdsId = mCommunityAdsId;
    }

    public void setmCommunityAdType(String mCommunityAdType) {
        this.mCommunityAdType = mCommunityAdType;
    }

    public void setmCommunityAdTitle(String mCommunityAdTitle) {
        this.mCommunityAdTitle = mCommunityAdTitle;
    }

    public void setmCommunityAdBody(String mCommunityAdBody) {
        this.mCommunityAdBody = mCommunityAdBody;
    }

    public void setmCommunityAdUrl(String mCommunityAdUrl) {
        this.mCommunityAdUrl = mCommunityAdUrl;
    }

    public void setmCommunityAdImage(String mCommunityAdImage) {
        this.mCommunityAdImage = mCommunityAdImage;
    }
}
