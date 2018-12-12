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

public class SelectedFriendList {

    private int mSelectedUserId;
    private String mSelectedUserName;

    public SelectedFriendList(int userId, String userName){
        mSelectedUserId = userId;
        mSelectedUserName = userName;

    }

    public String getSelectedUserName() {
        return mSelectedUserName;
    }

    public int getSelectedUserId() {
        return mSelectedUserId;
    }
}
