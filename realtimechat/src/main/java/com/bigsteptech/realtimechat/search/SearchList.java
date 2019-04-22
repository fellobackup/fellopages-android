package com.bigsteptech.realtimechat.search;


import com.bigsteptech.realtimechat.groups.data_model.GroupsList;

public class SearchList {

    private String title, imagePath, conversationId, userId;
    private int typeOfChat;

    public SearchList(String title, String imagePath, String conversationId, int typeOfChat, String userId) {
        this.title = title;
        this.imagePath = imagePath;
        this.conversationId = conversationId;
        this.typeOfChat = typeOfChat;
        this.userId = userId;
    }


    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getConversationId() {
        return conversationId;
    }

    public int getTypeOfChat() {
        return typeOfChat;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            SearchList conversation = (SearchList) object;
            if (this.conversationId.equals(conversation.getConversationId())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.conversationId.hashCode();
        return hash;
    }
}
