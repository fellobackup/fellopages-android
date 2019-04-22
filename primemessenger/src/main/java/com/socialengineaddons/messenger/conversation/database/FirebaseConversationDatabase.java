package com.socialengineaddons.messenger.conversation.database;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.conversation.data_model.Message;
import com.socialengineaddons.messenger.listners.FirebaseObservableListeners;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;

import rx.Observable;
import rx.functions.Func1;

public class FirebaseConversationDatabase implements ConversationDatabase {

    private final DatabaseReference chatMembersDb, chatMessagesDb;
    private final FirebaseObservableListeners firebaseObservableListeners;

    public FirebaseConversationDatabase(MessengerDatabaseUtils messengerDatabaseUtils, FirebaseObservableListeners firebaseObservableListeners) {
        chatMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        chatMessagesDb = messengerDatabaseUtils.getChatMessagesDatabase();
        this.firebaseObservableListeners = firebaseObservableListeners;
    }


    @Override
    public Observable<Boolean> observeTyping(String chatRoomId, String sender) {
        if(chatRoomId != null){
            return firebaseObservableListeners.listenToValueEvents(chatMembersDb.child(chatRoomId).child(sender).
                    child(Constants.FIREBASE_CHAT_TYPING), asBoolean());
        }
        return null;
    }

    @Override
    public void setTyping(String chatRoomId, String sender, String value) {
        if(chatRoomId != null){
            chatMembersDb.child(chatRoomId).child(sender).child(Constants.FIREBASE_CHAT_TYPING).setValue(value);
        }
    }

    @Override
    public Observable<Message> observeMessages(String chatRoomId) {
        return firebaseObservableListeners.listenToAddChildEvents(chatMessagesDb.child(chatRoomId), toMessage());
    }

    private Func1<DataSnapshot,Boolean> asBoolean() {
        return new Func1<DataSnapshot, Boolean>() {
            @Override
            public Boolean call(DataSnapshot dataSnapshot) {
                return dataSnapshot.exists() && !dataSnapshot.getValue(String.class).equals("0");
            }
        };
    }

    private Func1<DataSnapshot, Message> toMessage() {
        return new Func1<DataSnapshot, Message>() {
            @Override
            public Message call(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setMessageId(dataSnapshot.getKey());
                return message;
            }
        };
    }
}
