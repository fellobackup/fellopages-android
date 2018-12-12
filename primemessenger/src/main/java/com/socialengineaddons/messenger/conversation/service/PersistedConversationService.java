package com.socialengineaddons.messenger.conversation.service;


import com.socialengineaddons.messenger.conversation.data_model.Message;
import com.socialengineaddons.messenger.conversation.database.ConversationDatabase;

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
}
