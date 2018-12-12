package com.bigsteptech.realtimechat.contacts.data_model;


import com.bigsteptech.realtimechat.groups.data_model.GroupsList;

public class ContactsList {

    private String mUId;
    private String mUserTitle, mUserImage;
    private int mOnlineStatus;
    private long lastSeen;
    private boolean isContactSelected;

    public ContactsList(String userId){
        mUId = userId;
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

    public long getLastSeen() {
        return lastSeen;
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
