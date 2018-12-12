package com.socialengineaddons.messenger.conversation.database;

import com.socialengineaddons.messenger.conversation.data_model.Message;

import rx.Observable;

public interface ConversationDatabase {


    Observable<Boolean> observeTyping(String chatRoomId, String self);

    void setTyping(String chatRoomId, String self, String value);

    Observable<Message> observeMessages(String chatRoomId);

}
