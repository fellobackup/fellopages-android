package com.socialengineaddons.messenger.user.database;


import com.socialengineaddons.messenger.user.User;

import rx.Observable;


/**
 * Created by marco on 27/07/16.
 */

public interface UserDatabase {

    Observable<User> observeUser(String userId);

    Observable<Boolean>  initUserOnlineStatus();

    void setUserOnlineStatus(String userId);

}
