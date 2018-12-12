package com.bigsteptech.realtimechat.user;


public class User {

    private String uid;
    private String name, profileImageUrl, email, profileUrl;
    private long lastSeen;
    private int isOnline, visibility;
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
}
