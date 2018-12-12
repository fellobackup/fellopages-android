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

import org.json.JSONObject;

public class AddPeopleList {

    String mUserLabel;
    String mUserPhoto;
    String mUserEmailId;
    int mUserId;
    JSONObject mHostObject;

    public AddPeopleList(int userId, String userLabel, String userPhoto){

        mUserLabel = userLabel;
        mUserPhoto = userPhoto;
        mUserId = userId;
    }

    //For getting friend list in Message module
    public AddPeopleList(int userId,String userName, String userImage, String userEmailId){
        mUserId = userId;
        mUserLabel = userName;
        mUserPhoto = userImage;
        mUserEmailId = userEmailId;
    }

    // Getting user's list for host change
    public AddPeopleList(int userId, String userLabel, String userPhoto, JSONObject jsonObject){

        mUserLabel = userLabel;
        mUserPhoto = userPhoto;
        mUserId = userId;
        mHostObject = jsonObject;
    }

    public String getmUserLabel() {
        return mUserLabel;
    }

    public String getmUserPhoto() {
        return mUserPhoto;
    }

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    public JSONObject getmHostObject() {
        return mHostObject;
    }
}
