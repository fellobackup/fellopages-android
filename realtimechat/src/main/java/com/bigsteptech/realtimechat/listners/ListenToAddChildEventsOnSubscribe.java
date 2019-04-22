package com.bigsteptech.realtimechat.listners;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by marco on 13/09/16.
 */

class ListenToAddChildEventsOnSubscribe<T> implements Observable.OnSubscribe<T> {

    private final Query query;
    private final Func1<DataSnapshot, T> marshaller;

    ListenToAddChildEventsOnSubscribe(Query query, Func1<DataSnapshot, T> marshaller) {
        this.query = query;
        this.marshaller = marshaller;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        final ValueEventListener listener = query.addValueEventListener(new RxValueListener<>(subscriber, marshaller));
//        subscriber.add(Subscriptions.create(new Action0() {
//            @Override
//            public void call() {
////                query.removeEventListener(listener);
//            }
//        }));
    }

    private static class RxValueListener<T> implements ValueEventListener {

        private final Subscriber<? super T> subscriber;
        private final Func1<DataSnapshot, T> marshaller;
        private int counter = 0;

        RxValueListener(Subscriber<? super T> subscriber, Func1<DataSnapshot, T> marshaller) {
            this.subscriber = subscriber;
            this.marshaller = marshaller;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            counter = 0;
            Log.d(ListenToAddChildEventsOnSubscribe.class.getSimpleName(), "onDataChange called.." +
                    dataSnapshot.getChildrenCount() + "counter = " + counter);

            for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                Log.d(ListenToAddChildEventsOnSubscribe.class.getSimpleName(), "Key = " + snapshot.getKey());
                counter++;
//                if (!subscriber.isUnsubscribed()) {
                    Log.d(ListenToAddChildEventsOnSubscribe.class.getSimpleName(), " subscriber on" +
                            subscriber.isUnsubscribed()) ;
                subscriber.onNext(marshaller.call(snapshot));
//                    if(counter < dataSnapshot.getChildrenCount()){
//                        subscriber.onNext(marshaller.call(snapshot));
//                    } else {
//                        subscriber.onNext(marshaller.call(snapshot));
//                        subscriber.onCompleted();
//                    }
//                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
        }

    }

}
