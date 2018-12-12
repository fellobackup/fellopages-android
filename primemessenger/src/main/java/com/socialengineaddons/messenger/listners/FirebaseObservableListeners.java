package com.socialengineaddons.messenger.listners;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import rx.Observable;
import rx.functions.Func1;

public class FirebaseObservableListeners {

    public <T> Observable<T> listenToValueEvents(Query query, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToValueEventsOnSubscribe<>(query, marshaller));
    }

    public <T> Observable<T> listenToAddChildEvents(Query query, Func1<DataSnapshot, T> marshaller) {
        return Observable.create(new ListenToAddChildEventsOnSubscribe<T>(query, marshaller));
    }
}
