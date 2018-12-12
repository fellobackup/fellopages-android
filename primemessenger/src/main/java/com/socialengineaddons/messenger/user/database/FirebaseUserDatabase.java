package com.socialengineaddons.messenger.user.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.listners.FirebaseObservableListeners;
import com.socialengineaddons.messenger.user.User;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;


/**
 * Created by marco on 27/07/16.
 */

public class FirebaseUserDatabase implements UserDatabase {

    private final DatabaseReference usersDB;
    private final FirebaseObservableListeners firebaseObservableListeners;


    public FirebaseUserDatabase(FirebaseDatabase firebaseDatabase, FirebaseObservableListeners firebaseObservableListeners) {
        usersDB = firebaseDatabase.getReference(Constants.FIREBASE_USERS);
        this.firebaseObservableListeners = firebaseObservableListeners;
    }


    @Override
    public Observable<User> observeUser(String userId) {
        return firebaseObservableListeners.listenToValueEvents(usersDB.child(userId), as(User.class));
    }

    @Override
    public Observable<Boolean> initUserOnlineStatus() {
        DatabaseReference amOnline = usersDB.getParent().child(".info").child("connected");
        return firebaseObservableListeners.listenToValueEvents(amOnline, onlineStatusHandler());
    }

    @Override
    public void setUserOnlineStatus(final String userId) {
        final DatabaseReference usersRef = usersDB.child(userId);
        final int[] visibilityStatus = new int[1];
        final Map<String, Object> userConnectedValues = new HashMap<>();
        final Map<String, Object> userDisConnectedValues = new HashMap<>();
        userConnectedValues.put("isOnline", 1);
        userDisConnectedValues.put("isOnline", 0);


        usersRef.child("visibility").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    visibilityStatus[0] = dataSnapshot.getValue(Integer.class);

                    if(visibilityStatus[0] == 1){
                        userConnectedValues.put("lastSeen", 0);
                        userDisConnectedValues.put("lastSeen", ServerValue.TIMESTAMP);
                    }

                    usersRef.updateChildren(userConnectedValues);
                    usersRef.onDisconnect().updateChildren(userDisConnectedValues);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                usersRef.onDisconnect().updateChildren(userDisConnectedValues);
            }
        });

    }

    private <T> Func1<DataSnapshot, T> as(final Class<T> tClass) {
        return new Func1<DataSnapshot, T>() {
            @Override
            public T call(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    return dataSnapshot.getValue(tClass);
                } else {
                    return null;
                }
            }
        };
    }

    private Func1<DataSnapshot, Boolean> onlineStatusHandler() {
        return new Func1<DataSnapshot, Boolean>() {
            @Override
            public Boolean call(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(Boolean.class);
            }
        };
    }
}