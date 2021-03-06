package com.bigsteptech.realtimechat.groups.data_model;


import com.bigsteptech.realtimechat.Constants;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GroupsList {

    private String profileImageUrl, title, mLastActiveTime, ownerId, mGroupId;
    private int memberCount;
    private LinkedHashMap<String, Object> participants;
    private long lastUpdated;
    private String type, isGroup;

    public GroupsList(String ownerId, int memberCount, String isGroup, String type, LinkedHashMap<String, Object> participants){

        this.ownerId = ownerId;
        this.memberCount = memberCount;
        this.isGroup = isGroup;
        this.type = type;
        this.participants = participants;
    }

    public GroupsList(String ownerId, int memberCount, String isGroup, String type, String profileImageUrl,
                      String title, LinkedHashMap<String, Object> participants){

        this.ownerId = ownerId;
        this.memberCount = memberCount;
        this.isGroup = isGroup;
        this.type = type;
        this.profileImageUrl = profileImageUrl;
        this.title = title;
        this.participants = participants;
    }

    public GroupsList(String groupId, String groupImageUrl, String groupTitle, String lastActiveTime,
                      long lastUpdatedTime, int memberCount){

        mGroupId = groupId;
        profileImageUrl = groupImageUrl;
        title = groupTitle;
        mLastActiveTime = lastActiveTime;
        lastUpdated = lastUpdatedTime;
        this.memberCount = memberCount;
    }

    public GroupsList(String groupId){
        this.mGroupId = groupId;
    }


    public String getmGroupId() {
        return mGroupId;
    }

    public String getmLastActiveTime() {
        return mLastActiveTime;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ownerId", ownerId);
        result.put("type", type);
        result.put("isGroup", isGroup);
        result.put("memberCount", memberCount);
        result.put(Constants.FIREBASE_IMAGE_KEY, profileImageUrl);
        result.put("title", title);
        result.put("participants", participants);

        return result;
    }

    public LinkedHashMap<String, Object> getParticipants() {
        return participants;
    }

    public void setParticipants(LinkedHashMap<String, Object> participants) {
        this.participants = participants;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getType() {
        return type;
    }

    public String getIsGroup() {
        return isGroup;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            GroupsList conversation = (GroupsList) object;
            if (this.mGroupId.equals(conversation.getmGroupId())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.mGroupId.hashCode();
        return hash;
    }
}
