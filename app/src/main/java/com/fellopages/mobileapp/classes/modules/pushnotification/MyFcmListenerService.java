/*
* Copyright (c) 2016 BigStep Technologies Private Limited.
*
* You may not use this file except in compliance with the
* SocialEngineAddOns License Agreement.
* You may obtain a copy of the License at:
* https://www.socialengineaddons.com/android-app-license
* The full copyright and license information is also mentioned
* in the LICENSE file that was distributed with this
* source code.
*
*/
package com.fellopages.mobileapp.classes.modules.pushnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class MyFcmListenerService extends FirebaseMessagingService {
    public static int counter = 1;
    private String message;
    private JSONObject jsonObject,objectParamsJsonObject;
    int id, listingTypeId, albumId;
    private String type, imageUrl, title, viewUrl, sound;
    public static NotificationManager notificationManager;
    public static Map<Integer,String> map = new TreeMap<>(Collections.reverseOrder());
    NotificationCompat.InboxStyle inboxStyle;


    /**
     * Called when fcm token is refreshed
     */
    @Override
    public void onNewToken(String token) {
        registerRefreshedToken(token);
        super.onNewToken(token);
    }

    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        String bundleMessage = (String) data.get("message");
        title = (String) data.get("title");
        imageUrl = (String) data.get("imgUrl");
        viewUrl = (String) data.get("href");
        sound = (String) data.get("sound");
        try {
            jsonObject = new JSONObject(bundleMessage);
            message = jsonObject.optString("feed_title");
            objectParamsJsonObject = jsonObject.optJSONObject("object_params");
            id = objectParamsJsonObject.getInt("id");
            type = objectParamsJsonObject.getString("type");
            if (type != null && !type.isEmpty()) {
                switch (type) {
                    case ConstantVariables.MLT_MENU_TITLE:
                    case ConstantVariables.MLT_REVIEW_MENU_TITLE:
                        listingTypeId = objectParamsJsonObject.optInt("listingtype_id");
                        break;

                    case ConstantVariables.ALBUM_PHOTO_MENU_TITLE:
                        albumId = objectParamsJsonObject.optInt("album_id");
                        break;

                    case "activity_comment":
                        id = objectParamsJsonObject.optInt("resource_id");
                        break;
                }
            }

        } catch (JSONException e) {
            jsonObject = null;
            e.printStackTrace();
        }
        if (jsonObject == null) {
            message = bundleMessage;
        }
        generateCustomNotification(getApplicationContext(), message, title);
    }

    /**
     * Create and show a simple notification containing the received FCM message and title.
     * @param context Context of application.
     * @param message FCM message received.
     * @param title Title of FCM notification in case of single notification.
     */
    private void generateCustomNotification(Context context, String message, String title) {
        try{
            Intent broadCastInent = new Intent();
            broadCastInent.setAction(ConstantVariables.ACTION_COUNTER_UPDATE);
            context.sendBroadcast(broadCastInent);

            notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            map.put(counter, message);
            int icon = R.drawable.push_noti_icon;
            long when = System.currentTimeMillis();
            Bitmap bitmapLargeIcon;
            bitmapLargeIcon = BitmapUtils.getBitmapFromURL(imageUrl);

            if (inboxStyle == null) {
                inboxStyle = new NotificationCompat.InboxStyle();
            }
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                String value = entry.getValue();
                inboxStyle.addLine( value);
            }
            String summaryText = getApplicationContext().getResources().
                    getQuantityString(R.plurals.notification_count,
                            counter);
            inboxStyle.setSummaryText(String.format(getApplicationContext().getResources().getString
                    (R.string.total_push_notification), counter, summaryText));
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra("id", id);
            notificationIntent.putExtra("type", (type != null ? type: ""));
            notificationIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, listingTypeId);
            notificationIntent.putExtra(ConstantVariables.ALBUM_ID, albumId);
            notificationIntent.putExtra("notification_view_url", (viewUrl != null ? viewUrl: ""));
            notificationIntent.putExtra("headerTitle", title);
            notificationIntent.putExtra("is_single_notification", counter);
            notificationIntent.putExtra("message", message);
            notificationIntent.putExtra("objectParams", (objectParamsJsonObject != null ? objectParamsJsonObject.toString() : ""));

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            PendingIntent intent = PendingIntent.getActivity(context, 0 /* Request code */,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "channel_id_" + id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mBuilder.setChannelId("channel_id_" + id);
                NotificationChannel channel = new NotificationChannel(
                        "channel_id_" + id,
                        title,
                        NotificationManager.IMPORTANCE_HIGH
                );
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
            Notification notification = mBuilder
                    .setWhen(when)
                    .setCategory(Notification.CATEGORY_PROMO)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(ContextCompat.getColor(this, R.color.themeButtonColor))
                    .setSmallIcon(icon)
                    .setSound(sound.equals("1") ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) : null)
                    .setLargeIcon(bitmapLargeIcon)
                    .setVisibility(Notification.VISIBILITY_PRIVATE)
                    .setContentIntent(intent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                    .setStyle(counter > 1 ? inboxStyle : null)
                    .build();
            if (counter > 1) {
                notificationManager.cancelAll();
            }
            notificationManager.notify(id, notification);
            counter++;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to clear push notification when clicking on it.
     */
    public static void clearPushNotification() {
        if(notificationManager != null && map != null) {
            notificationManager.cancelAll();
            map.clear();
            counter = 1;
        }
    }

    /**
     * Method to Update FCM token on server when the token is refreshed.
     *
     * @param token Updated FCM token.
     */
    public void registerRefreshedToken(final String token) {
        AppConstant appConst = new AppConstant(getApplicationContext(), false);
        if (!appConst.isLoggedOutUser() && PreferencesUtils.getUserDetail(getApplicationContext()) != null) {
            Map<String, String> postParams = new HashMap<>();
            try {
                JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(getApplicationContext()));
                postParams.put("user_id", userDetail.optString("user_id"));
                postParams.put("registration_id", token);
                postParams.put("device_uuid", appConst.getDeviceUUID());
                appConst.postJsonRequest(UrlUtil.UPDATE_FCM_TOKEN_URL, postParams);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
