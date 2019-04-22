package com.bigsteptech.realtimechat.conversation.database;


import com.bigsteptech.realtimechat.conversation.data_model.Message;

import rx.Observable;

/**
 * Created by marco on 29/07/16.
 */

public interface ConversationDatabase {


    Observable<Boolean> observeTyping(String chatRoomId, String self);

    void setTyping(String chatRoomId, String self, String value);

    Observable<Message> observeMessages(String chatRoomId);

}
