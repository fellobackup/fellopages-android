package com.socialengineaddons.messenger.conversation.data_model;

public class AttachmentItems {

    private String title, key;
    private int iconResource;

    public AttachmentItems(String key, int icon, String title) {
        this.key = key;
        this.iconResource = icon;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public int getIcon() {
        return iconResource;
    }
}
