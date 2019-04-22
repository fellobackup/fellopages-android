package com.socialengineaddons.messenger.conversation.data_model;


import android.util.Log;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {

    String body, ownerId, fileUrl, userName, userImage;
    int status, type;
    private long createdAt;

    private String messageId;

    private String currentPositionOfSong;
    private int seekBarProgress;
    private boolean isLoadBitmap;
    private String videoThumbnail, attachmentType, attachmentId;
    private boolean isShowProgressBar;
    private String temporaryPath;
    private double audioDuration;
    private long itemId;
    private List<Message> messageArrayList;
    private long messageTimeStamp;

    private HashMap<String, Object> recipientsMap = new HashMap<>();

    public Message() {

    }

    public Message(String messageId) {
        this.messageId = messageId;
    }

    public Message(List<Message> messageArrayList) {
        Log.d(Message.class.getSimpleName(), " Size of Message List :" + messageArrayList.size());
        this.messageArrayList = messageArrayList;
    }

    public Message(boolean isShowProgressBar) {
        this.isShowProgressBar = isShowProgressBar;
    }

    public Message(String body, String ownerId, HashMap<String, Object> recipientMap,
                   String attachmentType, String attachmentId) {
        this.body = body;
        this.ownerId = ownerId;
        this.recipientsMap = recipientMap;
        this.attachmentType = attachmentType;
        this.attachmentId = attachmentId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("body", body);
        result.put("createdAt", ServerValue.TIMESTAMP);
        result.put("ownerId", ownerId);
        result.put("recipients", recipientsMap);
        result.put("attachmentType", attachmentType);
        result.put("attachmentId", attachmentId);
        return result;
    }

    public String getBody() {
        return body;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getSeekBarProgress() {
        return seekBarProgress;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Message conversation = (Message) object;
            if (this.messageId.equals(conversation.getMessageId())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        if(this.messageId != null) {
            int hash = 3;
            hash = 7 * hash + this.messageId.hashCode();
            return hash;
        }
        return 0;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public boolean isShowProgressBar() {
        return isShowProgressBar;
    }

    public void setShowProgressBar(boolean showProgressBar) {
        isShowProgressBar = showProgressBar;
    }

    public String getTemporaryPath() {
        return temporaryPath;
    }

    public void setTemporaryPath(String temporaryPath) {
        this.temporaryPath = temporaryPath;
    }

    public double getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(double audioDuration) {
        this.audioDuration = audioDuration;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public List<Message> getMessageArrayList() {
        return messageArrayList;
    }

    public long getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public void setMessageTimeStamp(long messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }
}
