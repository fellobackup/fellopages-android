package com.socialengineaddons.messenger.user.service;


import com.socialengineaddons.messenger.user.User;

import rx.Observable;


/**
 * Created by marco on 31/07/16.
 */

public interface UserService {

    Observable<User> getUser(String userId);

    void initOnlineStatus(String userId);

}