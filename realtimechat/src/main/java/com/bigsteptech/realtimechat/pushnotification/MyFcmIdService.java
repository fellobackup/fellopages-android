/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.bigsteptech.realtimechat.pushnotification;

import android.util.Log;

import com.bigsteptech.realtimechat.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFcmIdService extends FirebaseInstanceIdService {

    private DatabaseReference fcmTokens;

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        fcmTokens = FirebaseDatabase.getInstance().getReference(Constants.FCM_TOKEN);
//        registerRefreshedToken(token);
    }

    /**
     * Method to Update FCM token on server when the token is refreshed.
     *
     * @param token Updated FCM token.
     */
    public void registerRefreshedToken(final String token) {

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            final String self = FirebaseAuth.getInstance().getCurrentUser().getUid();
            fcmTokens.child(self).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fcmTokens.child(self).child("token" + dataSnapshot.getChildrenCount() + 1).setValue(token);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
