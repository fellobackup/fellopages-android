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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.fellopages.mobileapp.R;
import com.google.gson.Gson;
import com.fellopages.mobileapp.classes.modules.store.data.CartData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PreferencesUtils {

    // Module preferences
    private static final String MODULE_INFO_PREF = "ModuleInfo";
    private static final String CURRENT_MODULE = "currentSelectedModule";

    // Language Preferences
    public static final String DASHBOARD_DEFAULT_LANGUAGE = "defaultLanguage";
    public static final String DASHBOARD_MULTI_LANGUAGE = "multiLanguages";
    public static final String DASHBOARD_MENUS = "dashboardMenus";
    public static final String CURRENT_LANGUAGE = "current_language";
    private static final String DEFAULT_LANGUAGE_PREFERENCES = "default_language_preference";
    private static final String DEFAULT_LANGUAGE_KEY = "default_language_key";

    // User preferences
    private static final String USER_PREFERENCES = "authorized_user";
    private static final String USER_DETAILS = "userDetail";
    private static final String OAUTH_TOKEN = "oauth_token";
    private static final String OAUTH_SECRET = "oauth_secret";

    //Guest User Settings
    private static final String GUEST_USER_PREFERENCES = "guest_user_data";
    private static final String BROWSE_AS_GUEST = "browse_As_guest";

    //Notification Preferences
    private static final String NOTIFICATION_PREFERENCES ="notification_pref";
    public static final String MESSAGE_COUNT = "message_count";
    public static final String NOTIFICATION_COUNT = "notification_count";
    public static final String FRIEND_REQ_COUNT = "friend_req_count";
    public static final String CART_COUNT = "cart_count";

    //List Preferences
    private static final String CURRENT_LIST = "currentSelectedList";
    // Location Preferences
    public static final String DASHBOARD_DEFAULT_LOCATION = "defaultLocation";
    public static final String DASHBOARD_MULTI_LOCATION = "multiLocations";
    public static final String LOCATION_PERMISSION_DISPLAYED_ON_MAP = "locationPermissionDisplayedOnMap";

    // MLT Preferences
    private static final String CURRENT_MLT_OPTION_PREF = "current_mlt";
    private static final String CURRENT_MLT_LABEL = "current_mlt_label";
    private static final String CURRENT_MLT_SINGULAR_LABEL = "current_mlt_singular_label";
    private static final String CURRENT_LISTING_ID = "mlt_listing_id";
    private static final String BROWSE_MLT_TYPE = "browse_type";
    private static final String SECONDARY_BROWSE_MLT_TYPE = "secondary_browse_type";
    private static final String VIEW_MLT_TYPE = "view_type";
    private static final String CURRENT_MLT_ICON = "current_mlt_icon";
    private static final String CAN_MLT_CREATE = "can_create";
    private static final String MLT_PACKAGES_ENABLED = "packages_enabled";
    private static final String MLT_CAN_VIEW = "can_view_mlt";

    // App Version Preferences
    private static final String APP_PREFERENCES = "app_pref";
    private static final String APP_VERSION_PREF = "appVersion";
    private static final String APP_VERSION_STRING = "androidVersion";
    private static final String APP_LAUNCH_COUNT = "app_launch_count";
    public static final String APP_RATED = "isAppRated";
    public static final String NOT_RATED = "NotRated";

    // Sound setting Preferences
    private static final String SOUND_SETTING_PREF = "sound_setting";
    private static final String SOUND_ENABLED = "sound_value";

    // Login Info Preferences
    private static final String LOGIN_INFO_PREF = "LoginInfo";
    public static final String LOGIN_EMAIL = "email";
    public static final String LOGIN_PASSWORD = "password";
    private static final String LOGIN_USER_ID = "user_id";

    //Location Enabled Setting
    private static final String LOCATION_SETTING_PREFERENCES = "location_enabled_setting";
    private static final String LOCATION_ENABLED = "location_enabled";

    // Feed Reactions Preferences
    private static final String FEED_REACTIONS_PREF = "FeedReactions";
    public static final String FEED_REACTIONS = "feedReactions";
    public static final String MY_FEED_REACTIONS = "myFeedReactions";

    // Reactions Object Preferences
    private static final String REACTIONS_PREF = "ReactionsPref";
    private static final String REACTIONS = "reactionsObject";

    // Stickers Enabled Pref
    private static final String STICKERS_ENABLED_PREF = "StickersEnabled";
    private static final String STICKERS_ENABLED = "stickersEnabled";

    // Stickers Store Menu Pref
    private static final String STICKERS_STORE_PREF = "StickersStores";
    private static final String STICKERS_STORE_MENU = "stickersMenu";

    // Reactions Enabled Pref
    private static final String REACTIONS_ENABLED_PREF = "ReactionsEnabled";
    private static final String REACTIONS_ENABLED = "reactionsEnabled";

    //NestedCommentEnabled
    private static final String MODULE_ENABLED = "module_enabled";
    private static final String NESTED_COMMENT_ENABLED = "nested_comment_enabled";

    // People Suggestion Pref
    private static final String PEOPLE_SUGGESTION_PREF = "PeopleSuggestion";
    private static final String IS_CONTACT_SYNCED = "IsContactSynced";

    //Site Content Cover Pref
    private static final String SITE_CONTENT_COVER_PHOTO_ENABLED_PREF = "SiteContentCoverPhotoEnable";
    private static final String CONTENT_COVER_ENABLED = "contentCoverEnabled";

    //Enable Module Pref
    private static final String ENABLE_MODULE_PREF = "EnableModule";
    private static final String ENABLE_MODULE_LIST = "enableModuleList";

    //Emoji Enabled Pref
    private static final String EMOJI_ENABLE_PREF = "emoji_enabled_pref";
    private static final String EMOJI_ENABLED = "emoji_enabled";

    //Contacts Pref
    public static final String CONTACTS_PREF = "contact_pref";
    public static final String CONTACT_LIST = "contact_list";

    //Contacts Pref
    public static final String REPORT_AD_PREF = "report_ads_pref";
    public static final String REPORT_ADS_ARRAY = "report_ads_array";

    //Advancec event Pref
    public static final String TICKET_PREF = "ticket_enabled_pref";
    public static final String IS_TICKET_ENABLED = "is_ticket_enabled";
    public static final String IS_PACKAGE_ENABLED = "is_package_enabled";
    public static final String IS_CAN_CREATE= "is_can_create";
    public static final String ADV_EVENT_TITLE= "adv_event_title";

    // Video Source Pref
    public static final String VIDEO_SOURCE_PREF = "video_source_pref";
    public static final String IS_MY_DEVICE_ENABLED = "is_my_device_enabled";

    //Browse Member View type Pref
    public static final String MEMBER_VIEW_PREF = "member_view_pref";
    public static final String MEMBER_VIEW_TYPE = "member_view_type";

    // App Upgrade Preferences
    private static final String APP_UPGRADE_PREF = "app_upgrade_pref";
    private static final String APP_UPGRADE_DIALOG_IGNORED = "app_upgrade_dialog_ignored";
    private static final String APP_UPGRADE_REMIND = "app_upgrade_remind";

    // Video quality Preferences
    private static final String VIDEO_QUALITY_PREF = "video_quality_pref";
    private static final String VIDEO_QUALITY = "video_quality";

    // Story Preferences
    private static final String STORY_PREF = "story_pref";
    private static final String STORY_DURATION = "story_duration";
    private static final String STORY_PRIVACY = "story_privacy";
    private static final String STORY_PRIVACY_KEY = "story_privacy_key";

    // AAF filter Preferences
    private static final String AAF_FILTER_PREF = "aaf_filter_pref";
    private static final String IS_FILTER_ENABLED = "is_filter_enabled";

    private static final String ACTION_EVENT_PREF = "action_event_pref";
    private static final String IS_EVENT_ENABLED = "is_event_enabled";

    //OTP enable Preferences
    private static final String OTP_ENABLED_PREF = "otp_enabled_pref";
    private static final String OTP_OPTION = "otp_option";

    //OTP plugin enabled Preferences
    private static final String OTP_PLUGIN_PREF = "otp_plugin_pref";
    private static final String IS_OTP_PLUGIN = "is_otp_plugin";

    // AAF filter Preferences
    private static final String AAF_GREETINGS_PREF = "aaf_greetings_pref";
    private static final String AAF_GREETINGS = "aaf_greetings";
    private static final String BIRTHDAYS = "birthdays";
    private static final String GREETINGS_DATE = "greetings_date";


    // Show case view Preferences

    public static final String APP_TOUR_ENABLED = "app_tour_enabled";

    public static final String SHOW_CASE_VIEW_PREF = "show_case_view_pref";
    public static final String NAVIGATION_ICON_CASE_VIEW = "navigation_case_view";
    public static final String SEARCH_ICON_CASE_VIEW = "search_case_view";
    public static final String SEARCH_BAR_CASE_VIEW = "search_bar_case_view";
    public static final String LOCATION_ICON_CASE_VIEW = "location_case_view";
    public static final String CART_ICON_CASE_VIEW = "cart_case_view";
    public static final String STATUS_POST_CASE_VIEW = "status_case_view";
    public static final String FAB_CREATE_CASE_VIEW = "fab_create_case_view";
    public static final String FAB_MENU_CASE_VIEW = "fab_menu_case_view";
    public static final String USER_PROFILE_SHOW_CASE_VIEW = "user_profile_case_view";
    public static final String FEED_HOME_ICON_SHOW_CASE_VIEW = "feed_home_icon_case_view";

    // Showcase view user profile preferences
    public static final String USER_PROFILE_EDIT_PHOTO_SHOW_CASE_VIEW = "user_profile_edit_photo_case_view";
    public static final String USER_PROFILE_EDIT_SHOW_CASE_VIEW = "user_profile_profile_case_view";
    public static final String USER_PROFILE_MORE_SHOW_CASE_VIEW = "user_profile_more_case_view";
    public static final String USER_PROFILE_FRIENDS_SHOW_CASE_VIEW = "user_profile_friends_case_view";
    public static final String USER_PROFILE_FOLLOW_SHOW_CASE_VIEW = "user_profile_follow_case_view";
    public static final String USER_PROFILE_MESSAGE_SHOW_CASE_VIEW = "user_profile_message_case_view";
    public static final String USER_PROFILE_FAB_CREATE_SHOW_CASE_VIEW = "user_profile_fab_create_case_view";


    // Status privacy key selected
    private static final String STATUS_PRIVACY_PREF = "status_privacy_pref";
    private static final String STATUS_PRIVACY_KEY = "status_privacy_key";
    private static final String STATUS_POST_PRIVACY_OPTIONS = "status_post_privacy_option";
    private static final String STATUS_PRIVACY_MULTI_OPTIONS = "status_privacy_multi_options";

    public static final String USER_LOCATION_LATITUDE ="user_location_latitude";
    public static final String USER_LOCATION_LONGITUDE ="user_location_longitude";

    /**
     * Used to update current selected module.
     * @param moduleName - module name which will be updated
     */
    public static void updateCurrentModule(Context context, String moduleName){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MODULE_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_MODULE, moduleName);
        editor.apply();
    }

    //Getting the current module
    public static String getCurrentSelectedModule(Context context){
        return context.getSharedPreferences(MODULE_INFO_PREF, Context.MODE_PRIVATE)
                .getString(CURRENT_MODULE, null);
    }

    public static void  updateCurrentList(Context context, String listName){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MODULE_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_LIST, listName);
        editor.apply();
    }

    //Getting the current module
    public static String getCurrentSelectedList(Context context){
        return context.getSharedPreferences(MODULE_INFO_PREF, Context.MODE_PRIVATE)
                .getString(CURRENT_LIST, null);
    }

    //Updating guest user setting data
    public static void updateGuestUserSettings(Context context, String browse_as_guest){
        SharedPreferences sharedPreferences = context.getSharedPreferences(GUEST_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BROWSE_AS_GUEST, browse_as_guest);
        editor.apply();
    }

    //Checking for guest user setting
    public static boolean isGuestUserEnabled(Context context){
        return context.getSharedPreferences(GUEST_USER_PREFERENCES, Context.MODE_PRIVATE)
                .getString(BROWSE_AS_GUEST, "1").equals("1");
    }

    /**
     * Used to update user detail preferences.
     * @param userData - converted string of the current user's detail json object.
     * @param oauth_secret - Authentication secret key which verifies the user.
     * @param OauthToken Authentication token of the current user.
     */
    public static void updateUserPreferences(Context context, String userData,String oauth_secret,String OauthToken){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_DETAILS, userData);
        editor.putString(OAUTH_TOKEN, OauthToken);
        editor.putString(OAUTH_SECRET, oauth_secret);
        editor.apply();
    }

    /**
     * Update user details with new Locale when Language get changed of app
     * @param userData - converted string of the current user's detail json object.
     */
    public static void updateUserDetails(Context context, String userData){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_DETAILS, userData);
        editor.apply();
    }

    //Getting the current authentication token
    public static String getAuthToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(OAUTH_TOKEN, null);
    }

    //Used to get user preferences which stores the current user's details.
    public static SharedPreferences getUserPreferences(Context context) {
        return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    // Used to get current user's details.
    public static String getUserDetail(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_DETAILS, null);
    }

    // Updating default language.
    public static void updateDefaultLanguage(Context context, String language){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_LANGUAGE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_LANGUAGE_KEY, language);
        editor.apply();
    }

    // Used to get default language from preference
    public static String getDefaultLanguage(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_LANGUAGE_PREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(DEFAULT_LANGUAGE_KEY)) {
            return sharedPreferences.getString(DEFAULT_LANGUAGE_KEY, "en");
        }
        else {
            sharedPreferences = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
            return sharedPreferences.getString(DASHBOARD_DEFAULT_LANGUAGE, "en");
        }
    }

    /**
     * Used to update dash board menu preferences.
     * @param prefStringName - attribute to update in dashboard preferences.
     * @param data - data in string format which will be updated.
     */
    public static void updateDashBoardData(Context context, String prefStringName, String data){
        SharedPreferences mDashBoardPref = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor dashBoardEditor = mDashBoardPref.edit();
        dashBoardEditor.putString(prefStringName,data);
        dashBoardEditor.apply();
    }

    // Used to get Dashboard menus data
    public static String getDashboardMenus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DASHBOARD_MENUS, null);
    }

    // Used to get default language from preferences
    public static String getCurrentLanguage(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(CURRENT_LANGUAGE)) {
            return sharedPreferences.getString(CURRENT_LANGUAGE, "en");
        }
        else {
            return sharedPreferences.getString(DASHBOARD_DEFAULT_LANGUAGE, "en");
        }
    }

    // Used to get default location from preferences
    public static String getDefaultLocation(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DASHBOARD_DEFAULT_LOCATION, "");
    }

    // Used to display location permission popup on map
    public static boolean isLocationPermissionDisplayedOnMap(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOCATION_PERMISSION_DISPLAYED_ON_MAP, false);
    }

    // Set location permission popup displayed on map
    public static void setLocationPermissionDisplayedOnMap(Context context){
        SharedPreferences mDashBoardPref = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor dashBoardEditor = mDashBoardPref.edit();
        dashBoardEditor.putBoolean(LOCATION_PERMISSION_DISPLAYED_ON_MAP, true);
        dashBoardEditor.apply();
    }

    // get all supported languages
    public static String getLanguages(Context context){
        SharedPreferences mDashBoardPref = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        return mDashBoardPref.getString(DASHBOARD_MULTI_LANGUAGE, null);
    }

    // get all Locations
    public static String getLocations(Context context){
        SharedPreferences mDashBoardPref = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        return mDashBoardPref.getString(DASHBOARD_MULTI_LOCATION, null);
    }

    // get app tour setting
    public static int getAppTourEnabled(Context context){
        SharedPreferences mDashBoardPref = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        return Integer.parseInt(mDashBoardPref.getString(APP_TOUR_ENABLED, "0"));
    }

    // Clearing dashboard data.
    public static void clearDashboardData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DASHBOARD_MENUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /*
    For storing notification counts for feed and drawer menu
    */
    public static void updateNotificationPreferences(Context context, String msg_count,
                                                     String notification_count,
                                                     String friend_re_count,String cartCount){
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NOTIFICATION_COUNT, notification_count);
        editor.putString(MESSAGE_COUNT,msg_count);
        editor.putString(FRIEND_REQ_COUNT,friend_re_count);
        editor.putString(CART_COUNT,cartCount);
        editor.apply();
    }

    //Getting the notification counts preferences
    public static String getNotificationsCounts(Context context,String countName){
        return context.getSharedPreferences(NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE)
                .getString(countName, "0");
    }

    public static void updateCartCount(Context context,String cartCount){
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CART_COUNT,cartCount);
        editor.apply();
    }
    public static void clearNotificationsCount(Context context,String countName){
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(countName);
        editor.apply();
    }

    /* Used to clear all the stored preferences*
     *  basically used at the time of SignOut  */
    public static void clearSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(OAUTH_TOKEN);
        editor.remove(OAUTH_SECRET);
        editor.remove(USER_DETAILS);
        editor.apply();

        sharedPreferences = context.getSharedPreferences(LOGIN_INFO_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        sharedPreferences = context.getSharedPreferences(ENABLE_MODULE_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        sharedPreferences = context.getSharedPreferences(EMOJI_ENABLE_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        clearNotificationsCount(context,NOTIFICATION_COUNT);
        clearNotificationsCount(context,FRIEND_REQ_COUNT);
        clearNotificationsCount(context,MESSAGE_COUNT);
        clearNotificationsCount(context,CART_COUNT);
        clearReactionsPref(context);
        clearStickerStoreMenuPref(context);
        clearAllReactionsPref(context);
        clearTicketPref(context);
        clearVideoSourcePref(context);
        clearGreetingPref(context);

        sharedPreferences = context.getSharedPreferences(PEOPLE_SUGGESTION_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        CartData.clearCartData(context);

        sharedPreferences = context.getSharedPreferences(REPORT_AD_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        sharedPreferences = context.getSharedPreferences(MEMBER_VIEW_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        sharedPreferences = context.getSharedPreferences(APP_UPGRADE_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }


    /**
     * Used to update current selected listing type.
     * @param context Context of calling activity
     * @param key save value according to key (listingtype_id)
     * @param Label Label of current mlt.
     * @param listingId id of current mlt.
     * @param browseType browse type of browse page
     * @param viewType view type of mlt view page
     * @param mIcon icon of current mlt.
     * @param canCreate if 1 then show manage tab.
     * @param canView if false then redirect to login page for logged out user.
     */
    public static void updateCurrentListingType(Context context, int key, String Label, String singularLabel,
                                                 int listingId, int browseType, int viewType, String mIcon,
                                                 int canCreate, int packagesEnabled, boolean canView, int secondaryBrowseType){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_MLT_LABEL + key, Label);
        editor.putString(CURRENT_MLT_SINGULAR_LABEL + key, singularLabel);
        editor.putInt(CURRENT_LISTING_ID, listingId);
        editor.putInt(BROWSE_MLT_TYPE + key, browseType);
        editor.putInt(SECONDARY_BROWSE_MLT_TYPE + key, secondaryBrowseType);
        editor.putInt(VIEW_MLT_TYPE + key, viewType);
        editor.putString(CURRENT_MLT_ICON + key, mIcon);
        editor.putInt(CAN_MLT_CREATE + key, canCreate);
        editor.putInt(MLT_PACKAGES_ENABLED + key, packagesEnabled);
        editor.putBoolean(MLT_CAN_VIEW + key, canView);
        editor.apply();
    }

    //Getting the current selected listing label.
    public static String getCurrentSelectedListingLabel(Context context, int key){
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getString(CURRENT_MLT_LABEL + key, null);
    }

    //Getting the current selected listing singular label.
    public static String getCurrentSelectedListingSingularLabel(Context context, int key){
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getString(CURRENT_MLT_SINGULAR_LABEL + key, null);
    }

    //Getting the current selected listing id.
    public static int getCurrentSelectedListingId(Context context){
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getInt(CURRENT_LISTING_ID, 0);
    }

    //Getting the current selected listing icon.
    public static String getCurrentSelectedListingIcon(Context context, int key){
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getString(CURRENT_MLT_ICON + key, null);
    }

    //Getting the browse type of current selected listing.
    public static int getCurrentSelectedListingBrowseType(Context context, int key){
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getInt(BROWSE_MLT_TYPE + key, 0);
    }
    //Getting the secondary browse type of current selected listing.
    public static int getCurrentSelectedListingSecondaryBrowseType(Context context, int key){
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getInt(SECONDARY_BROWSE_MLT_TYPE + key, 0);
    }

    // Setting browse type for a listing type.
    public static void setCurrentSelectedListingId(Context context, int listingId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CURRENT_LISTING_ID, listingId);
        editor.apply();
    }

    //Getting the view type of current selected listing.
    public static int getMLTViewType(Context context, int key) {
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getInt(VIEW_MLT_TYPE + key, 0);
    }

    //Getting the create permission of current selected listing.
    public static int getMLTCanCreate(Context context, int key) {
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getInt(CAN_MLT_CREATE + key, 0);
    }

    //Getting the create permission of current selected listing.
    public static boolean canMLTView(Context context, int key) {
        return context.getSharedPreferences(CURRENT_MLT_OPTION_PREF, Context.MODE_PRIVATE)
                .getBoolean(MLT_CAN_VIEW + key, true);
    }

    // Update Current version of the app.
    public static void updateCurrentAppVersionPref(Context context, String appVersion){

        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_VERSION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_VERSION_STRING, appVersion);
        editor.apply();
    }

    //Getting the current version of the app.
    public static String getCurrentAppVersion(Context context){
        return context.getSharedPreferences(APP_VERSION_PREF, Context.MODE_PRIVATE)
                .getString(APP_VERSION_STRING, null);
    }

    // Update the Login Info Preferences and save the email and password of the logged-in user
    public static void UpdateLoginInfoPref(Context context, String email, String password, int userId){
        // Save email and base64 encrypted password in SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_EMAIL, email);
        editor.putInt(LOGIN_USER_ID, userId);
        if(password != null) {
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            String encryptedPassword = Base64.encodeToString(passwordBytes, Base64.NO_WRAP);
            editor.putString(LOGIN_PASSWORD, encryptedPassword);
        }else {
            editor.putString(LOGIN_PASSWORD, null);
        }

        editor.apply();
    }

    // Get Login Info (email and password of Login user)
    public static String getLoginInfo(Context context, String keyName){
        return context.getSharedPreferences(LOGIN_INFO_PREF, Context.MODE_PRIVATE)
                .getString(keyName, null);
    }

    // Get Login Info (email and password of Login user)
    public static int getLoginUserId(Context context){
        return context.getSharedPreferences(LOGIN_INFO_PREF, Context.MODE_PRIVATE)
                .getInt(LOGIN_USER_ID, 0);
    }

    //Updating location enabled setting
    public static void updateLocationEnabledSetting(Context context, int locationEnabled){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOCATION_SETTING_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LOCATION_ENABLED, locationEnabled);
        editor.apply();
    }

    //Checking for guest user setting
    public static boolean isLocationSettingEnabled(Context context){
        return context.getSharedPreferences(LOCATION_SETTING_PREFERENCES, Context.MODE_PRIVATE)
                .getInt(LOCATION_ENABLED, 0) == 1;
    }

    // Setting sound effect value into preferences.
    public static void setSoundEffectValue(Context context, boolean isSoundEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SOUND_SETTING_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SOUND_ENABLED, isSoundEnabled);
        editor.apply();
    }

    //Getting the sound effect value.
    public static boolean isSoundEffectEnabled(Context context) {
        return context.getSharedPreferences(SOUND_SETTING_PREF, Context.MODE_PRIVATE)
                .getBoolean(SOUND_ENABLED, false);
    }

    // Setting contact synced into preferences.
    public static void setContactSyncedInfo(Context context, boolean isContactSynced) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PEOPLE_SUGGESTION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_CONTACT_SYNCED, isContactSynced);
        editor.apply();
    }

    //Getting the contact synced info.
    public static boolean isContactSynced(Context context) {
        return context.getSharedPreferences(PEOPLE_SUGGESTION_PREF, Context.MODE_PRIVATE)
                .getBoolean(IS_CONTACT_SYNCED, false);
    }

    /**
     * Update FeedReactions/MyFeedReactions Preferences for feeds
     */
    public static void updateFeedReactionsPref(Context context, String reactionsKey, JSONObject feedReactions){

        if(feedReactions != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(FEED_REACTIONS_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(reactionsKey, feedReactions.toString());

            editor.apply();
        }
    }

    /**
     * Update FeedReactions/MyFeedReactions Preferences for feeds
     */
    public static void storeReactions(Context context, JSONObject reactions){

        if(reactions != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(REACTIONS_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(REACTIONS, reactions.toString());

            editor.apply();
        }
    }

    /**
     * Get All Reactions object
     */
    public static String getAllReactionsObject(Context context){
        return context.getSharedPreferences(REACTIONS_PREF, Context.MODE_PRIVATE)
                .getString(REACTIONS, null);
    }

    public static void clearAllReactionsPref(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(REACTIONS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Get String value from Like Shared Preferences
     */
    public static String getReactionsObject(Context context, String keyName){
        return context.getSharedPreferences(FEED_REACTIONS_PREF, Context.MODE_PRIVATE)
                .getString(keyName, null);
    }

    public static void clearReactionsPref(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(FEED_REACTIONS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public static void updateReactionsEnabledPref(Context context, int reactionsEnabled){

        SharedPreferences sharedPreferences = context.getSharedPreferences(REACTIONS_ENABLED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(REACTIONS_ENABLED, reactionsEnabled);
        editor.apply();
    }

    /**
     * Get Stickers Enabled Value
     */
    public static int getReactionsEnabled(Context context){
        return context.getSharedPreferences(REACTIONS_ENABLED_PREF, Context.MODE_PRIVATE)
                .getInt(REACTIONS_ENABLED, -1);
    }

    public static void updateStickersEnabledPref(Context context, int stickersEnabled){

        SharedPreferences sharedPreferences = context.getSharedPreferences(STICKERS_ENABLED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(STICKERS_ENABLED, stickersEnabled);
        editor.apply();
    }

    /**
     * Get Stickers Enabled Value
     */
    public static int getStickersEnabled(Context context){
        return context.getSharedPreferences(STICKERS_ENABLED_PREF, Context.MODE_PRIVATE)
                .getInt(STICKERS_ENABLED, 0);
    }

    public static void updateStickersStorePref(Context context, JSONArray stickerStoreMenuArray){

        if(stickerStoreMenuArray != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(STICKERS_STORE_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(STICKERS_STORE_MENU, stickerStoreMenuArray.toString());
            editor.apply();
        }
    }

    /**
     * Get Stickers Enabled Value
     */
    public static String getStickersStoreMenu(Context context){
        return context.getSharedPreferences(STICKERS_STORE_PREF, Context.MODE_PRIVATE)
                .getString(STICKERS_STORE_MENU, null);
    }

    public static void clearStickerStoreMenuPref(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(STICKERS_STORE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    //Updating Nestedcomments Enabled setting
    public static void updateNestedCommentEnabled(Context context, int nestedCommentEnabled){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MODULE_ENABLED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NESTED_COMMENT_ENABLED, nestedCommentEnabled);
        editor.apply();
    }

    //Checking for Nested Comment Enabled
    public static boolean isNestedCommentEnabled(Context context) {
        return context.getSharedPreferences(MODULE_ENABLED, Context.MODE_PRIVATE)
                .getInt(NESTED_COMMENT_ENABLED, 0) == 1;
    }

    /**
     * Update Site Content Cover Enabled Value
     */

    public static void updateSiteContentCoverPhotoEnabled(Context context, int contentCoverEnabled){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SITE_CONTENT_COVER_PHOTO_ENABLED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CONTENT_COVER_ENABLED, contentCoverEnabled);
        editor.apply();
    }

    /**
     * Get Site Content Cover Enabled Value
     */
    public static int getSiteContentCoverPhotoEnabled(Context context){
        return context.getSharedPreferences(SITE_CONTENT_COVER_PHOTO_ENABLED_PREF, Context.MODE_PRIVATE)
                .getInt(CONTENT_COVER_ENABLED, 0);

    }

    /**
     * Set enable module data
     */

    public static void setEnabledModuleList(Context context, String enableModule){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ENABLE_MODULE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ENABLE_MODULE_LIST, enableModule);
        editor.apply();
    }

    /**
     * Get Enable module list
     */
    public static String getEnabledModuleList(Context context){
        return context.getSharedPreferences(ENABLE_MODULE_PREF, Context.MODE_PRIVATE)
                .getString(ENABLE_MODULE_LIST, null);
    }

    // Setting emoji enabled value into preferences.
    public static void setEmojiEnablePref(Context context, int emojiEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(EMOJI_ENABLE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(EMOJI_ENABLED, emojiEnabled);
        editor.apply();
    }

    //Getting the emoji enabled value.
    public static int getEmojiEnabled(Context context) {
        return context.getSharedPreferences(EMOJI_ENABLE_PREF, Context.MODE_PRIVATE)
                .getInt(EMOJI_ENABLED, 0);
    }

    //Storing launch count of the app for Rating dialog
    public static void updateLaunchCount(Context context, int launchCount){
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(APP_LAUNCH_COUNT, launchCount);
        editor.apply();
    }

    //Getting the launch count value.
    public static int getLaunchCount(Context context) {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .getInt(APP_LAUNCH_COUNT, 0);
    }

    //Updating the never rate click pref.
    public static void updateRatePref(Context context,String buttonType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(buttonType, true);
        editor.apply();
    }

    public static boolean isAppRated(Context context,String buttonType){
        return context.getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
                .getBoolean(buttonType,false);
    }
    // Setting contacts list into preferences.
    public static void setContactList(Context context, String contactList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONTACTS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONTACT_LIST, contactList);
        editor.apply();
    }

    //Getting the contact list.
    public static String getContactList(Context context) {
        return context.getSharedPreferences(CONTACTS_PREF, Context.MODE_PRIVATE)
                .getString(CONTACT_LIST, "");
    }

    /**
     * Update Report Ads Form JsonArray
     */
    public static void updateReportAdsFormArray(Context context, JSONArray reportAdFormArray){

        if(reportAdFormArray != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(REPORT_AD_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(REPORT_ADS_ARRAY, reportAdFormArray.toString());
            editor.apply();
        }
    }

    /**
     * Get Report Ads Form JsonArray
     */
    public static String getReportAdsArray(Context context){
        return context.getSharedPreferences(REPORT_AD_PREF, Context.MODE_PRIVATE)
                .getString(REPORT_ADS_ARRAY, null);
    }

    //Updating the ticket enabled pref.
    public static void updateTicketEnabledPref(Context context, int ticketEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TICKET_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_TICKET_ENABLED, (ticketEnabled == 1));
        editor.apply();
    }

    //Updating the Adv. Event enabled pref.
    public static void updateAdvEventInfo(Context context, int canCreate, int packageEnabled, String title) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TICKET_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IS_CAN_CREATE, canCreate);
        editor.putInt(IS_PACKAGE_ENABLED, packageEnabled);
        editor.putString(ADV_EVENT_TITLE, title);
        editor.apply();
    }

    public static int getCanCreate(Context context){
        return context.getSharedPreferences(TICKET_PREF,Context.MODE_PRIVATE)
                .getInt(IS_CAN_CREATE, 0);
    }

    public static int getPackageEnabled(Context context){
        return context.getSharedPreferences(TICKET_PREF,Context.MODE_PRIVATE)
                .getInt(IS_PACKAGE_ENABLED, 0);
    }

    public static String getAdvEventTitle(Context context){
        return context.getSharedPreferences(TICKET_PREF,Context.MODE_PRIVATE)
                .getString(ADV_EVENT_TITLE, context.getResources().getString(R.string.title_activity_create_new_event));
    }

    public static boolean isTicketEnabled(Context context){
        return context.getSharedPreferences(TICKET_PREF,Context.MODE_PRIVATE)
                .getBoolean(IS_TICKET_ENABLED, false);
    }

    public static void clearTicketPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TICKET_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //Updating the video source pref.
    public static void setVideoSourcePref(Context context, boolean isMyDeviceEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(VIDEO_SOURCE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_MY_DEVICE_ENABLED, isMyDeviceEnabled);
        editor.apply();
    }

    public static boolean isMyDeviceEnabled(Context context){
        return context.getSharedPreferences(VIDEO_SOURCE_PREF,Context.MODE_PRIVATE)
                .getBoolean(IS_MY_DEVICE_ENABLED, false);
    }

    public static void clearVideoSourcePref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(VIDEO_SOURCE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Setting member view type into preferences.
    public static void updateMemberViewType(Context context, int viewType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MEMBER_VIEW_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MEMBER_VIEW_TYPE, viewType);
        editor.apply();
    }

    //Getting the member view type.
    public static int getMemberViewType(Context context) {
        return context.getSharedPreferences(MEMBER_VIEW_PREF, Context.MODE_PRIVATE)
                .getInt(MEMBER_VIEW_TYPE, 1);
    }

    // Method to set remind me later time for app upgrade popup.
    public static void setUpgradeRemindMeLaterTime(Context context, String time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_UPGRADE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_UPGRADE_REMIND, time);
        editor.apply();
    }

    // Method to set ignore for the app upgrade popup.
    public static void setAppUpgradeDialogIgnored(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_UPGRADE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(APP_UPGRADE_DIALOG_IGNORED, true);
        editor.apply();
    }

    // Returns true if the user clicked on ignore for app upgrade.
    public static boolean isAppUpgradeDialogIgnored(Context context) {
        return context.getSharedPreferences(APP_UPGRADE_PREF, Context.MODE_PRIVATE)
                .getBoolean(APP_UPGRADE_DIALOG_IGNORED, false);
    }

    // Returns the time when the user clicked on remind me later.
    public static String getAppUpgradeRemindTime(Context context) {
        return context.getSharedPreferences(APP_UPGRADE_PREF, Context.MODE_PRIVATE)
                .getString(APP_UPGRADE_REMIND, null);
    }

    // Setting video quality value into preferences.
    public static void setVideoQualityPref(Context context, int videoQuality) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(VIDEO_QUALITY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VIDEO_QUALITY, videoQuality);
        editor.apply();
    }

    //Getting the video quality value.
    public static int getVideoQuality(Context context) {
        return context.getSharedPreferences(VIDEO_QUALITY_PREF, Context.MODE_PRIVATE)
                .getInt(VIDEO_QUALITY, 0);
    }

    // Setting story duration value into preferences.
    public static void setStoryDuration(Context context, int storyDuration) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(STORY_DURATION, storyDuration);
        editor.apply();
    }

    //Getting the value of story duration
    public static int getStoryDuration(Context context) {
        return context.getSharedPreferences(STORY_PREF, Context.MODE_PRIVATE)
                .getInt(STORY_DURATION, 1);
    }

    // Setting story privacy.
    public static void setStoryPrivacy(Context context, JSONObject storyPrivacy) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORY_PRIVACY, storyPrivacy.toString());
        editor.apply();
    }

    //Getting the value of story privacy key
    public static String getStoryPrivacyKey(Context context) {
        return context.getSharedPreferences(STORY_PREF, Context.MODE_PRIVATE)
                .getString(STORY_PRIVACY_KEY, "everyone");
    }

    //Getting the value of story privacy
    public static String getStoryPrivacy(Context context) {
        return context.getSharedPreferences(STORY_PREF, Context.MODE_PRIVATE)
                .getString(STORY_PRIVACY, null);
    }

    // Setting story privacy key
    public static void setStoryPrivacyKey(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORY_PRIVACY_KEY, key);
        editor.apply();
    }

    // Setting aaf filter enabled value into preferences.
    public static void setFilterEnabled(Context context, int isFilterEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AAF_FILTER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FILTER_ENABLED, isFilterEnabled == 1);
        editor.apply();
    }

    //Getting the value filter enabled or not
    public static boolean isAAFFilterEnabled(Context context) {
        return context.getSharedPreferences(AAF_FILTER_PREF, Context.MODE_PRIVATE)
                .getBoolean(IS_FILTER_ENABLED, true);
    }

    public static int getAdvEventPackageEnabled(Context context){
        return context.getSharedPreferences(ACTION_EVENT_PREF, Context.MODE_PRIVATE)
                .getInt(IS_EVENT_ENABLED, 0);
    }

    public static void setPackageEnabled(Context context, int isEventEnabled){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACTION_EVENT_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IS_EVENT_ENABLED, isEventEnabled);
        editor.apply();
    }

    // Setting otp enabled value into preferences
    public static void setOtpEnabledOption(Context context, String otpOption){
        SharedPreferences sharedPreferences = context.getSharedPreferences(OTP_ENABLED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OTP_OPTION, otpOption);
        editor.apply();
    }

    //Getting the value otp enabled or not
    public static String getOtpEnabledOption(Context context) {
        return context.getSharedPreferences(OTP_ENABLED_PREF, Context.MODE_PRIVATE)
                .getString(OTP_OPTION, null);
    }

    // Setting otp plugin enabled value into preferences
    public static void setOtpPluginEnabled(Context context, int isOtpEnabled){
        SharedPreferences sharedPreferences = context.getSharedPreferences(OTP_PLUGIN_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_OTP_PLUGIN, isOtpEnabled == 1);
        editor.apply();
    }

    //Getting the value otp plugin enabled or not
    public static boolean isOTPPluginEnabled(Context context) {
        return context.getSharedPreferences(OTP_PLUGIN_PREF, Context.MODE_PRIVATE)
                .getBoolean(IS_OTP_PLUGIN, false);
    }

    // Setting removed greetings in pref.
    public static void setRemovedGreeting(Context context, String greetingId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AAF_GREETINGS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<String> removedList = getRemovedGreetings(context) != null
                ? getRemovedGreetings(context) : new ArrayList<>();
        removedList.add(greetingId);

        Gson gson = new Gson();
        editor.putString(AAF_GREETINGS, gson.toJson(removedList));
        editor.apply();
    }

    // Getting the list of removed greetings.
    public static ArrayList getRemovedGreetings(Context context) {
        String removedGreetings = context.getSharedPreferences(AAF_GREETINGS_PREF, Context.MODE_PRIVATE)
                .getString(AAF_GREETINGS, "");
        Gson gson = new Gson();
        return gson.fromJson(removedGreetings, ArrayList.class);
    }

    // Setting removed greetings in pref.
    public static void setRemovedBirthday(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AAF_GREETINGS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<String> removedList = getRemovedBirthdays(context) != null
                ? getRemovedBirthdays(context) : new ArrayList<>();
        removedList.add(userId);

        Gson gson = new Gson();
        editor.putString(BIRTHDAYS, gson.toJson(removedList));
        editor.apply();
    }

    // Setting status privacy key
    public static void setStatusPrivacyKey(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STATUS_PRIVACY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATUS_PRIVACY_KEY, key);
        editor.apply();
    }

    // Setting multiple options privacy key
    public static void setStatusPrivacyMultiOptions(Context context, String multiOptions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STATUS_PRIVACY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATUS_PRIVACY_MULTI_OPTIONS, multiOptions);
        editor.apply();
    }

    // Setting all status privacy options
    public static void setStatusPrivacyOptions(Context context, JSONObject statusPostPrivacy) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STATUS_PRIVACY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATUS_POST_PRIVACY_OPTIONS, statusPostPrivacy.toString());
        editor.apply();
    }

    //Getting the value of privacy key
    public static String getStatusPrivacyKey(Context context) {
        return context.getSharedPreferences(STATUS_PRIVACY_PREF, Context.MODE_PRIVATE)
                .getString(STATUS_PRIVACY_KEY, "everyone");
    }

    //Getting the multiple privacy values of privacy key
    public static String getStatusPrivacyMultiOptions(Context context) {
        return context.getSharedPreferences(STATUS_PRIVACY_PREF, Context.MODE_PRIVATE)
                .getString(STATUS_PRIVACY_MULTI_OPTIONS, null);
    }

    //Getting the status post privacy options.
    public static String getStatusPostPrivacyOptions(Context context) {
        return context.getSharedPreferences(STATUS_PRIVACY_PREF, Context.MODE_PRIVATE)
                .getString(STATUS_POST_PRIVACY_OPTIONS, null);
    }

    // Getting the list of removed greetings.
    public static ArrayList getRemovedBirthdays(Context context) {
        String removedGreetings = context.getSharedPreferences(AAF_GREETINGS_PREF, Context.MODE_PRIVATE)
                .getString(BIRTHDAYS, "");
        Gson gson = new Gson();
        return gson.fromJson(removedGreetings, ArrayList.class);
    }

    // Setting removed greetings in pref.
    public static void setCurrentDate(Context context, String currentDate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AAF_GREETINGS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GREETINGS_DATE, currentDate);
        editor.apply();
    }

    //Getting the Current date value.
    public static String getGreetingCurrentDate(Context context) {
        return context.getSharedPreferences(AAF_GREETINGS_PREF, Context.MODE_PRIVATE)
                .getString(GREETINGS_DATE, null);
    }

    public static void clearGreetingPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AAF_GREETINGS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    // Preferences for show case view work
    public static void setShowCaseViewPref(Context context, boolean navigationIcon, boolean searchBar,
                                           boolean cartIcon, boolean location, boolean searchIcon,
                                           boolean statusPost, boolean fabCreate, boolean fabMenu,
                                           boolean feedHomeIcon, boolean userProfile) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHOW_CASE_VIEW_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NAVIGATION_ICON_CASE_VIEW, navigationIcon);
        editor.putBoolean(SEARCH_BAR_CASE_VIEW, searchBar);
        editor.putBoolean(CART_ICON_CASE_VIEW, cartIcon);
        editor.putBoolean(LOCATION_ICON_CASE_VIEW, location);
        editor.putBoolean(SEARCH_ICON_CASE_VIEW, searchIcon);
        editor.putBoolean(STATUS_POST_CASE_VIEW, statusPost);
        editor.putBoolean(FAB_CREATE_CASE_VIEW, fabCreate);
        editor.putBoolean(FAB_MENU_CASE_VIEW, fabMenu);
        editor.putBoolean(FEED_HOME_ICON_SHOW_CASE_VIEW, feedHomeIcon);
        editor.putBoolean(USER_PROFILE_SHOW_CASE_VIEW, userProfile);
        editor.apply();
    }

    public static void setProfileShowCaseViewPref(Context context, boolean editProfile, boolean editPhoto,
                                                  boolean moreIcon, boolean message, boolean friends,
                                                  boolean fabCreate, boolean follow) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHOW_CASE_VIEW_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_PROFILE_EDIT_SHOW_CASE_VIEW, editProfile);
        editor.putBoolean(USER_PROFILE_EDIT_PHOTO_SHOW_CASE_VIEW, editPhoto);
        editor.putBoolean(USER_PROFILE_MORE_SHOW_CASE_VIEW, moreIcon);
        editor.putBoolean(USER_PROFILE_MESSAGE_SHOW_CASE_VIEW, message);
        editor.putBoolean(USER_PROFILE_FRIENDS_SHOW_CASE_VIEW, friends);
        editor.putBoolean(USER_PROFILE_FAB_CREATE_SHOW_CASE_VIEW, fabCreate);
        editor.putBoolean(USER_PROFILE_FOLLOW_SHOW_CASE_VIEW, follow);
        editor.apply();
    }

    public static void updateShowCaseView(Context context, String showCaseType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHOW_CASE_VIEW_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(showCaseType, true);
        editor.apply();
    }

    public static Boolean getShowCaseView(Context context, String showCaseType) {
        return context.getSharedPreferences(SHOW_CASE_VIEW_PREF, Context.MODE_PRIVATE)
                .getBoolean(showCaseType, false);
    }

}

