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


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.facebook.login.LoginManager;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.SearchActivity;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.adapters.SelectAlbumListAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.dialogs.CheckInLocationDialog;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnCheckInLocationResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.BadgeView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.fab.CustomFloatingActionButton;
import com.fellopages.mobileapp.classes.common.ui.fab.FloatingActionMenu;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.FetchAddressIntentService;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedHomeFragment;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.SingleFeedPage;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsProfilePage;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.messages.CreateNewMessage;
import com.fellopages.mobileapp.classes.modules.multipleListingType.BrowseMLTFragment;
import com.fellopages.mobileapp.classes.modules.packages.SelectPackage;
import com.fellopages.mobileapp.classes.modules.pushnotification.MyFcmListenerService;
import com.fellopages.mobileapp.classes.modules.store.CartView;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;
import com.fellopages.mobileapp.classes.modules.user.settings.SettingsListActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.util.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;
import ru.dimorinny.showcasecard.position.BottomRightCustom;
import ru.dimorinny.showcasecard.position.TopLeftToolbar;
import ru.dimorinny.showcasecard.position.ViewPosition;
import ru.dimorinny.showcasecard.step.ShowCaseStep;
import ru.dimorinny.showcasecard.step.ShowCaseStepDisplayer;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
import static com.fellopages.mobileapp.classes.core.ConstantVariables.SERVER_SETTINGS;
import static com.fellopages.mobileapp.classes.core.ConstantVariables.STATUS_POST_OPTIONS;

public class MainActivity extends FormActivity implements FragmentDrawer.FragmentDrawerListener,
        View.OnClickListener, OnCheckInLocationResponseListener, OnUploadResponseListener, ShowCaseStepDisplayer.DismissListener {

    private final int TYPE_HOME = 1, TYPE_MODULE = 2, TYPE_OTHER = 3;
    private boolean isHomePage = false, mIsCanView = true, isGuestUserHomePage = false, isSetLocation = false, isCurrentLocationSet = false;
    private String mAppTitle;
    private String mIcon;
    private String mSingularLabel;
    private ArrayList<String> mSelectPath;
    private BadgeView mCartCountBadge;
    private Menu optionMenu = null;
    private ProgressDialog progressDialog;
    private AppConstant mAppConst;
    private String currentSelectedOption;
    private FragmentDrawer drawerFragment;
    private SelectAlbumListAdapter listAdapter;
    private ArrayAdapter<String> locationAdapter;
    private Map<String, String> mSelectedLocationInfo;
    private Context mContext;
    private IntentFilter intentFilter;
    private SocialShareUtil socialShareUtil;
    Toolbar toolbar;
    CustomFloatingActionButton fabAlbumCreate, fabAlbumUpload;
    private FloatingActionButton mFabCreate;
    private FloatingActionMenu mFabMenu;
    private LinearLayout mHomeTabs, mFooterTabs;
    private CardView mEventFilters;
    private TabLayout mTabHost;
    private AppBarLayout.LayoutParams mToolbarParams;
    private AlertDialogWithAction mAlertDialogWithAction;
    private int mListingTypeId, mBrowseType, mViewType, mSecondaryViewType;
    private int mPackagesEnabled, mStoreWishListEnabled, mMLTWishListEnabled;
    private BottomSheetBehavior<View> behavior;
    public static FirebaseAnalytics mFirebaseAnalytics;
    private GoogleApiClient mGoogleApiClient;
    private CheckInLocationDialog checkInLocationDialog;
    private LocationRequest mLocationRequest;
    private MenuItem cartItem, searchItem, locationItem;
    private View searchBar;
    private ShowCaseStepDisplayer.Builder showCaseStepDisplayer;
    private boolean isShowCaseView = false;
    private int menuCallingTime = 1;
    private AppBarLayout appBarLayout;

    /**
     * Broadcast receiver for receiving broadcasting intent action for
     * viewing message and notification module (view all)
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case ConstantVariables.ACTION_VIEW_ALL_MESSAGES:
                            selectItem("core_mini_messages", getResources().getString(R.string.message_tab_name), null, null, 1);
                            break;

                        case ConstantVariables.ACTION_VIEW_ALL_NOTIFICATIONS:
                            if (optionMenu != null) {
                                optionMenu.findItem(R.id.action_search).setVisible(false);
                            }
                            selectItem("core_mini_notification", getResources().getString(R.string.notification_drawer), null, null, 1);
                            break;
                    }
                }
            }
        }
    };

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        Intent intent = getIntent();
        if (intent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false)) {
            Bundle parameters = intent.getExtras();
            if (parameters != null){
                String body = parameters.getString("string");
                String idString = parameters.getString("id");
            }
            Intent intent1 = new Intent(MainActivity.this, AdvEventsProfilePage.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler(this));
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantVariables.ACTION_VIEW_ALL_MESSAGES);
        intentFilter.addAction(ConstantVariables.ACTION_VIEW_ALL_NOTIFICATIONS);
        registerReceiver(broadcastReceiver, intentFilter);

        mAppConst = new AppConstant(this);
        socialShareUtil = new SocialShareUtil(mContext);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        if (!mAppConst.checkManifestPermission(Manifest.permission.WAKE_LOCK)) {
            mAppConst.requestForManifestPermission(Manifest.permission.WAKE_LOCK, ConstantVariables.PERMISSION_WAKE_LOCK);
        } else {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }

        // Load the App in the previous saved Language
        mAppConst.changeAppLocale(PreferencesUtils.getCurrentLanguage(this), false);

        setContentView(R.layout.activity_main);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mAppTitle = getApplicationContext().getResources().getString(R.string.app_name);
        mHomeTabs = findViewById(R.id.home_tab_layout);
        mFooterTabs = findViewById(R.id.quick_return_footer_ll);
        mEventFilters = findViewById(R.id.eventFilterBlock);
        mTabHost = findViewById(R.id.materialTabHost);
        mFabMenu = findViewById(R.id.fab_menu);
        mFabCreate = findViewById(R.id.create_fab);
        mFabCreate.hide();
        mFooterTabs.setVisibility(View.GONE);
        fabAlbumCreate = findViewById(R.id.create_new_album);
        fabAlbumUpload = findViewById(R.id.edit_album);
        mFabCreate.setOnClickListener(this);
        fabAlbumUpload.setOnClickListener(this);
        fabAlbumCreate.setOnClickListener(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mToolbarParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        appBarLayout = findViewById(R.id.appbar);

        // Header search bar's text view.
//        TextView tvSearch = findViewById(R.id.tv_search);
//        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_action_search);
//        if (drawable != null) {
//            drawable.mutate();
//            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.gray_stroke_color), PorterDuff.Mode.SRC_ATOP));
//        }
//        tvSearch.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        //drawer layout settings
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(MainActivity.this);
        isSetLocation = getIntent().hasExtra("isSetLocation");

        // Updating dashboard data.
        mAppConst.getJsonResponseFromUrl(UrlUtil.DASHBOARD_URL + "?browse_as_guest=1", new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                updateServerSettings();

                // Updating location & guest user info.
                PreferencesUtils.updateLocationEnabledSetting(mContext, jsonObject.optInt("location"));
                PreferencesUtils.updateGuestUserSettings(mContext, jsonObject.optString("browse_as_guest"));
                PreferencesUtils.setVideoQualityPref(mContext, jsonObject.optInt("video_quality"));
                PreferencesUtils.setFilterEnabled(mContext, jsonObject.optInt("showFilterType"));
                PreferencesUtils.setStoryDuration(mContext, jsonObject.optInt("storyDuration"));
                PreferencesUtils.setOtpEnabledOption(mContext, jsonObject.optString("loginoption"));
                PreferencesUtils.setOtpPluginEnabled(mContext, jsonObject.optInt("isOTPEnable"));

                JSONArray enabledModules = jsonObject.optJSONArray("enable_modules");

                if (enabledModules != null) {
                    String modules = enabledModules.toString().replace("[\"", "");
                    modules = modules.replace("\"]", "");
                    PreferencesUtils.updateNestedCommentEnabled(mContext, enabledModules.toString().contains("nestedcomment") ? 1 : 0);
                    PreferencesUtils.updateSiteContentCoverPhotoEnabled(mContext, enabledModules.toString().contains("sitecontentcoverphoto") ? 1 : 0);
                    PreferencesUtils.setEnabledModuleList(mContext, modules);
                }

                invalidateOptionsMenu();
                // Saving dashboard response into preferences.
                mAppConst.saveDashboardValues(jsonObject);

                // Updating drawer.
                drawerFragment.drawerUpdate();

                if (getIntent().hasExtra("isFromWelcomeScreen")) {
                    checkLocationPermission();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                // Updating drawer.
                drawerFragment.drawerUpdate();

                if (getIntent().hasExtra("isFromWelcomeScreen")) {
                    checkLocationPermission();
                }
            }
        });

        // calling for status post options.
        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "advancedactivity/feeds/feed-decoration", new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                STATUS_POST_OPTIONS.put(ConstantVariables.FEED_DECORATION, jsonObject.optJSONObject("feed_docoration_setting"));
                STATUS_POST_OPTIONS.put(ConstantVariables.WORD_STYLING, jsonObject.optJSONArray("word_styling"));
                STATUS_POST_OPTIONS.put(ConstantVariables.ON_THIS_DAY, jsonObject.optJSONObject("on_thisDay"));
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
            }
        });

        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "advancedactivity/feelings/banner", new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                STATUS_POST_OPTIONS.put(ConstantVariables.BANNER_DECORATION,
                        jsonObject.optJSONArray("response"));
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
            }
        });

        // Making server call to get response of the upgrade params.
        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "get-new-version?type=android", new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                try {
                    boolean isPopUpEnabled = jsonObject.optInt("isPopUpEnabled") == 1;
                    String latestVersion = jsonObject.optString("latestVersion");
                    String description = jsonObject.optString("description");
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String currentVersion = pInfo.versionName;
                    int hoursDifference = 0;

                    if (PreferencesUtils.getAppUpgradeRemindTime(mContext) != null) {
                        hoursDifference = GlobalFunctions.hoursDifferenceFromCurrentDate(PreferencesUtils.getAppUpgradeRemindTime(mContext));
                    }

                    // Checking for the conditions in which upgrade app dialog needs to be shown.
                    if (isPopUpEnabled && !PreferencesUtils.isAppUpgradeDialogIgnored(mContext)
                            && latestVersion != null && !latestVersion.isEmpty() && currentVersion != null
                            && GlobalFunctions.versionCompare(latestVersion, currentVersion) > 0
                            && (hoursDifference > 24 || PreferencesUtils.getAppUpgradeRemindTime(mContext) == null)) {
                        try {
                            showUpgradeDialogIfNeeded(latestVersion, description);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
            }
        });


        Bundle extras = getIntent().getExtras();

        // Check is there any push notification intent coming from MyFcmListenerService Class
        if (extras != null) {
            int id = extras.getInt("id");
            int isSingleNotification = extras.getInt("is_single_notification");
            String type = extras.getString("type");
            String notificationViewUrl = extras.getString("notification_view_url");
            String headerTitle = extras.getString("headerTitle");
            String message = extras.getString("message");

            JSONObject paramsObject = null;
            if (extras.getString("objectParams") != null &&
                    !extras.getString("objectParams").isEmpty()) {
                try {
                    paramsObject = new JSONObject(extras.getString("objectParams"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (type != null && notificationViewUrl != null) {
                if (isSingleNotification == 1) {
                    int listingTypeId = extras.getInt(ConstantVariables.LISTING_TYPE_ID, 0);
                    int albumId = extras.getInt(ConstantVariables.ALBUM_ID, 0);
                    if (!type.isEmpty()) {

                        if (type.equals("core_comment")) {
                            selectItem("core_mini_notification", getResources().getString(R.string.notification_drawer), null, null, 0);
                        } else if (type.equals("album_photo") && albumId != 0) {
                            startNewActivity(type, albumId, listingTypeId, notificationViewUrl, headerTitle, paramsObject);
                            selectItem("home", mAppTitle, null, null, 0);
                        } else {
                            startNewActivity(type, id, listingTypeId, notificationViewUrl, headerTitle, paramsObject);
                            selectItem("home", mAppTitle, null, null, 0);
                        }
                    } else {
                        mAlertDialogWithAction.showPushNotificationAlertDialog(headerTitle, message);
                        selectItem("home", mAppTitle, null, null, 0);
                    }
                } else {
                    selectItem("core_mini_notification", getResources().
                            getString(R.string.notification_drawer), null, null, 0);
                }
            } else if (extras.getBoolean("isRedirectedFromEventProfile")) {
                mPackagesEnabled = PreferencesUtils.getAdvEventPackageEnabled(mContext);
                selectItem(ConstantVariables.ADVANCED_EVENT_MENU_TITLE, PreferencesUtils.getAdvEventTitle(mContext), null, null, PreferencesUtils.getCanCreate(mContext));
            } else {
                selectItem("home", mAppTitle, null, null, 0);
            }
        } else {
            //first page selection when the app loaded first time
            selectItem("home", mAppTitle, null, null, 0);
        }

        displayCartCount();

        if (!PreferencesUtils.isAppRated(this, PreferencesUtils.APP_RATED)) {
            showRateAppDialogIfNeeded();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        createLocationCallback();
        createLocationRequest();

        drawerFragment.drawerUpdate();
    }

    private void updateServerSettings() {
        mAppConst.getJsonResponseFromUrl(UrlUtil.SERVER_SETTINGS_URL,
                new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject response) {
                        SERVER_SETTINGS.put(ConstantVariables.SERVER_SETTINGS_KEY, response);
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    }
                });
    }

    private void autoUpdateCurrentLocation() {
        if (AppConstant.isDeviceLocationEnable == 1 && AppConstant.mLocationType.equals("notspecific")) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        }
    }

    /*
     * Add show case view step and display them (app quick tour guide)
     * */
    private void displayShowCaseView() {

        Context mContext = getApplicationContext();
//        View searchBar = findViewById(R.id.search_bar);
        Log.d("ShowItHere ", "true" + " " + searchBar + " " + isHomePage + " " + PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.SEARCH_BAR_CASE_VIEW));
        if (isHomePage && !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.NAVIGATION_ICON_CASE_VIEW)) {
            isShowCaseView = true;
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.NAVIGATION_ICON_CASE_VIEW);
            showCaseStepDisplayer.addStep(new ShowCaseStep(new TopLeftToolbar(), mContext.getResources().getString(R.string.navigation_show_case_text)));
        }

        if (searchBar != null &&
                isHomePage &&
                searchBar.getVisibility() == View.VISIBLE &&
                !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.SEARCH_BAR_CASE_VIEW)) {
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.SEARCH_BAR_CASE_VIEW);
            isShowCaseView = true;
            showCaseStepDisplayer.addStep(new ShowCaseStep(new ViewPosition(searchBar.findViewById(R.id.tv_search)), mContext.getResources().getString(R.string.search_show_case_text), mContext.getResources().getDimension(R.dimen.radius_18)));
        }

        if (cartItem != null &&
                isHomePage &&
                cartItem.isVisible() &&
                isHomePage &&
                cartItem.getActionView() != null &&
                !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.CART_ICON_CASE_VIEW)) {
            isShowCaseView = true;
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.CART_ICON_CASE_VIEW);
            showCaseStepDisplayer.addStep(new ShowCaseStep(cartItem.getActionView(), mContext.getResources().getString(R.string.cart_show_case_text), mContext.getResources().getDimension(R.dimen.radius_18)));
        }

        if (locationItem != null &&
                locationItem.isVisible() &&
                isHomePage &&
                !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.LOCATION_ICON_CASE_VIEW)) {
            isShowCaseView = true;
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.LOCATION_ICON_CASE_VIEW);
            showCaseStepDisplayer.addStep(new ShowCaseStep(locationItem.getActionView(), mContext.getResources().getString(R.string.location_show_case_text), mContext.getResources().getDimension(R.dimen.radius_18)));
        }

        if (searchItem != null &&
                searchItem.isVisible() &&
                !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.SEARCH_ICON_CASE_VIEW)) {
            isShowCaseView = true;
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.SEARCH_ICON_CASE_VIEW);
            showCaseStepDisplayer.addStep(new ShowCaseStep(searchItem.getActionView(), mContext.getResources().getString(R.string.search_show_case_text), mContext.getResources().getDimension(R.dimen.radius_18)));
        }

        View postView = findViewById(R.id.status_update_text);

        if (postView != null &&
                !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.STATUS_POST_CASE_VIEW) &&
                !drawerFragment.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            isShowCaseView = true;
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.STATUS_POST_CASE_VIEW);
            showCaseStepDisplayer.addStep(new ShowCaseStep(postView, mContext.getResources().getString(R.string.feed_post_show_case_text), mContext.getResources().getDimension(R.dimen.radius_20)));
        }

        if (!isHomePage &&
                mFabCreate != null &&
                mFabCreate.getVisibility() == View.VISIBLE &&
                !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.FAB_CREATE_CASE_VIEW) &&
                !currentSelectedOption.equals(ConstantVariables.ALBUM_MENU_TITLE)) {
            isShowCaseView = true;
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.FAB_CREATE_CASE_VIEW);
            showCaseStepDisplayer.addStep(new ShowCaseStep(new ViewPosition(mFabCreate), mContext.getResources().getString(R.string.create_content_show_case_text)));
        }

        if (!isHomePage &&
                mFabMenu != null &&
                mFabMenu.getVisibility() == View.VISIBLE &&
                currentSelectedOption.equals(ConstantVariables.ALBUM_MENU_TITLE) &&
                !PreferencesUtils.getShowCaseView(mContext, PreferencesUtils.FAB_MENU_CASE_VIEW)) {
            isShowCaseView = true;
            int padding70 = getResources().getDimensionPixelOffset(R.dimen.padding_70dp);
            PreferencesUtils.updateShowCaseView(mContext, PreferencesUtils.FAB_MENU_CASE_VIEW);
            showCaseStepDisplayer.addStep(new ShowCaseStep(new BottomRightCustom(padding70), mContext.getResources().getString(R.string.create_album_show_case_text), mContext.getResources().getDimension(R.dimen.radius_25)));
        }

        if (isShowCaseView) {
            showCaseStepDisplayer.build().start();
        } else {
            checkLocationPermission();
        }
    }

    // Display product count over the cart menu icon in toolbar
    public void displayCartCount() {
        if (mCartCountBadge != null) {
            if (!PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("0") &&
                    !PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("") &&
                    !PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("null")) {
                mCartCountBadge.setVisibility(View.VISIBLE);
                mCartCountBadge.setText(PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT));
            } else {
                mCartCountBadge.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Method to show app upgrade dialog if needed.
     *
     * @param latestVersion Latest version of the app which is available on play store.
     * @param description   Description of the new upgradable version.
     */
    private void showUpgradeDialogIfNeeded(String latestVersion, String description) {
        View view = getLayoutInflater().inflate(R.layout.layout_upgrade_dialog, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvDownload = view.findViewById(R.id.tv_download);
        TextView tvRemindMeLater = view.findViewById(R.id.tv_reming_me_later);
        TextView tvIgnore = view.findViewById(R.id.tv_ignore);
        tvTitle.setText(getResources().getString(R.string.new_version_available) + " (" + latestVersion + ")");
        tvDescription.setText(description);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setCornerRadius(30f);
        view.setBackground(drawable);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Theme_LocationDialog);
        alertBuilder.setView(view);
        final AlertDialog dialog = alertBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = AppConstant.getDisplayMetricsWidth(mContext) - 100;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        // Adding click listener on all 3 buttons, and setting up the value accordingly.
        tvDownload.setOnClickListener(v -> {
            dialog.dismiss();
            openAppInPlayStore();
        });

        tvRemindMeLater.setOnClickListener(v -> {
            dialog.dismiss();
            PreferencesUtils.setUpgradeRemindMeLaterTime(mContext,
                    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
        });

        tvIgnore.setOnClickListener(v -> {
            dialog.dismiss();
            PreferencesUtils.setAppUpgradeDialogIgnored(mContext);
        });
    }

    private void showRateAppDialogIfNeeded() {
        PreferencesUtils.updateLaunchCount(this, PreferencesUtils.getLaunchCount(this) + 1);
        int count = PreferencesUtils.isAppRated(this, PreferencesUtils.NOT_RATED) ? 180 : 20;
        if (PreferencesUtils.getLaunchCount(this) % count == 0) {
            createAppRatingDialog(getString(R.string.rate_app_title) + " " + getString(R.string.app_name),
                    getString(R.string.rate_app_message)).show();
        }
    }

    private AlertDialog createAppRatingDialog(String rateAppTitle, String rateAppMessage) {
        return new AlertDialog.Builder(this, R.style.Theme_LocationDialog).setPositiveButton(getString(R.string.dialog_app_rate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                PreferencesUtils.updateRatePref(mContext, PreferencesUtils.APP_RATED);
                openAppInPlayStore();
            }
        }).setNegativeButton(getString(R.string.dialog_app_never), (paramAnonymousDialogInterface, paramAnonymousInt) -> {
            PreferencesUtils.updateRatePref(mContext, PreferencesUtils.NOT_RATED);
            PreferencesUtils.updateLaunchCount(mContext, 0);
            paramAnonymousDialogInterface.dismiss();
        }).setMessage(rateAppMessage).setTitle(rateAppTitle).create();
    }

    public void openAppInPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    /**
     * Swaps fragments in the main content view
     */

    public void selectItem(String name, String label, String headerLabel, String itemUrl, int canCreate) {
        Fragment fragment = null;
        isGuestUserHomePage = false;
        BrowseMLTFragment.selectedViewType = 0;
        // update the main content by replacing fragments
        Log.d("SelectedName ", name);
        if (name != null) {
            AppController.getInstance().trackEvent(name, "Dashboard Selection", label);
            switch (name) {
                case "signout":
                    PreferencesUtils.clearSharedPreferences(mContext);
                    DataStorage.clearApplicationData(mContext);
                    CookieSyncManager.createInstance(mContext);
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeSessionCookie();
                    TwitterCore.getInstance().getSessionManager().clearActiveSession();
                    task.execute((Void[]) null);
                    break;

                case ConstantVariables.USER_SETTINGS_MENU_TITLE:
                    Intent settingsActivity = new Intent(MainActivity.this, SettingsListActivity.class);
                    startActivity(settingsActivity);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case ConstantVariables.PRODUCT_CART_MENU_TITLE:
                    Intent cartIntent = new Intent(MainActivity.this, CartView.class);
                    startActivity(cartIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case ConstantVariables.GLOBAL_SEARCH_MENU_TITLE:
                    Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                    searchActivity.putExtra(ConstantVariables.IS_SEARCHED_FROM_DASHBOARD, true);
                    startActivity(searchActivity);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case ConstantVariables.MULTI_LANGUAGES_MENU_TITLE:
                    mAppConst.changeLanguage(mContext, currentSelectedOption);
                    break;

                case ConstantVariables.APP_TOUR_MENU_TITLE:
                    PreferencesUtils.setShowCaseViewPref(mContext, false, false, false,
                            false, false, false, false, false, false, false);

                    PreferencesUtils.setProfileShowCaseViewPref(mContext, false, false,
                            false, false, false, false, false);

                    menuCallingTime = 0;
                    appBarLayout.setExpanded(true);

                    isShowCaseView = false;
                    invalidateOptionsMenu();

                    selectItem("home", mAppTitle, null, null, 1);

                    if (drawerFragment != null)
                        drawerFragment.scrolledToTop();

                    break;

                case ConstantVariables.LOCATION_MENU_TITLE:
                    changeLocation();
                    break;

                case ConstantVariables.SPREAD_THE_WORD_MENU_TITLE:

                    String description = getResources().getString(R.string.spread_the_word_description);

                    if (!description.contains("siteapi/index/app-page")) {
                        description += " " + AppConstant.DEFAULT_URL.replace("api/rest/", "") + "siteapi/index/app-page";
                    }

                    socialShareUtil.shareContent(getResources().getString(R.string.spread_the_word_title) + " " +
                            getResources().getString(R.string.app_name), description);
                    break;

                case ConstantVariables.RATE_US_MENU_TITLE:
                    createAppRatingDialog(getString(R.string.rate_app_title) + " " + getString(R.string.app_name),
                            getString(R.string.rate_app_message)).show();
                    break;

                case ConstantVariables.COMET_CHAT_MENU_TITLE:
                    /**
                     * Redirect to Cometchat app if it is installed in phone
                     * else redirect to Google playstore to download install it.
                     */
                    if (isCometchatAppInstalled(ConstantVariables.COMETCHAT_PACKAGE_NAME)) {
                        String userEmail = PreferencesUtils.getLoginInfo(mContext, PreferencesUtils.LOGIN_EMAIL);
                        String password = PreferencesUtils.getLoginInfo(mContext, PreferencesUtils.LOGIN_PASSWORD);
                        int userId = PreferencesUtils.getLoginUserId(mContext);
                        Intent launchIntent = getApplicationContext().getPackageManager().
                                getLaunchIntentForPackage(ConstantVariables.COMETCHAT_PACKAGE_NAME);
                        if (launchIntent != null) {
                            launchIntent.putExtra("username", userEmail);
                            launchIntent.putExtra("password", password);
                            launchIntent.putExtra("user_id", userId);
                        }

                        LogUtils.LOGD(MainActivity.class.getSimpleName(), " password " + password
                                + "Email = " + userEmail + " userId " + userId);
                        startActivityForResult(launchIntent, 1);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + ConstantVariables.COMETCHAT_PACKAGE_NAME)));
                    }
                    break;

                default:
                    mFooterTabs.setVisibility(View.GONE);
                    currentSelectedOption = name;
                    PreferencesUtils.updateCurrentModule(mContext, name);
                    if (mAppConst.isLoggedOutUser()) {
                        mFabCreate.hide();
                        if (name.equals("sitereview_listing")) {
                            PreferencesUtils.updateCurrentListingType(mContext, mListingTypeId, label,
                                    mSingularLabel, mListingTypeId, mBrowseType, mViewType, mIcon,
                                    canCreate, mPackagesEnabled, mIsCanView, mSecondaryViewType);
                        } else if (name.equals("home")) {
                            label = headerLabel = mAppTitle;
                            isHomePage = true;
                            isGuestUserHomePage = true;
                        } else if (name.equals("core_main_siteevent")) {
                            mFabCreate.show();
                        }

                        if (mIsCanView) {
                            fragment = GlobalFunctions.getGuestUserFragment(name);
                        }

                        // Checking tab visibility for the logged out user.
                        // Making tool bar SCROLL_FLAG_SCROLL only for the selected modules.
                        switch (name) {
                            case ConstantVariables.MLT_MENU_TITLE:
                            case ConstantVariables.EVENT_MENU_TITLE:
                            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            case ConstantVariables.SITE_PAGE_TITLE_MENU:
                            case ConstantVariables.SITE_PAGE_MENU_TITLE:
                            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                            case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            case ConstantVariables.PRODUCT_MENU_TITLE:
                                setTabVisibility(TYPE_MODULE);
                                break;

                            default:
                                setTabVisibility(TYPE_OTHER);
                                break;
                        }
                        mTabHost.setVisibility(View.GONE);
                    } else {
                        switch (name) {
                            case ConstantVariables.SAVE_FEEDS:
                            case ConstantVariables.HOME_MENU_TITLE:
                                setTabVisibility(TYPE_HOME);
                                label = headerLabel = mAppTitle;
                                isHomePage = true;
                                break;

                            case ConstantVariables.SITE_PRODUCT_WISHLIST_MENU_TITLE:
                            case ConstantVariables.STORE_OFFER_MENU_TITLE:
                            case ConstantVariables.FORUM_MENU_TITLE:
                            case ConstantVariables.FRIEND_REQUEST_MENU_TITLE:
                            case ConstantVariables.USER_MENU_TITLE:
                            case ConstantVariables.CONTACT_US_MENU_TITLE:
                            case ConstantVariables.PRIVACY_POLICY_MENU_TITLE:
                            case ConstantVariables.TERMS_OF_SERVICE_MENU_TITLE:
                                mFabCreate.hide();
                                setTabVisibility(TYPE_OTHER);
                                break;

                            case ConstantVariables.ALBUM_MENU_TITLE:
                                mFabCreate.hide();
                                if (canCreate == 1) {
                                    mFabMenu.showMenu(false);
                                }
                                mFabMenu.setClosedOnTouchOutside(true);
                                setTabVisibility(TYPE_MODULE);
                                break;

                            case ConstantVariables.NOTIFICATION_MENU_TITLE:
                                mFabCreate.hide();
                                mFabMenu.hideMenu(false);
                                setTabVisibility(TYPE_MODULE);
                                break;

                            case ConstantVariables.DIARY_MENU_TITLE:
                            case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                                if (canCreate == 1) {
                                    mFabCreate.show();
                                } else {
                                    mFabCreate.hide();
                                }
                                setTabVisibility(TYPE_OTHER);
                                break;

                            case ConstantVariables.WISHLIST_MENU_TITLE:

                                mFabCreate.hide();
                                mFabMenu.hideMenu(false);
                                if (mStoreWishListEnabled == 1 && mMLTWishListEnabled == 1) {
                                    setTabVisibility(TYPE_MODULE);
                                } else {
                                    setTabVisibility(TYPE_OTHER);
                                }
                                break;

                            case ConstantVariables.MLT_MENU_TITLE:
                                PreferencesUtils.updateCurrentListingType(mContext, mListingTypeId, label,
                                        mSingularLabel, mListingTypeId, mBrowseType, mViewType, mIcon,
                                        canCreate, mPackagesEnabled, mIsCanView, mSecondaryViewType);

                            case ConstantVariables.STORE_MENU_TITLE:
                                if (canCreate == 1) {
                                    mFabCreate.show();
                                } else {
                                    mFabCreate.hide();
                                }
                                setTabVisibility(TYPE_MODULE);
                                break;
                            default:
                                //TODO
//                                mFabCreate.setImageDrawable(ContextCompat.getDrawable(
//                                        this, R.drawable.ic_action_new));

                                if (canCreate == 1) {
                                    mFabCreate.setImageDrawable(ContextCompat.getDrawable(
                                            this, R.drawable.ic_action_new));
                                    mFabCreate.show();
                                } else {
                                    mFabCreate.hide();
                                }
                                mFabMenu.hideMenu(false);
                                setTabVisibility(TYPE_MODULE);
                                break;
                        }
                        fragment = GlobalFunctions.getAuthenticateUserFragment(name, mStoreWishListEnabled, mMLTWishListEnabled);
                    }
            }

        } else if (label != null && !label.isEmpty() && itemUrl != null && !itemUrl.isEmpty()) {

            AppController.getInstance().trackEvent(headerLabel, "Dashboard Selection", "Webview Loaded");
            if (ConstantVariables.WEBVIEW_ENABLE == 1) {
                Intent webViewActivity = new Intent(MainActivity.this, WebViewActivity.class);
                webViewActivity.putExtra("headerText", headerLabel);
                webViewActivity.putExtra("url", itemUrl);
                startActivityForResult(webViewActivity, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            } else {
                LogUtils.LOGD("MainActivity", "GlobalFunctions.getWebViewUrl(itemUrl, this) " +
                        GlobalFunctions.getWebViewUrl(itemUrl, this));
                CustomTabUtil.launchCustomTab(this, GlobalFunctions.getWebViewUrl(itemUrl, this));
            }
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_content, fragment).commit();
            // Show Header label as Toolbar title if set
            if (headerLabel != null && !headerLabel.isEmpty()) {
                setTitle(headerLabel);
            } else {
                setTitle(label);
            }
        } else if (!mIsCanView && mAppConst.isLoggedOutUser()) {
            Intent mainIntent;
            if (ConstantVariables.INTRO_SLIDE_SHOW_ENABLED == 1) {
                mainIntent = new Intent(mContext, NewLoginActivity.class);
            } else {
                mainIntent = new Intent(mContext, LoginActivity.class);
            }
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if ((isHomePage || isGuestUserHomePage) && ConstantVariables.SHOW_APP_TITLE_IN_HEADER == 0) {
//            findViewById(R.id.search_bar).setVisibility(View.VISIBLE);
//            findViewById(R.id.search_bar).setOnClickListener(this);
            invalidateOptionsMenu();
        } else {
//            findViewById(R.id.search_bar).setVisibility(View.GONE);
        }

        CustomViews.createMarqueeTitle(mContext, toolbar);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.optionMenu = menu;

        if (AppConstant.isLocationEnable && isHomePage &&
                PreferencesUtils.isLocationSettingEnabled(mContext)) {
            menu.findItem(R.id.action_location).setVisible(true);
        } else {
            menu.findItem(R.id.action_location).setVisible(false);
        }

        if (currentSelectedOption.equals("home")) {
            menu.findItem(R.id.action_event).setVisible(false);
        } else {
            menu.findItem(R.id.action_event).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click
        switch (item.getItemId()) {

            case R.id.action_search:
                if (currentSelectedOption != null && currentSelectedOption.equals(ConstantVariables.PRODUCT_MENU_TITLE)) {
                    return super.onOptionsItemSelected(item);
                } else {
                    //calling the search activity
                    Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                    searchActivity.putExtra(ConstantVariables.IS_SEARCHED_FROM_DASHBOARD, false);
                    searchActivity.putExtra(ConstantVariables.CATEGORY_FORUM_TOPIC,
                            getResources().getString(R.string.query_search_forum_topics));
                    startActivity(searchActivity);
                    return true;
                }

            case R.id.action_location:
                changeLocation();
                return true;

            case R.id.action_event:
                Intent createEntry;
                String url;
                if (PreferencesUtils.getPackageEnabled(mContext) == 1) {
                    createEntry = new Intent(this, SelectPackage.class);
                    url = UrlUtil.ADV_EVENTS_PACKAGE_LIST_URL;
                } else {
                    createEntry = new Intent(MainActivity.this, CreateNewEntry.class);
                    url = AppConstant.DEFAULT_URL + "advancedevents/create";
                }
                createEntry.putExtra(ConstantVariables.CREATE_URL, url);
                createEntry.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
                startActivityForResult(createEntry, ConstantVariables.CREATE_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem toggleItem = menu.findItem(R.id.viewToggle);
        toggleItem.setVisible(false);
        menuCallingTime++;

        searchItem = menu.findItem(R.id.action_search);
        locationItem = menu.findItem(R.id.action_location);
        cartItem = menu.findItem(R.id.action_cart);
//        searchBar = findViewById(R.id.search_bar);
        if (searchItem != null && searchItem.getActionView() != null) {
            setColorFilter(searchItem.getActionView().findViewById(R.id.search_icon));
        }
        if (locationItem != null && locationItem.getActionView() != null) {
            setColorFilter(locationItem.getActionView().findViewById(R.id.location_icon));
        }
        if (cartItem != null && cartItem.getActionView() != null) {
            setColorFilter(cartItem.getActionView().findViewById(R.id.cart_icon));
        }

        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        // Getting enabled module array to check store cart icon is need to show or not.
        List<String> enabledModuleList = null;
        if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
            enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
        }

        if (enabledModuleList != null && enabledModuleList.contains("sitestore") && ConstantVariables.SHOW_CART_ICON) {
            cartItem.setVisible(true);
            cartItem.getActionView().setOnClickListener(this);
            mCartCountBadge = cartItem.getActionView().findViewById(R.id.cart_item_count);
            displayCartCount();
        } else {
            cartItem.setVisible(false);
        }

        if (locationItem != null && locationItem.isVisible()) {
            locationItem.getActionView().setOnClickListener(this);
        }

        if (searchItem != null && searchItem.isVisible()) {
            searchItem.getActionView().setOnClickListener(this);
        }

        /**
         * Changing search bar margin when the other menu items are visible or not.
         */
//        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) searchBar.getLayoutParams();
//        if (!cartItem.isVisible() && !locationItem.isVisible()) {
//            layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp));
//        } else {
//            layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp));
//        }

        if (currentSelectedOption != null) {

            switch (currentSelectedOption) {
                case ConstantVariables.NOTIFICATION_MENU_TITLE:
                case ConstantVariables.USER_SETTINGS_MENU_TITLE:
                case ConstantVariables.CONTACT_US_MENU_TITLE:
                case ConstantVariables.PRIVACY_POLICY_MENU_TITLE:
                case ConstantVariables.TERMS_OF_SERVICE_MENU_TITLE:
                case ConstantVariables.MESSAGE_MENU_TITLE:
                case ConstantVariables.FRIEND_REQUEST_MENU_TITLE:
                case ConstantVariables.STORE_OFFER_MENU_TITLE:
                    searchItem.setVisible(false);
                    Log.d(TAG, "onPrepareOptionsMenu: seach " + searchItem.isVisible());
                    break;
                case ConstantVariables.BLOG_MENU_TITLE:
                case ConstantVariables.CLASSIFIED_MENU_TITLE:
                case ConstantVariables.POLL_MENU_TITLE:
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.VIDEO_MENU_TITLE:
                case ConstantVariables.EVENT_MENU_TITLE:
                case ConstantVariables.MUSIC_MENU_TITLE:
                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                case ConstantVariables.ALBUM_MENU_TITLE:
                case ConstantVariables.MLT_MENU_TITLE:
                case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                case ConstantVariables.FORUM_MENU_TITLE:
                case ConstantVariables.USER_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                case ConstantVariables.SITE_PAGE_TITLE_MENU:
                case ConstantVariables.PRODUCT_MENU_TITLE:
                    Log.d(TAG, "onPrepareOptionsMenu: seach " + searchItem.isVisible());
                    break;
                default:
                    break;

            }

            if ((isHomePage || isGuestUserHomePage) && ConstantVariables.SHOW_APP_TITLE_IN_HEADER == 0) {
                searchItem.setVisible(true);
            }

        }

        /* Execute show case view code after initializing
         * all methods and views */
        if (menuCallingTime > 2 && !mAppConst.isLoggedOutUser()
                && PreferencesUtils.getAppTourEnabled(mContext) == 1) {
            /* Initialize show case view step builder */
            showCaseStepDisplayer = new ShowCaseStepDisplayer.Builder(this, R.color.colorAccent);
            displayShowCaseView();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setColorFilter(ImageView imageView) {
        if (imageView != null) {
            imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary));
        }
    }

    @Override
    public void onDrawerItemSelected(View view, String name, String label, String headerLabel,
                                     String singularLabel, String itemUrl, int listingId, int browseType,
                                     int viewType, String icon, int canCreate, int packagesEnabled,
                                     int siteStoreEnabled, int listingEnabled, boolean canView, int secondaryViewType) {
        mListingTypeId = listingId;
        mBrowseType = browseType;
        mViewType = viewType;
        mIcon = icon;
        mSingularLabel = singularLabel;
        mPackagesEnabled = packagesEnabled;
        mStoreWishListEnabled = siteStoreEnabled;
        mMLTWishListEnabled = listingEnabled;
        mIsCanView = canView;
        mSecondaryViewType = secondaryViewType;
        selectItem(name, label, headerLabel, itemUrl, canCreate);
    }


    //Showing list of albums for uploading photos in a particular album
    public void showListOfAlbums() {

        final Dialog albumListDialog = new Dialog(this, R.style.Theme_CustomDialog);
        final ListView listViewAlbumTitle = new ListView(this);
        final List<BrowseListItems> mBrowseItemList = new ArrayList<>();
        listAdapter =
                new SelectAlbumListAdapter(this, R.layout.simple_text_view, mBrowseItemList);
        listViewAlbumTitle.setAdapter(listAdapter);
        ProgressBar progressBar = new ProgressBar(this);
        albumListDialog.setContentView(progressBar);
        albumListDialog.setTitle(getApplicationContext().getResources()
                .getString(R.string.select_album_dialog_title));
        albumListDialog.setCancelable(true);
        albumListDialog.show();
        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "albums/upload",
                new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                        if (jsonObject != null) {
                            albumListDialog.setContentView(listViewAlbumTitle);
                            JSONArray body = jsonObject.optJSONArray("response");
                            if (body != null && body.length() > 2) {
                                JSONObject albumList = body.optJSONObject(0);
                                if (albumList.optString("name").equals("album")) {
                                    try {
                                        if (albumList.optJSONObject("multiOptions") != null) {
                                            Iterator<String> iter = albumList.optJSONObject("multiOptions").keys();
                                            while (iter.hasNext()) {
                                                String key = iter.next();
                                                String value = albumList.optJSONObject("multiOptions").getString(key);
                                                if (!key.equals("0"))
                                                    mBrowseItemList.add(new BrowseListItems(key, value));
                                                listAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            mBrowseItemList.add(new BrowseListItems("", getResources().
                                                    getString(R.string.no_album_error_message)));
                                            listAdapter.notifyDataSetChanged();
                                            listViewAlbumTitle.setOnItemClickListener(null);
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                        }
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        albumListDialog.dismiss();
                    }
                });


        listViewAlbumTitle.setOnItemClickListener((adapterView, view, i, l) -> {
            mFabMenu.close(true);
            String url = AppConstant.DEFAULT_URL + "albums/upload?album_id="
                    + mBrowseItemList.get(i).getAlbumId();
            albumListDialog.cancel();
            Intent uploadPhoto = new Intent(MainActivity.this, MultiMediaSelectorActivity.class);
            // Selection type photo to display items in grid
            uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO);
            // Whether photo shoot
            uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, true);
            // The maximum number of selectable image
            uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT,
                    ConstantVariables.FILE_UPLOAD_LIMIT);
            // Select mode
            uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE,
                    MultiMediaSelectorActivity.MODE_MULTI);
            uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_URL, url);

            startActivityForResult(uploadPhoto, ConstantVariables.REQUEST_IMAGE);
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (checkInLocationDialog != null && checkInLocationDialog.isShowing()) {
                checkInLocationDialog.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Fragment loadFragment;
            if (requestCode == ConstantVariables.TWITTER_REQUEST_CODE) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }

            Bundle bundle = null;
            if (data != null) {
                bundle = data.getExtras();
            }

            String mUploadPhotoUrl;

            if (requestCode == ConstantVariables.PERMISSION_GPS_SETTINGS) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (isHomePage) {
                            autoUpdateCurrentLocation();
                        } else {
                            loadFragmentOnGPSEnabled();
                        }
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.for_better_results_enable_gps), Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            } else if (resultCode != 0) {
                switch (resultCode) {

                    case ConstantVariables.CREATE_REQUEST_CODE:
                        loadFragment = GlobalFunctions.getAuthenticateUserFragment(currentSelectedOption, mStoreWishListEnabled, mMLTWishListEnabled);
                        if (loadFragment != null) {
                            loadFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.main_content, loadFragment);
                            ft.commitAllowingStateLoss();
                        }
                        break;

                    // Refreshing the fragment when creating any content.
                    case ConstantVariables.VIEW_PAGE_CODE:
                        loadFragment = GlobalFunctions.getAuthenticateUserFragment(currentSelectedOption, mStoreWishListEnabled, mMLTWishListEnabled);
                        if (requestCode == ConstantVariables.CREATE_REQUEST_CODE && loadFragment != null) {
                            PreferencesUtils.updateCurrentModule(mContext, currentSelectedOption);
                            loadFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.main_content, loadFragment);
                            ft.commitAllowingStateLoss();
                        }
                        break;

                    case ConstantVariables.REQUEST_IMAGE:
                        if (data != null) {
                            mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                            mUploadPhotoUrl = data.getStringExtra(MultiMediaSelectorActivity.EXTRA_URL);
                            StringBuilder sb = new StringBuilder();
                            for (String p : mSelectPath) {
                                sb.append(p);
                                sb.append("\n");
                            }
                            // uploading the photos to server
                            new UploadFileToServerUtils(mContext, findViewById(R.id.main_content),
                                    mUploadPhotoUrl, mSelectPath, this).execute();
                        }
                        break;

                    case ConstantVariables.FEED_REQUEST_CODE:

                        if (bundle != null) {
                            if (bundle.getBoolean("isPosted", false)) {
                                if (bundle.getBoolean("isExternalShare", false)) {
                                    ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.post_sharing_success_message));
                                } else {
                                    loadFragment = new FeedHomeFragment();
                                    loadFragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction ft = fragmentManager.beginTransaction();
                                    ft.replace(R.id.main_content, loadFragment);
                                    ft.commitAllowingStateLoss();
                                }
                            }

                            if (bundle.getBoolean("isTwitterPost", false)) {
                                TweetComposer.Builder builder = new TweetComposer.Builder(this);
                                builder.text(bundle.getString("mStatusBodyText", ""));
                                if (bundle.containsKey("imagePath")) {
                                    String imagePath = bundle.getString("imagePath");
                                    if (imagePath != null) {
                                        File myImageFile = new File(imagePath);
                                        builder.image(GlobalFunctions.getFileUri(mContext, myImageFile));
                                    }
                                } else if (bundle.containsKey(ConstantVariables.VIDEO_URL)) {
                                    URL url = new URL(bundle.getString(ConstantVariables.VIDEO_URL));
                                    builder.url(url);
                                }
                                builder.show();
                            }

                        }

                        break;

                }
            } else {
                switch (requestCode) {
                    case ConstantVariables.REQUEST_IMAGE:

                        if (data != null) {
                            mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                            mUploadPhotoUrl = data.getStringExtra(MultiMediaSelectorActivity.EXTRA_URL);
                            StringBuilder sb = new StringBuilder();
                            for (String p : mSelectPath) {
                                sb.append(p);
                                sb.append("\n");
                            }

                            // uploading the photos to server
                            new UploadFileToServerUtils(mContext, findViewById(R.id.main_content),
                                    mUploadPhotoUrl, mSelectPath, this).execute();
                        }

                        break;

                    case ConstantVariables.WEB_VIEW_ACTIVITY_CODE:
                        if (isHomePage) {
                            selectItem("home", mAppTitle, null, null, 0);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayCartCount();
        registerReceiver(broadcastReceiver, intentFilter);
        if (currentSelectedOption != null && currentSelectedOption.equals("home")) {
            if (mTabHost.getVisibility() == View.VISIBLE) {
                mTabHost.setVisibility(View.GONE);
            }
            if (mFooterTabs.getVisibility() == View.VISIBLE) {
                mFooterTabs.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {

        // If show case view is displayed close/hide it
        if (isShowCaseView) {
            showCaseStepDisplayer.dismissShowCaseView();
        }
        // if drawer is open, close it.
        if (drawerFragment.mDrawerLayout != null
                && drawerFragment.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerFragment.mDrawerLayout.closeDrawers();

        } else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (isHomePage) {
            //Going to home screen
            moveTaskToBack(true);
        } else {
            invalidateOptionsMenu();
            //Going back to home screen
            selectItem("home", mAppTitle, null, null, 0);

        }
    }


    @Override
    public void onClick(View view) {

        LogUtils.LOGD("MainActivity", String.valueOf(view.getId()));

        switch (view.getId()) {

            case R.id.create_fab:
                //create page url settings
                Intent createEntry;
                String url = null;

                if (currentSelectedOption != null) {

                    createEntry = new Intent(MainActivity.this, CreateNewEntry.class);

                    switch (currentSelectedOption) {
                        case ConstantVariables.HOME_MENU_TITLE:
                            if (view.getTag().equals("core_main_message")) {
                                createEntry = new Intent(MainActivity.this, CreateNewMessage.class);
                            } else {
                                createEntry = new Intent(this, SettingsListActivity.class);
                            }
                            break;

                        case ConstantVariables.MESSAGE_MENU_TITLE:
                            createEntry = new Intent(MainActivity.this, CreateNewMessage.class);
                            break;

                        case ConstantVariables.BLOG_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "/blogs/create";
                            break;

                        case ConstantVariables.CLASSIFIED_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "/classifieds/create";
                            break;

                        case ConstantVariables.GROUP_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "groups/create";
                            break;

                        case ConstantVariables.VIDEO_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "videos/create";
                            break;

                        case ConstantVariables.EVENT_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "events/create";
                            break;

                        case ConstantVariables.MUSIC_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "music/create";
                            break;

                        case ConstantVariables.POLL_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "/polls/create";
                            break;

                        case ConstantVariables.MLT_MENU_TITLE:
                            if (mPackagesEnabled == 1) {
                                createEntry = new Intent(this, SelectPackage.class);
                                url = UrlUtil.MLT_PACKAGE_LIST_URL + "&listingtype_id=" +
                                        PreferencesUtils.getCurrentSelectedListingId(mContext);
                            } else {
                                url = AppConstant.DEFAULT_URL + "listings/create?listingtype_id=" +
                                        PreferencesUtils.getCurrentSelectedListingId(mContext);
                            }
                            createEntry.putExtra(ConstantVariables.LISTING_TYPE_ID,
                                    PreferencesUtils.getCurrentSelectedListingId(mContext));
                            break;

                        case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "listings/wishlist/create";
                            break;

                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            if (mPackagesEnabled == 1) {
                                createEntry = new Intent(this, SelectPackage.class);
                                url = UrlUtil.ADV_EVENTS_PACKAGE_LIST_URL;
                            } else {
                                url = AppConstant.DEFAULT_URL + "advancedevents/create";
                            }
                            break;

                        case ConstantVariables.DIARY_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "advancedevents/diaries/create";
                            createEntry.putExtra(ConstantVariables.FORM_TYPE, "create_new_diary");
                            break;

                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        case ConstantVariables.SITE_PAGE_TITLE_MENU:
                            if (mPackagesEnabled == 1) {
                                createEntry = new Intent(this, SelectPackage.class);
                                url = UrlUtil.SITE_PAGE_PACKAGE_LIST_URL;
                            } else
                                url = AppConstant.DEFAULT_URL + "sitepages/create";
                            break;

                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            if (mPackagesEnabled == 1) {
                                createEntry = new Intent(this, SelectPackage.class);
                                url = UrlUtil.ADV_GROUP_PACKAGE_LIST_URL;
                            } else
                                url = AppConstant.DEFAULT_URL + "advancedgroups/create";
                            break;

                        case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "advancedvideos/create";
                            break;

                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "advancedvideos/channel/create";
                            break;

                        case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                            url = AppConstant.DEFAULT_URL + "advancedvideos/playlist/create";
                            break;
                        case ConstantVariables.STORE_MENU_TITLE:
                            if (mPackagesEnabled == 1) {
                                createEntry = new Intent(this, SelectPackage.class);
                                url = UrlUtil.STORE_PACKAGE_LIST_URL;
                            } else {
                                createEntry = new Intent(this, CreateNewEntry.class);
                                url = AppConstant.DEFAULT_URL + "sitestore/create/";
                            }
                            createEntry.putExtra(ConstantVariables.FORM_TYPE, "store_create");
                            PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.STORE_MENU_TITLE);
                            break;
                        default:
                            currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
                            break;

                    }

                    createEntry.putExtra(ConstantVariables.CREATE_URL, url);
                    startActivityForResult(createEntry, ConstantVariables.CREATE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.create_new_album:
                mFabMenu.close(true);
                url = AppConstant.DEFAULT_URL + "albums/upload?create_new_album=1";
                createEntry = new Intent(MainActivity.this, CreateNewEntry.class);
                createEntry.putExtra(ConstantVariables.CREATE_URL, url);
                startActivityForResult(createEntry, ConstantVariables.CREATE_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.edit_album:
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                } else {
                    showListOfAlbums();
                }
                break;

            case R.id.search_bar:
                PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.HOME_MENU_TITLE);
                //calling the search activity
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra(ConstantVariables.IS_SEARCHED_FROM_DASHBOARD, false);
                startActivity(searchActivity);
                break;


            case R.id.search_icon_view:
            case R.id.search_icon:
                //calling the search activity
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchIntent.putExtra(ConstantVariables.IS_SEARCHED_FROM_DASHBOARD, false);
                searchIntent.putExtra(ConstantVariables.CATEGORY_FORUM_TOPIC,
                        getResources().getString(R.string.query_search_forum_topics));
                startActivity(searchIntent);
                break;

            case R.id.action_location:
            case R.id.location_icon:
            case R.id.location_icon_view:
                if (!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                            ConstantVariables.ACCESS_FINE_LOCATION);
                } else {
                    changeLocation();
                }
                break;

            default:
                Intent intent = new Intent(this, CartView.class);
                startActivity(intent);


        }

    }

    public void setTabVisibility(int viewType) {

        switch (viewType) {
            case TYPE_HOME:
                mToolbarParams.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS);
                mHomeTabs.setVisibility(View.VISIBLE);
                mEventFilters.setVisibility(View.GONE);
                mTabHost.setVisibility(View.GONE);
                mFabCreate.setVisibility(View.GONE);
                mFabMenu.hideMenu(false);
                break;
            case TYPE_MODULE:
                mToolbarParams.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS);
                isHomePage = false;
                mHomeTabs.setVisibility(View.GONE);
                mEventFilters.setVisibility(View.GONE);
                break;
            case TYPE_OTHER:
                mToolbarParams.setScrollFlags(0);
                isHomePage = false;
                mHomeTabs.setVisibility(View.GONE);
                mEventFilters.setVisibility(View.GONE);
                mTabHost.setVisibility(View.GONE);
                mFabMenu.hideMenu(false);
                break;
        }
    }

    @Override
    public void onCheckInLocationChanged(final Bundle data) {
        loadFragmentOnDefaultLocationChanged(data.getBoolean("isNewLocation", true));

        if (data.getBoolean("isNewLocation", true)) {
            setCurrentLocationOnServer(data.getString("locationObject", ""), data.getInt("user_id"));
        }
    }

    private void loadFragmentOnDefaultLocationChanged(final boolean isNewLocation) {
        final Fragment loadFragment = GlobalFunctions.getAuthenticateUserFragment(currentSelectedOption,
                mStoreWishListEnabled, mMLTWishListEnabled);
        SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.main_content),
                mContext.getResources().getString(R.string.change_location_success_message),
                () -> {
                    if (loadFragment != null && isNewLocation) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.main_content, loadFragment);
                        ft.commitAllowingStateLoss();
                    }
                });
    }


    private void loadFragmentOnGPSEnabled() {
        final Fragment loadFragment = GlobalFunctions.getAuthenticateUserFragment(currentSelectedOption,
                mStoreWishListEnabled, mMLTWishListEnabled);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_content, loadFragment);
        ft.commitAllowingStateLoss();
    }


    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        String message;
        if (isRequestSuccessful) {
            message = getResources().
                    getQuantityString(R.plurals.photo_upload_msg,
                            mSelectPath.size(),
                            mSelectPath.size());
        } else {
            message = jsonObject.optString("message");
        }
        SnackbarUtils.displaySnackbar(findViewById(R.id.main_content), message);

    }

    /**
     * Method to start new activity according to push notification type.
     *
     * @param type                type of push notification.
     * @param id                  id of content page.
     * @param listingTypeId       listingtypeId in case of sitereview.
     * @param notificationViewUrl web view url.
     * @param headerTitle         title of push notification.
     */
    public void startNewActivity(String type, int id, int listingTypeId, String notificationViewUrl,
                                 String headerTitle, JSONObject paramsObject) {
        Intent viewIntent;
        MyFcmListenerService.clearPushNotification();

        switch (type) {
            case "user":
            case "siteverify_verify":
            case "siteverify_new":
            case "siteverify_user_request":
            case "siteverify_admin_approve":
                viewIntent = new Intent(getApplicationContext(), userProfile.class);
                viewIntent.putExtra("user_id", id);
                startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case "activity_action":
            case "activity_comment":
                if (type.equals("activity_action") && paramsObject != null &&
                        paramsObject.optInt("attachment_count") > 1) {
                    viewIntent = new Intent(getApplicationContext(), Comment.class);
                    String mLikeCommentsUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds/likes-comments";
                    viewIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                    viewIntent.putExtra(ConstantVariables.SUBJECT_TYPE, ConstantVariables.AAF_MENU_TITLE);
                    viewIntent.putExtra(ConstantVariables.SUBJECT_ID, id);
                    viewIntent.putExtra("reactionsEnabled", paramsObject.optInt("reactionsEnabled"));

                    if (paramsObject.optJSONObject("feed_reactions") != null) {
                        viewIntent.putExtra("popularReactions", paramsObject.optJSONObject("feed_reactions").toString());
                    }
                } else {
                    viewIntent = new Intent(getApplicationContext(), SingleFeedPage.class);
                }
                viewIntent.putExtra(ConstantVariables.ACTION_ID, id);
                startActivity(viewIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "messages_conversation":
                viewIntent = GlobalFunctions.getIntentForModule(getApplicationContext(), id, type, null);
                viewIntent.putExtra("UserName", headerTitle);
                startActivity(viewIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                viewIntent = GlobalFunctions.getIntentForModule(getApplicationContext(), id, type, null);
                if (viewIntent != null
                        && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains(type)) {

                    if (type.equals("sitereview_listing") || type.equals("sitereview_review")) {
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, listingTypeId);
                    }

                    startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (notificationViewUrl != null && !notificationViewUrl.isEmpty()) {
                    if (ConstantVariables.WEBVIEW_ENABLE == 0) {
                        CustomTabUtil.launchCustomTab(this, GlobalFunctions.
                                getWebViewUrl(notificationViewUrl, mContext));
                    } else {
                        Intent webViewActivity = new Intent(MainActivity.this, WebViewActivity.class);
                        webViewActivity.putExtra("headerText", headerTitle);
                        webViewActivity.putExtra("url", notificationViewUrl);
                        startActivityForResult(webViewActivity, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
                break;
        }
    }


    public void changeLocation() {

        if (AppConstant.mLocationType != null && AppConstant.mLocationType.equals("specific")) {

            try {
                mSelectedLocationInfo = new HashMap<>();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(getResources().getString(R.string.location_popup_title));
                JSONObject multiLocations = null;
                int selectedPosition = 0;
                locationAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
                String locations = PreferencesUtils.getLocations(mContext);

                // Put Location key and label in adapter and hashmap
                if (locations != null && !locations.isEmpty()) {
                    multiLocations = new JSONObject(locations);
                    JSONArray localeNames = multiLocations.names();

                    for (int i = 0; i < multiLocations.length(); i++) {
                        String locationKey = localeNames.getString(i);
                        locationAdapter.add(multiLocations.getString(locationKey));
                        mSelectedLocationInfo.put(multiLocations.getString(locationKey), locationKey);
                    }
                }

                // Show the previously selected location selected in AlertBox
                if (multiLocations != null && multiLocations.has(PreferencesUtils.getDefaultLocation(mContext))) {
                    String defaultLang = multiLocations.optString(PreferencesUtils.getDefaultLocation(mContext));
                    selectedPosition = locationAdapter.getPosition(defaultLang);

                }

                alertBuilder.setSingleChoiceItems(locationAdapter, selectedPosition,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                String location = mSelectedLocationInfo.get(locationAdapter.getItem(which));
                                if (location != null && !location.isEmpty() && !PreferencesUtils.getDefaultLocation(mContext).equals(location)) {
                                    PreferencesUtils.updateDashBoardData(mContext,
                                            PreferencesUtils.DASHBOARD_DEFAULT_LOCATION, location);
                                    loadFragmentOnDefaultLocationChanged(true);
                                }
                            }
                        });

                alertBuilder.create().show();
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            if (AppConstant.isDeviceLocationChange == 1) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantVariables.CHANGE_DEFAULT_LOCATION, true);
                checkInLocationDialog = new CheckInLocationDialog(MainActivity.this, bundle);
                checkInLocationDialog.show();
            } else {
                SnackbarUtils.displaySnackbar(findViewById(R.id.main_content), getResources().getString(R.string.change_location_permission_message));
            }
        }
    }

    /**
     * Executing background task for sending post request for sing out
     */
    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(MainActivity.this, "",
                    getResources().getString(R.string.progress_dialog_message) + "....",
                    true, false);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Map<String, String> params = new HashMap<>();
            params.put("device_uuid", mAppConst.getDeviceUUID());

            mAppConst.postJsonRequest(AppConstant.DEFAULT_URL + "logout", params);
            MyFcmListenerService.clearPushNotification();
            ShortcutBadger.removeCount(mContext);
            try {
                // Delete existing FirebaseInstanceId and generate a new one for the new user.
                FirebaseInstanceId.getInstance().deleteInstanceId();
                FirebaseInstanceId.getInstance().getToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                LoginManager.getInstance().logOut();

                Intent homeScreen;
                if (ConstantVariables.INTRO_SLIDE_SHOW_ENABLED == 1) {
                    homeScreen = new Intent(MainActivity.this, NewLoginActivity.class);
                } else {
                    homeScreen = new Intent(MainActivity.this, LoginActivity.class);
                }

                homeScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeScreen);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!isSetLocation && checkInLocationDialog != null && checkInLocationDialog.isShowing()) {
            checkInLocationDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    showListOfAlbums();
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext,
                                findViewById(R.id.main_content), ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

            case ConstantVariables.PERMISSION_WAKE_LOCK:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WAKE_LOCK)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WAKE_LOCK, ConstantVariables.PERMISSION_WAKE_LOCK);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext,
                                findViewById(R.id.main_content), ConstantVariables.PERMISSION_WAKE_LOCK);

                    }
                }
                break;

            case ConstantVariables.ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestForDeviceLocation();
                } else {
                    // If user press deny in the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an expanation to the user After the user
                        // sees the explanation, try again to request the permission.

                        AlertDialogWithAction mAlertDialogWithAction = new AlertDialogWithAction(this);
                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                ConstantVariables.ACCESS_FINE_LOCATION);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(this, findViewById(R.id.app_bar_content),
                                ConstantVariables.ACCESS_FINE_LOCATION);
                    }
                }
                break;

        }
    }

    public boolean isCometchatAppInstalled(String packageName) {
        try {
            getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void buildGoogleApiClient() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(1000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     *  Receiver class for {@link FetchAddressIntentService}
     */

    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
                String addressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
                Gson gson = new Gson();
                Address address = gson.fromJson(addressOutput, Address.class);
                try {
                    parseAddress(address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: long: " + location.getLongitude());
                startIntentService(location);
                stopLocationUpdates();
            }
        };
    }

    private void parseAddress(Address address) throws JSONException {
        if (!isCurrentLocationSet) {
            isCurrentLocationSet = true;

            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine and join them.
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            String mNewLocation = TextUtils.join(System.getProperty("line.separator"), addressFragments);

            Toast.makeText(this, getResources().getString(R.string.current_location_text) + " " + mNewLocation, Toast.LENGTH_LONG).show();

            PreferencesUtils.updateDashBoardData(mContext, PreferencesUtils.DASHBOARD_DEFAULT_LOCATION, mNewLocation);

            JSONObject userDetail = (!mAppConst.isLoggedOutUser() && PreferencesUtils.getUserDetail(mContext) != null) ? new JSONObject(PreferencesUtils.getUserDetail(mContext)) : null;

            if (userDetail != null) {
                userDetail.put(PreferencesUtils.USER_LOCATION_LATITUDE, address.getLatitude());
                userDetail.put(PreferencesUtils.USER_LOCATION_LONGITUDE, address.getLongitude());
                PreferencesUtils.updateUserDetails(mContext, userDetail.toString());
            }

            JSONObject locationObject = new JSONObject();
            locationObject.put("country", address.getCountryName());
            locationObject.put("state", address.getAdminArea());
            locationObject.put("zipcode", address.getPostalCode());
            locationObject.put("city", address.getSubAdminArea());
            locationObject.put("countryCode", address.getCountryCode());
            locationObject.put("address", address.getLocality());
            locationObject.put("formatted_address", mNewLocation);
            locationObject.put("location", mNewLocation);
            locationObject.put("latitude", address.getLatitude());
            locationObject.put("longitude", address.getLongitude());

            if (userDetail != null) {
                setCurrentLocationOnServer(locationObject.toString(), userDetail.optInt("user_id"));
            }

        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(Location mLastLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void setCurrentLocationOnServer(String object, int user_id) {
        Log.d(TAG, "setCurrentLocationOnServer");
        String url = AppConstant.DEFAULT_URL + "memberlocation/edit-address?resource_type=user&user_id=" + user_id;
        HashMap<String, String> params = new HashMap<>();
        params.put("location", object);

        mAppConst.postJsonResponseForUrl(url, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
            }
        });
    }

    public void requestForDeviceLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> resultTask = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        resultTask.addOnSuccessListener(locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize location
            // requests here.
            if (isHomePage) {
                autoUpdateCurrentLocation();
            } else {
                loadFragmentOnGPSEnabled();
            }
        });
        resultTask.addOnFailureListener(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult((Activity) mContext,
                                ConstantVariables.PERMISSION_GPS_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we won't show the dialog.
                    break;
            }
        });
    }

    /**
     * Show case view dismiss listener override method
     */
    @Override
    public void onDismiss() {
        isShowCaseView = false;
        checkLocationPermission();
    }

    /* Check and request for device location to access all the location based features */
    private void checkLocationPermission() {
        isCurrentLocationSet = false;
        if (isSetLocation &&
                AppConstant.mLocationType != null &&
                AppConstant.mLocationType.equals("notspecific") &&
                AppConstant.isDeviceLocationEnable == 1) {
            isSetLocation = false;

            if (!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                mAppConst.requestForManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION, ConstantVariables.ACCESS_FINE_LOCATION);
            } else {
                requestForDeviceLocation();
            }
        }
    }
}