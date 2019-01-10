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
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ConstantVariables {
    public static final String[] INCLUDED_MODULES_ARRAY = {"group", "blog", "classified", "event",
            "user", "music", "music_playlist", "music_playlist_song", "video", "album", "poll", "core",
            "activity", "advancedactivity", "forum", "forum_topic", "siteevent_event", "sitereview_review",
            "sitereview_listing", "sitereview_wishlist", "sitereview_video", "siteevent_review" ,"sitepage_page",
            "sitepagereview_review", "sitegroup_group", "sitegroupreview_review", "album_photo",
            "sitestoreproduct_wishlist","sitestoreproduct_product", "sitestore_store","sitestoreproduct_order",
            "sitestoreproduct_review","sitestorereview_review", "core_main_diaries", "core_main_sitevideo",
            "core_main_sitevideochannel", "core_main_sitevideoplaylist", "sitevideo_channel", "sitevideo_playlist"};
    public static final String[] ADV_SEARCH_MODULE_ARRAY = { "group", "blog", "classified", "event",
            "user", "music","music_playlist","music_playlist_song", "video", "album", "poll", "forum",
            "siteevent_event", "sitereview_review", "sitereview_listing","sitepage_page", "sitegroup_group"};
public static final String[] DELETED_MODULES = {"classified","event","forum","music","poll"};
    public static final int TOTAL_INTRO_SLIDESHOW_IMAGES = 3;
    public static final int INTRO_SLIDE_SHOW_ENABLED = 0;
public static final int TOTAL_SLIDESHOW_IMAGES = 1;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final int COMMENT_LIMIT = 20;
    // MLT View type for view page
    public static final int BLOG_VIEW = 1, CLASSIFIED_VIEW_WITHOUT_CAROUSEL = 2,
            CLASSIFIED_VIEW_WITH_CAROUSEL = 3;
    // MLT View type for browse page.
    public static final int LIST_VIEW = 1, GRID_VIEW = 2, MATRIX_VIEW = 3, SLIDER_VIEW = 4, MAP_VIEW = 4;
    // Advanced Event View page gutter menus option.
    public static final int REQUEST_INVITE = 1, CANCEL_INVITE = 2, JOIN_EVENT = 3, LEAVE_EVENT = 4,
            EVENT_WAIT_LIST = 5, BOOK_EVENT_TICKETS = 6;
    //For Music Module
    public static final String ACTION_SONG_COMPLETED = "com.socialengine.music.songCompleted";
    public static final String ACTION_SONG_PREPARED = "com.socialengine.music.songPrepared";
    public static final String ACTION_UPDATE_SONG = "com.socialengine.music.updateProgress";
    //For Home Page
    public static final String ACTION_VIEW_ALL_MESSAGES = "com.socialengine.messages.viewAll";
    public static final String ACTION_VIEW_ALL_NOTIFICATIONS = "com.socialengine.notification.viewAll";
    //For feed notifications
    public static final String ACTION_FEED_NOTIFICATIONS = "com.socialengine.home.action.feeds.notifications";
    public static final String ACTION_COUNTER_UPDATE = "com.socialengine.home.action.feeds.notifications.counter.update";
    // For Status post options.
    public static final Map<String, Object> STATUS_POST_OPTIONS = new HashMap<>();
    public static final String FEED_DECORATION = "feed_decoration";
    public static final String WORD_STYLING = "word_styling";
    public static final String ON_THIS_DAY = "on_this_day";
    public static final String BANNER_DECORATION = "banner_decoration";
    public static final String FEELING_ACTIVITY = "feeling_activity";
    public static final String DEFAULT_HASHTAG_COLOR = "#000000";
    //Request codes for starting activities
    public static final int CREATE_REQUEST_CODE = 2;
    public static final int PAGE_EDIT_CODE = 3;
    public static final int STORY_VIEW_PAGE_CODE = 60;
    public static final int ADD_VIDEO_CODE = 231;
    public static final int USER_PRIVACY_REQUEST_CODE = 85;
    public static final int FEELING_ACTIVITY_REQUEST_CODE = 84;
    public static final int CHILD_FEELING_REQUEST_CODE = 83;
    public static final int STATUS_FORM_CODE = 82;
    public static final int VIEW_PAGE_CODE = 5;
    public static final int VIEW_LIGHT_BOX = 8;
    public static final int LIGHT_BOX_DELETE = 9;
    public static final int LIGHT_BOX_EDIT = 10;
    public static final int MESSAGE_DELETED = 11;
    public static final int MESSAGE_CREATED = 12;
    public static final int FEED_REQUEST_CODE = 13;
    public static final int MESSAGE_VIEW_PAGE = 14;
    public static final int ADD_PEOPLE_CODE = 15;
    public static final int CHECK_IN_CODE = 16;
    public static final int ALBUM_VIEW_PAGE = 17;
    public static final int EDITOR_REQUEST_CODE = 18;
    public static final int EDIT_ENTRY_RETURN_CODE = 19;
    public static final int CHECK_IN_RESULT_CODE = 20;
    public static final int VERTICAL_ITEM_SPACE = 21;
    public static final int VIEW_POLL_REQUEST_CODE = 22;
    public static final int VIEW_PAGE_EDIT_CODE = 23;
    public static final int WAITING_MEMBERS_CODE = 24;
    public static final int OUTSIDE_SHARING_CODE = 25;
    public static final int SIGN_UP_CODE = 20;
    public static final int SIGN_UP_WEBVIEW_CODE = 21;
    public static final int PACKAGE_WEBVIEW_CODE = 22;
    public static final int LOCATION_UPDATE_CODE = 33;
    public static final int VIEW_COMMENT_PAGE_CODE = 35;
    public static final int VIEW_SINGLE_FEED_PAGE = 36;
    public static final int POSTED_NEW_FEED = 39;
    public static final int OVERVIEW_REQUEST_CODE = 38;
    public static final int VIEW_MULTI_IMAGE_COMMENT_PAGE_CODE = 79;
    // PERMISSION RESULT REQUEST CONSTANTS
    public static final int PERMISSION_CAMERA = 27;
    public static final int READ_EXTERNAL_STORAGE = 28;
    public static final int WRITE_EXTERNAL_STORAGE = 29;
    public static final int ACCESS_FINE_LOCATION = 30;
    public static final int ACCESS_COARSE_LOCATION = 31;
    public static final int READ_CONTACTS = 40;
    public static final int PERMISSION_WAKE_LOCK = 331;
    public static final int SEND_SMS = 332;
    public static final int PERMISSION_GPS_SETTINGS = 191;
    public static final int REQUEST_STORY_IMAGE_VIDEO = 800;
    public static final int REQUEST_IMAGE = 300;
    public static final int REQUEST_VIDEO = 400;
    public static final int REQUEST_VIDEO_CODE = 401;
    public static final int REQUEST_MUSIC = 500;
    public static final int REQUEST_STORY_POST = 111;
    public static final int VIDEO_CAPTURE_CODE = 700;
    public static final int FRAGMENT_LOAD_CODE = 34;
    public static final int UPDATE_REQUEST_CODE = 32;
    public static final int ADV_GROUPS_INVITE_REQUEST = 33;
    public static final int STICKER_STORE_REQUEST = 34;
    public static final int FILTER_PAGE_CODE = 35;
    public static final int ORDER_PAGE_CODE = 36;
    public static final int WISHLIST_CREATE_CODE = 37;
public static final int WEBVIEW_ENABLE = 1;
    public static final int REQUEST_CANCLED = 416;
    public static final int CONTACT_INFO_CODE = 110;
    public static final String IS_CONTENT_EDITED = "ContentEdited";
    public static final String USER_ID = "user_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_VALUE = "category_value";
    public static final String CATEGORY_FORUM_TOPIC = "category_forum_topic";
    public static final String EXTRA_CREATE_RESPONSE = "CreateViewDetails";
    public static final String VIDEO_TYPE = "VideoType";
    public static final String VIDEO_URL = "VideoUrl";
    public static final String VIEW_ID = "ViewId";
    public static final String EXTRA_MODULE_TYPE = "ModuleName";
    public static final String IS_SEARCHED_FROM_DASHBOARD = "dashboardSearch";
    public static final String FOOTER_TYPE = "footer";
    public static final String HEADER_TYPE = "header";
    //For push notifications
    // Google Project Number
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // Request Time
    public final static int REFRESH_NOTIFICATION_TIME = 80000;
    public final static int FIRST_COUNT_REQUEST_DELAY = 40000;
    //Swipe Refresh Layout disable request time
    public final static int REFRESH_DELAY_TIME = 1000;
    //Request code for fragment loading in twitter
    public static final int TWITTER_REQUEST_CODE = 140;
    public static final int PHOTO_LIGHT_BOX_WORD_LIMIT = 160;
    public static final String STORY_DESCRIPTION = "story_description";
    public static final String STORY_VIDEO_THUMB = "story_video_thumb";
    public static final String STORY_DURATION = "story_duration";
    public static float IMAGE_SCALE_FACTOR = 1.5f;
    public static final int FILE_UPLOAD_LIMIT = 10;
    public static final int IS_REDIRECTED_FROM_EVENT_PROFILE = 111;
    public static final int IS_REDIRECTED_RESULT_CODE = 0;
    // View Page Parameters
    public static final String VIEW_PAGE_URL = "viewUrl";
    public static final String VIEW_PAGE_ID = "content_id";
    public static final String ALBUM_ID = "album_id";
    /**
     *     Modules Title
     */
    public static final String HOME_MENU_TITLE = "home";
    public static final String NOTIFICATION_MENU_TITLE = "core_mini_notification";
    public static final String FRIEND_REQUEST_MENU_TITLE = "core_mini_friend_request";
    public static final String GLOBAL_SEARCH_MENU_TITLE = "core_main_global_search";
    public static final String USER_SETTINGS_MENU_TITLE = "user_settings";
    public static final String CONTACT_US_MENU_TITLE = "contact_us";
    public static final String PRIVACY_POLICY_MENU_TITLE = "privacy_policy";
    public static final String TERMS_OF_SERVICE_MENU_TITLE = "terms_of_service";
    public static final String MULTI_LANGUAGES_MENU_TITLE = "core_multi_languages";
    public static final String LOCATION_MENU_TITLE = "seaocore_location";
    public static final String SPREAD_THE_WORD_MENU_TITLE = "spread_the_word";
    public static final String WISHLIST_MENU_TITLE = "core_main_wishlist";
    public static final String COMET_CHAT_MENU_TITLE = "core_main_cometchat";
    public static final String AAF_MENU_TITLE = "activity_action";
    public static final String RATE_US_MENU_TITLE = "core_main_rate";
    public static final String CONTACT_INFO_MENU_TITLE = "contact_info";
    public static final String APP_TOUR_MENU_TITLE = "core_main_app_tour";
    public static final String SIGN_UP_FIELDS = "signup_fields";
    //Message titles
    public static final String MESSAGE_MENU_TITLE = "core_mini_messages";
    public static final String MESSAGE_CONVERSATION_MENU_TITLE = "messages_conversation";
    //Blog titles
    public static final String BLOG_MENU_TITLE = "core_main_blog";
    public static final String BLOG_TITLE = "blog";
    // Classified titles
    public static final String CLASSIFIED_MENU_TITLE = "core_main_classified";
    public static final String CLASSIFIED_TITLE = "classified";
    // Group titles
    public static final String GROUP_MENU_TITLE = "core_main_group";
    public static final String GROUP_TITLE = "group";
    // Event titles
    public static final String EVENT_MENU_TITLE = "core_main_event";
    public static final String EVENT_TITLE = "event";
    // Music titles
    public static final String MUSIC_MENU_TITLE = "core_main_music";
    public static final String MUSIC_TITLE = "music";
    public static final String MUSIC_PLAYLIST_TITLE = "music_playlist";
    public static final String MUSIC_PLAYLIST_SONG_TITLE = "music_playlist_song";
    // Album titles
    public static final String ALBUM_MENU_TITLE = "core_main_album";
    public static final String ALBUM_TITLE = "album";
    public static final String ALBUM_PHOTO_MENU_TITLE = "album_photo";
    // Poll titles
    public static final String POLL_MENU_TITLE = "core_main_poll";
    public static final String POLL_TITLE = "poll";
    // Video titles
    public static final String VIDEO_MENU_TITLE = "core_main_video";
    public static final String VIDEO_TITLE = "video";
    // Forum titles
    public static final String FORUM_MENU_TITLE = "core_main_forum";
    public static final String FORUM_TITLE = "forum";
    public static final String FORUM_TOPIC_MENU_TITLE = "forum_topic";
    public static final String FORUM_POST_MENU_TITLE = "forum_post";
    // MLT titles
    public static final String MLT_MENU_TITLE = "sitereview_listing";
    public static final String MLT_TITLE = "sitereview";
    public static final String MLT_VIDEO_MENU_TITLE = "sitereview_video";
    public static final String MLT_PHOTO_MENU_TITLE = "sitereview_photo";
    public static final String MLT_REVIEW_MENU_TITLE = "sitereview_review";
    public static final String MLT_WISHLIST_MENU_TITLE = "sitereview_wishlist";
    // Advanced event titles
    public static final String ADVANCED_EVENT_MENU_TITLE = "core_main_siteevent";
    public static final String ADVANCED_EVENT_TITLE = "siteevent";
    public static final String ADV_EVENT_REVIEW_MENU_TITLE = "siteevent_review";
    public static final String ADV_EVENT_VIDEO_MENU_TITLE = "siteevent_video";
    public static final String ADV_EVENT_MENU_TITLE = "siteevent_event";
    public static final String ADV_EVENT_DIARY_MENU_TITLE = "adv_event_diary";
    public static final String DIARY_MENU_TITLE = "core_main_diaries";
    public static final String ADV_EVENT_TICKET_MENU_TITLE = "show_available_tickets";
    public static final String ADV_EVENT_PAYMENT_METHOD = "adv_event_payment_method";
    public static final String ADV_EVENT_ADD_VIDEO = "add_video_siteevent";
    public static final String SITE_PAGE_ADD_VIDEO = "add_video_sitepage";
    public static final String SITE_STORE_ADD_VIDEO = "add_video_sitestore";
    public static final String ADV_GROUPS_ADD_VIDEO = "add_video_sitegroup";
    public static final String ADV_EVENT_TICKET_ORDER = "siteeventticket_order";
    // Site page titles
    public static final String SITE_PAGE_MENU_TITLE = "sitepage";
    public static final String SITE_PAGE_TITLE = "sitepage_page";
    public static final String SITE_PAGE_TITLE_MENU = "core_main_sitepage";
    public static final String SITE_PAGE_REVIEW_MENU_TITLE = "sitepagereview_review";
    public static final String SITE_PAGE_REVIEW_TITLE = "sitepage_review";
    public static final String SITE_PAGE_PHOTO_MENU_TITLE = "sitepage_photo";
    public static final String SITE_PAGE_ALBUM_MENU_TITLE = "sitepage_album";
    // Advanced group titles
    public static final String ADV_GROUPS_MENU_TITLE = "core_main_sitegroup";
    public static final String ADV_GROUPS_TITLE = "sitegroup";
    public static final String ADVANCED_GROUPS_MENU_TITLE = "sitegroup_group";
    public static final String ADV_GROUPS_REVIEW_MENU_TITLE = "sitegroupreview_review";
    public static final String ADV_GROUPS_REVIEW_TITLE = "sitegroup_review";
    public static final String ADV_GROUPS_VIDEO_MENU_TITLE = "sitegroupvideo_video";
    public static final String ADV_GROUPS_ALBUM_MENU_TITLE = "sitegroup_album";
    // Advanced Video titles
    public static final String ADV_VIDEO_MENU_TITLE = "core_main_sitevideo";
    public static final String ADV_VIDEO_TITLE = "sitevideo";
    public static final String ADV_VIDEO_CHANNEL_MENU_TITLE = "core_main_sitevideochannel";
    public static final String SITE_VIDEO_CHANNEL_MENU_TITLE = "sitevideo_channel";
    public static final String ADV_VIDEO_PLAYLIST_MENU_TITLE = "core_main_sitevideoplaylist";
    public static final String SITE_VIDEO_PLAYLIST_MENU_TITLE = "sitevideo_playlist";
    // User titles
    public static final String USER_MENU_TITLE = "core_main_user";
    public static final String USER_TITLE = "user";
    // Store titles
    public static final String STORE_MENU_TITLE = "core_main_sitestore";
    public static final String STORE_TITLE = "sitestore";
    public static final String SITE_STORE_MENU_TITLE = "sitestore_store";
    public static final String STORE_REVIEW_MENU_TITLE = "sitestorereview_review";
    public static final String SITE_STORE_VIDEO_MENU_TITLE = "sitestorevideo_video";
    public static final String STORE_ORDER_MENU_TITLE = "sitestore_orders";
    public static final String STORE_OFFER_MENU_TITLE = "core_main_sitestoreoffer";
    // Store product titles
    public static final String PRODUCT_MENU_TITLE = "core_main_sitestoreproduct";
    public static final String PRODUCT_TITLE = "sitestore_product";
    public static final String PRODUCT_CART_MENU_TITLE = "core_main_sitestoreproduct_cart";
    public static final String SITE_PRODUCT_MENU_TITLE = "sitestoreproduct_product";
    public static final String SITE_PRODUCT_REVIEW_MENU_TITLE = "sitestoreproduct_review";
    public static final String PRODUCT_WISHLIST_MENU_TITLE = "sitestoreproduct_wishlist";
    public static final String PRODUCT_ORDER_MENU_TITLE = "sitestoreproduct_order";
    public static final String PRODUCT_VIDEO_MENU_TITLE = "sitestoreproduct_video";
    public static final String SITE_PRODUCT_ORDER_MENU_TITLE = "core_main_sitestoreproduct_orders";
    public static final String SITE_PRODUCT_WISHLIST_MENU_TITLE = "core_main_sitestoreproduct_wishlists";
    public static final String OFFER_MENU_TITLE = "core_main_offers";
    // Create Url Parameter
    public static final String CREATE_URL = "createUrl";
    public static final String URL_STRING = "URL";
    public static final String EDIT_URL_STRING = "editURL";
    // Intent Parameters
    public static final String SIGN_UP_ACCOUNT = "signup_account";
    public static final String SUBSCRIPTION_ACCOUNT = "subscription_account";
    public static final String SETTING_NOTIFICATIONS = "settings_notifications";
    public static final String SETTING_SUBSCRIPTION_TITLE = "settings_subscription";
    public static final String SETTING_PRIVACY = "settings_privacy";
    public static final String NOTIFICATION_SETTING = "notification_settings";
    public static final String EDIT_MEMBER_PROFILE = "edit_member_profile";
    public static final String PAYMENT_METHOD = "payment_method";
    public static final String PAYMENT_METHOD_CONFIG = "payment_method_config";
    public static final String CREATE_REVIEW = "create_review";
    public static final String UPDATE_REVIEW = "update_review";
    public static final String BUYER_FORM = "buyer_form";
    public static final String COMPOSE_MESSAGE = "compose_message";
    public static final String ADD_TO_WISHLIST = "add_wishlist";
    public static final String ADD_TO_DIARY = "add_to_diary";
    public static final String ADD_TO_FRIEND_LIST = "add_to_friend_list";
    public static final String OTP_VERIFICATION = "otp_verification";
    public static final String AAF_VIDEO = "aaf_video";
    public static final String HASTAG_SEARCH = "hashtagsearch";
    public static final String SUBJECT_TYPE = "subject_type";
    public static final String SUBJECT_ID = "subject_id";
    public static final String STORY_ID = "story_id";
    public static final String VIDEO_SUBJECT_TYPE = "video_subject_type";
    public static final String ACTION_ID = "action_id";
    public static final String FRAGMENT_NAME = "fragmentName";
    public static final String TOTAL_ITEM_COUNT = "totalItemCount";
    public static final String IS_WAITING = "isWaiting";
    public static final String INVITE_GUEST = "inviteGuestUrl";
    public static final String PROFILE_RSVP_VALUE = "profile_rsvp_value";
    public static final String CAN_UPLOAD = "canUpload";
    public static final String CAN_UPLOAD_VIDEO = "canUploadVideo";
    public static final String RESPONSE_OBJECT = "responseObject";
    public static final String OVERVIEW = "OverView";
    public static final String OVERVIEW_KEY = "overview";
    public static final String CONTENT_TITLE = "content_title";
    public static final String TITLE = "title";
    public static final String MODULE_NAME = "moduleName";
    public static final String ITEM_POSITION = "position";
    public static final String ATTACH_VIDEO = "attach_video";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String CONTENT_ID = "content_id";
    public static final String LISTING_ID = "listing_id";
    public static final String LISTING_TYPE_ID = "listingtype_id";
    public static final String TAB_LABEL = "tab_label";
    public static final String MLT_VIEW_TYPE = "mlt_view_type";
    public static final String FORM_TYPE = "form_type";
    public static final String MENU_ARRAY = "menu_array";
    public static final String PROFILE_TYPE = "profile_type";
    public static final String PHOTO_LIST = "photo_list";
    public static final String IS_PHOTO_UPLOADED = "is_photo_upload_request";
    public static final String USER_PROFILE_COVER_BUNDLE = "user_profile_cover_bundle";
    public static final String CAN_EDIT = "canEdit";
    public static final String PHOTO_REQUEST_URL = "photoRequestUrl";
    public static final String SHOW_OPTIONS = "optionvisibility";
    public static final String ENABLE_COMMENT_CACHE = "commentcaching";
    public static final String SHOW_ALBUM_BUTTON = "ViewAlbum";
    public static final String CHANGE_DEFAULT_LOCATION = "default_location";
    public static final String IS_PROFILE_PAGE_REQUEST = "profile_page_request";
    public static final String IS_FIRST_TAB_REQUEST = "first_tab_request";
    public static final String IS_CATEGORY_BASED_RESULTS = "categoryBasedResults";
    public static final String PHOTO_COMMENT_COUNT = "photoCommentCount";
    public static final String IS_PHOTO_COMMENT = "is_photo_comment";
    public static final String FEED_LIST = "feed_list";
    public static final String IS_MULTI_PHOTO_FEED = "is_multi_photo_feed";
    public static final String LIKE_COMMENT_URL = "like_comment_url";
    public static final String PHOTO_POSITION = "photo_position";
    public static final String IS_LIKED = "isLike";
    public static final String PHOTO_LIKE_COUNT = "photoLikeCount";
    public static final String IS_ALBUM_PHOTO_REQUEST = "is_album_photo_request";
    public static final String TICKET_CREATE = "ticket_create";
    public static final String TICKET_DETAILS_FRAGMENT_NAME = "ticket_details";
    public static final String TICKET_DETAILS_FORM_TYPE = "ticket_info";
    public static final String PAYMENT_CONFIG_METHOD = "payment_config_method";
    public static final String SUGGESTION = "suggestion";
    public static final String IS_OWNER = "is_owner";
    public static final String IS_GUEST = "is_guest";
    //Activity feed post length
    public static final int FEED_TITLE_BODY_LENGTH = 300;
    // User Profile Code
    public static final int USER_PROFILE_CODE = 100;
    //Content Cover Edit Code
    public static final int CONTENT_COVER_EDIT_CODE = 171;
    // WebView Activity Request Code
    public static final int WEB_VIEW_ACTIVITY_CODE = 600;
    //Payment failed Request Code
    public static final int PAYMENT_FAILED_ACTIVITY_CODE = 601;
    //Payment success Request code
    public static final int PAYMENT_SUCCESS_ACTIVITY_CODE = 602;
    /**
     * People suggestion settings
     */
public static final int ENABLE_PEOPLE_SUGGESTION = 1;
public static final int PEOPLE_SUGGESTION_POSITION = 5;
public static final int PEOPLE_SUGGESTION_LIMIT = 3;
    /**
     * Story settings
     */
public static final int ENABLE_STORY = 0;
    public static final int STORY_WORD_LIMIT = 150;
    public static final int STORY_POST_COUNT_LIMIT = 5;
    /**
     *  Top header setting.
     */
public static final int SHOW_APP_TITLE_IN_HEADER = 0;
    /***
     * Gif animation time
     */
public static final int GIF_ANIMATION_TIME = 4;
    /*
    * Ad mob implementation settings
    */
    /**
     * Check which Ads are enabled on site
     * ENABLE_ADMOB ? 0 => Facebook Ads
     * ENABLE_ADMOB ? 1 => Google Ads.
     *
     * Check for each plugin which type of Ad is enabled
     * 0 => Facebook Ads, 1 => Google Ads, 2 => Community/Ads
     * 3 => Ad Campaigns, 4 => Sponsored stories
     */
public static final int ENABLE_ADMOB = 0;
    public static final int ENABLE_FEED_ADS = 0;
    public static final int ENABLE_CLASSIFIED_ADS = 0;
    public static final int ENABLE_MUSIC_ADS = 0;
    public static final int ENABLE_MLT_ADS = 0;
    public static final int ENABLE_BLOG_ADS = 0;
    public static final int ENABLE_ALBUM_ADS = 0;
    public static final int ENABLE_EVENT_ADS = 0;
    public static final int ENABLE_GROUPS_ADS = 0;
    public static final int ENABLE_POLL_ADS = 0;
    public static final int ENABLE_FORUM_ADS = 0;
    public static final int ENABLE_VIDEO_ADS = 0;
    public static final int ENABLE_ADV_EVENT_ADS = 0;
    public static final int ENABLE_SITE_PAGE_ADS = 0;
    public static final int ENABLE_ADV_GROUPS_ADS = 0;
    public static final int ENABLE_ADV_VIDEO_ADS = 0;
    public static final int FEED_ADS_TYPE = ENABLE_ADMOB;
    public static final int CLASSIFIED_ADS_TYPE = ENABLE_ADMOB;
    public static final int MUSIC_ADS_TYPE = ENABLE_ADMOB;
    public static final int MLT_ADS_TYPE = ENABLE_ADMOB;
    public static final int BLOG_ADS_TYPE = ENABLE_ADMOB;
    public static final int ALBUM_ADS_TYPE = ENABLE_ADMOB;
    public static final int EVENT_ADS_TYPE = ENABLE_ADMOB;
    public static final int GROUPS_ADS_TYPE = ENABLE_ADMOB;
    public static final int POLL_ADS_TYPE = ENABLE_ADMOB;
    public static final int FORUM_ADS_TYPE = ENABLE_ADMOB;
    public static final int VIDEO_ADS_TYPE = ENABLE_ADMOB;
    public static final int ADV_EVENT_ADS_TYPE = ENABLE_ADMOB;
    public static final int SITE_PAGE_ADS_TYPE = ENABLE_ADMOB;
    public static final int ADV_GROUPS_ADS_TYPE = ENABLE_ADMOB;
    public static final int ADV_VIDEO_ADS_TYPE = ENABLE_ADMOB;
    public static final int FEED_ADS_POSITION = 7;
    public static final int CLASSIFIED_ADS_POSITION = 7;
    public static final int MUSIC_ADS_POSITION = 7;
    public static final int MLT_ADS_POSITION = 7;
    public static final int BLOG_ADS_POSITION = 7;
    public static final int ALBUM_ADS_POSITION = 7;
    public static final int EVENT_ADS_POSITION = 7;
    public static final int GROUPS_ADS_POSITION = 7;
    public static final int POLL_ADS_POSITION = 7;
    public static final int FORUM_ADS_POSITION = 7;
    public static final int VIDEO_ADS_POSITION = 7;
    public static final int ADV_GROUPS_ADS_POSITION = 7;
    public static final int ADV_EVENT_ADS_POSITION = 7;
    public static final int SITE_PAGE_ADS_POSITION = 7;
    public static final int ADV_VIDEO_ADS_POSITION = 7;
    public static final int DEFAULT_AD_COUNT = 15;
    public static final int DEFAULT_TICKETS_COUNT = 10;
    public static final int TYPE_FACEBOOK_ADS = 0;
    public static final int TYPE_GOOGLE_ADS = 1;
    public static final int TYPE_COMMUNITY_ADS = 2;
    public static final int TYPE_SPONSORED_STORIES = 4;
public static final String COMETCHAT_PACKAGE_NAME = "";
    public static final String REQUEST_CODE = "requestCode";
    public static final String REACTION_NAME = "reaction";
    public static final String REACTION_RESPONSE = "reaction_response";
    public static JSONObject STICKERS_STORE_ARRAY = new JSONObject();
    public static final String ADV_VIDEO_INTEGRATED = "is_adv_video_integrated";
    public static final String ADV_VIDEOS_COUNT = "adv_videos_count";
    public static final String MY_PHOTO_REACTIONS = "my_photo_reactions";
    public static final String PHOTO_POPULAR_REACTIONS = "photo_popular_reactions";
    public static final String ADD_PRODUCT = "add_product";
    public static final String SELECT_PRODUCT = "select_product";
    public static final String EDIT_PRODUCT = "edit_product";
    public static final String DELETE_PRODUCT = "delete_product";
    public static final String UPGRADE_PACKAGE = "upgrade_package";
    public static final int UPGRADE_PACKAGE_CODE = 119;
    public static final int SELECT_PRODUCT_RETURN_CODE = 129;
    public static final int SELECT_FILE_RETURN_CODE = 139;
    public static final String EDIT_STORE = "edit_store";
    public static final String SHIPPING_METHOD  = "shipping_method";
    public static final String EDIT_METHOD = "edit_method";
    public static final String NETWORK_NAME = "network";
    public static final String NETWORK_RESPONSE = "network_response";
public static final boolean SHOW_CART_ICON = false;
    public static final int TWO_FACTOR_ACTIVITY_CODE = 41;
    public static final int TWO_FACTOR_VIEW_PAGE = 42;
    public static final String SERVER_SETTINGS_KEY = "server_settings";
    public static final Map<String, Object> SERVER_SETTINGS = new HashMap<>();
    public static final String UPLOAD_MAX_SIZE = "upload_max_size_limit";
    public static final String USER_QUOTA_LIMIT = "rest_space";
    public static final String VALID_FILE_SIZE = "validFileSize";
    public static final int ON_BACK_PRESSED_CODE = 999;
    public static final String IS_MANAGE_VIEW = "is_manage_view";
    public static final String PROFILE_QUICK_INFO = "profile_quick_info";
    public static final int PACKAGE_VIEW_CODE = 121;
    public static final String DEFAULT_IMAGE_PATH = "externals/images";
    public static final String TAG_VIDEO_LIGHT_BOX = "videoLightBox";
    public static final String KEY_SHARE_TYPE_MEDIA = "shareTypeMedia";
    public static final int VALUE_FAST_FORWARD_INCREMENT = 3000;
    public static final int VALUE_REWIND_INCREMENT = 3000;
    /**
     * Create session for user
     */
    public static final int CODE_USER_CREATE_SESSION = 7000;
    public static final String KEY_USER_CREATE_SESSION = "user_create_session";
}
