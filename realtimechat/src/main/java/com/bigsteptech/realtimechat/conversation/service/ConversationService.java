package com.bigsteptech.realtimechat.conversation.service;


import com.bigsteptech.realtimechat.conversation.data_model.Message;

import rx.Observable;

/**
 * Created by marco on 29/07/16.
 */

public interface ConversationService {

    Observable<Boolean> getTyping(String chatRoomId, String sender);

    void setTyping(String chatRoomId, String sender, String value);

    Observable<Message> getMessages(String chatRoomId);



}
