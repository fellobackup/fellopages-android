package com.bigsteptech.realtimechat.user.service;


import com.bigsteptech.realtimechat.user.User;
import com.bigsteptech.realtimechat.user.Users;

import rx.Observable;


/**
 * Created by marco on 31/07/16.
 */

public interface UserService {

    Observable<User> getUser(String userId);

    void initOnlineStatus(String userId);

}