package com.uniprogy.outquiz.notifications;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.uniprogy.outquiz.helpers.Misc;

public class UniFirebaseInstanceIDService  extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Misc.saveToken(refreshedToken);
    }

}
