package com.bigsteptech.realtimechat.conversation.data_model;


import java.util.List;

public class Conversation {

    private int onlineStatus, typeOfChat;
    private String mUserImage, mUserTitle, mChatMessage, mConversationId, mMessageId, toId;
    private long lastSeen, lastUpdated;
    private String conversationTitle, conversationImage, lastMessageCreator, lastMessageCreatorId;
    private int unreadMessageCount;
    private int lastMessageStatus, visibility;
    private String typeOfLastMessage;
    private String lastUpdatedTime;
    private boolean isDuplicateChatExist, isChatDeleted;
    private List<String> participants;
    private boolean isTyping;

    public Conversation(String conversationId, String messageId, Long lastUpdated, String toId,
                        int typeOfChat, String conversationTitle, String conversationImage,
                        String lastUpdatedTime, boolean isDuplicateChatExist, List<String> participants){
        this.mMessageId = messageId;
        this.mConversationId = conversationId;
        this.lastUpdated = lastUpdated;
        this.toId = toId;
        this.typeOfChat = typeOfChat;
        this.conversationTitle = conversationTitle;
        this.conversationImage = conversationImage;
        this.lastUpdatedTime = lastUpdatedTime;
        this.isDuplicateChatExist = isDuplicateChatExist;
        this.participants = participants;
    }

    public Conversation(String conversationId) {
        this.mConversationId = conversationId;
    }

    public String getmConversationId() {
        return mConversationId;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getmChatMessage() {
        return mChatMessage;
    }

    public String getmUserTitle() {
        return mUserTitle;
    }

    public String getmUserImage() {
        return mUserImage;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public String getmMessageId() {
        return mMessageId;
    }

    public void setmMessageId(String messageId) {
        this.mMessageId = messageId;
    }

    public String getTypeOfLastMessage() {
        return typeOfLastMessage;
    }

    public void setTypeOfLastMessage(String typeOfLastMessage) {
        this.typeOfLastMessage = typeOfLastMessage;
    }

    public void setmChatMessage(String mChatMessage) {
        this.mChatMessage = mChatMessage;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public void setmUserImage(String mUserImage) {
        this.mUserImage = mUserImage;
    }

    public void setmUserTitle(String mUserTitle) {
        this.mUserTitle = mUserTitle;
    }

    public String getToId() {
        return toId;
    }

    public String setToId(String toId) {
        return this.toId = toId;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getTypeOfChat() {
        return typeOfChat;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public String getConversationImage() {
        return conversationImage;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public String getLastMessageCreator() {
        return lastMessageCreator;
    }

    public void setLastMessageCreator(String lastMessageCreator) {
        this.lastMessageCreator = lastMessageCreator;
    }

    public int getLastMessageStatus() {
        return lastMessageStatus;
    }

    public void setLastMessageStatus(int lastMessageStatus) {
        this.lastMessageStatus = lastMessageStatus;
    }

    public String getLastMessageCreatorId() {
        return lastMessageCreatorId;
    }

    public void setLastMessageCreatorId(String lastMessageCreatorId) {
        this.lastMessageCreatorId = lastMessageCreatorId;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
         this.lastUpdatedTime = lastUpdatedTime;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public boolean isDuplicateChatExist() {
        return isDuplicateChatExist;
    }

    public void setDuplicateChatExist(boolean duplicateChatExist) {
        isDuplicateChatExist = duplicateChatExist;
    }

    public boolean isChatDeleted() {
        return isChatDeleted;
    }

    public void setChatDeleted(boolean chatDeleted) {
        isChatDeleted = chatDeleted;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Conversation conversation = (Conversation) object;
            if (this.mConversationId.equals(conversation.getmConversationId())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.mConversationId.hashCode();
        return hash;
    }
}

