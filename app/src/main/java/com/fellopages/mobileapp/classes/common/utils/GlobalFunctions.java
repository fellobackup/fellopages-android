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
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.security.ProviderInstaller;
import com.fellopages.mobileapp.BuildConfig;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.fragments.ModulesHomeFragment;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedHomeFragment;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedsFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsUtil;
import com.fellopages.mobileapp.classes.modules.advancedGroups.AdvGroupUtil;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoUtil;
import com.fellopages.mobileapp.classes.modules.album.AlbumUtil;
import com.fellopages.mobileapp.classes.modules.blog.BlogUtil;
import com.fellopages.mobileapp.classes.modules.classified.ClassifiedUtil;
import com.fellopages.mobileapp.classes.modules.directoryPages.SitePageUtil;
import com.fellopages.mobileapp.classes.modules.event.EventUtil;
import com.fellopages.mobileapp.classes.modules.forum.ForumUtil;
import com.fellopages.mobileapp.classes.modules.friendrequests.FriendRequests;
import com.fellopages.mobileapp.classes.modules.group.GroupUtil;
import com.fellopages.mobileapp.classes.modules.messages.CreateNewMessage;
import com.fellopages.mobileapp.classes.modules.messages.MessageViewActivity;
import com.fellopages.mobileapp.classes.modules.multipleListingType.MLTUtil;
import com.fellopages.mobileapp.classes.modules.music.MusicUtil;
import com.fellopages.mobileapp.classes.modules.offers.BrowseOffersFragment;
import com.fellopages.mobileapp.classes.modules.poll.PollUtil;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;
import com.fellopages.mobileapp.classes.modules.user.BrowseMemberFragment;
import com.fellopages.mobileapp.classes.modules.user.staticpages.FooterMenusFragment;
import com.fellopages.mobileapp.classes.modules.video.VideoUtil;
import com.fellopages.mobileapp.classes.modules.wishlist.ProductWishList;
import com.fellopages.mobileapp.classes.modules.wishlist.WishListHomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;

import android.view.View;
import android.util.TypedValue;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fellopages.mobileapp.classes.core.ConstantVariables.SERVER_SETTINGS;


// Generalize functions helpers
public class GlobalFunctions {

    // Checking the network connectivity
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Returns the ip address of the device
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (NullPointerException | SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Thrown when Google Play Services is not installed, up-to-date, or enabled
     * Show dialog to allow users to install, update, or otherwise enable Google Play services.
     */
    public static void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(callingActivity);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.showErrorDialogFragment(e.getConnectionStatusCode(), callingActivity, null, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                }
            });
        } catch (GooglePlayServicesNotAvailableException e) {
            LogUtils.LOGD("SecurityException", "Google Play Services not available.");
        }
    }


    public static boolean isSslEnabled() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Method to get currency symbol with price.
     *
     * @param isoCurrencyCode currency code.
     * @param amount          price.
     * @return return the formatted currency.
     */
    public static String getFormattedCurrencyString(String isoCurrencyCode, double amount) {

        NumberFormat currencyFormat = null;
        // This formats currency values as the user expects to read them (default locale).
        currencyFormat = NumberFormat.getCurrencyInstance();

        if (currencyFormat != null) {

            // This specifies the actual currency that the value is in, and provides the currency symbol.
            Currency currency = Currency.getInstance(isoCurrencyCode);
            // Note we don't supply a locale to this method - uses default locale to format the currency symbol.
            String symbol = currency.getSymbol();

            /*
             * Checking amount has non zero values after decimal points or not
             * if yes then use decimal format with amount as double value
             * otherwise change it to integer
            */

            if (amount % 1 != 0) {
                // We then tell our formatter to use this symbol.
                DecimalFormatSymbols decimalFormatSymbols = ((java.text.DecimalFormat) currencyFormat).getDecimalFormatSymbols();
                decimalFormatSymbols.setCurrencySymbol(symbol);
                ((java.text.DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);

                return currencyFormat.format(amount);
            } else {
                return symbol + (int) amount;
            }
        }
        return null;
    }

    /**
     * Method to return html data.
     *
     * @param context  context of calling activity.
     * @param bodyHTML html data.
     * @return return formatted html data.
     */
    public static String getHtmlData(Context context, String bodyHTML, boolean isJustify) {
        String head = "<head><meta name=\"viewport\" " +
                "content=\"width=device-width, " +
                "initial-scale=1.0, " +
                "minimum-scale=1.0, " +
                "maximum-scale=1.0, " +
                "user-scalable=0\">" +
                "<style>img { margin: 0px 0 0px 0;\n" +
                "    min-width: 30px;\n" +
                "    min-height: 30px;\n" +
                "    max-width: 100%;\n" +
                "    opacity:1;}" +
                "iframe { margin: 0px 0 0px 0;\n" +
                "    max-width: 100%;\n" +
                "    opacity:1;}" +
                "@font-face {font-family: '" + context.getResources().getString(R.string.font_family_body_1_material) +
                "';src: url('file:///android_asset/fonts/" + context.getResources().getString(R.string.default_font_name) +
                "');}" +
                "body {font-family: '" + context.getResources().getString(R.string.font_family_body_1_material) +
                "';}" +
                "</style></head>";

        if (bodyHTML != null && !bodyHTML.isEmpty()) {
            bodyHTML.replaceAll("\n\n", "<p>");
        }
        if (isJustify) {
            return "<html>" + head + "<body style=\"margin: 15px 15px 15px 15px;text-align:justify;\">" + bodyHTML + "</body></html>";

        } else {
            return "<html>" + head + "<body style=\"margin: 15px 15px 15px 15px;\">" + bodyHTML + "</body></html>";
        }
    }

    /**
     * This is used to check the given URL is valid or not.
     *
     * @param url url which need to be check.
     * @return return true if url is valid.
     */

    public static boolean isValidUrl(String url) {
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    /**
     * Method is used for checking valid email id format.
     *
     * @param email Email address which need to be check.
     * @return boolean true for valid false for invalid.
     */
    public static boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Method to find real path of the uploaded video file.
     *
     * @param context    Context of Class.
     * @param contentUri Uri for which path is to be find.
     * @return returns the real path of the video file.
     */
    public static String getPathFromUri(Context context, Uri contentUri) {
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            cursor.close();
            return filePath;
        } else {
            return contentUri.getPath();
        }
    }

    /**
     * Method to find real path of the uploaded music file.
     *
     * @param context    Context of Class.
     * @param contentUri Uri for which path is to be find.
     * @return returns the real path of the music file.
     */
    public static String getMusicFilePathFromURI(Context context, Uri contentUri) {

        String[] projection = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Method to show all music files when click on music icon.
     */
    public static void addMusicBlock(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(intent, ConstantVariables.REQUEST_MUSIC);
    }

    /**
     * Method to get minute difference between two dates.
     *
     * @param startDatetime Start time.
     * @param endDateTime   End time.
     * @return Returns the difference between start and end time in minutes.
     */
    public static int minutesDifferenceFromEndDate(String startDatetime, String endDateTime) {
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        String startDateFormat = "yyyy-MM-dd HH:mm", endDateFormat = "yyyy-MM-dd HH:mm";
        if (startDatetime.length() <= 10) {
            startDateFormat = "yyyy-MM-dd";
        }
        if (endDateTime.length() <= 10) {
            endDateFormat = "yyyy-MM-dd";
        }
        try {
            startTime.setTime(new SimpleDateFormat(startDateFormat, Locale.getDefault()).parse(startDatetime)); // Parse into Date object
            endTime.setTime(new SimpleDateFormat(endDateFormat, Locale.getDefault()).parse(endDateTime)); // Parse into Date object
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long differenceInMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        long differenceInMinutes = (differenceInMillis) / 1000L / 60L; // Divide by millis/sec, secs/min
        return (int) differenceInMinutes;
    }

    /* Method to get second difference between two dates.
            *
            * @param startDatetime Start time.
     * @param endDateTime   End time.
     * @return Returns the difference between start and end time in seconds.
            */
    public static int secondsDifferenceFromEndDate(String startDatetime, String endDateTime) {
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        String startDateFormat = "yyyy-MM-dd HH:mm:ss", endDateFormat = "yyyy-MM-dd HH:mm:ss";
        if (startDatetime.length() <= 10) {
            startDateFormat = "yyyy-MM-dd";
        }
        if (endDateTime.length() <= 10) {
            endDateFormat = "yyyy-MM-dd";
        }
        try {
            startTime.setTime(new SimpleDateFormat(startDateFormat, Locale.getDefault()).parse(startDatetime)); // Parse into Date object
            endTime.setTime(new SimpleDateFormat(endDateFormat, Locale.getDefault()).parse(endDateTime)); // Parse into Date object
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long differenceInMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        long differenceInMinutes = (differenceInMillis) / 1000L / 60L; // Divide by millis/sec, secs/min
        long differenceInSeconds = (differenceInMillis) / 1000L ;

        return (int) differenceInSeconds;
    }

    /**
     * Used to get current fragment to be loaded according to the module name for guest user.
     *
     * @param currentSelectedModule current module selected by user
     */
    public static Fragment getGuestUserFragment(String currentSelectedModule) {
        Fragment loadFragment = null;
        switch (currentSelectedModule) {
            case ConstantVariables.BLOG_MENU_TITLE:
                loadFragment = BlogUtil.getBrowsePageInstance();
                break;

            case ConstantVariables.CLASSIFIED_MENU_TITLE:
                loadFragment = ClassifiedUtil.getBrowsePageInstance();
                break;

            case ConstantVariables.ALBUM_MENU_TITLE:
                loadFragment = AlbumUtil.getBrowsePageInstance();
                break;

            case ConstantVariables.GROUP_MENU_TITLE:
                loadFragment = GroupUtil.getBrowsePageInstance();
                break;

            case ConstantVariables.VIDEO_MENU_TITLE:
                loadFragment = VideoUtil.getBrowsePageInstance();
                break;

            case ConstantVariables.MUSIC_MENU_TITLE:
                loadFragment = MusicUtil.getBrowsePageInstance();
                break;

            case ConstantVariables.FORUM_MENU_TITLE:
                loadFragment = ForumUtil.getHomePageInstance();
                break;

            case ConstantVariables.POLL_MENU_TITLE:
                loadFragment = PollUtil.getBrowsePageInstance();
                break;

            case ConstantVariables.HOME_MENU_TITLE:
                loadFragment = new FeedsFragment();
                break;

            case ConstantVariables.USER_MENU_TITLE:
                loadFragment = new BrowseMemberFragment();
                break;

            case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                loadFragment = MLTUtil.getBrowseWishListPageInstance();
                break;

            case ConstantVariables.WISHLIST_MENU_TITLE:
                loadFragment = new WishListHomeFragment();
                break;

            case ConstantVariables.STORE_MENU_TITLE:
                loadFragment = StoreUtil.getBrowseStoreInstance();
                break;

            case ConstantVariables.STORE_OFFER_MENU_TITLE:
                loadFragment = BrowseOffersFragment.newInstance();
                break;

            case ConstantVariables.SITE_PRODUCT_WISHLIST_MENU_TITLE:
                loadFragment = StoreUtil.getBrowseWishListPageInstance();
                break;

            case ConstantVariables.SITE_PRODUCT_ORDER_MENU_TITLE:
                loadFragment = StoreUtil.getOrderHomeFragment();
                break;

            case ConstantVariables.CONTACT_US_MENU_TITLE:
            case ConstantVariables.PRIVACY_POLICY_MENU_TITLE:
            case ConstantVariables.TERMS_OF_SERVICE_MENU_TITLE:
                loadFragment = new FooterMenusFragment();
                break;

            case ConstantVariables.DIARY_MENU_TITLE:
                loadFragment = AdvEventsUtil.getDiariesPageInstance();
                break;

            case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                loadFragment = AdvVideoUtil.getPlaylistBrowsePageInstance();
                break;

            default:
                loadFragment = new ModulesHomeFragment();
                break;

        }
        return loadFragment;
    }

    /**
     * Used to get current fragment to be loaded according to the module name for loggedIn user.
     *
     * @param currentSelectedModule current module selected by user
     * @param mStoreWishListEnabled
     * @param mMLTWishListEnabled
     */
    public static Fragment getAuthenticateUserFragment(String currentSelectedModule,
                                                       int mStoreWishListEnabled, int mMLTWishListEnabled) {

        Fragment loadFragment = null;

        switch (currentSelectedModule) {
            case ConstantVariables.FORUM_MENU_TITLE:
                loadFragment = ForumUtil.getHomePageInstance();
                break;
            case ConstantVariables.SAVE_FEEDS:
            case ConstantVariables.HOME_MENU_TITLE:
                if (currentSelectedModule.equals(ConstantVariables.SAVE_FEEDS)){
                    loadFragment = new FeedHomeFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putBoolean("isSaveFeeds", true);
                    loadFragment.setArguments(bundle2);
                } else {
                    loadFragment = new FeedHomeFragment();
                }

                break;
            case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                loadFragment = MLTUtil.getBrowseWishListPageInstance();
                break;
            case ConstantVariables.CONTACT_US_MENU_TITLE:
            case ConstantVariables.PRIVACY_POLICY_MENU_TITLE:
            case ConstantVariables.TERMS_OF_SERVICE_MENU_TITLE:
                loadFragment = new FooterMenusFragment();
                break;
            case ConstantVariables.USER_MENU_TITLE:
                loadFragment = new BrowseMemberFragment();
                break;
            case ConstantVariables.FRIEND_REQUEST_MENU_TITLE:
                loadFragment = new FriendRequests();
                break;
            case "browse_diaries_siteevent":
                loadFragment = AdvEventsUtil.getBrowseDiariesInstance();
                break;
            case ConstantVariables.STORE_OFFER_MENU_TITLE:
                loadFragment = BrowseOffersFragment.newInstance();
                break;
            case ConstantVariables.WISHLIST_MENU_TITLE:
                if (mStoreWishListEnabled == 1 && mMLTWishListEnabled == 1) {
                    loadFragment = new WishListHomeFragment();
                } else if (mStoreWishListEnabled == 1) {
                    loadFragment = new ProductWishList();
                } else {
                    loadFragment = MLTUtil.getBrowseWishListPageInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_WISHLIST_MENU_TITLE);
                    loadFragment.setArguments(bundle);
                }
                break;
            case ConstantVariables.SITE_PRODUCT_ORDER_MENU_TITLE:
                loadFragment = StoreUtil.getOrderHomeFragment();
                break;
            case ConstantVariables.DIARY_MENU_TITLE:
                loadFragment = AdvEventsUtil.getDiariesPageInstance();
                break;
            default:
                loadFragment = new ModulesHomeFragment();
                break;
        }
        return loadFragment;
    }

    /**
     * Retrieve the Icon according to the module name.
     *
     * @param itemName - module name for which we are setting the font icon
     */
    public static String getItemIcon(String itemName) {
        String icon;
        if (itemName != null) {
            switch (itemName) {
                case ConstantVariables.HOME_MENU_TITLE:
                    icon = "\uf015";
                    break;
                case ConstantVariables.GLOBAL_SEARCH_MENU_TITLE:
                    icon = "\uf002";
                    break;
                case ConstantVariables.MESSAGE_MENU_TITLE:
                    icon = "\uf0e0";
                    break;
                case ConstantVariables.NOTIFICATION_MENU_TITLE:
                    icon = "\uf0f3";
                    break;
                case ConstantVariables.FRIEND_REQUEST_MENU_TITLE:
                    icon = "\uf1d8";
                    break;
                case ConstantVariables.USER_MENU_TITLE:
                    icon = "\uf007";
                    break;
                case ConstantVariables.ALBUM_MENU_TITLE:
                    icon = "\uf03e";
                    break;
                case ConstantVariables.BLOG_MENU_TITLE:
                    icon = "\uf040";
                    break;
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    icon = "\uf0c0";
                    break;
                case ConstantVariables.EVENT_MENU_TITLE:
                    icon = "\uf073";
                    break;
                case ConstantVariables.CLASSIFIED_MENU_TITLE:
                    icon = "\uf1ea";
                    break;
                case ConstantVariables.VIDEO_MENU_TITLE:
                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    icon = "\uf03d";
                    break;
                case ConstantVariables.MUSIC_MENU_TITLE:
                    icon = "\uf001";
                    break;
                case ConstantVariables.FORUM_MENU_TITLE:
                    icon = "\uf086";
                    break;
                case ConstantVariables.MLT_MENU_TITLE:
                    icon = "\uf03a";
                    break;
                case ConstantVariables.WISHLIST_MENU_TITLE:
                case ConstantVariables.SITE_PRODUCT_WISHLIST_MENU_TITLE:
                case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                    icon = "\uf046";
                    break;
                case ConstantVariables.USER_SETTINGS_MENU_TITLE:
                    icon = "\uf013";
                    break;
                case ConstantVariables.CONTACT_US_MENU_TITLE:
                    icon = "\uf095";
                    break;
                case ConstantVariables.PRIVACY_POLICY_MENU_TITLE:
                    icon = "\uf023";
                    break;
                case ConstantVariables.TERMS_OF_SERVICE_MENU_TITLE:
                    icon = "\uf15c";
                    break;
                case ConstantVariables.MULTI_LANGUAGES_MENU_TITLE:
                    icon = "\uf0ac";
                    break;
                case ConstantVariables.LOCATION_MENU_TITLE:
                    icon = "\uf041";
                    break;
                case ConstantVariables.POLL_MENU_TITLE:
                    icon = "\uf080";
                    break;
                case ConstantVariables.SPREAD_THE_WORD_MENU_TITLE:
                    icon = "\uf045";
                    break;
                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    icon = "\uf073";
                    break;
                case "signout":
                    icon = "\uf011";
                    break;
                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                case ConstantVariables.SITE_PAGE_TITLE_MENU:
                    icon = "\uf15c";
                    break;

                case ConstantVariables.STORE_MENU_TITLE:
                    icon = "\uf290";
                    break;
                case ConstantVariables.PRODUCT_MENU_TITLE:
                    icon = "\uF291";
                    break;
                case ConstantVariables.STORE_OFFER_MENU_TITLE:
                    icon = "\uf15c";
                    break;
                case ConstantVariables.SITE_PRODUCT_ORDER_MENU_TITLE:
                    icon = "\uf187";
                    break;
                case ConstantVariables.PRODUCT_CART_MENU_TITLE:
                    icon = "\uf290";
                    break;
                case "core_main_cometchat":
                    icon = "\uf27a";
                    break;
                case ConstantVariables.DIARY_MENU_TITLE:
                    icon = "\uf02d";
                    break;
                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    icon = "\uf233";
                    break;
                case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                    icon = "\uf16a";
                    break;
                default:
                    icon = "\uf08b";
            }
        } else {
            icon = "\uf08b";
        }
        return icon;
    }

    /**
     * Retrieve the Icon background color according to the module name.
     *
     * @param itemName - module name for which we are setting the font icon background color
     */
    public static int getIconBackgroundColor(String itemName) {
        String icon;
        int colorHex = 0;
        if (itemName != null) {
            switch (itemName) {
                case ConstantVariables.HOME_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName1;
                    break;
                case ConstantVariables.GLOBAL_SEARCH_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName2;
                    break;
                case ConstantVariables.MESSAGE_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName3;
                    break;
                case ConstantVariables.NOTIFICATION_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName4;
                    break;
                case ConstantVariables.FRIEND_REQUEST_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName5;
                    break;
                case ConstantVariables.USER_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName6;
                    break;
                case ConstantVariables.ALBUM_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName7;
                    break;
                case ConstantVariables.BLOG_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName1;
                    break;
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName2;
                    break;
                case ConstantVariables.EVENT_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName3;
                    break;
                case ConstantVariables.CLASSIFIED_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName4;
                    break;
                case ConstantVariables.VIDEO_MENU_TITLE:
                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName5;
                    break;
                case ConstantVariables.MUSIC_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName6;
                    break;
                case ConstantVariables.FORUM_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName7;
                    break;
                case ConstantVariables.MLT_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName8;
                    break;
                case ConstantVariables.WISHLIST_MENU_TITLE:
                case ConstantVariables.SITE_PRODUCT_WISHLIST_MENU_TITLE:
                case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName1;
                    break;
                case ConstantVariables.USER_SETTINGS_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName2;
                    break;
                case ConstantVariables.CONTACT_US_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName3;
                    break;
                case ConstantVariables.PRIVACY_POLICY_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName4;
                    break;
                case ConstantVariables.TERMS_OF_SERVICE_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName5;
                    break;
                case ConstantVariables.MULTI_LANGUAGES_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName6;
                    break;
                case ConstantVariables.LOCATION_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName7;
                    break;
                case ConstantVariables.POLL_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName1;
                    break;
                case ConstantVariables.SPREAD_THE_WORD_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName2;
                    break;
                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName3;
                    break;
                case "signout":
                    colorHex = R.color.poll_vote_bar_colorName4;
                    break;
                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                case ConstantVariables.SITE_PAGE_TITLE_MENU:
                    colorHex = R.color.poll_vote_bar_colorName5;
                    break;

                case ConstantVariables.STORE_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName6;
                    break;
                case ConstantVariables.PRODUCT_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName7;
                    break;
                case ConstantVariables.STORE_OFFER_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName1;
                    break;
                case ConstantVariables.SITE_PRODUCT_ORDER_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName2;
                    break;
                case ConstantVariables.PRODUCT_CART_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName3;
                    break;
                case "core_main_cometchat":
                    colorHex = R.color.poll_vote_bar_colorName4;
                    break;
                case ConstantVariables.DIARY_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName5;
                    break;
                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName6;
                    break;
                case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                    colorHex = R.color.poll_vote_bar_colorName7;
                    break;
                default:
                    colorHex = R.color.poll_vote_bar_colorName5;
            }
        } else {
            colorHex = R.color.poll_vote_bar_colorName7;
        }
        return colorHex;
    }

    public static int getIconColor(int position) {
        int colorHex;
        switch (position) {
            case 1:
                colorHex = R.color.poll_vote_bar_colorName1;
                break;

            case 2:
                colorHex = R.color.poll_vote_bar_colorName2;
                break;

            case 3:
                colorHex = R.color.poll_vote_bar_colorName3;
                break;

            case 4:
                colorHex = R.color.poll_vote_bar_colorName5;
                break;

            case 5:
                colorHex = R.color.poll_vote_bar_colorName6;
                break;

            case 6:
                colorHex = R.color.poll_vote_bar_colorName7;
                break;

            case 7:
                colorHex = R.color.poll_vote_bar_colorName8;
                break;

            default:
                colorHex = R.color.green_blue;
                break;
        }
        return colorHex;
    }


    /**
     * Retrieve the default font icon typeface.
     */
    public static Typeface getFontIconTypeFace(Context context) {
        //TODO Needs to remove fontawesome-webfont.ttf file and below line later
//        return Typeface.createFromAsset(context.getAssets(), "fontIcons/fontawesome-webfont.ttf");
        return Typeface.createFromAsset(context.getAssets(), "fontIcons/font-awesome-webfont.ttf");
    }

    //Getting the view item id for view page redirection
    public static int getIdOfModule(JSONObject dataObject, String type) {

        switch (type) {

            case ConstantVariables.GROUP_MENU_TITLE:
            case ConstantVariables.GROUP_TITLE:
                return dataObject.optInt("group_id");

            case ConstantVariables.BLOG_MENU_TITLE:
            case ConstantVariables.BLOG_TITLE:
                return dataObject.optInt("blog_id");

            case ConstantVariables.CLASSIFIED_MENU_TITLE:
            case ConstantVariables.CLASSIFIED_TITLE:
                return dataObject.optInt("classified_id");

            case ConstantVariables.EVENT_MENU_TITLE:
            case ConstantVariables.EVENT_TITLE:
                return dataObject.optInt("event_id");

            case ConstantVariables.USER_MENU_TITLE:
            case ConstantVariables.USER_TITLE:
                return dataObject.optInt("user_id");

            case ConstantVariables.MUSIC_MENU_TITLE:
            case ConstantVariables.MUSIC_TITLE:
            case ConstantVariables.MUSIC_PLAYLIST_TITLE:
                return dataObject.optInt("playlist_id");

            case ConstantVariables.VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_VIDEO_MENU_TITLE:
            case ConstantVariables.VIDEO_TITLE:
            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_VIDEO_MENU_TITLE:
            case ConstantVariables.SITE_STORE_VIDEO_MENU_TITLE:
            case ConstantVariables.PRODUCT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_ADD_VIDEO:
            case ConstantVariables.SITE_PAGE_ADD_VIDEO:
            case ConstantVariables.SITE_STORE_ADD_VIDEO:
            case ConstantVariables.ADV_GROUPS_ADD_VIDEO:
            case ConstantVariables.ADV_VIDEO_TITLE:
                return dataObject.optInt("video_id");

            case ConstantVariables.ALBUM_MENU_TITLE:
            case ConstantVariables.ALBUM_TITLE:
            case ConstantVariables.ALBUM_PHOTO_MENU_TITLE:
                return dataObject.optInt("album_id");

            case ConstantVariables.FORUM_TOPIC_MENU_TITLE:
            case ConstantVariables.FORUM_POST_MENU_TITLE:
                return dataObject.optInt("topic_id");

            case ConstantVariables.FORUM_TITLE:
                return dataObject.optInt("forum_id");

            case ConstantVariables.POLL_MENU_TITLE:
            case ConstantVariables.POLL_TITLE:
                return dataObject.optInt("poll_id");

            case ConstantVariables.MLT_MENU_TITLE:
            case ConstantVariables.MLT_TITLE:
            case ConstantVariables.MLT_PHOTO_MENU_TITLE:
                return dataObject.optInt("listing_id");

            case ConstantVariables.MLT_REVIEW_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_REVIEW_MENU_TITLE:
                return dataObject.optInt("resource_id");

            case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
            case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                return dataObject.optInt("wishlist_id");

            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_MENU_TITLE:
                return dataObject.optInt("event_id");

            case ConstantVariables.ADV_EVENT_DIARY_MENU_TITLE:
                return dataObject.optInt("diary_id");

            case ConstantVariables.SITE_PAGE_TITLE:
            case ConstantVariables.SITE_PAGE_REVIEW_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_TITLE_MENU:
            case ConstantVariables.SITE_PAGE_PHOTO_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_ALBUM_MENU_TITLE:
                return dataObject.optInt("page_id");

            case ConstantVariables.ADVANCED_GROUPS_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_REVIEW_MENU_TITLE:
                return dataObject.optInt("group_id");

            case ConstantVariables.STORE_MENU_TITLE:
            case ConstantVariables.SITE_STORE_MENU_TITLE:
            case ConstantVariables.STORE_TITLE:
                return dataObject.optInt("store_id");

            case ConstantVariables.PRODUCT_MENU_TITLE:
            case ConstantVariables.SITE_PRODUCT_MENU_TITLE:
                return dataObject.optInt("product_id");

            case ConstantVariables.PRODUCT_ORDER_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_TICKET_ORDER:
                return dataObject.optInt("order_id");

            case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
            case ConstantVariables.SITE_VIDEO_CHANNEL_MENU_TITLE:
                return dataObject.optInt("channel_id");

            case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
            case ConstantVariables.SITE_VIDEO_PLAYLIST_MENU_TITLE:
                return dataObject.optInt("playlist_id");

            case ConstantVariables.SUGGESTION:
                return dataObject.optInt("entity_id");
            default:
                return 0;
        }
    }

    /**
     * Used to get module label according to the module name.
     *
     * @param name module name
     */
    public static String getLabelOfModule(Context context, String name, boolean returnToolBarTitle) {

        String label = null;
        if (name != null) {
            switch (name) {
                case ConstantVariables.BLOG_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_blog);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_blog);
                    break;

                case ConstantVariables.CLASSIFIED_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_classified);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_classfied);
                    break;

                case ConstantVariables.ALBUM_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_album);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_album);
                    break;

                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_group);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_group);
                    break;

                case ConstantVariables.VIDEO_MENU_TITLE:
                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_video);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_video);
                    break;

                case ConstantVariables.EVENT_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_event);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_event);

                    break;

                case ConstantVariables.MUSIC_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_music);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_music);
                    break;

                case ConstantVariables.FORUM_MENU_TITLE:
                    label = context.getResources().getString(R.string.action_bar_title_forum);
                    break;

                case ConstantVariables.MLT_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.menu_create_new) + " " +
                                PreferencesUtils.getCurrentSelectedListingSingularLabel(context,
                                        PreferencesUtils.getCurrentSelectedListingId(context));
                    else
                        label = PreferencesUtils.getCurrentSelectedListingSingularLabel(context,
                                PreferencesUtils.getCurrentSelectedListingId(context));
                    break;

                case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_wishlist);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_wishlist);
                    break;

                case ConstantVariables.HOME_MENU_TITLE:
                    label = context.getResources().getString(R.string.app_name);
                    break;

                case ConstantVariables.POLL_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.create_poll_title);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_Poll);
                    break;

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_event);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_adv_event);
                    break;

                case ConstantVariables.SITE_PAGE_TITLE_MENU:
                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_page);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_site_page);
                    break;

                case ConstantVariables.MESSAGE_MENU_TITLE:
                    label = context.getResources().getString(R.string.message_tab_name);
                    break;

                case ConstantVariables.NOTIFICATION_MENU_TITLE:
                    label = context.getResources().getString(R.string.notification_tab_name);
                    break;

                case ConstantVariables.FRIEND_REQUEST_MENU_TITLE:
                    label = context.getResources().getString(R.string.requests_tab_name);
                    break;

                case ConstantVariables.USER_MENU_TITLE:
                    label = context.getResources().getString(R.string.action_bar_title_members);
                    break;

                case ConstantVariables.CONTACT_US_MENU_TITLE:
                    label = context.getResources().getString(R.string.action_bar_title_contact_us);
                    break;

                case ConstantVariables.PRIVACY_POLICY_MENU_TITLE:
                    label = context.getResources().getString(R.string.action_bar_title_privacy_policy);
                    break;

                case ConstantVariables.TERMS_OF_SERVICE_MENU_TITLE:
                    label = context.getResources().getString(R.string.action_bar_title_terms_of_service);
                    break;

                case ConstantVariables.DIARY_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_create_diary);
                    else
                        label = context.getResources().getString(R.string.advanced_events_diaries_tab_title);
                    break;
                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_channel);
                    else
                        label = context.getResources().getString(R.string.action_bar_title_channels);
                    break;

                case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_playlist);
                    else
                        label = context.getResources().getString(R.string.playlist_tab);
                    break;
                case ConstantVariables.STORE_MENU_TITLE:
                    if (returnToolBarTitle)
                        label = context.getResources().getString(R.string.title_activity_create_new_store);
                    else
                        label = context.getResources().getString(R.string.browse_store);
                    break;
            }
        }
        return label;
    }

    //Getting the intent for the selected module item
    public static Intent getIntentForModule(Context context, int id, String type, String slug) {
        Intent viewIntent = null;
        String url = AppConstant.DEFAULT_URL;
        Bundle bundle = new Bundle();
        Log.d("TypeOfMenu ", type);
        switch (type) {
            case ConstantVariables.GROUP_MENU_TITLE:
            case ConstantVariables.GROUP_TITLE:
                viewIntent = GroupUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.BLOG_MENU_TITLE:
            case ConstantVariables.BLOG_TITLE:
                viewIntent = BlogUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.CLASSIFIED_MENU_TITLE:
            case ConstantVariables.CLASSIFIED_TITLE:
                viewIntent = ClassifiedUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.EVENT_MENU_TITLE:
            case ConstantVariables.EVENT_TITLE:
                viewIntent = EventUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.MUSIC_MENU_TITLE:
            case ConstantVariables.MUSIC_TITLE:
            case ConstantVariables.MUSIC_PLAYLIST_TITLE:
            case ConstantVariables.MUSIC_PLAYLIST_SONG_TITLE:
                viewIntent = MusicUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.ALBUM_MENU_TITLE:
            case ConstantVariables.ALBUM_TITLE:
            case ConstantVariables.ALBUM_PHOTO_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_ALBUM_MENU_TITLE:
                viewIntent = AlbumUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.POLL_MENU_TITLE:
            case ConstantVariables.POLL_TITLE:
                viewIntent = PollUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.FORUM_TITLE:
                viewIntent = ForumUtil.getProfilePageIntent(context, id, url, bundle, slug);
                break;

            case ConstantVariables.FORUM_TOPIC_MENU_TITLE:
            case ConstantVariables.FORUM_POST_MENU_TITLE:
                url += "forums/topic/" + id + "/";
                if (slug != null) {
                    url += slug;
                }
                viewIntent = ForumUtil.getViewTopicPageIntent(context, " ", url);
                assert viewIntent != null;
                viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;

            case ConstantVariables.VIDEO_MENU_TITLE:
            case ConstantVariables.VIDEO_TITLE:
            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_VIDEO_MENU_TITLE:
            case ConstantVariables.SITE_STORE_VIDEO_MENU_TITLE:
            case ConstantVariables.PRODUCT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_ADD_VIDEO:
            case ConstantVariables.SITE_PAGE_ADD_VIDEO:
            case ConstantVariables.SITE_STORE_ADD_VIDEO:
            case ConstantVariables.ADV_GROUPS_ADD_VIDEO:
                Log.d("FinalLoggedHere ", type);
                viewIntent = VideoUtil.getViewPageIntent(context, id, null, bundle);
                break;

            case ConstantVariables.ADV_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_VIDEO_TITLE:
                viewIntent = AdvVideoUtil.getViewPageIntent(context, id, null, bundle);
                break;

            case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
            case ConstantVariables.SITE_VIDEO_CHANNEL_MENU_TITLE:
                viewIntent = AdvVideoUtil.getChannelViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
            case ConstantVariables.SITE_VIDEO_PLAYLIST_MENU_TITLE:
                viewIntent = AdvVideoUtil.getPlayListViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.MESSAGE_CONVERSATION_MENU_TITLE:
                url += "messages/view/id/" + id + "?gutter_menu=1";
                viewIntent = new Intent(context, MessageViewActivity.class);
                viewIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, url);
                break;

            case ConstantVariables.MLT_MENU_TITLE:
            case ConstantVariables.MLT_TITLE:
            case ConstantVariables.MLT_PHOTO_MENU_TITLE:
                viewIntent = MLTUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.MLT_REVIEW_MENU_TITLE:
                viewIntent = MLTUtil.getUserReviewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.SITE_PRODUCT_REVIEW_MENU_TITLE:
                viewIntent = StoreUtil.getUserReviewPageIntent(context, id,
                        UrlUtil.PRODUCT_BROWSE_REVIEW_URL + "/" + id, bundle);
                break;

            case ConstantVariables.STORE_REVIEW_MENU_TITLE:
                //TODO Not implemented from API
                break;

            case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
                viewIntent = StoreUtil.getWishlistViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.SITE_PRODUCT_MENU_TITLE:
                viewIntent = StoreUtil.getProductViewPageIntent(context, id);
                break;

            case ConstantVariables.SITE_STORE_MENU_TITLE:
            case ConstantVariables.STORE_TITLE:
                viewIntent = StoreUtil.getStoreViewPageIntent(context, String.valueOf(id));
                break;

            case ConstantVariables.PRODUCT_ORDER_MENU_TITLE:
                viewIntent = StoreUtil.getOrderViewPageIntent(context, id);
                break;

            case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                viewIntent = MLTUtil.getWishlistViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_MENU_TITLE:
            case ConstantVariables.SUGGESTION:
                viewIntent = AdvEventsUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.ADV_EVENT_DIARY_MENU_TITLE:
                viewIntent = AdvEventsUtil.getViewDiaryIntent(context, id, url, bundle);
                break;

            case ConstantVariables.SITE_PAGE_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_TITLE:
            case ConstantVariables.SITE_PAGE_PHOTO_MENU_TITLE:
                viewIntent = SitePageUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.ADV_EVENT_REVIEW_MENU_TITLE:
                viewIntent = AdvEventsUtil.getUserReviewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.SITE_PAGE_REVIEW_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_REVIEW_TITLE:
                viewIntent = getAdvUserReviewPageIntent(context, id, "sitepage_review");
                break;

            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
            case ConstantVariables.ADVANCED_GROUPS_MENU_TITLE:
                viewIntent = AdvGroupUtil.getViewPageIntent(context, id, url, bundle);
                break;

            case ConstantVariables.ADV_GROUPS_REVIEW_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_REVIEW_TITLE:
                viewIntent = getAdvUserReviewPageIntent(context, id, "sitegroup_review");
                break;

            case ConstantVariables.ADV_EVENT_TICKET_ORDER:
                viewIntent = AdvEventsUtil.getEventTicketOrderIntent(context, id, url, bundle);
                break;
            default:
                break;
        }
        return viewIntent;

    }

    //Getting the intent for the selected sub module item
    public static Intent getIntentForSubModule(Context context, int id, int subModuleId, String type) {
        Intent viewIntent = null;
        String url = AppConstant.DEFAULT_URL;
        Bundle bundle = new Bundle();
        switch (type) {
            case ConstantVariables.STORE_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_ALBUM_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_ALBUM_MENU_TITLE:
            case ConstantVariables.ALBUM_PHOTO_MENU_TITLE:
                viewIntent = AlbumUtil.getViewPageIntent(context, id, subModuleId, url, bundle, type);
                break;
            default:
                break;
        }
        return viewIntent;

    }

    public static Intent getAdvUserReviewPageIntent(Context context, int id, String selectedModule) {

        String reviewUrl;
        Bundle bundle = new Bundle();
        if (selectedModule.equals(ConstantVariables.SITE_PAGE_REVIEW_TITLE)) {
            reviewUrl = AppConstant.DEFAULT_URL + "sitepage/reviews/browse/" + id + "?getRating=1&page=1";
            bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.SITE_PAGE_MENU_TITLE);

        } else {
            reviewUrl = AppConstant.DEFAULT_URL + "advancedgroups/reviews/browse/" + id + "?getRating=1&page=1";
            bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_GROUPS_MENU_TITLE);
        }

        bundle.putString(ConstantVariables.URL_STRING, reviewUrl);
        bundle.putString(ConstantVariables.FRAGMENT_NAME, "reviews");
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);

        Intent intent = new Intent(context, FragmentLoadActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * Method to set Intent params for the video view page redirection.
     *
     * @param attachmentType Attachment type of the video.
     * @param jsonObject     Json object which contains the attachment data.
     * @param attachmentId   Video view id.
     * @param mainIntent     Intent in which data is to be set.
     * @return Returns the updated intent by adding the required params.
     */
    public static Intent setIntentParamForVideo(String attachmentType, JSONObject jsonObject,
                                                int attachmentId, Intent mainIntent) {
        String viewPageUrl = null;
        switch (attachmentType) {
            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
                viewPageUrl = AppConstant.DEFAULT_URL + "listings/video/view/"
                        + jsonObject.optInt("listing_id") + "?gutter_menu=1"
                        + "&listingtype_id=" + jsonObject.optInt("listingtype_id")
                        + "&video_id=" + attachmentId;
                break;

            case ConstantVariables.ADV_EVENT_VIDEO_MENU_TITLE:
                viewPageUrl = AppConstant.DEFAULT_URL + "advancedevents/video/"
                        + jsonObject.optInt("event_id") + "/" + attachmentId + "?gutter_menu=1";
                break;

            case ConstantVariables.ADV_GROUPS_VIDEO_MENU_TITLE:
                viewPageUrl = AppConstant.DEFAULT_URL + "advancedgroups/video/view/"
                        + jsonObject.optInt("group_id") + "/" + attachmentId + "?gutter_menu=1";
                break;

            case ConstantVariables.PRODUCT_VIDEO_MENU_TITLE:
            case ConstantVariables.SITE_STORE_VIDEO_MENU_TITLE:
                viewPageUrl = AppConstant.DEFAULT_URL + "videogeneral/view?gutter_menu=1"
                        + "&subject_type=" + attachmentType + "&subject_id=" + attachmentId;
                break;
        }
        mainIntent.putExtra(ConstantVariables.VIDEO_SUBJECT_TYPE, attachmentType);
        if (viewPageUrl != null) {
            mainIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, viewPageUrl);
        }
        return mainIntent;
    }

    // checking for enabled modules from the api
    public static boolean isModuleEnabled(String name) {
        switch (name) {
            case ConstantVariables.FORUM_TITLE:
            case ConstantVariables.FORUM_TOPIC_MENU_TITLE:
            case ConstantVariables.FORUM_POST_MENU_TITLE:
            case ConstantVariables.GROUP_TITLE:
            case ConstantVariables.BLOG_TITLE:
            case ConstantVariables.CLASSIFIED_TITLE:
            case ConstantVariables.EVENT_TITLE:
            case ConstantVariables.USER_TITLE:
            case ConstantVariables.MUSIC_TITLE:
            case ConstantVariables.POLL_TITLE:
            case ConstantVariables.VIDEO_TITLE:
            case ConstantVariables.ADV_VIDEO_TITLE:
            case ConstantVariables.ALBUM_TITLE:
            case ConstantVariables.ADV_EVENT_MENU_TITLE:
            case ConstantVariables.ADVANCED_EVENT_TITLE:
            case ConstantVariables.MLT_TITLE:
            case ConstantVariables.MLT_MENU_TITLE:
            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
            case ConstantVariables.MLT_REVIEW_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_TITLE:
            case ConstantVariables.SITE_PAGE_REVIEW_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_REVIEW_TITLE:
            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
            case ConstantVariables.ADVANCED_GROUPS_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_REVIEW_TITLE:
            case ConstantVariables.ADV_GROUPS_REVIEW_MENU_TITLE:
            case ConstantVariables.DIARY_MENU_TITLE:
            case ConstantVariables.STORE_TITLE:
            case "core":
            case "activity":
                return true;
            default:
                return false;
        }
    }

    public static boolean isRTL(final Context context) {
        if (context == null) return false;
        final Resources res = context.getResources();
        return "ar".equals(res.getConfiguration().locale.getLanguage());
    }

    public static String getWebViewUrl(String url, Context context) {

        AppConstant appConstant = new AppConstant(context);
        HashMap<String, String> urlParams = new HashMap<>();

        // Added Parameter to disable header and footer.
        urlParams.put("disableHeaderAndFooter", String.valueOf(1));

        // Added Current Selected Language in WebUrl
        urlParams.put("language", PreferencesUtils.getCurrentLanguage(context));

        // Added Current Selected Location in WebUrl
        if (PreferencesUtils.getDefaultLocation(context) != null && !PreferencesUtils.getDefaultLocation(context).isEmpty())
            urlParams.put("restapilocation", PreferencesUtils.getDefaultLocation(context));

        if (PreferencesUtils.getAuthToken(context) != null && !PreferencesUtils.getAuthToken(context).isEmpty())
            urlParams.put("token", PreferencesUtils.getAuthToken(context));

        if (urlParams.size() != 0)
            url = appConstant.buildQueryString(url, urlParams);

        return url;

    }

    /**
     * Method to create a json object with default error message
     *
     * @param context Context of calling class.
     * @return Returns the json object with default error message.
     */
    public static JSONObject getErrorJsonString(Context context) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject("{\n\"message\":\"" + context.getResources()
                    .getString(R.string.please_retry_option) + "\"\n}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Method to create a json object with default error message
     *
     * @param message Message to be shown in error..
     * @return Returns the json object with default error message.
     */
    public static JSONObject getErrorJsonString(String message) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject("{\n\"message\":\"" + message + "\"\n}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Method to check that given string is a valid json or not.
     *
     * @param responseString Response string which needs to be check.
     * @return Returns true if the string is in proper json format else false.
     */
    public static boolean isValidJson(String responseString) {
        try {
            Object object = new JSONTokener(responseString).nextValue();
            if (object instanceof JSONObject || object instanceof JSONArray) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Method to return create response json object if exist.
     *
     * @param createResponse Create Response string.
     * @return Returns the body object if exists.
     */
    public static JSONObject getCreateResponse(String createResponse) {

        JSONObject body = null;
        try {
            if (createResponse != null && !createResponse.isEmpty()) {
                JSONObject jsonObject = new JSONObject(createResponse);
                body = jsonObject.getJSONObject("body");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;
    }

    public static ArrayList<JSONObject> sortReactionsObjectWithOrder(JSONObject reactionsObject) {

        ArrayList<JSONObject> reactionsArray = new ArrayList<>();

        try {
            JSONArray reactionNames = reactionsObject.names();
            for (int i = 0; i < reactionsObject.length(); i++) {
                reactionsArray.add(reactionsObject.getJSONObject(reactionNames.optString(i)));
            }

            Collections.sort(reactionsArray, new Comparator<JSONObject>() {

                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {

                    // Todo this has to be changed in order
                    if (lhs.optInt("order") > rhs.optInt("order")) {
                        return 1;
                    } else {
                        return -1;
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reactionsArray;
    }

    /**
     * Method to launch intent for document.
     *
     * @param context Context of calling class.
     */
    public static void openDocumentUploadingIntent(Context context, View view) {
        Intent intent = new Intent();

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        String[] mimetypes = {"text/*", "application/pdf", "application/msword", "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint", "application/epub+zip", "application/vnd.sun.xml.calc",
                "application/vnd.sun.xml.impress", "application/vnd.sun.xml.draw", "image/tiff",
                "application/vnd.oasis.opendocument.text", "application/vnd.oasis.opendocument.spreadsheet",
                "application/vnd.oasis.opendocument.presentation", "application/vnd.sun.xml.writer",
                "application/vnd.oasis.opendocument.formula", "application/vnd.oasis.opendocument.graphics",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            intent.putExtra("CONTENT_TYPE", mimetypes);
        }

        if (isIntentAvailable(context, intent)) {
            ((Activity) context).startActivityForResult(intent, ConstantVariables.INPUT_FILE_REQUEST_CODE);
        } else {
            SnackbarUtils.displaySnackbar(view, "No activity available to handle your request");
        }
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager mgr = context.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    public static String getFileRealPathFromUri(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {
                // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);

            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Method to get List Drawable, which will set onto checked text view.
     *
     * @param context Context of calling class.
     * @return Returns the List Drawable.
     */
    public static StateListDrawable getCheckMarkDrawable(Context context) {

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_check_box_24dp).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.themeButtonColor),
                PorterDuff.Mode.SRC_ATOP));
        Drawable drawableUnChecked = ContextCompat.getDrawable(context, R.drawable.ic_check_box_outline_24dp).mutate();
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_checked, android.R.attr.state_focused}, drawable);
        sld.addState(new int[]{-android.R.attr.state_checked, android.R.attr.state_focused}, drawableUnChecked);
        sld.addState(new int[]{-android.R.attr.state_checked}, drawableUnChecked);
        sld.addState(new int[]{android.R.attr.state_checked}, drawable);
        return sld;
    }

    public static void setWebSettings(WebView description, boolean isWebviewActivity) {
        WebSettings settings = description.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (isWebviewActivity) {
            settings.setDomStorageEnabled(true);
            //Mixed content using HTTP and HTTPS on WebViews are disabled by default starting Lollipop.
            //change the default WebView setting on Lollipop using.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        }
    }

    /**
     * Method to calculate hours difference between 2 dates.
     *
     * @param datetime Date-time, which needs to be compared with the current date.
     * @return returns the difference between dates in hours.
     */
    public static int hoursDifferenceFromCurrentDate(String datetime) {
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).parse(datetime)); // Parse into Date object
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance(); // Get time now
        long differenceInMillis = now.getTimeInMillis() - date.getTimeInMillis();
        long differenceInHours = (differenceInMillis) / 1000L / 60L / 60L; // Divide by millis/sec, secs/min, mins/hr
        return (int) differenceInHours;
    }

    /**
     * Compares two version strings.
     * <p>
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param latestVersion  a string of ordinal numbers separated by decimal points.
     * @param currentVersion a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if latestVersion is _numerically_ less than currentVersion.
     * The result is a positive integer if latestVersion is _numerically_ greater than currentVersion.
     * The result is zero if the strings are _numerically_ equal.
     */
    public static int versionCompare(String latestVersion, String currentVersion) {
        String[] latestVersionArray = latestVersion.split("\\.");
        String[] currentVersionArray = currentVersion.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < latestVersionArray.length && i < currentVersionArray.length && latestVersionArray[i].equals(currentVersionArray[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < latestVersionArray.length && i < currentVersionArray.length) {
            int diff = Integer.valueOf(latestVersionArray[i]).compareTo(Integer.valueOf(currentVersionArray[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(latestVersionArray.length - currentVersionArray.length);
    }

    public static void showValidationPopup(final Context mContext, final String redirectUrl, final String editUrl) {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        final LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextInputLayout passwordWrapper = new TextInputLayout(mContext);
        passwordWrapper.setHint(mContext.getResources().getString(R.string.lbl_enter_password));
        final AppCompatEditText password = new AppCompatEditText(mContext);
        password.setGravity(Gravity.START | Gravity.TOP);
        password.setFocusableInTouchMode(true);
        password.setSingleLine(true);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint(mContext.getResources().getString(R.string.enter_password));
        passwordWrapper.addView(password);
        linearLayout.addView(passwordWrapper);
        int padding = (int) mContext.getResources().getDimension(R.dimen.padding_15dp);
        linearLayout.setPadding(padding, padding, padding, 0);
        alertBuilder.setView(linearLayout);
        alertBuilder.setTitle(mContext.getResources().getString(R.string.validate_before_configuration));
        alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.continue_string), null);
        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), null);
        final AlertDialog mAlertDialog = alertBuilder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button continueButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!password.getText().toString().equals("")) {

                            Intent intent = new Intent(mContext, EditEntry.class);
                            intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.PAYMENT_METHOD_CONFIG);
                            intent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.PAYMENT_METHOD_CONFIG);
                            intent.putExtra(ConstantVariables.URL_STRING, redirectUrl + "?password=" + password.getText().toString());
                            intent.putExtra(ConstantVariables.EDIT_URL_STRING, editUrl + "?password=" + password.getText().toString());

                            mContext.startActivity(intent);
                            mAlertDialog.cancel();
                        }
                    }
                });
            }
        });
        mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mAlertDialog.cancel();
            }
        });
        mAlertDialog.show();
    }

    /**
     * Method to get font size in sp from pixel.
     *
     * @param context Context of calling class.
     * @param pixel   Size in pixel.
     * @return Returns the size in sp.
     */
    public static int getFontSizeFromPixel(Context context, int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixel,
                context.getResources().getDisplayMetrics());
    }

    public static String getFileNameByUri(@NonNull Context context, @NonNull Uri uri) {
        String fileName = "unknown";
        Uri filePathUri = uri;
        try {
            if (uri.getScheme().compareTo("content") == 0) {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                    filePathUri = Uri.parse(cursor.getString(column_index));
                    fileName = filePathUri.getLastPathSegment();
                }
            } else if (uri.getScheme().compareTo("file") == 0) {
                fileName = filePathUri.getLastPathSegment();
            } else {
                fileName = fileName + "_" + filePathUri.getLastPathSegment();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            return fileName;
        }
        return fileName;
    }


    public static String getFileNameFromPath(@NonNull String path) {
        if (!path.isEmpty()) {
            return path.substring(path.lastIndexOf("/")+1);
        } else {
            return "";
        }

    }

    public static int isValidFileSize(long length) {
        int maxUploadSize = 5;
        if (SERVER_SETTINGS.get(ConstantVariables.SERVER_SETTINGS_KEY) != null) {
            maxUploadSize = ((JSONObject) SERVER_SETTINGS.get(ConstantVariables.SERVER_SETTINGS_KEY)).optInt(ConstantVariables.UPLOAD_MAX_SIZE, 5);
        }
        if (maxUploadSize > length) {
            return 0;
        } else {
            return maxUploadSize;
        }

    }

    public static String validateFileSize(long length, Context context) {
        int maxUploadSize = 5, userQuota = 5;
        String message = ConstantVariables.VALID_FILE_SIZE;
        if (SERVER_SETTINGS.get(ConstantVariables.SERVER_SETTINGS_KEY) != null) {
            JSONObject config = ((JSONObject) SERVER_SETTINGS.get(ConstantVariables.SERVER_SETTINGS_KEY));
            maxUploadSize = config.optInt(ConstantVariables.UPLOAD_MAX_SIZE, 5);
            userQuota = config.optInt(ConstantVariables.USER_QUOTA_LIMIT, 50);
        }
        if (userQuota != -1 && userQuota < length) {
            message = context.getResources().getString(R.string.user_quota_storage_limit_message, userQuota);
        } else if (maxUploadSize < length) {
            message = context.getResources().getString(R.string.max_upload_limit_message, maxUploadSize);
        }
        return message;
    }

    public static boolean isMapAppEnabled(Context context) {

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return info.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /* For checking location service is enabled or not*/
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void requestForDeviceLocation(final Context mContext) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult((Activity) mContext, ConstantVariables.PERMISSION_GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    // Method to create basic notification
    public static void addNotification(Context context, String contentTitle, String contentDescription, int smallIcon, int notifyId, int priority) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "channel_id_" + notifyId);


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setContentTitle(contentTitle)
                .setContentText(contentDescription)
                .setOngoing(false)
                .setSmallIcon(smallIcon)
                .setPriority(priority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("channel_id_" + notifyId);
            NotificationChannel channel = new NotificationChannel(
                    "channel_id_" + notifyId,
                    contentTitle,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        manager.notify(notifyId, mBuilder.build());
    }

    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * Method to message owner.
     *
     * @param context               Context of calling class.
     * @param currentSelectedModule Currently selected module.
     * @param browseList            BrowseListItems instance which contains the info.
     */
    public static void messageOwner(Context context, String currentSelectedModule,
                                    BrowseListItems browseList) {
        Intent intent;
        if (currentSelectedModule.equals("userProfile")
                || currentSelectedModule.equals(ConstantVariables.USER_MENU_TITLE)) {
            intent = new Intent(context, CreateNewMessage.class);
            intent.putExtra(ConstantVariables.USER_ID, browseList.getUserId());
            intent.putExtra(ConstantVariables.CONTENT_TITLE, browseList.getUserDisplayName());
            intent.putExtra("isSendMessageRequest", true);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            intent = new Intent(context, CreateNewEntry.class);
            intent.putExtra(ConstantVariables.CREATE_URL, browseList.getMessageOwnerUrl());
            intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, currentSelectedModule);
            intent.putExtra(ConstantVariables.FORM_TYPE, "message_owner");
            intent.putExtra(ConstantVariables.CONTENT_TITLE, browseList.getMessageOwnerTitle());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
