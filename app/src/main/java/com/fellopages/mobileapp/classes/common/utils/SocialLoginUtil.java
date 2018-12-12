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

package com.fellopages.mobileapp.classes.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.MainActivity;
import com.fellopages.mobileapp.classes.modules.user.signup.SubscriptionActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SocialLoginUtil {

    private static final Map<String, String> FACEBOOK_TWITTER_PARAMS = new HashMap<>();
    private static final Bundle FACEBOOK_TWITTER_BUNDLE = new Bundle();
    private static OnSocialLoginSuccessListener mOnSocialLoginSuccessListener;


    /**
     * Method For Initializing Facebook SDK
     * @param context Context of calling class.
     */
    public static void initializeFacebookSDK (Context context) {
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setApplicationId(context.getResources().getString(R.string.facebook_app_id));
    }


    /**
     * Method to clear Facebook/Twitter instances.
     * @param context Context of calling class.
     * @param loginType Login Type.
     */
    public static void clearFbTwitterInstances(Context context, String loginType) {
        if (loginType != null && !loginType.isEmpty()) {
            if (loginType.equals("facebook")) {
                LoginManager.getInstance().logOut();
            } else {
                CookieSyncManager.createInstance(context);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }
        }
    }


    /***
     * Method to put facebook access params into hash map.
     * @param facebookUid Facebook user id.
     * @param fbAccessToken Facebook access token.
     */
    public static void putFacebookAccessParams (String facebookUid, String fbAccessToken) {
        FACEBOOK_TWITTER_PARAMS.clear();
        FACEBOOK_TWITTER_PARAMS.put("facebook_uid", facebookUid);
        FACEBOOK_TWITTER_PARAMS.put("access_token", fbAccessToken);
        FACEBOOK_TWITTER_PARAMS.put("code", "%20");
    }


    /**
     * Method to put Twitter access params into hash map.
     * @param twitterUid Twitter user id.
     * @param twitterToken Twitter access token.
     * @param twitterSecret Twitter Secret token.
     */
    public static void putTwitterAccessParams(String twitterUid, String twitterToken, String twitterSecret){
        FACEBOOK_TWITTER_PARAMS.clear();
        FACEBOOK_TWITTER_PARAMS.put("twitter_uid", twitterUid);
        FACEBOOK_TWITTER_PARAMS.put("twitter_token", twitterToken);
        FACEBOOK_TWITTER_PARAMS.put("twitter_secret", twitterSecret);
    }


    /**
     * Method to get Facebook/Twitter access params.
     * @return Returns the Social login access params.
     */
    public static Map<String, String> getFacebookTwitterParams () {
        return FACEBOOK_TWITTER_PARAMS;
    }

    public static void putFacebookBundleParams(String gender, String timezone, String firstName, String lastName,
                                               String imageUrl) {
        FACEBOOK_TWITTER_BUNDLE.clear();
        FACEBOOK_TWITTER_BUNDLE.putString("gender", gender);
        FACEBOOK_TWITTER_BUNDLE.putString("timezone", timezone);
        FACEBOOK_TWITTER_BUNDLE.putString("firstName", firstName);
        FACEBOOK_TWITTER_BUNDLE.putString("lastName", lastName);
        FACEBOOK_TWITTER_BUNDLE.putString("picture", imageUrl);
    }


    /**
     * Method to return social login intent params.
     * @param loginType Type of social login.
     * @param email Email of the user.
     * @param response Login Response of social login
     * @return Returns the social login intent params.
     */
    public static Bundle getSocialLoginBundle(String loginType, String email, String response, String firstName,
                                              String lastName, String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("loginType", loginType);
        bundle.putString("email", email);
        bundle.putString("response", response);
        return bundle;
    }


    /**
     * Method for Facebook login authentication process
     * @param context Context of calling class.
     * @param view Main View of the class.
     * @param callbackManager Facebook callback manager.
     * @param isIntegrationRequest True if it is integration request.
     */
    public static void registerFacebookLoginCallback(final Context context, final View view,
                                                     CallbackManager callbackManager,
                                                     final boolean isIntegrationRequest) {

        final AppConstant appConstant = new AppConstant(context);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        appConstant.showProgressDialog();

                        if (loginResult.getAccessToken() != null) {

                            final String fbAccessToken = loginResult.getAccessToken().getToken();

                            GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    if (object != null) {

                                        String fbUid = object.optString("id");
                                        String email = object.optString("email");
                                        String gender = object.optString("gender");
                                        String timezone = object.optString("timezone");
                                        String firstName = object.optString("first_name");
                                        String lastName = object.optString("last_name");
                                        JSONObject pictureObject = object.optJSONObject("picture");
                                        JSONObject dataObject = pictureObject.optJSONObject("data");
                                        String imageUrl = dataObject.optString("url");

                                        String loginType = "facebook";
                                        putFacebookAccessParams(fbUid, fbAccessToken);
                                        putFacebookBundleParams(gender, timezone, firstName, lastName, imageUrl);

                                        Map<String, String> params = getFacebookTwitterParams();
                                        params.put("ip", GlobalFunctions.getLocalIpAddress());
                                        String loginUrl = UrlUtil.LOGIN_URL;
                                        if (isIntegrationRequest) {
                                            loginUrl = loginUrl.replace("login", "login/facebook");
                                        }
                                        loginUrl = appConstant.buildQueryString(loginUrl,
                                                appConstant.getAuthenticationParams());

                                        performActionOnSocialLogin(context, appConstant,
                                                appConstant.buildQueryString(loginUrl, params),
                                                loginType, email, firstName, lastName, imageUrl, isIntegrationRequest);

                                    }
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender,timezone, picture.type(large), first_name, last_name, age_range");
                            graphRequest.setParameters(parameters);
                            graphRequest.executeAsync();
                        }
                    }

                    @Override
                    public void onCancel() {
                        appConstant.hideProgressDialog();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        appConstant.hideProgressDialog();
                        if (exception != null && exception.toString().contains("Invalid key hash")) {
                            SnackbarUtils.displaySnackbarLongTime(view, exception.toString());
                        } else {
                            SnackbarUtils.displaySnackbar(view,
                                    context.getResources().getString(R.string.please_retry_option));
                        }
                    }
                });
    }


    /**
     * Method for Twitter login authentication process
     * @param context Context of calling class.
     * @param view Main View.
     * @param twitterLoginButton Twitter login button.
     * @param isIntegrationRequest True if it is integration request.
     */
    public static void registerTwitterLoginCallback(final Context context, final View view,
                                                    TwitterLoginButton twitterLoginButton,
                                                    final boolean isIntegrationRequest) {

        final AppConstant appConstant = new AppConstant(context);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                appConstant.showProgressDialog();
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String twitterToken = authToken.token;
                String twitterSecret = authToken.secret;
                String twitterUid = String.valueOf(result.data.getUserId());
                final String userName = result.data.getUserName();
                final String loginType = "twitter";

                putTwitterAccessParams(twitterUid, twitterToken, twitterSecret);
                final Map<String, String> params = getFacebookTwitterParams();
                params.put("ip", GlobalFunctions.getLocalIpAddress());
                String loginUrl = UrlUtil.LOGIN_URL;
                if (isIntegrationRequest) {
                    loginUrl = loginUrl.replace("login", "login/twitter");
                }
                loginUrl = appConstant.buildQueryString(loginUrl,
                        appConstant.getAuthenticationParams());

                TwitterAuthClient authClient = new TwitterAuthClient();
                final String finalLoginUrl = loginUrl;
                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        String email = result.data;
                        performActionOnSocialLogin(context, appConstant, appConstant.buildQueryString(finalLoginUrl, params),
                                loginType, email, userName, null, null, isIntegrationRequest);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        performActionOnSocialLogin(context, appConstant, appConstant.buildQueryString(finalLoginUrl, params),
                                loginType, null, userName, null, null, isIntegrationRequest);
                    }
                });

            }

            @Override
            public void failure(TwitterException exception) {
                String message = context.getResources().getString(R.string.please_retry_option);
                if (exception.toString().contains("TwitterAuthException")) {
                    message = context.getResources().getString(R.string.twitter_authentication_failed_message);
                }
                SnackbarUtils.displaySnackbarLongTime(view, message);
            }
        });

    }


    /**
     * Method to get login response on successful call back from social login.
     * @param context Context of calling class.
     * @param appConstant Instance of AppConstant Class.
     * @param loginUrl Login Url.
     * @param loginType Type of social login.
     * @param email Email of the user..
     * @param isIntegrationRequest True if it is integration request.
     */
    public static void performActionOnSocialLogin(final Context context, final AppConstant appConstant,
                                                  final String loginUrl, final String loginType,
                                                  final String email, final String firstName,
                                                  final String lastName, final String imageUrl,
                                                  final boolean isIntegrationRequest) {

        final Map<String, String> params = getFacebookTwitterParams();
        params.put("ip", GlobalFunctions.getLocalIpAddress());

        appConstant.postJsonResponseForUrl(loginUrl, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject)  {
                appConstant.hideProgressDialog();
                if (isIntegrationRequest && mOnSocialLoginSuccessListener != null) {
                    mOnSocialLoginSuccessListener.onSuccess(loginType);

                } else {

                    if (jsonObject.optString("body") != null && !jsonObject.optString("body").isEmpty()) {
                        Intent webViewIntent = new Intent(context, WebViewActivity.class);
                        webViewIntent.putExtra("isSubscription", true);
                        webViewIntent.putExtra("url", jsonObject.optString("body"));
                        webViewIntent.putExtra("loginType", loginType);
                        context.startActivity(webViewIntent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else if (jsonObject.has("oauth_token")){
                        PreferencesUtils.clearSharedPreferences(context);
                        PreferencesUtils.clearDashboardData(context);
                        DataStorage.clearApplicationData(context);
                        JSONObject userDetail = jsonObject.optJSONObject("user");
                        String oauth_secret = jsonObject.optString("oauth_secret");
                        String oauth_token = jsonObject.optString("oauth_token");
                        PreferencesUtils.updateUserPreferences(context, userDetail.toString(),
                                oauth_secret, oauth_token);
                        PreferencesUtils.updateDashBoardData(context,
                                PreferencesUtils.DASHBOARD_MENUS, null);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("isSetLocation", true);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else if (jsonObject.has("subscription")) {
                        Intent loginIntent = new Intent(context, SubscriptionActivity.class);
                        Bundle bundle = new Bundle();
                        if (loginType.equals("facebook")) {
                            bundle = FACEBOOK_TWITTER_BUNDLE;
                        } else if (loginType.equals("twitter")) {
                            bundle.putString("username", firstName);
                        }
                        bundle.putString("loginType", loginType);
                        bundle.putString("email", email);
                        bundle.putString("response", jsonObject.toString());
                        loginIntent.putExtra("fb_twitter_info", bundle);
                        context.startActivity(loginIntent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                appConstant.hideProgressDialog();
                clearFbTwitterInstances(context, loginType);
                if (mOnSocialLoginSuccessListener != null) {
                    mOnSocialLoginSuccessListener.onError(loginType, message);
                }
            }
        });

    }

    /**
     * Method to set listener for the fb/twitter integration under general settings.
     * @param onSocialLoginSuccessListener Instance of Listener.
     */
    public static void setSocialLoginListener(OnSocialLoginSuccessListener onSocialLoginSuccessListener) {
        mOnSocialLoginSuccessListener = onSocialLoginSuccessListener;
    }

    public interface OnSocialLoginSuccessListener {
        void onSuccess(String loginType);
        void onError(String loginType, String errorMessage);
    }

}
