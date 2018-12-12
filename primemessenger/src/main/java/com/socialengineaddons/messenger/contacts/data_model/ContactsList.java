package com.socialengineaddons.messenger.contacts.data_model;


import android.graphics.Color;

import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.user.User;

import java.util.Random;

public class ContactsList {

    private String mUId;
    private String mUserTitle, mUserImage;
    private int mOnlineStatus, profileColor;
    private long lastSeen;
    private boolean isContactSelected;

    public ContactsList(String userId){
        mUId = userId;
    }

    public ContactsList(User user){
        mUId = user.getUid();
        mUserTitle = user.getName();
        mUserImage = user.getProfileImageUrl();
        mOnlineStatus = user.getIsOnline();
        this.lastSeen = user.getLastSeen();
        setProfileColor();
    }

    public ContactsList(String userId, String userTitle, String userImage, int online, long lastSeen){

        mUId = userId;
        mUserTitle = userTitle;
        mUserImage = userImage;
        mOnlineStatus = online;
        this.lastSeen = lastSeen;
    }

    public String getmUserId() {
        return mUId;
    }

    public String getmUserTitle() {
        return mUserTitle;
    }

    public String getmUserImage() {
        return mUserImage;
    }

    public int getmOnlineStatus() {
        return mOnlineStatus;
    }

    public void setmUserTitle(String mUserTitle) {
        this.mUserTitle = mUserTitle;
    }

    public void setmUserImage(String mUserImage) {
        this.mUserImage = mUserImage;
    }

    public void setmOnlineStatus(int mOnlineStatus) {
        this.mOnlineStatus = mOnlineStatus;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isContactSelected() {
        return isContactSelected;
    }

    public void setContactSelected(boolean contactSelected) {
        isContactSelected = contactSelected;
    }

    public void setProfileColor() {
        Random random = new Random();
        this.profileColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public int getProfileColor() {
        return profileColor != 0 ? profileColor : R.color.colorAccent;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            ContactsList conversation = (ContactsList) object;
            if (this.mUId.equals(conversation.getmUserId())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.mUId.hashCode();
        return hash;
    }
}
