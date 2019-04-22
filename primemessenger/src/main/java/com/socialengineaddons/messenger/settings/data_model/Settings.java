package com.socialengineaddons.messenger.settings.data_model;


public class Settings {

    String settingName, settingTitle, language;
    int onlineStatus, notificationsEnabled;

    public Settings(String settingName, String settingTitle, int onlineStatus, int notificationEnabled, String language) {
        this.settingName = settingName;
        this.settingTitle = settingTitle;
        this.onlineStatus = onlineStatus;
        this.notificationsEnabled = notificationEnabled;
        this.language = language;
    }

    public String getSettingName() {
        return settingName;
    }

    public String getSettingTitle() {
        return settingTitle;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public int isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public String getLanguage() {
        return language;
    }
}
