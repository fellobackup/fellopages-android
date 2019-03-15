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
 *
 */
package com.fellopages.mobileapp.classes.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.fellopages.mobileapp.BuildConfig;
import com.fellopages.mobileapp.classes.core.impl.LoginListener;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListnerFeeds;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.CacheUtils;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.google.android.gms.common.ConnectionResult;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AppConstant {
    private Context mContext;
    private String oauthToken, oauth_secret;
    public static Locale mLocale;
    private JSONObject mBody, mUserDetail;
    private boolean isRetryOption = false, mIsLoginSignUpRequest = false;
    private ProgressDialog pDialog;
    private Map<String, String> postParams, mAuthParams, mRequestParams;

    private int mStatusCode;
    private static final int REQUEST_TIMEOUT_MS = 10000;
    private static final float BACK_OF_MULTIPLIER = 2.0f;
    private static final int NO_OF_RETRY_ATTEMPTS = 3;

    // GridView image padding
    public static final int GRID_PADDING = 2; // in dp

    // Number of items shown per page
    // by default 20 but user can configure this
    public static final int LIMIT = 20;
    public static int NUM_OF_COLUMNS_FOR_VIEW_PAGE = 3;
    public static int NUM_OF_COLUMNS_FOR_PHOTO_GRID = 2;
    public static final int FEATURED_CONTENT_LIMIT = 5;

    // GridView image padding
    public static final int STICKERS_GRID_PADDING = 8; // in dp

    // Default url for data access - OLD
    public static final String DEFAULT_URL =
            BuildConfig.DEBUG ? "https://www.fellopages.com/beta1/api/rest/" : "https://www.fellopages.com/api/rest/";
//    public static final String DEFAULT_URL = "https://www.fellopages.com/api/rest/";
//    public static final String DEFAULT_URL = "https://www.fellopages.com/beta1/api/rest/";
    public static final String oauth_consumer_key = "tqrqueo5pxnae436nmrgeqhzs6jiud1n";
    public static final String oauth_consumer_secret = "dlixjfdviokbfk48mv1x0ir2u8v7o9xj";

    private static final String tag_json_obj = "json_obj_req";
    static boolean isLocationEnable = false;
    public static String mLocationType = "";
    public static int isDeviceLocationEnable = 0, isDeviceLocationChange = 1;

    private OnCommunityAdsLoadedListener mCommunityAdsLoadedListener;

    public AppConstant(Context context) {
        mContext = context;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getResources().getString(R.string.progress_dialog_wait) + "…");
        pDialog.setCancelable(false);
        initializeVariable();
    }

    public AppConstant(Context context, boolean isNeedProgressDialog) {
        mContext = context;

        if (isNeedProgressDialog) {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getResources().getString(R.string.progress_dialog_wait) + "…");
            pDialog.setCancelable(false);
        }

        initializeVariable();
    }

    /**
     * Method to initialize class member variables.
     */
    private void initializeVariable() {
        mAuthParams = new HashMap<>();
        mRequestParams = new HashMap<>();
        oauthToken = PreferencesUtils.getUserPreferences(mContext).getString("oauth_token", null);
        oauth_secret = PreferencesUtils.getUserPreferences(mContext).getString("oauth_secret", null);
        mAuthParams.put("oauth_consumer_key", oauth_consumer_key);
        mAuthParams.put("oauth_consumer_secret", oauth_consumer_secret);

        if (oauthToken != null) {
            mAuthParams.put("oauth_token", oauthToken);
            mAuthParams.put("oauth_secret", oauth_secret);
        }

        if (checkManifestPermission(android.Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            mRequestParams.put("device_id", tm.getDeviceId());
        }

        mRequestParams.put("language", PreferencesUtils.getCurrentLanguage(mContext));
        mRequestParams.put("_ANDROID_VERSION", PreferencesUtils.getCurrentAppVersion(mContext));
        mRequestParams.put("restapilocation", PreferencesUtils.getDefaultLocation(mContext));

        if (PreferencesUtils.getUserDetail(mContext) != null) {
            try {
                mUserDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                String locale = mUserDetail.optString("locale");
                if (locale.contains("_")) {
                    String localNameArray[] = locale.split("_");
                    mLocale = new Locale(localNameArray[0], localNameArray[1]);
                } else {
                    mLocale = new Locale(locale);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if user isn't logged in
     *
     * @param refresh true if recheck from shared preference
     * @return true if user isn't logged in, otherwise false
     */
    public boolean isLoggedOutUser(boolean refresh) {
        if (refresh) {
            return PreferencesUtils.getUserPreferences(mContext).getString("oauth_token", null) == null;
        } else {
            return isLoggedOutUser();
        }
    }

    public boolean isLoggedOutUser() {
        return oauthToken == null;
    }

    public static int getNumOfColumns(Context context) {
        if (context.getResources().getBoolean(R.bool.isTabletView)) {
            return 2;
        } else {
            return 1;
        }
    }

    public Map<String, String> getAuthenticationParams() {
        return mAuthParams;
    }

    public Map<String, String> getRequestParams() {
        return mRequestParams;
    }

    /**
     * Get request execution for getting the response from server.
     *
     * @param url              Url on which the request will be executed
     * @param responseListener Interface used to listen response events
     */
    public void getJsonResponseFromUrl(String url, final OnResponseListener responseListener) {
        manageResponse(url, Request.Method.GET, null, responseListener);
    }

    /**
     * Used to execute post request on server without looking for response.
     *
     * @param url    Url on which the request will be executed
     * @param params Parameters required for executing the post request on server
     */
    public void postJsonRequest(String url, Map<String, String> params) {
        manageResponse(url, Request.Method.POST, params, null);
    }

    /**
     * Used to execute post request on server without looking for response and params.
     *
     * @param url Url on which the request will be executed
     */
    public void postJsonRequest(String url) {
        manageResponse(url, Request.Method.POST, new HashMap<>(), null);
    }

    /**
     * Used to execute post request on server without params.
     *
     * @param url              Url on which the request will be executed
     * @param responseListener Interface used to listen response events
     */
    public void postJsonRequestWithoutParams(String url, final OnResponseListener responseListener) {
        manageResponse(url, Request.Method.POST, new HashMap<>(), responseListener);
    }

    /**
     * Used to execute post request on server..
     *
     * @param url              Url on which the request will be executed
     * @param params           Parameters required for executing the post request on server
     * @param responseListener Interface used to listen response events
     */
    public void postLoginSignUpRequest(String url, Map<String, String> params,
                                       final OnResponseListener responseListener) {
        mIsLoginSignUpRequest = true;
        manageResponse(url, Request.Method.POST, params, responseListener);
    }

    /**
     * Used to execute post request on server..
     *
     * @param url              Url on which the request will be executed
     * @param params           Parameters required for executing the post request on server
     * @param responseListener Interface used to listen response events
     */
    public void postJsonResponseForUrl(String url, Map<String, String> params,
                                       final OnResponseListener responseListener) {
        manageResponse(url, Request.Method.POST, params, responseListener);
    }

    /**
     * Used to execute delete request on server..
     *
     * @param params           parameters required for request execution
     * @param url              Url on which the request will be executed
     * @param responseListener Interface used to listen response events
     */
    public void deleteResponseForUrl(String url, Map<String, String> params,
                                     final OnResponseListener responseListener) {
        manageResponse(url, Request.Method.DELETE, params, responseListener);
    }

    /**
     * Used to update song tally count.
     *
     * @param params           Parameters required for executing the put request on server
     * @param url              Url on which the request will be executed
     * @param responseListener Interface used to listen response events
     */
    public void putResponseForUrl(String url, Map<String, String> params,
                                  final OnResponseListener responseListener) {
        manageResponse(url, Request.Method.PUT, params, responseListener);
    }

    /**
     * Method to manage response for GET, POST, DELETE, and PUT Request.
     *
     * @param url                Url of calling service.
     * @param method             Type of method.
     * @param params             Post Params.
     * @param onResponseListener Interface used to listen response events
     */
    private void manageResponse(String url, final int method, Map<String, String> params,
                                final OnResponseListener onResponseListener) {


        try {
            if (!url.contains("graph")) {
                url = buildQueryString(url, mAuthParams);

                // Don't send location for browse albums
                if (url.contains("rest/albums?")) {
                    mRequestParams.remove("restapilocation");
                }

                // Put Language Params, location params, and version params in Params
                url = buildQueryString(url, mRequestParams);

            }
            postParams = params;


            LogUtils.LOGD(AppConstant.class.getSimpleName(), "Request Url: " + url);
            Log.d("LikeAction ", params + " " + url);
            StringRequest request = new StringRequest(method, url, response -> {
                try {
                    if (response != null && !response.isEmpty()) {
                        LogUtils.LOGD(AppConstant.class.getSimpleName(), "Request Response: " + (method == Request.Method.GET ? "Successful" : response));
                        JSONObject json = new JSONObject(response);
                        Log.d("LoggedParamsAndUrl ", String.valueOf(json));
                        mStatusCode = json.optInt("status_code");
                        mBody = json.optJSONObject("body");
                        Log.d("JsonTag ", json.toString());
                        if (mStatusCode != 0 && isRequestSuccessful(mStatusCode) && mBody == null) {
                            JSONArray bodyJsonArray = json.optJSONArray("body");
                            mBody = convertToJsonObject(bodyJsonArray);
                            if (mBody != null && mBody.length() == 0 && json.optString("body") != null) {
                                mBody = json;
                            }
                        }

                        if (mStatusCode == 406) {
                            eraseUserDatabase();
                        } else if (isRequestSuccessful(mStatusCode) && onResponseListener != null) {
                            onResponseListener.onTaskCompleted(mBody);
                        } else if (onResponseListener != null) {
                            String message = json.optString("message");
                            String errorCode = json.optString("error_code");

                            if (errorCode != null &&
                                    (errorCode.equals("email_not_verified") || errorCode.equals("not_approved") || errorCode.equals("subscription_fail"))) {
                                message = json.optString("error_code");
                            }

                            onResponseListener.onErrorInExecutingTask(message, isRetryOption);
                        }
                    } else {
                        if (onResponseListener != null) {
                            onResponseListener.onErrorInExecutingTask(mContext.getResources()
                                    .getString(R.string.please_retry_option), isRetryOption);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    if (onResponseListener != null) {
                        onResponseListener.onErrorInExecutingTask(mContext.getResources()
                                .getString(R.string.parse_error), isRetryOption);
                    }
                }
            }, error -> {
                LogUtils.LOGD(AppConstant.class.getSimpleName(), "VolleyError: " + error);

                if (onResponseListener != null) {
                    onResponseListener.onErrorInExecutingTask(displayVolleyError(error), isRetryOption);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    if (mIsLoginSignUpRequest) {
                        try {
                            String registrationId = FirebaseInstanceId.getInstance().getToken(mContext.getResources().getString(R.string.gcm_defaultSenderId), "FCM");
                            if (registrationId != null && !registrationId.isEmpty()) {
                                postParams.put("registration_id", registrationId);
                                postParams.put("device_uuid", getDeviceUUID());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtils.LOGD(AppConstant.class.getSimpleName(), "Request Params: " + postParams);
                    return postParams;
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        volleyError = new VolleyError(new String(volleyError.networkResponse.data));
                    }

                    return volleyError;
                }
            };

            //Setting timeout to 0 to fix multiple post request at once
            if (method == Request.Method.POST) {
                request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            } else {
                request.setRetryPolicy(new DefaultRetryPolicy(
                        REQUEST_TIMEOUT_MS,
                        NO_OF_RETRY_ATTEMPTS,
                        BACK_OF_MULTIPLIER));
            }

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(request, tag_json_obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method for converting JSONArray to JSONObject
    public JSONObject convertToJsonObject(JSONArray jsonArray) {
        JSONObject newJsonObject = new JSONObject();

        try {
            newJsonObject.put("response", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newJsonObject;
    }

    /**
     * Marking all messages as read
     */
    public void markAllMessageRead(HashMap<String, String> params) {
        postJsonRequest(UrlUtil.MESSAGE_READ_URL, params);
    }

    /**
     * Marking all notifications as read
     */
    public void markAllNotificationsRead() {
        postJsonRequest(UrlUtil.NOTIFICATION_ALL_READ_URL, null);
    }

    /**
     * Marking all friend requests as read
     */
    public void markAllFriendRequestsRead() {
        postJsonRequest(UrlUtil.REQUEST_READ_URL, null);
    }

    public static boolean isRequestSuccessful(int statusCode) {
        switch (statusCode) {
            case 200:
                return true;
            case 201:
            case 204:
                return true;
            default:
                return false;
        }
    }

    //Used to display volley errors on every page using the error instance
    private String displayVolleyError(VolleyError error) {
        if (error instanceof TimeoutError) {
            isRetryOption = true;
            return mContext.getResources().getString(R.string.time_our_error) + "…";
        } else if (error instanceof NoConnectionError) {
            isRetryOption = true;
            return mContext.getResources().getString(R.string.network_connectivity_error);
        } else if (error instanceof AuthFailureError) {
            return mContext.getResources().getString(R.string.authentication_failure);
        } else if (error instanceof ServerError) {
            return mContext.getResources().getString(R.string.server_error);
        } else if (error instanceof NetworkError) {
            return mContext.getResources().getString(R.string.network_error);
        } else if (error instanceof ParseError) {
            return mContext.getResources().getString(R.string.parse_error);
        } else {
            return mContext.getResources().getString(R.string.please_retry_option);
        }
    }

    /*
     * getting screen width
     */
    @SuppressWarnings("deprecation")
    public int getScreenWidth() {
        int columnWidth = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display;
        if (wm != null) {
            display = wm.getDefaultDisplay();
            final Point point = new Point();

            try {
                display.getSize(point);
            } catch (NoSuchMethodError ignore) {
                // Older device
                point.x = display.getWidth();
                point.y = display.getHeight();
            }

            columnWidth = point.x;
        }

        return columnWidth;
    }

    /*
     * getting screen height
     */
    @SuppressWarnings("deprecation")
    public int getScreenHeight() {
        int columnHeight = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display;
        if (wm != null) {
            display = wm.getDefaultDisplay();
            final Point point = new Point();

            try {
                if (display != null) {
                    display.getSize(point);
                }
            } catch (NoSuchMethodError ignore) {
                // Older device
                point.x = display.getWidth();
                point.y = display.getHeight();
            }

            columnHeight = point.y;
        }

        return columnHeight;
    }

    public static String convertDateFormat(Resources resources, String date) {
        DateFormat sourceFormat;

        if (mLocale != null) {
            sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);
        } else {
            sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String s = "";

        try {
            Date dob_var = sourceFormat.parse(date);
            if (dob_var != null) {
                long diff = System.currentTimeMillis() - dob_var.getTime();
                if (diff < 60000) {
                    s += resources.getString(R.string.time_afewseconds);
                } else if (diff < 3600000) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                    s += resources.getQuantityString(R.plurals.time_minute, (int) minutes, minutes);
                } else if (diff < 86400000) {
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);
                    s += resources.getQuantityString(R.plurals.time_hour, (int) hours, hours);
                } else {
                    long days = TimeUnit.MILLISECONDS.toDays(diff);
                    if (((int) days) > 7) {
                        SimpleDateFormat sdf;
                        if (mLocale != null) {
                            sdf = new SimpleDateFormat("MMM dd, yyyy", mLocale);
                        } else {
                            sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        }
                        s += sdf.format(dob_var);
                    } else {
                        s += resources.getQuantityString(R.plurals.time_day, (int) days, days);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static String getYearFormat(String date) {
        String stringMonth = "";
        SimpleDateFormat format;

        if (mLocale != null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        try {
            Date dateObj = format.parse(date);
            stringMonth = (String) android.text.format.DateFormat.format("yyyy", dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stringMonth;
    }

    public static String getMonthFromDate(String date, String monthFormat) {
        String stringMonth = "";
        SimpleDateFormat format;

        if (mLocale != null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        try {
            Date dateObj = format.parse(date);
            stringMonth = (String) android.text.format.DateFormat.format(monthFormat, dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stringMonth;
    }

    public static String getDayFromDate(String date) {
        String day = "";
        SimpleDateFormat format;

        if (mLocale != null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        try {
            Date dateObj = format.parse(date);
            day = (String) android.text.format.DateFormat.format("dd", dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return day;
    }

    @Nullable
    public static String getHoursFromDate(String date, boolean use12hrFormat) {
        if (use12hrFormat) {
//            String timeString = null;
//            String minuteString;
            SimpleDateFormat format;

            if (mLocale != null) {
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);
            } else {
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            }
            try {
                Date dateObj = format.parse(date);
                return new SimpleDateFormat("h:mm a", mLocale == null ? Locale.getDefault() : mLocale).format(dateObj);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return getHoursFromDate(date);
        }
    }

    public static String getHoursFromDate(String date) {
        String timeString = null;
        String minuteString;
        SimpleDateFormat format;

        if (mLocale != null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        try {
            Date dateObj = format.parse(date);
            int hours = dateObj.getHours();
            int minutes = dateObj.getMinutes();

            if (minutes < 9)
                minuteString = "0" + minutes;
            else
                minuteString = "" + minutes;
            if (hours > 12) {
                hours -= 12;
                timeString = hours + ":" + minuteString + " PM ";
            } else {
                timeString = hours + ":" + minuteString + " AM ";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeString;
    }

    public String calculateDifference(int seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        String tempTimer;

        // Adding hours.
        if (hours == 0) {
            tempTimer = "";
        } else {
            tempTimer = ((hours < 10) ? "0" + hours + ":" : hours + ":");
        }

        // Adding minutes.
        if (minute == 0) {
            // Checking if the hours is not 0 then adding the hours in timer string.
            if (hours != 0) {
                tempTimer = tempTimer + "0" + minute;
            } else {
                tempTimer = "0" + minute;
            }
        } else {
            tempTimer = tempTimer + ((minute < 10) ? "0" + minute : "" + minute);
        }

        // Adding seconds.
        if (second == 0) {
            // Checking if the hours/minutes is not 0 then adding the seconds into timer string.
            if (minute != 0 || hours != 0) {
                tempTimer = tempTimer + ":0" + second;
            } else {
                tempTimer = "0" + second;
            }
        } else {
            tempTimer = tempTimer + ((second < 10) ? ":0" + second : ":" + second);
        }

        return tempTimer;
    }

    /**
     * Used to build url string.
     *
     * @param queryParams query parameters to be added in url
     * @param requestUrl  - url in which the parameters will be added
     */
    public String buildQueryString(String requestUrl, Map<String, String> queryParams) {

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            requestUrl = Uri.parse(requestUrl)
                    .buildUpon()
                    .appendQueryParameter(key, value)
                    .build().toString();
        }

        return requestUrl;
    }

    public void showProgressDialog() {
        try {
            if (!pDialog.isShowing())
                pDialog.show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard() {
        View view = ((Activity) mContext).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void hideKeyboardInDialog(View view) {
        if (view != null) {
            InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void showKeyboard() {
        View view = ((Activity) mContext).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }

    //Refreshing the user details
    public void refreshUserData() {
        if (!isLoggedOutUser()) {
            getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "user/profile/" + mUserDetail.optString("user_id"), new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    if (jsonObject != null) {
                        JSONObject userDetail = jsonObject.optJSONObject("response");
                        userDetail.put("gutterMenu", jsonObject.optJSONArray("gutterMenu"));
                        userDetail.put("profile_tabs", jsonObject.optJSONArray("profile_tabs"));
                        if (userDetail != null) {
                            PreferencesUtils.updateUserPreferences(mContext, userDetail.toString(),
                                    oauth_secret, oauthToken);
                            if (userDetail.length() > 0
                                    && CacheUtils.getInstance(mContext).getLru().get(userDetail.optString("image")) != null) {
                                CacheUtils.getInstance(mContext).getLru().remove(userDetail.optString("image"));
                            }
                        }
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                }
            });
        }
    }

    /**
     * Used to update user data for reflecting the changes in dashboard.
     *
     * @param jsonObject json object of user details
     */
    void saveDashboardValues(JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONArray menuObject = jsonObject.optJSONArray("menus");

            // Language Work
            JSONObject mLanguageObject = jsonObject.optJSONObject("languages");

            if (mLanguageObject != null && mLanguageObject.length() != 0) {
                String mDefaultLanguageCode = mLanguageObject.optString("default");
                JSONObject mMultiLanguages = mLanguageObject.optJSONObject("languages");

                // Pass Language Params using intent
                if (mDefaultLanguageCode != null && !mDefaultLanguageCode.isEmpty() &&
                        !mDefaultLanguageCode.equals("null")) {
                    PreferencesUtils.updateDashBoardData(mContext,
                            PreferencesUtils.DASHBOARD_DEFAULT_LANGUAGE,
                            mDefaultLanguageCode);
                    PreferencesUtils.updateDefaultLanguage(mContext,
                            mDefaultLanguageCode);
                }
                if (mMultiLanguages != null && mMultiLanguages.length() != 0) {
                    PreferencesUtils.updateDashBoardData(mContext,
                            PreferencesUtils.DASHBOARD_MULTI_LANGUAGE,
                            mMultiLanguages.toString());
                }
            }

            // Location Work
            JSONObject mLocationObject = jsonObject.optJSONObject("restapilocation");

            if (mLocationObject != null && mLocationObject.length() != 0) {
                String mDefaultLocation = mLocationObject.optString("default");
                JSONObject mMultiLocations = mLocationObject.optJSONObject("restapilocation");
                mLocationType = mLocationObject.optString("locationType");
                isLocationEnable = true;

                if (PreferencesUtils.getDefaultLocation(mContext) == null &&
                        mDefaultLocation != null &&
                        !mDefaultLocation.isEmpty()) {
                    PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.DASHBOARD_DEFAULT_LOCATION, mDefaultLocation);
                }
                if (mMultiLocations != null && mMultiLocations.length() != 0) {
                    PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.DASHBOARD_MULTI_LOCATION, mMultiLocations.toString());
                }
                if (mLocationType.equals("notspecific")) {
                    // Get device location is enable or not
                    isDeviceLocationEnable = mLocationObject.optInt("autodetectLocation", 0);
                    isDeviceLocationChange = mLocationObject.optInt("isChangeManually", 1);
                }
            }

            // Pass Dashboard Menus using intent
            if (menuObject != null && menuObject.length() != 0) {
                PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.DASHBOARD_MENUS, menuObject.toString());
            }

            // Set pref for app tour setting
            PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.APP_TOUR_ENABLED, String.valueOf(jsonObject.optInt("app_tour")));
        }
    }

    public void changeLanguage(final Context mContext, final String currentSelectedOption) {
        try {
            final ArrayAdapter<String> languageAdapter;
            final Map<String, String> mSelectedLanguageInfo;

            mSelectedLanguageInfo = new HashMap<>();

            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
            alertBuilder.setTitle(mContext.getResources().getString(R.string.language_popup_title));

            JSONObject multiLanguages = null;
            int selectedPosition = 0;

            languageAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_singlechoice);
            String languages = PreferencesUtils.getLanguages(mContext);

            if (languages != null && !languages.isEmpty()) {
                multiLanguages = new JSONObject(languages);
                JSONArray localeNames = multiLanguages.names();

                for (int i = 0; i < multiLanguages.length(); i++) {
                    String locale = localeNames.getString(i);
                    languageAdapter.add(multiLanguages.getString(locale));
                    mSelectedLanguageInfo.put(multiLanguages.getString(locale), locale);
                }
            }

            if (multiLanguages != null && multiLanguages.has(PreferencesUtils.getCurrentLanguage(mContext))) {
                String defaultLang = multiLanguages.optString(PreferencesUtils.getCurrentLanguage(mContext));
                selectedPosition = languageAdapter.getPosition(defaultLang);
            }

            alertBuilder.setSingleChoiceItems(languageAdapter, selectedPosition,
                    (dialog, which) -> {
                        String localeName = mSelectedLanguageInfo.get(languageAdapter.getItem(which));

                        if (localeName != null && !localeName.equals(PreferencesUtils.getCurrentLanguage(mContext))) {

                            changeAppLocale(localeName, false);

                            PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.CURRENT_LANGUAGE, localeName);

                            Bundle bundle = new Bundle();
                            bundle.putString("previousSelected", currentSelectedOption);
                            Intent intent = new Intent(mContext, WelcomeScreen.class);
                            intent.putExtras(bundle);
                            ((Activity) mContext).overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            ((Activity) mContext).finish();
                            ((Activity) mContext).overridePendingTransition(0, 0);
                            mContext.startActivity(intent);
                        }

                        dialog.dismiss();
                    });

            alertBuilder.create().show();
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean isRtlSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) mContext).getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public void changeAppLocale(String languageCode, boolean isDashBoardRequest) {
        Locale locale;

        if (languageCode.contains("_")) {
            String localNameArray[] = languageCode.split("_");
            locale = new Locale(localNameArray[0], localNameArray[1]);
        } else {
            locale = new Locale(languageCode);
        }

        Locale.setDefault(locale);

        if (!isDashBoardRequest) {
            Configuration config = new Configuration();
            config.locale = locale;
            ((Activity) mContext).getBaseContext().getResources().updateConfiguration(config, null);
        }

        // Change App Locale when user change language of app
        if (PreferencesUtils.getUserDetail(mContext) != null) {
            try {
                JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                userDetail.put("locale", languageCode);
                PreferencesUtils.updateUserDetails(mContext, userDetail.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkManifestPermission(String manifestPermission) {
        return ContextCompat.checkSelfPermission(mContext, manifestPermission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestForManifestPermission(String manifestPermission, int requestCode) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mContext, manifestPermission) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{manifestPermission}, requestCode);
        }
    }

    /**
     * Method to get the screen width pixels.
     *
     * @param context Context of calling class.
     * @return returns the screen width pixel.
     **/
    public static int getDisplayMetricsWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Method to get the screen height pixels.
     *
     * @param context Context of calling class.
     * @return returns the screen height pixel.
     **/
    public static int getDisplayMetricsHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void eraseUserDatabase() {
        PreferencesUtils.clearSharedPreferences(mContext);
        DataStorage.clearApplicationData(mContext);
        CookieSyncManager.createInstance(mContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        LoginManager.getInstance().logOut();
        Intent homeScreen;

        if (ConstantVariables.INTRO_SLIDE_SHOW_ENABLED == 1) {
            homeScreen = new Intent(mContext, NewLoginActivity.class);
        } else {
            homeScreen = new Intent(mContext, LoginActivity.class);
        }

        homeScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(homeScreen);
        ((Activity) mContext).finish();
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static Animation getZoomInAnimation(Context context) {
        Animation zoomInAnimation = AnimationUtils.loadAnimation(context, R.anim.bubble);
        zoomInAnimation.setDuration(300);
        return zoomInAnimation;
    }

    public String getDeviceUUID() {
        return Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK.
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(mContext);
        return status == ConnectionResult.SUCCESS;
    }

    public void setOnCommunityAdsLoadedListener(OnCommunityAdsLoadedListener communityAdsLoadedListener) {
        mCommunityAdsLoadedListener = communityAdsLoadedListener;
    }

    public void getCommunityAds(int placementCount, int adType) {
        String communityAdsUrl = UrlUtil.GET_COMMUNITY_ADS_URL + "?placementCount=" + placementCount + "&type=" + adType;

        getJsonResponseFromUrl(communityAdsUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if (mCommunityAdsLoadedListener != null) {
                    mCommunityAdsLoadedListener.onCommunityAdsLoaded(jsonObject.optJSONArray("advertisments"));
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                if (mCommunityAdsLoadedListener != null) {
                    mCommunityAdsLoadedListener.onCommunityAdsLoaded(null);
                }
            }
        });
    }

    public void getCommunityAds(int placementCount, int adType, JSONObject jsonObject, final OnCommunityAdsLoadedListnerFeeds onCommunityAdsLoadedListnerFeeds) {
        final JSONObject dataObject = jsonObject;
        String communityAdsUrl = UrlUtil.GET_COMMUNITY_ADS_URL + "?placementCount=" + placementCount + "&type=" + adType;

        getJsonResponseFromUrl(communityAdsUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if (onCommunityAdsLoadedListnerFeeds != null) {
                    onCommunityAdsLoadedListnerFeeds.onCommunityAdsLoaded(jsonObject.optJSONArray("advertisments"), dataObject);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                if (onCommunityAdsLoadedListnerFeeds != null) {
                    onCommunityAdsLoadedListnerFeeds.onCommunityAdsLoaded(null, dataObject);
                }
            }
        });
    }

    /**
     * proceedToUserLogin
     *
     * @param mContext      Context
     * @param bundle        Bundle
     * @param intentAction  Intent action
     * @param intentType    Intent type
     * @param emailValue    email
     * @param passwordValue password
     * @param jsonObject    json object
     */
    void proceedToUserLogin(Context mContext,
                            Bundle bundle,
                            String intentAction,
                            String intentType,
                            String emailValue,
                            String passwordValue,
                            JSONObject jsonObject) {
        proceedToUserLogin(mContext, bundle, intentAction, intentType, emailValue, passwordValue, jsonObject, null);
    }

    /**
     * proceedToUserLogin
     *
     * @param mContext      Context
     * @param bundle        Bundle
     * @param intentAction  Intent action
     * @param intentType    Intent type
     * @param emailValue    email
     * @param passwordValue password
     * @param jsonObject    json object
     * @param loginListener override callback
     */
    void proceedToUserLogin(Context mContext,
                            Bundle bundle,
                            String intentAction,
                            String intentType,
                            String emailValue,
                            String passwordValue,
                            JSONObject jsonObject,
                            @Nullable LoginListener loginListener) {

        PreferencesUtils.clearSharedPreferences(mContext);
        PreferencesUtils.clearDashboardData(mContext);
        DataStorage.clearApplicationData(mContext);

        final Intent intent;

        if (bundle != null) {
            intent = new Intent(mContext, Status.class);
            intent.putExtras(bundle);
            intent.setAction(intentAction);
            intent.setType(intentType);
        } else {
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("isSetLocation", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (jsonObject.has("oauth_token")) {
            JSONObject userDetail = jsonObject.optJSONObject("user");
            String user_language = userDetail.optString("language");
            String oauthToken = jsonObject.optString("oauth_token");
            String oauth_secret = jsonObject.optString("oauth_secret");
            PreferencesUtils.updateUserPreferences(mContext, userDetail.toString(), oauth_secret, oauthToken);

            // Save email and base64 encrypted password in SharedPreferences
            PreferencesUtils.UpdateLoginInfoPref(mContext, emailValue, passwordValue, userDetail.optInt("user_id"));

            /* English is coming from API instead of it's language code, It will automatically work when API issue will be resolved.. */
            if (user_language.equals("English")) {
                user_language = "en";
            }

            // Set default language to current language when we have only single language for the app
            String multiLanguages = PreferencesUtils.getLanguages(mContext);
            if (multiLanguages != null && !multiLanguages.isEmpty()) {
                PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.CURRENT_LANGUAGE, user_language);
                changeAppLocale(user_language, false);
            } else {
                PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.CURRENT_LANGUAGE, PreferencesUtils.getDefaultLanguage(mContext));
                changeAppLocale(PreferencesUtils.getDefaultLanguage(mContext), false);
            }

            if (loginListener != null) {
                loginListener.onOverrideLogin();
            } else {
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        } else if (jsonObject.optString("body") != null && !jsonObject.optString("body").isEmpty()) {
            Intent webViewIntent = new Intent(mContext, WebViewActivity.class);
            webViewIntent.putExtra("email", emailValue);
            webViewIntent.putExtra("password", passwordValue);
            webViewIntent.putExtra("isSubscription", true);
            webViewIntent.putExtra("url", jsonObject.optString("body"));
            mContext.startActivity(webViewIntent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void proceedToUserSignup(Context mContext,
                                    Bundle mFbTwitterBundle,
                                    String emailAddress,
                                    String password,
                                    String subscriptionUrl,
                                    JSONObject body) {
        /**
         * Check If there user has chosen a paid subscription
         * redirect to the web view activity on the url which is coming with body string in response.
         * else user will be logged-in
         */
        if (subscriptionUrl == null || subscriptionUrl.isEmpty()) {
            PreferencesUtils.clearSharedPreferences(mContext);
            DataStorage.clearApplicationData(mContext);
            PreferencesUtils.clearDashboardData(mContext);
            String OauthToken = body.optString("oauth_token");
            String oauth_secret = body.optString("oauth_secret");
            JSONObject userDetail = body.optJSONObject("user");

            if (userDetail != null) {
                String user_language = userDetail.optString("language");

                if (user_language.equals("English"))
                    user_language = "en";

                // Set default language to current language when we have only single language for the app
                String multiLanguages = PreferencesUtils.getLanguages(mContext);

                if (multiLanguages != null && !multiLanguages.isEmpty()) {
                    PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.CURRENT_LANGUAGE, user_language);
                } else {
                    PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.CURRENT_LANGUAGE, PreferencesUtils.getDefaultLanguage(mContext));
                }

                PreferencesUtils.updateUserPreferences(mContext, userDetail.toString(), oauth_secret, OauthToken);

                // Save email and base64 encrypted password in SharedPreferences
                PreferencesUtils.UpdateLoginInfoPref(mContext, emailAddress, password, userDetail.optInt("user_id"));
            }

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("isSetLocation", true);
            ((Activity) mContext).finish();
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("isSubscription", true);
            intent.putExtra("email", emailAddress);
            intent.putExtra("password", password);
            intent.putExtra("url", subscriptionUrl);

            if (mFbTwitterBundle != null && !mFbTwitterBundle.isEmpty()) {
                intent.putExtra("fb_twitter_info", mFbTwitterBundle);
            }

            ((Activity) mContext).startActivityForResult(intent, ConstantVariables.SIGN_UP_WEBVIEW_CODE);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    /**
     * Restart the app
     */
    public void restartApp() {
        Intent intent = new Intent(mContext, WelcomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void animateEnter() {
        animateEnter((Activity) mContext);
    }

    public void animateClose() {
        animateClose((Activity) mContext);
    }

    private static void animateEnter(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    private static void animateClose(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static String buildUrl(Integer id) {

        return DEFAULT_URL + "advancedevents/view/" + id + "?gutter_menu=" + 1;
    }
}
