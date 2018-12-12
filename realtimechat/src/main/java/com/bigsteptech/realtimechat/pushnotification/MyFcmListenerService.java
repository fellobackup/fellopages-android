/*
* Copyright (c) 2016 BigStep Technologies Private Limited.
*
* You may not use this file except in compliance with the
* SocialEngineAddOns License Agreement.
* You may obtain a copy of the License at:
* https://www.socialengineaddons.com/android-app-license
* The full copyright and license information is also mentioned
* in the LICENSE file that was distributed with this
* source code.
*
*/
package com.bigsteptech.realtimechat.pushnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.conversation.ConversationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


public class MyFcmListenerService extends FirebaseMessagingService {
    public static int counter = 1;
    private String chatRoomId, messageId;
    public static NotificationManager notificationManager;
    public static Map<Integer,String> map = new TreeMap<>(Collections.reverseOrder());
    public static ArrayList<String> chatRoomIdsList = new ArrayList<>();
    private DatabaseReference chatRoomDb, chatMessagesDb, userDb;
    String title, message, typeOfMessage;
    private int typeOfChat;
    private int id = 1;


    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

//        Log.d(MyFcmListenerService.class.getSimpleName(), "onMessageReceived called..");

//        Map<String, String> data = remoteMessage.getData();
//        chatRoomId = data.get("chatKey");
//        messageId = data.get("messageKey");
//
//        if(!chatRoomIdsList.contains(chatRoomId)){
//            chatRoomIdsList.add(chatRoomId);
//        }
//
//        if(chatRoomIdsList.size() > 1){
//            title = getApplicationContext().getResources().getString(R.string.app_name);
//            message = String.format(getApplicationContext().getResources().getString
//                    (R.string.total_push_notification), counter, chatRoomIdsList.size());
//            generateCustomNotification(getApplicationContext(), message, title);
//        } else {
//            getMessage();
//        }
    }

    private void getMessage() {

        userDb = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS);
        chatRoomDb = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHATROOMS);
        chatMessagesDb = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_MESSAGES);

        chatRoomDb.child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String isGroup = dataSnapshot.hasChild("isGroup") ? dataSnapshot.child("isGroup").getValue(String.class) : "0";
                typeOfChat = isGroup.equals("0") ? 0 : 1;

                chatMessagesDb.child(chatRoomId).child(messageId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        typeOfMessage = dataSnapshot.child("attachmentType").getValue(String.class);

                        if(counter > 1){
                            message = counter + " new messages";
                        } else {
                            switch (typeOfMessage) {

                                case "text":
                                    message = dataSnapshot.child("body").getValue(String.class);
                                    break;

                                case "image":
                                    message = "\uf030" + " " + "Photo";
                                    break;

                                case "video":
                                    message = "\uf03d" + " " + "Video";
                                    break;

                                case "audio":
                                    message = "\uF001" + " " + "Audio";
                                    break;

                                case "file":
                                    message = "\uF15C" + " " + "Document";
                                    break;
                            }
                        }

                        if(isGroup.equals("1")){
                            title = dataSnapshot.child("title").getValue(String.class);
                            generateCustomNotification(getApplicationContext(), message, title);
                        } else {
                            String ownerId = dataSnapshot.child("ownerId").getValue(String.class);
                            userDb.child(ownerId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    title = dataSnapshot.getValue(String.class);
                                    generateCustomNotification(getApplicationContext(), message, title);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Create and show a simple notification containing the received FCM message and title.
     * @param context Context of application.
     * @param message FCM message received.
     * @param title Title of FCM notification in case of single notification.
     */
    private void generateCustomNotification(Context context, String message, String title) {

        Log.d(MyFcmListenerService.class.getSimpleName(), " Message = " + message + " Title =" + title);

        try{
            notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            map.put(counter, message);
            long when = System.currentTimeMillis();
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            inboxStyle.addLine( message);
            Intent notificationIntent = new Intent(context, ConversationActivity.class);
            notificationIntent.putExtra("chatRoomId", chatRoomId);
            notificationIntent.putExtra("typeOfChat", typeOfChat);
//            notificationIntent.putExtra(Constants.SENDER, selfUid);
//            notificationIntent.putExtra(Constants.DESTINATION, conversation.getToId());
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK );
            PendingIntent intent = PendingIntent.getActivity(context, 0 /* Request code */,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this)
                    .setWhen(when)
                    .setCategory(Notification.CATEGORY_PROMO)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_group_add_black_24dp)
                    .setContentText(message)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVisibility(Notification.VISIBILITY_PRIVATE)
                    .setContentIntent(intent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                    .setStyle(counter > 1 ? inboxStyle : null)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
            counter++;
            id++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to clear push notification when clicking on it.
     */
    public static void clearPushNotification() {
        if(notificationManager != null && map != null) {
            notificationManager.cancelAll();
            map.clear();
            counter = 1;
        }
    }

}
