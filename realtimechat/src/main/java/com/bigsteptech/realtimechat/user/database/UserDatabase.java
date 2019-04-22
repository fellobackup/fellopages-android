package com.bigsteptech.realtimechat.user.database;


import com.bigsteptech.realtimechat.user.User;
import com.bigsteptech.realtimechat.user.Users;

import rx.Observable;


/**
 * Created by marco on 27/07/16.
 */

public interface UserDatabase {

    Observable<User> observeUser(String userId);

    Observable<Boolean>  initUserOnlineStatus();

    void setUserOnlineStatus(String userId);

}
