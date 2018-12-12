package com.socialengineaddons.messenger.user;


import android.graphics.Color;

import com.socialengineaddons.messenger.R;

import java.util.Random;

public class User {

    private String uid;
    private String name, profileImageUrl, email, profileUrl;
    private long lastSeen;
    private int isOnline, visibility;
    private int profileColor;
    private boolean isDeleted = false;

    @SuppressWarnings("unused") //Used by Firebase
    public User() {
    }

    public User(String email, int isOnline, long lastSeen, String name, String profileImageUrl, String profileUrl,
                int visibility) {

        this.email = email;
        this.isOnline = isOnline;
        this.lastSeen = lastSeen;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.profileUrl = profileUrl;
        this.visibility = visibility;
    }

    public User(String name, String profileImageUrl, int isOnline, long lastSeen, int visibility) {
        this.isOnline = isOnline;
        this.lastSeen = lastSeen;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.visibility = visibility;
    }

    public String getEmail() {
        return email;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getVisibility() {
        return visibility;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
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
            User user = (User) object;
            if (this.uid.equals(user.getUid())) {
                result = true;
            }
        }
        return result;
    }
}
