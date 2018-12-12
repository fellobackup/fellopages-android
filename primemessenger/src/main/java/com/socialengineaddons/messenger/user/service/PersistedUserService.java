package com.socialengineaddons.messenger.user.service;


import android.util.Log;

import com.socialengineaddons.messenger.user.User;
import com.socialengineaddons.messenger.user.database.UserDatabase;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by marco on 31/07/16.
 */

public class PersistedUserService implements UserService {

    private final UserDatabase userDatabase;

    public PersistedUserService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    @Override
    public Observable<User> getUser(String userId) {
        return userDatabase.observeUser(userId);
    }

    @Override
    public void initOnlineStatus(final String userId) {
        userDatabase.initUserOnlineStatus()
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean result) {
                        Log.d(PersistedUserService.class.getSimpleName(), "Connected =" + result);
                        if (result.equals(Boolean.TRUE))
                            userDatabase.setUserOnlineStatus(userId);
                    }
                });
    }


}
