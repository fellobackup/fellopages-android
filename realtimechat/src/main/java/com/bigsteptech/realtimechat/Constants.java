package com.bigsteptech.realtimechat;


import android.os.Handler;

public class Constants {

    // this will be sender id of the project
    public static final String MESSENGER_SENDER_ID = "361411816178";

    // This will be the api_key in google-services.json file
    public static final String MESSENGER_API_KEY = "AIzaSyBwxlxJTqnDxzJ_zlRnkyUkUmKGsW66ZTY";

    // This will be the mobilesdk_app_id in google-services.json file
    public static final String MESSENGER_APPLICATION_ID = "1:361411816178:android:df202d650ad970a3";

    // This will be the firebase_url in google-services.json file
    public static final String MESSENGER_DATABASE_URL = "https://devadonsdevelopment.firebaseio.com";

    // This will be the firebase_url in google-services.json file
    public static final String MESSENGER_STORAGE_BUCKET = "devadonsdevelopment.appspot.com";


    // Firebase Database Names
    public static final String FIREBASE_USERS = "users";
    public static final String FIREBASE_MESSAGES = "messages";
    public static final String FIREBASE_CHATROOMS = "chats";
    public static final String FIREBASE_CHATROOMS_MEMBERS = "members";
    public static final String BLOCK_LIST_DB = "blockList";
    public static final String FIREBASE_FRIENDS_DB = "friends";
    public static final String FIREBASE_DUPLICATE_CHATROOMS = "duplicateChats";
    public static final String FIREBASE_FILES_DB = "files";
    public static final String FCM_TOKEN = "fcmTokens";

    public static final String FIREBASE_IMAGE_KEY = "profileImageUrl";

    public static final String FIREBASE_USERS_NAME = "name";
    public static final String FIREBASE_USERS_UID = "uid";
    public static final String FIREBASE_USERS_EMAIL = "email";
    public static final String FIREBASE_USERS_LASTSEEN = "lastSeen";
    public static final String FIREBASE_USERS_ISONLINE = "isOnline";

    public static final String FIREBASE_CHAT_TYPING = "isTyping";
    public static final String FIREBASE_RESOURCE_TYPE = "resourceType";

    public static final String SENDER ="sender";
    public static final String DESTINATION ="destination";
    public static final String USER_TITLE = "userTitle";
    public static final String LAST_SEEN = "lastSeen";

    public static final int READ_EXTERNAL_STORAGE = 1;
    public static final int PERMISSION_CAMERA = 2;
    public static final int REQUEST_IMAGE = 10;
    public static final int UPLOAD_IMAGE = 11;
    public static final int REQUEST_VIDEO = 12;
    public static final int REQUEST_AUDIO = 13;
    public static final int INPUT_FILE_REQUEST_CODE = 14;
    public static final int GROUP_INFO_REQUEST = 15;
    public static final int OPEN_MESSENGER_REQUEST = 39;
    public static Handler PROGRESSBAR_HANDLER;
    public static final int CONTACTS_LIMIT = 500;


}
