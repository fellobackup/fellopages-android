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

package com.fellopages.mobileapp.classes.modules.advancedActivityFeeds;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;
import com.fellopages.mobileapp.classes.common.fragments.ModulesHomeFragment;
import com.fellopages.mobileapp.classes.common.fragments.MyEventsFragment;
import com.fellopages.mobileapp.classes.common.ui.BadgeView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsMyEventsFragment;
import com.fellopages.mobileapp.classes.modules.friendrequests.FeedFriendRequests;
import com.fellopages.mobileapp.classes.modules.friendrequests.FriendRequests;
import com.fellopages.mobileapp.classes.modules.messages.NewMessagesFragment;
import com.fellopages.mobileapp.classes.modules.notifications.NotificationFragment;

import org.json.JSONException;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedHomeFragment extends Fragment implements View.OnClickListener {

    View rootView;
    AppConstant mAppConst;
    Bundle bundle = null;
    ViewPager pager;
    FragmentAdapter adapter;
    Context mContext;
    ImageButton home, friend_tab, event_tab, message, notification;
    BadgeView requestBadge, notificationBadge, messageBadge;
    String notificationCount, messageCount, requestCount;
    private FloatingActionButton mFabCreate;
    private int mSelectedTabPosition = 0;
    private IntentFilter intentFilter;
    private boolean isSaveFeeds = false;

    private SharedPreferences prefs = null;

    public static FeedHomeFragment newInstance() {
        return new FeedHomeFragment();
    }

    public FeedHomeFragment() {
        // Required empty public constructor
    }

    public static FeedHomeFragment newInstance(Bundle bundle) {
        // Required  public constructor
        FeedHomeFragment fragment = new FeedHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    /* Broadcast receiver for receiving broadcasting intent action for*/

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mContext != null && intent != null && mAppConst != null
                    && intent.getAction().equals(ConstantVariables.ACTION_COUNTER_UPDATE)) {
                // getting the notification updates
                getAlertNotifications();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantVariables.ACTION_COUNTER_UPDATE);
        if (getActivity() != null){
            prefs = getActivity().getSharedPreferences("first.app.run", Context.MODE_PRIVATE);
        }

        setHasOptionsMenu(true);
    }


    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggle = menu.findItem(R.id.viewToggle);
        if (toggle != null) {
            toggle.setVisible(false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mAppConst = new AppConstant(mContext);
        rootView = inflater.inflate(R.layout.view_pager, null);
        mFabCreate = getActivity().findViewById(R.id.create_fab);


        home = getActivity().findViewById(R.id.home_button);
        friend_tab = getActivity().findViewById(R.id.friend_tab);
        event_tab = getActivity().findViewById(R.id.event_tab);
        message = getActivity().findViewById(R.id.msg_tab);
        notification = getActivity().findViewById(R.id.notification_tab);
        home.setOnClickListener(this);
        friend_tab.setOnClickListener(this);
        event_tab.setOnClickListener(this);
        message.setOnClickListener(this);
        notification.setOnClickListener(this);

        mFabCreate.setVisibility(View.GONE);
        //Setting up badge view for notification count
        requestBadge = getActivity().findViewById(R.id.request_indicator);
        messageBadge = getActivity().findViewById(R.id.message_indicator);
        notificationBadge = getActivity().findViewById(R.id.notification_indicator);
        updateNotificationCounts(false);

        pager = rootView.findViewById(R.id.viewpager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedTabPosition = position;
                // when user do a swipe the selected tab change
                switch (position) {
                    case 0:
                        mFabCreate.hide();
                        home.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        friend_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        event_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        message.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        notification.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        break;

                    case 1:
                        mFabCreate.hide();
                        friend_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        event_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        home.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        message.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        notification.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        updateNotificationCounts(true);
                        break;

                    case 2:


                        mFabCreate.show();
                        friend_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        message.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        event_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        home.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        notification.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        updateNotificationCounts(true);
                        break;

                    case 3:
                        mFabCreate.setImageDrawable(ContextCompat.getDrawable(
                                getContext(), R.drawable.ic_action_new));

                        mFabCreate.setTag("core_main_message");

                        mFabCreate.show();
                        friend_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        notification.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        event_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        message.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        home.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        updateNotificationCounts(true);
                        break;

                    case 4:
                        mFabCreate.setImageDrawable(ContextCompat.getDrawable(
                                getContext(), R.drawable.ic_settings_white_24dp));
                        mFabCreate.setTag("core_main_notification");
                        friend_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        event_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        home.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        message.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        notification.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        updateNotificationCounts(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (pager != null) {
            adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());

            if (getArguments() != null) {
                bundle = getArguments();
                adapter.addFragmentWithIcon(FeedsFragment.newInstance(bundle),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_home),
                        mContext.getResources().getString(R.string.feed_tab_name));
                adapter.addFragmentWithIcon(FeedFriendRequests.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_friend_request_home),
                        mContext.getResources().getString(R.string.requests_tab_name));
                adapter.addFragmentWithIcon(MyEventsFragment.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_cropped_event_icon),
                        mContext.getResources().getString(R.string.query_search_event));
                adapter.addFragmentWithIcon(NewMessagesFragment.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_message)
                        , mContext.getResources().getString(R.string.message_tab_name));
                adapter.addFragmentWithIcon(NotificationFragment.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification)
                        , mContext.getResources().getString(R.string.notification_tab_name));

            } else {
                bundle = getArguments();
                adapter.addFragmentWithIcon(FeedsFragment.newInstance(bundle),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_home)
                        , mContext.getResources().getString(R.string.feed_tab_name));
                adapter.addFragmentWithIcon(FeedFriendRequests.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_friend_request_home),
                        mContext.getResources().getString(R.string.requests_tab_name));
                adapter.addFragmentWithIcon(MyEventsFragment.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_cropped_event_icon),
                        mContext.getResources().getString(R.string.query_search_event));
                adapter.addFragmentWithIcon(NewMessagesFragment.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_message)
                        , mContext.getResources().getString(R.string.message_tab_name));
                adapter.addFragmentWithIcon(NotificationFragment.newInstance(null),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification)
                        , mContext.getResources().getString(R.string.notification_tab_name));
            }

            if (prefs.getBoolean("firstrun", true)) {
                new Handler().postDelayed(() -> {
                    pager.setAdapter(adapter);
                    pager.setOffscreenPageLimit(adapter.getCount() + 1);

                    if (pager.getCurrentItem() == 0) {
                        home.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        friend_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        event_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        message.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                        notification.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                    }
                }, 1500);
                prefs.edit().putBoolean("firstrun", false).apply();
            } else {
                pager.setAdapter(adapter);
                pager.setOffscreenPageLimit(adapter.getCount() + 1);

                if (pager.getCurrentItem() == 0) {
                    home.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    friend_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                    event_tab.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                    message.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                    notification.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color));
                }
            }

        }

        if (!mAppConst.isLoggedOutUser()) {
            getAlertNotifications();
            // Kick off the first runnable task right away
            handler.postDelayed(runnableCode, ConstantVariables.FIRST_COUNT_REQUEST_DELAY);
        }

        return rootView;
    }

    private void getAlertNotifications() {
        // getting the notification updates
        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "notifications/new-updates",
                new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        if (jsonObject != null) {

                            PreferencesUtils.updateNotificationPreferences(mContext,
                                    jsonObject.optString("messages")
                                    , jsonObject.optString("notifications"),
                                    jsonObject.optString("friend_requests"),
                                    jsonObject.optString("cartProductsCount"));

                            updateNotificationCounts(false);
                            Intent intent = new Intent();
                            intent.setAction(ConstantVariables.ACTION_FEED_NOTIFICATIONS);
                            mContext.sendBroadcast(intent);

                        }
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                    }
                });
    }

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    // Define the task to be run here
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            // getting the notification updates
            getAlertNotifications();
            // Repeat this runnable code again every 60 seconds
            handler.postDelayed(runnableCode, ConstantVariables.REFRESH_NOTIFICATION_TIME);
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnableCode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(runnableCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        // Unregister receiver if activity is not in front
        if (getActivity() != null)
            getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int clickedPosition = mSelectedTabPosition;

        switch (id) {
            // Work for Scrolling the pages to top when the tabs will be reselected
            case R.id.home_button:
                mFabCreate.hide();
                pager.setCurrentItem(0, true);
                break;

            case R.id.friend_tab:
                Log.d("ThisIsTheTab ", "true");
                mFabCreate.hide();
                pager.setCurrentItem(1, true);
                break;

            case R.id.event_tab:
                mFabCreate.hide();
                pager.setCurrentItem(2, true);

            case R.id.msg_tab:
                pager.setCurrentItem(3, true);
                break;

            case R.id.notification_tab:
                pager.setCurrentItem(4, true);
                break;
        }

        // Attach fragment to scroll to top position when tab is reselected.
        if ((mSelectedTabPosition == pager.getCurrentItem())
                && (mSelectedTabPosition == clickedPosition) && adapter != null) {
            Fragment fragment = adapter.getItem(pager.getCurrentItem());
            fragment.onAttach(mContext);
        }
    }

    public void updateNotificationCounts(boolean isClicked) {
        notificationCount = PreferencesUtils.getNotificationsCounts(mContext, PreferencesUtils.NOTIFICATION_COUNT);
        messageCount = PreferencesUtils.getNotificationsCounts(mContext, PreferencesUtils.MESSAGE_COUNT);
        requestCount = PreferencesUtils.getNotificationsCounts(mContext, PreferencesUtils.FRIEND_REQ_COUNT);
        Log.d("requestCount ", requestCount);
        if (notificationCount != null && !notificationCount.equals("0")) {

            int combined = Integer.parseInt(notificationCount) + Integer.parseInt(requestCount);
            notificationBadge.setText(String.valueOf(combined));
            notificationBadge.setVisibility(View.VISIBLE);

            // Show app notification count on app icon in supported launcher
            ShortcutBadger.applyCount(mContext, Integer.valueOf(notificationCount));

            if (isClicked && mSelectedTabPosition == 3) {
                notificationBadge.setVisibility(View.GONE);
                PreferencesUtils.clearNotificationsCount(mContext, PreferencesUtils.NOTIFICATION_COUNT);
                ShortcutBadger.removeCount(mContext);
                mAppConst.markAllNotificationsRead();
            }
        } else if(!requestCount.equals("0")){
            Log.d("requestCount ", requestCount);
            notificationBadge.setText(requestCount);
            notificationBadge.setVisibility(View.VISIBLE);

            // Show app notification count on app icon in supported launcher
            ShortcutBadger.applyCount(mContext, Integer.valueOf(notificationCount));

            if (isClicked && mSelectedTabPosition == 3) {
                notificationBadge.setVisibility(View.GONE);
                PreferencesUtils.clearNotificationsCount(mContext, PreferencesUtils.NOTIFICATION_COUNT);
                ShortcutBadger.removeCount(mContext);
                mAppConst.markAllNotificationsRead();
            }
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
        if (messageCount != null && !messageCount.equals("0")) {
            messageBadge.setText(messageCount);
            messageBadge.setVisibility(View.VISIBLE);
            if (isClicked && mSelectedTabPosition == 2) {
                messageBadge.setVisibility(View.GONE);
                PreferencesUtils.clearNotificationsCount(mContext, PreferencesUtils.MESSAGE_COUNT);
                mAppConst.markAllMessageRead(null);
            }
        } else {
            messageBadge.setVisibility(View.GONE);
        }
        if (requestCount != null && !requestCount.equals("0")) {
//            requestBadge.setText(requestCount);
//            requestBadge.setVisibility(View.VISIBLE);
            if (isClicked && mSelectedTabPosition == 1) {
                requestBadge.setVisibility(View.GONE);
                PreferencesUtils.clearNotificationsCount(mContext, PreferencesUtils.FRIEND_REQ_COUNT);
                mAppConst.markAllFriendRequestsRead();
            }
        } else {
            requestBadge.setVisibility(View.GONE);
        }

    }
}
