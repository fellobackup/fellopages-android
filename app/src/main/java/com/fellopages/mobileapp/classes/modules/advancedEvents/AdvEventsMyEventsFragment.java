/*
 *   Copyright (c) 2015 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.modules.advancedEvents;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesManageDataAdapter;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdvEventsMyEventsFragment extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, AdapterView.OnItemSelectedListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private String mMyEventsUrl, mCurrentSelectedModule, mEventType = "-1";
    private View rootView, footerView;
    private Context mContext;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1, mUserId;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private GridViewWithHeaderAndFooter mGridView;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private AdvModulesManageDataAdapter mManageDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false, isVisibleToUser = false, isMemberEvents = false,
            isOrganizerEvents = false, isAdLoaded = false, isCommunityAds = false, isFirstTab = false;
    private Snackbar snackbar;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private NativeAdsManager listNativeAdsManager;


    public static AdvEventsMyEventsFragment newInstance(Bundle bundle) {
        // Required public constructor
        AdvEventsMyEventsFragment fragment = new AdvEventsMyEventsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(this);

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.grid_view_layout, null);
        Log.d("AdvEventsMyEvents ", "AdvEventsMyEventsFragment");
        footerView = CustomViews.getFooterView(inflater);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        mGridView = rootView.findViewById(R.id.gridView);
        mGridView.setOnScrollListener(this);
        mGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);
        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);
        ViewCompat.setNestedScrollingEnabled(mGridView, true);

        if(ConstantVariables.ENABLE_ADV_EVENT_ADS == 1) {
            switch (ConstantVariables.ADV_EVENT_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_COMMUNITY_ADS:
                    isCommunityAds = true;
                    break;
            }
        }

        pageNumber = 1;

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_siteevent")){
            PreferencesUtils.updateCurrentModule(mContext,"core_main_siteevent");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }
        mMyEventsUrl = UrlUtil.MANAGE_ADV_EVENTS_URL + "&page=" + pageNumber +"&showEventType=all";

        if(getArguments() != null) {

            Bundle bundle = getArguments();

            isMemberEvents = bundle.getBoolean("isMemberEvents");
            isOrganizerEvents = bundle.getBoolean("isOrganizerEvents");
            mUserId = bundle.getInt("user_id");
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

            // If The Fragment Being called from User profile page.
            if (isMemberEvents) {

                spinner = rootView.findViewById(R.id.filter_view);
                rootView.findViewById(R.id.eventFilterBlock).setVisibility(View.VISIBLE);

                adapter = new ArrayAdapter<>(mContext, R.layout.simple_text_view);

                /* Add events filter type to spinner using adpter */
                adapter.add(getResources().getString(R.string.my_event_filter_see_all));
                adapter.add(getResources().getString(R.string.my_event_filter_leading));
                adapter.add(getResources().getString(R.string.my_event_filter_hosting));
                adapter.add(getResources().getString(R.string.my_event_filter_attending));
                adapter.add(getResources().getString(R.string.my_event_filter_maybe_attending));
                adapter.add(getResources().getString(R.string.my_event_filter_not_attending));
                adapter.add(getResources().getString(R.string.my_event_filter_liked));

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);
                spinner.setTag("eventFilter");

            }
            if (isFirstTab) {
                sendRequestToServer();
            }
        }

        mManageDataAdapter = new AdvModulesManageDataAdapter(getActivity(), R.layout.list_advanced_event_info,
                mBrowseItemList, "manage_siteevent", AdvEventsMyEventsFragment.this);
        mGridView.setAdapter(mManageDataAdapter);

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if(ConstantVariables.ADV_EVENT_ADS_TYPE == ConstantVariables.TYPE_GOOGLE_ADS){
                mAdFetcher = new AdFetcher(mContext);
                mAdFetcher.loadAds(mBrowseItemList,mManageDataAdapter,ConstantVariables.ADV_EVENT_ADS_POSITION);
            }
            sendRequestToServer();
        } else if(snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mGridView != null){
            mGridView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
        String eventTitle = listItems.getmBrowseListTitle();

        Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                mCurrentSelectedModule, null);
        mainIntent.putExtra(ConstantVariables.CONTENT_TITLE, eventTitle);
        startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void sendRequestToServer() {

        mLoadingPageNo = 1;

        if(isMemberEvents){
            mMyEventsUrl = UrlUtil.MANAGE_ADV_EVENTS_URL +  "&page=" + pageNumber + "&user_id=" + mUserId + "&viewType=all&rsvp=" + mEventType;
        } else if (isOrganizerEvents) {
            mMyEventsUrl = UrlUtil.MANAGE_ADV_EVENTS_URL +  "&page=" + pageNumber + "&host_id=" + mUserId
                    + "&viewType=all&host_type=siteevent_organizer";
        }

        mAppConst.getJsonResponseFromUrl(mMyEventsUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.ADV_EVENT_ADS_POSITION,
                            ConstantVariables.ADV_EVENT_ADS_TYPE);
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    sendRequestToServer();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }

            }
        });

        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                isAdLoaded = false;
                sendRequestToServer();
                if (listNativeAdsManager != null) {
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if(limit == totalItemCount && !isLoading) {

            if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()){
                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String url;
                if(isMemberEvents ) {
                    url = mMyEventsUrl = UrlUtil.MANAGE_ADV_EVENTS_URL + "&page=" + mLoadingPageNo + "&showEventType=all"
                            + "&user_id=" + mUserId + "&rsvp=" + mEventType;

                } else if (isOrganizerEvents) {
                    url = mMyEventsUrl = UrlUtil.MANAGE_ADV_EVENTS_URL +  "&page=" + mLoadingPageNo + "&showEventType=all"
                            + "&host_id=" + mUserId + "&host_type=siteevent_organizer";
                } else {
                    url = mMyEventsUrl = UrlUtil.MANAGE_ADV_EVENTS_URL + "&page=" + mLoadingPageNo + "&showEventType=all";
                }

                isLoading = true;
                loadMoreData(url);
            }

        }

    }

    /**
     * Load more content on scrolling
     * @param url Url to send request on server
     */

    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.ADV_EVENT_ADS_POSITION,
                            ConstantVariables.ADV_EVENT_ADS_TYPE);
                } else {
                    CustomViews.removeFooterView(footerView);
                    addDataToList(jsonObject);
                }
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    /**
     * Add data in List and notify adapter
     * @param jsonObject
     */
    int j=0;
    public void addDataToList(JSONObject jsonObject){

        mBody = jsonObject;
        if (mBody != null) {
            try {
                mTotalItemCount = mBody.optInt("getTotalItemCount");

                mBrowseList.setmTotalItemCount(mTotalItemCount);
                if (mTotalItemCount != 0) {
                    mGridView.setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                    mDataResponse = mBody.optJSONArray("response");
                    for (int i = 0; i < mDataResponse.length(); i++) {
                        if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                                && mBrowseItemList.size() % ConstantVariables.ADV_EVENT_ADS_POSITION == 0) {
                            switch (ConstantVariables.ADV_EVENT_ADS_TYPE){
                                case ConstantVariables.TYPE_FACEBOOK_ADS:
                                    NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                                    mBrowseItemList.add(ad);
                                    break;
                                case ConstantVariables.TYPE_GOOGLE_ADS:
                                    if(mAdFetcher.getAdList() != null && !mAdFetcher.getAdList().isEmpty()){
                                        if(j < mAdFetcher.getAdList().size()) {
                                            NativeAppInstallAd nativeAppInstallAd = (NativeAppInstallAd) mAdFetcher.getAdList().get(j);
                                            j++;
                                            mBrowseItemList.add(nativeAppInstallAd);
                                        }else {
                                            j=0;
                                        }
                                    }
                                    break;
                                default:
                                    if(mAdvertisementsArray != null){
                                        if(j < mAdvertisementsArray.length()){
                                            mBrowseItemList.add(addCommunityAddsToList(j));
                                            j++;
                                        } else {
                                            j = 0;
                                        }
                                    }
                                    break;
                            }
                        }

                        JSONObject jsonDataObject = mDataResponse.getJSONObject(i);
                        int event_id = jsonDataObject.optInt("event_id");
                        String title = jsonDataObject.optString("title");
                        String location = jsonDataObject.optString("location");
                        String starttime = jsonDataObject.optString("starttime");
                        String image_icon = jsonDataObject.optString("image");
                        String event_status = jsonDataObject.optString("status");

                        JSONObject hostObject = jsonDataObject.optJSONObject("hosted_by");
                        String hostTitle = null, hostType = null, hostImage = null;
                        int hostId = 0;
                        if(hostObject != null){
                            hostTitle = hostObject.optString("host_title");
                            hostId = hostObject.optInt("host_id");
                            hostType = hostObject.optString("host_type");
                            hostImage = hostObject.optString("image");
                        }

                        int member_count = jsonDataObject.optInt("member_count");
                        int view_count = jsonDataObject.optInt("view_count");
                        int likes_count = jsonDataObject.optInt("like_count");
                        int closed = jsonDataObject.optInt("closed");
                        int allow_to_view = jsonDataObject.optInt("allow_to_view");
                        int hasMultipleDates = jsonDataObject.optInt("hasMultipleDates");
                        int isJoined = jsonDataObject.optInt("isJoined");

                        mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
                                hostTitle, member_count, event_id, closed,
                                jsonDataObject.optJSONArray("menu"), location,
                                starttime, hostId, likes_count, view_count, hostType, (allow_to_view == 1) ,
                                (isOrganizerEvents || isMemberEvents), hasMultipleDates, isJoined, event_status));
//
//                        if (allow_to_view == 1) {
//                            if (isOrganizerEvents || isMemberEvents) {
//                                mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
//                                        hostTitle, member_count, event_id, closed,
//                                        jsonDataObject.optJSONArray("menu"), location,
//                                        starttime, hostId, likes_count, view_count, hostType, true ,
//                                        true, hasMultipleDates));
//                            } else {
//                                mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
//                                        hostTitle, member_count, event_id, closed,
//                                        jsonDataObject.getJSONArray("menu"), location,
//                                        starttime, hostId, likes_count, view_count, hostType, true,
//                                        false, hasMultipleDates));
//                            }
//                        } else {
//                            if (isOrganizerEvents || isMemberEvents) {
//                                mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
//                                        hostTitle, member_count, event_id, closed,
//                                        jsonDataObject.getJSONArray("menu"), location,
//                                        starttime, hostId, likes_count, view_count, hostType, false,
//                                        true, hasMultipleDates));
//                            } else {
//                                mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
//                                        hostTitle, member_count, event_id, closed,
//                                        jsonDataObject.getJSONArray("menu"), location,
//                                        starttime, hostId, likes_count, view_count, hostType, false,
//                                        false, hasMultipleDates));
//                            }
//                        }
                    }
                } else {
                    mGridView.setVisibility(View.INVISIBLE);
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = rootView.findViewById(R.id.error_icon);
                    TextView errorMessage = rootView.findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf073");
                    errorMessage.setText(getActivity().getResources().getString(R.string.no_events));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mGridView.setVisibility(View.INVISIBLE);
            }
            isVisibleToUser = true;
            mManageDataAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Update currentSelected Option on back press*/
        switch (requestCode){
            case ConstantVariables.VIEW_PAGE_CODE:
                PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
                PreferencesUtils.updateCurrentList(mContext, "manage_siteevent");
                if(resultCode == ConstantVariables.VIEW_PAGE_CODE){
                    onRefresh();
                }
                break;

            case ConstantVariables.PAGE_EDIT_CODE:
                if(resultCode == ConstantVariables.PAGE_EDIT_CODE){
                    onRefresh();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                mEventType = "-1";
                break;
            case 1:
                mEventType = "-4";
                break;
            case 2:
                mEventType = "-2";
                break;
            case 3:
                mEventType = "2";
                break;
            case 4:
                mEventType = "1";
                break;
            case 5:
                mEventType = "0";
                break;
            case 6:
                mEventType = "-3";
                break;
        }
        if (isVisibleToUser) {
            swipeRefreshLayout.setRefreshing(true);
            sendRequestToServer();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.ADV_EVENT_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mManageDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    private CommunityAdsList addCommunityAddsToList(int j) {

        JSONObject singleAdObject = mAdvertisementsArray.optJSONObject(j);
        int adId = singleAdObject.optInt("userad_id");
        String ad_type = singleAdObject.optString("ad_type");
        String cads_title = singleAdObject.optString("cads_title");
        String cads_body = singleAdObject.optString("cads_body");
        String cads_url = singleAdObject.optString("cads_url");
        String image = singleAdObject.optString("image");
        return new CommunityAdsList(adId, ad_type, cads_title, cads_body,
                cads_url, image);
    }

    @Override
    public void onCommunityAdsLoaded(JSONArray advertisementsArray) {
        mAdvertisementsArray = advertisementsArray;

        if(!isAdLoaded && mAdvertisementsArray != null){
            isAdLoaded = true;
            int j = 0;
            for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.ADV_EVENT_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mManageDataAdapter.notifyDataSetChanged();
                }
            }
        } else{
            CustomViews.removeFooterView(footerView);
            addDataToList(mBody);
        }
    }
}


