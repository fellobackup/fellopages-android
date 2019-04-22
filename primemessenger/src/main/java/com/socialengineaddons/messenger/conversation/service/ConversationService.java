package com.socialengineaddons.messenger.conversation.service;


import com.socialengineaddons.messenger.conversation.data_model.Message;

import rx.Observable;

public interface ConversationService {

    Observable<Boolean> getTyping(String chatRoomId, String sender);

    void setTyping(String chatRoomId, String sender, String value);
}
