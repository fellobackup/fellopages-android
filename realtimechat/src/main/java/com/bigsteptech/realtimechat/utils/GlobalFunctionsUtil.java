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


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.conversation.data_model.Message;
import com.bigsteptech.realtimechat.interfaces.OnDeleteClearChat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.himanshuvirmani.androidcache.CacheManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalFunctionsUtil {

    private static ProgressDialog progressDialog;
    private static  CacheManager cacheManager  = MessengerDatabaseUtils.getInstance().getCacheManagerInstance();
    private static OnDeleteClearChat mOnDeleteClearChat;

    public static void setmOnDeleteClearChat(OnDeleteClearChat onDeleteClearChat) {
        mOnDeleteClearChat = onDeleteClearChat;
    }

    public static void deleteConversation (final Context context, final String conversationId, final DatabaseReference chatMessageDb,
                                           final String selfUid, final DatabaseReference chatMembersDb, final int typeOfChat) {

        Log.d(GlobalFunctionsUtil.class.getSimpleName(), " conversationId : " + conversationId);

        if(conversationId != null) {

            // Block the user
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

            alertBuilder.setMessage(context.getResources().getString(R.string.delete_conversation_alert));
            alertBuilder.setTitle(context.getResources().getString(R.string.delete_conversation));

            alertBuilder.setPositiveButton(context.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    showProgressDialog(context, context.getResources().getString(R.string.delete_conversation_message));
                    chatMessageDb.child(conversationId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int i = 0;
                            if(dataSnapshot.getChildrenCount() > 0) {
                                for(DataSnapshot message : dataSnapshot.getChildren()) {
                                    DataSnapshot recipients = message.child("recipients");
                                    i++;
                                    if(recipients.getChildrenCount() == 1) {
                                        chatMessageDb.child(conversationId).child(message.getKey()).removeValue();
                                    } else if(recipients.hasChild(selfUid)) {
                                        chatMessageDb.child(conversationId).child(message.getKey()).child("recipients").child(selfUid).removeValue();
                                    }

                                    if(i == dataSnapshot.getChildrenCount()) {

                                        //Delete the lastMessageId from members table
                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put("deleted", "1");
                                        ArrayList<Message> messages = new ArrayList<>();
                                        Message listMessage = new Message(messages);
                                        cacheManager.put("messages_" + conversationId, listMessage);
                                        chatMembersDb.child(conversationId).child(selfUid).updateChildren(childUpdates);
                                        if(mOnDeleteClearChat != null ) {
                                            mOnDeleteClearChat.deleteChat();
                                        }
                                        hideProgressDialog();
                                    }
                                }
                            } else {
                                if(mOnDeleteClearChat != null ) {
                                    mOnDeleteClearChat.deleteChat();
                                }
                                hideProgressDialog();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            alertBuilder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertBuilder.create().show();
        }
    }

    public static void clearConversation (final Context context, final String conversationId, final DatabaseReference chatMessageDb,
                                           final String selfUid, final DatabaseReference chatMembersDb, final int typeOfChat) {

        Log.d(GlobalFunctionsUtil.class.getSimpleName(), " conversationId : " + conversationId);

        if(conversationId != null) {

            // Block the user
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

            alertBuilder.setMessage(context.getResources().getString(R.string.clear_conversation_alert));
            alertBuilder.setTitle(context.getResources().getString(R.string.clear_conversation));

            alertBuilder.setPositiveButton(context.getResources().getString(R.string.clear), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    showProgressDialog(context, context.getResources().getString(R.string.clear_conversation_message));
                    chatMessageDb.child(conversationId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int i = 0;
                            if(dataSnapshot.getChildrenCount() > 0) {
                                for(DataSnapshot message : dataSnapshot.getChildren()) {
                                    DataSnapshot recipients = message.child("recipients");
                                    i++;
                                    if(recipients.getChildrenCount() == 1) {
                                        chatMessageDb.child(conversationId).child(message.getKey()).removeValue();
                                    } else if(recipients.hasChild(selfUid)) {
                                        chatMessageDb.child(conversationId).child(message.getKey()).child("recipients").child(selfUid).removeValue();
                                    }

                                    if(i == dataSnapshot.getChildrenCount()) {

                                        //Delete the lastMessageId from members table
                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put("lastMessageId", "false");
                                        childUpdates.put("deleted", "0");
                                        ArrayList<Message> messages = new ArrayList<>();
                                        Message listMessage = new Message(messages);
                                        cacheManager.put("messages_" + conversationId, listMessage);
                                        chatMembersDb.child(conversationId).child(selfUid).updateChildren(childUpdates);
                                        if(mOnDeleteClearChat != null ) {
                                            mOnDeleteClearChat.clearChat();
                                        }
                                        hideProgressDialog();
                                    }
                                }
                            } else {
                                hideProgressDialog();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            alertBuilder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertBuilder.create().show();
        }
    }


    private static  void showProgressDialog(Context context, String message) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message + "…");
        progressDialog.setCancelable(false);

        try {
            if (!progressDialog.isShowing())
                progressDialog.show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
