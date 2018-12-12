package com.socialengineaddons.messenger.search;


import android.graphics.Color;

import com.socialengineaddons.messenger.R;

import java.util.Random;

public class SearchList {

    private String title, imagePath, conversationId, userId;
    private int typeOfChat, profileColor;

    public SearchList(String title, String imagePath, String conversationId, int typeOfChat,
                      String userId, int profileColor) {
        this.title = title;
        this.imagePath = imagePath;
        this.conversationId = conversationId;
        this.typeOfChat = typeOfChat;
        this.userId = userId;
        if (profileColor != 0) {
            this.profileColor = profileColor;
        } else {
            setProfileColor();
        }
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
