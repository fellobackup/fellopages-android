package com.bigsteptech.realtimechat.conversation.service;


import com.bigsteptech.realtimechat.conversation.data_model.Message;
import com.bigsteptech.realtimechat.conversation.database.ConversationDatabase;

import rx.Observable;

/**
 * Created by marco on 29/07/16.
 */

public class PersistedConversationService implements ConversationService {

    private final ConversationDatabase conversationDatabase;

    public PersistedConversationService(ConversationDatabase conversationDatabase) {
        this.conversationDatabase = conversationDatabase;
    }

    @Override
    public Observable<Boolean> getTyping(String chatRoomId, String destination) {
        return conversationDatabase.observeTyping(chatRoomId, destination);
    }

    @Override
    public void setTyping(String chatRoomId, String destination, String value) {
        conversationDatabase.setTyping(chatRoomId, destination, value);
    }

    @Override
    public Observable<Message> getMessages(String chatRoomId) {
        return conversationDatabase.observeMessages(chatRoomId);
    }
}
