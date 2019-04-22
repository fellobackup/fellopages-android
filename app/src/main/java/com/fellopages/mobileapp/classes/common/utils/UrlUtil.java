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

import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

public class UrlUtil {

    /* Default urls used for loading pages */

    public static final String LOGIN_URL = AppConstant.DEFAULT_URL+ "login?subscriptionForm=1";

    public static final String LOGIN_OTP_URL = AppConstant.DEFAULT_URL+ "otpverifier/send?";

    public static final String SIGNUP_URL = AppConstant.DEFAULT_URL + "signup?oauth_consumer_key=" +
            AppConstant.oauth_consumer_key  + "&oauth_consumer_secret=" + AppConstant.oauth_consumer_secret;

    public static final String SIGNUP_OTP_SEND_URL = AppConstant.DEFAULT_URL+ "otpverifier/verify-mobileno?subscriptionForm=1";

    public static final String DASHBOARD_URL = AppConstant.DEFAULT_URL + "get-dashboard-menus";

    public static final String MEMBER_LIST_URL = AppConstant.DEFAULT_URL +"members/index/get-lists";

    // Album Module
    public static final String BROWSE_ALBUM_URL = AppConstant.DEFAULT_URL + "albums";
    public static final String MANAGE_ALBUM_URL = AppConstant.DEFAULT_URL + "albums/manage?limit=" +
            AppConstant.LIMIT + "&addPhotoLink=1" ;
    public static final String ALBUM_VIEW_PAGE = AppConstant.DEFAULT_URL + "albums/view/";
    public static final String ALBUM_VIEW_URL = AppConstant.DEFAULT_URL + "albums/view-album?gutter_menu=1&subject_id=";

    // Music Module
    public static final String BROWSE_MUSIC_URL = AppConstant.DEFAULT_URL + "music?limit=" + AppConstant.LIMIT;
    public static final String MANAGE_MUSIC_URL = AppConstant.DEFAULT_URL + "music/manage?limit=" +
            AppConstant.LIMIT;

    // Blog Module
    public static final String BROWSE_BLOG_URL = AppConstant.DEFAULT_URL + "blogs?limit=" + AppConstant.LIMIT;
    public static final String MANAGE_BLOG_URL = AppConstant.DEFAULT_URL + "blogs/manage?limit=" +
            AppConstant.LIMIT;

    // Classified Module
    public static final String BROWSE_CLASSIFIED_URL = AppConstant.DEFAULT_URL + "classifieds?limit=" +
            AppConstant.LIMIT;
    public static final String MANAGE_CLASSIFIED_URL = AppConstant.DEFAULT_URL + "classifieds/manage?limit=" +
            AppConstant.LIMIT;

    // Event Module
    public static final String UPCOMING_EVENT_URL = AppConstant.DEFAULT_URL + "events?filter=future" +
            "&limit=" + AppConstant.LIMIT;

    public static final String PAST_EVENT_URL = AppConstant.DEFAULT_URL + "events?filter=past" +
            "&limit=" + AppConstant.LIMIT;

    public static final String MANAGE_EVENT_URL = AppConstant.DEFAULT_URL + "events/manage?limit=" +
            AppConstant.LIMIT;

    public static final String BROWSE_EVENT_URL = AppConstant.DEFAULT_URL + "events?limit=" + AppConstant.LIMIT;

    // Group Module
    public static final String BROWSE_GROUP_URL = AppConstant.DEFAULT_URL + "groups?limit=" + AppConstant.LIMIT;

    public static final String MANAGE_GROUP_URL = AppConstant.DEFAULT_URL + "groups/manage?limit=" +
            AppConstant.LIMIT;


    //Video Module
    public static final String BROWSE_VIDEO_URL = AppConstant.DEFAULT_URL + "videos";

    public static final String MANAGE_VIDEO_URL = AppConstant.DEFAULT_URL + "videos/manage?limit=" +
            AppConstant.LIMIT;
    public static final String VIDEO_VIEW_URL = AppConstant.DEFAULT_URL +"videos/view/";

    // Poll Module
    public static final String BROWSE_POLL_URL = AppConstant.DEFAULT_URL + "polls?limit=" + AppConstant.LIMIT;

    public static final String MANAGE_POLL_URL = AppConstant.DEFAULT_URL + "polls/manage?limit=" +
            AppConstant.LIMIT;
    public static final String POLL_VIEW_URL = AppConstant.DEFAULT_URL + "polls/view/";

    // Message Module
    public static final String MESSAGE_INBOX_URL = AppConstant.DEFAULT_URL + "messages/inbox?limit=" +
            AppConstant.LIMIT;
    public static final String MESSAGE_READ_URL =  AppConstant.DEFAULT_URL + "messages/mark-message-read-unread";

    public static final String MESSAGE_VIEW_URL =  AppConstant.DEFAULT_URL + "messages/view/id/";

    public static final String MESSAGE_SENTBOX_URL = AppConstant.DEFAULT_URL + "messages/outbox?limit=" +
            AppConstant.LIMIT;

    public static final String MESSAGE_DELETE_URL = AppConstant.DEFAULT_URL + "messages/delete";

    // Get Friends Url
    public static final String GET_FRIENDS_LIST = AppConstant.DEFAULT_URL + "advancedactivity/friends/suggest";

    // Get Users Url
    public static final String GET_USERS_LIST = AppConstant.DEFAULT_URL + "user/suggest?message=1";

    // Notifications
    public static final String MAIN_NOTIFICATION_URL = AppConstant.DEFAULT_URL + "notifications?myRequests=0" +
            "&object=1&type=android&limit=" + AppConstant.LIMIT;

    public static final String NOTIFICATION_READ_URL =  AppConstant.DEFAULT_URL + "notifications/markread";
    public static final String NOTIFICATION_ALL_READ_URL =  AppConstant.DEFAULT_URL + "notifications/markallread";
    public static final String MY_REQUEST_URL = AppConstant.DEFAULT_URL + "notifications?myRequests=1&" +
            "object=1&limit=" + AppConstant.LIMIT ;
    public static final String REQUEST_READ_URL =  AppConstant.DEFAULT_URL + "notifications/mark-friend-request-read";

    public static final String GROUP_REQUEST_ACCEPT_URL = AppConstant.DEFAULT_URL + "groups/member/accept/";
    public static final String GROUP_REQUEST_IGNORE_URL = AppConstant.DEFAULT_URL + "groups/member/ignore/";
    public static final String EVENT_REQUEST_ACCEPT_URL = AppConstant.DEFAULT_URL + "events/member/accept/";
    public static final String EVENT_REQUEST_IGNORE_URL = AppConstant.DEFAULT_URL + "events/member/ignore/";

    // Friend Request
    public static final String FRIEND_REQUEST_URL = AppConstant.DEFAULT_URL + "notifications/friend-request?" +
            "limit=" + AppConstant.LIMIT ;
    public static final String USER_CONFIRM_URL = AppConstant.DEFAULT_URL + "user/confirm";
    public static final String USER_REJECT_URL = AppConstant.DEFAULT_URL + "user/reject";

    // Account Settings Url
    public static final String ACCOUNT_SETTINGS = AppConstant.DEFAULT_URL + "get-user-account-menu";

    // AAF
    public static final String FFEDS_URL = AppConstant.DEFAULT_URL + "advancedactivity/feeds" +
            "?object_info=1&getAttachedImageDimention=0&feed_filter=1&post_elements=1" + "&post_menus=1";


    public static final String AAF_LIKE_COMMNET_URL = AppConstant.DEFAULT_URL + "advancedactivity/feeds/likes-comments?" +
            "viewAllComments=1" + "&limit=" + ConstantVariables.COMMENT_LIMIT ;
    public static final String AAF_VIEW_LIKES_URL = AppConstant.DEFAULT_URL + "advancedactivity/feeds/likes-comments?" +
            "viewAllLikes=1";

    //Comment and like urls
    public static final String LIKE_COMMENT_URL = AppConstant.DEFAULT_URL + "likes-comments?viewAllComments=1" +
            "&limit=" + ConstantVariables.COMMENT_LIMIT ;
    public static final String VIEW_LIKES_URL = AppConstant.DEFAULT_URL + "likes-comments?viewAllLikes=1";


    //Advanced Events
    public static final String BROWSE_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedevents?";

    public static final String MANAGE_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedevents/manage?" +
            "limit=" + AppConstant.LIMIT;

    public static final String BROWSE_DIARIES_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedevents/diaries?" +
            "limit=" + AppConstant.LIMIT;

    public static final String BROWSE_CATEGORIES_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedevents/categories?showCount=1";

    public static final String BROWSE_CALENDAR_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedevents/calender?" +
            "limit=" + AppConstant.LIMIT;

    public static final String MY_TICKETS_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedeventtickets/tickets/index?" +
            "limit=" + AppConstant.LIMIT;

    public static final String BROWSE_COUPONS_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedeventtickets/coupons/index?limit="
            + AppConstant.LIMIT;

    public static final String USERS_INFO_ORDER_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedeventtickets/order/buyer-details?";


    public static final String PAYMENT_METHOD_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedeventtickets/order/checkout?";

    public static final String PLACED_ORDER_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedeventtickets/order/place-order?";

    public static final String ORDER_VIEW_PAGE_ADV_EVENTS_URL = AppConstant.DEFAULT_URL + "advancedeventtickets/order/view?";

    public static final String PAYMENT_METHOD_CONFIG_UPDATE_URL = AppConstant.DEFAULT_URL + "advancedeventtickets/order/set-event-gateway-info?";

    // Forgot Password
    public static final String FORGOT_PASSWORD_URL = AppConstant.DEFAULT_URL + "forgot-password";

    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    // Multiple Listing Type
    public static final String BROWSE_MLT_URL = AppConstant.DEFAULT_URL + "listings?";
    public static final String MANAGE_MLT_URL = AppConstant.DEFAULT_URL + "listings/manage?getGutterMenu=1" +
            "&limit=" + AppConstant.LIMIT;
    public static final String BROWSE_CATEGORIES_MLT_URL = AppConstant.DEFAULT_URL + "listings/categories?showCount=1";
    public static final String BROWSE_WISHLIST_URL = AppConstant.DEFAULT_URL + "listings/wishlist/browse?limit=" +
            AppConstant.LIMIT;

    // Directory Pages
    public static final String BROWSE_SITE_PAGE_URL = AppConstant.DEFAULT_URL + "sitepages/browse";
    public static final String MANAGE_SITE_PAGE_URL = AppConstant.DEFAULT_URL + "sitepages/manage?" +
            "limit=" + AppConstant.LIMIT;
    public static final String CATEGORY_SITE_PAGE_URL = AppConstant.DEFAULT_URL + "sitepages/category?showCount=1";
    public static final String POPULAR_SITE_PAGE_URL = AppConstant.DEFAULT_URL + "sitepages/browse?" +
            "limit=" + AppConstant.LIMIT;

    // Adv Event Package List url
    public static final String ADV_EVENTS_PACKAGE_LIST_URL = AppConstant.DEFAULT_URL + "advancedevents/packages?limit=" +
            AppConstant.LIMIT;




    public static final String MLT_PACKAGE_LIST_URL = AppConstant.DEFAULT_URL + "listings/packages?limit=" +
            AppConstant.LIMIT;


    //HashTag browse Url
    public static final String BROWSE_HASHTAG_URL = AppConstant.DEFAULT_URL + "sitehashtag/browse?";

    //Site Pages Package List url
    public static final String SITE_PAGE_PACKAGE_LIST_URL = AppConstant.DEFAULT_URL + "sitepages/packages?limit=" +
            AppConstant.LIMIT;

    // Group/Communities Plugin
    public static final String ADV_GROUP_BROWSE_PAGE_URL = AppConstant.DEFAULT_URL + "advancedgroups";
    public static final String ADV_GROUP_MANAE_PAGE_URL = AppConstant.DEFAULT_URL + "advancedgroups/manage?limit=" +
            AppConstant.LIMIT;
    public static final String ADV_GROUP_CATEGORY_HOME_PAGE_URL = AppConstant.DEFAULT_URL + "advancedgroups/category?showCount=1";
    public static final String ADV_GROUP_PACKAGE_LIST_URL = AppConstant.DEFAULT_URL + "advancedgroups/packages?limit=" +
            AppConstant.LIMIT;


    //Store Plugin Start
    public static final String BROWSE_STORE_URL = AppConstant.DEFAULT_URL + "sitestore/browse?limit="
            + AppConstant.LIMIT;

    public static final String MANAGE_STORE_URL = AppConstant.DEFAULT_URL + "sitestore/manage?limit="
            + AppConstant.LIMIT;
    public static final String BROWSE_PRODUCTS_URL = AppConstant.DEFAULT_URL + "sitestore/product/browse";
    public static final String MANAGE_PRODUCTS_URL = AppConstant.DEFAULT_URL + "sitestore/product/manage";

    public static final String PRODUCT_VIEW_URL = AppConstant.DEFAULT_URL + "sitestore/product/view/";

    public static final String STORE_VIEW_URL =  AppConstant.DEFAULT_URL + "sitestore/view/";

    public static final String CART_VIEW_URL = AppConstant.DEFAULT_URL + "sitestore/cart";

    public static final String CHECKOUT_ADDRESS_URL = AppConstant.DEFAULT_URL + "sitestore/checkout/address?store_id=";
    public static final String SHIPPING_METHOD_URL = AppConstant.DEFAULT_URL + "sitestore/checkout/shipping?store_id=";
    public static final String PAYMENT_OPTION_URL = AppConstant.DEFAULT_URL + "sitestore/checkout/payment?store_id=";
    public static final String ADD_TO_CART_URL = AppConstant.DEFAULT_URL + "sitestore/product/add-to-cart/";
    public static final String UPDATE_CART_URL = AppConstant.DEFAULT_URL + "sitestore/cart/update-quantity";
    public static final String DELETE_PRODUCT_URL = AppConstant.DEFAULT_URL + "sitestore/cart/delete-product/";
    public static final String GET_STATE_URL = AppConstant.DEFAULT_URL +"sitestore/checkout/states?store_id=";
    public static final String POST_BILL_SHIP_URL = AppConstant.DEFAULT_URL + "sitestore/checkout/address?store_id=";
    public static final String VALIDATE_ORDER_URL =  AppConstant.DEFAULT_URL + "sitestore/checkout/validating-order";
    public static final String STORE_OFFERS_URL = AppConstant.DEFAULT_URL + "sitestore/offers/index";
    public static final String ADD_TO_WISHLIST_STORE = AppConstant.DEFAULT_URL + "sitestore/product/wishlist/add?product_id=";
    public static final String REMOVE_FROM_WISHLIT = AppConstant.DEFAULT_URL + "sitestore/product/wishlist/remove";
    public static final String PRODUCT_VARIATIONS_URL = AppConstant.DEFAULT_URL + "sitestore/product/variation-option?";
    public static final String MY_ORDERS_URL = AppConstant.DEFAULT_URL + "sitestore/orders?";
    public static final String BROWSE_DOWNLOADABLE_URL = AppConstant.DEFAULT_URL + "sitestore/orders/downloadable-files?";
    public static final String ORDER_VIEW_URL =  AppConstant.DEFAULT_URL +"sitestore/orders/view/";
    public static final String BROWSE_PRODUCT_CATEGORIES = AppConstant.DEFAULT_URL +"sitestore/product/category";
    public static final String PRODUCT_WISHLIST_URL =  AppConstant.DEFAULT_URL + "sitestore/product/wishlist/browse?limit=" +
            AppConstant.LIMIT;
    public static final String STORE_CATEGORY_URL =  AppConstant.DEFAULT_URL + "sitestore/category?limit=" +
            AppConstant.LIMIT;
    public static final String STORE_PACKAGE_LIST_URL = AppConstant.DEFAULT_URL + "sitestore/package?limit=" +
            AppConstant.LIMIT;


    public static final String PRODUCT_WISHLIST_VIEW_URL = AppConstant.DEFAULT_URL +"sitestore/product/wishlist/";
    public static final String PRODUCT_FILTER_URL = AppConstant.DEFAULT_URL + "sitestore/product/product-search-form";
    public static final String PRODUCT_BROWSE_REVIEW_URL =  AppConstant.DEFAULT_URL+"sitestore/product/review/browse";
    public static final String PRODUCT_REVIEW_LIKE_COMMENT_URL = AppConstant.DEFAULT_URL +"sitestore/product/review/list-comments/";
    public static final String STORE_UPDATE_REVIEW_URL = AppConstant.DEFAULT_URL + "sitestore/review/edit/";


    public static final String AAF_VIEW_REACTIONS_URL  = AppConstant.DEFAULT_URL + "reactions/reactions";
    public static final String AAF_VIEW_STICKERS_URL  = AppConstant.DEFAULT_URL + "reactions/stickers";
    public static final String BROWSE_STICKERS_STORES_URL  = AppConstant.DEFAULT_URL + "reactions/store";
    public static final String ADD_STICKERS_STORE_URL  = AppConstant.DEFAULT_URL + "reactions/store/add";
    public static final String REMOVE_STICKERS_STORE_URL  = AppConstant.DEFAULT_URL + "reactions/store/remove";

    public static final String CONTENT_REACTIONS_URL = AppConstant.DEFAULT_URL + "reactions/content-reaction?" +
            "getReaction=1";
    public static final String NESTED_COMMENTS_VIEW_URL  = AppConstant.DEFAULT_URL + "advancedcomments/likes-comments" +
            "?viewAllComments=1" + "&limit=" + ConstantVariables.COMMENT_LIMIT;

    public static final String VIEW_PHOTOS_URL  = AppConstant.DEFAULT_URL + "albums/view-content-album";

    // People Suggestion url.
    public static final String PEOPLE_SUGGESTION_URL = AppConstant.DEFAULT_URL + "suggestions/suggestion-listing?limit=";
    public static final String MEMBERSHIP_DEFAULT_URL = AppConstant.DEFAULT_URL + "members/get-contact-list-members";

    // FCM token update url.
    public static final String UPDATE_FCM_TOKEN_URL  = AppConstant.DEFAULT_URL + "user/update-fcm-token";

    // Adv Videos Urls
    public static final String IS_SITEVIDEO_ENABLED = AppConstant.DEFAULT_URL + "is-sitevideo-plugin-enabled";

    // Get cover image menu url
    public static final String GET_COVER_MENU_URL = AppConstant.DEFAULT_URL + "coverphoto/get-cover-photo-menu?";

    //
    public static final String UPLOAD_COVER_PHOTO_URL = AppConstant.DEFAULT_URL + "coverphoto/upload-cover-photo?";
    public static final String UPLOAD_USER_COVER_PHOTO_URL = AppConstant.DEFAULT_URL + "user/profilepage/upload-cover-photo/";

    public static final String GET_COMMUNITY_ADS_URL = AppConstant.DEFAULT_URL + "communityads/index/index";

    public static final String REMOVE_COMMUNITY_ADS_URL = AppConstant.DEFAULT_URL + "communityads/index/remove-ad";

    // Add tag in photos
    public static final String ADD_TAG_URL = AppConstant.DEFAULT_URL + "tags/add?";

    // Remove tags in photos
    public static final String REMOVE_TAG_URL = AppConstant.DEFAULT_URL + "tags/remove?";

    // Advanced video urls.
    public static final String ADV_VIDEO_BROWSE_URL = AppConstant.DEFAULT_URL + "advancedvideos/browse";
    public static final String ADV_VIDEO_MANAGE_URL = AppConstant.DEFAULT_URL + "advancedvideos/manage?limit="
            + AppConstant.LIMIT;
    public static final String BROWSE_ADV_VIDEO_CATEGORIES = AppConstant.DEFAULT_URL +"advancedvideos/categories";
    public static final String ADV_VIDEO_VIEW_URL = AppConstant.DEFAULT_URL +"advancedvideo/view/";
    public static final String ADV_VIDEO_CHANNEL_BROWSE_URL = AppConstant.DEFAULT_URL + "advancedvideos/channel/browse";
    public static final String ADV_VIDEO_CHANNEL_MANAGE_URL = AppConstant.DEFAULT_URL + "advancedvideos/channel/manage?limit="
            + AppConstant.LIMIT;
    public static final String BROWSE_ADV_VIDEO_CHANNEL_CATEGORIES = AppConstant.DEFAULT_URL +"advancedvideos/channel/categories";
    public static final String ADV_VIDEO_PLAYLIST_BROWSE_URL = AppConstant.DEFAULT_URL + "advancedvideos/playlist/browse";
    public static final String ADV_VIDEO_PLAYLIST_MANAGE_URL = AppConstant.DEFAULT_URL + "advancedvideos/playlist/manage?limit="
            + AppConstant.LIMIT;

    //Otp plugin url
    public static final String TWO_FACTOR_GET_VERIFICATION_URL = AppConstant.DEFAULT_URL + "/otpverifier/add-mobileno";
    public static final String TWO_FACTOR_ADD_MOBILE_URL = AppConstant.DEFAULT_URL + "otpverifier/add-mobileno";
    public static final String TWO_FACTOR_EDIT_MOBILE_URL = AppConstant.DEFAULT_URL + "otpverifier/edit-mobileno";
    public static final String TWO_FACTOR_DELETE_MOBILE_URL = AppConstant.DEFAULT_URL + "otpverifier/delete-mobileno";
    public static final String TWO_FACTOR_ENABLE_DISABLE_URL= AppConstant.DEFAULT_URL + "otpverifier/enable-verification";
    public static final String TWO_FACTOR_OTP_VERIFICATION_URL = AppConstant.DEFAULT_URL + "otpverifier/code-verification";

    public static final String FORGOT_PASSWORD_OTP_URL = AppConstant.DEFAULT_URL + "otpverifier/forgot-password?subscriptionForm=1";
    public static final String FORGOT_PASSWORD_OTP_VERIFY_URL = AppConstant.DEFAULT_URL + "otpverifier/verify?subscriptionForm=1";
    public static final String FORGOT_PASSWORD_RESET_URL = AppConstant.DEFAULT_URL + "otpverifier/reset?subscriptionForm=1";

    // Member followers url
    public static final String MEMBER_FOLLOWERS_URL = AppConstant.DEFAULT_URL + "advancedmember/followers?";
    public static final String MEMBER_FOLLOWING_URL = AppConstant.DEFAULT_URL + "advancedmember/following?";
    // Server Configuration URL
    public static final String SERVER_SETTINGS_URL = AppConstant.DEFAULT_URL + "get-server-settings?";
    //Contact Information url
    public static final String CONTACT_INFORMATION_URL = AppConstant.DEFAULT_URL + "sitepage/get-contact/";
}
