/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.common.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsUtil;
import com.fellopages.mobileapp.classes.modules.advancedGroups.AdvGroupUtil;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoUtil;
import com.fellopages.mobileapp.classes.modules.album.AlbumUtil;
import com.fellopages.mobileapp.classes.modules.blog.BlogUtil;
import com.fellopages.mobileapp.classes.modules.classified.ClassifiedUtil;
import com.fellopages.mobileapp.classes.modules.directoryPages.SitePageUtil;
import com.fellopages.mobileapp.classes.modules.event.EventUtil;
import com.fellopages.mobileapp.classes.modules.group.GroupUtil;
import com.fellopages.mobileapp.classes.modules.messages.InboxFragment;
import com.fellopages.mobileapp.classes.modules.messages.SentBoxFragment;
import com.fellopages.mobileapp.classes.modules.multipleListingType.MLTUtil;
import com.fellopages.mobileapp.classes.modules.music.MusicUtil;
import com.fellopages.mobileapp.classes.modules.notifications.MainNotificationFragment;
import com.fellopages.mobileapp.classes.modules.notifications.MyRequestsFragment;
import com.fellopages.mobileapp.classes.modules.poll.PollUtil;
import com.fellopages.mobileapp.classes.modules.pushnotification.MyFcmListenerService;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;
import com.fellopages.mobileapp.classes.modules.video.VideoUtil;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModulesHomeFragment extends Fragment implements TabLayout.OnTabSelectedListener {


    Context mContext;
    View rootView;
    ViewPager pager;
    TabLayout tabHost;
    FragmentAdapter adapter;
    private String mCurrentSelectedOption;
    private AppConstant mAppConst;

    public ModulesHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public static ModulesHomeFragment newInstance(Bundle bundle) {
        // Required  public constructor
        ModulesHomeFragment fragment = new ModulesHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mAppConst = new AppConstant(mContext);
        rootView = inflater.inflate(R.layout.view_pager, null);
        pager = rootView.findViewById(R.id.viewpager);
        tabHost = getActivity().findViewById(R.id.materialTabHost);
        tabHost.setVisibility(View.VISIBLE);
        tabHost.setTabMode(TabLayout.MODE_FIXED);

        mCurrentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);

        if(mCurrentSelectedOption != null){

            switch (mCurrentSelectedOption){

                case "core_main_blog":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(BlogUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_blog_tab));
                    adapter.addFragment(BlogUtil.getManagePageInstance(),
                            mContext.getResources().getString(R.string.my_blog_tab));
                    break;

                case "core_main_classified":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(ClassifiedUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_classifieds_tab));
                    adapter.addFragment(ClassifiedUtil.getManagePageInstance(),
                            mContext.getResources().getString(R.string.my_classified_tab));
                    break;

                case "core_main_album":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(AlbumUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.all_albums_tab));
                    adapter.addFragment(AlbumUtil.getPhotosFragmentInstance(),
                            mContext.getResources().getString(R.string.photos_tab));
                    adapter.addFragment(AlbumUtil.getManagePageInstance(),
                            mContext.getResources().getString(R.string.my_album_tab));
                    break;

                case "core_main_group":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(GroupUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_group_tab_title));
                    adapter.addFragment(GroupUtil.getManagePageInstance(),
                            mContext.getResources().getString(R.string.manage_group_tab_title));
                    break;

                case "core_main_video":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(VideoUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_video_tab));
                    adapter.addFragment(VideoUtil.getManagePageInstance(),
                            mContext.getResources().getString(R.string.my_video_tab));
                    break;

                case "core_main_event":

                    // Do not show My Events Tab for Logged-out users
                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(EventUtil.getBrowsePageInstance(), mContext.getResources().
                            getString(R.string.upcoming_events_tab_title));
                    adapter.addFragment(EventUtil.getPastEventsFragmentInstance(), mContext.getResources().
                            getString(R.string.past_events_tab_title));

                    if(!mAppConst.isLoggedOutUser()){
                        adapter.addFragment(EventUtil.getManagePageInstance(), mContext.getResources().
                                getString(R.string.my_events_tab_title));
                    }
                    break;

                case "core_main_music":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(MusicUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_music_tab));
                    adapter.addFragment(MusicUtil.getManagePageInstance(),
                            mContext.getResources().getString(R.string.my_music_tab));
                    break;

                case "core_main_poll":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(PollUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_polls_tab));
                    adapter.addFragment(PollUtil.getManagePageInstance(),
                            mContext.getResources().getString(R.string.my_polls_tab));
                    break;

                case "core_mini_messages":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(InboxFragment.newInstance(null),
                            mContext.getResources().getString(R.string.inbox_tab));
                    adapter.addFragment(SentBoxFragment.newInstance(null),
                            mContext.getResources().getString(R.string.sentbox_tab));
                    break;

                case "core_mini_notification":

                    MyFcmListenerService.clearPushNotification();
                    ShortcutBadger.removeCount(mContext);
                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(MainNotificationFragment.newInstance(null),
                            mContext.getResources().getString(R.string.all_notification));
                    adapter.addFragment(MyRequestsFragment.newInstance(null),
                            mContext.getResources().getString(R.string.my_request_tab));
                    break;

                case ConstantVariables.MLT_MENU_TITLE:

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    int mListingTypeId = PreferencesUtils.getCurrentSelectedListingId(mContext);
                    int mCanCreate = PreferencesUtils.getMLTCanCreate(mContext, mListingTypeId);
                    String label = PreferencesUtils.getCurrentSelectedListingLabel(mContext, mListingTypeId);

                    // Do not show Manage Tab for Logged-out users
                    if (mAppConst.isLoggedOutUser()) {
                        adapter.addFragment(MLTUtil.getBrowsePageInstance(), label);
                        adapter.addFragment(new BrowseCategoriesFragment(),
                                mContext.getResources().getString(R.string.category_tab));
                    } else {
                        adapter.addFragment(MLTUtil.getBrowsePageInstance(), label);
                        if (mCanCreate == 1) {
                            adapter.addFragment(MLTUtil.getManagePageInstance(), mContext.getResources().getString(R.string.manage_tab) +
                                    " " + label);
                            tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
                            tabHost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                        }
                        adapter.addFragment(new BrowseCategoriesFragment(),
                                mContext.getResources().getString(R.string.category_tab));
                    }
                    break;

                case "core_main_siteevent":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
                    tabHost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    adapter.addFragment(AdvEventsUtil.getBrowsePageInstance(), mContext.getResources().
                            getString(R.string.advanced_events_browse_tab_title));
                    adapter.addFragment(new AdvBrowseCategoriesFragment(), mContext.getResources().
                            getString(R.string.advanced_events_categories_tab_title));

                    // Do not show Manage tab to Logged-out users
                    if (!mAppConst.isLoggedOutUser()) {
                        adapter.addFragment(AdvEventsUtil.getManagePageInstance(), mContext.getResources().
                                getString(R.string.advanced_events_my_tab_title));

                        // Adding ticket and coupons tab when the ticket selling is enabled from admin panel.
                        if (PreferencesUtils.isTicketEnabled(mContext)) {
                            adapter.addFragment(AdvEventsUtil.getTicketPageInstance(), mContext.getResources().
                                    getString(R.string.advanced_events_my_tickets_tab_title));
                            adapter.addFragment(AdvEventsUtil.getCouponPageInstance(), mContext.getResources().
                                    getString(R.string.coupon_tab));
                        }
                    }

                    adapter.addFragment(AdvEventsUtil.getCalendarPageInstance(), mContext.getResources().
                            getString(R.string.advanced_events_calendar_tab_title));
                    PreferencesUtils.updateCurrentList(mContext, "browse_siteevent");
                    break;

                case "core_main_sitepage":
                case "sitepage":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(SitePageUtil.getBrowsePageInstance(), mContext.getResources().
                            getString(R.string.site_page_browse_pages_title));

                    if (!mAppConst.isLoggedOutUser()) {
                        tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
                        adapter.addFragment(SitePageUtil.getManagePageInstance(), mContext.getResources().
                                getString(R.string.site_page_manage_pages_title));
                    }
                    adapter.addFragment(new BrowseCategoriesFragment(), mContext.getResources().
                            getString(R.string.site_page_category_pages_title));
                    adapter.addFragment(SitePageUtil.getPopularPageInstance(), mContext.getResources().
                            getString(R.string.site_page_popular_pages_title));
                    break;

                case "core_main_sitegroup":

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(AdvGroupUtil.getBrowsePageInstance(), mContext.getResources().
                            getString(R.string.adv_group_browse_pages_title));
                    adapter.addFragment(new BrowseCategoriesFragment(), mContext.getResources().
                            getString(R.string.category_tab));
                    if (!mAppConst.isLoggedOutUser()) {
                        tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
                        adapter.addFragment(AdvGroupUtil.getManagePageInstance(), mContext.getResources().
                                getString(R.string.manage_group_tab_title));
                    }
                    tabHost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    break;

                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(AdvVideoUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.action_bar_title_video));
                    if (!mAppConst.isLoggedOutUser()) {
                        tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
                        adapter.addFragment(AdvVideoUtil.getManagePageInstance(),
                                mContext.getResources().getString(R.string.my_video_tab));
                        tabHost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    }
                    adapter.addFragment(new AdvBrowseCategoriesFragment(),
                            mContext.getResources().getString(R.string.category_tab));
                    break;

                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(AdvVideoUtil.getChannelBrowsePageInstance(),
                            mContext.getResources().getString(R.string.action_bar_title_channels));
                    if (!mAppConst.isLoggedOutUser()) {
                        tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
                        adapter.addFragment(AdvVideoUtil.getChannelManagePageInstance(),
                                mContext.getResources().getString(R.string.my_channel_tab));
                        tabHost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    }
                    adapter.addFragment(new AdvBrowseCategoriesFragment(),
                            mContext.getResources().getString(R.string.category_tab));
                    break;

                case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(AdvVideoUtil.getPlaylistBrowsePageInstance(),
                            mContext.getResources().getString(R.string.playlist_tab));
                    adapter.addFragment(AdvVideoUtil.getPlaylistManagePageInstance(),
                            mContext.getResources().getString(R.string.my_playlist_tab));
                    break;

                case ConstantVariables.STORE_MENU_TITLE:

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(StoreUtil.getBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_store));
                    if (!mAppConst.isLoggedOutUser()) {
                        adapter.addFragment(StoreUtil.getManagePageInstance(),
                                mContext.getResources().getString(R.string.manage_store));
                    }
                    adapter.addFragment(new BrowseCategoriesFragment(),
                            mContext.getResources().getString(R.string.category_tab));
                    break;

                case ConstantVariables.PRODUCT_MENU_TITLE:

                    adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
                    adapter.addFragment(StoreUtil.getProductsBrowsePageInstance(),
                            mContext.getResources().getString(R.string.browse_products));
                    if (!mAppConst.isLoggedOutUser()) {
                        tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
                        tabHost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                        adapter.addFragment(StoreUtil.getProductsManagePageInstance(),
                                mContext.getResources().getString(R.string.manage_products));
                    }
                    adapter.addFragment(new AdvBrowseCategoriesFragment(),
                            mContext.getResources().getString(R.string.advanced_events_categories_tab_title));
                    break;
            }

            if (pager != null && adapter != null) {
                pager.setAdapter(adapter);
                pager.setOffscreenPageLimit(adapter.getCount() + 1);
                tabHost.setupWithViewPager(pager);
                tabHost.addOnTabSelectedListener(this);
            }
        }

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabHost = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        if(mCurrentSelectedOption != null && mCurrentSelectedOption.equals("core_main_siteevent")){
            switch (tab.getPosition()) {
                case 0:
                case 1:
                    PreferencesUtils.updateCurrentList(mContext, "browse_siteevent");
                    break;
                case 2:
                    PreferencesUtils.updateCurrentList(mContext, "manage_siteevent");

            }
        }
        // when the tab is clicked the pager swipe content to the tab position
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

        if (adapter != null && adapter.getCount() > 0 && tab.getPosition() < adapter.getCount()
                && adapter.getItem(tab.getPosition()) != null) {
            Fragment fragment = adapter.getItem(tab.getPosition());
            fragment.onAttach(mContext);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }
}
