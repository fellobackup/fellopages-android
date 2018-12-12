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

package com.fellopages.mobileapp.classes.common.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.Invite;
import com.fellopages.mobileapp.classes.common.activities.InviteGuest;
import com.fellopages.mobileapp.classes.common.fragments.AdvModulesInfoTabFragment;
import com.fellopages.mobileapp.classes.common.fragments.AdvReviewFragment;
import com.fellopages.mobileapp.classes.common.fragments.BlankFragment;
import com.fellopages.mobileapp.classes.common.fragments.FragmentUtils;
import com.fellopages.mobileapp.classes.common.fragments.InfoFragment;
import com.fellopages.mobileapp.classes.common.fragments.InfoTabFragment;
import com.fellopages.mobileapp.classes.common.fragments.MapViewFragment;
import com.fellopages.mobileapp.classes.common.fragments.MemberFragment;
import com.fellopages.mobileapp.classes.common.fragments.PhotoFragment;
import com.fellopages.mobileapp.classes.common.fragments.UserReviewFragment;
import com.fellopages.mobileapp.classes.common.interfaces.OnFragmentDataChangeListener;
import com.fellopages.mobileapp.classes.common.ui.CustomFloatingView;
import com.fellopages.mobileapp.classes.common.ui.fab.CustomFloatingActionButton;
import com.fellopages.mobileapp.classes.common.ui.fab.FloatingActionMenu;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedsFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventGuestDetailsFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsInfoTabFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsUtil;
import com.fellopages.mobileapp.classes.modules.advancedGroups.AdvGroupUtil;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoBrowseFragment;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoUtil;
import com.fellopages.mobileapp.classes.modules.album.AlbumUtil;
import com.fellopages.mobileapp.classes.modules.blog.BlogUtil;
import com.fellopages.mobileapp.classes.modules.classified.ClassifiedUtil;
import com.fellopages.mobileapp.classes.modules.directoryPages.SitePageUtil;
import com.fellopages.mobileapp.classes.modules.event.EventUtil;
import com.fellopages.mobileapp.classes.modules.group.GroupUtil;
import com.fellopages.mobileapp.classes.modules.multipleListingType.MLTUtil;
import com.fellopages.mobileapp.classes.modules.music.MusicUtil;
import com.fellopages.mobileapp.classes.modules.offers.BrowseOffersFragment;
import com.fellopages.mobileapp.classes.modules.poll.PollUtil;
import com.fellopages.mobileapp.classes.modules.user.BrowseMemberFragment;
import com.fellopages.mobileapp.classes.modules.user.profile.MemberInfoFragment;
import com.fellopages.mobileapp.classes.modules.video.VideoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***
 * View Pager Adapter to set Tabs
 */
public class ViewPageFragmentAdapter extends FragmentStatePagerAdapter implements
        ViewPager.OnPageChangeListener, View.OnClickListener, OnFragmentDataChangeListener {


    //Member Variables.
    private Context mContext;
    private ViewPager mViewPager;
    private TabLayout mSlidingTabs;
    private FloatingActionButton mFabCreate;
    private FloatingActionMenu mFabMenu;
    private CustomFloatingActionButton mFabSearch, mFabInvite, mFabMessage;
    private CustomFloatingView messageView;
    private View shadowView;
    private JSONArray mProfileTab;
    private Map<Integer, Fragment> mPageReferenceMap = new HashMap<>();
    private Map<Fragment, Integer> mFragmentMap = new HashMap<>();
    private Bundle mBundle;
    private AppConstant mAppConst;
    private String mCurrentSelectedModule, mMemberPageUrl;
    private int mTotalItemCount;
    private boolean mIsInfoDataUpdate = false, mIsReviewCreateUpdate = false;
    private String composeMessageUrl;
    private int isSiteVideoEnabled, mAdvVideosCount, mFriendTabPosition = 0;
    public int mPhotoTabPosition = 0;
    private BrowseListItems browseListItems;


    /**
     * Public Constructor to initialize the FragmentStatePagerAdapter.
     *
     * @param context         Context of calling class.
     * @param fragmentManager SupportFragmentManager instance.
     * @param profileTabs     Profile tabs array.
     * @param bundle          Bundle which contains the details.
     */
    public ViewPageFragmentAdapter(Context context, FragmentManager fragmentManager,
                                   JSONArray profileTabs, Bundle bundle) {
        super(fragmentManager);
        this.mContext = context;
        this.mProfileTab = profileTabs;
        this.mBundle = bundle;
        mAppConst = new AppConstant(mContext);
        mCurrentSelectedModule = mBundle.getString(ConstantVariables.EXTRA_MODULE_TYPE);
        if (mCurrentSelectedModule == null || mCurrentSelectedModule.isEmpty()) {
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        isSiteVideoEnabled = bundle.getInt(ConstantVariables.ADV_VIDEO_INTEGRATED, 0);
        mAdvVideosCount = bundle.getInt(ConstantVariables.ADV_VIDEOS_COUNT, 0);
        addVideosTab();
        getViews();
    }


    /**
     * Method to get views of fab button, view pager and menus.
     */
    public void getViews() {

        mSlidingTabs = (TabLayout) ((Activity) mContext).findViewById(R.id.slidingTabs);
        mViewPager = (ViewPager) ((Activity) mContext).findViewById(R.id.pager);
        mFabCreate = (FloatingActionButton) ((Activity) mContext).findViewById(R.id.create_fab);
        mFabMenu = (FloatingActionMenu) ((Activity) mContext).findViewById(R.id.fab_menu);
        mFabSearch = (CustomFloatingActionButton) ((Activity) mContext).findViewById(R.id.search);
        mFabInvite = (CustomFloatingActionButton) ((Activity) mContext).findViewById(R.id.invite);
        mFabMessage = (CustomFloatingActionButton) ((Activity) mContext).findViewById(R.id.message_members);
        messageView = ((Activity) mContext).findViewById(R.id.message_view);
        shadowView = ((Activity) mContext).findViewById(R.id.shadow);
        mFabCreate.hide();
        mViewPager.addOnPageChangeListener(this);
        mFabCreate.setOnClickListener(this);
        mFabSearch.setOnClickListener(this);
        mFabInvite.setOnClickListener(this);
        mFabMessage.setOnClickListener(this);
        messageView.setOnClickListener(this);
        FragmentUtils.setFragmentDataChangeListener(this);

    }


    /**
     * Method to update the fragment data.
     *
     * @param bundle               Bundle which contains the details.
     * @param profileTabs          Profile tabs array.
     * @param isInfoDataUpdate     If true then update the fragment.
     * @param isReviewCreateUpdate If true then update the fragment(Review).
     */
    public void updateData(Bundle bundle, JSONArray profileTabs, boolean isInfoDataUpdate,
                           boolean isReviewCreateUpdate) {
        this.mProfileTab = profileTabs;
        this.mBundle = bundle;
        this.mIsInfoDataUpdate = isInfoDataUpdate;
        this.mIsReviewCreateUpdate = isReviewCreateUpdate;
        if (mIsInfoDataUpdate || mIsReviewCreateUpdate) {
            notifyDataSetChanged();
        }
    }


    /**
     * Method to get fragment by passing the key(Position)
     *
     * @param key Key associated to fragment.
     * @return Returns the associated fragment.
     */
    public Fragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }


    /**
     * Method to get fragment position by passing the fragment instance.
     *
     * @param fragment Fragment for which position is to be find.
     * @return Returns the position of the fragment.
     */
    public int getFragmentPosition(Fragment fragment) {
        return mFragmentMap.get(fragment);
    }


    /**
     * Method to Load fragment on tabs according to position.
     *
     * @param position Position of the fragment.
     * @return Returns the fragment with bundle data.
     */
    public Fragment getFragmentToBeLoaded(int position) {

        String tabName;
        Fragment loadFragment = null;
        try {
            JSONObject profileTabObject = mProfileTab.getJSONObject(position);
            tabName = profileTabObject.getString("name");
            String redirectUrl = null;
            JSONObject urlParams = profileTabObject.optJSONObject("urlParams");
            int totalItemCount = profileTabObject.optInt("totalItemCount");
            String url = profileTabObject.optString("url");
            if (url != null && !url.isEmpty()) {
                redirectUrl = AppConstant.DEFAULT_URL + url;
                if (urlParams != null && urlParams.length() != 0) {
                    JSONArray urlParamsNames = urlParams.names();
                    Map<String, String> params = new HashMap<>();
                    for (int j = 0; j < urlParams.length(); j++) {
                        String name = urlParamsNames.optString(j);
                        String value = urlParams.optString(name);
                        if (tabName.equals("photos")) {
                            redirectUrl += "/" + value;
                        } else {
                            params.put(name, value);
                        }
                    }

                    if (params.size() != 0) {
                        redirectUrl = mAppConst.buildQueryString(redirectUrl, params);
                    }
                }
            }

            Bundle bundle = new Bundle();
            switch (mCurrentSelectedModule) {
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.EVENT_MENU_TITLE:
                    bundle.putString(ConstantVariables.MODULE_NAME, "groupEventProfile");
                    bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                    if (mBundle.containsKey(ConstantVariables.PROFILE_RSVP_VALUE)) {
                        bundle.putInt(ConstantVariables.PROFILE_RSVP_VALUE, mBundle.getInt(ConstantVariables.PROFILE_RSVP_VALUE));
                        bundle.putBoolean("showRsvp", true);
                    }

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    bundle.putString(ConstantVariables.VIEW_PAGE_URL, mBundle.getString(ConstantVariables.VIEW_PAGE_URL));
                    break;

                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                    bundle.putInt(ConstantVariables.VIEW_PAGE_ID, mBundle.getInt(ConstantVariables.CONTENT_ID));
                    break;

                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    bundle.putInt(ConstantVariables.VIEW_PAGE_ID, mBundle.getInt(ConstantVariables.CONTENT_ID));
                    bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                    break;

                case ConstantVariables.MLT_MENU_TITLE:
                    bundle.putInt(ConstantVariables.LISTING_ID, mBundle.getInt(ConstantVariables.LISTING_ID));
                    bundle.putInt(ConstantVariables.MLT_VIEW_TYPE, mBundle.getInt(ConstantVariables.MLT_VIEW_TYPE));
                    bundle.putInt(ConstantVariables.LISTING_TYPE_ID, mBundle.getInt(ConstantVariables.LISTING_TYPE_ID));
                    break;

                default:
                    bundle.putString(ConstantVariables.MODULE_NAME, "userProfile");
                    bundle.putInt(ConstantVariables.USER_ID, mBundle.getInt(ConstantVariables.USER_ID));
                    break;
            }
            bundle.putInt(ConstantVariables.CAN_UPLOAD, mBundle.getInt(ConstantVariables.CAN_UPLOAD));
            bundle.putInt(ConstantVariables.CONTENT_ID, mBundle.getInt(ConstantVariables.CONTENT_ID));
            bundle.putString(ConstantVariables.RESPONSE_OBJECT, mBundle.getString(ConstantVariables.RESPONSE_OBJECT));
            bundle.putString(ConstantVariables.SUBJECT_TYPE, mBundle.getString(ConstantVariables.SUBJECT_TYPE));
            bundle.putInt(ConstantVariables.SUBJECT_ID, mBundle.getInt(ConstantVariables.SUBJECT_ID));
            bundle.putString(ConstantVariables.CONTENT_TITLE, mBundle.getString(ConstantVariables.CONTENT_TITLE));

            bundle.putBoolean(ConstantVariables.IS_PROFILE_PAGE_REQUEST, true);
            bundle.putInt(ConstantVariables.TOTAL_ITEM_COUNT, totalItemCount);
            bundle.putString(ConstantVariables.FRAGMENT_NAME, tabName);
            bundle.putString(ConstantVariables.URL_STRING, redirectUrl);
            bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
            bundle.putString(ConstantVariables.TAB_LABEL, profileTabObject.optString("label"));
            bundle.putBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST, (position == 0));

            switch (tabName) {

                case "update":
                    loadFragment = new FeedsFragment();
                    break;

                case "information":
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            loadFragment = new MemberInfoFragment();
                            bundle.putString(ConstantVariables.FORM_TYPE, "eventInfo");
                            break;
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            loadFragment = new MemberInfoFragment();
                            bundle.putString(ConstantVariables.FORM_TYPE, "info_tab");
                            break;
                        default:
                            loadFragment = new AdvModulesInfoTabFragment();
                            break;
                    }
                    break;

                case "occurence_info":
                    loadFragment = new MemberInfoFragment();
                    bundle.putString(ConstantVariables.FORM_TYPE, "occurrenceInfo");
                    break;

                case "description":
                    loadFragment = new MemberInfoFragment();
                    bundle.putString(ConstantVariables.FORM_TYPE, "description");
                    break;

                case "announcement":
                    loadFragment = new MemberInfoFragment();
                    bundle.putString(ConstantVariables.FORM_TYPE, "announcement");
                    break;

                case "specification":
                    loadFragment = new InfoTabFragment();
                    break;
                case "map":
                    JSONObject mapObject = profileTabObject.optJSONObject("location");
                    bundle.putDouble("latitude", mapObject.optDouble("latitude"));
                    bundle.putDouble("longitude", mapObject.optDouble("longitude"));
                    bundle.putString("location", mapObject.optString("label"));
                    loadFragment = MapViewFragment.newInstance(bundle);
                    break;
                case "info":
                    switch (mCurrentSelectedModule) {

                        case ConstantVariables.GROUP_MENU_TITLE:
                        case ConstantVariables.EVENT_MENU_TITLE:
                            loadFragment = new InfoFragment();
                            break;

                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            loadFragment = new AdvEventsInfoTabFragment();
                            break;

                        default:
                            bundle.putString(ConstantVariables.FORM_TYPE, "memberInfo");
                            loadFragment = new MemberInfoFragment();
                            break;
                    }
                    break;

                case "overview":
                    if (mCurrentSelectedModule.equals(ConstantVariables.MLT_MENU_TITLE)) {
                        loadFragment = new InfoFragment();
                    } else {
                        bundle.putString(ConstantVariables.FORM_TYPE, "overview");
                        loadFragment = new MemberInfoFragment();
                    }
                    break;

                case "organizer_info":
                    bundle.putString(ConstantVariables.FORM_TYPE, "organizerInfo");
                    loadFragment = new MemberInfoFragment();
                    break;

                case "friends":
                    loadFragment = new BrowseMemberFragment();
                    bundle.putBoolean("isMemberFriends", true);
                    mFriendTabPosition = position;
                    break;

                case "followers":
                    loadFragment = new BrowseMemberFragment();
                    bundle.putBoolean("isMemberFollowers", true);
                    break;

                case "following":
                    loadFragment = new BrowseMemberFragment();
                    bundle.putBoolean("isMemberFollowing", true);
                    break;

                case "blog":
                    loadFragment = BlogUtil.getBrowsePageInstance();
                    bundle.putBoolean("isMemberBlogs", true);
                    break;

                case "group":
                    loadFragment = GroupUtil.getBrowsePageInstance();
                    bundle.putBoolean("isMemberGroups", true);
                    break;

                case "organizer_events":
                    loadFragment = AdvEventsUtil.getManagePageInstance();
                    bundle.putBoolean("isOrganizerEvents", true);
                    break;

                case "event":
                    if (profileTabObject.optBoolean("isAdvancedModuleEnabled")) {
                        loadFragment = AdvEventsUtil.getManagePageInstance();
                    } else {
                        loadFragment = EventUtil.getBrowsePageInstance();
                    }
                    bundle.putBoolean("isMemberEvents", true);
                    break;

                case "album":
                    if (bundle.containsKey("isCoverRequest")) {
                        bundle.putBoolean("isMemberAlbums", true);
                    } else {
                        switch (mCurrentSelectedModule) {
                            case ConstantVariables.SITE_PAGE_MENU_TITLE:
                                bundle.putBoolean("isSitePageAlbums", true);
                                break;

                            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                                bundle.putBoolean("isSiteGroupAlbums", true);
                                break;

                            default:
                                bundle.putBoolean("isMemberAlbums", true);
                                break;
                        }
                    }
                    loadFragment = AlbumUtil.getBrowsePageInstance();
                    break;

                case "classified":
                    loadFragment = ClassifiedUtil.getBrowsePageInstance();
                    bundle.putBoolean("isMemberClassifieds", true);
                    break;

                case "music":
                    loadFragment = MusicUtil.getBrowsePageInstance();
                    bundle.putBoolean("isMemberMusics", true);
                    break;

                case "video":
                case "videos":
                    bundle.putInt(ConstantVariables.ADV_VIDEO_INTEGRATED, isSiteVideoEnabled);
                    bundle.putBoolean("isProfilePageRequest", true);
                    List includedModulesList = Arrays.asList(ConstantVariables.INCLUDED_MODULES_ARRAY);
                    List deletedModulesList = Arrays.asList(ConstantVariables.DELETED_MODULES);
                    boolean isAdvancedModuleEnabled = profileTabObject.optBoolean("isAdvancedModuleEnabled", false);

                    // When the advanced video module is enabled and exist in android app
                    // then load the advanced video browse page.
                    if (isAdvancedModuleEnabled && mCurrentSelectedModule.equals("userProfile")
                            && includedModulesList.contains(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                            && !deletedModulesList.contains(ConstantVariables.ADV_VIDEO_MENU_TITLE)) {
                        loadFragment = AdvVideoUtil.getBrowsePageInstance();
                    } else if (mCurrentSelectedModule.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)) {
                        loadFragment = AdvVideoUtil.getBrowsePageInstance();
                    } else {
                        loadFragment = VideoUtil.getBrowsePageInstance();
                    }

                    if (isSiteVideoEnabled == 0) {
                        String viewPageUrl = null;
                        switch (mCurrentSelectedModule) {
                            case ConstantVariables.MLT_MENU_TITLE:
                                redirectUrl += "?listingtype_id=" + mBundle.getInt(ConstantVariables.LISTING_TYPE_ID);
                                viewPageUrl = AppConstant.DEFAULT_URL + "listings/video/view/"
                                        + mBundle.getInt(ConstantVariables.LISTING_ID) + "?gutter_menu=1"
                                        + "&listingtype_id=" + mBundle.getInt(ConstantVariables.LISTING_TYPE_ID)
                                        + "&video_id=";
                                bundle.putString(ConstantVariables.URL_STRING, redirectUrl);
                                bundle.putString(ConstantVariables.VIDEO_SUBJECT_TYPE, ConstantVariables.MLT_VIDEO_MENU_TITLE);
                                break;

                            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                                viewPageUrl = AppConstant.DEFAULT_URL + "advancedevents/video/"
                                        + mBundle.getInt(ConstantVariables.CONTENT_ID) + "/";
                                bundle.putString(ConstantVariables.VIDEO_SUBJECT_TYPE, ConstantVariables.ADV_EVENT_VIDEO_MENU_TITLE);
                                bundle.putBoolean("isAdvEventProfileVideos", true);
                                break;

                            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                                viewPageUrl = AppConstant.DEFAULT_URL + "advancedgroups/video/view/"
                                        + mBundle.getInt(ConstantVariables.CONTENT_ID) + "/";
                                bundle.putString(ConstantVariables.VIDEO_SUBJECT_TYPE, ConstantVariables.ADV_GROUPS_VIDEO_MENU_TITLE);
                                break;
                        }
                        if (viewPageUrl != null && !viewPageUrl.isEmpty()) {
                            bundle.putString(ConstantVariables.VIEW_PAGE_URL, viewPageUrl);
                        }
                    }

                    break;

                case "channel":
                    loadFragment = AdvVideoUtil.getChannelBrowsePageInstance();
                    bundle.putBoolean("isMemberChannels", true);
                    break;

                case "poll":
                    loadFragment = PollUtil.getBrowsePageInstance();
                    bundle.putBoolean("isMemberPolls", true);
                    break;

                case ConstantVariables.MLT_MENU_TITLE:
                    loadFragment = MLTUtil.getBrowsePageInstance();
                    if (mCurrentSelectedModule.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)) {
                        bundle.putBoolean("isAdvGroupsMLT", true);
                    } else {
                        bundle.putBoolean("isMemberMLT", true);
                    }
                    break;


                case "photos":
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                            loadFragment = AlbumUtil.getBrowsePageInstance();
                            bundle.putBoolean("isSitePageAlbums", true);
                            break;

                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            loadFragment = AlbumUtil.getBrowsePageInstance();
                            bundle.putBoolean("isSiteGroupAlbums", true);
                            break;

                        default:
                            loadFragment = new PhotoFragment();
                            break;
                    }
                    mPhotoTabPosition = position;
                    break;

                case "details":
                    loadFragment = new InfoTabFragment();
                    break;

                case "reviews":
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.MLT_MENU_TITLE:
                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            loadFragment = new UserReviewFragment();
                            break;

                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            loadFragment = new AdvReviewFragment();
                            redirectUrl = AppConstant.DEFAULT_URL + "advancedgroups/reviews/browse/"
                                    + mBundle.getInt(ConstantVariables.CONTENT_ID) + "?getRating=1&page=1";
                            break;

                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                            loadFragment = new AdvReviewFragment();
                            redirectUrl = AppConstant.DEFAULT_URL + "sitepage/reviews/browse/"
                                    + mBundle.getInt(ConstantVariables.CONTENT_ID) + "?getRating=1&page=1";
                            break;
                    }
                    bundle.putString(ConstantVariables.URL_STRING, redirectUrl);
                    break;

                case "sitepage":
                    loadFragment = SitePageUtil.getBrowsePageInstance();
                    bundle.putBoolean("isMemberPages", true);
                    break;

                case "advevents":
                    loadFragment = AdvEventsUtil.getBrowsePageInstance();
                    if (urlParams != null) {
                        bundle.putString("parent_type", urlParams.optString("parent_type"));
                        bundle.putInt("parent_id", urlParams.optInt("parent_id"));
                    }
                    bundle.putBoolean("isSiteGroupEvents", true);
                    break;

                case "sitegroup":
                    loadFragment = AdvGroupUtil.getBrowsePageInstance();
                    bundle.putBoolean("isMemberSiteGroup", true);
                    break;

                case "offer":
                case "coupons":
                    loadFragment = new BrowseOffersFragment();
                    break;

                case "members":
                case "Subscriber":
                    mMemberPageUrl = redirectUrl;
                    mTotalItemCount = totalItemCount;
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.GROUP_MENU_TITLE:
                        case ConstantVariables.EVENT_MENU_TITLE:
                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            loadFragment = new MemberFragment();
                            break;

                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            bundle.putString("list_name", "members_siteevent");
                            loadFragment = new AdvEventGuestDetailsFragment();
                            break;
                    }
                    break;

                case "occurence_index":
                    bundle.putString("list_name", "occurrence_siteevent");
                    loadFragment = new AdvEventGuestDetailsFragment();
                    break;

                case "where-to-buy":
                    loadFragment = MLTUtil.getWhereToBuyInstance();
                    break;

                default:
                    loadFragment = new BlankFragment();
                    break;
            }

            mPageReferenceMap.put(position, loadFragment);
            mFragmentMap.put(loadFragment, position);
            if (loadFragment != null) {
                loadFragment.setArguments(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadFragment;
    }


    @Override
    public Fragment getItem(int position) {
        return getFragmentToBeLoaded(position);
    }


    @Override
    public int getCount() {
        return mProfileTab.length();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        JSONObject profileTabObject;
        String tabLabel = null;
        int totalItemCount;
        try {
            profileTabObject = mProfileTab.optJSONObject(position);
            tabLabel = profileTabObject.optString("label").trim();
            totalItemCount = profileTabObject.optInt("totalItemCount");
            if (totalItemCount != 0)
                tabLabel += " (" + totalItemCount + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tabLabel;
    }


    @Override
    public int getItemPosition(Object object) {

        // Checking the notify fragment and update the data accordingly.
        if (mIsInfoDataUpdate) {
            if (object instanceof AdvEventsInfoTabFragment) {
                ((AdvEventsInfoTabFragment) object).updateData(mBundle, true);
            } else if (object instanceof InfoTabFragment) {
                ((InfoTabFragment) object).updateData(mBundle, true);
            } else if (object instanceof AdvModulesInfoTabFragment) {
                ((AdvModulesInfoTabFragment) object).updateData(mBundle, true);
            } else if (object instanceof InfoFragment) {
                ((InfoFragment) object).updateData(mBundle);
            }
        }

        // Update the review fragment when there is any change in review(Create/Update).
        if (mIsReviewCreateUpdate) {
            if (object instanceof AdvReviewFragment) {
                ((AdvReviewFragment) object).onRefresh();
            } else if (object instanceof UserReviewFragment) {
                ((UserReviewFragment) object).onRefresh();
            }
        }

        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }


    @Override
    public void onFragmentTitleUpdated(Fragment fragment, int count) {

        if (mFragmentMap != null && mSlidingTabs != null && mViewPager != null) {

            // Called when the tab item count is changed for any fragment.
            int position = getFragmentPosition(fragment);

            TabLayout.Tab tab = mSlidingTabs.getTabAt(position);
            if (tab != null) {
                String title = mViewPager.getAdapter().getPageTitle(position).toString();
                if (title.contains("(")) {
                    title = title.substring(0, title.indexOf("("));
                    title = title + "(" + count + ")";
                } else {
                    title = title + " (" + count + ")";
                }
                tab.setText(title);
            }
        }
    }

    @Override
    public void showMessageGuestIcon(Fragment fragment, boolean showMessageIcon, String url) {

        composeMessageUrl = url;
        int position = getFragmentPosition(fragment);
        showFabIcons(position, showMessageIcon);
    }

    private void showFabIcons(int position, boolean showMessageIcon) {

        if (mBundle.getString(ConstantVariables.INVITE_GUEST) == null && !showMessageIcon) {
            mFabCreate.show();
            mFabCreate.setImageResource(R.drawable.ic_action_search);
            mFabCreate.setTag(position);
            mFabMenu.hideMenu(false);

        } else {
            if (mCurrentSelectedModule.equals(ConstantVariables.GROUP_MENU_TITLE)
                    || mCurrentSelectedModule.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)) {
                mFabSearch.setLabelText(mContext.getResources().getString(R.string.search_members));
                mFabInvite.setLabelText(mContext.getResources().getString(R.string.title_activity_invite));
                mFabMessage.setLabelText(mContext.getResources().getString(R.string.message_members));
            } else {
                mFabSearch.setLabelText(mContext.getResources().getString(R.string.search_guests));
                mFabInvite.setLabelText(mContext.getResources().getString(R.string.title_activity_invite_event));
                mFabMessage.setLabelText(mContext.getResources().getString(R.string.message_guest));
            }

            if (mBundle.getString(ConstantVariables.INVITE_GUEST) != null) {
                mFabInvite.setVisibility(View.VISIBLE);
            }

            if (showMessageIcon) {
                mFabMessage.setVisibility(View.VISIBLE);
            }

            mFabMenu.setVisibility(View.VISIBLE);
            mFabCreate.hide();
            mFabMenu.showMenu(false);
            mFabMenu.setClosedOnTouchOutside(true);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        try {
            JSONObject jsonObject = mProfileTab.getJSONObject(position);
            String tabName = jsonObject.getString("name");
            messageView.setVisibility(View.GONE);
            shadowView.setVisibility(View.GONE);
            switch (tabName) {
                case "photos":
                    if (mBundle.getInt(ConstantVariables.CAN_UPLOAD) == 1) {
                        mFabCreate.show();
                        mFabCreate.setImageResource(R.drawable.ic_action_new);
                        mFabCreate.setTag(position);
                    } else {
                        mFabCreate.hide();
                    }
                    mFabMenu.hideMenu(false);
                    break;

                case "video":
                case "videos":
                    if (mBundle.getInt(ConstantVariables.CAN_UPLOAD_VIDEO) == 1) {
                        mFabCreate.show();
                        mFabCreate.setImageResource(R.drawable.ic_action_new);
                        mFabCreate.setTag(position);
                    } else {
                        mFabCreate.hide();
                    }
                    mFabMenu.hideMenu(false);
                    break;

                case "members":
                    showFabIcons(position, false);
                    break;

                case "update":
                    mFabCreate.hide();
                    mFabMenu.hideMenu(false);
                    if (mCurrentSelectedModule.equals("userProfile")) {
                        checkForMessageOption(browseListItems);
                    } else {
                        checkForMessageOwnerOption(browseListItems);
                    }
                    break;

                default:
                    mFabCreate.hide();
                    mFabMenu.hideMenu(false);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.create_fab:
                int position = (int) view.getTag();
                if (getFragment(position) instanceof PhotoFragment) {
                    ((PhotoFragment) getFragment(position)).checkPermission();

                } else if (getFragment(position) instanceof MemberFragment
                        || getFragment(position) instanceof AdvEventGuestDetailsFragment) {
                    launchSearchPageActivity();
                } else if (getFragment(position) instanceof AdvVideoBrowseFragment) {
                    launchVideoUploadingActivity(getFragment(position));
                }
                break;

            case R.id.search:
                mFabMenu.close(true);
                launchSearchPageActivity();
                break;

            case R.id.invite:
                mFabMenu.close(true);
                if (mCurrentSelectedModule.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)
                        || mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {

                    Intent inviteIntent = new Intent(mContext, InviteGuest.class);
                    inviteIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                    inviteIntent.putExtra(ConstantVariables.URL_STRING, mBundle.getString(ConstantVariables.INVITE_GUEST));
                    mContext.startActivity(inviteIntent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    Intent invite = new Intent(mContext, Invite.class);
                    invite.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                    invite.putExtra(ConstantVariables.URL_STRING, mBundle.getString(ConstantVariables.INVITE_GUEST));
                    mContext.startActivity(invite);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.message_members:
                mFabMenu.close(true);
                Intent message = new Intent(mContext, CreateNewEntry.class);
                message.putExtra(ConstantVariables.CREATE_URL, AppConstant.DEFAULT_URL + composeMessageUrl);
                message.putExtra(ConstantVariables.CONTENT_ID, mBundle.getInt(ConstantVariables.CONTENT_ID));
                message.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                message.putExtra(ConstantVariables.FORM_TYPE, "compose_message");
                mContext.startActivity(message);
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;

            case R.id.message_view:
                GlobalFunctions.messageOwner(mContext, mCurrentSelectedModule, browseListItems);
                break;
        }
    }

    /***
     * Method to launch video creation page.
     * @param fragment Fragment instance.
     */
    private void launchVideoUploadingActivity(Fragment fragment) {
        Intent intent = new Intent(mContext, CreateNewEntry.class);
        intent.putExtra("channel_id", mBundle.getInt(ConstantVariables.SUBJECT_ID));
        intent.putExtra(ConstantVariables.CREATE_URL, AppConstant.DEFAULT_URL + "advancedvideos/create");
        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_MENU_TITLE);
        fragment.startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Method to launch Search Page activity.
     */
    public void launchSearchPageActivity() {

        Intent membersIntent = new Intent(mContext, FragmentLoadActivity.class);
        membersIntent.putExtra(ConstantVariables.URL_STRING, mMemberPageUrl);
        membersIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
        membersIntent.putExtra(ConstantVariables.FRAGMENT_NAME, "members");
        membersIntent.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, mTotalItemCount);
        membersIntent.putExtra(ConstantVariables.IS_WAITING, false);
        membersIntent.putExtra("isSearchRequest", true);
        membersIntent.putExtra(ConstantVariables.CONTENT_ID, mBundle.getInt(ConstantVariables.CONTENT_ID));
        membersIntent.putExtra(ConstantVariables.CONTENT_TITLE, mBundle.getString(ConstantVariables.CONTENT_TITLE));
        if (mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
            membersIntent.putExtra("list_name", "members_siteevent");
        }
        mContext.startActivity(membersIntent);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
    }

    private void addVideosTab() {

        String siteVideoUrl = "advancedvideos/index?subject_id=" + mBundle.getInt(ConstantVariables.SUBJECT_ID)
                + "&subject_type=" + mBundle.getString(ConstantVariables.SUBJECT_TYPE);
        boolean isVideoTabExist = false;
        try {
            for (int i = 0; i < mProfileTab.length(); i++) {
                JSONObject singleJsonObject = mProfileTab.optJSONObject(i);
                String tabName = singleJsonObject.optString("name");

                //Check if Adv Video is integrated with the plugin and replace the url with the Adv Video plugin

                if (isSiteVideoEnabled == 1 && (tabName.equals("video") || tabName.equals("videos"))) {
                    isVideoTabExist = true;
                    singleJsonObject.put("url", siteVideoUrl);
                    singleJsonObject.put("totalItemCount", mAdvVideosCount);
                }

            }

            // If Already Video Tab is not coming in response then add it with Adv Video url
            if (isSiteVideoEnabled == 1 && !isVideoTabExist) {
                JSONObject singleJsonObject = new JSONObject();
                singleJsonObject.put("totalItemCount", mAdvVideosCount);
                singleJsonObject.put("name", "videos");
                singleJsonObject.put("label", mContext.getResources().
                        getString(R.string.action_bar_title_video));
                singleJsonObject.put("url", siteVideoUrl);
                mProfileTab.put(singleJsonObject);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isFriendTabExist() {
        return mFriendTabPosition != 0;
    }

    public void loadFriendTab() {
        if (mFriendTabPosition != 0 && mViewPager != null && getItem(mFriendTabPosition) != null
                && getItem(mFriendTabPosition) instanceof BrowseMemberFragment) {
            mViewPager.setCurrentItem(mFriendTabPosition);
        }
    }

    /**
     * Method to check for the message option at user profile page.
     *
     * @param browseListItems BrowseListItems instance which contains the user info.
     */
    public void checkForMessageOption(BrowseListItems browseListItems) {
        this.browseListItems = browseListItems;
        if (mViewPager != null && mViewPager.getCurrentItem() == 0
                && browseListItems != null && browseListItems.getUserId() != 0) {
            messageView.bringToFront();
            messageView.setVisibility(View.VISIBLE);
            shadowView.setVisibility(View.VISIBLE);
        } else {
            messageView.setVisibility(View.GONE);
            shadowView.setVisibility(View.GONE);
        }
    }

    /**
     * Method to check for the message owner option.
     *
     * @param browseListItems BrowseListItems instance which contains the user info.
     */
    public void checkForMessageOwnerOption(BrowseListItems browseListItems) {
        this.browseListItems = browseListItems;
        if (mViewPager != null && mViewPager.getCurrentItem() == 0
                && browseListItems != null && browseListItems.getMessageOwnerUrl() != null) {
            messageView.bringToFront();
            messageView.setVisibility(View.VISIBLE);
            shadowView.setVisibility(View.VISIBLE);
        } else {
            messageView.setVisibility(View.GONE);
            shadowView.setVisibility(View.GONE);
        }
    }
}
