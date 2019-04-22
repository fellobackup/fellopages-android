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

package com.fellopages.mobileapp.classes.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.TLSSocketFactory;
import com.fellopages.mobileapp.classes.common.utils.okhttp.OkHttpUtils;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private FirebaseAnalytics mFirebaseAnalytics;

    private static AppController mInstance;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FontChanger.setDefaultFont(this, "MONOSPACE", getResources().getString(R.string.default_font_name));
        FontChanger.setDefaultFont(this, "DEFAULT", getResources().getString(R.string.default_font_name));
        FontChanger.setDefaultFont(this, "SERIF", getResources().getString(R.string.default_font_name));
        FontChanger.setDefaultFont(this, "SANS_SERIF", getResources().getString(R.string.default_font_name));
        mInstance = this;
        VolleyLog.DEBUG = false;

        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_key),
                        getResources().getString(R.string.twitter_secret)))
                .debug(false)
                .build();
        Twitter.initialize(config);

        Fabric.with(this, new Crashlytics());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Updating current app version in preferences.
        try {
            PreferencesUtils.updateCurrentAppVersionPref(this, this.getPackageManager().
                    getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier((hostname, session) -> true)
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            } else {
                HttpStack stack;
                try {
                    stack = new HurlStack(null, new TLSSocketFactory());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    stack = new HurlStack();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    stack = new HurlStack();
                }
                mRequestQueue = Volley.newRequestQueue(getApplicationContext(), stack);
            }

            if (!GlobalFunctions.isSslEnabled()) {
                try {
                    ProviderInstaller.installIfNeeded(getApplicationContext());
                    SSLContext sslContext;
                    sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, null, null);
                    sslContext.createSSLEngine();
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                        | NoSuchAlgorithmException | KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setShouldCache(false);
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, label);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE , action);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    /***
     * Tracking event
     *
     * @param name event name
     * @param params   event params
     */
    public void trackEventCustom(String name, Bundle params) {
        mFirebaseAnalytics.logEvent(name, params);
    }
}
