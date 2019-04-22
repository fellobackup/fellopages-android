/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.bigsteptech.realtimechat.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.MessengerActivity;
import com.bigsteptech.realtimechat.interfaces.OnCacheCreateListener;
import com.bigsteptech.realtimechat.interfaces.OnNewMessageReceivedListener;
import com.bigsteptech.realtimechat.interfaces.OnPushNotificationClearListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.himanshuvirmani.androidcache.CacheManager;

import java.io.IOException;

public class MessengerDatabaseUtils {

    private FirebaseDatabase messengerDatabase;
    private OnNewMessageReceivedListener onNewMessageReceivedListener;
    private OnPushNotificationClearListener onPushNotificationClearListener;
    private static MessengerDatabaseUtils messengerInstance;
    private OnCacheCreateListener onCacheCreateListener;
    private FirebaseApp messengerApp;


    public MessengerDatabaseUtils() {}

    public static MessengerDatabaseUtils getInstance(){
        if(messengerInstance == null){
            messengerInstance = new MessengerDatabaseUtils();
        }
        return messengerInstance;
    }

    private String getDeviceUUID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    // Set The NewMessage Received Listeners
    public void setOnNewMessageReceivedListener(OnNewMessageReceivedListener newMessageReceivedListener) {
        this.onNewMessageReceivedListener = newMessageReceivedListener;
    }

    // Set The NewMessage Received Listeners
    public void setPushNotificationClearListener(OnPushNotificationClearListener pushNotificationClearListener) {
        this.onPushNotificationClearListener = pushNotificationClearListener;
    }

    // Set The OnCache Create Listeners
    public void setOnCacheCreatedListener(OnCacheCreateListener cacheCreatedListener) {
        this.onCacheCreateListener = cacheCreatedListener;
    }

    public CacheManager getCacheManagerInstance() {
        if(onCacheCreateListener != null) {
            return onCacheCreateListener.getCacheManager();
        }
        return null;
    }

    public void pushNotificationsClicked() {
        if(onPushNotificationClearListener != null) {
            onPushNotificationClearListener.clearPushNotifications();
        }
    }


    public void initializeMessengerApp(Context context) {

        /**
         * Connect to the Firebase Messenger database
         */
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey(Constants.MESSENGER_API_KEY)
                .setApplicationId(Constants.MESSENGER_APPLICATION_ID)
                .setDatabaseUrl(Constants.MESSENGER_DATABASE_URL)
                .setStorageBucket(Constants.MESSENGER_STORAGE_BUCKET)
                .build();
        messengerApp = FirebaseApp.initializeApp(context, options, "messenger");
        messengerDatabase = FirebaseDatabase.getInstance(messengerApp);
    }

    public void loginInWithEmailAndPassword(final Context context, String email, String password,
                                            final boolean openMessengerActivity, final int requestCode) {

        FirebaseAuth.getInstance(messengerApp).signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser firebaseUser = task.getResult().getUser();
                            getUsersDatabase().child(firebaseUser.getUid()).child("isOnline").setValue(1);
                            if(onNewMessageReceivedListener != null){
                                onNewMessageReceivedListener.getNewMessageCount(firebaseUser.getUid());
                            }

                            new Thread(new Runnable() {
                                public void run() {
                                    //code
                                    String registrationToken = null;
                                    try {
                                        registrationToken = FirebaseInstanceId.getInstance(messengerApp).
                                                getToken(Constants.MESSENGER_SENDER_ID, "FCM");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    setToken(registrationToken, firebaseUser.getUid(), context);
                                }
                            }).start();


                            if(openMessengerActivity) {
                                Intent intent = new Intent(context, MessengerActivity.class);
                                if(requestCode != 0){
                                    ((Activity)context).startActivityForResult(intent, requestCode);
                                } else {
                                    context.startActivity(intent);
                                }
                            }
                        }
                    }
        });
    }

    private void setToken(String token, String currentUserId, Context context) {

        getFcmTokenDatabase().child(currentUserId).child(getDeviceUUID(context)).child(token).setValue("true");
    }

    public void deleteToken(Context context) {

        if(getCurrentUserId() != null) {
            getFcmTokenDatabase().child(getCurrentUserId()).child(getDeviceUUID(context)).removeValue();
        }
    }

    public FirebaseDatabase getMessengerDatabaseInstance() {
        return messengerDatabase;
    }

    public DatabaseReference getUsersDatabase () {

        return messengerDatabase.getReference(Constants.FIREBASE_USERS);
    }

    public DatabaseReference getChatsDatabase () {

        return messengerDatabase.getReference(Constants.FIREBASE_CHATROOMS);
    }

    public DatabaseReference getChatMessagesDatabase () {

        return messengerDatabase.getReference(Constants.FIREBASE_MESSAGES);
    }

    public DatabaseReference getChatMembersDatabase () {

        return messengerDatabase.getReference(Constants.FIREBASE_CHATROOMS_MEMBERS);
    }

    public DatabaseReference getBlockedListDatabase () {

        return messengerDatabase.getReference(Constants.BLOCK_LIST_DB);
    }

    public DatabaseReference getFriendsDatabase () {

        return messengerDatabase.getReference(Constants.FIREBASE_FRIENDS_DB);
    }

    public DatabaseReference getDuplicateChatsDatabase () {

        return messengerDatabase.getReference(Constants.FIREBASE_DUPLICATE_CHATROOMS);
    }

    public DatabaseReference getFilesDatabase () {

        return messengerDatabase.getReference(Constants.FIREBASE_FILES_DB);
    }

    public DatabaseReference getFcmTokenDatabase () {

        return messengerDatabase.getReference(Constants.FCM_TOKEN);
    }

    public StorageReference getStorageRef() {

        return FirebaseStorage.getInstance(messengerApp).getReference();
    }

    public String getCurrentUserId() {

        FirebaseUser currentLoggedInUser = FirebaseAuth.getInstance(messengerApp).getCurrentUser();
        if( currentLoggedInUser != null){
            return currentLoggedInUser.getUid();
        } else {
            return null;
        }
    }

    public int getUnReadMessageCount(final String currentUserId) {

        getChatMembersDatabase().orderByChild(currentUserId + "/newMessageCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int unReadMsgCount = 0;
                int i = 0;

                for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){

                    i++;
                    if(childDataSnapshot.child(currentUserId).hasChild("newMessageCount")){

                        unReadMsgCount += childDataSnapshot.child(currentUserId).child("newMessageCount").
                                                getValue(Integer.class);

                        if(i == dataSnapshot.getChildrenCount() && onNewMessageReceivedListener != null){
                            onNewMessageReceivedListener.updateNewMessageCount(unReadMsgCount);
                        }
                    }
                }

                if(unReadMsgCount == 0 &&  onNewMessageReceivedListener != null){
                    onNewMessageReceivedListener.updateNewMessageCount(unReadMsgCount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return 0;
    }

    public void logoutUser() {
        FirebaseAuth.getInstance(messengerApp).signOut();
    }

}
